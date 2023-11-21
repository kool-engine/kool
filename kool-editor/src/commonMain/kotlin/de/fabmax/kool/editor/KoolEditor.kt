package de.fabmax.kool.editor

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.LoadableFile
import de.fabmax.kool.editor.actions.DeleteNodeAction
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetVisibilityAction
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.overlays.GridOverlay
import de.fabmax.kool.editor.overlays.SceneObjectsOverlay
import de.fabmax.kool.editor.overlays.SelectionOverlay
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay
import de.fabmax.kool.editor.ui.EditorUi
import de.fabmax.kool.editor.ui.FloatingToolbar
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.modules.ui2.docking.DockLayout
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logW

class KoolEditor(val ctx: KoolContext, val paths: ProjectPaths) {

    init { instance = this }

    val editorInputContext = InputStack.InputHandler("Editor input")
    val editorCameraTransform = EditorCamTransform(this)
    private val editorBackgroundScene = scene("editor-camera") {
        addNode(editorCameraTransform)
        mainRenderPass.clearColor = Color.BLACK
        mainRenderPass.clearDepth = false
    }

    val editorOverlay = scene("editor-overlay") {
        camera.setClipRange(0.1f, 1000f)
        mainRenderPass.clearColor = null
        mainRenderPass.clearDepth = false
    }
    val gridOverlay = GridOverlay()
    val lightOverlay = SceneObjectsOverlay()
    val gizmoOverlay = TransformGizmoOverlay(this)
    val selectionOverlay = SelectionOverlay(this)

    val editorContent = Node("Editor Content").apply {
        tags[TAG_EDITOR_SUPPORT_CONTENT] = "true"
        addNode(gridOverlay)
        addNode(lightOverlay)
        addNode(selectionOverlay)
        addNode(gizmoOverlay)

        editorOverlay.addNode(this)
    }

    val appLoader = AppLoader(this, paths)
    val modeController = AppModeController(this)
    val availableAssets = AvailableAssets(paths.assetsBasePath, paths.assetsSubDir)
    val ui = EditorUi(this)

    private val editorAppCallbacks = object : ApplicationCallbacks {
        override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
            EditorState.saveProject()
            saveEditorConfig()
            return PlatformFunctions.onWindowCloseRequest(ctx)
        }

