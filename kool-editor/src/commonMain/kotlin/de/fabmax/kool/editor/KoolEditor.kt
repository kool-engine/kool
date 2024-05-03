package de.fabmax.kool.editor

import de.fabmax.kool.*
import de.fabmax.kool.editor.actions.DeleteNodeAction
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetVisibilityAction
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.ProjectData
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.overlays.GridOverlay
import de.fabmax.kool.editor.overlays.SceneObjectsOverlay
import de.fabmax.kool.editor.overlays.SelectionOverlay
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay
import de.fabmax.kool.editor.ui.EditorUi
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.modules.filesystem.InMemoryFileSystem
import de.fabmax.kool.modules.filesystem.toZip
import de.fabmax.kool.modules.ui2.docking.DockLayout
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun KoolEditor(projectFiles: ProjectFiles, ctx: KoolContext): KoolEditor {
    val projectModel = try {
        val data = Json.decodeFromString<ProjectData>(projectFiles.projectModelFile.read().decodeToString())
        EditorProject(data)
    } catch (e: Exception) {
        logW("KoolEditor") { "Failed loading project model, creating empty" }
        EditorProject.emptyProject()
    }
    return KoolEditor(projectFiles, projectModel, ctx)
}

class KoolEditor(val projectFiles: ProjectFiles, val projectModel: EditorProject, val ctx: KoolContext) {
    init { instance = this }

    val loadedApp = mutableStateOf<LoadedApp?>(null)
    val activeScene = mutableStateOf<SceneModel?>(null)

    val editorInputContext = InputStack.InputHandler("Editor input")
    val editMode = EditorEditMode(this)

    val editorCameraTransform = EditorCamTransform(this)
    private val editorBackgroundScene = scene("editor-camera") {
        addNode(editorCameraTransform)
        clearColor = Color.BLACK
        clearDepth = false
    }

    val editorOverlay = scene("editor-overlay") {
        clearColor = null
        clearDepth = false
        tryEnableInfiniteDepth()
    }
    val gridOverlay = GridOverlay()
    val lightOverlay = SceneObjectsOverlay()
    val gizmoOverlay = TransformGizmoOverlay()
    val selectionOverlay = SelectionOverlay(this)

    val editorContent = Node("Editor Content").apply {
        tags[TAG_EDITOR_SUPPORT_CONTENT] = "true"
        addNode(gridOverlay)
        addNode(lightOverlay)
        addNode(selectionOverlay)
        addNode(gizmoOverlay)

        editorOverlay.addNode(this)
    }

    val appLoader = AppLoader(this)
    val modeController = AppModeController(this)
    val availableAssets = AvailableAssets(projectFiles)
    val ui = EditorUi(this)

    private val editorAppCallbacks = object : ApplicationCallbacks {
        override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
            saveProject()
            saveEditorConfig()
            return PlatformFunctions.onWindowCloseRequest(ctx)
        }

