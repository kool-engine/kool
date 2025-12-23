/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")

package physx

import kotlin.js.JsAny
import kotlin.js.js

external interface SupportFunctions : JsAny, DestroyableNative {
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
     * @param scene WebIDL type: [PxScene]
     * @return WebIDL type: [PxArray_PxActorPtr] (Ref)
     */
    fun PxScene_getActiveActors(scene: PxScene): PxArray_PxActorPtr

    /**
     * @param articulation WebIDL type: [PxArticulationReducedCoordinate]
     * @return WebIDL type: unsigned long
     */
    fun PxArticulationReducedCoordinate_getMinSolverPositionIterations(articulation: PxArticulationReducedCoordinate): Int

    /**
     * @param articulation WebIDL type: [PxArticulationReducedCoordinate]
     * @return WebIDL type: unsigned long
     */
    fun PxArticulationReducedCoordinate_getMinSolverVelocityIterations(articulation: PxArticulationReducedCoordinate): Int

}

fun SupportFunctionsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): SupportFunctions = js("_module.wrapPointer(ptr, _module.SupportFunctions)")

external interface PxU8Ptr : JsAny, DestroyableNative, PxU8ConstPtr

fun PxU8PtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxU8Ptr = js("_module.wrapPointer(ptr, _module.PxU8Ptr)")

external interface PxU8ConstPtr : JsAny, DestroyableNative

fun PxU8ConstPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxU8ConstPtr = js("_module.wrapPointer(ptr, _module.PxU8ConstPtr)")

external interface PxU16Ptr : JsAny, DestroyableNative, PxU16ConstPtr

fun PxU16PtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxU16Ptr = js("_module.wrapPointer(ptr, _module.PxU16Ptr)")

external interface PxU16ConstPtr : JsAny, DestroyableNative

fun PxU16ConstPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxU16ConstPtr = js("_module.wrapPointer(ptr, _module.PxU16ConstPtr)")

external interface PxU32Ptr : JsAny, DestroyableNative, PxU32ConstPtr

fun PxU32PtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxU32Ptr = js("_module.wrapPointer(ptr, _module.PxU32Ptr)")

external interface PxU32ConstPtr : JsAny, DestroyableNative

fun PxU32ConstPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxU32ConstPtr = js("_module.wrapPointer(ptr, _module.PxU32ConstPtr)")

external interface PxI32Ptr : JsAny, DestroyableNative, PxI32ConstPtr

fun PxI32PtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxI32Ptr = js("_module.wrapPointer(ptr, _module.PxI32Ptr)")

external interface PxI32ConstPtr : JsAny, DestroyableNative

fun PxI32ConstPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxI32ConstPtr = js("_module.wrapPointer(ptr, _module.PxI32ConstPtr)")

external interface PxRealPtr : JsAny, DestroyableNative, PxRealConstPtr

fun PxRealPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRealPtr = js("_module.wrapPointer(ptr, _module.PxRealPtr)")

external interface PxRealConstPtr : JsAny, DestroyableNative

fun PxRealConstPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRealConstPtr = js("_module.wrapPointer(ptr, _module.PxRealConstPtr)")

external interface PxActorPtr : JsAny, DestroyableNative

fun PxActorPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxActorPtr = js("_module.wrapPointer(ptr, _module.PxActorPtr)")

external interface PxMaterialPtr : JsAny, DestroyableNative

fun PxMaterialPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMaterialPtr = js("_module.wrapPointer(ptr, _module.PxMaterialPtr)")

external interface PxMaterialConstPtr : JsAny, DestroyableNative

fun PxMaterialConstPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMaterialConstPtr = js("_module.wrapPointer(ptr, _module.PxMaterialConstPtr)")

external interface PxShapePtr : JsAny, DestroyableNative

fun PxShapePtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxShapePtr = js("_module.wrapPointer(ptr, _module.PxShapePtr)")

