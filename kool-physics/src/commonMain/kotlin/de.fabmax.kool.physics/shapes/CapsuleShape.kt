package de.fabmax.kool.physics.shapes

expect class CapsuleShape(height: Float, radius: Float) : CollisionShape {
    val height: Float
    val radius: Float
}