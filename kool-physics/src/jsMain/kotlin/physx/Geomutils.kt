package physx

external interface PxBoxGeometry: PxGeometry

external interface PxBVHStructure

external interface PxCapsuleGeometry: PxGeometry

external interface PxConvexMesh {
    fun getNbVertices(): Int
    fun getNbPolygons(): Int
    //fun getPolygonData(index: Int, data: PxHullPolygon)
}

external interface PxConvexMeshGeometry: PxGeometry

external interface PxConvexMeshGeometryFlag {
    val eTIGHT_BOUNDS: Enum
}

external interface PxConvexMeshGeometryFlags

external interface PxGeometry

//class PxHullPolygon {
//    var mPlane = FloatArray(4)
//    var mNbVerts = 0
//    var mIndexBase = 0
//}

external interface PxMeshScale

external interface PxPlaneGeometry: PxGeometry

external interface PxSphereBoxGeometry: PxGeometry