external interface NativeArrayHelpers : JsAny, DestroyableNative {
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
     * @param base  WebIDL type: VoidPtr
     * @param index WebIDL type: long
     * @param value WebIDL type: octet
     */
    fun setU8At(base: JsAny, index: Int, value: Byte)

    /**
     * @param base  WebIDL type: VoidPtr
     * @param index WebIDL type: long
     * @param value WebIDL type: unsigned short
     */
    fun setU16At(base: JsAny, index: Int, value: Short)

    /**
     * @param base  WebIDL type: VoidPtr
     * @param index WebIDL type: long
     * @param value WebIDL type: unsigned long
     */
    fun setU32At(base: JsAny, index: Int, value: Int)

    /**
     * @param base  WebIDL type: VoidPtr
     * @param index WebIDL type: long
     * @param value WebIDL type: float
     */
    fun setRealAt(base: JsAny, index: Int, value: Float)

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxU8Ptr] (Value)
     */
    fun voidToU8Ptr(voidPtr: JsAny): PxU8Ptr

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxU16Ptr] (Value)
     */
    fun voidToU16Ptr(voidPtr: JsAny): PxU16Ptr

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxU32Ptr] (Value)
     */
    fun voidToU32Ptr(voidPtr: JsAny): PxU32Ptr

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxI32Ptr] (Value)
     */
    fun voidToI32Ptr(voidPtr: JsAny): PxI32Ptr

    /**
     * @param voidPtr WebIDL type: VoidPtr
     * @return WebIDL type: [PxRealPtr] (Value)
     */
    fun voidToRealPtr(voidPtr: JsAny): PxRealPtr

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
     * @param base  WebIDL type: [PxDebugPoint]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxDebugPoint]
     */
    fun getDebugPointAt(base: PxDebugPoint, index: Int): PxDebugPoint

    /**
     * @param base  WebIDL type: [PxDebugLine]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxDebugLine]
     */
    fun getDebugLineAt(base: PxDebugLine, index: Int): PxDebugLine

    /**
     * @param base  WebIDL type: [PxDebugTriangle]
     * @param index WebIDL type: long
     * @return WebIDL type: [PxDebugTriangle]
     */
    fun getDebugTriangleAt(base: PxDebugTriangle, index: Int): PxDebugTriangle

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

}

fun NativeArrayHelpersFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): NativeArrayHelpers = js("_module.wrapPointer(ptr, _module.NativeArrayHelpers)")

external interface PxArray_PxMaterialConst : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxMaterial] (Const)
     */
    fun get(index: Int): PxMaterial

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxMaterialConstPtr] (Const, Ref)
     */
    fun set(index: Int, value: PxMaterialConstPtr)

    /**
     * @return WebIDL type: [PxMaterialConstPtr]
     */
    fun begin(): PxMaterialConstPtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxMaterial] (Const)
     */
    fun pushBack(value: PxMaterial)

    fun clear()

}

fun PxArray_PxMaterialConst(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxMaterialConst = js("new _module.PxArray_PxMaterialConst()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxMaterialConst(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxMaterialConst = js("new _module.PxArray_PxMaterialConst(size)")

fun PxArray_PxMaterialConstFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxMaterialConst = js("_module.wrapPointer(ptr, _module.PxArray_PxMaterialConst)")

external interface PxArray_PxHeightFieldSample : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxHeightFieldSample] (Ref)
     */
    fun get(index: Int): PxHeightFieldSample

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxHeightFieldSample] (Const, Ref)
     */
    fun set(index: Int, value: PxHeightFieldSample)

    /**
     * @return WebIDL type: [PxHeightFieldSample]
     */
    fun begin(): PxHeightFieldSample

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxHeightFieldSample] (Ref)
     */
    fun pushBack(value: PxHeightFieldSample)

    fun clear()

}

