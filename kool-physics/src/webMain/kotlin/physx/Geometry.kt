/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")

package physx

import kotlin.js.JsAny
import kotlin.js.js

external interface PxBoxGeometry : JsAny, DestroyableNative, PxGeometry {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var halfExtents: PxVec3
}

/**
 * @param hx WebIDL type: float
 * @param hy WebIDL type: float
 * @param hz WebIDL type: float
 */
fun PxBoxGeometry(hx: Float, hy: Float, hz: Float, _module: JsAny = PhysXJsLoader.physXJs): PxBoxGeometry = js("new _module.PxBoxGeometry(hx, hy, hz)")

fun PxBoxGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBoxGeometry = js("_module.wrapPointer(ptr, _module.PxBoxGeometry)")

external interface PxBVH : JsAny, PxBase

fun PxBVHFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBVH = js("_module.wrapPointer(ptr, _module.PxBVH)")

external interface PxCapsuleGeometry : JsAny, DestroyableNative, PxGeometry {
    /**
     * WebIDL type: float
     */
    var radius: Float
    /**
     * WebIDL type: float
     */
    var halfHeight: Float
}

/**
 * @param radius     WebIDL type: float
 * @param halfHeight WebIDL type: float
 */
fun PxCapsuleGeometry(radius: Float, halfHeight: Float, _module: JsAny = PhysXJsLoader.physXJs): PxCapsuleGeometry = js("new _module.PxCapsuleGeometry(radius, halfHeight)")

fun PxCapsuleGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxCapsuleGeometry = js("_module.wrapPointer(ptr, _module.PxCapsuleGeometry)")

external interface PxContactBuffer : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxContactPoint] (Value)
     */
    fun get_contacts(index: Int): PxContactPoint
    fun set_contacts(index: Int, value: PxContactPoint)
    /**
     * WebIDL type: unsigned long
     */
    var count: Int
    /**
     * WebIDL type: unsigned long
     */
    var pad: Int
    /**
     * WebIDL type: unsigned long
     */
    var MAX_CONTACTS: Int

    fun reset()

    /**
     * @param worldPoint    WebIDL type: [PxVec3] (Const, Ref)
     * @param worldNormalIn WebIDL type: [PxVec3] (Const, Ref)
     * @param separation    WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun contact(worldPoint: PxVec3, worldNormalIn: PxVec3, separation: Float): Boolean

    /**
     * @param worldPoint    WebIDL type: [PxVec3] (Const, Ref)
     * @param worldNormalIn WebIDL type: [PxVec3] (Const, Ref)
     * @param separation    WebIDL type: float
     * @param faceIndex1    WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun contact(worldPoint: PxVec3, worldNormalIn: PxVec3, separation: Float, faceIndex1: Int): Boolean

    /**
     * @param pt WebIDL type: [PxContactPoint] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun contact(pt: PxContactPoint): Boolean

}

fun PxContactBufferFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxContactBuffer = js("_module.wrapPointer(ptr, _module.PxContactBuffer)")

inline fun PxContactBuffer.getContacts(index: Int) = get_contacts(index)
inline fun PxContactBuffer.setContacts(index: Int, value: PxContactPoint) = set_contacts(index, value)

external interface PxContactPoint : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var normal: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var point: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var targetVel: PxVec3
    /**
     * WebIDL type: float
     */
    var separation: Float
    /**
     * WebIDL type: float
     */
    var maxImpulse: Float
    /**
     * WebIDL type: float
     */
    var staticFriction: Float
    /**
     * WebIDL type: octet
     */
    var materialFlags: Byte
    /**
     * WebIDL type: unsigned long
     */
    var internalFaceIndex1: Int
    /**
     * WebIDL type: float
     */
    var dynamicFriction: Float
    /**
     * WebIDL type: float
     */
    var restitution: Float
    /**
     * WebIDL type: float
     */
    var damping: Float
}

fun PxContactPoint(_module: JsAny = PhysXJsLoader.physXJs): PxContactPoint = js("new _module.PxContactPoint()")

fun PxContactPointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxContactPoint = js("_module.wrapPointer(ptr, _module.PxContactPoint)")

external interface PxConvexCoreBox : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var extents: PxVec3
}

/**
 * @param eX WebIDL type: float
 * @param eY WebIDL type: float
 * @param eZ WebIDL type: float
 */
fun PxConvexCoreBox(eX: Float, eY: Float, eZ: Float, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreBox = js("new _module.PxConvexCoreBox(eX, eY, eZ)")

fun PxConvexCoreBoxFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreBox = js("_module.wrapPointer(ptr, _module.PxConvexCoreBox)")

external interface PxConvexCoreCone : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var height: Float
    /**
     * WebIDL type: float
     */
    var radius: Float
}

/**
 * @param height WebIDL type: float
 * @param radius WebIDL type: float
 */
fun PxConvexCoreCone(height: Float, radius: Float, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreCone = js("new _module.PxConvexCoreCone(height, radius)")

fun PxConvexCoreConeFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreCone = js("_module.wrapPointer(ptr, _module.PxConvexCoreCone)")

external interface PxConvexCoreCylinder : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var height: Float
    /**
     * WebIDL type: float
     */
    var radius: Float
}

/**
 * @param height WebIDL type: float
 * @param radius WebIDL type: float
 */
fun PxConvexCoreCylinder(height: Float, radius: Float, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreCylinder = js("new _module.PxConvexCoreCylinder(height, radius)")

fun PxConvexCoreCylinderFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreCylinder = js("_module.wrapPointer(ptr, _module.PxConvexCoreCylinder)")

external interface PxConvexCoreEllipsoid : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var radii: PxVec3
}

/**
 * @param rX WebIDL type: float
 * @param rY WebIDL type: float
 * @param rZ WebIDL type: float
 */
fun PxConvexCoreEllipsoid(rX: Float, rY: Float, rZ: Float, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreEllipsoid = js("new _module.PxConvexCoreEllipsoid(rX, rY, rZ)")

fun PxConvexCoreEllipsoidFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreEllipsoid = js("_module.wrapPointer(ptr, _module.PxConvexCoreEllipsoid)")

external interface PxConvexCorePoint : JsAny, DestroyableNative

fun PxConvexCorePoint(_module: JsAny = PhysXJsLoader.physXJs): PxConvexCorePoint = js("new _module.PxConvexCorePoint()")

fun PxConvexCorePointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCorePoint = js("_module.wrapPointer(ptr, _module.PxConvexCorePoint)")

external interface PxConvexCoreSegment : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var length: Float
}

/**
 * @param length WebIDL type: float
 */
