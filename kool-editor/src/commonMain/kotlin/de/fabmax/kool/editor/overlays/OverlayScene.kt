package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.scene
import de.fabmax.kool.editor.sceneOrigin
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.ClearColorLoad
import de.fabmax.kool.pipeline.ClearDepthLoad
import de.fabmax.kool.scene.Scene

class OverlayScene(val editor: KoolEditor) : Scene("Overlay scene"), EditorOverlay {

    val grid = GridOverlay(this)
    val selection = SelectionOverlay(this)
    val gizmo = TransformGizmoOverlay(this)
    val sceneObjects = SceneObjectsOverlay()
    val physicsObjects = PhysicsObjectsOverlay()

    var currentScene: EditorScene? = null
        private set
    var lastPickPosition: Vec3d? = null
        private set

    private val overlays: List<EditorOverlay> = listOf(
        grid,
        selection,
        gizmo,
        sceneObjects,
        physicsObjects
    )

    init {
        clearColor = ClearColorLoad
        clearDepth = ClearDepthLoad

        addNode(grid)
        addNode(sceneObjects)
        addNode(physicsObjects)
        addNode(selection)
        addNode(gizmo)
    }

    fun doPicking(ptr: Pointer) {
        val editorScene = currentScene ?: return
        val rayTest = RayTest()

        lastPickPosition = null

        if (editorScene.hitTest.computePickRay(ptr, rayTest.ray)) {
            var hitDist = Double.POSITIVE_INFINITY
            var selectedEntity: GameEntity? = null
            for (i in overlays.indices) {
                rayTest.clear()
                val hitEntity = overlays[i].pick(rayTest)
                if (rayTest.isHit && rayTest.hitDistance < hitDist) {
                    hitDist = rayTest.hitDistance
                    lastPickPosition = rayTest.hitPositionGlobal - editor.sceneOrigin
                    selectedEntity = hitEntity
                }
            }
            selection.selectSingle(selectedEntity)

            if (lastPickPosition == null) {
                val cam = editorScene.scene.camera
                val camPlane = PlaneD(cam.globalLookAt.toVec3d(), cam.globalLookDir.toVec3d())
                val pickPos = MutableVec3d()
                camPlane.intersectionPoint(rayTest.ray, pickPos)
                lastPickPosition = pickPos - editor.sceneOrigin
            }
        }
    }

    override fun onEditorSceneChanged(scene: EditorScene) {
        currentScene = scene
        overlays.forEach { it.onEditorSceneChanged(scene) }
    }
}