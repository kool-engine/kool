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

external interface PxRealPtr

fun PxRealPtr.destroy() {
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
     * @param base  WebIDL type: [PxBounds3]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxBounds3]
     */
    fun getBounds3At(base: PxBounds3, index: Int): PxBounds3

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
     * @param base  WebIDL type: [PxControllerShapeHit]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxControllerShapeHit]
     */
    fun getControllerShapeHitAt(base: PxControllerShapeHit, index: Int): PxControllerShapeHit

    /**
     * @param base  WebIDL type: [PxControllersHit]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxControllersHit]
     */
    fun getControllersHitAt(base: PxControllersHit, index: Int): PxControllersHit

    /**
     * @param base  WebIDL type: [PxControllerObstacleHit]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxControllerObstacleHit]
     */
    fun getControllerObstacleHitAt(base: PxControllerObstacleHit, index: Int): PxControllerObstacleHit

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

external interface PassThroughFilterShaderImpl : PassThroughFilterShader {
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

fun PassThroughFilterShaderImpl(): PassThroughFilterShaderImpl {
    fun _PassThroughFilterShaderImpl(_module: dynamic) = js("new _module.PassThroughFilterShaderImpl()")
    return _PassThroughFilterShaderImpl(PhysXJsLoader.physXJs)
}

external interface PxPvd

object PxVisualizationParameterEnum {
    val eSCALE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eSCALE()
    val eWORLD_AXES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eWORLD_AXES()
    val eBODY_AXES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eBODY_AXES()
    val eBODY_MASS_AXES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eBODY_MASS_AXES()
    val eBODY_LIN_VELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eBODY_LIN_VELOCITY()
    val eBODY_ANG_VELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eBODY_ANG_VELOCITY()
    val eCONTACT_POINT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCONTACT_POINT()
    val eCONTACT_NORMAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCONTACT_NORMAL()
    val eCONTACT_ERROR: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCONTACT_ERROR()
    val eCONTACT_FORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCONTACT_FORCE()
    val eACTOR_AXES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eACTOR_AXES()
    val eCOLLISION_AABBS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_AABBS()
    val eCOLLISION_SHAPES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_SHAPES()
    val eCOLLISION_AXES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_AXES()
    val eCOLLISION_COMPOUNDS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_COMPOUNDS()
    val eCOLLISION_FNORMALS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_FNORMALS()
    val eCOLLISION_EDGES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_EDGES()
    val eCOLLISION_STATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_STATIC()
    val eCOLLISION_DYNAMIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_DYNAMIC()
    val eJOINT_LOCAL_FRAMES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eJOINT_LOCAL_FRAMES()
    val eJOINT_LIMITS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eJOINT_LIMITS()
    val eCULL_BOX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eCULL_BOX()
    val eMBP_REGIONS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eMBP_REGIONS()
    val eSIMULATION_MESH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eSIMULATION_MESH()
    val eSDF: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eSDF()
    val eNUM_VALUES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eNUM_VALUES()
    val eFORCE_DWORD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVisualizationParameterEnum_eFORCE_DWORD()
}

