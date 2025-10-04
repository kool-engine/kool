package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.StructMember

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

fun StructMember<*>.asAttribute(): Attribute = Attribute(name, type)

fun KslVertexStage.vertexAttribFloat1(attrib: Attribute): KslExprFloat1 {
    check(attrib.type == GpuType.Float1) { "Attribute $attrib is expected to have type Float1 but has ${attrib.type}" }
    return vertexAttribFloat1(attrib.name)
}

fun KslVertexStage.vertexAttribFloat2(attrib: Attribute): KslExprFloat2 {
    check(attrib.type == GpuType.Float2) { "Attribute $attrib is expected to have type Float2 but has ${attrib.type}" }
    return vertexAttribFloat2(attrib.name)
}

fun KslVertexStage.vertexAttribFloat3(attrib: Attribute): KslExprFloat3 {
    check(attrib.type == GpuType.Float3) { "Attribute $attrib is expected to have type Float3 but has ${attrib.type}" }
    return vertexAttribFloat3(attrib.name)
}

fun KslVertexStage.vertexAttribFloat4(attrib: Attribute): KslExprFloat4 {
    check(attrib.type == GpuType.Float4) { "Attribute $attrib is expected to have type Float4 but has ${attrib.type}" }
    return vertexAttribFloat4(attrib.name)
}

fun KslVertexStage.vertexAttribInt1(attrib: Attribute): KslExprInt1 {
    check(attrib.type == GpuType.Int1) { "Attribute $attrib is expected to have type Int1 but has ${attrib.type}" }
    return vertexAttribInt1(attrib.name)
}

fun KslVertexStage.vertexAttribInt2(attrib: Attribute): KslExprInt2 {
    check(attrib.type == GpuType.Int2) { "Attribute $attrib is expected to have type Int2 but has ${attrib.type}" }
    return vertexAttribInt2(attrib.name)
}

fun KslVertexStage.vertexAttribInt3(attrib: Attribute): KslExprInt3 {
    check(attrib.type == GpuType.Int3) { "Attribute $attrib is expected to have type Int3 but has ${attrib.type}" }
    return vertexAttribInt3(attrib.name)
}

fun KslVertexStage.vertexAttribInt4(attrib: Attribute): KslExprInt4 {
    check(attrib.type == GpuType.Int4) { "Attribute $attrib is expected to have type Int4 but has ${attrib.type}" }
    return vertexAttribInt4(attrib.name)
}

fun KslVertexStage.instanceAttribFloat1(attrib: Attribute): KslExprFloat1 {
    check(attrib.type == GpuType.Float1) { "Attribute $attrib is expected to have type Float1 but has ${attrib.type}" }
    return instanceAttribFloat1(attrib.name)
}

fun KslVertexStage.instanceAttribFloat2(attrib: Attribute): KslExprFloat2 {
    check(attrib.type == GpuType.Float2) { "Attribute $attrib is expected to have type Float2 but has ${attrib.type}" }
    return instanceAttribFloat2(attrib.name)
}

fun KslVertexStage.instanceAttribFloat3(attrib: Attribute): KslExprFloat3 {
    check(attrib.type == GpuType.Float3) { "Attribute $attrib is expected to have type Float3 but has ${attrib.type}" }
    return instanceAttribFloat3(attrib.name)
}

fun KslVertexStage.instanceAttribFloat4(attrib: Attribute): KslExprFloat4 {
    check(attrib.type == GpuType.Float4) { "Attribute $attrib is expected to have type Float4 but has ${attrib.type}" }
    return instanceAttribFloat4(attrib.name)
}

fun KslVertexStage.instanceAttribInt1(attrib: Attribute): KslExprInt1 {
    check(attrib.type == GpuType.Int1) { "Attribute $attrib is expected to have type Int1 but has ${attrib.type}" }
    return instanceAttribInt1(attrib.name)
}

fun KslVertexStage.instanceAttribInt2(attrib: Attribute): KslExprInt2 {
    check(attrib.type == GpuType.Int2) { "Attribute $attrib is expected to have type Int2 but has ${attrib.type}" }
    return instanceAttribInt2(attrib.name)
}

fun KslVertexStage.instanceAttribInt3(attrib: Attribute): KslExprInt3 {
    check(attrib.type == GpuType.Int3) { "Attribute $attrib is expected to have type Int3 but has ${attrib.type}" }
    return instanceAttribInt3(attrib.name)
}

fun KslVertexStage.instanceAttribInt4(attrib: Attribute): KslExprInt4 {
    check(attrib.type == GpuType.Int4) { "Attribute $attrib is expected to have type Int4 but has ${attrib.type}" }
    return instanceAttribInt4(attrib.name)
}

fun KslVertexStage.instanceAttribUint1(attrib: Attribute): KslExprUint1 {
    check(attrib.type == GpuType.Uint1) { "Attribute $attrib is expected to have type Uint1 but has ${attrib.type}" }
    return instanceAttribUint1(attrib.name)
}

fun KslVertexStage.instanceAttribUint2(attrib: Attribute): KslExprUint2 {
    check(attrib.type == GpuType.Uint2) { "Attribute $attrib is expected to have type Uint2 but has ${attrib.type}" }
    return instanceAttribUint2(attrib.name)
}

fun KslVertexStage.instanceAttribUint3(attrib: Attribute): KslExprUint3 {
    check(attrib.type == GpuType.Uint3) { "Attribute $attrib is expected to have type Uint3 but has ${attrib.type}" }
    return instanceAttribUint3(attrib.name)
}

fun KslVertexStage.instanceAttribUint4(attrib: Attribute): KslExprUint4 {
    check(attrib.type == GpuType.Uint4) { "Attribute $attrib is expected to have type Uint4 but has ${attrib.type}" }
    return instanceAttribUint4(attrib.name)
}

fun KslVertexStage.instanceAttribMat2(attrib: Attribute): KslExprMat2 {
    check(attrib.type == GpuType.Mat2) { "Attribute $attrib is expected to have type Mat2 but has ${attrib.type}" }
    return instanceAttribMat2(attrib.name)
}

fun KslVertexStage.instanceAttribMat3(attrib: Attribute): KslExprMat3 {
    check(attrib.type == GpuType.Mat3) { "Attribute $attrib is expected to have type Mat3 but has ${attrib.type}" }
    return instanceAttribMat3(attrib.name)
}

fun KslVertexStage.instanceAttribMat4(attrib: Attribute): KslExprMat4 {
    check(attrib.type == GpuType.Mat4) { "Attribute $attrib is expected to have type Mat4 but has ${attrib.type}" }
    return instanceAttribMat4(attrib.name)
}

