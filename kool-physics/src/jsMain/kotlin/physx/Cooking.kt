/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxConvexFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxConvexFlags(flags: Int): PxConvexFlags {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxConvexFlags(flags)")
}

external interface PxConvexMeshDesc {
    var points: PxBoundedData
    var flags: PxConvexFlags
}
fun PxConvexMeshDesc(): PxConvexMeshDesc {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxConvexMeshDesc()")
}

external interface PxCooking {
    fun createConvexMesh(desc: PxConvexMeshDesc, insertionCallback: PxPhysicsInsertionCallback): PxConvexMesh
}

external interface PxCookingParams
fun PxCookingParams(sc: PxTolerancesScale): PxCookingParams {
    val module = PhysxJsLoader.physxJs
    return js("new module.PxCookingParams(sc)")
}

object PxConvexFlagEnum {
    val e16_BIT_INDICES: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_e16_BIT_INDICES()
    val eCOMPUTE_CONVEX: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_eCOMPUTE_CONVEX()
    val eCHECK_ZERO_AREA_TRIANGLES: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES()
    val eQUANTIZE_INPUT: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_eQUANTIZE_INPUT()
    val eDISABLE_MESH_VALIDATION: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_eDISABLE_MESH_VALIDATION()
    val ePLANE_SHIFTING: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_ePLANE_SHIFTING()
    val eFAST_INERTIA_COMPUTATION: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION()
    val eGPU_COMPATIBLE: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_eGPU_COMPATIBLE()
    val eSHIFT_VERTICES: Int get() = PhysxJsLoader.physxJs._emscripten_enum_PxConvexFlagEnum_eSHIFT_VERTICES()
}