fun PxArray_PxHeightFieldSample(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxHeightFieldSample = js("new _module.PxArray_PxHeightFieldSample()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxHeightFieldSample(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxHeightFieldSample = js("new _module.PxArray_PxHeightFieldSample(size)")

fun PxArray_PxHeightFieldSampleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxHeightFieldSample = js("_module.wrapPointer(ptr, _module.PxArray_PxHeightFieldSample)")

external interface PxArray_PxReal : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun get(index: Int): Float

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: float
     */
    fun set(index: Int, value: Float)

    /**
     * @return WebIDL type: VoidPtr
     */
    fun begin(): JsAny

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: float
     */
    fun pushBack(value: Float)

    fun clear()

}

fun PxArray_PxReal(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxReal = js("new _module.PxArray_PxReal()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxReal(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxReal = js("new _module.PxArray_PxReal(size)")

fun PxArray_PxRealFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxReal = js("_module.wrapPointer(ptr, _module.PxArray_PxReal)")

external interface PxArray_PxU8 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: octet
     */
    fun get(index: Int): Byte

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: octet
     */
    fun set(index: Int, value: Byte)

    /**
     * @return WebIDL type: VoidPtr
     */
    fun begin(): JsAny

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: octet
     */
    fun pushBack(value: Byte)

    /**
     * @param buffer WebIDL type: VoidPtr
     * @param size   WebIDL type: unsigned long
     */
    fun setFromBuffer(buffer: JsAny, size: Int)

    fun clear()

}

fun PxArray_PxU8(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU8 = js("new _module.PxArray_PxU8()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxU8(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU8 = js("new _module.PxArray_PxU8(size)")

fun PxArray_PxU8FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU8 = js("_module.wrapPointer(ptr, _module.PxArray_PxU8)")

external interface PxArray_PxU16 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: unsigned short
     */
    fun get(index: Int): Short

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: unsigned short
     */
    fun set(index: Int, value: Short)

    /**
     * @return WebIDL type: VoidPtr
     */
    fun begin(): JsAny

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: unsigned short
     */
    fun pushBack(value: Short)

    fun clear()

}

fun PxArray_PxU16(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU16 = js("new _module.PxArray_PxU16()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxU16(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU16 = js("new _module.PxArray_PxU16(size)")

fun PxArray_PxU16FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU16 = js("_module.wrapPointer(ptr, _module.PxArray_PxU16)")

external interface PxArray_PxU32 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun get(index: Int): Int

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: unsigned long
     */
    fun set(index: Int, value: Int)

    /**
     * @return WebIDL type: VoidPtr
     */
    fun begin(): JsAny

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: unsigned long
     */
    fun pushBack(value: Int)

    fun clear()

}

fun PxArray_PxU32(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU32 = js("new _module.PxArray_PxU32()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxU32(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU32 = js("new _module.PxArray_PxU32(size)")

fun PxArray_PxU32FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxU32 = js("_module.wrapPointer(ptr, _module.PxArray_PxU32)")

external interface PxArray_PxVec3 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxVec3] (Ref)
     */
    fun get(index: Int): PxVec3

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxVec3] (Const, Ref)
     */
    fun set(index: Int, value: PxVec3)

    /**
     * @return WebIDL type: [PxVec3]
     */
    fun begin(): PxVec3

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxVec3] (Ref)
     */
    fun pushBack(value: PxVec3)

    fun clear()

}

fun PxArray_PxVec3(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxVec3 = js("new _module.PxArray_PxVec3()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxVec3(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxVec3 = js("new _module.PxArray_PxVec3(size)")

fun PxArray_PxVec3FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxVec3 = js("_module.wrapPointer(ptr, _module.PxArray_PxVec3)")

external interface PxArray_PxVec4 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxVec4] (Ref)
     */
    fun get(index: Int): PxVec4

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxVec4] (Const, Ref)
     */
    fun set(index: Int, value: PxVec4)

    /**
     * @return WebIDL type: [PxVec4]
     */
    fun begin(): PxVec4

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxVec4] (Ref)
     */
    fun pushBack(value: PxVec4)

    fun clear()

}

fun PxArray_PxVec4(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxVec4 = js("new _module.PxArray_PxVec4()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxVec4(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxVec4 = js("new _module.PxArray_PxVec4(size)")

fun PxArray_PxVec4FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxVec4 = js("_module.wrapPointer(ptr, _module.PxArray_PxVec4)")

external interface PxArray_PxActorPtr : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxActor]
     */
    fun get(index: Int): PxActor

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxActorPtr] (Const, Ref)
     */
    fun set(index: Int, value: PxActorPtr)

    /**
     * @return WebIDL type: [PxActorPtr]
     */
    fun begin(): PxActorPtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxActor]
     */
    fun pushBack(value: PxActor)

    fun clear()

}

fun PxArray_PxActorPtr(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxActorPtr = js("new _module.PxArray_PxActorPtr()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxActorPtr(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxActorPtr = js("new _module.PxArray_PxActorPtr(size)")

fun PxArray_PxActorPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxActorPtr = js("_module.wrapPointer(ptr, _module.PxArray_PxActorPtr)")

external interface PxArray_PxContactPairPoint : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxContactPairPoint] (Ref)
     */
    fun get(index: Int): PxContactPairPoint

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxContactPairPoint] (Const, Ref)
     */
    fun set(index: Int, value: PxContactPairPoint)

    /**
     * @return WebIDL type: [PxContactPairPoint]
     */
    fun begin(): PxContactPairPoint

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxContactPairPoint] (Ref)
     */
    fun pushBack(value: PxContactPairPoint)

    fun clear()

}

fun PxArray_PxContactPairPoint(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxContactPairPoint = js("new _module.PxArray_PxContactPairPoint()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxContactPairPoint(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxContactPairPoint = js("new _module.PxArray_PxContactPairPoint(size)")

fun PxArray_PxContactPairPointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxContactPairPoint = js("_module.wrapPointer(ptr, _module.PxArray_PxContactPairPoint)")

external interface PxArray_PxRaycastHit : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxRaycastHit] (Ref)
     */
    fun get(index: Int): PxRaycastHit

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxRaycastHit] (Const, Ref)
     */
    fun set(index: Int, value: PxRaycastHit)

    /**
     * @return WebIDL type: [PxRaycastHit]
     */
    fun begin(): PxRaycastHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxRaycastHit] (Ref)
     */
    fun pushBack(value: PxRaycastHit)

    fun clear()

}

