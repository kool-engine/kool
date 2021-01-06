package de.fabmax.kool.physics

class RigidBodyProperties {
    var collisionGroupBits = 1
    var collisionMask = 0xffffffff.toInt()

    var friction = 0.5f
    var rollingFriction = 0f
    var restitution = 0f

    var linearDamping = 0f
    var angularDamping = 0f
    var sleepThreshold = 1f
    var canSleep = true

    fun setCollisionGroup(group: Int, selfGroupCollision: Boolean = true) {
        collisionGroupBits = 1 shl group
        if (!selfGroupCollision) {
            clearCollidesWith(group)
        }
    }

    fun clearCollidesWith(group: Int) {
        collisionMask = collisionMask and (1 shl group).inv()
    }

    fun setCollidesWith(group: Int) {
        collisionMask = collisionMask or (1 shl group)
    }
}

fun rigidBodyProperties(block: RigidBodyProperties.() -> Unit) = RigidBodyProperties().apply(block)
