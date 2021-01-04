package de.fabmax.kool.physics.shapes

import de.fabmax.kool.util.MeshBuilder

expect class CylinderShape(height: Float, radius: Float) : CommonCylinderShape, CollisionShape

abstract class CommonCylinderShape(val height: Float, val radius: Float) {

    open fun generateGeometry(target: MeshBuilder) {
        target.apply {
            cylinder {
                height = this@CommonCylinderShape.height
                radius = this@CommonCylinderShape.radius
                steps = 40
                origin.set(0f, -height/2f, 0f)
            }
        }
    }

}