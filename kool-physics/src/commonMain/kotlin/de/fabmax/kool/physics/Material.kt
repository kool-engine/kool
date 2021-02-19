package de.fabmax.kool.physics

expect class Material(staticFriction: Float, dynamicFriction: Float = staticFriction, restitution: Float = 0.2f) : Releasable {
    val staticFriction: Float
    val dynamicFriction: Float
    val restitution: Float
}