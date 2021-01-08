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
    val eLIMIT_ENABLED: PxRevoluteJointFlag
    val eDRIVE_ENABLED: PxRevoluteJointFlag
    val eDRIVE_FREESPIN: PxRevoluteJointFlag
}

external interface PxRevoluteJointFlags {
    fun isSet(flag: PxRevoluteJointFlag)
    fun set(flag: PxRevoluteJointFlag)
    fun clear(flag: PxRevoluteJointFlag)
}
