package physx

external interface PxBoxGeometry: PxGeometry

external interface PxBVHStructure

external interface PxCapsuleGeometry: PxGeometry

external interface PxConvexMesh {
    fun getNbVertices(): Int
    fun getNbPolygons(): Int
    fun getPolygonData(index: Int, data: PxHullPolygon)
    fun getVertices(): Int
    fun getIndexBuffer(): Int
}

external interface PxConvexMeshGeometry: PxGeometry

@Suppress("UnsafeCastFromDynamic")
object PxConvexMeshGeometryFlag {
    val eTIGHT_BOUNDS: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexMeshGeometryFlag_eTIGHT_BOUNDS()
}

external interface PxConvexMeshGeometryFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}

external interface PxGeometry

external interface PxHullPolygon {
    var mNbVerts: Int
    var mIndexBase: Int

    fun get_mPlane(index: Int): Float
}

external interface PxMeshScale

external interface PxPlaneGeometry: PxGeometry

external interface PxSphereGeometry: PxGeometry
