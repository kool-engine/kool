package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.*

/**
 * @author fabmax
 */

enum class GpuType(val channels: Int, val byteSize: Int, val isInt: Boolean) {
    FLOAT1(1, 4, false),
    FLOAT2(2, 8, false),
    FLOAT3(3, 12, false),
    FLOAT4(4, 16, false),

    INT1(1, 4, true),
    INT2(2, 8, true),
    INT3(3, 12, true),
    INT4(4, 16, true),

    MAT2(2, 16, false),
    MAT3(3, 36, false),
    MAT4(4, 64, false)
}

val KslType.gpuType: GpuType
    get() = when (this) {
        KslFloat1 -> GpuType.FLOAT1
        KslFloat2 -> GpuType.FLOAT2
        KslFloat3 -> GpuType.FLOAT3
        KslFloat4 -> GpuType.FLOAT4

        KslInt1 -> GpuType.INT1
        KslInt2 -> GpuType.INT2
        KslInt3 -> GpuType.INT3
        KslInt4 -> GpuType.INT4

        KslMat2 -> GpuType.MAT2
        KslMat3 -> GpuType.MAT3
        KslMat4 -> GpuType.MAT4
        else -> throw IllegalArgumentException("KslType has no corresponding glsl type: $this")
    }

data class Attribute(val name: String, val type: GpuType) {
    @Deprecated("locationIncrement is platform specific")
    val locationIncrement: Int = when(type) {
        GpuType.MAT2 -> 2
        GpuType.MAT3 -> 3
        GpuType.MAT4 -> 4
        else -> 1
    }

    override fun toString(): String {
        return name
    }

    companion object {
        val POSITIONS = Attribute("attrib_positions", GpuType.FLOAT3)
        val NORMALS = Attribute("attrib_normals", GpuType.FLOAT3)
        val TANGENTS = Attribute("attrib_tangents", GpuType.FLOAT4)
        val TEXTURE_COORDS = Attribute("attrib_texture_coords", GpuType.FLOAT2)
        val COLORS = Attribute("attrib_colors", GpuType.FLOAT4)
        val JOINTS = Attribute("attrib_joints", GpuType.INT4)
        val WEIGHTS = Attribute("attrib_weights", GpuType.FLOAT4)
        val EMISSIVE_COLOR = Attribute("attrib_emissive_color", GpuType.FLOAT3)
        val METAL_ROUGH = Attribute("attrib_metal_rough", GpuType.FLOAT2)

        val INSTANCE_MODEL_MAT = Attribute("attrib_model_mat", GpuType.MAT4)
        val INSTANCE_COLOR = Attribute("attrib_instance_color", GpuType.FLOAT4)
    }
}

fun KslVertexStage.vertexAttribFloat1(attrib: Attribute): KslExprFloat1 {
    check(attrib.type == GpuType.FLOAT1) { "Attribute $attrib is expected to have type FLOAT but has ${attrib.type}" }
    return vertexAttribFloat1(attrib.name)
}

fun KslVertexStage.vertexAttribFloat2(attrib: Attribute): KslExprFloat2 {
    check(attrib.type == GpuType.FLOAT2) { "Attribute $attrib is expected to have type VEC_2F but has ${attrib.type}" }
    return vertexAttribFloat2(attrib.name)
}

fun KslVertexStage.vertexAttribFloat3(attrib: Attribute): KslExprFloat3 {
    check(attrib.type == GpuType.FLOAT3) { "Attribute $attrib is expected to have type VEC_3F but has ${attrib.type}" }
    return vertexAttribFloat3(attrib.name)
}

fun KslVertexStage.vertexAttribFloat4(attrib: Attribute): KslExprFloat4 {
    check(attrib.type == GpuType.FLOAT4) { "Attribute $attrib is expected to have type VEC_4F but has ${attrib.type}" }
    return vertexAttribFloat4(attrib.name)
}

fun KslVertexStage.vertexAttribInt1(attrib: Attribute): KslExprInt1 {
    check(attrib.type == GpuType.INT1) { "Attribute $attrib is expected to have type INT but has ${attrib.type}" }
    return vertexAttribInt1(attrib.name)
}

fun KslVertexStage.vertexAttribInt2(attrib: Attribute): KslExprInt2 {
    check(attrib.type == GpuType.INT2) { "Attribute $attrib is expected to have type VEC_2I but has ${attrib.type}" }
    return vertexAttribInt2(attrib.name)
}

fun KslVertexStage.vertexAttribInt3(attrib: Attribute): KslExprInt3 {
    check(attrib.type == GpuType.INT3) { "Attribute $attrib is expected to have type VEC_3I but has ${attrib.type}" }
    return vertexAttribInt3(attrib.name)
}

fun KslVertexStage.vertexAttribInt4(attrib: Attribute): KslExprInt4 {
    check(attrib.type == GpuType.INT4) { "Attribute $attrib is expected to have type VEC_4I but has ${attrib.type}" }
    return vertexAttribInt4(attrib.name)
}

fun KslVertexStage.instanceAttribFloat1(attrib: Attribute): KslExprFloat1 {
    check(attrib.type == GpuType.FLOAT1) { "Attribute $attrib is expected to have type FLOAT but has ${attrib.type}" }
    return instanceAttribFloat1(attrib.name)
}

fun KslVertexStage.instanceAttribFloat2(attrib: Attribute): KslExprFloat2 {
    check(attrib.type == GpuType.FLOAT2) { "Attribute $attrib is expected to have type VEC_2F but has ${attrib.type}" }
    return instanceAttribFloat2(attrib.name)
}

fun KslVertexStage.instanceAttribFloat3(attrib: Attribute): KslExprFloat3 {
    check(attrib.type == GpuType.FLOAT3) { "Attribute $attrib is expected to have type VEC_3F but has ${attrib.type}" }
    return instanceAttribFloat3(attrib.name)
}

fun KslVertexStage.instanceAttribFloat4(attrib: Attribute): KslExprFloat4 {
    check(attrib.type == GpuType.FLOAT4) { "Attribute $attrib is expected to have type VEC_4F but has ${attrib.type}" }
    return instanceAttribFloat4(attrib.name)
}

fun KslVertexStage.instanceAttribMat3(attrib: Attribute): KslExprMat3 {
    check(attrib.type == GpuType.MAT3) { "Attribute $attrib is expected to have type MAT_3F but has ${attrib.type}" }
    return instanceAttribMat3(attrib.name)
}

fun KslVertexStage.instanceAttribMat4(attrib: Attribute): KslExprMat4 {
    check(attrib.type == GpuType.MAT4) { "Attribute $attrib is expected to have type MAT_4F but has ${attrib.type}" }
    return instanceAttribMat4(attrib.name)
}