        override fun onFileDrop(droppedFiles: List<LoadableFile>) {
            val targetPath = ui.assetBrowser.selectedDirectory.value?.path ?: ""
            availableAssets.importAssets(targetPath, droppedFiles)
        }
    }

    init {
        Assets.assetsBasePath = paths.assetsBasePath
        AppAssets.impl = CachedAppAssets

        AppState.isInEditorState.set(true)
        AppState.appModeState.set(AppMode.EDIT)

        ctx.applicationCallbacks = editorAppCallbacks

        // editor background needs to be the first scene, not only because its background but also because it hosts
        // the editor camera controller, which is also used by other scenes, and we want it to update first
        ctx.scenes += editorBackgroundScene
        ctx.scenes += ui

        registerKeyBindings()
        registerSceneObjectPicking()
        registerAutoSaveOnFocusLoss()
        appLoader.appReloadListeners += AppReloadListener {
            handleAppReload(it)
        }

        PlatformFunctions.onEditorStarted(ctx)
        appLoader.reloadApp()
    }

    fun setEditorOverlayVisibility(isVisible: Boolean) {
        editorOverlay.children.forEach {
            it.isVisible = isVisible
        }
        ui.sceneView.isShowToolbar.set(isVisible)
    }

    fun editBehaviorSource(behavior: AppBehavior) = editBehaviorSource(behavior.qualifiedName)

    fun editBehaviorSource(behaviorClassName: String) {
        val sourcePath = "${paths.commonSrcPath}/${behaviorClassName.replace('.', '/')}.kt"
        PlatformFunctions.editBehavior(sourcePath)
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
        editorInputContext.addKeyListener(
            name = "Copy",
            keyCode = LocalKeyCode('C'),
            filter = InputStack.KEY_FILTER_CTRL_PRESSED
        ) {
            EditorCopyAndPaste.copySelection()
        }
        editorInputContext.addKeyListener(
            name = "Paste",
            keyCode = LocalKeyCode('V'),
            filter = InputStack.KEY_FILTER_CTRL_PRESSED
        ) {
            EditorCopyAndPaste.paste()
        }
        editorInputContext.addKeyListener(
            name = "Duplicate",
            keyCode = LocalKeyCode('D'),
            filter = InputStack.KEY_FILTER_CTRL_PRESSED
        ) {
            EditorCopyAndPaste.duplicateSelection()
        }
        editorInputContext.addKeyListener(
            name = "Delete selected objects",
            keyCode = KeyboardInput.KEY_DEL
        ) {
            DeleteNodeAction(EditorState.getSelectedSceneNodes()).apply()
        }
        editorInputContext.addKeyListener(
            name = "Hide selected objects",
            keyCode = LocalKeyCode('H'),
            filter = { it.isPressed && !it.isAltDown }
        ) {
            val selection = EditorState.getSelectedSceneNodes()
            SetVisibilityAction(selection, selection.any { !it.isVisibleState.value }).apply()
        }
        editorInputContext.addKeyListener(
            name = "Unhide all hidden objects",
            keyCode = LocalKeyCode('H'),
            filter = { it.isPressed && it.isAltDown }
        ) {
            EditorState.activeScene.value?.sceneNodes?.filter { !it.isVisibleState.value } ?.let { nodes ->
                SetVisibilityAction(nodes, true).apply()
            }
        }
        editorInputContext.addKeyListener(
            name = "Focus selected object",
            keyCode = KeyboardInput.KEY_NP_DECIMAL
        ) {
            editorCameraTransform.focusSelectedObject()
        }
        editorInputContext.addKeyListener(
            name = "Toggle box select",
            keyCode = LocalKeyCode('B')
        ) {
            ui.sceneView.toolbar.toggleActionMode(FloatingToolbar.EditActionMode.BOX_SELECT)
        }
        editorInputContext.addKeyListener(
            name = "Toggle move object",
            keyCode = LocalKeyCode('G')
        ) {
            ui.sceneView.toolbar.toggleActionMode(FloatingToolbar.EditActionMode.MOVE)
        }
        editorInputContext.addKeyListener(
            name = "Toggle rotate object",
            keyCode = LocalKeyCode('R')
        ) {
            ui.sceneView.toolbar.toggleActionMode(FloatingToolbar.EditActionMode.ROTATE)
        }
        editorInputContext.addKeyListener(
            name = "Toggle scale object",
            keyCode = LocalKeyCode('S')
        ) {
            ui.sceneView.toolbar.toggleActionMode(FloatingToolbar.EditActionMode.SCALE)
        }
        editorInputContext.addKeyListener(
            name = "Cancel current operation",
            keyCode = KeyboardInput.KEY_ESC
        ) {
            // for now, we take the naive approach and check any possible operation that can be canceled
            // this might not scale well when we have more possible operations...

            // disable box select
            ui.sceneView.isBoxSelectMode.set(false)
            // cancel any ongoing drag
            ui.dndController.dndContext.cancelDrag()
        }

        InputStack.pushTop(editorInputContext)
    }

    private fun registerSceneObjectPicking() {
        editorInputContext.pointerListeners += object : InputStack.PointerListener {
            val rayTest = RayTest()

            override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
                val sceneModel = EditorState.activeScene.value ?: return
                val appScene = sceneModel.drawNode
                val ptr = pointerState.primaryPointer
                if (ptr.isLeftButtonClicked && !ptr.isConsumed()) {
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
                        EditorState.selectSingle(selectedNodeModel)
                    }
                }
            }
        }
    }

    private suspend fun handleAppReload(loadedApp: LoadedApp) {
        // clear scene objects from old app
        editorCameraTransform.clearChildren()
        editorCameraTransform.addNode(editorBackgroundScene.camera)
        editorCameraTransform.addNode(editorOverlay.camera)

        // dispose old scene + objects
        EditorState.projectModel.getCreatedScenes().map { it.drawNode }.let { oldScenes ->
            ctx.scenes -= oldScenes.toSet()
            oldScenes.forEach {
                it.removeOffscreenPass(selectionOverlay.selectionPass)
                it.release()
            }
        }
        EditorState.loadedApp.value?.app?.onDispose(ctx)
        selectionOverlay.selectionPass.disposePipelines(ctx)

        // initialize newly loaded app
        loadedApp.app.loadApp(EditorState.projectModel, ctx)

        // add scene objects from new app
        EditorState.projectModel.getCreatedScenes().let { newScenes ->
            if (newScenes.size != 1) {
                logW { "Unusual number of scene, currently only single scene setups are supported" }
            }
            newScenes.firstOrNull()?.let { sceneModel ->
                val scene = sceneModel.drawNode
                ctx.scenes += scene

                scene.addOffscreenPass(selectionOverlay.selectionPass)
                selectionOverlay.selectionPass.drawNode = scene

                // replace original scene cam with editor cam
                val editorCam = PerspectiveCamera()
                editorCam.setClipRange(0.1f, 1000f)
                scene.camera = editorCam
                editorCameraTransform.addNode(scene.camera)
                ui.sceneView.applyViewportTo(scene)

                val aoPipeline = sceneModel.getComponent<SsaoComponent>()?.aoPipeline as? AoPipeline.ForwardAoPipeline
                aoPipeline?.proxyCamera?.trackedCam = editorCam
            }
        }

        EditorState.loadedApp.set(loadedApp)
        if (EditorState.activeScene.value == null) {
            EditorState.activeScene.set(EditorState.projectModel.getCreatedScenes().getOrNull(0))
        }
        if (EditorState.selection.isEmpty()) {
            EditorState.activeScene.value?.let { EditorState.selection.add(it) }
        }

        if (AppState.appMode == AppMode.EDIT) {
            ui.appStateInfo.set("App is in edit mode")
            setEditorOverlayVisibility(true)
        } else {
            ui.appStateInfo.set("App is running")
            setEditorOverlayVisibility(false)
        }

        updateOverlays()
        EditorActions.clear()
    }

    private fun updateOverlays() {
        ctx.scenes -= editorOverlay
        ctx.scenes += editorOverlay

        editorOverlay.onRenderScene.clear()
        ui.sceneView.applyViewportTo(editorBackgroundScene)
        ui.sceneView.applyViewportTo(editorOverlay)

        ctx.scenes -= ui
        ctx.scenes += ui
        ui.sceneBrowser.refreshSceneTree()

        selectionOverlay.invalidateSelection()
    }

    private fun saveEditorConfig() {
        DockLayout.saveLayout(ui.dock, "editor.ui.layout")
    }

    companion object {
        lateinit var instance: KoolEditor
            private set

        const val TAG_EDITOR_SUPPORT_CONTENT = "%editor-content-hidden"
    }
}