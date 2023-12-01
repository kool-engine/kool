package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape

expect fun CapsuleGeometry(height: Float, radius: Float): CapsuleGeometry

interface CapsuleGeometry : CollisionGeometry {
    val height: Float
    val radius: Float

    override fun generateMesh(target: MeshBuilder) {
        target.apply {
            profile {
                val halfHeight = height / 2f
                simpleShape(false) {
                    xyArc(Vec2f(halfHeight + radius, 0f), Vec2f(halfHeight, 0f), 90f.deg, 10, true)
                    xyArc(Vec2f(-halfHeight, radius), Vec2f(-halfHeight, 0f), 90f.deg, 10, true)
                }
                for (i in 0 .. 20) {
                    sample()
                    rotate(360f.deg / 20, 0f.deg, 0f.deg)
                }
            }
        }
    }

    override fun getBounds(result: BoundingBoxF): BoundingBoxF {
        return result.set(-radius - height / 2f, -radius, -radius, radius + height / 2f, radius, radius)
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // rough approximation: use inertia of a slightly shorter cylinder
        val h = height - radius
        val iy = 0.5f * mass * radius * radius
        val ixz = 1f / 12f * mass * (3 * radius * radius + h * h)
        return result.set(ixz, iy, ixz)
    }
}
