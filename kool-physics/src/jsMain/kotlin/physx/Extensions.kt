/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxDefaultAllocator

fun PxDefaultAllocator(): PxDefaultAllocator {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxDefaultAllocator()")
}

fun PxDefaultAllocator.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDefaultCpuDispatcher : PxCpuDispatcher

fun PxDefaultCpuDispatcher.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxJoint : PxBase

external interface PxRevoluteJoint : PxJoint {
    /**
     * @param velocity WebIDL type: float
     */
    fun setDriveVelocity(velocity: Float)

    /**
     * @param velocity WebIDL type: float
     * @param autowake WebIDL type: boolean
     */
    fun setDriveVelocity(velocity: Float, autowake: Boolean)

    /**
     * @return WebIDL type: float
     */
    fun getDriveVelocity(): Float

    /**
     * @param limit WebIDL type: float
     */
    fun setDriveForceLimit(limit: Float)

    /**
     * @return WebIDL type: float
     */
    fun getDriveForceLimit(): Float

    /**
     * @param ratio WebIDL type: float
     */
    fun setDriveGearRatio(ratio: Float)

    /**
     * @return WebIDL type: float
     */
    fun getDriveGearRatio(): Float

    /**
     * @param flags WebIDL type: [PxRevoluteJointFlags] (Ref)
     */
    fun setRevoluteJointFlags(flags: PxRevoluteJointFlags)

    /**
     * @param flag  WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setRevoluteJointFlag(flag: Int, value: Boolean)

    /**
     * @return WebIDL type: [PxRevoluteJointFlags] (Value)
     */
    fun getRevoluteJointFlags(): PxRevoluteJointFlags

}

fun PxRevoluteJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxRevoluteJoint.driveVelocity
    get() = getDriveVelocity()
    set(value) { setDriveVelocity(value) }
var PxRevoluteJoint.driveForceLimit
    get() = getDriveForceLimit()
    set(value) { setDriveForceLimit(value) }
var PxRevoluteJoint.driveGearRatio
    get() = getDriveGearRatio()
    set(value) { setDriveGearRatio(value) }
var PxRevoluteJoint.revoluteJointFlags
    get() = getRevoluteJointFlags()
    set(value) { setRevoluteJointFlags(value) }

external interface PxRevoluteJointFlags {
    /**
     * @param flag WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxRevoluteJointFlags(flags: Short): PxRevoluteJointFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxRevoluteJointFlags(flags)")
}

fun PxRevoluteJointFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

object PxRevoluteJointFlagEnum {
    val eLIMIT_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRevoluteJointFlagEnum_eLIMIT_ENABLED()
    val eDRIVE_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_ENABLED()
    val eDRIVE_FREESPIN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_FREESPIN()
}

