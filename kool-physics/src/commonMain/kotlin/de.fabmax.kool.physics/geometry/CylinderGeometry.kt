package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder

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
}