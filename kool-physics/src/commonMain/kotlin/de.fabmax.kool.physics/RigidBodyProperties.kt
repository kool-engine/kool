package de.fabmax.kool.physics

class RigidBodyProperties {
    var collisionGroup = 1
    var collisionMask = 0xffffffff.toInt()

    var friction = 0.5f
    var rollingFriction = 0f
    var restitution = 0f

    var linearDamping = 0f
    var angularDamping = 0f
    var sleepThreshold = 1f
    var canSleep = true

    fun clearCollidesWith(group: Int) {
        collisionMask = collisionMask and group.inv()
    }

    fun setCollidesWith(group: Int) {
        collisionMask = collisionMask or group
    }
}

fun rigidBodyProperties(block: RigidBodyProperties.() -> Unit) = RigidBodyProperties().apply(block)
