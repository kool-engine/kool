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

@Suppress("UnsafeCastFromDynamic")
object PxRevoluteJointFlag {
    val eLIMIT_ENABLED: Int get() = PhysX.physx._emscripten_enum_physx_PxRevoluteJointFlag_eLIMIT_ENABLED()
    val eDRIVE_ENABLED: Int get() = PhysX.physx._emscripten_enum_physx_PxRevoluteJointFlag_eDRIVE_ENABLED()
    val eDRIVE_FREESPIN: Int get() = PhysX.physx._emscripten_enum_physx_PxRevoluteJointFlag_eDRIVE_FREESPIN()
}

external interface PxRevoluteJointFlags : PxFlags
