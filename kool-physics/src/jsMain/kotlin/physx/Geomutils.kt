/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxBoxGeometry : PxGeometry
fun PxBoxGeometry(hx: Float, hy: Float, hz: Float): PxBoxGeometry {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxBoxGeometry(hx, hy, hz)")
}

external interface PxBVHStructure : PxBase

external interface PxCapsuleGeometry : PxGeometry
fun PxCapsuleGeometry(radius: Float, halfHeight: Float): PxCapsuleGeometry {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxCapsuleGeometry(radius, halfHeight)")
}

external interface PxConvexMesh : PxBase {
    fun getNbVertices(): Int
    fun getVertices(): PxVec3
    fun getIndexBuffer(): PxU8Ptr
    fun getNbPolygons(): Int
    fun getPolygonData(index: Int, data: PxHullPolygon): Boolean
    fun getReferenceCount(): Int
    fun acquireReference()
    fun getLocalBounds(): PxBounds3
    fun isGpuCompatible(): Boolean
}

external interface PxConvexMeshGeometry : PxGeometry
fun PxConvexMeshGeometry(mesh: PxConvexMesh): PxConvexMeshGeometry {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxConvexMeshGeometry(mesh)")
}
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale): PxConvexMeshGeometry {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxConvexMeshGeometry(mesh, scaling)")
}
fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags): PxConvexMeshGeometry {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxConvexMeshGeometry(mesh, scaling, flags)")
}

external interface PxConvexMeshGeometryFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxConvexMeshGeometryFlags(flags: Byte): PxConvexMeshGeometryFlags {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxConvexMeshGeometryFlags(flags)")
}

external interface PxGeometry

external interface PxHullPolygon {
    var mPlane: Array<Float>
    var mNbVerts: Short
    var mIndexBase: Short
}
fun PxHullPolygon(): PxHullPolygon {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxHullPolygon()")
}

external interface PxMeshScale
fun PxMeshScale(): PxMeshScale {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxMeshScale()")
}
fun PxMeshScale(r: Float): PxMeshScale {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxMeshScale(r)")
}
fun PxMeshScale(s: PxVec3, r: PxQuat): PxMeshScale {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxMeshScale(s, r)")
}

external interface PxPlaneGeometry : PxGeometry
fun PxPlaneGeometry(): PxPlaneGeometry {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxPlaneGeometry()")
}

external interface PxSphereGeometry : PxGeometry
fun PxSphereGeometry(ir: Float): PxSphereGeometry {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxSphereGeometry(ir)")
}

object PxConvexMeshGeometryFlagEnum {
    val eTIGHT_BOUNDS: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS()
}