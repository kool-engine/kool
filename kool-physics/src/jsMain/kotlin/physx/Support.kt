/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface SupportFunctions {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param actor WebIDL type: [PxRigidActor] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: [PxShape]
     */
    fun PxActor_getShape(actor: PxRigidActor, index: Int): PxShape

    /**
     * @param pairHeader WebIDL type: [PxContactPairHeader] (Ref)
     * @param index      WebIDL type: long
     * @return WebIDL type: [PxActor]
     */
    fun PxContactPairHeader_getActor(pairHeader: PxContactPairHeader, index: Int): PxActor

    /**
     * @param scene WebIDL type: [PxScene]
     * @return WebIDL type: [Vector_PxActorPtr] (Ref)
     */
    fun PxScene_getActiveActors(scene: PxScene): Vector_PxActorPtr

}

fun SupportFunctions.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxActorPtr

fun PxActorPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMaterialPtr

fun PxMaterialPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMaterialConstPtr

fun PxMaterialConstPtr.destroy() {
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

external interface PxU8Ptr : PxU8ConstPtr

fun PxU8Ptr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU8ConstPtr

fun PxU8ConstPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU16Ptr : PxU16ConstPtr

fun PxU16Ptr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU16ConstPtr

fun PxU16ConstPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU32Ptr : PxU32ConstPtr

fun PxU32Ptr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU32ConstPtr

fun PxU32ConstPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface TypeHelpers {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param base  WebIDL type: [PxU8ConstPtr] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: octet
     */
    fun getU8At(base: PxU8ConstPtr, index: Int): Byte

    /**
     * @param base  WebIDL type: [PxU16ConstPtr] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: unsigned short
     */
    fun getU16At(base: PxU16ConstPtr, index: Int): Short

    /**
     * @param base  WebIDL type: [PxU32ConstPtr] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: unsigned long
     */
    fun getU32At(base: PxU32ConstPtr, index: Int): Int

    /**
     * @param base  WebIDL type: [PxRealPtr] (Ref)
     * @param index WebIDL type: long
     * @return WebIDL type: float
     */
    fun getRealAt(base: PxRealPtr, index: Int): Float

    /**
     * @param base  WebIDL type: [PxActor]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxActor]
     */
    fun getActorAt(base: PxActor, index: Int): PxActor

    /**
     * @param base  WebIDL type: [PxContactPair]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxContactPair]
     */
    fun getContactPairAt(base: PxContactPair, index: Int): PxContactPair

    /**
     * @param base  WebIDL type: [PxContactPairHeader]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxContactPairHeader]
     */
    fun getContactPairHeaderAt(base: PxContactPairHeader, index: Int): PxContactPairHeader

    /**
     * @param base  WebIDL type: [PxController]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxController]
     */
    fun getControllerAt(base: PxController, index: Int): PxController

    /**
     * @param base  WebIDL type: [PxObstacle]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxObstacle]
     */
    fun getObstacleAt(base: PxObstacle, index: Int): PxObstacle

    /**
     * @param base  WebIDL type: [PxShape]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxShape]
     */
    fun getShapeAt(base: PxShape, index: Int): PxShape

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

    /**
     * @param baseJoint WebIDL type: [PxArticulationJointBase]
     * @return WebIDL type: [PxArticulationJoint]
     */
    fun articulationBaseJointToJoint(baseJoint: PxArticulationJointBase): PxArticulationJoint

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: any
     */
    fun voidToAny(voidPtr: Any): Int

}

fun TypeHelpers.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxMaterialConst {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxMaterial] (Const)
     */
    fun at(index: Int): PxMaterial

    /**
     * @return WebIDL type: [PxMaterialConstPtr]
     */
    fun data(): PxMaterialConstPtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxMaterial] (Const)
     */
    fun push_back(value: PxMaterial)

    fun clear()

}

fun Vector_PxMaterialConst(): Vector_PxMaterialConst {
    fun _Vector_PxMaterialConst(_module: dynamic) = js("new _module.Vector_PxMaterialConst()")
    return _Vector_PxMaterialConst(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxMaterialConst(size: Int): Vector_PxMaterialConst {
    fun _Vector_PxMaterialConst(_module: dynamic, size: Int) = js("new _module.Vector_PxMaterialConst(size)")
    return _Vector_PxMaterialConst(PhysXJsLoader.physXJs, size)
}

fun Vector_PxMaterialConst.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxHeightFieldSample {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxHeightFieldSample] (Ref)
     */
    fun at(index: Int): PxHeightFieldSample

    /**
     * @return WebIDL type: [PxHeightFieldSample]
     */
    fun data(): PxHeightFieldSample

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxHeightFieldSample] (Ref)
     */
    fun push_back(value: PxHeightFieldSample)

    fun clear()

}

