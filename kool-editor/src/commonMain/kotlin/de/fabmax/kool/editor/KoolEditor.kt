package de.fabmax.kool.editor

import de.fabmax.kool.*
import de.fabmax.kool.editor.actions.DeleteSceneNodesAction
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetVisibilityAction
import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.overlays.GridOverlay
import de.fabmax.kool.editor.overlays.SceneObjectsOverlay
import de.fabmax.kool.editor.overlays.SelectionOverlay
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay
import de.fabmax.kool.editor.ui.EditorUi
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.modules.filesystem.InMemoryFileSystem
import de.fabmax.kool.modules.filesystem.copyRecursively
import de.fabmax.kool.modules.filesystem.toZip
import de.fabmax.kool.modules.filesystem.writeText
import de.fabmax.kool.modules.ui2.docking.DockLayout
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun KoolEditor(projectFiles: ProjectFiles, ctx: KoolContext): KoolEditor {
    val projDataDir = projectFiles.projectModelDir
    val reader = ProjectReader(projDataDir)
    val data = reader.loadTree() ?: EditorProject.emptyProjectData()
    if (reader.parserErrors > 0) {
        fun Int.toString(len: Int): String {
            var str = "$this"
            while (str.length < len) str = "0$str"
            return str
        }
        val dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val backupName = "project_backup_" +
                "${dateTime.year}${dateTime.monthNumber.toString(2)}${dateTime.dayOfMonth.toString(2)}_" +
                "${dateTime.hour.toString(2)}${dateTime.minute.toString(2)}${dateTime.second.toString(2)}"
        logE("KoolEditor") { "ProjectReader reported errors, backing up original project data as $backupName" }
        val backupDir = projectFiles.fileSystem.createDirectory("src/commonMain/resources/$backupName")
        projDataDir.copyRecursively(backupDir)
    }
    return KoolEditor(projectFiles, EditorProject(data), ctx)
}

class KoolEditor(val projectFiles: ProjectFiles, val projectModel: EditorProject, val ctx: KoolContext) {
    init { instance = this }

    val loadedApp = mutableStateOf<LoadedApp?>(null)
    val activeScene = mutableStateOf<EditorScene?>(null)

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
            onExit()
            return true
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

    suspend fun startApp() {
        saveProject()

        val app = loadedApp.value?.app ?: return
        val sceneModel = projectModel.createdScenes.values.firstOrNull() ?: return

        logI { "Start app" }
        editMode.mode.set(EditorEditMode.Mode.NONE)
        InputStack.handlerStack.filterIsInstance<EditorKeyListener>().forEach {
            InputStack.handlerStack.stageRemove(it)
        }

        // fixme: a bit hacky currently: restore app scene camera
        //  it was replaced by custom editor cam during editor app load
        sceneModel.sceneComponent.cameraComponent?.drawNode?.let { cam ->
            sceneModel.scene.camera = cam
            (cam as? PerspectiveCamera)?.let {
                val aoPipeline = sceneModel.sceneEntity.getComponent<SsaoComponent>()?.aoPipeline as? AoPipeline.ForwardAoPipeline
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

    fun onExit() {
        PlatformFunctions.saveProjectBlocking()
        saveEditorConfig()
        PlatformFunctions.onExit(ctx)
    }

    private fun setEditorOverlayVisibility(isVisible: Boolean) {
        editorCameraTransform.isVisible = isVisible
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
            SetVisibilityAction(selection, selection.any { !it.isVisible }).apply()
        }
        editorInputContext.addKeyListener(Key.UnhideHidden) {
            val hidden = activeScene.value?.sceneEntities?.values?.filter { !it.isVisible }
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
        if (!AppState.isEditMode) {
            stopApp()
        }

        val prevSelection = selectionOverlay.selection.map { it.id }
        selectionOverlay.clearSelection()
        ctx.scenes -= editorOverlay

        // clear scene objects from old app
        editorCameraTransform.clearChildren()
        editorCameraTransform.addNode(editorBackgroundScene.camera)
        editorCameraTransform.addNode(editorOverlay.camera)

        // dispose old scene + objects
        projectModel.createdScenes.values.forEach { editorScene ->
            ctx.scenes -= editorScene.scene
            editorScene.scene.removeOffscreenPass(selectionOverlay.selectionPass)
        }
        this.loadedApp.value?.app?.onDispose(ctx)
        selectionOverlay.selectionPass.disposePipelines()
        projectModel.releaseScenes()

        // initialize newly loaded app
        loadedApp.app.loadApp(projectModel, ctx)

        // add scene objects from new app
        projectModel.createdScenes.values.let { newScenes ->
            if (newScenes.size != 1) {
                logW { "Unsupported number of scene, currently only single scene setups are supported" }
            }
            newScenes.firstOrNull()?.let { editorScene ->
                val scene = editorScene.scene
                ctx.scenes += scene

                scene.addOffscreenPass(selectionOverlay.selectionPass)
                selectionOverlay.selectionPass.drawNode = scene

                // replace original scene cam with editor cam
                val editorCam = PerspectiveCamera()
                val far = if (editorOverlay.isInfiniteDepth) 1e9f else 1000f
                editorCam.setClipRange(0.1f, far)
                scene.camera = editorCam
                editorCameraTransform.addNode(scene.camera)
                ui.sceneView.applyViewportTo(scene)

                val aoPipeline = editorScene.sceneEntity.getComponent<SsaoComponent>()?.aoPipeline as? AoPipeline.ForwardAoPipeline
                aoPipeline?.proxyCamera?.trackedCam = editorCam
            }
        }

        this.loadedApp.set(loadedApp)
        activeScene.set(projectModel.createdScenes.values.first())

        selectionOverlay.setSelection(prevSelection.mapNotNull { it.gameEntity })
        ui.objectProperties.windowSurface.triggerUpdate()

        if (AppState.appMode == AppMode.EDIT) {
            ui.appStateInfo.set("App is in edit mode")
            setEditorOverlayVisibility(true)
        } else {
            ui.appStateInfo.set("App is running")
            setEditorOverlayVisibility(false)
        }

        updateOverlays()
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
        ProjectWriter.saveProjectData(projectModel.projectData, projectFiles.projectModelDir)
        // also save single file version
        projectFiles.getProjectFileMonolithic().writeText(jsonCodec.encodeToString(projectModel.projectData))
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