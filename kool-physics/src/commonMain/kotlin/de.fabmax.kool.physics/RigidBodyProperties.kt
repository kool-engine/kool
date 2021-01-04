package de.fabmax.kool.physics

class RigidBodyProperties {
    var friction = 0.5f
    var rollingFriction = 0f
    var restitution = 0f

    var linearDamping = 0f
    var angularDamping = 0f
    var sleepThreshold = 1f
}

fun rigidBodyProperties(block: RigidBodyProperties.() -> Unit) = RigidBodyProperties().apply(block)
