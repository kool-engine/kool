package de.fabmax.kool.physics

data class Material(val staticFriction: Float, val dynamicFriction: Float = staticFriction, val restitution: Float = 0.2f)