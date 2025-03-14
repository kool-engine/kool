package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.*

data class Attribute(val name: String, val type: GpuType) {
    override fun toString(): String {
        return name
    }

    companion object {
        val POSITIONS = Attribute("attrib_positions", GpuType.Float3)
        val NORMALS = Attribute("attrib_normals", GpuType.Float3)
        val TANGENTS = Attribute("attrib_tangents", GpuType.Float4)
        val TEXTURE_COORDS = Attribute("attrib_texture_coords", GpuType.Float2)
        val COLORS = Attribute("attrib_colors", GpuType.Float4)
        val JOINTS = Attribute("attrib_joints", GpuType.Int4)
        val WEIGHTS = Attribute("attrib_weights", GpuType.Float4)
        val EMISSIVE_COLOR = Attribute("attrib_emissive_color", GpuType.Float3)
        val METAL_ROUGH = Attribute("attrib_metal_rough", GpuType.Float2)

        val INSTANCE_MODEL_MAT = Attribute("attrib_model_mat", GpuType.Mat4)
        val INSTANCE_COLOR = Attribute("attrib_instance_color", GpuType.Float4)
    }
}

fun KslVertexStage.vertexAttribFloat1(attrib: Attribute): KslExprFloat1 {
    check(attrib.type == GpuType.Float1) { "Attribute $attrib is expected to have type FLOAT but has ${attrib.type}" }
    return vertexAttribFloat1(attrib.name)
}

fun KslVertexStage.vertexAttribFloat2(attrib: Attribute): KslExprFloat2 {
    check(attrib.type == GpuType.Float2) { "Attribute $attrib is expected to have type VEC_2F but has ${attrib.type}" }
    return vertexAttribFloat2(attrib.name)
}

fun KslVertexStage.vertexAttribFloat3(attrib: Attribute): KslExprFloat3 {
    check(attrib.type == GpuType.Float3) { "Attribute $attrib is expected to have type VEC_3F but has ${attrib.type}" }
    return vertexAttribFloat3(attrib.name)
}

fun KslVertexStage.vertexAttribFloat4(attrib: Attribute): KslExprFloat4 {
    check(attrib.type == GpuType.Float4) { "Attribute $attrib is expected to have type VEC_4F but has ${attrib.type}" }
    return vertexAttribFloat4(attrib.name)
}

fun KslVertexStage.vertexAttribInt1(attrib: Attribute): KslExprInt1 {
    check(attrib.type == GpuType.Int1) { "Attribute $attrib is expected to have type INT but has ${attrib.type}" }
    return vertexAttribInt1(attrib.name)
}

fun KslVertexStage.vertexAttribInt2(attrib: Attribute): KslExprInt2 {
    check(attrib.type == GpuType.Int2) { "Attribute $attrib is expected to have type VEC_2I but has ${attrib.type}" }
    return vertexAttribInt2(attrib.name)
}

fun KslVertexStage.vertexAttribInt3(attrib: Attribute): KslExprInt3 {
    check(attrib.type == GpuType.Int3) { "Attribute $attrib is expected to have type VEC_3I but has ${attrib.type}" }
    return vertexAttribInt3(attrib.name)
}

fun KslVertexStage.vertexAttribInt4(attrib: Attribute): KslExprInt4 {
    check(attrib.type == GpuType.Int4) { "Attribute $attrib is expected to have type VEC_4I but has ${attrib.type}" }
    return vertexAttribInt4(attrib.name)
}

fun KslVertexStage.instanceAttribFloat1(attrib: Attribute): KslExprFloat1 {
    check(attrib.type == GpuType.Float1) { "Attribute $attrib is expected to have type FLOAT but has ${attrib.type}" }
    return instanceAttribFloat1(attrib.name)
}

fun KslVertexStage.instanceAttribFloat2(attrib: Attribute): KslExprFloat2 {
    check(attrib.type == GpuType.Float2) { "Attribute $attrib is expected to have type VEC_2F but has ${attrib.type}" }
    return instanceAttribFloat2(attrib.name)
}

fun KslVertexStage.instanceAttribFloat3(attrib: Attribute): KslExprFloat3 {
    check(attrib.type == GpuType.Float3) { "Attribute $attrib is expected to have type VEC_3F but has ${attrib.type}" }
    return instanceAttribFloat3(attrib.name)
}

fun KslVertexStage.instanceAttribFloat4(attrib: Attribute): KslExprFloat4 {
    check(attrib.type == GpuType.Float4) { "Attribute $attrib is expected to have type VEC_4F but has ${attrib.type}" }
    return instanceAttribFloat4(attrib.name)
}

fun KslVertexStage.instanceAttribMat3(attrib: Attribute): KslExprMat3 {
    check(attrib.type == GpuType.Mat3) { "Attribute $attrib is expected to have type MAT_3F but has ${attrib.type}" }
    return instanceAttribMat3(attrib.name)
}

fun KslVertexStage.instanceAttribMat4(attrib: Attribute): KslExprMat4 {
    check(attrib.type == GpuType.Mat4) { "Attribute $attrib is expected to have type MAT_4F but has ${attrib.type}" }
    return instanceAttribMat4(attrib.name)
}

