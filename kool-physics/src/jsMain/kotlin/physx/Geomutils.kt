package physx

external interface PxBoxGeometry : PxGeometry

external interface PxBVHStructure : PxBase

external interface PxCapsuleGeometry : PxGeometry

external interface PxConvexMesh : PxBase {
    fun getNbVertices(): Int
    fun getNbPolygons(): Int
    fun getPolygonData(index: Int, data: PxHullPolygon)
    fun getVertices(): Int
    fun getIndexBuffer(): Int
}

external interface PxConvexMeshGeometry : PxGeometry

external interface PxConvexMeshGeometryFlag {
    val eTIGHT_BOUNDS: Int
}

external interface PxConvexMeshGeometryFlags : PxFlags

external interface PxGeometry

external interface PxHullPolygon {
    var mNbVerts: Int
    var mIndexBase: Int

    fun get_mPlane(index: Int): Float
}

external interface PxMeshScale

external interface PxPlaneGeometry : PxGeometry

external interface PxSphereGeometry : PxGeometry
