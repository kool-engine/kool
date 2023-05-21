package de.fabmax.kool.editor

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.ui.EditorUi
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrbitInputTransform
import kotlin.math.roundToInt

class KoolEditor(val ctx: KoolContext) {

    val editorInputContext = InputStack.InputHandler("Editor input")
    val editorCameraTransform = OrbitInputTransform("Camera input transform").apply {
        setMouseRotation(20f, -30f)
        InputStack.defaultInputHandler.pointerListeners += this
    }
    val editorContent = Node("Editor Content").apply {
        tags[TAG_EDITOR_SUPPORT_CONTENT] = "true"
        addNode(editorCameraTransform)
    }

    val appLoader = AppLoader(this, APP_PROJECT_SRC_DIRS, APP_PROJECT_CLASS_PATH)
    val availableAssets = AvailableAssets(APP_ASSETS_DIR)
    val ui = EditorUi(this)

    init {
        instance = this
        Assets.assetsBasePath = APP_ASSETS_DIR
        AppAssets.impl = CachedAppAssets()

        ctx.scenes += ui

        registerKeyBindings()
        registerSceneObjectPicking()
        registerAutoSave()

        appLoader.appReloadListeners += AppReloadListener { handleAppReload(it) }
        appLoader.reloadApp()
    }

    private fun registerAutoSave() {
        // auto save on window focus loss
        var wasFocused = false
        editorContent.onUpdate {
            if (wasFocused && !ctx.isWindowFocused) {
                EditorState.saveProject()
            }
            wasFocused = ctx.isWindowFocused
        }

        // auto save on exit
        ctx.applicationCallbacks = object : ApplicationCallbacks {
            override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
                EditorState.saveProject()
                return true
            }
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
        EditorState.projectModel.getCreatedScenes().map { it.node }.let { oldScenes ->
            ctx.scenes -= oldScenes.toSet()
            oldScenes.forEach { it.dispose(ctx) }
        }
        EditorState.loadedApp.value?.app?.onDispose(ctx)

        // add scene objects from new app
        AppState.isInEditorState.set(true)
        AppState.isEditModeState.set(true)

        loadedApp.app.startApp(EditorState.projectModel, ctx)
        EditorState.projectModel.getCreatedScenes().map { it.node }.let { newScenes ->
            ctx.scenes += newScenes
            newScenes.forEach { scene ->
                scene.dispose(ctx)
                scene.addNode(editorContent)
                editorCameraTransform.addNode(scene.camera)

                scene.onRenderScene += {
                    val dockNode = ui.centerSlot.dockedTo.value
                    if (dockNode != null) {
                        val x = dockNode.boundsLeftDp.value.px.roundToInt()
                        val w = dockNode.boundsRightDp.value.px.roundToInt() - x
                        val h = dockNode.boundsBottomDp.value.px.roundToInt() - dockNode.boundsTopDp.value.px.roundToInt()
                        val y = it.windowHeight - dockNode.boundsBottomDp.value.px.roundToInt()

                        scene.mainRenderPass.useWindowViewport = false
                        scene.mainRenderPass.viewport.set(x, y, w, h)
                    }
                }
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

    private fun bringEditorMenuToTop() {
        ctx.scenes -= ui
        ctx.scenes += ui
        ui.sceneBrowser.refreshSceneTree()
    }

    companion object {
        lateinit var instance: KoolEditor
            private set

        const val TAG_EDITOR_SUPPORT_CONTENT = "%editor-content-hidden"

        // todo: don't use hard-coded project paths
        const val APP_PROJECT_DIR = "kool-editor-template"
        const val APP_PROJECT_CLASS_PATH = "${APP_PROJECT_DIR}/build/classes/kotlin/jvm/main"
        const val APP_PROJECT_MODEL_PATH = "${APP_PROJECT_DIR}/src/commonMain/resources/kool-project.json"
        const val APP_PROJECT_MAIN_CLASS = "de.fabmax.kool.app.App"

        val APP_PROJECT_SRC_DIRS = setOf(
            "${APP_PROJECT_DIR}/src/commonMain/kotlin",
            "${APP_PROJECT_DIR}/src/jsMain/kotlin",
            "${APP_PROJECT_DIR}/src/jvmMain/kotlin"
        )

        const val APP_ASSETS_DIR = "$APP_PROJECT_DIR/src/commonMain/resources/assets"
    }
}