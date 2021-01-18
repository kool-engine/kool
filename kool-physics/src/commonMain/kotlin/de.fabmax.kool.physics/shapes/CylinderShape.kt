package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.MeshBuilder

expect class CylinderShape(length: Float, radius: Float) : CommonCylinderShape, CollisionShape

abstract class CommonCylinderShape(val length: Float, val radius: Float) {

    open fun generateGeometry(target: MeshBuilder) {
        target.apply {
            withTransform {
                rotate(90f, Vec3f.Z_AXIS)
                cylinder {
                    height = this@CommonCylinderShape.length
                    radius = this@CommonCylinderShape.radius
                    steps = 30
                    origin.set(0f, -height/2f, 0f)
                }
            }
        }
    }

}