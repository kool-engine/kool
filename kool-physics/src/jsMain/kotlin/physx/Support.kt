/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxActorPtr

fun PxActorPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMaterialPtr

fun PxMaterialPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleWheelsPtr

fun PxVehicleWheelsPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRealPtr

fun PxRealPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU8Ptr

fun PxU8Ptr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU16Ptr

fun PxU16Ptr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU32Ptr

fun PxU32Ptr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface TypeHelpers {
    /**
     * @param base  WebIDL type: [PxU8Ptr] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: octet
     */
    fun getU8At(base: PxU8Ptr, index: Int): Byte

    /**
     * @param base  WebIDL type: [PxU16Ptr] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: unsigned short
     */
    fun getU16At(base: PxU16Ptr, index: Int): Short

    /**
     * @param base  WebIDL type: [PxU32Ptr] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: unsigned long
     */
    fun getU32At(base: PxU32Ptr, index: Int): Int

    /**
     * @param base  WebIDL type: [PxRealPtr] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: float
     */
    fun getRealAt(base: PxRealPtr, index: Int): Float

    /**
     * @param base  WebIDL type: [PxContactPair]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxContactPair]
     */
    fun getContactPairAt(base: PxContactPair, index: Int): PxContactPair

    /**
     * @param base  WebIDL type: [PxTriggerPair]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxTriggerPair]
     */
    fun getTriggerPairAt(base: PxTriggerPair, index: Int): PxTriggerPair

    /**
     * @param base  WebIDL type: [PxVec3]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxVec3]
     */
    fun getVec3At(base: PxVec3, index: Int): PxVec3

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxU8Ptr] (Value)
     */
    fun voidToU8Ptr(voidPtr: Any): PxU8Ptr

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxU16Ptr] (Value)
     */
    fun voidToU16Ptr(voidPtr: Any): PxU16Ptr

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxU32Ptr] (Value)
     */
    fun voidToU32Ptr(voidPtr: Any): PxU32Ptr

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxRealPtr] (Value)
     */
    fun voidToRealPtr(voidPtr: Any): PxRealPtr

}

fun TypeHelpers.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxMaterial {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxMaterial] (Const)
     */
    fun at(index: Int): PxMaterial

    /**
     * @return WebIDL type: [PxMaterialPtr]
     */
    fun data(): PxMaterialPtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxMaterial]
     */
    fun push_back(value: PxMaterial)

}

fun Vector_PxMaterial(): Vector_PxMaterial {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxMaterial()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxMaterial(size: Int): Vector_PxMaterial {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxMaterial(size)")
}

fun Vector_PxMaterial.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxReal {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun at(index: Int): Float

    /**
     * @return WebIDL type: VoidPtr
     */
    fun data(): Any

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: float
     */
    fun push_back(value: Float)

}

fun Vector_PxReal(): Vector_PxReal {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxReal()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxReal(size: Int): Vector_PxReal {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxReal(size)")
}

fun Vector_PxReal.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxU16 {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: unsigned short
     */
    fun at(index: Int): Short

    /**
     * @return WebIDL type: VoidPtr
     */
    fun data(): Any

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: unsigned short
     */
    fun push_back(value: Short)

}

fun Vector_PxU16(): Vector_PxU16 {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxU16()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxU16(size: Int): Vector_PxU16 {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxU16(size)")
}

fun Vector_PxU16.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxU32 {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun at(index: Int): Int

    /**
     * @return WebIDL type: VoidPtr
     */
    fun data(): Any

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: unsigned long
     */
    fun push_back(value: Int)

}

fun Vector_PxU32(): Vector_PxU32 {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxU32()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxU32(size: Int): Vector_PxU32 {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxU32(size)")
}

fun Vector_PxU32.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxVec3 {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxVec3] (Ref)
     */
    fun at(index: Int): PxVec3

    /**
     * @return WebIDL type: [PxVec3]
     */
    fun data(): PxVec3

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxVec3] (Ref)
     */
    fun push_back(value: PxVec3)

}

fun Vector_PxVec3(): Vector_PxVec3 {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxVec3()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxVec3(size: Int): Vector_PxVec3 {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxVec3(size)")
}

fun Vector_PxVec3.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxRaycastQueryResult {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxRaycastQueryResult] (Ref)
     */
    fun at(index: Int): PxRaycastQueryResult

    /**
     * @return WebIDL type: [PxRaycastQueryResult]
     */
    fun data(): PxRaycastQueryResult

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxRaycastQueryResult] (Ref)
     */
    fun push_back(value: PxRaycastQueryResult)

}

fun Vector_PxRaycastQueryResult(): Vector_PxRaycastQueryResult {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxRaycastQueryResult()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxRaycastQueryResult(size: Int): Vector_PxRaycastQueryResult {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxRaycastQueryResult(size)")
}

fun Vector_PxRaycastQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxSweepQueryResult {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxSweepQueryResult] (Ref)
     */
    fun at(index: Int): PxSweepQueryResult

    /**
     * @return WebIDL type: [PxSweepQueryResult]
     */
    fun data(): PxSweepQueryResult

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxSweepQueryResult] (Ref)
     */
    fun push_back(value: PxSweepQueryResult)

}

