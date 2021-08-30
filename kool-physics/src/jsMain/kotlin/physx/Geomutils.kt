/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxBoxGeometry : PxGeometry

/**
 * @param hx WebIDL type: float
 * @param hy WebIDL type: float
 * @param hz WebIDL type: float
 */
fun PxBoxGeometry(hx: Float, hy: Float, hz: Float): PxBoxGeometry {
    fun _PxBoxGeometry(_module: dynamic, hx: Float, hy: Float, hz: Float) = js("new _module.PxBoxGeometry(hx, hy, hz)")
    return _PxBoxGeometry(PhysXJsLoader.physXJs, hx, hy, hz)
}

fun PxBoxGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBVHStructure : PxBase

external interface PxCapsuleGeometry : PxGeometry

/**
 * @param radius     WebIDL type: float
 * @param halfHeight WebIDL type: float
 */
fun PxCapsuleGeometry(radius: Float, halfHeight: Float): PxCapsuleGeometry {
    fun _PxCapsuleGeometry(_module: dynamic, radius: Float, halfHeight: Float) = js("new _module.PxCapsuleGeometry(radius, halfHeight)")
    return _PxCapsuleGeometry(PhysXJsLoader.physXJs, radius, halfHeight)
}

fun PxCapsuleGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConvexMesh : PxBase {
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
     * @return WebIDL type: unsigned long
     */
    fun getReferenceCount(): Int

    fun acquireReference()

    /**
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getLocalBounds(): PxBounds3

    /**
     * @return WebIDL type: boolean
     */
    fun isGpuCompatible(): Boolean

}

val PxConvexMesh.nbVertices
    get() = getNbVertices()
val PxConvexMesh.vertices
    get() = getVertices()
val PxConvexMesh.indexBuffer
    get() = getIndexBuffer()
val PxConvexMesh.nbPolygons
    get() = getNbPolygons()
val PxConvexMesh.referenceCount
    get() = getReferenceCount()
val PxConvexMesh.localBounds
    get() = getLocalBounds()

external interface PxConvexMeshGeometry : PxGeometry

/**
 * @param mesh WebIDL type: [PxConvexMesh]
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh): PxConvexMeshGeometry {
    fun _PxConvexMeshGeometry(_module: dynamic, mesh: PxConvexMesh) = js("new _module.PxConvexMeshGeometry(mesh)")
    return _PxConvexMeshGeometry(PhysXJsLoader.physXJs, mesh)
}

/**
 * @param mesh    WebIDL type: [PxConvexMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale): PxConvexMeshGeometry {
    fun _PxConvexMeshGeometry(_module: dynamic, mesh: PxConvexMesh, scaling: PxMeshScale) = js("new _module.PxConvexMeshGeometry(mesh, scaling)")
    return _PxConvexMeshGeometry(PhysXJsLoader.physXJs, mesh, scaling)
}

/**
 * @param mesh    WebIDL type: [PxConvexMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 * @param flags   WebIDL type: [PxConvexMeshGeometryFlags] (Ref)
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags): PxConvexMeshGeometry {
    fun _PxConvexMeshGeometry(_module: dynamic, mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags) = js("new _module.PxConvexMeshGeometry(mesh, scaling, flags)")
    return _PxConvexMeshGeometry(PhysXJsLoader.physXJs, mesh, scaling, flags)
}

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxConvexMeshGeometryFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxConvexMeshGeometryFlags(flags: Byte): PxConvexMeshGeometryFlags {
    fun _PxConvexMeshGeometryFlags(_module: dynamic, flags: Byte) = js("new _module.PxConvexMeshGeometryFlags(flags)")
    return _PxConvexMeshGeometryFlags(PhysXJsLoader.physXJs, flags)
}

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

fun PxGeometryHolder(): PxGeometryHolder {
    fun _PxGeometryHolder(_module: dynamic) = js("new _module.PxGeometryHolder()")
    return _PxGeometryHolder(PhysXJsLoader.physXJs)
}

/**
 * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
 */
