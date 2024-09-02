package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.localToViewD
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Float32Buffer

abstract class OverlayObject(val gameEntity: GameEntity) {
    abstract val color: Color

    private val modelMat: Mat4d get() = gameEntity.localToViewD

    private val invModelMat = MutableMat4d()

    fun addInstance(target: Float32Buffer, color: Color = this.color) {
        val selectionOv = KoolEditor.instance.selectionOverlay
        val instColor = if (selectionOv.isSelected(gameEntity)) selectionOv.selectionColor.toLinear() else color
        modelMat.putTo(target)
        instColor.putTo(target)
    }

    fun rayTest(rayTest: RayTest, mesh: Mesh): Boolean {
        val pos = modelMat.getTranslation()
        val radius = mesh.geometry.bounds.size.length()
        val n = pos.nearestPointOnRay(rayTest.ray.origin, rayTest.ray.direction, MutableVec3d())
        if (n.distance(pos) < radius) {
            val d = n.distance(rayTest.ray.origin)
            if (d < rayTest.hitDistance) {
                modelMat.invert(invModelMat)
                return meshRayTest(rayTest, mesh)
            }
        }
        return false
    }

    private fun meshRayTest(rayTest: RayTest, mesh: Mesh): Boolean {
        modelMat.invert(invModelMat)
        val localRay = rayTest.getRayTransformed(invModelMat)
        val isHit = mesh.rayTest.rayTest(rayTest, localRay)
        if (isHit) {
            // fixme: rather ugly workaround: mesh ray test transforms hit position to global coordinates using
            //  the mesh's transform not the instance's, leading to a wrong hit-position / distance
            mesh.toLocalCoords(rayTest.hitPositionGlobal)
            modelMat.transform(rayTest.hitPositionGlobal)
            rayTest.setHit(mesh, rayTest.hitPositionGlobal)
        }
        return isHit
    }
}