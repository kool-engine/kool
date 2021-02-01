package de.fabmax.kool.physics

class RigidBodyProperties {
    val simFilterData = FilterData()
    val queryFilterData = FilterData()

    var linearDamping = 0.05f
    var angularDamping = 0.02f

    init {
        setCollisionGroup(0)
        setCollidesWithEverything()
    }

    fun setCollisionGroup(group: Int) {
        if (group !in 0..31) {
            throw IllegalArgumentException("group must be within 0..31 (is $group)")
        }
        simFilterData.data[0] = 1 shl group
    }

    fun clearCollidesWith(group: Int) {
        if (group !in 0..31) {
            throw IllegalArgumentException("group must be within 0..31 (is $group)")
        }
        simFilterData.clearBit(1, group)
    }

    fun setCollidesWith(group: Int) {
        if (group !in 0..31) {
            throw IllegalArgumentException("group must be within 0..31 (is $group)")
        }
        simFilterData.setBit(1, group)
    }

    fun setCollidesWithEverything() {
        simFilterData.data[1] = -1
    }
}

fun rigidBodyProperties(block: RigidBodyProperties.() -> Unit) =
    RigidBodyProperties().apply(block)
