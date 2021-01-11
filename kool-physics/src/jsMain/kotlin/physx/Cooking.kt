package physx

external interface PxConvexMeshDesc {
    var points: PxBoundedData
    var flags: PxConvexFlags
}

external interface PxConvexFlag {
    val e16_BIT_INDICES: Int
    val eCOMPUTE_CONVEX: Int
    val eCHECK_ZERO_AREA_TRIANGLES: Int
    val eQUANTIZE_INPUT: Int
    val eDISABLE_MESH_VALIDATION: Int
    val ePLANE_SHIFTING: Int
    val eFAST_INERTIA_COMPUTATION: Int
    val eGPU_COMPATIBLE: Int
    val eSHIFT_VERTICES: Int
}

external interface PxConvexFlags : PxFlags

external interface PxCooking {
    fun createConvexMesh(desc: PxConvexMeshDesc, insertionCallback: PxPhysicsInsertionCallback): PxConvexMesh
}

external interface PxCookingParams
