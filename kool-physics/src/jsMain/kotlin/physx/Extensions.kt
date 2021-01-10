package physx

external interface PxDefaultAllocator

external interface PxDefaultErrorCallback

external interface PxJoint

external interface PxRevoluteJoint: PxJoint {
    fun getDriveVelocity(): Float
    fun setDriveVelocity(velocity: Float, autoWake: Boolean)

    fun getDriveForceLimit(): Float
    fun setDriveForceLimit(limit: Float)

    fun getDriveGearRatio(): Float
    fun setDriveGearRatio(ratio: Float)

    fun getRevoluteJointFlags(): PxRevoluteJointFlags
    fun setRevoluteJointFlags(flags: PxRevoluteJointFlags)
}

external interface PxRevoluteJointFlag {
    val eLIMIT_ENABLED: Int
    val eDRIVE_ENABLED: Int
    val eDRIVE_FREESPIN: Int

    fun isSet(flags: PxRevoluteJointFlags, flag: Int): Boolean
    fun set(flags: PxRevoluteJointFlags, flag: Int)
    fun clear(flags: PxRevoluteJointFlags, flag: Int)
}

external interface PxRevoluteJointFlags
fun PxRevoluteJointFlags.isSet(flag: Int) = PhysX.PxRevoluteJointFlag.isSet(this, flag)
fun PxRevoluteJointFlags.set(flag: Int) = PhysX.PxRevoluteJointFlag.set(this, flag)
fun PxRevoluteJointFlags.clear(flag: Int) = PhysX.PxRevoluteJointFlag.clear(this, flag)
