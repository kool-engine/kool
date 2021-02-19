package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder
import physx.PxGeometry
import physx.destroy

actual interface CollisionGeometry : Releasable {
    val pxGeometry: PxGeometry

    actual fun generateMesh(target: MeshBuilder)

    actual fun getBounds(result: BoundingBox): BoundingBox

    actual fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f

    override fun release() {
        pxGeometry.destroy()
    }
}