fun PxGeometryHolder(geometry: PxGeometry): PxGeometryHolder {
    fun _PxGeometryHolder(_module: dynamic, geometry: PxGeometry) = js("new _module.PxGeometryHolder(geometry)")
    return _PxGeometryHolder(PhysXJsLoader.physXJs, geometry)
}

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
     * @param geom WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(geom: PxGeometry, pose: PxTransform): PxBounds3

    /**
     * @param geom      WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose      WebIDL type: [PxTransform] (Const, Ref)
     * @param inflation WebIDL type: float
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(geom: PxGeometry, pose: PxTransform, inflation: Float): PxBounds3

    /**
     * @param geom WebIDL type: [PxGeometry] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(geom: PxGeometry): Boolean

}

fun PxGeometryQuery.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHeightField : PxBase {
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
     * @return WebIDL type: unsigned long
     */
    fun getReferenceCount(): Int

    fun acquireReference()

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
val PxHeightField.referenceCount
    get() = getReferenceCount()
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

fun PxHeightFieldDesc(): PxHeightFieldDesc {
    fun _PxHeightFieldDesc(_module: dynamic) = js("new _module.PxHeightFieldDesc()")
    return _PxHeightFieldDesc(PhysXJsLoader.physXJs)
}

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxHeightFieldFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxHeightFieldFlags(flags: Short): PxHeightFieldFlags {
    fun _PxHeightFieldFlags(_module: dynamic, flags: Short) = js("new _module.PxHeightFieldFlags(flags)")
    return _PxHeightFieldFlags(PhysXJsLoader.physXJs, flags)
}

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

fun PxHeightFieldGeometry(): PxHeightFieldGeometry {
    fun _PxHeightFieldGeometry(_module: dynamic) = js("new _module.PxHeightFieldGeometry()")
    return _PxHeightFieldGeometry(PhysXJsLoader.physXJs)
}

/**
 * @param hf          WebIDL type: [PxHeightField]
 * @param flags       WebIDL type: [PxMeshGeometryFlags] (Ref)
 * @param heightScale WebIDL type: float
 * @param rowScale    WebIDL type: float
 * @param columnScale WebIDL type: float
 */
fun PxHeightFieldGeometry(hf: PxHeightField, flags: PxMeshGeometryFlags, heightScale: Float, rowScale: Float, columnScale: Float): PxHeightFieldGeometry {
    fun _PxHeightFieldGeometry(_module: dynamic, hf: PxHeightField, flags: PxMeshGeometryFlags, heightScale: Float, rowScale: Float, columnScale: Float) = js("new _module.PxHeightFieldGeometry(hf, flags, heightScale, rowScale, columnScale)")
    return _PxHeightFieldGeometry(PhysXJsLoader.physXJs, hf, flags, heightScale, rowScale, columnScale)
}

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
}

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
    var mPlane: Array<Float>
    /**
     * WebIDL type: short
     */
    var mNbVerts: Short
    /**
     * WebIDL type: short
     */
    var mIndexBase: Short
}

fun PxHullPolygon(): PxHullPolygon {
    fun _PxHullPolygon(_module: dynamic) = js("new _module.PxHullPolygon()")
    return _PxHullPolygon(PhysXJsLoader.physXJs)
}

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxMeshFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxMeshFlags(flags: Byte): PxMeshFlags {
    fun _PxMeshFlags(_module: dynamic, flags: Byte) = js("new _module.PxMeshFlags(flags)")
    return _PxMeshFlags(PhysXJsLoader.physXJs, flags)
}

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxMeshGeometryFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxMeshGeometryFlags(flags: Byte): PxMeshGeometryFlags {
    fun _PxMeshGeometryFlags(_module: dynamic, flags: Byte) = js("new _module.PxMeshGeometryFlags(flags)")
    return _PxMeshGeometryFlags(PhysXJsLoader.physXJs, flags)
}

fun PxMeshGeometryFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshScale

fun PxMeshScale(): PxMeshScale {
    fun _PxMeshScale(_module: dynamic) = js("new _module.PxMeshScale()")
    return _PxMeshScale(PhysXJsLoader.physXJs)
}

/**
 * @param r WebIDL type: float
 */
fun PxMeshScale(r: Float): PxMeshScale {
    fun _PxMeshScale(_module: dynamic, r: Float) = js("new _module.PxMeshScale(r)")
    return _PxMeshScale(PhysXJsLoader.physXJs, r)
}

/**
 * @param s WebIDL type: [PxVec3] (Const, Ref)
 * @param r WebIDL type: [PxQuat] (Const, Ref)
 */
fun PxMeshScale(s: PxVec3, r: PxQuat): PxMeshScale {
    fun _PxMeshScale(_module: dynamic, s: PxVec3, r: PxQuat) = js("new _module.PxMeshScale(s, r)")
    return _PxMeshScale(PhysXJsLoader.physXJs, s, r)
}

