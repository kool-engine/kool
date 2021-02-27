/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxBase {
    fun release()

    /**
     * @return WebIDL type: DOMString (Const)
     */
    fun getConcreteTypeName(): String

    /**
     * @return WebIDL type: long
     */
    fun getConcreteType(): Int

    /**
     * @param flag  WebIDL type: [PxBaseFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setBaseFlag(flag: Int, value: Boolean)

    /**
     * @param inFlags WebIDL type: [PxBaseFlags] (Ref)
     */
    fun setBaseFlags(inFlags: PxBaseFlags)

    /**
     * @return WebIDL type: [PxBaseFlags] (Value)
     */
    fun getBaseFlags(): PxBaseFlags

    /**
     * @return WebIDL type: boolean
     */
    fun isReleasable(): Boolean

}

val PxBase.concreteTypeName
    get() = getConcreteTypeName()
val PxBase.concreteType
    get() = getConcreteType()

var PxBase.baseFlags
    get() = getBaseFlags()
    set(value) { setBaseFlags(value) }

external interface PxBaseFlags {
    /**
     * @param flag WebIDL type: [PxBaseFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxBaseFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxBaseFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxBaseFlags(flags: Short): PxBaseFlags {
    fun _PxBaseFlags(_module: dynamic, flags: Short) = js("new _module.PxBaseFlags(flags)")
    return _PxBaseFlags(PhysXJsLoader.physXJs, flags)
}

fun PxBaseFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBaseTask

fun PxBaseTask.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBoundedData {
    /**
     * WebIDL type: unsigned long
     */
    var count: Int
    /**
     * WebIDL type: unsigned long
     */
    var stride: Int
    /**
     * WebIDL type: VoidPtr (Const)
     */
    var data: Any
}

fun PxBoundedData(): PxBoundedData {
    fun _PxBoundedData(_module: dynamic) = js("new _module.PxBoundedData()")
    return _PxBoundedData(PhysXJsLoader.physXJs)
}

