package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.simpleShape
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

expect class CapsuleShape(height: Float, radius: Float) : CommonCapsuleShape, CollisionShape

abstract class CommonCapsuleShape(val height: Float, val radius: Float) {

    open fun generateGeometry(target: MeshBuilder) {
        target.apply {
            profile {
                val halfHeight = height / 2
                simpleShape(false) {
                    val steps = 8
                    for (i in 0..steps) {
                        val a = (i / steps.toFloat() * PI / 2 + PI * 1.5).toFloat()
                        val x = cos(a)
                        val y = sin(a)
                        xy(x * radius, y * radius - halfHeight)
                        normals += MutableVec3f(x, y, 0f)
                    }
                    for (i in 0..steps) {
                        val a = (i / steps.toFloat() * PI / 2).toFloat()
                        val x = cos(a)
                        val y = sin(a)
                        xy(x * radius, y * radius + halfHeight)
                        normals += MutableVec3f(x, y, 0f)
                    }
                }

                for (i in 0 .. 20) {
                    sample()
                    rotate(0f, -360f / 20, 0f)
                }
            }
        }
    }

}
