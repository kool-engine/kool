/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_PARAMETER", "unused")

package physx

external interface PxDebugPoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var pos: PxVec3
    /**
     * WebIDL type: unsigned long
     */
    var color: Int
}

fun PxDebugPointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDebugPoint = js("_module.wrapPointer(ptr, _module.PxDebugPoint)")

external interface PxDebugLine {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var pos0: PxVec3
    /**
     * WebIDL type: unsigned long
     */
    var color0: Int
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var pos1: PxVec3
    /**
     * WebIDL type: unsigned long
     */
    var color1: Int
}

fun PxDebugLineFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDebugLine = js("_module.wrapPointer(ptr, _module.PxDebugLine)")

external interface PxDebugTriangle {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var pos0: PxVec3
    /**
     * WebIDL type: unsigned long
     */
    var color0: Int
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var pos1: PxVec3
    /**
     * WebIDL type: unsigned long
     */
    var color1: Int
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var pos2: PxVec3
    /**
     * WebIDL type: unsigned long
     */
    var color2: Int
}

fun PxDebugTriangleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDebugTriangle = js("_module.wrapPointer(ptr, _module.PxDebugTriangle)")

external interface PxRenderBuffer {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbPoints(): Int

    /**
     * @return WebIDL type: [PxDebugPoint] (Const)
     */
    fun getPoints(): PxDebugPoint

    /**
     * @param point WebIDL type: [PxDebugPoint] (Const, Ref)
     */
    fun addPoint(point: PxDebugPoint)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbLines(): Int

    /**
     * @return WebIDL type: [PxDebugLine] (Const)
     */
    fun getLines(): PxDebugLine

    /**
     * @param line WebIDL type: [PxDebugLine] (Const, Ref)
     */
    fun addLine(line: PxDebugLine)

    /**
     * @param nbLines WebIDL type: unsigned long (Const)
     * @return WebIDL type: [PxDebugLine]
     */
    fun reserveLines(nbLines: Int): PxDebugLine

    /**
     * @param nbLines WebIDL type: unsigned long (Const)
     * @return WebIDL type: [PxDebugPoint]
     */
    fun reservePoints(nbLines: Int): PxDebugPoint

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTriangles(): Int

    /**
     * @return WebIDL type: [PxDebugTriangle] (Const)
     */
    fun getTriangles(): PxDebugTriangle

    /**
     * @param triangle WebIDL type: [PxDebugTriangle] (Const, Ref)
     */
    fun addTriangle(triangle: PxDebugTriangle)

    /**
     * @param other WebIDL type: [PxRenderBuffer] (Const, Ref)
     */
    fun append(other: PxRenderBuffer)

    fun clear()

    /**
     * @param delta WebIDL type: [PxVec3] (Const, Ref)
     */
    fun shift(delta: PxVec3)

    /**
     * @return WebIDL type: boolean
     */
    fun empty(): Boolean

}

fun PxRenderBufferFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRenderBuffer = js("_module.wrapPointer(ptr, _module.PxRenderBuffer)")

val PxRenderBuffer.nbPoints
    get() = getNbPoints()
val PxRenderBuffer.points
    get() = getPoints()
val PxRenderBuffer.nbLines
    get() = getNbLines()
val PxRenderBuffer.lines
    get() = getLines()
val PxRenderBuffer.nbTriangles
    get() = getNbTriangles()
val PxRenderBuffer.triangles
    get() = getTriangles()

