package de.fabmax.kool.editor

import de.fabmax.kool.*
import de.fabmax.kool.editor.actions.DeleteSceneNodesAction
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetVisibilityAction
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.ProjectData
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.overlays.GridOverlay
import de.fabmax.kool.editor.overlays.SceneObjectsOverlay
import de.fabmax.kool.editor.overlays.SelectionOverlay
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay
import de.fabmax.kool.editor.ui.EditorUi
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerState
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
    val projModelJson = try {
        projectFiles.projectModelFile.read().decodeToString()
    } catch (e: Exception) {
        ""
    }
    if (projModelJson.isEmpty()) {
        logI("KoolEditor") { "Project file not found, creating empty" }
        return KoolEditor(projectFiles, EditorProject.emptyProject(), ctx)
    }

    val project = try {
        EditorProject(Json.decodeFromString<ProjectData>(projModelJson))
    } catch (e: Exception) {
        e.printStackTrace()
        error("Failed deserializing project, fix / delete existing project file")
    }
    return KoolEditor(projectFiles, project, ctx)
}

class KoolEditor(val projectFiles: ProjectFiles, val projectModel: EditorProject, val ctx: KoolContext) {
    init { instance = this }

    val loadedApp = mutableStateOf<LoadedApp?>(null)
    val activeScene = mutableStateOf<SceneModel?>(null)

    val editorInputContext = EditorKeyListener("Edit mode")
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
    val sceneObjectsOverlay = SceneObjectsOverlay()
    val gizmoOverlay = TransformGizmoOverlay()
    val selectionOverlay = SelectionOverlay(this)

    val editorContent = Node("Editor Content").apply {
        tags[TAG_EDITOR_SUPPORT_CONTENT] = "true"
        addNode(gridOverlay)
        addNode(sceneObjectsOverlay)
        addNode(selectionOverlay)
        addNode(gizmoOverlay)

        editorOverlay.addNode(this)
    }

    val appLoader = AppLoader(this)
    val availableAssets = AvailableAssets(projectFiles)
    val ui = EditorUi(this)

