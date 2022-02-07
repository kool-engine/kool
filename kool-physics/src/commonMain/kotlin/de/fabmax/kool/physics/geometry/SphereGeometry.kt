package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.scene.geometry.MeshBuilder

expect class SphereGeometry(radius: Float) : CommonSphereGeometry, CollisionGeometry

abstract class CommonSphereGeometry(val radius: Float) {
    open fun generateMesh(target: MeshBuilder) {
        target.icoSphere {
            radius = this@CommonSphereGeometry.radius
            steps = 2
        }
    }

    open fun getBounds(result: BoundingBox) = result.set(-radius, -radius, -radius, radius, radius, radius)

    open fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        val i = 2f / 5f * mass * radius * radius
        return result.set(i, i, i)
    }
}