fun PxConvexCoreSegment(length: Float, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreSegment = js("new _module.PxConvexCoreSegment(length)")

fun PxConvexCoreSegmentFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreSegment = js("_module.wrapPointer(ptr, _module.PxConvexCoreSegment)")

external interface PxConvexCoreGeometry : JsAny, DestroyableNative, PxGeometry {
    /**
     * @return WebIDL type: [PxConvexCoreTypeEnum] (enum)
     */
    fun getCoreType(): Int

    /**
     * @return WebIDL type: VoidPtr (Const)
     */
    fun getCoreData(): JsAny

    /**
     * @return WebIDL type: float
     */
    fun getMargin(): Float

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxConvexCoreGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreGeometry = js("_module.wrapPointer(ptr, _module.PxConvexCoreGeometry)")

val PxConvexCoreGeometry.coreType: PxConvexCoreTypeEnum
    get() = PxConvexCoreTypeEnum.forValue(getCoreType())
val PxConvexCoreGeometry.coreData
    get() = getCoreData()
val PxConvexCoreGeometry.margin
    get() = getMargin()

external interface PxConvexCoreGeometryFactory : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param box    WebIDL type: [PxConvexCoreBox] (Const, Ref)
     * @param margin WebIDL type: float
     * @return WebIDL type: [PxConvexCoreGeometry]
     */
    fun createFromBox(box: PxConvexCoreBox, margin: Float): PxConvexCoreGeometry

    /**
     * @param cone   WebIDL type: [PxConvexCoreCone] (Const, Ref)
     * @param margin WebIDL type: float
     * @return WebIDL type: [PxConvexCoreGeometry]
     */
    fun createFromCone(cone: PxConvexCoreCone, margin: Float): PxConvexCoreGeometry

    /**
     * @param cylinder WebIDL type: [PxConvexCoreCylinder] (Const, Ref)
     * @param margin   WebIDL type: float
     * @return WebIDL type: [PxConvexCoreGeometry]
     */
    fun createFromCylinder(cylinder: PxConvexCoreCylinder, margin: Float): PxConvexCoreGeometry

    /**
     * @param ellipsoid WebIDL type: [PxConvexCoreEllipsoid] (Const, Ref)
     * @param margin    WebIDL type: float
     * @return WebIDL type: [PxConvexCoreGeometry]
     */
    fun createFromEllipsoid(ellipsoid: PxConvexCoreEllipsoid, margin: Float): PxConvexCoreGeometry

    /**
     * @param point  WebIDL type: [PxConvexCorePoint] (Const, Ref)
     * @param margin WebIDL type: float
     * @return WebIDL type: [PxConvexCoreGeometry]
     */
    fun createFromPoint(point: PxConvexCorePoint, margin: Float): PxConvexCoreGeometry

    /**
     * @param segment WebIDL type: [PxConvexCoreSegment] (Const, Ref)
     * @param margin  WebIDL type: float
     * @return WebIDL type: [PxConvexCoreGeometry]
     */
    fun createFromSegment(segment: PxConvexCoreSegment, margin: Float): PxConvexCoreGeometry

}

fun PxConvexCoreGeometryFactoryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexCoreGeometryFactory = js("_module.wrapPointer(ptr, _module.PxConvexCoreGeometryFactory)")

external interface PxConvexMesh : JsAny, PxRefCounted {
    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbVertices(): Int

    /**
     * @return WebIDL type: [PxVec3] (Const)
     */
    fun getVertices(): PxVec3

    /**
     * @return WebIDL type: [PxU8ConstPtr] (Value)
     */
    fun getIndexBuffer(): PxU8ConstPtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbPolygons(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @param data  WebIDL type: [PxHullPolygon] (Ref)
     * @return WebIDL type: boolean
     */
    fun getPolygonData(index: Int, data: PxHullPolygon): Boolean

    /**
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getLocalBounds(): PxBounds3

    /**
     * @return WebIDL type: boolean
     */
    fun isGpuCompatible(): Boolean

}

fun PxConvexMeshFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexMesh = js("_module.wrapPointer(ptr, _module.PxConvexMesh)")

val PxConvexMesh.nbVertices
    get() = getNbVertices()
val PxConvexMesh.vertices
    get() = getVertices()
val PxConvexMesh.indexBuffer
    get() = getIndexBuffer()
val PxConvexMesh.nbPolygons
    get() = getNbPolygons()
val PxConvexMesh.localBounds
    get() = getLocalBounds()

external interface PxConvexMeshGeometry : JsAny, DestroyableNative, PxGeometry {
    /**
     * WebIDL type: [PxMeshScale] (Value)
     */
    var scale: PxMeshScale
    /**
     * WebIDL type: [PxConvexMesh]
     */
    var convexMesh: PxConvexMesh
    /**
     * WebIDL type: [PxConvexMeshGeometryFlags] (Value)
     */
    var meshFlags: PxConvexMeshGeometryFlags
}

/**
 * @param mesh WebIDL type: [PxConvexMesh]
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, _module: JsAny = PhysXJsLoader.physXJs): PxConvexMeshGeometry = js("new _module.PxConvexMeshGeometry(mesh)")

/**
 * @param mesh    WebIDL type: [PxConvexMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, _module: JsAny = PhysXJsLoader.physXJs): PxConvexMeshGeometry = js("new _module.PxConvexMeshGeometry(mesh, scaling)")

/**
 * @param mesh    WebIDL type: [PxConvexMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 * @param flags   WebIDL type: [PxConvexMeshGeometryFlags] (Ref)
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags, _module: JsAny = PhysXJsLoader.physXJs): PxConvexMeshGeometry = js("new _module.PxConvexMeshGeometry(mesh, scaling, flags)")

fun PxConvexMeshGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexMeshGeometry = js("_module.wrapPointer(ptr, _module.PxConvexMeshGeometry)")

external interface PxConvexMeshGeometryFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxConvexMeshGeometryFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxConvexMeshGeometryFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxConvexMeshGeometryFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxConvexMeshGeometryFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxConvexMeshGeometryFlags = js("new _module.PxConvexMeshGeometryFlags(flags)")

fun PxConvexMeshGeometryFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexMeshGeometryFlags = js("_module.wrapPointer(ptr, _module.PxConvexMeshGeometryFlags)")

fun PxConvexMeshGeometryFlags.isSet(flag: PxConvexMeshGeometryFlagEnum) = isSet(flag.value)
fun PxConvexMeshGeometryFlags.raise(flag: PxConvexMeshGeometryFlagEnum) = raise(flag.value)
fun PxConvexMeshGeometryFlags.clear(flag: PxConvexMeshGeometryFlagEnum) = clear(flag.value)

external interface PxGeometry : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: [PxGeometryTypeEnum] (enum)
     */
    fun getType(): Int

}

fun PxGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGeometry = js("_module.wrapPointer(ptr, _module.PxGeometry)")

val PxGeometry.type: PxGeometryTypeEnum
    get() = PxGeometryTypeEnum.forValue(getType())

external interface PxGeometryHolder : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: [PxGeometryTypeEnum] (enum)
     */
    fun getType(): Int

    /**
     * @return WebIDL type: [PxSphereGeometry] (Ref)
     */
    fun sphere(): PxSphereGeometry

    /**
     * @return WebIDL type: [PxPlaneGeometry] (Ref)
     */
    fun plane(): PxPlaneGeometry

    /**
     * @return WebIDL type: [PxCapsuleGeometry] (Ref)
     */
    fun capsule(): PxCapsuleGeometry

    /**
     * @return WebIDL type: [PxBoxGeometry] (Ref)
     */
    fun box(): PxBoxGeometry

    /**
     * @return WebIDL type: [PxConvexMeshGeometry] (Ref)
     */
    fun convexMesh(): PxConvexMeshGeometry

    /**
     * @return WebIDL type: [PxTriangleMeshGeometry] (Ref)
     */
    fun triangleMesh(): PxTriangleMeshGeometry

    /**
     * @return WebIDL type: [PxHeightFieldGeometry] (Ref)
     */
    fun heightField(): PxHeightFieldGeometry

    /**
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     */
    fun storeAny(geometry: PxGeometry)

}

fun PxGeometryHolder(_module: JsAny = PhysXJsLoader.physXJs): PxGeometryHolder = js("new _module.PxGeometryHolder()")

/**
 * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
 */
fun PxGeometryHolder(geometry: PxGeometry, _module: JsAny = PhysXJsLoader.physXJs): PxGeometryHolder = js("new _module.PxGeometryHolder(geometry)")

fun PxGeometryHolderFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGeometryHolder = js("_module.wrapPointer(ptr, _module.PxGeometryHolder)")

val PxGeometryHolder.type: PxGeometryTypeEnum
    get() = PxGeometryTypeEnum.forValue(getType())

external interface PxGeometryQuery : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param unitDir  WebIDL type: [PxVec3] (Const, Ref)
     * @param maxDist  WebIDL type: float
     * @param geom0    WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose0    WebIDL type: [PxTransform] (Const, Ref)
     * @param geom1    WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose1    WebIDL type: [PxTransform] (Const, Ref)
     * @param sweepHit WebIDL type: [PxSweepHit] (Ref)
     * @return WebIDL type: boolean
     */
    fun sweep(unitDir: PxVec3, maxDist: Float, geom0: PxGeometry, pose0: PxTransform, geom1: PxGeometry, pose1: PxTransform, sweepHit: PxSweepHit): Boolean

    /**
     * @param unitDir  WebIDL type: [PxVec3] (Const, Ref)
     * @param maxDist  WebIDL type: float
     * @param geom0    WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose0    WebIDL type: [PxTransform] (Const, Ref)
     * @param geom1    WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose1    WebIDL type: [PxTransform] (Const, Ref)
     * @param sweepHit WebIDL type: [PxSweepHit] (Ref)
     * @param hitFlags WebIDL type: [PxHitFlags] (Ref)
     * @return WebIDL type: boolean
     */
    fun sweep(unitDir: PxVec3, maxDist: Float, geom0: PxGeometry, pose0: PxTransform, geom1: PxGeometry, pose1: PxTransform, sweepHit: PxSweepHit, hitFlags: PxHitFlags): Boolean

    /**
     * @param unitDir   WebIDL type: [PxVec3] (Const, Ref)
     * @param maxDist   WebIDL type: float
     * @param geom0     WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose0     WebIDL type: [PxTransform] (Const, Ref)
     * @param geom1     WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose1     WebIDL type: [PxTransform] (Const, Ref)
     * @param sweepHit  WebIDL type: [PxSweepHit] (Ref)
     * @param hitFlags  WebIDL type: [PxHitFlags] (Ref)
     * @param inflation WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun sweep(unitDir: PxVec3, maxDist: Float, geom0: PxGeometry, pose0: PxTransform, geom1: PxGeometry, pose1: PxTransform, sweepHit: PxSweepHit, hitFlags: PxHitFlags, inflation: Float): Boolean

    /**
     * @param geom0 WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose0 WebIDL type: [PxTransform] (Const, Ref)
     * @param geom1 WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose1 WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun overlap(geom0: PxGeometry, pose0: PxTransform, geom1: PxGeometry, pose1: PxTransform): Boolean

    /**
     * @param origin   WebIDL type: [PxVec3] (Const, Ref)
     * @param unitDir  WebIDL type: [PxVec3] (Const, Ref)
     * @param geom     WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param maxDist  WebIDL type: float
     * @param hitFlags WebIDL type: [PxHitFlags] (Ref)
     * @param maxHits  WebIDL type: unsigned long
     * @param rayHits  WebIDL type: [PxRaycastHit]
     * @return WebIDL type: unsigned long
     */
    fun raycast(origin: PxVec3, unitDir: PxVec3, geom: PxGeometry, pose: PxTransform, maxDist: Float, hitFlags: PxHitFlags, maxHits: Int, rayHits: PxRaycastHit): Int

    /**
     * @param point WebIDL type: [PxVec3] (Const, Ref)
     * @param geom  WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose  WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: float
     */
    fun pointDistance(point: PxVec3, geom: PxGeometry, pose: PxTransform): Float

    /**
     * @param point        WebIDL type: [PxVec3] (Const, Ref)
     * @param geom         WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose         WebIDL type: [PxTransform] (Const, Ref)
     * @param closestPoint WebIDL type: [PxVec3]
     * @return WebIDL type: float
     */
    fun pointDistance(point: PxVec3, geom: PxGeometry, pose: PxTransform, closestPoint: PxVec3): Float

    /**
     * @param bounds WebIDL type: [PxBounds3] (Ref)
     * @param geom   WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose   WebIDL type: [PxTransform] (Const, Ref)
     */
    fun computeGeomBounds(bounds: PxBounds3, geom: PxGeometry, pose: PxTransform)

    /**
     * @param bounds    WebIDL type: [PxBounds3] (Ref)
     * @param geom      WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose      WebIDL type: [PxTransform] (Const, Ref)
     * @param inflation WebIDL type: float
     */
    fun computeGeomBounds(bounds: PxBounds3, geom: PxGeometry, pose: PxTransform, inflation: Float)

    /**
     * @param geom WebIDL type: [PxGeometry] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(geom: PxGeometry): Boolean

}

fun PxGeometryQueryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGeometryQuery = js("_module.wrapPointer(ptr, _module.PxGeometryQuery)")

external interface PxHeightField : JsAny, PxRefCounted {
    /**
     * @param destBuffer     WebIDL type: VoidPtr
     * @param destBufferSize WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun saveCells(destBuffer: JsAny, destBufferSize: Int): Int

    /**
     * @param startCol     WebIDL type: long
     * @param startRow     WebIDL type: long
     * @param subfieldDesc WebIDL type: [PxHeightFieldDesc] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun modifySamples(startCol: Int, startRow: Int, subfieldDesc: PxHeightFieldDesc): Boolean

    /**
     * @param startCol     WebIDL type: long
     * @param startRow     WebIDL type: long
     * @param subfieldDesc WebIDL type: [PxHeightFieldDesc] (Const, Ref)
     * @param shrinkBounds WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun modifySamples(startCol: Int, startRow: Int, subfieldDesc: PxHeightFieldDesc, shrinkBounds: Boolean): Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbRows(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbColumns(): Int

    /**
     * @return WebIDL type: [PxHeightFieldFormatEnum] (enum)
     */
    fun getFormat(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getSampleStride(): Int

    /**
     * @return WebIDL type: float
     */
    fun getConvexEdgeThreshold(): Float

    /**
     * @return WebIDL type: [PxHeightFieldFlags] (Value)
     */
    fun getFlags(): PxHeightFieldFlags

    /**
     * @param x WebIDL type: float
     * @param z WebIDL type: float
     * @return WebIDL type: float
     */
    fun getHeight(x: Float, z: Float): Float

    /**
     * @param triangleIndex WebIDL type: unsigned long
     * @return WebIDL type: unsigned short
     */
    fun getTriangleMaterialIndex(triangleIndex: Int): Short

    /**
     * @param triangleIndex WebIDL type: unsigned long
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getTriangleNormal(triangleIndex: Int): PxVec3

    /**
     * @param row    WebIDL type: unsigned long
     * @param column WebIDL type: unsigned long
     * @return WebIDL type: [PxHeightFieldSample] (Const, Ref)
     */
    fun getSample(row: Int, column: Int): PxHeightFieldSample

    /**
     * @return WebIDL type: unsigned long
     */
    fun getTimestamp(): Int

}

