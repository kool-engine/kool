/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_PARAMETER", "unused")

package physx

external interface PxBVH33MidphaseDesc {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var meshSizePerformanceTradeOff: Float
    /**
     * WebIDL type: [PxMeshCookingHintEnum] (enum)
     */
    var meshCookingHint: Int

    fun setToDefault()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxBVH33MidphaseDescFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBVH33MidphaseDesc = js("_module.wrapPointer(ptr, _module.PxBVH33MidphaseDesc)")

fun PxBVH33MidphaseDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBVH34MidphaseDesc {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var numPrimsPerLeaf: Int

    fun setToDefault()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxBVH34MidphaseDescFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBVH34MidphaseDesc = js("_module.wrapPointer(ptr, _module.PxBVH34MidphaseDesc)")

fun PxBVH34MidphaseDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConvexFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxConvexFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxConvexFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxConvexFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxConvexFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxConvexFlags = js("new _module.PxConvexFlags(flags)")

fun PxConvexFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConvexFlags = js("_module.wrapPointer(ptr, _module.PxConvexFlags)")

fun PxConvexFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConvexMeshDesc {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxBoundedData] (Value)
     */
    var points: PxBoundedData
    /**
     * WebIDL type: [PxConvexFlags] (Value)
     */
    var flags: PxConvexFlags
}

fun PxConvexMeshDesc(_module: dynamic = PhysXJsLoader.physXJs): PxConvexMeshDesc = js("new _module.PxConvexMeshDesc()")

fun PxConvexMeshDescFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConvexMeshDesc = js("_module.wrapPointer(ptr, _module.PxConvexMeshDesc)")

fun PxConvexMeshDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxCookingParams {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var areaTestEpsilon: Float
    /**
     * WebIDL type: float
     */
    var planeTolerance: Float
    /**
     * WebIDL type: [PxConvexMeshCookingTypeEnum] (enum)
     */
    var convexMeshCookingType: Int
    /**
     * WebIDL type: boolean
     */
    var suppressTriangleMeshRemapTable: Boolean
    /**
     * WebIDL type: boolean
     */
    var buildTriangleAdjacencies: Boolean
    /**
     * WebIDL type: boolean
     */
    var buildGPUData: Boolean
    /**
     * WebIDL type: [PxTolerancesScale] (Value)
     */
    var scale: PxTolerancesScale
    /**
     * WebIDL type: [PxMeshPreprocessingFlags] (Value)
     */
    var meshPreprocessParams: PxMeshPreprocessingFlags
    /**
     * WebIDL type: float
     */
    var meshWeldTolerance: Float
    /**
     * WebIDL type: [PxMidphaseDesc] (Value)
     */
    var midphaseDesc: PxMidphaseDesc
    /**
     * WebIDL type: unsigned long
     */
    var gaussMapLimit: Int
}

/**
 * @param sc WebIDL type: [PxTolerancesScale] (Const, Ref)
 */
fun PxCookingParams(sc: PxTolerancesScale, _module: dynamic = PhysXJsLoader.physXJs): PxCookingParams = js("new _module.PxCookingParams(sc)")

fun PxCookingParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxCookingParams = js("_module.wrapPointer(ptr, _module.PxCookingParams)")

fun PxCookingParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshPreprocessingFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxMeshPreprocessingFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxMeshPreprocessingFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxMeshPreprocessingFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxMeshPreprocessingFlags(flags: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMeshPreprocessingFlags = js("new _module.PxMeshPreprocessingFlags(flags)")

fun PxMeshPreprocessingFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMeshPreprocessingFlags = js("_module.wrapPointer(ptr, _module.PxMeshPreprocessingFlags)")

fun PxMeshPreprocessingFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMidphaseDesc {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxBVH33MidphaseDesc] (Value)
     */
    var mBVH33Desc: PxBVH33MidphaseDesc
    /**
     * WebIDL type: [PxBVH34MidphaseDesc] (Value)
     */
    var mBVH34Desc: PxBVH34MidphaseDesc

    /**
     * @return WebIDL type: [PxMeshMidPhaseEnum] (enum)
     */
    fun getType(): Int

    /**
     * @param type WebIDL type: [PxMeshMidPhaseEnum] (enum)
     */
    fun setToDefault(type: Int)

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxMidphaseDesc(_module: dynamic = PhysXJsLoader.physXJs): PxMidphaseDesc = js("new _module.PxMidphaseDesc()")

fun PxMidphaseDescFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMidphaseDesc = js("_module.wrapPointer(ptr, _module.PxMidphaseDesc)")

fun PxMidphaseDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxMidphaseDesc.type
    get() = getType()

external interface PxTriangleMeshDesc : PxSimpleTriangleMesh {
    /**
     * WebIDL type: [PxTypedBoundedData_PxU16Const] (Const, Value)
     */
    var materialIndices: PxTypedBoundedData_PxU16Const
}

fun PxTriangleMeshDesc(_module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshDesc = js("new _module.PxTriangleMeshDesc()")

fun PxTriangleMeshDescFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriangleMeshDesc = js("_module.wrapPointer(ptr, _module.PxTriangleMeshDesc)")

fun PxTriangleMeshDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

object PxConvexFlagEnum {
    val e16_BIT_INDICES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_e16_BIT_INDICES()
    val eCOMPUTE_CONVEX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_eCOMPUTE_CONVEX()
    val eCHECK_ZERO_AREA_TRIANGLES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES()
    val eQUANTIZE_INPUT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_eQUANTIZE_INPUT()
    val eDISABLE_MESH_VALIDATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_eDISABLE_MESH_VALIDATION()
    val ePLANE_SHIFTING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_ePLANE_SHIFTING()
    val eFAST_INERTIA_COMPUTATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION()
    val eSHIFT_VERTICES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_eSHIFT_VERTICES()
}

object PxConvexMeshCookingTypeEnum {
    val eQUICKHULL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexMeshCookingTypeEnum_eQUICKHULL()
}

object PxMeshCookingHintEnum {
    val eSIM_PERFORMANCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshCookingHintEnum_eSIM_PERFORMANCE()
    val eCOOKING_PERFORMANCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshCookingHintEnum_eCOOKING_PERFORMANCE()
}

object PxMeshPreprocessingFlagEnum {
    val eWELD_VERTICES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshPreprocessingFlagEnum_eWELD_VERTICES()
    val eDISABLE_CLEAN_MESH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshPreprocessingFlagEnum_eDISABLE_CLEAN_MESH()
    val eDISABLE_ACTIVE_EDGES_PRECOMPUTE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshPreprocessingFlagEnum_eDISABLE_ACTIVE_EDGES_PRECOMPUTE()
    val eFORCE_32BIT_INDICES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshPreprocessingFlagEnum_eFORCE_32BIT_INDICES()
}

object PxMeshMidPhaseEnum {
    val eBVH33: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshMidPhaseEnum_eBVH33()
    val eBVH34: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMeshMidPhaseEnum_eBVH34()
}