fun PxMeshScale.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPlaneGeometry : PxGeometry

fun PxPlaneGeometry(): PxPlaneGeometry {
    fun _PxPlaneGeometry(_module: dynamic) = js("new _module.PxPlaneGeometry()")
    return _PxPlaneGeometry(PhysXJsLoader.physXJs)
}

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

fun PxSimpleTriangleMesh(): PxSimpleTriangleMesh {
    fun _PxSimpleTriangleMesh(_module: dynamic) = js("new _module.PxSimpleTriangleMesh()")
    return _PxSimpleTriangleMesh(PhysXJsLoader.physXJs)
}

fun PxSimpleTriangleMesh.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSphereGeometry : PxGeometry

/**
 * @param ir WebIDL type: float
 */
fun PxSphereGeometry(ir: Float): PxSphereGeometry {
    fun _PxSphereGeometry(_module: dynamic, ir: Float) = js("new _module.PxSphereGeometry(ir)")
    return _PxSphereGeometry(PhysXJsLoader.physXJs, ir)
}

fun PxSphereGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

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

fun PxTriangle(): PxTriangle {
    fun _PxTriangle(_module: dynamic) = js("new _module.PxTriangle()")
    return _PxTriangle(PhysXJsLoader.physXJs)
}

/**
 * @param p0 WebIDL type: [PxVec3] (Const, Ref)
 * @param p1 WebIDL type: [PxVec3] (Const, Ref)
 * @param p2 WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxTriangle(p0: PxVec3, p1: PxVec3, p2: PxVec3): PxTriangle {
    fun _PxTriangle(_module: dynamic, p0: PxVec3, p1: PxVec3, p2: PxVec3) = js("new _module.PxTriangle(p0, p1, p2)")
    return _PxTriangle(PhysXJsLoader.physXJs, p0, p1, p2)
}

fun PxTriangle.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTriangleMesh : PxBase {
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

    /**
     * @return WebIDL type: unsigned long
     */
    fun getReferenceCount(): Int

    fun acquireReference()

}

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
val PxTriangleMesh.referenceCount
    get() = getReferenceCount()

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxTriangleMeshFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxTriangleMeshFlags(flags: Byte): PxTriangleMeshFlags {
    fun _PxTriangleMeshFlags(_module: dynamic, flags: Byte) = js("new _module.PxTriangleMeshFlags(flags)")
    return _PxTriangleMeshFlags(PhysXJsLoader.physXJs, flags)
}

fun PxTriangleMeshFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTriangleMeshGeometry : PxGeometry {
    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

/**
 * @param mesh WebIDL type: [PxTriangleMesh]
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh): PxTriangleMeshGeometry {
    fun _PxTriangleMeshGeometry(_module: dynamic, mesh: PxTriangleMesh) = js("new _module.PxTriangleMeshGeometry(mesh)")
    return _PxTriangleMeshGeometry(PhysXJsLoader.physXJs, mesh)
}

/**
 * @param mesh    WebIDL type: [PxTriangleMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, scaling: PxMeshScale): PxTriangleMeshGeometry {
    fun _PxTriangleMeshGeometry(_module: dynamic, mesh: PxTriangleMesh, scaling: PxMeshScale) = js("new _module.PxTriangleMeshGeometry(mesh, scaling)")
    return _PxTriangleMeshGeometry(PhysXJsLoader.physXJs, mesh, scaling)
}

/**
 * @param mesh    WebIDL type: [PxTriangleMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 * @param flags   WebIDL type: [PxMeshGeometryFlags] (Ref)
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, scaling: PxMeshScale, flags: PxMeshGeometryFlags): PxTriangleMeshGeometry {
    fun _PxTriangleMeshGeometry(_module: dynamic, mesh: PxTriangleMesh, scaling: PxMeshScale, flags: PxMeshGeometryFlags) = js("new _module.PxTriangleMeshGeometry(mesh, scaling, flags)")
    return _PxTriangleMeshGeometry(PhysXJsLoader.physXJs, mesh, scaling, flags)
}

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

object PxTriangleMeshFlagEnum {
    val e16_BIT_INDICES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshFlagEnum_e16_BIT_INDICES()
    val eADJACENCY_INFO: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriangleMeshFlagEnum_eADJACENCY_INFO()
}

