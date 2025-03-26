/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_PARAMETER", "unused")

package physx

external interface PxBoxGeometry : PxGeometry {
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
fun PxBoxGeometry(hx: Float, hy: Float, hz: Float, _module: dynamic = PhysXJsLoader.physXJs): PxBoxGeometry = js("new _module.PxBoxGeometry(hx, hy, hz)")

fun PxBoxGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBoxGeometry = js("_module.wrapPointer(ptr, _module.PxBoxGeometry)")

fun PxBoxGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBVH : PxBase

fun PxBVHFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBVH = js("_module.wrapPointer(ptr, _module.PxBVH)")

external interface PxCapsuleGeometry : PxGeometry {
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
fun PxCapsuleGeometry(radius: Float, halfHeight: Float, _module: dynamic = PhysXJsLoader.physXJs): PxCapsuleGeometry = js("new _module.PxCapsuleGeometry(radius, halfHeight)")

fun PxCapsuleGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxCapsuleGeometry = js("_module.wrapPointer(ptr, _module.PxCapsuleGeometry)")

fun PxCapsuleGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactBuffer {
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

fun PxContactBufferFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxContactBuffer = js("_module.wrapPointer(ptr, _module.PxContactBuffer)")

fun PxContactBuffer.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPoint {
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

fun PxContactPoint(_module: dynamic = PhysXJsLoader.physXJs): PxContactPoint = js("new _module.PxContactPoint()")

fun PxContactPointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxContactPoint = js("_module.wrapPointer(ptr, _module.PxContactPoint)")

fun PxContactPoint.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConvexMesh : PxRefCounted {
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

fun PxConvexMeshFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConvexMesh = js("_module.wrapPointer(ptr, _module.PxConvexMesh)")

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

external interface PxConvexMeshGeometry : PxGeometry {
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
fun PxConvexMeshGeometry(mesh: PxConvexMesh, _module: dynamic = PhysXJsLoader.physXJs): PxConvexMeshGeometry = js("new _module.PxConvexMeshGeometry(mesh)")

/**
 * @param mesh    WebIDL type: [PxConvexMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, _module: dynamic = PhysXJsLoader.physXJs): PxConvexMeshGeometry = js("new _module.PxConvexMeshGeometry(mesh, scaling)")

/**
 * @param mesh    WebIDL type: [PxConvexMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 * @param flags   WebIDL type: [PxConvexMeshGeometryFlags] (Ref)
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags, _module: dynamic = PhysXJsLoader.physXJs): PxConvexMeshGeometry = js("new _module.PxConvexMeshGeometry(mesh, scaling, flags)")

fun PxConvexMeshGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConvexMeshGeometry = js("_module.wrapPointer(ptr, _module.PxConvexMeshGeometry)")

fun PxConvexMeshGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConvexMeshGeometryFlags {
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
fun PxConvexMeshGeometryFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxConvexMeshGeometryFlags = js("new _module.PxConvexMeshGeometryFlags(flags)")

fun PxConvexMeshGeometryFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConvexMeshGeometryFlags = js("_module.wrapPointer(ptr, _module.PxConvexMeshGeometryFlags)")

fun PxConvexMeshGeometryFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGeometry {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: [PxGeometryTypeEnum] (enum)
     */
    fun getType(): Int

}

fun PxGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGeometry = js("_module.wrapPointer(ptr, _module.PxGeometry)")

fun PxGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxGeometry.type
    get() = getType()

external interface PxGeometryHolder {
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

fun PxGeometryHolder(_module: dynamic = PhysXJsLoader.physXJs): PxGeometryHolder = js("new _module.PxGeometryHolder()")

/**
 * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
 */
fun PxGeometryHolder(geometry: PxGeometry, _module: dynamic = PhysXJsLoader.physXJs): PxGeometryHolder = js("new _module.PxGeometryHolder(geometry)")

fun PxGeometryHolderFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGeometryHolder = js("_module.wrapPointer(ptr, _module.PxGeometryHolder)")

fun PxGeometryHolder.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxGeometryHolder.type
    get() = getType()

external interface PxGeometryQuery {
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

fun PxGeometryQueryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGeometryQuery = js("_module.wrapPointer(ptr, _module.PxGeometryQuery)")

fun PxGeometryQuery.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHeightField : PxRefCounted {
    /**
     * @param destBuffer     WebIDL type: VoidPtr
     * @param destBufferSize WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun saveCells(destBuffer: Any, destBufferSize: Int): Int

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

fun PxHeightFieldFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxHeightField = js("_module.wrapPointer(ptr, _module.PxHeightField)")

val PxHeightField.nbRows
    get() = getNbRows()
val PxHeightField.nbColumns
    get() = getNbColumns()
val PxHeightField.format
    get() = getFormat()
val PxHeightField.sampleStride
    get() = getSampleStride()
val PxHeightField.convexEdgeThreshold
    get() = getConvexEdgeThreshold()
val PxHeightField.flags
    get() = getFlags()
val PxHeightField.timestamp
    get() = getTimestamp()

external interface PxHeightFieldDesc {
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

fun PxHeightFieldDesc(_module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldDesc = js("new _module.PxHeightFieldDesc()")

fun PxHeightFieldDescFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldDesc = js("_module.wrapPointer(ptr, _module.PxHeightFieldDesc)")

fun PxHeightFieldDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHeightFieldFlags {
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
fun PxHeightFieldFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldFlags = js("new _module.PxHeightFieldFlags(flags)")

fun PxHeightFieldFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldFlags = js("_module.wrapPointer(ptr, _module.PxHeightFieldFlags)")

fun PxHeightFieldFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHeightFieldGeometry : PxGeometry {
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

fun PxHeightFieldGeometry(_module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldGeometry = js("new _module.PxHeightFieldGeometry()")

/**
 * @param hf          WebIDL type: [PxHeightField]
 * @param flags       WebIDL type: [PxMeshGeometryFlags] (Ref)
 * @param heightScale WebIDL type: float
 * @param rowScale    WebIDL type: float
 * @param columnScale WebIDL type: float
 */
fun PxHeightFieldGeometry(hf: PxHeightField, flags: PxMeshGeometryFlags, heightScale: Float, rowScale: Float, columnScale: Float, _module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldGeometry = js("new _module.PxHeightFieldGeometry(hf, flags, heightScale, rowScale, columnScale)")

fun PxHeightFieldGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldGeometry = js("_module.wrapPointer(ptr, _module.PxHeightFieldGeometry)")

fun PxHeightFieldGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHeightFieldSample {
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

fun PxHeightFieldSample(_module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldSample = js("new _module.PxHeightFieldSample()")

fun PxHeightFieldSampleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxHeightFieldSample = js("_module.wrapPointer(ptr, _module.PxHeightFieldSample)")

fun PxHeightFieldSample.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHullPolygon {
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

fun PxHullPolygon(_module: dynamic = PhysXJsLoader.physXJs): PxHullPolygon = js("new _module.PxHullPolygon()")

fun PxHullPolygonFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxHullPolygon = js("_module.wrapPointer(ptr, _module.PxHullPolygon)")

fun PxHullPolygon.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshFlags {
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
fun PxMeshFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxMeshFlags = js("new _module.PxMeshFlags(flags)")

fun PxMeshFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMeshFlags = js("_module.wrapPointer(ptr, _module.PxMeshFlags)")

fun PxMeshFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshGeometryFlags {
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
fun PxMeshGeometryFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxMeshGeometryFlags = js("new _module.PxMeshGeometryFlags(flags)")

fun PxMeshGeometryFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMeshGeometryFlags = js("_module.wrapPointer(ptr, _module.PxMeshGeometryFlags)")

fun PxMeshGeometryFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshScale

fun PxMeshScale(_module: dynamic = PhysXJsLoader.physXJs): PxMeshScale = js("new _module.PxMeshScale()")

/**
 * @param r WebIDL type: float
 */
fun PxMeshScale(r: Float, _module: dynamic = PhysXJsLoader.physXJs): PxMeshScale = js("new _module.PxMeshScale(r)")

/**
 * @param s WebIDL type: [PxVec3] (Const, Ref)
 * @param r WebIDL type: [PxQuat] (Const, Ref)
 */
fun PxMeshScale(s: PxVec3, r: PxQuat, _module: dynamic = PhysXJsLoader.physXJs): PxMeshScale = js("new _module.PxMeshScale(s, r)")

fun PxMeshScaleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMeshScale = js("_module.wrapPointer(ptr, _module.PxMeshScale)")

fun PxMeshScale.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPlaneGeometry : PxGeometry

fun PxPlaneGeometry(_module: dynamic = PhysXJsLoader.physXJs): PxPlaneGeometry = js("new _module.PxPlaneGeometry()")

fun PxPlaneGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxPlaneGeometry = js("_module.wrapPointer(ptr, _module.PxPlaneGeometry)")

fun PxPlaneGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSimpleTriangleMesh {
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

fun PxSimpleTriangleMesh(_module: dynamic = PhysXJsLoader.physXJs): PxSimpleTriangleMesh = js("new _module.PxSimpleTriangleMesh()")

fun PxSimpleTriangleMeshFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSimpleTriangleMesh = js("_module.wrapPointer(ptr, _module.PxSimpleTriangleMesh)")

fun PxSimpleTriangleMesh.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSphereGeometry : PxGeometry {
    /**
     * WebIDL type: float
     */
    var radius: Float
}

/**
 * @param ir WebIDL type: float
 */
fun PxSphereGeometry(ir: Float, _module: dynamic = PhysXJsLoader.physXJs): PxSphereGeometry = js("new _module.PxSphereGeometry(ir)")

fun PxSphereGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSphereGeometry = js("_module.wrapPointer(ptr, _module.PxSphereGeometry)")

fun PxSphereGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTetMaker {
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

fun PxTetMakerFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetMaker = js("_module.wrapPointer(ptr, _module.PxTetMaker)")

external interface PxTetrahedronMeshAnalysisResults {
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
fun PxTetrahedronMeshAnalysisResults(flags: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshAnalysisResults = js("new _module.PxTetrahedronMeshAnalysisResults(flags)")

fun PxTetrahedronMeshAnalysisResultsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshAnalysisResults = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshAnalysisResults)")

fun PxTetrahedronMeshAnalysisResults.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTetrahedronMeshDesc {
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

fun PxTetrahedronMeshDesc(_module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("new _module.PxTetrahedronMeshDesc()")

/**
 * @param meshVertices   WebIDL type: [PxArray_PxVec3] (Ref)
 * @param meshTetIndices WebIDL type: [PxArray_PxU32] (Ref)
 */
fun PxTetrahedronMeshDesc(meshVertices: PxArray_PxVec3, meshTetIndices: PxArray_PxU32, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("new _module.PxTetrahedronMeshDesc(meshVertices, meshTetIndices)")

/**
 * @param meshVertices   WebIDL type: [PxArray_PxVec3] (Ref)
 * @param meshTetIndices WebIDL type: [PxArray_PxU32] (Ref)
 * @param meshFormat     WebIDL type: [PxTetrahedronMeshFormatEnum] (enum)
 */
fun PxTetrahedronMeshDesc(meshVertices: PxArray_PxVec3, meshTetIndices: PxArray_PxU32, meshFormat: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("new _module.PxTetrahedronMeshDesc(meshVertices, meshTetIndices, meshFormat)")

/**
 * @param meshVertices              WebIDL type: [PxArray_PxVec3] (Ref)
 * @param meshTetIndices            WebIDL type: [PxArray_PxU32] (Ref)
 * @param meshFormat                WebIDL type: [PxTetrahedronMeshFormatEnum] (enum)
 * @param numberOfTetsPerHexElement WebIDL type: unsigned short
 */
fun PxTetrahedronMeshDesc(meshVertices: PxArray_PxVec3, meshTetIndices: PxArray_PxU32, meshFormat: Int, numberOfTetsPerHexElement: Short, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("new _module.PxTetrahedronMeshDesc(meshVertices, meshTetIndices, meshFormat, numberOfTetsPerHexElement)")

fun PxTetrahedronMeshDescFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshDesc = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshDesc)")

fun PxTetrahedronMeshDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTetrahedronMesh : PxRefCounted {
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
    fun getTetrahedrons(): Any

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

fun PxTetrahedronMeshFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMesh = js("_module.wrapPointer(ptr, _module.PxTetrahedronMesh)")

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

external interface PxTetrahedronMeshExt {
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

fun PxTetrahedronMeshExtFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshExt = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshExt)")

external interface PxTetrahedronMeshFlags {
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
fun PxTetrahedronMeshFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshFlags = js("new _module.PxTetrahedronMeshFlags(flags)")

fun PxTetrahedronMeshFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshFlags = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshFlags)")

fun PxTetrahedronMeshFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTetrahedronMeshGeometry : PxGeometry {
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
fun PxTetrahedronMeshGeometry(mesh: PxTetrahedronMesh, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshGeometry = js("new _module.PxTetrahedronMeshGeometry(mesh)")

fun PxTetrahedronMeshGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTetrahedronMeshGeometry = js("_module.wrapPointer(ptr, _module.PxTetrahedronMeshGeometry)")

external interface PxTriangle {
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

fun PxTriangle(_module: dynamic = PhysXJsLoader.physXJs): PxTriangle = js("new _module.PxTriangle()")

/**
 * @param p0 WebIDL type: [PxVec3] (Const, Ref)
 * @param p1 WebIDL type: [PxVec3] (Const, Ref)
 * @param p2 WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxTriangle(p0: PxVec3, p1: PxVec3, p2: PxVec3, _module: dynamic = PhysXJsLoader.physXJs): PxTriangle = js("new _module.PxTriangle(p0, p1, p2)")

fun PxTriangleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriangle = js("_module.wrapPointer(ptr, _module.PxTriangle)")

fun PxTriangle.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTriangleMesh : PxRefCounted {
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
    fun getTriangles(): Any

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

fun PxTriangleMeshFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMesh = js("_module.wrapPointer(ptr, _module.PxTriangleMesh)")

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

external interface PxTriangleMeshAnalysisResults {
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
fun PxTriangleMeshAnalysisResults(flags: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshAnalysisResults = js("new _module.PxTriangleMeshAnalysisResults(flags)")

fun PxTriangleMeshAnalysisResultsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshAnalysisResults = js("_module.wrapPointer(ptr, _module.PxTriangleMeshAnalysisResults)")

fun PxTriangleMeshAnalysisResults.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTriangleMeshFlags {
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
fun PxTriangleMeshFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshFlags = js("new _module.PxTriangleMeshFlags(flags)")

fun PxTriangleMeshFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshFlags = js("_module.wrapPointer(ptr, _module.PxTriangleMeshFlags)")

fun PxTriangleMeshFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTriangleMeshGeometry : PxGeometry {
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
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshGeometry = js("new _module.PxTriangleMeshGeometry(mesh)")

/**
 * @param mesh    WebIDL type: [PxTriangleMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, scaling: PxMeshScale, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshGeometry = js("new _module.PxTriangleMeshGeometry(mesh, scaling)")

/**
 * @param mesh    WebIDL type: [PxTriangleMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 * @param flags   WebIDL type: [PxMeshGeometryFlags] (Ref)
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, scaling: PxMeshScale, flags: PxMeshGeometryFlags, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshGeometry = js("new _module.PxTriangleMeshGeometry(mesh, scaling, flags)")

fun PxTriangleMeshGeometryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshGeometry = js("_module.wrapPointer(ptr, _module.PxTriangleMeshGeometry)")

fun PxTriangleMeshGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

object PxConvexMeshGeometryFlagEnum {
    val eTIGHT_BOUNDS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS()
}

object PxGeometryTypeEnum {
    val eSPHERE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxGeometryTypeEnum_eSPHERE()
    val ePLANE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxGeometryTypeEnum_ePLANE()
    val eCAPSULE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxGeometryTypeEnum_eCAPSULE()
    val eBOX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxGeometryTypeEnum_eBOX()
    val eCONVEXMESH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxGeometryTypeEnum_eCONVEXMESH()
    val eTRIANGLEMESH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxGeometryTypeEnum_eTRIANGLEMESH()
    val eHEIGHTFIELD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxGeometryTypeEnum_eHEIGHTFIELD()
    val eCUSTOM: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxGeometryTypeEnum_eCUSTOM()
}

object PxHeightFieldFlagEnum {
    val eNO_BOUNDARY_EDGES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHeightFieldFlagEnum_eNO_BOUNDARY_EDGES()
}

object PxHeightFieldFormatEnum {
    val eS16_TM: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHeightFieldFormatEnum_eS16_TM()
}

object PxMeshFlagEnum {
    val eFLIPNORMALS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshFlagEnum_eFLIPNORMALS()
    val e16_BIT_INDICES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshFlagEnum_e16_BIT_INDICES()
}

object PxMeshGeometryFlagEnum {
    val eDOUBLE_SIDED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshGeometryFlagEnum_eDOUBLE_SIDED()
}

object PxTetrahedronMeshAnalysisResultEnum {
    val eVALID: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTetrahedronMeshAnalysisResultEnum_eVALID()
    val eDEGENERATE_TETRAHEDRON: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTetrahedronMeshAnalysisResultEnum_eDEGENERATE_TETRAHEDRON()
    val eMESH_IS_PROBLEMATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTetrahedronMeshAnalysisResultEnum_eMESH_IS_PROBLEMATIC()
    val eMESH_IS_INVALID: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTetrahedronMeshAnalysisResultEnum_eMESH_IS_INVALID()
}

object PxTetrahedronMeshFlagEnum {
    val e16_BIT_INDICES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTetrahedronMeshFlagEnum_e16_BIT_INDICES()
}

object PxTetrahedronMeshFormatEnum {
    val eTET_MESH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTetrahedronMeshFormatEnum_eTET_MESH()
    val eHEX_MESH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTetrahedronMeshFormatEnum_eHEX_MESH()
}

object PxTriangleMeshAnalysisResultEnum {
    val eVALID: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eVALID()
    val eZERO_VOLUME: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eZERO_VOLUME()
    val eOPEN_BOUNDARIES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eOPEN_BOUNDARIES()
    val eSELF_INTERSECTIONS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eSELF_INTERSECTIONS()
    val eINCONSISTENT_TRIANGLE_ORIENTATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eINCONSISTENT_TRIANGLE_ORIENTATION()
    val eCONTAINS_ACUTE_ANGLED_TRIANGLES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eCONTAINS_ACUTE_ANGLED_TRIANGLES()
    val eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eEDGE_SHARED_BY_MORE_THAN_TWO_TRIANGLES()
    val eCONTAINS_DUPLICATE_POINTS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eCONTAINS_DUPLICATE_POINTS()
    val eCONTAINS_INVALID_POINTS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eCONTAINS_INVALID_POINTS()
    val eREQUIRES_32BIT_INDEX_BUFFER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eREQUIRES_32BIT_INDEX_BUFFER()
    val eTRIANGLE_INDEX_OUT_OF_RANGE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eTRIANGLE_INDEX_OUT_OF_RANGE()
    val eMESH_IS_PROBLEMATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eMESH_IS_PROBLEMATIC()
    val eMESH_IS_INVALID: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshAnalysisResultEnum_eMESH_IS_INVALID()
}

object PxTriangleMeshFlagEnum {
    val e16_BIT_INDICES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshFlagEnum_e16_BIT_INDICES()
    val eADJACENCY_INFO: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshFlagEnum_eADJACENCY_INFO()
}

