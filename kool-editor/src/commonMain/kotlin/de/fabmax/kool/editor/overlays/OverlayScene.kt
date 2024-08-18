package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.scene
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.PlaneF
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Scene

class OverlayScene(val editor: KoolEditor) : Scene("Overlay scene"), EditorOverlay {

    val grid = GridOverlay()
    val selection = SelectionOverlay(this)
    val gizmo = TransformGizmoOverlay(this)
    val sceneObjects = SceneObjectsOverlay()
    val physicsObjects = PhysicsObjectsOverlay()

    var currentScene: EditorScene? = null
        private set
    var lastPickPosition: Vec3f? = null
        private set

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
            var hitDist = Float.POSITIVE_INFINITY
            var selectedEntity: GameEntity? = null
            for (i in overlays.indices) {
                rayTest.clear()
                val hitEntity = overlays[i].pick(rayTest)
                if (rayTest.isHit && rayTest.hitDistanceSqr < hitDist) {
                    hitDist = rayTest.hitDistanceSqr
                    lastPickPosition = Vec3f(rayTest.hitPositionGlobal)
                    selectedEntity = hitEntity
                }
            }
            selection.selectSingle(selectedEntity)

            if (lastPickPosition == null) {
                val cam = editorScene.scene.camera
                val camPlane = PlaneF(cam.globalLookAt, cam.globalLookDir)
                val pickPos = MutableVec3f()
                camPlane.intersectionPoint(rayTest.ray, pickPos)
                lastPickPosition = pickPos
            }
        }
    }

    override fun onEditorSceneChanged(scene: EditorScene) {
        currentScene = scene
        overlays.forEach { it.onEditorSceneChanged(scene) }
    }
}