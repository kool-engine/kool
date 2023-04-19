package de.fabmax.kool.editor

import de.fabmax.kool.ApplicationCallbacks
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
import de.fabmax.kool.scene.OrbitInputTransform
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

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

    val projectModel: MProject = loadProjectModel()
    val loadedApp = MutableStateValue<EditorAwareApp?>(null)
    val selectedScene = MutableStateValue<MScene?>(null)

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
                saveProject(projectModel)
            }
            wasFocused = ctx.isWindowFocused
        }

        // auto save on exit
        ctx.applicationCallbacks = object : ApplicationCallbacks {
            override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
                saveProject(projectModel)
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
                val sceneModel = selectedScene.value ?: return
                val appScene = sceneModel.created ?: return
                val ptr = pointerState.primaryPointer
                if (ptr.isLeftButtonClicked) {
                    if (appScene.computePickRay(ptr, ctx, rayTest.ray)) {
                        rayTest.clear()
                        appScene.rayTest(rayTest)
                        menu.sceneBrowser.selectedObject.set(sceneModel.nodesToNodeModels[rayTest.hitNode])
                    }
                }
            }
        }
    }

    private fun handleAppReload(app: EditorAwareApp) {
        // clear scene objects from old app
        editorCameraTransform.clearChildren()
        projectModel.created?.let { oldScenes ->
            ctx.scenes -= oldScenes.toSet()
            oldScenes.forEach { it.dispose(ctx) }
        }
        loadedApp.value?.onDispose(true, ctx)

        // add scene objects from new app
        app.startApp(projectModel, true, ctx)
        projectModel.created?.let { newScenes ->
            ctx.scenes += newScenes.toSet()
            newScenes.forEach {
                it.dispose(ctx)
                it.addNode(editorContent)
                editorCameraTransform.addNode(it.camera)
            }
        }
        loadedApp.set(app)

        if (selectedScene.value == null) {
            selectedScene.set(projectModel.scenes.getOrNull(0))
        }
        menu.sceneBrowser.refreshSceneTree()

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
            logW { "Project not found at ${projFile.absolutePath}, creating new empty project" }
            val proj = newProject()
            saveProject(proj)
            proj
        }
    }

    private fun newProject(): MProject {
        val testScene = MScene(
            nodeProperties = MCommonNodeProperties(
                hierarchyPath = mutableListOf("New Scene"),
                transform = MTransform.IDENTITY
            ),
            clearColor = MColor(MdColor.GREY tone 900),
            meshes = mutableListOf(
                MMesh(
                    MCommonNodeProperties(
                        hierarchyPath = mutableListOf("New Scene", "Test Mesh"),
                        transform = MTransform.IDENTITY
                    ),
                    generatorClass = "de.fabmax.kool.app.SampleProceduralMesh"
                )
            )
        )

        val projectModel =  MProject(
            mainClass = PROJECT_MAIN_CLASS,
            scenes = listOf(testScene)
        )
        return projectModel
    }

    private fun saveProject(projModel: MProject) {
        val modelPath = File(PROJECT_MODEL_PATH)
        modelPath.parentFile.mkdirs()
        modelPath.writeText(Json.encodeToString(projModel))
        logD { "Saved project to ${modelPath.absolutePath}" }
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