package de.fabmax.kool.physics

class RigidBodyProperties {
    val simFilterData = PhysicsFilterData()
    val queryFilterData = PhysicsFilterData()

    var material = Material(0.5f, 0.5f, 0.2f)

    var linearDamping = 0.05f
    var angularDamping = 0.0f
    var canSleep = true

    init {
        setCollisionGroup(0)
        collidesWithEverything()
    }

    fun setCollisionGroup(group: Int) {
        simFilterData.data[0] = 1 shl group
    }

    fun clearCollidesWith(group: Int) {
        simFilterData.clearBit(1, group)
    }

    fun setCollidesWith(group: Int) {
        simFilterData.setBit(1, group)
    }

    fun collidesWithEverything() {
        simFilterData.data[1] = -1
    }
}

fun rigidBodyProperties(block: RigidBodyProperties.() -> Unit) = RigidBodyProperties().apply(block)
