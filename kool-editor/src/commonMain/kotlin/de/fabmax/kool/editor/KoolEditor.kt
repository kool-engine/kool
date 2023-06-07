package de.fabmax.kool.editor

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.LoadableFile
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.ui.EditorUi
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.modules.ui2.docking.DockNode
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrbitInputTransform
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene
import kotlin.math.roundToInt

class KoolEditor(val ctx: KoolContext, val paths: ProjectPaths) {

    val editorInputContext = InputStack.InputHandler("Editor input")
    val editorCameraTransform = OrbitInputTransform("Camera input transform").apply {
        setMouseRotation(20f, -30f)
        InputStack.defaultInputHandler.pointerListeners += this
    }
    val editorContent = Node("Editor Content").apply {
        tags[TAG_EDITOR_SUPPORT_CONTENT] = "true"
        addNode(editorCameraTransform)
    }
    val editorOverlay = scene("editor-overlay") { addNode(editorContent) }

    val appLoader = AppLoader(this, paths)
    val availableAssets = AvailableAssets(paths.assetsPath)
    val ui = EditorUi(this)

    private val editorAppCallbacks = object : ApplicationCallbacks {
        override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
            EditorState.saveProject()
            return true
        }

        override fun onFileDrop(droppedFiles: List<LoadableFile>) {
            val targetPath = ui.resourceBrowser.selectedDirectory.value?.path ?: ""
            availableAssets.importAssets(targetPath, droppedFiles)
        }
    }

    init {
        instance = this
        Assets.assetsBasePath = paths.assetsPath
        AppAssets.impl = CachedAppAssets

        ctx.applicationCallbacks = editorAppCallbacks
        ctx.scenes += ui

        registerKeyBindings()
        registerSceneObjectPicking()
        registerAutoSaveOnFocusLoss()

        appLoader.appReloadListeners += AppReloadListener { handleAppReload(it) }
        appLoader.reloadApp()
    }

    private fun registerAutoSaveOnFocusLoss() {
        // auto save on window focus loss
        var wasFocused = false
        editorContent.onUpdate {
            if (wasFocused && !ctx.isWindowFocused) {
                EditorState.saveProject()
            }
            wasFocused = ctx.isWindowFocused
        }
    }

    private fun registerKeyBindings() {
        editorInputContext.addKeyListener(
            name = "Undo",
            keyCode = LocalKeyCode('Z'),
            filter = InputStack.KEY_FILTER_CTRL_PRESSED
        ) {
            EditorActions.undo()
        }
        editorInputContext.addKeyListener(
            name = "Redo",
            keyCode = LocalKeyCode('Y'),
            filter = InputStack.KEY_FILTER_CTRL_PRESSED
        ) {
            EditorActions.redo()
        }

        InputStack.pushTop(editorInputContext)
    }

    private fun registerSceneObjectPicking() {
        editorInputContext.pointerListeners += object : InputStack.PointerListener {
            val rayTest = RayTest()

            override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
                val sceneModel = EditorState.selectedScene.value ?: return
                val appScene = sceneModel.node
                val ptr = pointerState.primaryPointer
                if (ptr.isLeftButtonClicked) {
                    if (appScene.computePickRay(ptr, ctx, rayTest.ray)) {
                        rayTest.clear()
                        appScene.rayTest(rayTest)

                        var it = rayTest.hitNode
                        var selectedNodeModel: SceneNodeModel? = null
                        while (it != null) {
                            selectedNodeModel = sceneModel.nodesToNodeModels[it] as? SceneNodeModel
                            if (selectedNodeModel != null) {
                                break
                            }
                            it = it.parent
                        }
                        EditorState.selectedNode.set(selectedNodeModel)
                    }
                }
            }
        }
    }

    private suspend fun handleAppReload(loadedApp: LoadedApp) {
        // clear scene objects from old app
        editorCameraTransform.clearChildren()
        editorCameraTransform.addNode(editorOverlay.camera)

        EditorState.projectModel.getCreatedScenes().map { it.node }.let { oldScenes ->
            ctx.scenes -= oldScenes.toSet()
            oldScenes.forEach {
                it.dispose(ctx)
            }
        }
        EditorState.loadedApp.value?.app?.onDispose(ctx)

        // add scene objects from new app
        AppState.isInEditorState.set(true)
        AppState.isEditModeState.set(true)

        loadedApp.app.startApp(EditorState.projectModel, ctx)
        EditorState.projectModel.getCreatedScenes().map { it.node }.let { newScenes ->
            ctx.scenes += newScenes
            newScenes.forEach { scene ->
                editorCameraTransform.addNode(scene.camera)
                ui.centerSlot.dockedTo.value?.let { scene.setViewportToDockNode(it) }
            }
        }

        EditorState.loadedApp.set(loadedApp)
        if (EditorState.selectedScene.value == null) {
            EditorState.selectedScene.set(EditorState.projectModel.getCreatedScenes().getOrNull(0))
        }
        if (EditorState.selectedNode.value == null) {
            EditorState.selectedNode.set(EditorState.selectedScene.value)
        }

        bringEditorMenuToTop()
        EditorActions.clear()
    }

    private fun Scene.setViewportToDockNode(dockNode: DockNode) {
        mainRenderPass.useWindowViewport = false
        onRenderScene += {
            val x = dockNode.boundsLeftDp.value.px.roundToInt()
            val w = dockNode.boundsRightDp.value.px.roundToInt() - x
            val h = dockNode.boundsBottomDp.value.px.roundToInt() - dockNode.boundsTopDp.value.px.roundToInt()
            val y = it.windowHeight - dockNode.boundsBottomDp.value.px.roundToInt()
            mainRenderPass.viewport.set(x, y, w, h)
        }
    }

    private fun bringEditorMenuToTop() {
        ctx.scenes -= editorOverlay
        ctx.scenes += editorOverlay
        editorOverlay.mainRenderPass.clearColor = null
        editorOverlay.mainRenderPass.clearDepth = false
        editorOverlay.onRenderScene.clear()
        ui.centerSlot.dockedTo.value?.let { editorOverlay.setViewportToDockNode(it) }

        ctx.scenes -= ui
        ctx.scenes += ui
        ui.sceneBrowser.refreshSceneTree()
    }

    companion object {
        lateinit var instance: KoolEditor
            private set

        const val TAG_EDITOR_SUPPORT_CONTENT = "%editor-content-hidden"
    }
}