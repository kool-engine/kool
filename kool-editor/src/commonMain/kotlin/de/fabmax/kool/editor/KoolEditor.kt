package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.menu.EditorMenu
import de.fabmax.kool.editor.model.*
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logW
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class KoolEditor(val ctx: KoolContext) {

    private val projectModel: MProject = loadProjectModel()
    val editorInputContext = InputStack.InputHandler("Editor input")
    val editorContent = Node("Editor Content").apply {
        tags["hidden"] = "true"
    }

    val loadedApp = MutableStateValue<AppContext?>(null)
    val appLoader = AppLoader(this)
    val menu = EditorMenu(this)

    init {
        ctx.scenes += menu

        registerKeyBindings()
        registerSceneObjectPicking()

        appLoader.appReloadListeners += this::handleAppReload
        appLoader.reloadApp()
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
                val appScene = loadedApp.value?.appScenes?.get(0) ?: return
                val ptr = pointerState.primaryPointer
                if (ptr.isLeftButtonClicked) {
                    if (appScene.computePickRay(ptr, ctx, rayTest.ray)) {
                        rayTest.clear()
                        appScene.rayTest(rayTest)
                        menu.sceneBrowser.selectedObject.set(rayTest.hitNode)
                    }
                }
            }
        }
    }

    private fun handleAppReload(app: EditorAwareApp) {
        val oldApp = loadedApp.value
        oldApp?.let { oldAppCtx ->
            ctx.scenes -= oldAppCtx.appScenes.toSet()
            oldAppCtx.appScenes.forEach { it.dispose(ctx) }
            oldAppCtx.app.onDispose(true, ctx)
        }

        val newApp = AppContext(app, app.startApp(projectModel, true, ctx))
        newApp.appScenes.forEach { it.addNode(editorContent) }
        loadedApp.set(newApp)
        ctx.scenes += newApp.appScenes

        bringEditorMenuToTop()
        EditorActions.clear()
    }

    private fun bringEditorMenuToTop() {
        ctx.scenes -= menu
        ctx.scenes += menu
    }

    private fun loadProjectModel(): MProject {
        val projFile = File(PROJECT_MODEL_PATH)
        return try {
            Json.decodeFromString<MProject>(projFile.readText())
        } catch (e: Exception) {
            logW { "Project not found, creating new empty project" }
            newProject()
        }
    }

    private fun newProject(): MProject {
        val emptyScene = MScene(
            commonProps = MCommonNodeProperties(
                hierarchyPath = listOf("New Scene"),
                transform = MTransform.IDENTITY
            ),
            clearColor = MColor(MdColor.GREY tone 900),
            proceduralMeshes = listOf(
                MProceduralMesh(
                    MCommonNodeProperties(
                        hierarchyPath = listOf("New Scene", "Test Mesh"),
                        transform = MTransform.IDENTITY
                    ),
                    generatorClass = "de.fabmax.kool.app.SampleProceduralMesh"
                )
            )
        )

        val projectModel =  MProject(
            mainClass = PROJECT_MAIN_CLASS,
            scenes = listOf(emptyScene)
        )

        val modelPath = File(PROJECT_MODEL_PATH)
        modelPath.parentFile.mkdirs()
        modelPath.writeText(Json.encodeToString(projectModel))
        return projectModel
    }

    class AppContext(val app: EditorAwareApp, val appScenes: List<Scene>)

    interface AppReloadListener {
        fun onAppReload(oldApp: AppContext?, newApp: AppContext)
    }

    companion object {
        // todo: don't use hard-coded project paths
        const val PROJECT_DIR = "kool-editor-template"
        const val PROJECT_SRC_DIR = "${PROJECT_DIR}/src"
        const val PROJECT_JAR_PATH = "${PROJECT_DIR}/build/libs/kool-editor-template-jvm-0.11.0-SNAPSHOT.jar"
        const val PROJECT_MODEL_PATH = "${PROJECT_DIR}/src/commonMain/resources/kool-project.json"
        const val PROJECT_MAIN_CLASS = "de.fabmax.kool.app.App"
    }
}