    private val editorAppCallbacks = object : ApplicationCallbacks {
        override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
            PlatformFunctions.saveProjectBlocking()
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

    fun startApp() {
        val app = loadedApp.value?.app ?: return
        val sceneModel = projectModel.createdScenes.values.firstOrNull() ?: return

        logI { "Start app" }
        InputStack.handlerStack.removeAll { it is EditorKeyListener }

        // fixme: a bit hacky currently: restore app scene camera
        //  it was replaced by custom editor cam during editor app load
        sceneModel.cameraState.value?.camera?.let { cam ->
            sceneModel.drawNode.camera = cam
            (cam as? PerspectiveCamera)?.let {
                val aoPipeline = sceneModel.getComponent<SsaoComponent>()?.aoPipeline as? AoPipeline.ForwardAoPipeline
                aoPipeline?.proxyCamera?.trackedCam = it
            }
        }

        AppState.appModeState.set(AppMode.PLAY)
        app.startApp(projectModel, KoolSystem.requireContext())
        setEditorOverlayVisibility(false)
        ui.appStateInfo.set("App is running")
    }

    fun stopApp() {
        logI { "Stop app" }
        AppState.appModeState.set(AppMode.EDIT)
        setEditorOverlayVisibility(true)
        appLoader.reloadApp()

        editorInputContext.push()
    }

    fun resetApp() {
        logI { "Reset app" }
        appLoader.reloadApp()
    }

    private fun setEditorOverlayVisibility(isVisible: Boolean) {
        editorOverlay.children.forEach {
            it.isVisible = isVisible
        }
        ui.sceneView.isShowOverlays.set(isVisible)
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
                Assets.launch { saveProject() }
            }
            wasFocused = ctx.isWindowFocused
        }
    }

    private fun registerKeyBindings() {
        editorInputContext.addKeyListener(Key.ToggleBoxSelectMode) { editMode.toggleMode(EditorEditMode.Mode.BOX_SELECT) }
        editorInputContext.addKeyListener(Key.ToggleImmediateMoveMode) { editMode.toggleMode(EditorEditMode.Mode.MOVE_IMMEDIATE) }
        editorInputContext.addKeyListener(Key.ToggleImmediateRotateMode) { editMode.toggleMode(EditorEditMode.Mode.ROTATE_IMMEDIATE) }
        editorInputContext.addKeyListener(Key.ToggleImmediateScaleMode) { editMode.toggleMode(EditorEditMode.Mode.SCALE_IMMEDIATE) }
        editorInputContext.addKeyListener(Key.ToggleMoveMode) { editMode.toggleMode(EditorEditMode.Mode.MOVE) }
        editorInputContext.addKeyListener(Key.ToggleRotateMode) { editMode.toggleMode(EditorEditMode.Mode.ROTATE) }
        editorInputContext.addKeyListener(Key.ToggleScaleMode) { editMode.toggleMode(EditorEditMode.Mode.SCALE) }

        editorInputContext.addKeyListener(Key.FocusSelected) { editorCameraTransform.focusSelectedObject() }

        editorInputContext.addKeyListener(Key.DeleteSelected) {
            DeleteSceneNodesAction(selectionOverlay.getSelectedSceneNodes()).apply()
        }
        editorInputContext.addKeyListener(Key.HideSelected) {
            val selection = selectionOverlay.getSelectedSceneNodes()
            SetVisibilityAction(selection, selection.any { !it.isVisibleState.value }).apply()
        }
        editorInputContext.addKeyListener(Key.UnhideHidden) {
            val hidden = activeScene.value?.sceneNodes?.filter { !it.isVisibleState.value }
            hidden?.let { nodes -> SetVisibilityAction(nodes, true).apply() }
        }

        editorInputContext.addKeyListener(Key.Duplicate) { EditorClipboard.duplicateSelection() }
        editorInputContext.addKeyListener(Key.Copy) { EditorClipboard.copySelection() }
        editorInputContext.addKeyListener(Key.Paste) { EditorClipboard.paste() }
        editorInputContext.addKeyListener(Key.Undo) { EditorActions.undo() }
        editorInputContext.addKeyListener(Key.Redo) { EditorActions.redo() }
        editorInputContext.addKeyListener(Key.Cancel) {
            when {
                editMode.mode.value != EditorEditMode.Mode.NONE -> editMode.mode.set(EditorEditMode.Mode.NONE)
                selectionOverlay.selection.isNotEmpty() -> selectionOverlay.clearSelection()
            }
        }

        editorInputContext.push()
    }

    private fun registerSceneObjectPicking() {
        editorInputContext.pointerListeners += object : InputStack.PointerListener {
            override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
                val ptr = pointerState.primaryPointer
                if (ptr.isLeftButtonClicked && !ptr.isConsumed()) {
                    selectionOverlay.clickSelect(ptr)
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
        projectModel.createdScenes.values.forEach { sceneModel ->
            ctx.scenes -= sceneModel.drawNode
            sceneModel.drawNode.removeOffscreenPass(selectionOverlay.selectionPass)
        }
        this.loadedApp.value?.app?.onDispose(ctx)
        selectionOverlay.selectionPass.disposePipelines()

        // initialize newly loaded app
        loadedApp.app.loadApp(projectModel, ctx)

        // add scene objects from new app
        projectModel.createdScenes.values.let { newScenes ->
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
            activeScene.set(projectModel.createdScenes.values.first())
        }
        if (selectionOverlay.selection.isEmpty()) {
            activeScene.value?.let { selectionOverlay.selectSingle(it) }
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
        sceneObjectsOverlay.updateOverlayObjects()
    }

    private fun saveEditorConfig() {
        DockLayout.saveLayout(ui.dock, "editor.ui.layout")
    }

    suspend fun saveProject() {
        val text = jsonCodec.encodeToString(projectModel.projectData)
        projectFiles.projectModelFile.write(text.encodeToByteArray().toBuffer())
        logD { "Saved project model" }
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