package physx

external interface PxDefaultAllocator

external interface PxDefaultErrorCallback

external interface PxJoint : PxBase

external interface PxRevoluteJoint : PxJoint {
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
}

external interface PxRevoluteJointFlags : PxFlags
