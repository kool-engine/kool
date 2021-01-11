package de.fabmax.kool.physics

class RigidBodyProperties {
    var collisionGroupBits = 1
    var collisionMask = 0x7fffffff

    var friction = 0.5f
    var restitution = 0.2f

    var linearDamping = 0.05f
    var angularDamping = 0.0f
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
