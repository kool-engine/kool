package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.geometry.MeshBuilder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

expect fun CylinderGeometry(length: Float, radius: Float): CylinderGeometry

interface CylinderGeometry : CollisionGeometry {

    val length: Float
    val radius: Float

    override fun generateMesh(target: MeshBuilder) {
        target.apply {
            withTransform {
                // physics cylinder extends along the x-axis, MeshBuilder's cylinder extends along y-axis
                rotate(90f.deg, Vec3f.Z_AXIS)
                cylinder {
                    height = this@CylinderGeometry.length
                    radius = this@CylinderGeometry.radius
                    steps = 32
                }
            }
        }
    }

    override fun getBounds(result: BoundingBoxF): BoundingBoxF {
        result.set(-length * 0.5f, -radius, -radius, length * 0.5f, radius, radius)
        return result
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
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