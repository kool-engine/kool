/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxBase {
    fun release()
    fun getConcreteTypeName(): String
    fun getConcreteType(): Int
    fun setBaseFlag(flag: Int, value: Boolean)
    fun setBaseFlags(inFlags: PxBaseFlags)
    fun getBaseFlags(): PxBaseFlags
    fun isReleasable(): Boolean
}

external interface PxBaseFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxBaseFlags(flags: Short): PxBaseFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxBaseFlags(flags)")
}

external interface PxBaseTask

external interface PxBoundedData {
    var count: Int
    var stride: Int
    var data: Any
}

external interface PxBounds3 {
    var minimum: PxVec3
    var maximum: PxVec3

    fun setEmpty()
    fun setMaximal()
    fun include(v: PxVec3)
    fun isEmpty(): Boolean
    fun intersects(b: PxBounds3): Boolean
    fun intersects1D(b: PxBounds3, axis: Int): Boolean
    fun contains(v: PxVec3): Boolean
    fun isInside(box: PxBounds3): Boolean
    fun getCenter(): PxVec3
    fun getDimensions(): PxVec3
    fun getExtents(): PxVec3
    fun scaleSafe(scale: Float)
    fun scaleFast(scale: Float)
    fun fattenSafe(distance: Float)
    fun fattenFast(distance: Float)
    fun isFinite(): Boolean
    fun isValid(): Boolean
}
fun PxBounds3(): PxBounds3 {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxBounds3()")
}
fun PxBounds3(minimum: PxVec3, maximum: PxVec3): PxBounds3 {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxBounds3(minimum, maximum)")
}

external interface PxCpuDispatcher

external interface PxDefaultErrorCallback
fun PxDefaultErrorCallback(): PxDefaultErrorCallback {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxDefaultErrorCallback()")
}

external interface PxFoundation {
    fun release()
}

external interface PxPhysicsInsertionCallback

external interface PxQuat {
    var x: Float
    var y: Float
    var z: Float
    var w: Float
}
fun PxQuat(): PxQuat {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxQuat()")
}
fun PxQuat(x: Float, y: Float, z: Float, w: Float): PxQuat {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxQuat(x, y, z, w)")
}

external interface PxTolerancesScale
fun PxTolerancesScale(): PxTolerancesScale {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxTolerancesScale()")
}

external interface PxTransform {
    var q: PxQuat
    var p: PxVec3
}
fun PxTransform(r: Int): PxTransform {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxTransform(r)")
}
fun PxTransform(p0: PxVec3, q0: PxQuat): PxTransform {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxTransform(p0, q0)")
}

external interface PxRealPtr

external interface PxU8Ptr

external interface PxVec3 {
    var x: Float
    var y: Float
    var z: Float
}
fun PxVec3(): PxVec3 {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVec3()")
}
fun PxVec3(x: Float, y: Float, z: Float): PxVec3 {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVec3(x, y, z)")
}

object PxBaseFlagEnum {
    val eOWNS_MEMORY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBaseFlagEnum_eOWNS_MEMORY()
    val eIS_RELEASABLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBaseFlagEnum_eIS_RELEASABLE()
}

object PxIDENTITYEnum {
    val PxIdentity: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxIDENTITYEnum_PxIdentity()
}