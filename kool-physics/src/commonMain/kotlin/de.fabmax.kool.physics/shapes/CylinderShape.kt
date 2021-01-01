package de.fabmax.kool.physics.shapes

expect class CylinderShape(radius: Float, height: Float) : CollisionShape {
    val radius: Float
    val height: Float
}