package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.geometry.MeshBuilder

expect fun SphereGeometry(radius: Float): SphereGeometry

interface SphereGeometry : CollisionGeometry {
    val radius: Float

    override fun generateMesh(target: MeshBuilder) {
        target.icoSphere {
            radius = this@SphereGeometry.radius
            steps = 2
        }
    }

    override fun getBounds(result: BoundingBoxF) = result.set(-radius, -radius, -radius, radius, radius, radius)

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        val i = 2f / 5f * mass * radius * radius
        return result.set(i, i, i)
    }
}