fun PxHeightFieldFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxHeightField = js("_module.wrapPointer(ptr, _module.PxHeightField)")

val PxHeightField.nbRows
    get() = getNbRows()
val PxHeightField.nbColumns
    get() = getNbColumns()
val PxHeightField.format: PxHeightFieldFormatEnum
    get() = PxHeightFieldFormatEnum.forValue(getFormat())
val PxHeightField.sampleStride
    get() = getSampleStride()
val PxHeightField.convexEdgeThreshold
    get() = getConvexEdgeThreshold()
val PxHeightField.flags
    get() = getFlags()
val PxHeightField.timestamp
    get() = getTimestamp()

external interface PxHeightFieldDesc : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var nbRows: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbColumns: Int
    /**
     * WebIDL type: [PxHeightFieldFormatEnum] (enum)
     */
    var format: Int
    /**
     * WebIDL type: [PxStridedData] (Value)
     */
    var samples: PxStridedData
    /**
     * WebIDL type: float
     */
    var convexEdgeThreshold: Float
    /**
     * WebIDL type: [PxHeightFieldFlags] (Value)
     */
    var flags: PxHeightFieldFlags

    fun setToDefault()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxHeightFieldDesc(_module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldDesc = js("new _module.PxHeightFieldDesc()")

fun PxHeightFieldDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldDesc = js("_module.wrapPointer(ptr, _module.PxHeightFieldDesc)")

var PxHeightFieldDesc.formatEnum: PxHeightFieldFormatEnum
    get() = PxHeightFieldFormatEnum.forValue(format)
    set(value) { format = value.value }

external interface PxHeightFieldFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxHeightFieldFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxHeightFieldFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxHeightFieldFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxHeightFieldFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldFlags = js("new _module.PxHeightFieldFlags(flags)")

fun PxHeightFieldFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldFlags = js("_module.wrapPointer(ptr, _module.PxHeightFieldFlags)")

fun PxHeightFieldFlags.isSet(flag: PxHeightFieldFlagEnum) = isSet(flag.value)
fun PxHeightFieldFlags.raise(flag: PxHeightFieldFlagEnum) = raise(flag.value)
fun PxHeightFieldFlags.clear(flag: PxHeightFieldFlagEnum) = clear(flag.value)

external interface PxHeightFieldGeometry : JsAny, DestroyableNative, PxGeometry {
    /**
     * WebIDL type: [PxHeightField]
     */
    var heightField: PxHeightField
    /**
     * WebIDL type: float
     */
    var heightScale: Float
    /**
     * WebIDL type: float
     */
    var rowScale: Float
    /**
     * WebIDL type: float
     */
    var columnScale: Float
    /**
     * WebIDL type: [PxMeshGeometryFlags] (Value)
     */
    var heightFieldFlags: PxMeshGeometryFlags

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxHeightFieldGeometry(_module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldGeometry = js("new _module.PxHeightFieldGeometry()")

/**
 * @param hf          WebIDL type: [PxHeightField]
 * @param flags       WebIDL type: [PxMeshGeometryFlags] (Ref)
 * @param heightScale WebIDL type: float
 * @param rowScale    WebIDL type: float
 * @param columnScale WebIDL type: float
 */
fun PxHeightFieldGeometry(hf: PxHeightField, flags: PxMeshGeometryFlags, heightScale: Float, rowScale: Float, columnScale: Float, _module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldGeometry = js("new _module.PxHeightFieldGeometry(hf, flags, heightScale, rowScale, columnScale)")

fun PxHeightFieldGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldGeometry = js("_module.wrapPointer(ptr, _module.PxHeightFieldGeometry)")

external interface PxHeightFieldSample : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: short
     */
    var height: Short
    /**
     * WebIDL type: octet
     */
    var materialIndex0: Byte
    /**
     * WebIDL type: octet
     */
    var materialIndex1: Byte

    /**
     * @return WebIDL type: octet
     */
    fun tessFlag(): Byte

    fun clearTessFlag()

    fun setTessFlag()

}

fun PxHeightFieldSample(_module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldSample = js("new _module.PxHeightFieldSample()")

fun PxHeightFieldSampleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxHeightFieldSample = js("_module.wrapPointer(ptr, _module.PxHeightFieldSample)")

external interface PxHullPolygon : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_mPlane(index: Int): Float
    fun set_mPlane(index: Int, value: Float)
    /**
     * WebIDL type: short
     */
    var mNbVerts: Short
    /**
     * WebIDL type: short
     */
    var mIndexBase: Short
}

fun PxHullPolygon(_module: JsAny = PhysXJsLoader.physXJs): PxHullPolygon = js("new _module.PxHullPolygon()")

fun PxHullPolygonFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxHullPolygon = js("_module.wrapPointer(ptr, _module.PxHullPolygon)")

inline fun PxHullPolygon.getMPlane(index: Int) = get_mPlane(index)
inline fun PxHullPolygon.setMPlane(index: Int, value: Float) = set_mPlane(index, value)

external interface PxMeshFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxMeshFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxMeshFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxMeshFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxMeshFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxMeshFlags = js("new _module.PxMeshFlags(flags)")

fun PxMeshFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMeshFlags = js("_module.wrapPointer(ptr, _module.PxMeshFlags)")

fun PxMeshFlags.isSet(flag: PxMeshFlagEnum) = isSet(flag.value)
fun PxMeshFlags.raise(flag: PxMeshFlagEnum) = raise(flag.value)
fun PxMeshFlags.clear(flag: PxMeshFlagEnum) = clear(flag.value)

external interface PxMeshGeometryFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxMeshGeometryFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxMeshGeometryFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxMeshGeometryFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxMeshGeometryFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxMeshGeometryFlags = js("new _module.PxMeshGeometryFlags(flags)")

fun PxMeshGeometryFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMeshGeometryFlags = js("_module.wrapPointer(ptr, _module.PxMeshGeometryFlags)")

fun PxMeshGeometryFlags.isSet(flag: PxMeshGeometryFlagEnum) = isSet(flag.value)
fun PxMeshGeometryFlags.raise(flag: PxMeshGeometryFlagEnum) = raise(flag.value)
fun PxMeshGeometryFlags.clear(flag: PxMeshGeometryFlagEnum) = clear(flag.value)

external interface PxMeshScale : JsAny, DestroyableNative

fun PxMeshScale(_module: JsAny = PhysXJsLoader.physXJs): PxMeshScale = js("new _module.PxMeshScale()")

/**
 * @param r WebIDL type: float
 */
fun PxMeshScale(r: Float, _module: JsAny = PhysXJsLoader.physXJs): PxMeshScale = js("new _module.PxMeshScale(r)")

/**
 * @param s WebIDL type: [PxVec3] (Const, Ref)
 * @param r WebIDL type: [PxQuat] (Const, Ref)
 */
fun PxMeshScale(s: PxVec3, r: PxQuat, _module: JsAny = PhysXJsLoader.physXJs): PxMeshScale = js("new _module.PxMeshScale(s, r)")

fun PxMeshScaleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMeshScale = js("_module.wrapPointer(ptr, _module.PxMeshScale)")

external interface PxPlaneGeometry : JsAny, DestroyableNative, PxGeometry

fun PxPlaneGeometry(_module: JsAny = PhysXJsLoader.physXJs): PxPlaneGeometry = js("new _module.PxPlaneGeometry()")

fun PxPlaneGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPlaneGeometry = js("_module.wrapPointer(ptr, _module.PxPlaneGeometry)")

external interface PxSimpleTriangleMesh : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxBoundedData] (Value)
     */
    var points: PxBoundedData
    /**
     * WebIDL type: [PxBoundedData] (Value)
     */
    var triangles: PxBoundedData
    /**
     * WebIDL type: [PxMeshFlags] (Value)
     */
    var flags: PxMeshFlags

    fun setToDefault()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxSimpleTriangleMesh(_module: JsAny = PhysXJsLoader.physXJs): PxSimpleTriangleMesh = js("new _module.PxSimpleTriangleMesh()")

fun PxSimpleTriangleMeshFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSimpleTriangleMesh = js("_module.wrapPointer(ptr, _module.PxSimpleTriangleMesh)")

external interface PxSphereGeometry : JsAny, DestroyableNative, PxGeometry {
    /**
     * WebIDL type: float
     */
    var radius: Float
}

/**
 * @param ir WebIDL type: float
 */
fun PxSphereGeometry(ir: Float, _module: JsAny = PhysXJsLoader.physXJs): PxSphereGeometry = js("new _module.PxSphereGeometry(ir)")

fun PxSphereGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSphereGeometry = js("_module.wrapPointer(ptr, _module.PxSphereGeometry)")

external interface PxTetMaker : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param triangleMesh    WebIDL type: [PxSimpleTriangleMesh] (Const, Ref)
     * @param outVertices     WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outTetIndices   WebIDL type: [PxArray_PxU32] (Ref)
     * @param validate        WebIDL type: boolean
     * @param volumeThreshold WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun createConformingTetrahedronMesh(triangleMesh: PxSimpleTriangleMesh, outVertices: PxArray_PxVec3, outTetIndices: PxArray_PxU32, validate: Boolean, volumeThreshold: Float): Boolean

    /**
     * @param tetMesh                              WebIDL type: [PxTetrahedronMeshDesc] (Const, Ref)
     * @param numVoxelsAlongLongestBoundingBoxAxis WebIDL type: unsigned long
     * @param outVertices                          WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outTetIndices                        WebIDL type: [PxArray_PxU32] (Ref)
     * @return WebIDL type: boolean
     */
    fun createVoxelTetrahedronMesh(tetMesh: PxTetrahedronMeshDesc, numVoxelsAlongLongestBoundingBoxAxis: Int, outVertices: PxArray_PxVec3, outTetIndices: PxArray_PxU32): Boolean

    /**
     * @param tetMesh         WebIDL type: [PxTetrahedronMeshDesc] (Const, Ref)
     * @param voxelEdgeLength WebIDL type: float
     * @param outVertices     WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outTetIndices   WebIDL type: [PxArray_PxU32] (Ref)
     * @return WebIDL type: boolean
     */
    fun createVoxelTetrahedronMeshFromEdgeLength(tetMesh: PxTetrahedronMeshDesc, voxelEdgeLength: Float, outVertices: PxArray_PxVec3, outTetIndices: PxArray_PxU32): Boolean

    /**
     * @param triangleMesh            WebIDL type: [PxSimpleTriangleMesh] (Const, Ref)
     * @param minVolumeThreshold      WebIDL type: float
     * @param minTriangleAngleRadians WebIDL type: float
     * @return WebIDL type: [PxTriangleMeshAnalysisResults] (Value)
     */
    fun validateTriangleMesh(triangleMesh: PxSimpleTriangleMesh, minVolumeThreshold: Float, minTriangleAngleRadians: Float): PxTriangleMeshAnalysisResults

    /**
     * @param points                WebIDL type: [PxBoundedData] (Const, Ref)
     * @param tetrahedra            WebIDL type: [PxBoundedData] (Const, Ref)
     * @param minTetVolumeThreshold WebIDL type: float
     * @return WebIDL type: [PxTetrahedronMeshAnalysisResults] (Value)
     */
    fun validateTetrahedronMesh(points: PxBoundedData, tetrahedra: PxBoundedData, minTetVolumeThreshold: Float): PxTetrahedronMeshAnalysisResults

    /**
     * @param inputVertices       WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices        WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param targetTriangleCount WebIDL type: long
     * @param maximalEdgeLength   WebIDL type: float
     * @param outputVertices      WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices       WebIDL type: [PxArray_PxU32] (Ref)
     */
    fun simplifyTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, targetTriangleCount: Int, maximalEdgeLength: Float, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32)

    /**
     * @param inputVertices       WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices        WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param targetTriangleCount WebIDL type: long
     * @param maximalEdgeLength   WebIDL type: float
     * @param outputVertices      WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices       WebIDL type: [PxArray_PxU32] (Ref)
     * @param vertexMap           WebIDL type: [PxArray_PxU32]
     */
    fun simplifyTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, targetTriangleCount: Int, maximalEdgeLength: Float, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, vertexMap: PxArray_PxU32)

    /**
     * @param inputVertices        WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices         WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param targetTriangleCount  WebIDL type: long
     * @param maximalEdgeLength    WebIDL type: float
     * @param outputVertices       WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices        WebIDL type: [PxArray_PxU32] (Ref)
     * @param vertexMap            WebIDL type: [PxArray_PxU32]
     * @param edgeLengthCostWeight WebIDL type: float
     */
    fun simplifyTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, targetTriangleCount: Int, maximalEdgeLength: Float, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, vertexMap: PxArray_PxU32, edgeLengthCostWeight: Float)

    /**
     * @param inputVertices              WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices               WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param targetTriangleCount        WebIDL type: long
     * @param maximalEdgeLength          WebIDL type: float
     * @param outputVertices             WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices              WebIDL type: [PxArray_PxU32] (Ref)
     * @param vertexMap                  WebIDL type: [PxArray_PxU32]
     * @param edgeLengthCostWeight       WebIDL type: float
     * @param flatnessDetectionThreshold WebIDL type: float
     */
    fun simplifyTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, targetTriangleCount: Int, maximalEdgeLength: Float, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, vertexMap: PxArray_PxU32, edgeLengthCostWeight: Float, flatnessDetectionThreshold: Float)

    /**
     * @param inputVertices                             WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices                              WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param targetTriangleCount                       WebIDL type: long
     * @param maximalEdgeLength                         WebIDL type: float
     * @param outputVertices                            WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices                             WebIDL type: [PxArray_PxU32] (Ref)
     * @param vertexMap                                 WebIDL type: [PxArray_PxU32]
     * @param edgeLengthCostWeight                      WebIDL type: float
     * @param flatnessDetectionThreshold                WebIDL type: float
     * @param projectSimplifiedPointsOnInputMeshSurface WebIDL type: boolean
     */
    fun simplifyTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, targetTriangleCount: Int, maximalEdgeLength: Float, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, vertexMap: PxArray_PxU32, edgeLengthCostWeight: Float, flatnessDetectionThreshold: Float, projectSimplifiedPointsOnInputMeshSurface: Boolean)

    /**
     * @param inputVertices                             WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices                              WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param targetTriangleCount                       WebIDL type: long
     * @param maximalEdgeLength                         WebIDL type: float
     * @param outputVertices                            WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices                             WebIDL type: [PxArray_PxU32] (Ref)
     * @param vertexMap                                 WebIDL type: [PxArray_PxU32]
     * @param edgeLengthCostWeight                      WebIDL type: float
     * @param flatnessDetectionThreshold                WebIDL type: float
     * @param projectSimplifiedPointsOnInputMeshSurface WebIDL type: boolean
     * @param outputVertexToInputTriangle               WebIDL type: [PxArray_PxU32]
     */
    fun simplifyTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, targetTriangleCount: Int, maximalEdgeLength: Float, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, vertexMap: PxArray_PxU32, edgeLengthCostWeight: Float, flatnessDetectionThreshold: Float, projectSimplifiedPointsOnInputMeshSurface: Boolean, outputVertexToInputTriangle: PxArray_PxU32)

    /**
     * @param inputVertices                             WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices                              WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param targetTriangleCount                       WebIDL type: long
     * @param maximalEdgeLength                         WebIDL type: float
     * @param outputVertices                            WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices                             WebIDL type: [PxArray_PxU32] (Ref)
     * @param vertexMap                                 WebIDL type: [PxArray_PxU32]
     * @param edgeLengthCostWeight                      WebIDL type: float
     * @param flatnessDetectionThreshold                WebIDL type: float
     * @param projectSimplifiedPointsOnInputMeshSurface WebIDL type: boolean
     * @param outputVertexToInputTriangle               WebIDL type: [PxArray_PxU32]
     * @param removeDisconnectedPatches                 WebIDL type: boolean
     */
    fun simplifyTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, targetTriangleCount: Int, maximalEdgeLength: Float, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, vertexMap: PxArray_PxU32, edgeLengthCostWeight: Float, flatnessDetectionThreshold: Float, projectSimplifiedPointsOnInputMeshSurface: Boolean, outputVertexToInputTriangle: PxArray_PxU32, removeDisconnectedPatches: Boolean)

    /**
     * @param inputVertices  WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices   WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param gridResolution WebIDL type: unsigned long
     * @param outputVertices WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices  WebIDL type: [PxArray_PxU32] (Ref)
     */
    fun remeshTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, gridResolution: Int, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32)

    /**
     * @param inputVertices  WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices   WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param gridResolution WebIDL type: unsigned long
     * @param outputVertices WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices  WebIDL type: [PxArray_PxU32] (Ref)
     * @param vertexMap      WebIDL type: [PxArray_PxU32]
     */
    fun remeshTriangleMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, gridResolution: Int, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, vertexMap: PxArray_PxU32)

    /**
     * @param inputVertices   WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices    WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param useTreeNodes    WebIDL type: boolean
     * @param outputVertices  WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices   WebIDL type: [PxArray_PxU32] (Ref)
     * @param volumeThreshold WebIDL type: float
     */
    fun createTreeBasedTetrahedralMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, useTreeNodes: Boolean, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, volumeThreshold: Float)

    /**
     * @param inputVertices  WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices   WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param outputVertices WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices  WebIDL type: [PxArray_PxU32] (Ref)
     * @param resolution     WebIDL type: long
     */
    fun createRelaxedVoxelTetrahedralMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, resolution: Int)

    /**
     * @param inputVertices           WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices            WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param outputVertices          WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices           WebIDL type: [PxArray_PxU32] (Ref)
     * @param resolution              WebIDL type: long
     * @param numRelaxationIterations WebIDL type: long
     */
    fun createRelaxedVoxelTetrahedralMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, resolution: Int, numRelaxationIterations: Int)

    /**
     * @param inputVertices           WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param inputIndices            WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param outputVertices          WebIDL type: [PxArray_PxVec3] (Ref)
     * @param outputIndices           WebIDL type: [PxArray_PxU32] (Ref)
     * @param resolution              WebIDL type: long
     * @param numRelaxationIterations WebIDL type: long
     * @param relMinTetVolume         WebIDL type: float
     */
    fun createRelaxedVoxelTetrahedralMesh(inputVertices: PxArray_PxVec3, inputIndices: PxArray_PxU32, outputVertices: PxArray_PxVec3, outputIndices: PxArray_PxU32, resolution: Int, numRelaxationIterations: Int, relMinTetVolume: Float)

    /**
     * @param triangles              WebIDL type: [PxI32ConstPtr] (Ref)
     * @param numTriangles           WebIDL type: unsigned long
     * @param islandIndexPerTriangle WebIDL type: [PxArray_PxU32] (Ref)
     */
    fun detectTriangleIslands(triangles: PxI32ConstPtr, numTriangles: Int, islandIndexPerTriangle: PxArray_PxU32)

    /**
     * @param islandIndexPerTriangle WebIDL type: [PxU32ConstPtr] (Ref)
     * @param numTriangles           WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun findLargestIslandId(islandIndexPerTriangle: PxU32ConstPtr, numTriangles: Int): Int

}

fun PxTetMakerFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetMaker = js("_module.wrapPointer(ptr, _module.PxTetMaker)")

external interface PxTetrahedronMeshAnalysisResults : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxTetrahedronMeshAnalysisResultEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxTetrahedronMeshAnalysisResultEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxTetrahedronMeshAnalysisResultEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxTetrahedronMeshAnalysisResults(flags: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshAnalysisResults = js("new _module.PxTetrahedronMeshAnalysisResults(flags)")

fun PxTetrahedronMeshAnalysisResultsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshAnalysisResults = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshAnalysisResults)")

fun PxTetrahedronMeshAnalysisResults.isSet(flag: PxTetrahedronMeshAnalysisResultEnum) = isSet(flag.value)
fun PxTetrahedronMeshAnalysisResults.raise(flag: PxTetrahedronMeshAnalysisResultEnum) = raise(flag.value)
fun PxTetrahedronMeshAnalysisResults.clear(flag: PxTetrahedronMeshAnalysisResultEnum) = clear(flag.value)

external interface PxTetrahedronMeshDesc : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxTypedBoundedData_PxU16] (Value)
     */
    var materialIndices: PxTypedBoundedData_PxU16
    /**
     * WebIDL type: [PxBoundedData] (Value)
     */
    var points: PxBoundedData
    /**
     * WebIDL type: [PxBoundedData] (Value)
     */
    var tetrahedrons: PxBoundedData
    /**
     * WebIDL type: [PxMeshFlags] (Value)
     */
    var flags: PxMeshFlags
    /**
     * WebIDL type: short
     */
    var tetsPerElement: Short

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxTetrahedronMeshDesc(_module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("new _module.PxTetrahedronMeshDesc()")

/**
 * @param meshVertices   WebIDL type: [PxArray_PxVec3] (Ref)
 * @param meshTetIndices WebIDL type: [PxArray_PxU32] (Ref)
 */
fun PxTetrahedronMeshDesc(meshVertices: PxArray_PxVec3, meshTetIndices: PxArray_PxU32, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("new _module.PxTetrahedronMeshDesc(meshVertices, meshTetIndices)")

/**
 * @param meshVertices   WebIDL type: [PxArray_PxVec3] (Ref)
 * @param meshTetIndices WebIDL type: [PxArray_PxU32] (Ref)
 * @param meshFormat     WebIDL type: [PxTetrahedronMeshFormatEnum] (enum)
 */
fun PxTetrahedronMeshDesc(meshVertices: PxArray_PxVec3, meshTetIndices: PxArray_PxU32, meshFormat: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("new _module.PxTetrahedronMeshDesc(meshVertices, meshTetIndices, meshFormat)")

/**
 * @param meshVertices              WebIDL type: [PxArray_PxVec3] (Ref)
 * @param meshTetIndices            WebIDL type: [PxArray_PxU32] (Ref)
 * @param meshFormat                WebIDL type: [PxTetrahedronMeshFormatEnum] (enum)
 * @param numberOfTetsPerHexElement WebIDL type: unsigned short
 */
fun PxTetrahedronMeshDesc(meshVertices: PxArray_PxVec3, meshTetIndices: PxArray_PxU32, meshFormat: Int, numberOfTetsPerHexElement: Short, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("new _module.PxTetrahedronMeshDesc(meshVertices, meshTetIndices, meshFormat, numberOfTetsPerHexElement)")

fun PxTetrahedronMeshDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshDesc)")

external interface PxTetrahedronMesh : JsAny, PxRefCounted {
    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbVertices(): Int

    /**
     * @return WebIDL type: [PxVec3] (Const)
     */
    fun getVertices(): PxVec3

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTetrahedrons(): Int

    /**
     * @return WebIDL type: VoidPtr (Const)
     */
    fun getTetrahedrons(): JsAny

    /**
     * @return WebIDL type: [PxTetrahedronMeshFlags] (Value)
     */
    fun getTetrahedronMeshFlags(): PxTetrahedronMeshFlags

    /**
     * @return WebIDL type: [PxU32ConstPtr] (Value)
     */
    fun getTetrahedraRemap(): PxU32ConstPtr

    /**
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getLocalBounds(): PxBounds3

}

fun PxTetrahedronMeshFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMesh = js("_module.wrapPointer(ptr, _module.PxTetrahedronMesh)")

val PxTetrahedronMesh.nbVertices
    get() = getNbVertices()
val PxTetrahedronMesh.vertices
    get() = getVertices()
val PxTetrahedronMesh.nbTetrahedrons
    get() = getNbTetrahedrons()
val PxTetrahedronMesh.tetrahedrons
    get() = getTetrahedrons()
val PxTetrahedronMesh.tetrahedronMeshFlags
    get() = getTetrahedronMeshFlags()
val PxTetrahedronMesh.tetrahedraRemap
    get() = getTetrahedraRemap()
val PxTetrahedronMesh.localBounds
    get() = getLocalBounds()

external interface PxTetrahedronMeshExt : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param mesh      WebIDL type: [PxTetrahedronMesh] (Const)
     * @param point     WebIDL type: [PxVec3] (Const, Ref)
     * @param bary      WebIDL type: [PxVec4] (Ref)
     * @param tolerance WebIDL type: float
     * @return WebIDL type: long
     */
    fun findTetrahedronContainingPoint(mesh: PxTetrahedronMesh, point: PxVec3, bary: PxVec4, tolerance: Float): Int

    /**
     * @param mesh  WebIDL type: [PxTetrahedronMesh] (Const)
     * @param point WebIDL type: [PxVec3] (Const, Ref)
     * @param bary  WebIDL type: [PxVec4] (Ref)
     * @return WebIDL type: long
     */
    fun findTetrahedronClosestToPoint(mesh: PxTetrahedronMesh, point: PxVec3, bary: PxVec4): Int

    /**
     * @param tetMeshVertices        WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param tetMeshIndices         WebIDL type: [PxArray_PxU32] (Const, Ref)
     * @param pointsToEmbed          WebIDL type: [PxArray_PxVec3] (Const, Ref)
     * @param barycentricCoordinates WebIDL type: [PxArray_PxVec4] (Ref)
     * @param tetLinks               WebIDL type: [PxArray_PxU32] (Ref)
     */
    fun createPointsToTetrahedronMap(tetMeshVertices: PxArray_PxVec3, tetMeshIndices: PxArray_PxU32, pointsToEmbed: PxArray_PxVec3, barycentricCoordinates: PxArray_PxVec4, tetLinks: PxArray_PxU32)

    /**
     * @param mesh             WebIDL type: [PxTetrahedronMesh] (Const)
     * @param surfaceTriangles WebIDL type: [PxArray_PxU32] (Ref)
     */
    fun extractTetMeshSurface(mesh: PxTetrahedronMesh, surfaceTriangles: PxArray_PxU32)

    /**
     * @param mesh                 WebIDL type: [PxTetrahedronMesh] (Const)
     * @param surfaceTriangles     WebIDL type: [PxArray_PxU32] (Ref)
     * @param surfaceTriangleToTet WebIDL type: [PxArray_PxU32]
     */
    fun extractTetMeshSurface(mesh: PxTetrahedronMesh, surfaceTriangles: PxArray_PxU32, surfaceTriangleToTet: PxArray_PxU32)

    /**
     * @param mesh                    WebIDL type: [PxTetrahedronMesh] (Const)
     * @param surfaceTriangles        WebIDL type: [PxArray_PxU32] (Ref)
     * @param surfaceTriangleToTet    WebIDL type: [PxArray_PxU32]
     * @param flipTriangleOrientation WebIDL type: boolean
     */
    fun extractTetMeshSurface(mesh: PxTetrahedronMesh, surfaceTriangles: PxArray_PxU32, surfaceTriangleToTet: PxArray_PxU32, flipTriangleOrientation: Boolean)

}

fun PxTetrahedronMeshExtFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshExt = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshExt)")

external interface PxTetrahedronMeshFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxTetrahedronMeshFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxTetrahedronMeshFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxTetrahedronMeshFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxTetrahedronMeshFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshFlags = js("new _module.PxTetrahedronMeshFlags(flags)")

fun PxTetrahedronMeshFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshFlags = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshFlags)")

fun PxTetrahedronMeshFlags.isSet(flag: PxTetrahedronMeshFlagEnum) = isSet(flag.value)
fun PxTetrahedronMeshFlags.raise(flag: PxTetrahedronMeshFlagEnum) = raise(flag.value)
fun PxTetrahedronMeshFlags.clear(flag: PxTetrahedronMeshFlagEnum) = clear(flag.value)

external interface PxTetrahedronMeshGeometry : JsAny, PxGeometry {
    /**
     * WebIDL type: [PxTetrahedronMesh]
     */
    var tetrahedronMesh: PxTetrahedronMesh

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

/**
 * @param mesh WebIDL type: [PxTetrahedronMesh]
 */
fun PxTetrahedronMeshGeometry(mesh: PxTetrahedronMesh, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshGeometry = js("new _module.PxTetrahedronMeshGeometry(mesh)")

fun PxTetrahedronMeshGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTetrahedronMeshGeometry = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshGeometry)")

external interface PxTriangle : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param normal WebIDL type: [PxVec3] (Ref)
     */
    fun normal(normal: PxVec3)

    /**
     * @param normal WebIDL type: [PxVec3] (Ref)
     */
    fun denormalizedNormal(normal: PxVec3)

    /**
     * @return WebIDL type: float
     */
    fun area(): Float

    /**
     * @param u WebIDL type: float
     * @param v WebIDL type: float
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun pointFromUV(u: Float, v: Float): PxVec3

}

fun PxTriangle(_module: JsAny = PhysXJsLoader.physXJs): PxTriangle = js("new _module.PxTriangle()")

/**
 * @param p0 WebIDL type: [PxVec3] (Const, Ref)
 * @param p1 WebIDL type: [PxVec3] (Const, Ref)
 * @param p2 WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxTriangle(p0: PxVec3, p1: PxVec3, p2: PxVec3, _module: JsAny = PhysXJsLoader.physXJs): PxTriangle = js("new _module.PxTriangle(p0, p1, p2)")

fun PxTriangleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriangle = js("_module.wrapPointer(ptr, _module.PxTriangle)")

external interface PxTriangleMesh : JsAny, PxRefCounted {
    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbVertices(): Int

    /**
     * @return WebIDL type: [PxVec3] (Const)
     */
    fun getVertices(): PxVec3

    /**
     * @return WebIDL type: [PxVec3]
     */
    fun getVerticesForModification(): PxVec3

    /**
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun refitBVH(): PxBounds3

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTriangles(): Int

    /**
     * @return WebIDL type: VoidPtr (Const)
     */
    fun getTriangles(): JsAny

    /**
     * @return WebIDL type: [PxTriangleMeshFlags] (Value)
     */
    fun getTriangleMeshFlags(): PxTriangleMeshFlags

    /**
     * @return WebIDL type: [PxU32ConstPtr] (Const, Value)
     */
    fun getTrianglesRemap(): PxU32ConstPtr

    /**
     * @param triangleIndex WebIDL type: unsigned long
     * @return WebIDL type: unsigned short
     */
    fun getTriangleMaterialIndex(triangleIndex: Int): Short

    /**
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getLocalBounds(): PxBounds3

}

fun PxTriangleMeshFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMesh = js("_module.wrapPointer(ptr, _module.PxTriangleMesh)")

val PxTriangleMesh.nbVertices
    get() = getNbVertices()
val PxTriangleMesh.vertices
    get() = getVertices()
val PxTriangleMesh.verticesForModification
    get() = getVerticesForModification()
val PxTriangleMesh.nbTriangles
    get() = getNbTriangles()
val PxTriangleMesh.triangles
    get() = getTriangles()
val PxTriangleMesh.triangleMeshFlags
    get() = getTriangleMeshFlags()
val PxTriangleMesh.trianglesRemap
    get() = getTrianglesRemap()
val PxTriangleMesh.localBounds
    get() = getLocalBounds()

external interface PxTriangleMeshAnalysisResults : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxTriangleMeshAnalysisResultEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxTriangleMeshAnalysisResultEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxTriangleMeshAnalysisResultEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxTriangleMeshAnalysisResults(flags: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshAnalysisResults = js("new _module.PxTriangleMeshAnalysisResults(flags)")

fun PxTriangleMeshAnalysisResultsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshAnalysisResults = js("_module.wrapPointer(ptr, _module.PxTriangleMeshAnalysisResults)")

fun PxTriangleMeshAnalysisResults.isSet(flag: PxTriangleMeshAnalysisResultEnum) = isSet(flag.value)
fun PxTriangleMeshAnalysisResults.raise(flag: PxTriangleMeshAnalysisResultEnum) = raise(flag.value)
fun PxTriangleMeshAnalysisResults.clear(flag: PxTriangleMeshAnalysisResultEnum) = clear(flag.value)

external interface PxTriangleMeshFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxTriangleMeshFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxTriangleMeshFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxTriangleMeshFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxTriangleMeshFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshFlags = js("new _module.PxTriangleMeshFlags(flags)")

fun PxTriangleMeshFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshFlags = js("_module.wrapPointer(ptr, _module.PxTriangleMeshFlags)")

fun PxTriangleMeshFlags.isSet(flag: PxTriangleMeshFlagEnum) = isSet(flag.value)
fun PxTriangleMeshFlags.raise(flag: PxTriangleMeshFlagEnum) = raise(flag.value)
fun PxTriangleMeshFlags.clear(flag: PxTriangleMeshFlagEnum) = clear(flag.value)

external interface PxTriangleMeshGeometry : JsAny, DestroyableNative, PxGeometry {
    /**
     * WebIDL type: [PxMeshScale] (Value)
     */
    var scale: PxMeshScale
    /**
     * WebIDL type: [PxMeshGeometryFlags] (Value)
     */
    var meshFlags: PxMeshGeometryFlags
    /**
     * WebIDL type: [PxTriangleMesh]
     */
    var triangleMesh: PxTriangleMesh

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

/**
 * @param mesh WebIDL type: [PxTriangleMesh]
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshGeometry = js("new _module.PxTriangleMeshGeometry(mesh)")

/**
 * @param mesh    WebIDL type: [PxTriangleMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, scaling: PxMeshScale, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshGeometry = js("new _module.PxTriangleMeshGeometry(mesh, scaling)")

/**
 * @param mesh    WebIDL type: [PxTriangleMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 * @param flags   WebIDL type: [PxMeshGeometryFlags] (Ref)
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, scaling: PxMeshScale, flags: PxMeshGeometryFlags, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshGeometry = js("new _module.PxTriangleMeshGeometry(mesh, scaling, flags)")

fun PxTriangleMeshGeometryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshGeometry = js("_module.wrapPointer(ptr, _module.PxTriangleMeshGeometry)")

value class PxConvexCoreTypeEnum private constructor(val value: Int) {
    companion object {
        val ePOINT: PxConvexCoreTypeEnum = PxConvexCoreTypeEnum(PxConvexCoreTypeEnum_ePOINT(PhysXJsLoader.physXJs))
        val eSEGMENT: PxConvexCoreTypeEnum = PxConvexCoreTypeEnum(PxConvexCoreTypeEnum_eSEGMENT(PhysXJsLoader.physXJs))
        val eBOX: PxConvexCoreTypeEnum = PxConvexCoreTypeEnum(PxConvexCoreTypeEnum_eBOX(PhysXJsLoader.physXJs))
        val eELLIPSOID: PxConvexCoreTypeEnum = PxConvexCoreTypeEnum(PxConvexCoreTypeEnum_eELLIPSOID(PhysXJsLoader.physXJs))
        val eCYLINDER: PxConvexCoreTypeEnum = PxConvexCoreTypeEnum(PxConvexCoreTypeEnum_eCYLINDER(PhysXJsLoader.physXJs))
        val eCONE: PxConvexCoreTypeEnum = PxConvexCoreTypeEnum(PxConvexCoreTypeEnum_eCONE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            ePOINT.value -> ePOINT
            eSEGMENT.value -> eSEGMENT
            eBOX.value -> eBOX
            eELLIPSOID.value -> eELLIPSOID
            eCYLINDER.value -> eCYLINDER
            eCONE.value -> eCONE
            else -> error("Invalid enum value $value for enum PxConvexCoreTypeEnum")
        }
    }
}

private fun PxConvexCoreTypeEnum_ePOINT(module: JsAny): Int = js("module._emscripten_enum_PxConvexCoreTypeEnum_ePOINT()")
private fun PxConvexCoreTypeEnum_eSEGMENT(module: JsAny): Int = js("module._emscripten_enum_PxConvexCoreTypeEnum_eSEGMENT()")
private fun PxConvexCoreTypeEnum_eBOX(module: JsAny): Int = js("module._emscripten_enum_PxConvexCoreTypeEnum_eBOX()")
private fun PxConvexCoreTypeEnum_eELLIPSOID(module: JsAny): Int = js("module._emscripten_enum_PxConvexCoreTypeEnum_eELLIPSOID()")
private fun PxConvexCoreTypeEnum_eCYLINDER(module: JsAny): Int = js("module._emscripten_enum_PxConvexCoreTypeEnum_eCYLINDER()")
private fun PxConvexCoreTypeEnum_eCONE(module: JsAny): Int = js("module._emscripten_enum_PxConvexCoreTypeEnum_eCONE()")

value class PxConvexMeshGeometryFlagEnum private constructor(val value: Int) {
    companion object {
        val eTIGHT_BOUNDS: PxConvexMeshGeometryFlagEnum = PxConvexMeshGeometryFlagEnum(PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eTIGHT_BOUNDS.value -> eTIGHT_BOUNDS
            else -> error("Invalid enum value $value for enum PxConvexMeshGeometryFlagEnum")
        }
    }
}

private fun PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS(module: JsAny): Int = js("module._emscripten_enum_PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS()")

value class PxGeometryTypeEnum private constructor(val value: Int) {
    companion object {
        val eSPHERE: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eSPHERE(PhysXJsLoader.physXJs))
        val ePLANE: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_ePLANE(PhysXJsLoader.physXJs))
        val eCAPSULE: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eCAPSULE(PhysXJsLoader.physXJs))
        val eBOX: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eBOX(PhysXJsLoader.physXJs))
        val eCONVEXCORE: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eCONVEXCORE(PhysXJsLoader.physXJs))
        val eCONVEXMESH: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eCONVEXMESH(PhysXJsLoader.physXJs))
        val ePARTICLESYSTEM: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_ePARTICLESYSTEM(PhysXJsLoader.physXJs))
        val eTETRAHEDRONMESH: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eTETRAHEDRONMESH(PhysXJsLoader.physXJs))
        val eTRIANGLEMESH: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eTRIANGLEMESH(PhysXJsLoader.physXJs))
        val eHEIGHTFIELD: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eHEIGHTFIELD(PhysXJsLoader.physXJs))
        val eCUSTOM: PxGeometryTypeEnum = PxGeometryTypeEnum(PxGeometryTypeEnum_eCUSTOM(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eSPHERE.value -> eSPHERE
            ePLANE.value -> ePLANE
            eCAPSULE.value -> eCAPSULE
            eBOX.value -> eBOX
            eCONVEXCORE.value -> eCONVEXCORE
            eCONVEXMESH.value -> eCONVEXMESH
            ePARTICLESYSTEM.value -> ePARTICLESYSTEM
            eTETRAHEDRONMESH.value -> eTETRAHEDRONMESH
            eTRIANGLEMESH.value -> eTRIANGLEMESH
            eHEIGHTFIELD.value -> eHEIGHTFIELD
            eCUSTOM.value -> eCUSTOM
            else -> error("Invalid enum value $value for enum PxGeometryTypeEnum")
        }
    }
}

private fun PxGeometryTypeEnum_eSPHERE(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eSPHERE()")
private fun PxGeometryTypeEnum_ePLANE(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_ePLANE()")
private fun PxGeometryTypeEnum_eCAPSULE(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eCAPSULE()")
private fun PxGeometryTypeEnum_eBOX(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eBOX()")
private fun PxGeometryTypeEnum_eCONVEXCORE(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eCONVEXCORE()")
private fun PxGeometryTypeEnum_eCONVEXMESH(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eCONVEXMESH()")
private fun PxGeometryTypeEnum_ePARTICLESYSTEM(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_ePARTICLESYSTEM()")
private fun PxGeometryTypeEnum_eTETRAHEDRONMESH(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eTETRAHEDRONMESH()")
private fun PxGeometryTypeEnum_eTRIANGLEMESH(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eTRIANGLEMESH()")
private fun PxGeometryTypeEnum_eHEIGHTFIELD(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eHEIGHTFIELD()")
private fun PxGeometryTypeEnum_eCUSTOM(module: JsAny): Int = js("module._emscripten_enum_PxGeometryTypeEnum_eCUSTOM()")

value class PxHeightFieldFlagEnum private constructor(val value: Int) {
    companion object {
        val eNO_BOUNDARY_EDGES: PxHeightFieldFlagEnum = PxHeightFieldFlagEnum(PxHeightFieldFlagEnum_eNO_BOUNDARY_EDGES(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eNO_BOUNDARY_EDGES.value -> eNO_BOUNDARY_EDGES
            else -> error("Invalid enum value $value for enum PxHeightFieldFlagEnum")
        }
    }
}

private fun PxHeightFieldFlagEnum_eNO_BOUNDARY_EDGES(module: JsAny): Int = js("module._emscripten_enum_PxHeightFieldFlagEnum_eNO_BOUNDARY_EDGES()")

value class PxHeightFieldFormatEnum private constructor(val value: Int) {
    companion object {
        val eS16_TM: PxHeightFieldFormatEnum = PxHeightFieldFormatEnum(PxHeightFieldFormatEnum_eS16_TM(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eS16_TM.value -> eS16_TM
            else -> error("Invalid enum value $value for enum PxHeightFieldFormatEnum")
        }
    }
}

private fun PxHeightFieldFormatEnum_eS16_TM(module: JsAny): Int = js("module._emscripten_enum_PxHeightFieldFormatEnum_eS16_TM()")

value class PxMeshFlagEnum private constructor(val value: Int) {
    companion object {
        val eFLIPNORMALS: PxMeshFlagEnum = PxMeshFlagEnum(PxMeshFlagEnum_eFLIPNORMALS(PhysXJsLoader.physXJs))
        val e16_BIT_INDICES: PxMeshFlagEnum = PxMeshFlagEnum(PxMeshFlagEnum_e16_BIT_INDICES(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eFLIPNORMALS.value -> eFLIPNORMALS
            e16_BIT_INDICES.value -> e16_BIT_INDICES
            else -> error("Invalid enum value $value for enum PxMeshFlagEnum")
        }
    }
}

private fun PxMeshFlagEnum_eFLIPNORMALS(module: JsAny): Int = js("module._emscripten_enum_PxMeshFlagEnum_eFLIPNORMALS()")
private fun PxMeshFlagEnum_e16_BIT_INDICES(module: JsAny): Int = js("module._emscripten_enum_PxMeshFlagEnum_e16_BIT_INDICES()")

value class PxMeshGeometryFlagEnum private constructor(val value: Int) {
    companion object {
        val eDOUBLE_SIDED: PxMeshGeometryFlagEnum = PxMeshGeometryFlagEnum(PxMeshGeometryFlagEnum_eDOUBLE_SIDED(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eDOUBLE_SIDED.value -> eDOUBLE_SIDED
            else -> error("Invalid enum value $value for enum PxMeshGeometryFlagEnum")
        }
    }
}

private fun PxMeshGeometryFlagEnum_eDOUBLE_SIDED(module: JsAny): Int = js("module._emscripten_enum_PxMeshGeometryFlagEnum_eDOUBLE_SIDED()")

value class PxTetrahedronMeshAnalysisResultEnum private constructor(val value: Int) {
    companion object {
        val eVALID: PxTetrahedronMeshAnalysisResultEnum = PxTetrahedronMeshAnalysisResultEnum(PxTetrahedronMeshAnalysisResultEnum_eVALID(PhysXJsLoader.physXJs))
        val eDEGENERATE_TETRAHEDRON: PxTetrahedronMeshAnalysisResultEnum = PxTetrahedronMeshAnalysisResultEnum(PxTetrahedronMeshAnalysisResultEnum_eDEGENERATE_TETRAHEDRON(PhysXJsLoader.physXJs))
        val eMESH_IS_PROBLEMATIC: PxTetrahedronMeshAnalysisResultEnum = PxTetrahedronMeshAnalysisResultEnum(PxTetrahedronMeshAnalysisResultEnum_eMESH_IS_PROBLEMATIC(PhysXJsLoader.physXJs))
        val eMESH_IS_INVALID: PxTetrahedronMeshAnalysisResultEnum = PxTetrahedronMeshAnalysisResultEnum(PxTetrahedronMeshAnalysisResultEnum_eMESH_IS_INVALID(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eVALID.value -> eVALID
            eDEGENERATE_TETRAHEDRON.value -> eDEGENERATE_TETRAHEDRON
            eMESH_IS_PROBLEMATIC.value -> eMESH_IS_PROBLEMATIC
            eMESH_IS_INVALID.value -> eMESH_IS_INVALID
            else -> error("Invalid enum value $value for enum PxTetrahedronMeshAnalysisResultEnum")
        }
    }
}

private fun PxTetrahedronMeshAnalysisResultEnum_eVALID(module: JsAny): Int = js("module._emscripten_enum_PxTetrahedronMeshAnalysisResultEnum_eVALID()")
private fun PxTetrahedronMeshAnalysisResultEnum_eDEGENERATE_TETRAHEDRON(module: JsAny): Int = js("module._emscripten_enum_PxTetrahedronMeshAnalysisResultEnum_eDEGENERATE_TETRAHEDRON()")
private fun PxTetrahedronMeshAnalysisResultEnum_eMESH_IS_PROBLEMATIC(module: JsAny): Int = js("module._emscripten_enum_PxTetrahedronMeshAnalysisResultEnum_eMESH_IS_PROBLEMATIC()")
private fun PxTetrahedronMeshAnalysisResultEnum_eMESH_IS_INVALID(module: JsAny): Int = js("module._emscripten_enum_PxTetrahedronMeshAnalysisResultEnum_eMESH_IS_INVALID()")

value class PxTetrahedronMeshFlagEnum private constructor(val value: Int) {
    companion object {
        val e16_BIT_INDICES: PxTetrahedronMeshFlagEnum = PxTetrahedronMeshFlagEnum(PxTetrahedronMeshFlagEnum_e16_BIT_INDICES(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            e16_BIT_INDICES.value -> e16_BIT_INDICES
            else -> error("Invalid enum value $value for enum PxTetrahedronMeshFlagEnum")
        }
    }
}

private fun PxTetrahedronMeshFlagEnum_e16_BIT_INDICES(module: JsAny): Int = js("module._emscripten_enum_PxTetrahedronMeshFlagEnum_e16_BIT_INDICES()")

value class PxTetrahedronMeshFormatEnum private constructor(val value: Int) {
    companion object {
        val eTET_MESH: PxTetrahedronMeshFormatEnum = PxTetrahedronMeshFormatEnum(PxTetrahedronMeshFormatEnum_eTET_MESH(PhysXJsLoader.physXJs))
        val eHEX_MESH: PxTetrahedronMeshFormatEnum = PxTetrahedronMeshFormatEnum(PxTetrahedronMeshFormatEnum_eHEX_MESH(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eTET_MESH.value -> eTET_MESH
            eHEX_MESH.value -> eHEX_MESH
            else -> error("Invalid enum value $value for enum PxTetrahedronMeshFormatEnum")
        }
    }
}

private fun PxTetrahedronMeshFormatEnum_eTET_MESH(module: JsAny): Int = js("module._emscripten_enum_PxTetrahedronMeshFormatEnum_eTET_MESH()")
private fun PxTetrahedronMeshFormatEnum_eHEX_MESH(module: JsAny): Int = js("module._emscripten_enum_PxTetrahedronMeshFormatEnum_eHEX_MESH()")

value class PxTriangleMeshAnalysisResultEnum private constructor(val value: Int) {
    companion object {
        val eVALID: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eVALID(PhysXJsLoader.physXJs))
        val eZERO_VOLUME: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eZERO_VOLUME(PhysXJsLoader.physXJs))
        val eOPEN_BOUNDARIES: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eOPEN_BOUNDARIES(PhysXJsLoader.physXJs))
        val eSELF_INTERSECTIONS: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eSELF_INTERSECTIONS(PhysXJsLoader.physXJs))
        val eINCONSISTENT_TRIANGLE_ORIENTATION: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eINCONSISTENT_TRIANGLE_ORIENTATION(PhysXJsLoader.physXJs))
        val eCONTAINS_ACUTE_ANGLED_TRIANGLES: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eCONTAINS_ACUTE_ANGLED_TRIANGLES(PhysXJsLoader.physXJs))
        val eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES(PhysXJsLoader.physXJs))
        val eCONTAINS_DUPLICATE_POINTS: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eCONTAINS_DUPLICATE_POINTS(PhysXJsLoader.physXJs))
        val eCONTAINS_INVALID_POINTS: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eCONTAINS_INVALID_POINTS(PhysXJsLoader.physXJs))
        val eREQUIRES_32BIT_INDEX_BUFFER: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eREQUIRES_32BIT_INDEX_BUFFER(PhysXJsLoader.physXJs))
        val eTRIANGLE_INDEX_OUT_OF_RANGE: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eTRIANGLE_INDEX_OUT_OF_RANGE(PhysXJsLoader.physXJs))
        val eMESH_IS_PROBLEMATIC: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eMESH_IS_PROBLEMATIC(PhysXJsLoader.physXJs))
        val eMESH_IS_INVALID: PxTriangleMeshAnalysisResultEnum = PxTriangleMeshAnalysisResultEnum(PxTriangleMeshAnalysisResultEnum_eMESH_IS_INVALID(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eVALID.value -> eVALID
            eZERO_VOLUME.value -> eZERO_VOLUME
            eOPEN_BOUNDARIES.value -> eOPEN_BOUNDARIES
            eSELF_INTERSECTIONS.value -> eSELF_INTERSECTIONS
            eINCONSISTENT_TRIANGLE_ORIENTATION.value -> eINCONSISTENT_TRIANGLE_ORIENTATION
            eCONTAINS_ACUTE_ANGLED_TRIANGLES.value -> eCONTAINS_ACUTE_ANGLED_TRIANGLES
            eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES.value -> eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES
            eCONTAINS_DUPLICATE_POINTS.value -> eCONTAINS_DUPLICATE_POINTS
            eCONTAINS_INVALID_POINTS.value -> eCONTAINS_INVALID_POINTS
            eREQUIRES_32BIT_INDEX_BUFFER.value -> eREQUIRES_32BIT_INDEX_BUFFER
            eTRIANGLE_INDEX_OUT_OF_RANGE.value -> eTRIANGLE_INDEX_OUT_OF_RANGE
            eMESH_IS_PROBLEMATIC.value -> eMESH_IS_PROBLEMATIC
            eMESH_IS_INVALID.value -> eMESH_IS_INVALID
            else -> error("Invalid enum value $value for enum PxTriangleMeshAnalysisResultEnum")
        }
    }
}

private fun PxTriangleMeshAnalysisResultEnum_eVALID(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eVALID()")
private fun PxTriangleMeshAnalysisResultEnum_eZERO_VOLUME(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eZERO_VOLUME()")
private fun PxTriangleMeshAnalysisResultEnum_eOPEN_BOUNDARIES(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eOPEN_BOUNDARIES()")
private fun PxTriangleMeshAnalysisResultEnum_eSELF_INTERSECTIONS(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eSELF_INTERSECTIONS()")
private fun PxTriangleMeshAnalysisResultEnum_eINCONSISTENT_TRIANGLE_ORIENTATION(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eINCONSISTENT_TRIANGLE_ORIENTATION()")
private fun PxTriangleMeshAnalysisResultEnum_eCONTAINS_ACUTE_ANGLED_TRIANGLES(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eCONTAINS_ACUTE_ANGLED_TRIANGLES()")
private fun PxTriangleMeshAnalysisResultEnum_eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES()")
private fun PxTriangleMeshAnalysisResultEnum_eCONTAINS_DUPLICATE_POINTS(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eCONTAINS_DUPLICATE_POINTS()")
private fun PxTriangleMeshAnalysisResultEnum_eCONTAINS_INVALID_POINTS(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eCONTAINS_INVALID_POINTS()")
private fun PxTriangleMeshAnalysisResultEnum_eREQUIRES_32BIT_INDEX_BUFFER(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eREQUIRES_32BIT_INDEX_BUFFER()")
private fun PxTriangleMeshAnalysisResultEnum_eTRIANGLE_INDEX_OUT_OF_RANGE(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eTRIANGLE_INDEX_OUT_OF_RANGE()")
private fun PxTriangleMeshAnalysisResultEnum_eMESH_IS_PROBLEMATIC(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eMESH_IS_PROBLEMATIC()")
private fun PxTriangleMeshAnalysisResultEnum_eMESH_IS_INVALID(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eMESH_IS_INVALID()")

value class PxTriangleMeshFlagEnum private constructor(val value: Int) {
    companion object {
        val e16_BIT_INDICES: PxTriangleMeshFlagEnum = PxTriangleMeshFlagEnum(PxTriangleMeshFlagEnum_e16_BIT_INDICES(PhysXJsLoader.physXJs))
        val eADJACENCY_INFO: PxTriangleMeshFlagEnum = PxTriangleMeshFlagEnum(PxTriangleMeshFlagEnum_eADJACENCY_INFO(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            e16_BIT_INDICES.value -> e16_BIT_INDICES
            eADJACENCY_INFO.value -> eADJACENCY_INFO
            else -> error("Invalid enum value $value for enum PxTriangleMeshFlagEnum")
        }
    }
}

private fun PxTriangleMeshFlagEnum_e16_BIT_INDICES(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshFlagEnum_e16_BIT_INDICES()")
private fun PxTriangleMeshFlagEnum_eADJACENCY_INFO(module: JsAny): Int = js("module._emscripten_enum_PxTriangleMeshFlagEnum_eADJACENCY_INFO()")

