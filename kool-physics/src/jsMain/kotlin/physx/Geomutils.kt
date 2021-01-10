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

external interface PxConvexMeshGeometryFlag {
    val eTIGHT_BOUNDS: Int

    fun isSet(flags: PxConvexMeshGeometryFlags, flag: Int): Boolean
    fun set(flags: PxConvexMeshGeometryFlags, flag: Int)
    fun clear(flags: PxConvexMeshGeometryFlags, flag: Int)
}

external interface PxConvexMeshGeometryFlags
fun PxConvexMeshGeometryFlags.isSet(flag: Int) = PhysX.PxConvexMeshGeometryFlag.isSet(this, flag)
fun PxConvexMeshGeometryFlags.set(flag: Int) = PhysX.PxConvexMeshGeometryFlag.set(this, flag)
fun PxConvexMeshGeometryFlags.clear(flag: Int) = PhysX.PxConvexMeshGeometryFlag.clear(this, flag)

external interface PxGeometry

external interface PxHullPolygon {
    var mNbVerts: Int
    var mIndexBase: Int

    fun get_mPlane(index: Int): Float
}

external interface PxMeshScale

external interface PxPlaneGeometry: PxGeometry

external interface PxSphereGeometry: PxGeometry
