package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.scene.Scene

class OverlayScene(val editor: KoolEditor) : Scene("Overlay scene"), EditorOverlay {

    val grid = GridOverlay()
    val selection = SelectionOverlay(this)
    val gizmo = TransformGizmoOverlay(this)
    val sceneObjects = SceneObjectsOverlay()
    val physicsObjects = PhysicsObjectsOverlay()

    private val overlays: List<EditorOverlay> = listOf(
        grid,
        selection,
        gizmo,
        sceneObjects,
        physicsObjects
    )

    init {
        clearColor = null
        clearDepth = false
        tryEnableInfiniteDepth()

        addNode(sceneObjects)
        addNode(physicsObjects)
        addNode(selection)
        addNode(gizmo)
        addNode(grid)
    }

    override fun onEditorSceneChanged(scene: EditorScene) {
        overlays.forEach { it.onEditorSceneChanged(scene) }
    }
}