        override fun onFileDrop(droppedFiles: List<LoadableFile>) {
            val targetPath = ui.assetBrowser.selectedDirectory.value?.path ?: ""
            launchOnMainThread {
                availableAssets.importAssets(targetPath, droppedFiles)
            }
        }
    }

    init {
        AppAssets.impl = CachedAppAssets(fileSystemAssetLoader(projectFiles.assets))

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
        PlatformFunctions.editBehavior(behaviorClassName)
    }

    private fun registerAutoSaveOnFocusLoss() {
        // auto save on window focus loss
        var wasFocused = false
        editorContent.onUpdate {
            if (wasFocused && !ctx.isWindowFocused) {
                saveProject()
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
            EditorClipboard.copySelection()
        }
        editorInputContext.addKeyListener(
            name = "Paste",
            keyCode = LocalKeyCode('V'),
            filter = InputStack.KEY_FILTER_CTRL_PRESSED
        ) {
            EditorClipboard.paste()
        }
        editorInputContext.addKeyListener(
            name = "Duplicate",
            keyCode = LocalKeyCode('D'),
            filter = InputStack.KEY_FILTER_CTRL_PRESSED
        ) {
            EditorClipboard.duplicateSelection()
        }
        editorInputContext.addKeyListener(
            name = "Delete selected objects",
            keyCode = KeyboardInput.KEY_DEL
        ) {
            DeleteNodeAction(selectionOverlay.getSelectedSceneNodes()).apply()
        }
        editorInputContext.addKeyListener(
            name = "Hide selected objects",
            keyCode = LocalKeyCode('H'),
            filter = { it.isPressed && !it.isAltDown }
        ) {
            val selection = selectionOverlay.getSelectedSceneNodes()
            SetVisibilityAction(selection, selection.any { !it.isVisibleState.value }).apply()
        }
        editorInputContext.addKeyListener(
            name = "Unhide all hidden objects",
            keyCode = LocalKeyCode('H'),
            filter = { it.isPressed && it.isAltDown }
        ) {
            activeScene.value?.sceneNodes?.filter { !it.isVisibleState.value } ?.let { nodes ->
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
            editMode.mode.set(EditorEditMode.Mode.BOX_SELECT)
        }
        editorInputContext.addKeyListener(
            name = "Move selected object",
            keyCode = LocalKeyCode('G')
        ) {
            editMode.mode.set(EditorEditMode.Mode.MOVE_IMMEDIATE)
        }
        editorInputContext.addKeyListener(
            name = "Rotate selected object",
            keyCode = LocalKeyCode('R')
        ) {
            editMode.mode.set(EditorEditMode.Mode.ROTATE_IMMEDIATE)
        }
        editorInputContext.addKeyListener(
            name = "Scale selected object",
            keyCode = LocalKeyCode('S')
        ) {
            editMode.mode.set(EditorEditMode.Mode.SCALE_IMMEDIATE)
        }
        editorInputContext.addKeyListener(
            name = "Cancel current operation",
            keyCode = KeyboardInput.KEY_ESC
        ) {
            // for now, we take the naive approach and check any possible operation that can be canceled
            // this might not scale well when we have more possible operations...
            when {
                gizmoOverlay.isTransformDrag -> gizmoOverlay.cancelTransformOperation()
                ui.dndController.dndContext.isDrag -> ui.dndController.dndContext.cancelDrag()
                editMode.mode.value != EditorEditMode.Mode.NONE -> editMode.mode.set(EditorEditMode.Mode.NONE)
                selectionOverlay.selection.isNotEmpty() -> selectionOverlay.clearSelection()
            }
        }

        InputStack.pushTop(editorInputContext)
    }

    private fun registerSceneObjectPicking() {
        editorInputContext.pointerListeners += object : InputStack.PointerListener {
            val rayTest = RayTest()

            override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
                val sceneModel = activeScene.value ?: return
                val appScene = sceneModel.drawNode
                val ptr = pointerState.primaryPointer
                if (ptr.isLeftButtonClicked && !ptr.isConsumed()) {
                    if (appScene.computePickRay(ptr, rayTest.ray)) {
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
                        selectionOverlay.selectSingle(selectedNodeModel)
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
        projectModel.getCreatedScenes().forEach { sceneModel ->
            ctx.scenes -= sceneModel.drawNode
            sceneModel.drawNode.removeOffscreenPass(selectionOverlay.selectionPass)
        }
        this.loadedApp.value?.app?.onDispose(ctx)
        selectionOverlay.selectionPass.disposePipelines()

        // initialize newly loaded app
        loadedApp.app.loadApp(projectModel, ctx)

        // add scene objects from new app
        projectModel.getCreatedScenes().let { newScenes ->
            if (newScenes.size != 1) {
                logW { "Unsupported number of scene, currently only single scene setups are supported" }
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

        this.loadedApp.set(loadedApp)
        if (activeScene.value == null) {
            activeScene.set(projectModel.getCreatedScenes().getOrNull(0))
        }
        if (selectionOverlay.selection.isEmpty()) {
            activeScene.value?.let { selectionOverlay.selection.add(it) }
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

    fun saveProject() {
        Assets.launch {
            val text = jsonCodec.encodeToString(projectModel.projectData)
            projectFiles.projectModelFile.write(text.encodeToByteArray().toBuffer())
            logD { "Saved project model" }
        }
    }

    fun exportProject() {
        Assets.launch {
            val zippedProj = InMemoryFileSystem(projectFiles.fileSystem).toZip()
            Assets.saveFileByUser(zippedProj, "kool-editor-proj.zip")
        }
    }

    companion object {
        lateinit var instance: KoolEditor
            private set

        const val TAG_EDITOR_SUPPORT_CONTENT = "%editor-content-hidden"

        @OptIn(ExperimentalSerializationApi::class)
        val jsonCodec = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }
    }
}