fun Vector_PxSweepQueryResult(): Vector_PxSweepQueryResult {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxSweepQueryResult()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxSweepQueryResult(size: Int): Vector_PxSweepQueryResult {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxSweepQueryResult(size)")
}

fun Vector_PxSweepQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxRaycastHit {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxRaycastHit] (Ref)
     */
    fun at(index: Int): PxRaycastHit

    /**
     * @return WebIDL type: [PxRaycastHit]
     */
    fun data(): PxRaycastHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxRaycastHit] (Ref)
     */
    fun push_back(value: PxRaycastHit)

}

fun Vector_PxRaycastHit(): Vector_PxRaycastHit {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxRaycastHit()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxRaycastHit(size: Int): Vector_PxRaycastHit {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxRaycastHit(size)")
}

fun Vector_PxRaycastHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxSweepHit {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxSweepHit] (Ref)
     */
    fun at(index: Int): PxSweepHit

    /**
     * @return WebIDL type: [PxSweepHit]
     */
    fun data(): PxSweepHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxSweepHit] (Ref)
     */
    fun push_back(value: PxSweepHit)

}

fun Vector_PxSweepHit(): Vector_PxSweepHit {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxSweepHit()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxSweepHit(size: Int): Vector_PxSweepHit {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxSweepHit(size)")
}

fun Vector_PxSweepHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxVehicleDrivableSurfaceType {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleDrivableSurfaceType] (Ref)
     */
    fun at(index: Int): PxVehicleDrivableSurfaceType

    /**
     * @return WebIDL type: [PxVehicleDrivableSurfaceType]
     */
    fun data(): PxVehicleDrivableSurfaceType

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxVehicleDrivableSurfaceType] (Ref)
     */
    fun push_back(value: PxVehicleDrivableSurfaceType)

}

fun Vector_PxVehicleDrivableSurfaceType(): Vector_PxVehicleDrivableSurfaceType {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxVehicleDrivableSurfaceType()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxVehicleDrivableSurfaceType(size: Int): Vector_PxVehicleDrivableSurfaceType {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxVehicleDrivableSurfaceType(size)")
}

fun Vector_PxVehicleDrivableSurfaceType.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxWheelQueryResult {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxWheelQueryResult] (Ref)
     */
    fun at(index: Int): PxWheelQueryResult

    /**
     * @return WebIDL type: [PxWheelQueryResult]
     */
    fun data(): PxWheelQueryResult

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxWheelQueryResult] (Ref)
     */
    fun push_back(value: PxWheelQueryResult)

}

fun Vector_PxWheelQueryResult(): Vector_PxWheelQueryResult {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxWheelQueryResult()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxWheelQueryResult(size: Int): Vector_PxWheelQueryResult {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxWheelQueryResult(size)")
}

fun Vector_PxWheelQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxVehicleWheels {
    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleWheels]
     */
    fun at(index: Int): PxVehicleWheels

    /**
     * @return WebIDL type: [PxVehicleWheelsPtr]
     */
    fun data(): PxVehicleWheelsPtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxVehicleWheels]
     */
    fun push_back(value: PxVehicleWheels)

}

fun Vector_PxVehicleWheels(): Vector_PxVehicleWheels {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxVehicleWheels()")
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxVehicleWheels(size: Int): Vector_PxVehicleWheels {
    val module = PhysXJsLoader.physXJs
    return js("new module.Vector_PxVehicleWheels(size)")
}

fun Vector_PxVehicleWheels.destroy() {
    PhysXJsLoader.destroy(this)
}