fun PxArray_PxRaycastHit(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxRaycastHit = js("new _module.PxArray_PxRaycastHit()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxRaycastHit(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxRaycastHit = js("new _module.PxArray_PxRaycastHit(size)")

fun PxArray_PxRaycastHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxRaycastHit = js("_module.wrapPointer(ptr, _module.PxArray_PxRaycastHit)")

external interface PxArray_PxSweepHit : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxSweepHit] (Ref)
     */
    fun get(index: Int): PxSweepHit

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxSweepHit] (Const, Ref)
     */
    fun set(index: Int, value: PxSweepHit)

    /**
     * @return WebIDL type: [PxSweepHit]
     */
    fun begin(): PxSweepHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxSweepHit] (Ref)
     */
    fun pushBack(value: PxSweepHit)

    fun clear()

}

fun PxArray_PxSweepHit(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxSweepHit = js("new _module.PxArray_PxSweepHit()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxSweepHit(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxSweepHit = js("new _module.PxArray_PxSweepHit(size)")

fun PxArray_PxSweepHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxSweepHit = js("_module.wrapPointer(ptr, _module.PxArray_PxSweepHit)")

external interface Vector_PxMaterialConst : JsAny, DestroyableNative {
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

fun Vector_PxMaterialConst(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxMaterialConst = js("new _module.Vector_PxMaterialConst()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxMaterialConst(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxMaterialConst = js("new _module.Vector_PxMaterialConst(size)")

fun Vector_PxMaterialConstFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxMaterialConst = js("_module.wrapPointer(ptr, _module.Vector_PxMaterialConst)")

external interface Vector_PxHeightFieldSample : JsAny, DestroyableNative {
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

fun Vector_PxHeightFieldSample(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxHeightFieldSample = js("new _module.Vector_PxHeightFieldSample()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxHeightFieldSample(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxHeightFieldSample = js("new _module.Vector_PxHeightFieldSample(size)")

fun Vector_PxHeightFieldSampleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxHeightFieldSample = js("_module.wrapPointer(ptr, _module.Vector_PxHeightFieldSample)")

external interface PxArray_PxShapePtr : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxShape]
     */
    fun get(index: Int): PxShape

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [PxShapePtr] (Const, Ref)
     */
    fun set(index: Int, value: PxShapePtr)

    /**
     * @return WebIDL type: [PxShapePtr]
     */
    fun begin(): PxShapePtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxShape]
     */
    fun pushBack(value: PxShape)

    fun clear()

}

fun PxArray_PxShapePtr(_module: JsAny = PhysXJsLoader.physXJs): PxArray_PxShapePtr = js("new _module.PxArray_PxShapePtr()")

/**
 * @param size WebIDL type: unsigned long
 */
fun PxArray_PxShapePtr(size: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxShapePtr = js("new _module.PxArray_PxShapePtr(size)")

fun PxArray_PxShapePtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArray_PxShapePtr = js("_module.wrapPointer(ptr, _module.PxArray_PxShapePtr)")

external interface Vector_PxReal : JsAny, DestroyableNative {
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
    fun data(): JsAny

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

fun Vector_PxReal(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxReal = js("new _module.Vector_PxReal()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxReal(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxReal = js("new _module.Vector_PxReal(size)")

fun Vector_PxRealFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxReal = js("_module.wrapPointer(ptr, _module.Vector_PxReal)")

external interface Vector_PxU8 : JsAny, DestroyableNative {
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
    fun data(): JsAny

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

fun Vector_PxU8(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxU8 = js("new _module.Vector_PxU8()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxU8(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxU8 = js("new _module.Vector_PxU8(size)")

fun Vector_PxU8FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxU8 = js("_module.wrapPointer(ptr, _module.Vector_PxU8)")

external interface Vector_PxU16 : JsAny, DestroyableNative {
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
    fun data(): JsAny

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

fun Vector_PxU16(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxU16 = js("new _module.Vector_PxU16()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxU16(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxU16 = js("new _module.Vector_PxU16(size)")

fun Vector_PxU16FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxU16 = js("_module.wrapPointer(ptr, _module.Vector_PxU16)")

external interface Vector_PxU32 : JsAny, DestroyableNative {
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
    fun data(): JsAny

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

fun Vector_PxU32(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxU32 = js("new _module.Vector_PxU32()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxU32(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxU32 = js("new _module.Vector_PxU32(size)")

fun Vector_PxU32FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxU32 = js("_module.wrapPointer(ptr, _module.Vector_PxU32)")

external interface Vector_PxVec3 : JsAny, DestroyableNative {
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

fun Vector_PxVec3(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxVec3 = js("new _module.Vector_PxVec3()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxVec3(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxVec3 = js("new _module.Vector_PxVec3(size)")

fun Vector_PxVec3FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxVec3 = js("_module.wrapPointer(ptr, _module.Vector_PxVec3)")

external interface Vector_PxVec4 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxVec4] (Ref)
     */
    fun at(index: Int): PxVec4

    /**
     * @return WebIDL type: [PxVec4]
     */
    fun data(): PxVec4

    /**
     * @return WebIDL type: unsigned long
     */
    fun size(): Int

    /**
     * @param value WebIDL type: [PxVec4] (Ref)
     */
    fun push_back(value: PxVec4)

    fun clear()

}

fun Vector_PxVec4(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxVec4 = js("new _module.Vector_PxVec4()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxVec4(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxVec4 = js("new _module.Vector_PxVec4(size)")

fun Vector_PxVec4FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxVec4 = js("_module.wrapPointer(ptr, _module.Vector_PxVec4)")

external interface Vector_PxActorPtr : JsAny, DestroyableNative {
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

fun Vector_PxActorPtr(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxActorPtr = js("new _module.Vector_PxActorPtr()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxActorPtr(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxActorPtr = js("new _module.Vector_PxActorPtr(size)")

fun Vector_PxActorPtrFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxActorPtr = js("_module.wrapPointer(ptr, _module.Vector_PxActorPtr)")

external interface Vector_PxContactPairPoint : JsAny, DestroyableNative {
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

fun Vector_PxContactPairPoint(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxContactPairPoint = js("new _module.Vector_PxContactPairPoint()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxContactPairPoint(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxContactPairPoint = js("new _module.Vector_PxContactPairPoint(size)")

fun Vector_PxContactPairPointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxContactPairPoint = js("_module.wrapPointer(ptr, _module.Vector_PxContactPairPoint)")

external interface Vector_PxRaycastHit : JsAny, DestroyableNative {
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

fun Vector_PxRaycastHit(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxRaycastHit = js("new _module.Vector_PxRaycastHit()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxRaycastHit(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxRaycastHit = js("new _module.Vector_PxRaycastHit(size)")

fun Vector_PxRaycastHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxRaycastHit = js("_module.wrapPointer(ptr, _module.Vector_PxRaycastHit)")

external interface Vector_PxSweepHit : JsAny, DestroyableNative {
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

fun Vector_PxSweepHit(_module: JsAny = PhysXJsLoader.physXJs): Vector_PxSweepHit = js("new _module.Vector_PxSweepHit()")

/**
 * @param size WebIDL type: unsigned long
 */
fun Vector_PxSweepHit(size: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxSweepHit = js("new _module.Vector_PxSweepHit(size)")

fun Vector_PxSweepHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Vector_PxSweepHit = js("_module.wrapPointer(ptr, _module.Vector_PxSweepHit)")

external interface PassThroughFilterShader : JsAny, DestroyableNative, PxSimulationFilterShader {
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

fun PassThroughFilterShaderFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PassThroughFilterShader = js("_module.wrapPointer(ptr, _module.PassThroughFilterShader)")

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

fun PassThroughFilterShaderImpl(_module: JsAny = PhysXJsLoader.physXJs): PassThroughFilterShaderImpl = js("new _module.PassThroughFilterShaderImpl()")

external interface PxPvd : JsAny {
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

    fun release()

}

fun PxPvdFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPvd = js("_module.wrapPointer(ptr, _module.PxPvd)")

external interface PxPvdTransport : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun connect(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isConnected(): Boolean

    fun disconnect()

    fun release()

    fun flush()

}

fun PxPvdTransportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPvdTransport = js("_module.wrapPointer(ptr, _module.PxPvdTransport)")

external interface SimplePvdTransport : JsAny, DestroyableNative, PxPvdTransport {
    /**
     * @param inBytes  WebIDL type: any
     * @param inLength WebIDL type: unsigned long
     */
    fun send(inBytes: Int, inLength: Int)

}

fun SimplePvdTransportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): SimplePvdTransport = js("_module.wrapPointer(ptr, _module.SimplePvdTransport)")

external interface SimplPvdTransportImpl : SimplePvdTransport {
    /**
     * return WebIDL type: boolean
     */
    var connect: () -> Boolean

    /**
     * return WebIDL type: boolean
     */
    var isConnected: () -> Boolean

    var disconnect: () -> Unit

    /**
     * param inBytes  WebIDL type: any
     * param inLength WebIDL type: unsigned long
     */
    var send: (inBytes: Int, inLength: Int) -> Unit

    var flush: () -> Unit

}

fun SimplPvdTransportImpl(_module: JsAny = PhysXJsLoader.physXJs): SimplPvdTransportImpl = js("new _module.SimplPvdTransportImpl()")

external interface PxPvdInstrumentationFlags : JsAny, DestroyableNative {
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
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxPvdInstrumentationFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxPvdInstrumentationFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxPvdInstrumentationFlags = js("new _module.PxPvdInstrumentationFlags(flags)")

fun PxPvdInstrumentationFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPvdInstrumentationFlags = js("_module.wrapPointer(ptr, _module.PxPvdInstrumentationFlags)")

fun PxPvdInstrumentationFlags.isSet(flag: PxPvdInstrumentationFlagEnum) = isSet(flag.value)
fun PxPvdInstrumentationFlags.raise(flag: PxPvdInstrumentationFlagEnum) = raise(flag.value)
fun PxPvdInstrumentationFlags.clear(flag: PxPvdInstrumentationFlagEnum) = clear(flag.value)

external interface PxPvdSceneClient : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag  WebIDL type: [PxPvdSceneFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setScenePvdFlag(flag: Int, value: Boolean)

    /**
     * @param flags WebIDL type: [PxPvdSceneFlags] (Ref)
     */
    fun setScenePvdFlags(flags: PxPvdSceneFlags)

    /**
     * @return WebIDL type: [PxPvdSceneFlags] (Value)
     */
    fun getScenePvdFlags(): PxPvdSceneFlags

    /**
     * @param name   WebIDL type: DOMString
     * @param origin WebIDL type: [PxVec3] (Const, Ref)
     * @param up     WebIDL type: [PxVec3] (Const, Ref)
     * @param target WebIDL type: [PxVec3] (Const, Ref)
     */
    fun updateCamera(name: String, origin: PxVec3, up: PxVec3, target: PxVec3)

}

fun PxPvdSceneClientFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPvdSceneClient = js("_module.wrapPointer(ptr, _module.PxPvdSceneClient)")

var PxPvdSceneClient.scenePvdFlags
    get() = getScenePvdFlags()
    set(value) { setScenePvdFlags(value) }

fun PxPvdSceneClient.setScenePvdFlag(flag: PxPvdSceneFlagEnum, value: Boolean) = setScenePvdFlag(flag.value, value)

external interface PxPvdSceneFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxPvdSceneFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxPvdSceneFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxPvdSceneFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxPvdSceneFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxPvdSceneFlags = js("new _module.PxPvdSceneFlags(flags)")

fun PxPvdSceneFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPvdSceneFlags = js("_module.wrapPointer(ptr, _module.PxPvdSceneFlags)")

fun PxPvdSceneFlags.isSet(flag: PxPvdSceneFlagEnum) = isSet(flag.value)
fun PxPvdSceneFlags.raise(flag: PxPvdSceneFlagEnum) = raise(flag.value)
fun PxPvdSceneFlags.clear(flag: PxPvdSceneFlagEnum) = clear(flag.value)

external interface PxOmniPvd : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun startSampling(): Boolean

    fun release()

}

fun PxOmniPvdFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxOmniPvd = js("_module.wrapPointer(ptr, _module.PxOmniPvd)")

value class PxVisualizationParameterEnum private constructor(val value: Int) {
    companion object {
        val eSCALE: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eSCALE(PhysXJsLoader.physXJs))
        val eWORLD_AXES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eWORLD_AXES(PhysXJsLoader.physXJs))
        val eBODY_AXES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eBODY_AXES(PhysXJsLoader.physXJs))
        val eBODY_MASS_AXES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eBODY_MASS_AXES(PhysXJsLoader.physXJs))
        val eBODY_LIN_VELOCITY: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eBODY_LIN_VELOCITY(PhysXJsLoader.physXJs))
        val eBODY_ANG_VELOCITY: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eBODY_ANG_VELOCITY(PhysXJsLoader.physXJs))
        val eCONTACT_POINT: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCONTACT_POINT(PhysXJsLoader.physXJs))
        val eCONTACT_NORMAL: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCONTACT_NORMAL(PhysXJsLoader.physXJs))
        val eCONTACT_ERROR: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCONTACT_ERROR(PhysXJsLoader.physXJs))
        val eCONTACT_FORCE: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCONTACT_FORCE(PhysXJsLoader.physXJs))
        val eACTOR_AXES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eACTOR_AXES(PhysXJsLoader.physXJs))
        val eCOLLISION_AABBS: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCOLLISION_AABBS(PhysXJsLoader.physXJs))
        val eCOLLISION_SHAPES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCOLLISION_SHAPES(PhysXJsLoader.physXJs))
        val eCOLLISION_AXES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCOLLISION_AXES(PhysXJsLoader.physXJs))
        val eCOLLISION_COMPOUNDS: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCOLLISION_COMPOUNDS(PhysXJsLoader.physXJs))
        val eCOLLISION_FNORMALS: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCOLLISION_FNORMALS(PhysXJsLoader.physXJs))
        val eCOLLISION_EDGES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCOLLISION_EDGES(PhysXJsLoader.physXJs))
        val eCOLLISION_STATIC: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCOLLISION_STATIC(PhysXJsLoader.physXJs))
        val eCOLLISION_DYNAMIC: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCOLLISION_DYNAMIC(PhysXJsLoader.physXJs))
        val eJOINT_LOCAL_FRAMES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eJOINT_LOCAL_FRAMES(PhysXJsLoader.physXJs))
        val eJOINT_LIMITS: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eJOINT_LIMITS(PhysXJsLoader.physXJs))
        val eCULL_BOX: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eCULL_BOX(PhysXJsLoader.physXJs))
        val eMBP_REGIONS: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eMBP_REGIONS(PhysXJsLoader.physXJs))
        val eSIMULATION_MESH: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eSIMULATION_MESH(PhysXJsLoader.physXJs))
        val eSDF: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eSDF(PhysXJsLoader.physXJs))
        val eNUM_VALUES: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eNUM_VALUES(PhysXJsLoader.physXJs))
        val eFORCE_DWORD: PxVisualizationParameterEnum = PxVisualizationParameterEnum(PxVisualizationParameterEnum_eFORCE_DWORD(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eSCALE.value -> eSCALE
            eWORLD_AXES.value -> eWORLD_AXES
            eBODY_AXES.value -> eBODY_AXES
            eBODY_MASS_AXES.value -> eBODY_MASS_AXES
            eBODY_LIN_VELOCITY.value -> eBODY_LIN_VELOCITY
            eBODY_ANG_VELOCITY.value -> eBODY_ANG_VELOCITY
            eCONTACT_POINT.value -> eCONTACT_POINT
            eCONTACT_NORMAL.value -> eCONTACT_NORMAL
            eCONTACT_ERROR.value -> eCONTACT_ERROR
            eCONTACT_FORCE.value -> eCONTACT_FORCE
            eACTOR_AXES.value -> eACTOR_AXES
            eCOLLISION_AABBS.value -> eCOLLISION_AABBS
            eCOLLISION_SHAPES.value -> eCOLLISION_SHAPES
            eCOLLISION_AXES.value -> eCOLLISION_AXES
            eCOLLISION_COMPOUNDS.value -> eCOLLISION_COMPOUNDS
            eCOLLISION_FNORMALS.value -> eCOLLISION_FNORMALS
            eCOLLISION_EDGES.value -> eCOLLISION_EDGES
            eCOLLISION_STATIC.value -> eCOLLISION_STATIC
            eCOLLISION_DYNAMIC.value -> eCOLLISION_DYNAMIC
            eJOINT_LOCAL_FRAMES.value -> eJOINT_LOCAL_FRAMES
            eJOINT_LIMITS.value -> eJOINT_LIMITS
            eCULL_BOX.value -> eCULL_BOX
            eMBP_REGIONS.value -> eMBP_REGIONS
            eSIMULATION_MESH.value -> eSIMULATION_MESH
            eSDF.value -> eSDF
            eNUM_VALUES.value -> eNUM_VALUES
            eFORCE_DWORD.value -> eFORCE_DWORD
            else -> error("Invalid enum value $value for enum PxVisualizationParameterEnum")
        }
    }
}

private fun PxVisualizationParameterEnum_eSCALE(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eSCALE()")
private fun PxVisualizationParameterEnum_eWORLD_AXES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eWORLD_AXES()")
private fun PxVisualizationParameterEnum_eBODY_AXES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eBODY_AXES()")
private fun PxVisualizationParameterEnum_eBODY_MASS_AXES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eBODY_MASS_AXES()")
private fun PxVisualizationParameterEnum_eBODY_LIN_VELOCITY(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eBODY_LIN_VELOCITY()")
private fun PxVisualizationParameterEnum_eBODY_ANG_VELOCITY(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eBODY_ANG_VELOCITY()")
private fun PxVisualizationParameterEnum_eCONTACT_POINT(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCONTACT_POINT()")
private fun PxVisualizationParameterEnum_eCONTACT_NORMAL(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCONTACT_NORMAL()")
private fun PxVisualizationParameterEnum_eCONTACT_ERROR(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCONTACT_ERROR()")
private fun PxVisualizationParameterEnum_eCONTACT_FORCE(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCONTACT_FORCE()")
private fun PxVisualizationParameterEnum_eACTOR_AXES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eACTOR_AXES()")
private fun PxVisualizationParameterEnum_eCOLLISION_AABBS(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_AABBS()")
private fun PxVisualizationParameterEnum_eCOLLISION_SHAPES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_SHAPES()")
private fun PxVisualizationParameterEnum_eCOLLISION_AXES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_AXES()")
private fun PxVisualizationParameterEnum_eCOLLISION_COMPOUNDS(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_COMPOUNDS()")
private fun PxVisualizationParameterEnum_eCOLLISION_FNORMALS(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_FNORMALS()")
private fun PxVisualizationParameterEnum_eCOLLISION_EDGES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_EDGES()")
private fun PxVisualizationParameterEnum_eCOLLISION_STATIC(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_STATIC()")
private fun PxVisualizationParameterEnum_eCOLLISION_DYNAMIC(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCOLLISION_DYNAMIC()")
private fun PxVisualizationParameterEnum_eJOINT_LOCAL_FRAMES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eJOINT_LOCAL_FRAMES()")
private fun PxVisualizationParameterEnum_eJOINT_LIMITS(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eJOINT_LIMITS()")
private fun PxVisualizationParameterEnum_eCULL_BOX(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eCULL_BOX()")
private fun PxVisualizationParameterEnum_eMBP_REGIONS(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eMBP_REGIONS()")
private fun PxVisualizationParameterEnum_eSIMULATION_MESH(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eSIMULATION_MESH()")
private fun PxVisualizationParameterEnum_eSDF(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eSDF()")
private fun PxVisualizationParameterEnum_eNUM_VALUES(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eNUM_VALUES()")
private fun PxVisualizationParameterEnum_eFORCE_DWORD(module: JsAny): Int = js("module._emscripten_enum_PxVisualizationParameterEnum_eFORCE_DWORD()")

value class PxPvdInstrumentationFlagEnum private constructor(val value: Int) {
    companion object {
        val eDEBUG: PxPvdInstrumentationFlagEnum = PxPvdInstrumentationFlagEnum(PxPvdInstrumentationFlagEnum_eDEBUG(PhysXJsLoader.physXJs))
        val ePROFILE: PxPvdInstrumentationFlagEnum = PxPvdInstrumentationFlagEnum(PxPvdInstrumentationFlagEnum_ePROFILE(PhysXJsLoader.physXJs))
        val eMEMORY: PxPvdInstrumentationFlagEnum = PxPvdInstrumentationFlagEnum(PxPvdInstrumentationFlagEnum_eMEMORY(PhysXJsLoader.physXJs))
        val eALL: PxPvdInstrumentationFlagEnum = PxPvdInstrumentationFlagEnum(PxPvdInstrumentationFlagEnum_eALL(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eDEBUG.value -> eDEBUG
            ePROFILE.value -> ePROFILE
            eMEMORY.value -> eMEMORY
            eALL.value -> eALL
            else -> error("Invalid enum value $value for enum PxPvdInstrumentationFlagEnum")
        }
    }
}

private fun PxPvdInstrumentationFlagEnum_eDEBUG(module: JsAny): Int = js("module._emscripten_enum_PxPvdInstrumentationFlagEnum_eDEBUG()")
private fun PxPvdInstrumentationFlagEnum_ePROFILE(module: JsAny): Int = js("module._emscripten_enum_PxPvdInstrumentationFlagEnum_ePROFILE()")
private fun PxPvdInstrumentationFlagEnum_eMEMORY(module: JsAny): Int = js("module._emscripten_enum_PxPvdInstrumentationFlagEnum_eMEMORY()")
private fun PxPvdInstrumentationFlagEnum_eALL(module: JsAny): Int = js("module._emscripten_enum_PxPvdInstrumentationFlagEnum_eALL()")

value class PxPvdSceneFlagEnum private constructor(val value: Int) {
    companion object {
        val eTRANSMIT_CONTACTS: PxPvdSceneFlagEnum = PxPvdSceneFlagEnum(PxPvdSceneFlagEnum_eTRANSMIT_CONTACTS(PhysXJsLoader.physXJs))
        val eTRANSMIT_SCENEQUERIES: PxPvdSceneFlagEnum = PxPvdSceneFlagEnum(PxPvdSceneFlagEnum_eTRANSMIT_SCENEQUERIES(PhysXJsLoader.physXJs))
        val eTRANSMIT_CONSTRAINTS: PxPvdSceneFlagEnum = PxPvdSceneFlagEnum(PxPvdSceneFlagEnum_eTRANSMIT_CONSTRAINTS(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eTRANSMIT_CONTACTS.value -> eTRANSMIT_CONTACTS
            eTRANSMIT_SCENEQUERIES.value -> eTRANSMIT_SCENEQUERIES
            eTRANSMIT_CONSTRAINTS.value -> eTRANSMIT_CONSTRAINTS
            else -> error("Invalid enum value $value for enum PxPvdSceneFlagEnum")
        }
    }
}

private fun PxPvdSceneFlagEnum_eTRANSMIT_CONTACTS(module: JsAny): Int = js("module._emscripten_enum_PxPvdSceneFlagEnum_eTRANSMIT_CONTACTS()")
private fun PxPvdSceneFlagEnum_eTRANSMIT_SCENEQUERIES(module: JsAny): Int = js("module._emscripten_enum_PxPvdSceneFlagEnum_eTRANSMIT_SCENEQUERIES()")
private fun PxPvdSceneFlagEnum_eTRANSMIT_CONSTRAINTS(module: JsAny): Int = js("module._emscripten_enum_PxPvdSceneFlagEnum_eTRANSMIT_CONSTRAINTS()")