fun Vector_PxHeightFieldSample(): Vector_PxHeightFieldSample {
    fun _Vector_PxHeightFieldSample(_module: dynamic) = js("new _module.Vector_PxHeightFieldSample()")
    return _Vector_PxHeightFieldSample(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxHeightFieldSample(size: Int): Vector_PxHeightFieldSample {
    fun _Vector_PxHeightFieldSample(_module: dynamic, size: Int) = js("new _module.Vector_PxHeightFieldSample(size)")
    return _Vector_PxHeightFieldSample(PhysXJsLoader.physXJs, size)
}

fun Vector_PxHeightFieldSample.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxReal {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxReal(): Vector_PxReal {
    fun _Vector_PxReal(_module: dynamic) = js("new _module.Vector_PxReal()")
    return _Vector_PxReal(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxReal(size: Int): Vector_PxReal {
    fun _Vector_PxReal(_module: dynamic, size: Int) = js("new _module.Vector_PxReal(size)")
    return _Vector_PxReal(PhysXJsLoader.physXJs, size)
}

fun Vector_PxReal.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxU8 {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: octet
     */
    fun at(index: Int): Byte

    /**
     * @return WebIDL type: VoidPtr
     */
    fun data(): Any

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: octet
     */
    fun push_back(value: Byte)

    fun clear()

}

fun Vector_PxU8(): Vector_PxU8 {
    fun _Vector_PxU8(_module: dynamic) = js("new _module.Vector_PxU8()")
    return _Vector_PxU8(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxU8(size: Int): Vector_PxU8 {
    fun _Vector_PxU8(_module: dynamic, size: Int) = js("new _module.Vector_PxU8(size)")
    return _Vector_PxU8(PhysXJsLoader.physXJs, size)
}

fun Vector_PxU8.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxU16 {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxU16(): Vector_PxU16 {
    fun _Vector_PxU16(_module: dynamic) = js("new _module.Vector_PxU16()")
    return _Vector_PxU16(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxU16(size: Int): Vector_PxU16 {
    fun _Vector_PxU16(_module: dynamic, size: Int) = js("new _module.Vector_PxU16(size)")
    return _Vector_PxU16(PhysXJsLoader.physXJs, size)
}

fun Vector_PxU16.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxU32 {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxU32(): Vector_PxU32 {
    fun _Vector_PxU32(_module: dynamic) = js("new _module.Vector_PxU32()")
    return _Vector_PxU32(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxU32(size: Int): Vector_PxU32 {
    fun _Vector_PxU32(_module: dynamic, size: Int) = js("new _module.Vector_PxU32(size)")
    return _Vector_PxU32(PhysXJsLoader.physXJs, size)
}

fun Vector_PxU32.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxVec3 {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxVec3(): Vector_PxVec3 {
    fun _Vector_PxVec3(_module: dynamic) = js("new _module.Vector_PxVec3()")
    return _Vector_PxVec3(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxVec3(size: Int): Vector_PxVec3 {
    fun _Vector_PxVec3(_module: dynamic, size: Int) = js("new _module.Vector_PxVec3(size)")
    return _Vector_PxVec3(PhysXJsLoader.physXJs, size)
}

fun Vector_PxVec3.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxActorPtr {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxActor]
     */
    fun at(index: Int): PxActor

    /**
     * @return WebIDL type: [PxActorPtr]
     */
    fun data(): PxActorPtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxActor]
     */
    fun push_back(value: PxActor)

    fun clear()

}

fun Vector_PxActorPtr(): Vector_PxActorPtr {
    fun _Vector_PxActorPtr(_module: dynamic) = js("new _module.Vector_PxActorPtr()")
    return _Vector_PxActorPtr(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxActorPtr(size: Int): Vector_PxActorPtr {
    fun _Vector_PxActorPtr(_module: dynamic, size: Int) = js("new _module.Vector_PxActorPtr(size)")
    return _Vector_PxActorPtr(PhysXJsLoader.physXJs, size)
}

fun Vector_PxActorPtr.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxContactPairPoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxContactPairPoint] (Ref)
     */
    fun at(index: Int): PxContactPairPoint

    /**
     * @return WebIDL type: [PxContactPairPoint]
     */
    fun data(): PxContactPairPoint

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxContactPairPoint] (Ref)
     */
    fun push_back(value: PxContactPairPoint)

    fun clear()

}

fun Vector_PxContactPairPoint(): Vector_PxContactPairPoint {
    fun _Vector_PxContactPairPoint(_module: dynamic) = js("new _module.Vector_PxContactPairPoint()")
    return _Vector_PxContactPairPoint(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxContactPairPoint(size: Int): Vector_PxContactPairPoint {
    fun _Vector_PxContactPairPoint(_module: dynamic, size: Int) = js("new _module.Vector_PxContactPairPoint(size)")
    return _Vector_PxContactPairPoint(PhysXJsLoader.physXJs, size)
}

fun Vector_PxContactPairPoint.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxRaycastQueryResult {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxRaycastQueryResult(): Vector_PxRaycastQueryResult {
    fun _Vector_PxRaycastQueryResult(_module: dynamic) = js("new _module.Vector_PxRaycastQueryResult()")
    return _Vector_PxRaycastQueryResult(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxRaycastQueryResult(size: Int): Vector_PxRaycastQueryResult {
    fun _Vector_PxRaycastQueryResult(_module: dynamic, size: Int) = js("new _module.Vector_PxRaycastQueryResult(size)")
    return _Vector_PxRaycastQueryResult(PhysXJsLoader.physXJs, size)
}

fun Vector_PxRaycastQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxSweepQueryResult {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxSweepQueryResult(): Vector_PxSweepQueryResult {
    fun _Vector_PxSweepQueryResult(_module: dynamic) = js("new _module.Vector_PxSweepQueryResult()")
    return _Vector_PxSweepQueryResult(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxSweepQueryResult(size: Int): Vector_PxSweepQueryResult {
    fun _Vector_PxSweepQueryResult(_module: dynamic, size: Int) = js("new _module.Vector_PxSweepQueryResult(size)")
    return _Vector_PxSweepQueryResult(PhysXJsLoader.physXJs, size)
}

fun Vector_PxSweepQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxRaycastHit {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxRaycastHit(): Vector_PxRaycastHit {
    fun _Vector_PxRaycastHit(_module: dynamic) = js("new _module.Vector_PxRaycastHit()")
    return _Vector_PxRaycastHit(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxRaycastHit(size: Int): Vector_PxRaycastHit {
    fun _Vector_PxRaycastHit(_module: dynamic, size: Int) = js("new _module.Vector_PxRaycastHit(size)")
    return _Vector_PxRaycastHit(PhysXJsLoader.physXJs, size)
}

fun Vector_PxRaycastHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxSweepHit {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxSweepHit(): Vector_PxSweepHit {
    fun _Vector_PxSweepHit(_module: dynamic) = js("new _module.Vector_PxSweepHit()")
    return _Vector_PxSweepHit(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxSweepHit(size: Int): Vector_PxSweepHit {
    fun _Vector_PxSweepHit(_module: dynamic, size: Int) = js("new _module.Vector_PxSweepHit(size)")
    return _Vector_PxSweepHit(PhysXJsLoader.physXJs, size)
}

fun Vector_PxSweepHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxVehicleDrivableSurfaceType {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxVehicleDrivableSurfaceType(): Vector_PxVehicleDrivableSurfaceType {
    fun _Vector_PxVehicleDrivableSurfaceType(_module: dynamic) = js("new _module.Vector_PxVehicleDrivableSurfaceType()")
    return _Vector_PxVehicleDrivableSurfaceType(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxVehicleDrivableSurfaceType(size: Int): Vector_PxVehicleDrivableSurfaceType {
    fun _Vector_PxVehicleDrivableSurfaceType(_module: dynamic, size: Int) = js("new _module.Vector_PxVehicleDrivableSurfaceType(size)")
    return _Vector_PxVehicleDrivableSurfaceType(PhysXJsLoader.physXJs, size)
}

fun Vector_PxVehicleDrivableSurfaceType.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxWheelQueryResult {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxWheelQueryResult(): Vector_PxWheelQueryResult {
    fun _Vector_PxWheelQueryResult(_module: dynamic) = js("new _module.Vector_PxWheelQueryResult()")
    return _Vector_PxWheelQueryResult(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxWheelQueryResult(size: Int): Vector_PxWheelQueryResult {
    fun _Vector_PxWheelQueryResult(_module: dynamic, size: Int) = js("new _module.Vector_PxWheelQueryResult(size)")
    return _Vector_PxWheelQueryResult(PhysXJsLoader.physXJs, size)
}

fun Vector_PxWheelQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Vector_PxVehicleWheels {
    /**
     * Native object address.
     */
    val ptr: Int

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

    fun clear()

}

fun Vector_PxVehicleWheels(): Vector_PxVehicleWheels {
    fun _Vector_PxVehicleWheels(_module: dynamic) = js("new _module.Vector_PxVehicleWheels()")
    return _Vector_PxVehicleWheels(PhysXJsLoader.physXJs)
}

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxVehicleWheels(size: Int): Vector_PxVehicleWheels {
    fun _Vector_PxVehicleWheels(_module: dynamic, size: Int) = js("new _module.Vector_PxVehicleWheels(size)")
    return _Vector_PxVehicleWheels(PhysXJsLoader.physXJs, size)
}

fun Vector_PxVehicleWheels.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPvdTransport {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun connect(): Boolean

    fun disconnect()

    /**
     * @return WebIDL type: boolean
     */
    fun isConnected(): Boolean

}

external interface SimplePvdTransport : PxPvdTransport {
    /**
     * @return WebIDL type: boolean
     */
    override fun connect(): Boolean

    /**
     * @param inBytes  WebIDL type: any
     * @param inLength WebIDL type: unsigned long
     */
    fun send(inBytes: Int, inLength: Int)

}

fun SimplePvdTransport.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface JSPvdTransport : SimplePvdTransport {
    /**
     * return WebIDL type: boolean
     */
    var connect: () -> Boolean

    /**
     * param inBytes  WebIDL type: any
     * param inLength WebIDL type: unsigned long
     */
    var send: (inBytes: Int, inLength: Int) -> Unit

}

fun JSPvdTransport(): JSPvdTransport {
    fun _JSPvdTransport(_module: dynamic) = js("new _module.JSPvdTransport()")
    return _JSPvdTransport(PhysXJsLoader.physXJs)
}

external interface PxPvdInstrumentationFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxPvdInstrumentationFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxPvdInstrumentationFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxPvdInstrumentationFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxPvdInstrumentationFlags(flags: Byte): PxPvdInstrumentationFlags {
    fun _PxPvdInstrumentationFlags(_module: dynamic, flags: Byte) = js("new _module.PxPvdInstrumentationFlags(flags)")
    return _PxPvdInstrumentationFlags(PhysXJsLoader.physXJs, flags)
}

fun PxPvdInstrumentationFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPvd {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param transport WebIDL type: [PxPvdTransport] (Ref)
     * @param flags     WebIDL type: [PxPvdInstrumentationFlags] (Ref)
     * @return WebIDL type: boolean
     */
    fun connect(transport: PxPvdTransport, flags: PxPvdInstrumentationFlags): Boolean

}

external interface PassThroughFilterShader : PxSimulationFilterShader {
    /**
     * WebIDL type: unsigned long
     */
    var outputPairFlags: Int

    /**
     * @param attributes0   WebIDL type: unsigned long
     * @param filterData0w0 WebIDL type: unsigned long
     * @param filterData0w1 WebIDL type: unsigned long
     * @param filterData0w2 WebIDL type: unsigned long
     * @param filterData0w3 WebIDL type: unsigned long
     * @param attributes1   WebIDL type: unsigned long
     * @param filterData1w0 WebIDL type: unsigned long
     * @param filterData1w1 WebIDL type: unsigned long
     * @param filterData1w2 WebIDL type: unsigned long
     * @param filterData1w3 WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun filterShader(attributes0: Int, filterData0w0: Int, filterData0w1: Int, filterData0w2: Int, filterData0w3: Int, attributes1: Int, filterData1w0: Int, filterData1w1: Int, filterData1w2: Int, filterData1w3: Int): Int

}

fun PassThroughFilterShader.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface JavaPassThroughFilterShader : PassThroughFilterShader {
    /**
     * param attributes0   WebIDL type: unsigned long
     * param filterData0w0 WebIDL type: unsigned long
     * param filterData0w1 WebIDL type: unsigned long
     * param filterData0w2 WebIDL type: unsigned long
     * param filterData0w3 WebIDL type: unsigned long
     * param attributes1   WebIDL type: unsigned long
     * param filterData1w0 WebIDL type: unsigned long
     * param filterData1w1 WebIDL type: unsigned long
     * param filterData1w2 WebIDL type: unsigned long
     * param filterData1w3 WebIDL type: unsigned long
     * return WebIDL type: unsigned long
     */
    var filterShader: (attributes0: Int, filterData0w0: Int, filterData0w1: Int, filterData0w2: Int, filterData0w3: Int, attributes1: Int, filterData1w0: Int, filterData1w1: Int, filterData1w2: Int, filterData1w3: Int) -> Int

}

fun JavaPassThroughFilterShader(): JavaPassThroughFilterShader {
    fun _JavaPassThroughFilterShader(_module: dynamic) = js("new _module.JavaPassThroughFilterShader()")
    return _JavaPassThroughFilterShader(PhysXJsLoader.physXJs)
}

object PxPvdInstrumentationFlagEnum {
    val eDEBUG: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPvdInstrumentationFlagEnum_eDEBUG()
    val ePROFILE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPvdInstrumentationFlagEnum_ePROFILE()
    val eMEMORY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPvdInstrumentationFlagEnum_eMEMORY()
    val eALL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPvdInstrumentationFlagEnum_eALL()
}

