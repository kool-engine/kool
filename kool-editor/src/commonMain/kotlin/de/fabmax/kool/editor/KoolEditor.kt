package de.fabmax.kool.editor

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.menu.EditorMenu
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrbitInputTransform

class KoolEditor(val ctx: KoolContext) {

    val editorInputContext = InputStack.InputHandler("Editor input")
    val editorCameraTransform = OrbitInputTransform("Camera input transform").apply {
        setMouseRotation(20f, -30f)
        InputStack.defaultInputHandler.pointerListeners += this
    }
    val editorContent = Node("Editor Content").apply {
        tags["hidden"] = "true"
        addNode(editorCameraTransform)
    }

    val appLoader = AppLoader(this)
    val menu = EditorMenu(this)

    init {
        ctx.scenes += menu

        registerKeyBindings()
        registerSceneObjectPicking()
        registerAutoSave()

        appLoader.addIgnorePath(PROJECT_MODEL_PATH)
        appLoader.appReloadListeners += this::handleAppReload
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
                val appScene = sceneModel.created ?: return
                val ptr = pointerState.primaryPointer
                if (ptr.isLeftButtonClicked) {
                    if (appScene.computePickRay(ptr, ctx, rayTest.ray)) {
                        rayTest.clear()
                        appScene.rayTest(rayTest)
                        EditorState.selectedObject.set(sceneModel.nodesToNodeModels[rayTest.hitNode])
                    }
                }
            }
        }
    }

    private fun handleAppReload(app: EditorAwareApp) {
        // clear scene objects from old app
        editorCameraTransform.clearChildren()
        EditorState.projectModel.created?.let { oldScenes ->
            ctx.scenes -= oldScenes.toSet()
            oldScenes.forEach { it.dispose(ctx) }
        }
        EditorState.loadedApp.value?.onDispose(true, ctx)

        // add scene objects from new app
        app.startApp(EditorState.projectModel, true, ctx)
        EditorState.projectModel.created?.let { newScenes ->
            ctx.scenes += newScenes.toSet()
            newScenes.forEach {
                it.dispose(ctx)
                it.addNode(editorContent)
                editorCameraTransform.addNode(it.camera)
            }
        }
        EditorState.loadedApp.set(app)

        bringEditorMenuToTop()
        EditorActions.clear()
    }

    private fun bringEditorMenuToTop() {
        ctx.scenes -= menu
        ctx.scenes += menu
        menu.sceneBrowser.refreshSceneTree()
    }

    companion object {
        // todo: don't use hard-coded project paths
        const val PROJECT_DIR = "kool-editor-template"
        const val PROJECT_SRC_DIR = "${PROJECT_DIR}/src"
        const val PROJECT_CLASS_PATH = "${PROJECT_DIR}/build/classes/kotlin/jvm/main"
        const val PROJECT_MODEL_PATH = "${PROJECT_DIR}/src/commonMain/resources/kool-project.json"
        const val PROJECT_MAIN_CLASS = "de.fabmax.kool.app.App"
    }
}