external interface PxBase {
    /**
     * Native object address.
     */
    val ptr: Int

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

fun PxBaseFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBase = js("_module.wrapPointer(ptr, _module.PxBase)")

val PxBase.concreteTypeName
    get() = getConcreteTypeName()
val PxBase.concreteType
    get() = getConcreteType()

var PxBase.baseFlags
    get() = getBaseFlags()
    set(value) { setBaseFlags(value) }

external interface PxBaseFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxBaseFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxBaseFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxBaseFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxBaseFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxBaseFlags = js("new _module.PxBaseFlags(flags)")

fun PxBaseFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBaseFlags = js("_module.wrapPointer(ptr, _module.PxBaseFlags)")

fun PxBaseFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBaseTask

fun PxBaseTaskFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBaseTask = js("_module.wrapPointer(ptr, _module.PxBaseTask)")

fun PxBaseTask.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBoundedData : PxStridedData {
    /**
     * WebIDL type: unsigned long
     */
    var count: Int
}

fun PxBoundedData(_module: dynamic = PhysXJsLoader.physXJs): PxBoundedData = js("new _module.PxBoundedData()")

fun PxBoundedDataFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBoundedData = js("_module.wrapPointer(ptr, _module.PxBoundedData)")

fun PxBoundedData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBounds3 {
    /**
     * Native object address.
     */
    val ptr: Int

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

fun PxBounds3(_module: dynamic = PhysXJsLoader.physXJs): PxBounds3 = js("new _module.PxBounds3()")

/**
 * @param minimum WebIDL type: [PxVec3] (Const, Ref)
 * @param maximum WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxBounds3(minimum: PxVec3, maximum: PxVec3, _module: dynamic = PhysXJsLoader.physXJs): PxBounds3 = js("new _module.PxBounds3(minimum, maximum)")

fun PxBounds3FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBounds3 = js("_module.wrapPointer(ptr, _module.PxBounds3)")

fun PxBounds3.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxBounds3.center
    get() = getCenter()
val PxBounds3.dimensions
    get() = getDimensions()
val PxBounds3.extents
    get() = getExtents()

external interface PxCollection {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param obj WebIDL type: [PxBase] (Ref)
     */
    fun add(obj: PxBase)

    /**
     * @param obj WebIDL type: [PxBase] (Ref)
     * @param id  WebIDL type: unsigned long long
     */
    fun add(obj: PxBase, id: Long)

    /**
     * @param obj WebIDL type: [PxBase] (Ref)
     */
    fun remove(obj: PxBase)

    /**
     * @param obj WebIDL type: [PxBase] (Ref)
     * @return WebIDL type: boolean
     */
    fun contains(obj: PxBase): Boolean

    /**
     * @param obj WebIDL type: [PxBase] (Ref)
     * @param id  WebIDL type: unsigned long long
     */
    fun addId(obj: PxBase, id: Long)

    /**
     * @param id WebIDL type: unsigned long long
     */
    fun removeId(id: Long)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbObjects(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxBase] (Ref)
     */
    fun getObject(index: Int): PxBase

    /**
     * @param id WebIDL type: unsigned long long
     * @return WebIDL type: [PxBase]
     */
    fun find(id: Long): PxBase

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbIds(): Int

    /**
     * @param obj WebIDL type: [PxBase] (Const, Ref)
     * @return WebIDL type: unsigned long long
     */
    fun getId(obj: PxBase): Long

    fun release()

}

fun PxCollectionFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxCollection = js("_module.wrapPointer(ptr, _module.PxCollection)")

val PxCollection.nbObjects
    get() = getNbObjects()
val PxCollection.nbIds
    get() = getNbIds()

external interface PxCpuDispatcher

fun PxCpuDispatcherFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxCpuDispatcher = js("_module.wrapPointer(ptr, _module.PxCpuDispatcher)")

fun PxCpuDispatcher.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDefaultAllocator

fun PxDefaultAllocator(_module: dynamic = PhysXJsLoader.physXJs): PxDefaultAllocator = js("new _module.PxDefaultAllocator()")

fun PxDefaultAllocatorFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDefaultAllocator = js("_module.wrapPointer(ptr, _module.PxDefaultAllocator)")

fun PxDefaultAllocator.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDefaultCpuDispatcher : PxCpuDispatcher

fun PxDefaultCpuDispatcherFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDefaultCpuDispatcher = js("_module.wrapPointer(ptr, _module.PxDefaultCpuDispatcher)")

fun PxDefaultCpuDispatcher.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDefaultErrorCallback : PxErrorCallback

fun PxDefaultErrorCallback(_module: dynamic = PhysXJsLoader.physXJs): PxDefaultErrorCallback = js("new _module.PxDefaultErrorCallback()")

fun PxDefaultErrorCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDefaultErrorCallback = js("_module.wrapPointer(ptr, _module.PxDefaultErrorCallback)")

fun PxDefaultErrorCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxErrorCallback {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param code    WebIDL type: [PxErrorCodeEnum] (enum)
     * @param message WebIDL type: DOMString (Const)
     * @param file    WebIDL type: DOMString (Const)
     * @param line    WebIDL type: long
     */
    fun reportError(code: Int, message: String, file: String, line: Int)

}

fun PxErrorCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxErrorCallback = js("_module.wrapPointer(ptr, _module.PxErrorCallback)")

fun PxErrorCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxErrorCallbackImpl : PxErrorCallback {
    /**
     * param code    WebIDL type: [PxErrorCodeEnum] (enum)
     * param message WebIDL type: DOMString (Const)
     * param file    WebIDL type: DOMString (Const)
     * param line    WebIDL type: long
     */
    var reportError: (code: Int, message: String, file: String, line: Int) -> Unit

}

fun PxErrorCallbackImpl(_module: dynamic = PhysXJsLoader.physXJs): PxErrorCallbackImpl = js("new _module.PxErrorCallbackImpl()")

external interface PxFoundation {
    /**
     * Native object address.
     */
    val ptr: Int

    fun release()

}

fun PxFoundationFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxFoundation = js("_module.wrapPointer(ptr, _module.PxFoundation)")

external interface PxInputData

fun PxInputDataFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxInputData = js("_module.wrapPointer(ptr, _module.PxInputData)")

fun PxInputData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxInsertionCallback

fun PxInsertionCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxInsertionCallback = js("_module.wrapPointer(ptr, _module.PxInsertionCallback)")

external interface PxMat33 {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var column0: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var column1: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var column2: PxVec3

    /**
     * @return WebIDL type: [PxMat33] (Value)
     */
    fun getTranspose(): PxMat33

    /**
     * @return WebIDL type: [PxMat33] (Value)
     */
    fun getInverse(): PxMat33

    /**
     * @return WebIDL type: float
     */
    fun getDeterminant(): Float

    /**
     * @param other WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun transform(other: PxVec3): PxVec3

    /**
     * @param other WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun transformTranspose(other: PxVec3): PxVec3

}

fun PxMat33(_module: dynamic = PhysXJsLoader.physXJs): PxMat33 = js("new _module.PxMat33()")

/**
 * @param r WebIDL type: [PxIDENTITYEnum] (enum)
 */
fun PxMat33(r: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMat33 = js("new _module.PxMat33(r)")

/**
 * @param col0 WebIDL type: [PxVec3] (Const, Ref)
 * @param col1 WebIDL type: [PxVec3] (Const, Ref)
 * @param col2 WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxMat33(col0: PxVec3, col1: PxVec3, col2: PxVec3, _module: dynamic = PhysXJsLoader.physXJs): PxMat33 = js("new _module.PxMat33(col0, col1, col2)")

fun PxMat33FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMat33 = js("_module.wrapPointer(ptr, _module.PxMat33)")

fun PxMat33.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxMat33.transpose
    get() = getTranspose()
val PxMat33.inverse
    get() = getInverse()
val PxMat33.determinant
    get() = getDeterminant()

external interface PxOutputStream

fun PxOutputStreamFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxOutputStream = js("_module.wrapPointer(ptr, _module.PxOutputStream)")

fun PxOutputStream.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPlane {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var n: PxVec3
    /**
     * WebIDL type: float
     */
    var d: Float

    /**
     * @param p WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: float
     */
    fun distance(p: PxVec3): Float

    /**
     * @param p WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun contains(p: PxVec3): Boolean

    /**
     * @param p WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun project(p: PxVec3): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun pointInPlane(): PxVec3

    fun normalize()

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxPlane] (Value)
     */
    fun transform(pose: PxTransform): PxPlane

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxPlane] (Value)
     */
    fun inverseTransform(pose: PxTransform): PxPlane

}

fun PxPlane(_module: dynamic = PhysXJsLoader.physXJs): PxPlane = js("new _module.PxPlane()")

/**
 * @param nx       WebIDL type: float
 * @param ny       WebIDL type: float
 * @param nz       WebIDL type: float
 * @param distance WebIDL type: float
 */
fun PxPlane(nx: Float, ny: Float, nz: Float, distance: Float, _module: dynamic = PhysXJsLoader.physXJs): PxPlane = js("new _module.PxPlane(nx, ny, nz, distance)")

/**
 * @param normal   WebIDL type: [PxVec3] (Const, Ref)
 * @param distance WebIDL type: float
 */
fun PxPlane(normal: PxVec3, distance: Float, _module: dynamic = PhysXJsLoader.physXJs): PxPlane = js("new _module.PxPlane(normal, distance)")

/**
 * @param p0 WebIDL type: [PxVec3] (Const, Ref)
 * @param p1 WebIDL type: [PxVec3] (Const, Ref)
 * @param p2 WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxPlane(p0: PxVec3, p1: PxVec3, p2: PxVec3, _module: dynamic = PhysXJsLoader.physXJs): PxPlane = js("new _module.PxPlane(p0, p1, p2)")

fun PxPlaneFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxPlane = js("_module.wrapPointer(ptr, _module.PxPlane)")

fun PxPlane.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxQuat {
    /**
     * Native object address.
     */
    val ptr: Int

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

    /**
     * @return WebIDL type: boolean
     */
    fun isIdentity(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isFinite(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isUnit(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isSane(): Boolean

    /**
     * @return WebIDL type: float
     */
    fun getAngle(): Float

    /**
     * @param q WebIDL type: [PxQuat] (Const, Ref)
     * @return WebIDL type: float
     */
    fun getAngle(q: PxQuat): Float

    /**
     * @return WebIDL type: float
     */
    fun magnitudeSquared(): Float

    /**
     * @param q WebIDL type: [PxQuat] (Const, Ref)
     * @return WebIDL type: float
     */
    fun dot(q: PxQuat): Float

    /**
     * @return WebIDL type: [PxQuat] (Value)
     */
    fun getNormalized(): PxQuat

    /**
     * @return WebIDL type: float
     */
    fun magnitude(): Float

    /**
     * @return WebIDL type: float
     */
    fun normalize(): Float

    /**
     * @return WebIDL type: [PxQuat] (Value)
     */
    fun getConjugate(): PxQuat

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getImaginaryPart(): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getBasisVector0(): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getBasisVector1(): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getBasisVector2(): PxVec3

    /**
     * @param v WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun rotate(v: PxVec3): PxVec3

    /**
     * @param v WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun rotateInv(v: PxVec3): PxVec3

}

fun PxQuat(_module: dynamic = PhysXJsLoader.physXJs): PxQuat = js("new _module.PxQuat()")

/**
 * @param r WebIDL type: [PxIDENTITYEnum] (enum)
 */
fun PxQuat(r: Int, _module: dynamic = PhysXJsLoader.physXJs): PxQuat = js("new _module.PxQuat(r)")

/**
 * @param nx WebIDL type: float
 * @param ny WebIDL type: float
 * @param nz WebIDL type: float
 * @param nw WebIDL type: float
 */
fun PxQuat(nx: Float, ny: Float, nz: Float, nw: Float, _module: dynamic = PhysXJsLoader.physXJs): PxQuat = js("new _module.PxQuat(nx, ny, nz, nw)")

/**
 * @param angleRadians WebIDL type: float
 * @param unitAxis     WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxQuat(angleRadians: Float, unitAxis: PxVec3, _module: dynamic = PhysXJsLoader.physXJs): PxQuat = js("new _module.PxQuat(angleRadians, unitAxis)")

fun PxQuatFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxQuat = js("_module.wrapPointer(ptr, _module.PxQuat)")

fun PxQuat.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxQuat.angle
    get() = getAngle()
val PxQuat.normalized
    get() = getNormalized()
val PxQuat.conjugate
    get() = getConjugate()
val PxQuat.imaginaryPart
    get() = getImaginaryPart()
val PxQuat.basisVector0
    get() = getBasisVector0()
val PxQuat.basisVector1
    get() = getBasisVector1()
val PxQuat.basisVector2
    get() = getBasisVector2()

external interface PxRefCounted : PxBase {
    /**
     * @return WebIDL type: unsigned long
     */
    fun getReferenceCount(): Int

    fun acquireReference()

}

fun PxRefCountedFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRefCounted = js("_module.wrapPointer(ptr, _module.PxRefCounted)")

val PxRefCounted.referenceCount
    get() = getReferenceCount()

external interface PxStridedData {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var stride: Int
    /**
     * WebIDL type: VoidPtr (Const)
     */
    var data: Any
}

fun PxStridedDataFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxStridedData = js("_module.wrapPointer(ptr, _module.PxStridedData)")

fun PxStridedData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTolerancesScale {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var length: Float
    /**
     * WebIDL type: float
     */
    var speed: Float

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxTolerancesScale(_module: dynamic = PhysXJsLoader.physXJs): PxTolerancesScale = js("new _module.PxTolerancesScale()")

fun PxTolerancesScaleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTolerancesScale = js("_module.wrapPointer(ptr, _module.PxTolerancesScale)")

fun PxTolerancesScale.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTransform {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxQuat] (Value)
     */
    var q: PxQuat
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var p: PxVec3

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getInverse(): PxTransform

    /**
     * @param input WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun transform(input: PxVec3): PxVec3

    /**
     * @param input WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun transformInv(input: PxVec3): PxVec3

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isSane(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isFinite(): Boolean

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getNormalized(): PxTransform

}

fun PxTransform(_module: dynamic = PhysXJsLoader.physXJs): PxTransform = js("new _module.PxTransform()")

/**
 * @param r WebIDL type: [PxIDENTITYEnum] (enum)
 */
fun PxTransform(r: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTransform = js("new _module.PxTransform(r)")

/**
 * @param p0 WebIDL type: [PxVec3] (Const, Ref)
 * @param q0 WebIDL type: [PxQuat] (Const, Ref)
 */
fun PxTransform(p0: PxVec3, q0: PxQuat, _module: dynamic = PhysXJsLoader.physXJs): PxTransform = js("new _module.PxTransform(p0, q0)")

fun PxTransformFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTransform = js("_module.wrapPointer(ptr, _module.PxTransform)")

fun PxTransform.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxTransform.inverse
    get() = getInverse()
val PxTransform.normalized
    get() = getNormalized()

external interface PxTypedBoundedData_PxU16 {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var stride: Int
    /**
     * WebIDL type: [PxU16Ptr] (Value)
     */
    var data: PxU16Ptr
}

fun PxTypedBoundedData_PxU16FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTypedBoundedData_PxU16 = js("_module.wrapPointer(ptr, _module.PxTypedBoundedData_PxU16)")

fun PxTypedBoundedData_PxU16.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTypedBoundedData_PxU16Const {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var stride: Int
    /**
     * WebIDL type: [PxU16ConstPtr] (Const, Value)
     */
    var data: PxU16ConstPtr
}

fun PxTypedBoundedData_PxU16ConstFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTypedBoundedData_PxU16Const = js("_module.wrapPointer(ptr, _module.PxTypedBoundedData_PxU16Const)")

fun PxTypedBoundedData_PxU16Const.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVec3 {
    /**
     * Native object address.
     */
    val ptr: Int

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
     * @return WebIDL type: boolean
     */
    fun isZero(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isFinite(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isNormalized(): Boolean

    /**
     * @return WebIDL type: float
     */
    fun magnitudeSquared(): Float

    /**
     * @return WebIDL type: float
     */
    fun magnitude(): Float

    /**
     * @param v WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: float
     */
    fun dot(v: PxVec3): Float

    /**
     * @param v WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun cross(v: PxVec3): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getNormalized(): PxVec3

    /**
     * @return WebIDL type: float
     */
    fun normalize(): Float

    /**
     * @return WebIDL type: float
     */
    fun normalizeSafe(): Float

    /**
     * @return WebIDL type: float
     */
    fun normalizeFast(): Float

    /**
     * @param a WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun multiply(a: PxVec3): PxVec3

    /**
     * @param v WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun minimum(v: PxVec3): PxVec3

    /**
     * @return WebIDL type: float
     */
    fun minElement(): Float

    /**
     * @param v WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun maximum(v: PxVec3): PxVec3

    /**
     * @return WebIDL type: float
     */
    fun maxElement(): Float

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun abs(): PxVec3

}

fun PxVec3(_module: dynamic = PhysXJsLoader.physXJs): PxVec3 = js("new _module.PxVec3()")

/**
 * @param x WebIDL type: float
 * @param y WebIDL type: float
 * @param z WebIDL type: float
 */
fun PxVec3(x: Float, y: Float, z: Float, _module: dynamic = PhysXJsLoader.physXJs): PxVec3 = js("new _module.PxVec3(x, y, z)")

fun PxVec3FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVec3 = js("_module.wrapPointer(ptr, _module.PxVec3)")

fun PxVec3.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVec3.normalized
    get() = getNormalized()

external interface PxVec4 {
    /**
     * Native object address.
     */
    val ptr: Int

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

    /**
     * @return WebIDL type: boolean
     */
    fun isZero(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isFinite(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isNormalized(): Boolean

    /**
     * @return WebIDL type: float
     */
    fun magnitudeSquared(): Float

    /**
     * @return WebIDL type: float
     */
    fun magnitude(): Float

    /**
     * @param v WebIDL type: [PxVec4] (Const, Ref)
     * @return WebIDL type: float
     */
    fun dot(v: PxVec4): Float

    /**
     * @return WebIDL type: [PxVec4] (Value)
     */
    fun getNormalized(): PxVec4

    /**
     * @return WebIDL type: float
     */
    fun normalize(): Float

    /**
     * @param a WebIDL type: [PxVec4] (Const, Ref)
     * @return WebIDL type: [PxVec4] (Value)
     */
    fun multiply(a: PxVec4): PxVec4

    /**
     * @param v WebIDL type: [PxVec4] (Const, Ref)
     * @return WebIDL type: [PxVec4] (Value)
     */
    fun minimum(v: PxVec4): PxVec4

    /**
     * @param v WebIDL type: [PxVec4] (Const, Ref)
     * @return WebIDL type: [PxVec4] (Value)
     */
    fun maximum(v: PxVec4): PxVec4

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getXYZ(): PxVec3

}

fun PxVec4(_module: dynamic = PhysXJsLoader.physXJs): PxVec4 = js("new _module.PxVec4()")

/**
 * @param x WebIDL type: float
 * @param y WebIDL type: float
 * @param z WebIDL type: float
 * @param w WebIDL type: float
 */
fun PxVec4(x: Float, y: Float, z: Float, w: Float, _module: dynamic = PhysXJsLoader.physXJs): PxVec4 = js("new _module.PxVec4(x, y, z, w)")

fun PxVec4FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVec4 = js("_module.wrapPointer(ptr, _module.PxVec4)")

fun PxVec4.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVec4.normalized
    get() = getNormalized()
val PxVec4.xYZ
    get() = getXYZ()

object PxDebugColorEnum {
    val eARGB_BLACK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_BLACK()
    val eARGB_RED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_RED()
    val eARGB_GREEN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_GREEN()
    val eARGB_BLUE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_BLUE()
    val eARGB_YELLOW: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_YELLOW()
    val eARGB_MAGENTA: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_MAGENTA()
    val eARGB_CYAN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_CYAN()
    val eARGB_WHITE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_WHITE()
    val eARGB_GREY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_GREY()
    val eARGB_DARKRED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_DARKRED()
    val eARGB_DARKGREEN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_DARKGREEN()
    val eARGB_DARKBLUE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDebugColorEnum_eARGB_DARKBLUE()
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

