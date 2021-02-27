/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxBVH33MidphaseDesc {
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

fun PxBVH33MidphaseDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBVH34MidphaseDesc {
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

fun PxBVH34MidphaseDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConvexFlags {
    /**
     * @param flag WebIDL type: [PxConvexFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxConvexFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxConvexFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxConvexFlags(flags: Short): PxConvexFlags {
    fun _PxConvexFlags(_module: dynamic, flags: Short) = js("new _module.PxConvexFlags(flags)")
    return _PxConvexFlags(PhysXJsLoader.physXJs, flags)
}

fun PxConvexFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConvexMeshDesc {
    /**
     * WebIDL type: [PxBoundedData] (Value)
     */
    var points: PxBoundedData
    /**
     * WebIDL type: [PxConvexFlags] (Value)
     */
    var flags: PxConvexFlags
}

fun PxConvexMeshDesc(): PxConvexMeshDesc {
    fun _PxConvexMeshDesc(_module: dynamic) = js("new _module.PxConvexMeshDesc()")
    return _PxConvexMeshDesc(PhysXJsLoader.physXJs)
}

fun PxConvexMeshDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxCooking {
    fun release()

    /**
     * @param desc              WebIDL type: [PxConvexMeshDesc] (Const, Ref)
     * @param insertionCallback WebIDL type: [PxPhysicsInsertionCallback] (Ref)
     * @return WebIDL type: [PxConvexMesh]
     */
    fun createConvexMesh(desc: PxConvexMeshDesc, insertionCallback: PxPhysicsInsertionCallback): PxConvexMesh

    /**
     * @param desc              WebIDL type: [PxTriangleMeshDesc] (Const, Ref)
     * @param insertionCallback WebIDL type: [PxPhysicsInsertionCallback] (Ref)
     * @return WebIDL type: [PxTriangleMesh]
     */
    fun createTriangleMesh(desc: PxTriangleMeshDesc, insertionCallback: PxPhysicsInsertionCallback): PxTriangleMesh

    /**
     * @param desc              WebIDL type: [PxHeightFieldDesc] (Const, Ref)
     * @param insertionCallback WebIDL type: [PxPhysicsInsertionCallback] (Ref)
     * @return WebIDL type: [PxHeightField]
     */
    fun createHeightField(desc: PxHeightFieldDesc, insertionCallback: PxPhysicsInsertionCallback): PxHeightField

}

external interface PxCookingParams {
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
fun PxCookingParams(sc: PxTolerancesScale): PxCookingParams {
    fun _PxCookingParams(_module: dynamic, sc: PxTolerancesScale) = js("new _module.PxCookingParams(sc)")
    return _PxCookingParams(PhysXJsLoader.physXJs, sc)
}

fun PxCookingParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshPreprocessingFlags {
    /**
     * @param flag WebIDL type: [PxMeshPreprocessingFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxMeshPreprocessingFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxMeshPreprocessingFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxMeshPreprocessingFlags(flags: Int): PxMeshPreprocessingFlags {
    fun _PxMeshPreprocessingFlags(_module: dynamic, flags: Int) = js("new _module.PxMeshPreprocessingFlags(flags)")
    return _PxMeshPreprocessingFlags(PhysXJsLoader.physXJs, flags)
}

fun PxMeshPreprocessingFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMidphaseDesc {
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

fun PxMidphaseDesc(): PxMidphaseDesc {
    fun _PxMidphaseDesc(_module: dynamic) = js("new _module.PxMidphaseDesc()")
    return _PxMidphaseDesc(PhysXJsLoader.physXJs)
}

fun PxMidphaseDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxMidphaseDesc.type
    get() = getType()

external interface PxTriangleMeshDesc : PxSimpleTriangleMesh {
    /**
     * WebIDL type: [PxU16StridedData] (Value)
     */
    var materialIndices: PxU16StridedData

    override fun setToDefault()

    /**
     * @return WebIDL type: boolean
     */
    override fun isValid(): Boolean

}

fun PxTriangleMeshDesc(): PxTriangleMeshDesc {
    fun _PxTriangleMeshDesc(_module: dynamic) = js("new _module.PxTriangleMeshDesc()")
    return _PxTriangleMeshDesc(PhysXJsLoader.physXJs)
}

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
    val eGPU_COMPATIBLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConvexFlagEnum_eGPU_COMPATIBLE()
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

