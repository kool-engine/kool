package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.simpleShape

expect class CapsuleShape(height: Float, radius: Float) : CommonCapsuleShape, CollisionShape

abstract class CommonCapsuleShape(val height: Float, val radius: Float) {

    open fun generateGeometry(target: MeshBuilder) {
        target.apply {
            profile {
                val halfHeight = height / 2f
                simpleShape(false) {
                    xyArc(Vec2f(halfHeight + radius, 0f), Vec2f(halfHeight, 0f), 90f, 10, true)
                    xyArc(Vec2f(-halfHeight, radius), Vec2f(-halfHeight, 0f), 90f, 10, true)
                }
                for (i in 0 .. 20) {
                    sample()
                    rotate(360f / 20, 0f, 0f)
                }
            }
        }
    }

}
