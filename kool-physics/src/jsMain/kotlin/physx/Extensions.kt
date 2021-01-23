/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxDefaultAllocator
fun PxDefaultAllocator(): PxDefaultAllocator {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxDefaultAllocator()")
}

external interface PxDefaultCpuDispatcher : PxCpuDispatcher

external interface PxJoint : PxBase

external interface PxRevoluteJoint : PxJoint {
    fun setDriveVelocity(velocity: Float)
    fun setDriveVelocity(velocity: Float, autowake: Boolean)
    fun getDriveVelocity(): Float
    fun setDriveForceLimit(limit: Float)
    fun getDriveForceLimit(): Float
    fun setDriveGearRatio(ratio: Float)
    fun getDriveGearRatio(): Float
    fun setRevoluteJointFlags(flags: PxRevoluteJointFlags)
    fun getRevoluteJointFlags(): PxRevoluteJointFlags
}

external interface PxRevoluteJointFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxRevoluteJointFlags(flags: Int): PxRevoluteJointFlags {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxRevoluteJointFlags(flags)")
}

object PxRevoluteJointFlagEnum {
    val eLIMIT_ENABLED: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxRevoluteJointFlagEnum_eLIMIT_ENABLED()
    val eDRIVE_ENABLED: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_ENABLED()
    val eDRIVE_FREESPIN: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_FREESPIN()
}