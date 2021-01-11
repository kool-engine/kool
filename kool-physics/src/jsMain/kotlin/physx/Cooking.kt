package physx

external interface PxConvexMeshDesc {
    var points: PxBoundedData
    var flags: PxConvexFlags
}

@Suppress("UnsafeCastFromDynamic")
object PxConvexFlag {
    val e16_BIT_INDICES: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_eENABLE_ACTIVE_ACTORS()
    val eCOMPUTE_CONVEX: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_eCOMPUTE_CONVEX()
    val eCHECK_ZERO_AREA_TRIANGLES: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_eCHECK_ZERO_AREA_TRIANGLES()
    val eQUANTIZE_INPUT: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_eQUANTIZE_INPUT()
    val eDISABLE_MESH_VALIDATION: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_eDISABLE_MESH_VALIDATION()
    val ePLANE_SHIFTING: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_ePLANE_SHIFTING()
    val eFAST_INERTIA_COMPUTATION: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_eFAST_INERTIA_COMPUTATION()
    val eGPU_COMPATIBLE: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_eGPU_COMPATIBLE()
    val eSHIFT_VERTICES: Int get() = PhysX.physx._emscripten_enum_physx_PxConvexFlag_eSHIFT_VERTICES()
}

external interface PxConvexFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}

external interface PxCooking {
    fun createConvexMesh(desc: PxConvexMeshDesc, insertionCallback: PxPhysicsInsertionCallback): PxConvexMesh
}

external interface PxCookingParams
