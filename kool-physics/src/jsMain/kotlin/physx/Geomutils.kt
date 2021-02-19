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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxBoxGeometry(hx, hy, hz)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxCapsuleGeometry(radius, halfHeight)")
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
     * @return WebIDL type: [PxU8Ptr] (Value)
     */
    fun getIndexBuffer(): PxU8Ptr

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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxConvexMeshGeometry(mesh)")
}

/**
 * @param mesh    WebIDL type: [PxConvexMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale): PxConvexMeshGeometry {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxConvexMeshGeometry(mesh, scaling)")
}

/**
 * @param mesh    WebIDL type: [PxConvexMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 * @param flags   WebIDL type: [PxConvexMeshGeometryFlags] (Ref)
 */
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags): PxConvexMeshGeometry {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxConvexMeshGeometry(mesh, scaling, flags)")
}

fun PxConvexMeshGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConvexMeshGeometryFlags {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxConvexMeshGeometryFlags(flags)")
}

fun PxConvexMeshGeometryFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGeometry

fun PxGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHullPolygon {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxHullPolygon()")
}

fun PxHullPolygon.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshFlags {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxMeshFlags(flags)")
}

fun PxMeshFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshGeometryFlags {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxMeshGeometryFlags(flags)")
}

fun PxMeshGeometryFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshScale

fun PxMeshScale(): PxMeshScale {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxMeshScale()")
}

/**
 * @param r WebIDL type: float
 */
fun PxMeshScale(r: Float): PxMeshScale {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxMeshScale(r)")
}

/**
 * @param s WebIDL type: [PxVec3] (Const, Ref)
 * @param r WebIDL type: [PxQuat] (Const, Ref)
 */
fun PxMeshScale(s: PxVec3, r: PxQuat): PxMeshScale {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxMeshScale(s, r)")
}

fun PxMeshScale.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPlaneGeometry : PxGeometry

fun PxPlaneGeometry(): PxPlaneGeometry {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxPlaneGeometry()")
}

fun PxPlaneGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSimpleTriangleMesh {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxSimpleTriangleMesh()")
}

fun PxSimpleTriangleMesh.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSphereGeometry : PxGeometry

/**
 * @param ir WebIDL type: float
 */
fun PxSphereGeometry(ir: Float): PxSphereGeometry {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxSphereGeometry(ir)")
}

fun PxSphereGeometry.destroy() {
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
     * @return WebIDL type: [PxU32Ptr] (Const, Value)
     */
    fun getTrianglesRemap(): PxU32Ptr

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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxTriangleMeshFlags(flags)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxTriangleMeshGeometry(mesh)")
}

/**
 * @param mesh    WebIDL type: [PxTriangleMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, scaling: PxMeshScale): PxTriangleMeshGeometry {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxTriangleMeshGeometry(mesh, scaling)")
}

/**
 * @param mesh    WebIDL type: [PxTriangleMesh]
 * @param scaling WebIDL type: [PxMeshScale] (Const, Ref)
 * @param flags   WebIDL type: [PxMeshGeometryFlags] (Ref)
 */
fun PxTriangleMeshGeometry(mesh: PxTriangleMesh, scaling: PxMeshScale, flags: PxMeshGeometryFlags): PxTriangleMeshGeometry {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxTriangleMeshGeometry(mesh, scaling, flags)")
}

fun PxTriangleMeshGeometry.destroy() {
    PhysXJsLoader.destroy(this)
}

object PxConvexMeshGeometryFlagEnum {
    val eTIGHT_BOUNDS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS()
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