fun PxBoundedData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBounds3 {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var minimum: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var maximum: PxVec3

    fun setEmpty()

    fun setMaximal()

    /**
     * @param v WebIDL type: [PxVec3] (Const, Ref)
     */
    fun include(v: PxVec3)

    /**
     * @return WebIDL type: boolean
     */
    fun isEmpty(): Boolean

    /**
     * @param b WebIDL type: [PxBounds3] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun intersects(b: PxBounds3): Boolean

    /**
     * @param b    WebIDL type: [PxBounds3] (Const, Ref)
     * @param axis WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun intersects1D(b: PxBounds3, axis: Int): Boolean

    /**
     * @param v WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun contains(v: PxVec3): Boolean

    /**
     * @param box WebIDL type: [PxBounds3] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isInside(box: PxBounds3): Boolean

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getCenter(): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getDimensions(): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getExtents(): PxVec3

    /**
     * @param scale WebIDL type: float
     */
    fun scaleSafe(scale: Float)

    /**
     * @param scale WebIDL type: float
     */
    fun scaleFast(scale: Float)

    /**
     * @param distance WebIDL type: float
     */
    fun fattenSafe(distance: Float)

    /**
     * @param distance WebIDL type: float
     */
    fun fattenFast(distance: Float)

    /**
     * @return WebIDL type: boolean
     */
    fun isFinite(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxBounds3(): PxBounds3 {
    fun _PxBounds3(_module: dynamic) = js("new _module.PxBounds3()")
    return _PxBounds3(PhysXJsLoader.physXJs)
}

/**
 * @param minimum WebIDL type: [PxVec3] (Const, Ref)
 * @param maximum WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxBounds3(minimum: PxVec3, maximum: PxVec3): PxBounds3 {
    fun _PxBounds3(_module: dynamic, minimum: PxVec3, maximum: PxVec3) = js("new _module.PxBounds3(minimum, maximum)")
    return _PxBounds3(PhysXJsLoader.physXJs, minimum, maximum)
}

fun PxBounds3.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxBounds3.center
    get() = getCenter()
val PxBounds3.dimensions
    get() = getDimensions()
val PxBounds3.extents
    get() = getExtents()

external interface PxCpuDispatcher

fun PxCpuDispatcher.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDefaultErrorCallback : PxErrorCallback

fun PxDefaultErrorCallback(): PxDefaultErrorCallback {
    fun _PxDefaultErrorCallback(_module: dynamic) = js("new _module.PxDefaultErrorCallback()")
    return _PxDefaultErrorCallback(PhysXJsLoader.physXJs)
}

fun PxDefaultErrorCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxErrorCallback {
    /**
     * @param code    WebIDL type: [PxErrorCodeEnum] (enum)
     * @param message WebIDL type: DOMString (Const)
     * @param file    WebIDL type: DOMString (Const)
     * @param line    WebIDL type: long
     */
    fun reportError(code: Int, message: String, file: String, line: Int)

}

fun PxErrorCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface JavaErrorCallback : PxErrorCallback {
    /**
     * param code    WebIDL type: [PxErrorCodeEnum] (enum)
     * param message WebIDL type: DOMString (Const)
     * param file    WebIDL type: DOMString (Const)
     * param line    WebIDL type: long
     */
    var reportError: (code: Int, message: String, file: String, line: Int) -> Unit

}

fun JavaErrorCallback(): JavaErrorCallback {
    fun _JavaErrorCallback(_module: dynamic) = js("new _module.JavaErrorCallback()")
    return _JavaErrorCallback(PhysXJsLoader.physXJs)
}

external interface PxFoundation {
    fun release()

}

external interface PxPhysicsInsertionCallback

external interface PxQuat {
    /**
     * WebIDL type: float
     */
    var x: Float
    /**
     * WebIDL type: float
     */
    var y: Float
    /**
     * WebIDL type: float
     */
    var z: Float
    /**
     * WebIDL type: float
     */
    var w: Float
}

fun PxQuat(): PxQuat {
    fun _PxQuat(_module: dynamic) = js("new _module.PxQuat()")
    return _PxQuat(PhysXJsLoader.physXJs)
}

/**
 * @param x WebIDL type: float
 * @param y WebIDL type: float
 * @param z WebIDL type: float
 * @param w WebIDL type: float
 */
fun PxQuat(x: Float, y: Float, z: Float, w: Float): PxQuat {
    fun _PxQuat(_module: dynamic, x: Float, y: Float, z: Float, w: Float) = js("new _module.PxQuat(x, y, z, w)")
    return _PxQuat(PhysXJsLoader.physXJs, x, y, z, w)
}

fun PxQuat.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTolerancesScale

fun PxTolerancesScale(): PxTolerancesScale {
    fun _PxTolerancesScale(_module: dynamic) = js("new _module.PxTolerancesScale()")
    return _PxTolerancesScale(PhysXJsLoader.physXJs)
}

fun PxTolerancesScale.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTransform {
    /**
     * WebIDL type: [PxQuat] (Value)
     */
    var q: PxQuat
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var p: PxVec3
}

/**
 * @param r WebIDL type: [PxIDENTITYEnum] (enum)
 */
fun PxTransform(r: Int): PxTransform {
    fun _PxTransform(_module: dynamic, r: Int) = js("new _module.PxTransform(r)")
    return _PxTransform(PhysXJsLoader.physXJs, r)
}

/**
 * @param p0 WebIDL type: [PxVec3] (Const, Ref)
 * @param q0 WebIDL type: [PxQuat] (Const, Ref)
 */
fun PxTransform(p0: PxVec3, q0: PxQuat): PxTransform {
    fun _PxTransform(_module: dynamic, p0: PxVec3, q0: PxQuat) = js("new _module.PxTransform(p0, q0)")
    return _PxTransform(PhysXJsLoader.physXJs, p0, q0)
}

fun PxTransform.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxStridedData {
    /**
     * WebIDL type: unsigned long
     */
    var stride: Int
    /**
     * WebIDL type: VoidPtr (Const)
     */
    var data: Any
}

fun PxStridedData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxU16StridedData {
    /**
     * WebIDL type: unsigned long
     */
    var stride: Int
    /**
     * WebIDL type: [PxU16ConstPtr] (Const, Value)
     */
    var data: PxU16ConstPtr
}

fun PxU16StridedData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVec3 {
    /**
     * WebIDL type: float
     */
    var x: Float
    /**
     * WebIDL type: float
     */
    var y: Float
    /**
     * WebIDL type: float
     */
    var z: Float
}

fun PxVec3(): PxVec3 {
    fun _PxVec3(_module: dynamic) = js("new _module.PxVec3()")
    return _PxVec3(PhysXJsLoader.physXJs)
}

/**
 * @param x WebIDL type: float
 * @param y WebIDL type: float
 * @param z WebIDL type: float
 */
fun PxVec3(x: Float, y: Float, z: Float): PxVec3 {
    fun _PxVec3(_module: dynamic, x: Float, y: Float, z: Float) = js("new _module.PxVec3(x, y, z)")
    return _PxVec3(PhysXJsLoader.physXJs, x, y, z)
}

fun PxVec3.destroy() {
    PhysXJsLoader.destroy(this)
}

object PxBaseFlagEnum {
    val eOWNS_MEMORY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBaseFlagEnum_eOWNS_MEMORY()
    val eIS_RELEASABLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBaseFlagEnum_eIS_RELEASABLE()
}

object PxErrorCodeEnum {
    val eNO_ERROR: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eNO_ERROR()
    val eDEBUG_INFO: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eDEBUG_INFO()
    val eDEBUG_WARNING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eDEBUG_WARNING()
    val eINVALID_PARAMETER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eINVALID_PARAMETER()
    val eINVALID_OPERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eINVALID_OPERATION()
    val eOUT_OF_MEMORY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eOUT_OF_MEMORY()
    val eINTERNAL_ERROR: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eINTERNAL_ERROR()
    val eABORT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eABORT()
    val ePERF_WARNING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_ePERF_WARNING()
    val eMASK_ALL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxErrorCodeEnum_eMASK_ALL()
}

object PxIDENTITYEnum {
    val PxIdentity: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxIDENTITYEnum_PxIdentity()
}

