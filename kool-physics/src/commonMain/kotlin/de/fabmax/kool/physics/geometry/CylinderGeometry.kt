package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.scene.geometry.MeshBuilder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

expect class CylinderGeometry(length: Float, radius: Float) : CommonCylinderGeometry, CollisionGeometry

abstract class CommonCylinderGeometry(val length: Float, val radius: Float) {
    open fun generateMesh(target: MeshBuilder) {
        target.apply {
            withTransform {
                // physics cylinder extends along the x-axis, MeshBuilder's cylinder extends along y-axis
                rotate(90f, Vec3f.Z_AXIS)
                cylinder {
                    height = this@CommonCylinderGeometry.length
                    radius = this@CommonCylinderGeometry.radius
                    steps = 32
                    origin.set(0f, -height/2f, 0f)
                }
            }
        }
    }

    open fun getBounds(result: BoundingBox): BoundingBox {
        result.set(-length * 0.5f, -radius, -radius, length * 0.5f, radius, radius)
        return result
    }

    open fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        val ix = 0.5f * mass * radius * radius
        val iyz = 1f / 12f * mass * (3 * radius * radius + length * length)
        return result.set(ix, iyz, iyz)
    }

    companion object {
        fun convexMeshPoints(length: Float, radius: Float, n: Int = 32): List<Vec3f> {
            val points = mutableListOf<Vec3f>()
            for (i in 0 until n) {
                val a = i * 2f * PI.toFloat() / n
                val y = cos(a) * radius
                val z = sin(a) * radius
                points.add(Vec3f(length * -0.5f, y, z))
                points.add(Vec3f(length * 0.5f, y, z))
            }
            return points
        }
    }
}