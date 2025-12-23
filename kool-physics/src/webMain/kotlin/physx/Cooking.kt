/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")

package physx

import kotlin.js.JsAny
import kotlin.js.js

external interface PxBVH33MidphaseDesc : JsAny, DestroyableNative {
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

fun PxBVH33MidphaseDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBVH33MidphaseDesc = js("_module.wrapPointer(ptr, _module.PxBVH33MidphaseDesc)")

var PxBVH33MidphaseDesc.meshCookingHintEnum: PxMeshCookingHintEnum
    get() = PxMeshCookingHintEnum.forValue(meshCookingHint)
    set(value) { meshCookingHint = value.value }

external interface PxBVH34MidphaseDesc : JsAny, DestroyableNative {
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

fun PxBVH34MidphaseDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBVH34MidphaseDesc = js("_module.wrapPointer(ptr, _module.PxBVH34MidphaseDesc)")

external interface PxConvexFlags : JsAny, DestroyableNative {
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
fun PxConvexFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxConvexFlags = js("new _module.PxConvexFlags(flags)")

fun PxConvexFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexFlags = js("_module.wrapPointer(ptr, _module.PxConvexFlags)")

fun PxConvexFlags.isSet(flag: PxConvexFlagEnum) = isSet(flag.value)
fun PxConvexFlags.raise(flag: PxConvexFlagEnum) = raise(flag.value)
fun PxConvexFlags.clear(flag: PxConvexFlagEnum) = clear(flag.value)

external interface PxConvexMeshDesc : JsAny, DestroyableNative {
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

fun PxConvexMeshDesc(_module: JsAny = PhysXJsLoader.physXJs): PxConvexMeshDesc = js("new _module.PxConvexMeshDesc()")

fun PxConvexMeshDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConvexMeshDesc = js("_module.wrapPointer(ptr, _module.PxConvexMeshDesc)")

external interface PxCookingParams : JsAny, DestroyableNative {
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
fun PxCookingParams(sc: PxTolerancesScale, _module: JsAny = PhysXJsLoader.physXJs): PxCookingParams = js("new _module.PxCookingParams(sc)")

fun PxCookingParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxCookingParams = js("_module.wrapPointer(ptr, _module.PxCookingParams)")

var PxCookingParams.convexMeshCookingTypeEnum: PxConvexMeshCookingTypeEnum
    get() = PxConvexMeshCookingTypeEnum.forValue(convexMeshCookingType)
    set(value) { convexMeshCookingType = value.value }

external interface PxMeshPreprocessingFlags : JsAny, DestroyableNative {
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
fun PxMeshPreprocessingFlags(flags: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMeshPreprocessingFlags = js("new _module.PxMeshPreprocessingFlags(flags)")

fun PxMeshPreprocessingFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMeshPreprocessingFlags = js("_module.wrapPointer(ptr, _module.PxMeshPreprocessingFlags)")

fun PxMeshPreprocessingFlags.isSet(flag: PxMeshPreprocessingFlagEnum) = isSet(flag.value)
fun PxMeshPreprocessingFlags.raise(flag: PxMeshPreprocessingFlagEnum) = raise(flag.value)
fun PxMeshPreprocessingFlags.clear(flag: PxMeshPreprocessingFlagEnum) = clear(flag.value)

external interface PxMidphaseDesc : JsAny, DestroyableNative {
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

fun PxMidphaseDesc(_module: JsAny = PhysXJsLoader.physXJs): PxMidphaseDesc = js("new _module.PxMidphaseDesc()")

fun PxMidphaseDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMidphaseDesc = js("_module.wrapPointer(ptr, _module.PxMidphaseDesc)")

val PxMidphaseDesc.type: PxMeshMidPhaseEnum
    get() = PxMeshMidPhaseEnum.forValue(getType())

fun PxMidphaseDesc.setToDefault(type: PxMeshMidPhaseEnum) = setToDefault(type.value)

external interface PxTriangleMeshDesc : JsAny, DestroyableNative, PxSimpleTriangleMesh {
    /**
     * WebIDL type: [PxTypedBoundedData_PxU16Const] (Const, Value)
     */
    var materialIndices: PxTypedBoundedData_PxU16Const
}

fun PxTriangleMeshDesc(_module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshDesc = js("new _module.PxTriangleMeshDesc()")

fun PxTriangleMeshDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriangleMeshDesc = js("_module.wrapPointer(ptr, _module.PxTriangleMeshDesc)")

value class PxConvexFlagEnum private constructor(val value: Int) {
    companion object {
        val e16_BIT_INDICES: PxConvexFlagEnum = PxConvexFlagEnum(PxConvexFlagEnum_e16_BIT_INDICES(PhysXJsLoader.physXJs))
        val eCOMPUTE_CONVEX: PxConvexFlagEnum = PxConvexFlagEnum(PxConvexFlagEnum_eCOMPUTE_CONVEX(PhysXJsLoader.physXJs))
        val eCHECK_ZERO_AREA_TRIANGLES: PxConvexFlagEnum = PxConvexFlagEnum(PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES(PhysXJsLoader.physXJs))
        val eQUANTIZE_INPUT: PxConvexFlagEnum = PxConvexFlagEnum(PxConvexFlagEnum_eQUANTIZE_INPUT(PhysXJsLoader.physXJs))
        val eDISABLE_MESH_VALIDATION: PxConvexFlagEnum = PxConvexFlagEnum(PxConvexFlagEnum_eDISABLE_MESH_VALIDATION(PhysXJsLoader.physXJs))
        val ePLANE_SHIFTING: PxConvexFlagEnum = PxConvexFlagEnum(PxConvexFlagEnum_ePLANE_SHIFTING(PhysXJsLoader.physXJs))
        val eFAST_INERTIA_COMPUTATION: PxConvexFlagEnum = PxConvexFlagEnum(PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION(PhysXJsLoader.physXJs))
        val eSHIFT_VERTICES: PxConvexFlagEnum = PxConvexFlagEnum(PxConvexFlagEnum_eSHIFT_VERTICES(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            e16_BIT_INDICES.value -> e16_BIT_INDICES
            eCOMPUTE_CONVEX.value -> eCOMPUTE_CONVEX
            eCHECK_ZERO_AREA_TRIANGLES.value -> eCHECK_ZERO_AREA_TRIANGLES
            eQUANTIZE_INPUT.value -> eQUANTIZE_INPUT
            eDISABLE_MESH_VALIDATION.value -> eDISABLE_MESH_VALIDATION
            ePLANE_SHIFTING.value -> ePLANE_SHIFTING
            eFAST_INERTIA_COMPUTATION.value -> eFAST_INERTIA_COMPUTATION
            eSHIFT_VERTICES.value -> eSHIFT_VERTICES
            else -> error("Invalid enum value $value for enum PxConvexFlagEnum")
        }
    }
}

private fun PxConvexFlagEnum_e16_BIT_INDICES(module: JsAny): Int = js("module._emscripten_enum_PxConvexFlagEnum_e16_BIT_INDICES()")
private fun PxConvexFlagEnum_eCOMPUTE_CONVEX(module: JsAny): Int = js("module._emscripten_enum_PxConvexFlagEnum_eCOMPUTE_CONVEX()")
private fun PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES(module: JsAny): Int = js("module._emscripten_enum_PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES()")
private fun PxConvexFlagEnum_eQUANTIZE_INPUT(module: JsAny): Int = js("module._emscripten_enum_PxConvexFlagEnum_eQUANTIZE_INPUT()")
private fun PxConvexFlagEnum_eDISABLE_MESH_VALIDATION(module: JsAny): Int = js("module._emscripten_enum_PxConvexFlagEnum_eDISABLE_MESH_VALIDATION()")
private fun PxConvexFlagEnum_ePLANE_SHIFTING(module: JsAny): Int = js("module._emscripten_enum_PxConvexFlagEnum_ePLANE_SHIFTING()")
private fun PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION(module: JsAny): Int = js("module._emscripten_enum_PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION()")
private fun PxConvexFlagEnum_eSHIFT_VERTICES(module: JsAny): Int = js("module._emscripten_enum_PxConvexFlagEnum_eSHIFT_VERTICES()")

value class PxConvexMeshCookingTypeEnum private constructor(val value: Int) {
    companion object {
        val eQUICKHULL: PxConvexMeshCookingTypeEnum = PxConvexMeshCookingTypeEnum(PxConvexMeshCookingTypeEnum_eQUICKHULL(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eQUICKHULL.value -> eQUICKHULL
            else -> error("Invalid enum value $value for enum PxConvexMeshCookingTypeEnum")
        }
    }
}

private fun PxConvexMeshCookingTypeEnum_eQUICKHULL(module: JsAny): Int = js("module._emscripten_enum_PxConvexMeshCookingTypeEnum_eQUICKHULL()")

value class PxMeshCookingHintEnum private constructor(val value: Int) {
    companion object {
        val eSIM_PERFORMANCE: PxMeshCookingHintEnum = PxMeshCookingHintEnum(PxMeshCookingHintEnum_eSIM_PERFORMANCE(PhysXJsLoader.physXJs))
        val eCOOKING_PERFORMANCE: PxMeshCookingHintEnum = PxMeshCookingHintEnum(PxMeshCookingHintEnum_eCOOKING_PERFORMANCE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eSIM_PERFORMANCE.value -> eSIM_PERFORMANCE
            eCOOKING_PERFORMANCE.value -> eCOOKING_PERFORMANCE
            else -> error("Invalid enum value $value for enum PxMeshCookingHintEnum")
        }
    }
}

private fun PxMeshCookingHintEnum_eSIM_PERFORMANCE(module: JsAny): Int = js("module._emscripten_enum_PxMeshCookingHintEnum_eSIM_PERFORMANCE()")
private fun PxMeshCookingHintEnum_eCOOKING_PERFORMANCE(module: JsAny): Int = js("module._emscripten_enum_PxMeshCookingHintEnum_eCOOKING_PERFORMANCE()")

value class PxMeshPreprocessingFlagEnum private constructor(val value: Int) {
    companion object {
        val eWELD_VERTICES: PxMeshPreprocessingFlagEnum = PxMeshPreprocessingFlagEnum(PxMeshPreprocessingFlagEnum_eWELD_VERTICES(PhysXJsLoader.physXJs))
        val eDISABLE_CLEAN_MESH: PxMeshPreprocessingFlagEnum = PxMeshPreprocessingFlagEnum(PxMeshPreprocessingFlagEnum_eDISABLE_CLEAN_MESH(PhysXJsLoader.physXJs))
        val eDISABLE_ACTIVE_EDGES_PRECOMPUTE: PxMeshPreprocessingFlagEnum = PxMeshPreprocessingFlagEnum(PxMeshPreprocessingFlagEnum_eDISABLE_ACTIVE_EDGES_PRECOMPUTE(PhysXJsLoader.physXJs))
        val eFORCE_32BIT_INDICES: PxMeshPreprocessingFlagEnum = PxMeshPreprocessingFlagEnum(PxMeshPreprocessingFlagEnum_eFORCE_32BIT_INDICES(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eWELD_VERTICES.value -> eWELD_VERTICES
            eDISABLE_CLEAN_MESH.value -> eDISABLE_CLEAN_MESH
            eDISABLE_ACTIVE_EDGES_PRECOMPUTE.value -> eDISABLE_ACTIVE_EDGES_PRECOMPUTE
            eFORCE_32BIT_INDICES.value -> eFORCE_32BIT_INDICES
            else -> error("Invalid enum value $value for enum PxMeshPreprocessingFlagEnum")
        }
    }
}

private fun PxMeshPreprocessingFlagEnum_eWELD_VERTICES(module: JsAny): Int = js("module._emscripten_enum_PxMeshPreprocessingFlagEnum_eWELD_VERTICES()")
private fun PxMeshPreprocessingFlagEnum_eDISABLE_CLEAN_MESH(module: JsAny): Int = js("module._emscripten_enum_PxMeshPreprocessingFlagEnum_eDISABLE_CLEAN_MESH()")
private fun PxMeshPreprocessingFlagEnum_eDISABLE_ACTIVE_EDGES_PRECOMPUTE(module: JsAny): Int = js("module._emscripten_enum_PxMeshPreprocessingFlagEnum_eDISABLE_ACTIVE_EDGES_PRECOMPUTE()")
private fun PxMeshPreprocessingFlagEnum_eFORCE_32BIT_INDICES(module: JsAny): Int = js("module._emscripten_enum_PxMeshPreprocessingFlagEnum_eFORCE_32BIT_INDICES()")

value class PxMeshMidPhaseEnum private constructor(val value: Int) {
    companion object {
        val eBVH33: PxMeshMidPhaseEnum = PxMeshMidPhaseEnum(PxMeshMidPhaseEnum_eBVH33(PhysXJsLoader.physXJs))
        val eBVH34: PxMeshMidPhaseEnum = PxMeshMidPhaseEnum(PxMeshMidPhaseEnum_eBVH34(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eBVH33.value -> eBVH33
            eBVH34.value -> eBVH34
            else -> error("Invalid enum value $value for enum PxMeshMidPhaseEnum")
        }
    }
}

private fun PxMeshMidPhaseEnum_eBVH33(module: JsAny): Int = js("module._emscripten_enum_PxMeshMidPhaseEnum_eBVH33()")
private fun PxMeshMidPhaseEnum_eBVH34(module: JsAny): Int = js("module._emscripten_enum_PxMeshMidPhaseEnum_eBVH34()")

