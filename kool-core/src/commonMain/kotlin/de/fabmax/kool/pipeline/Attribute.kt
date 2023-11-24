package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.*

/**
 * @author fabmax
 */

enum class GlslType(val channels: Int, val byteSize: Int, val isInt: Boolean, val glslType: String) {
    FLOAT(1, 4, false, "float"),
    VEC_2F(2, 8, false, "vec2"),
    VEC_3F(3, 12, false, "vec3"),
    VEC_4F(4, 16, false, "vec4"),

    INT(1, 4, true, "int"),
    VEC_2I(2, 8, true, "ivec2"),
    VEC_3I(3, 12, true, "ivec3"),
    VEC_4I(4, 16, true, "ivec4"),

    MAT_2F(2, 16, false, "mat2"),
    MAT_3F(3, 36, false, "mat3"),
    MAT_4F(4, 64, false, "mat4")
}

val KslType.glslType: GlslType
    get() = when (this) {
        KslFloat1 -> GlslType.FLOAT
        KslFloat2 -> GlslType.VEC_2F
        KslFloat3 -> GlslType.VEC_3F
        KslFloat4 -> GlslType.VEC_4F

        KslInt1 -> GlslType.INT
        KslInt2 -> GlslType.VEC_2I
        KslInt3 -> GlslType.VEC_3I
        KslInt4 -> GlslType.VEC_4I

        KslMat2 -> GlslType.MAT_2F
        KslMat3 -> GlslType.MAT_3F
        KslMat4 -> GlslType.MAT_4F
        else -> throw IllegalArgumentException("KslType has no corresponding glsl type: $this")
    }

data class Attribute(val name: String, val type: GlslType) {
    //val props = PlatformAttributeProps(this)

    val locationIncrement: Int = when(type) {
        GlslType.MAT_2F -> 2
        GlslType.MAT_3F -> 3
        GlslType.MAT_4F -> 4
        else -> 1
    }

    override fun toString(): String {
        return name
    }

    companion object {
        val POSITIONS = Attribute("attrib_positions", GlslType.VEC_3F)
        val NORMALS = Attribute("attrib_normals", GlslType.VEC_3F)
        val TANGENTS = Attribute("attrib_tangents", GlslType.VEC_4F)
        val TEXTURE_COORDS = Attribute("attrib_texture_coords", GlslType.VEC_2F)
        val COLORS = Attribute("attrib_colors", GlslType.VEC_4F)
        val JOINTS = Attribute("attrib_joints", GlslType.VEC_4I)
        val WEIGHTS = Attribute("attrib_weights", GlslType.VEC_4F)
        val EMISSIVE_COLOR = Attribute("attrib_emissive_color", GlslType.VEC_3F)
        val METAL_ROUGH = Attribute("attrib_metal_rough", GlslType.VEC_2F)

        val INSTANCE_MODEL_MAT = Attribute("attrib_model_mat", GlslType.MAT_4F)
        val INSTANCE_COLOR = Attribute("attrib_instance_color", GlslType.VEC_4F)
    }
}

fun KslVertexStage.vertexAttribFloat1(attrib: Attribute): KslExprFloat1 {
    check(attrib.type == GlslType.FLOAT) { "Attribute $attrib is expected to have type FLOAT but has ${attrib.type}" }
    return vertexAttribFloat1(attrib.name)
}

fun KslVertexStage.vertexAttribFloat2(attrib: Attribute): KslExprFloat2 {
    check(attrib.type == GlslType.VEC_2F) { "Attribute $attrib is expected to have type VEC_2F but has ${attrib.type}" }
    return vertexAttribFloat2(attrib.name)
}

fun KslVertexStage.vertexAttribFloat3(attrib: Attribute): KslExprFloat3 {
    check(attrib.type == GlslType.VEC_3F) { "Attribute $attrib is expected to have type VEC_3F but has ${attrib.type}" }
    return vertexAttribFloat3(attrib.name)
}

fun KslVertexStage.vertexAttribFloat4(attrib: Attribute): KslExprFloat4 {
    check(attrib.type == GlslType.VEC_4F) { "Attribute $attrib is expected to have type VEC_4F but has ${attrib.type}" }
    return vertexAttribFloat4(attrib.name)
}

fun KslVertexStage.vertexAttribInt1(attrib: Attribute): KslExprInt1 {
    check(attrib.type == GlslType.INT) { "Attribute $attrib is expected to have type INT but has ${attrib.type}" }
    return vertexAttribInt1(attrib.name)
}

fun KslVertexStage.vertexAttribInt2(attrib: Attribute): KslExprInt2 {
    check(attrib.type == GlslType.VEC_2I) { "Attribute $attrib is expected to have type VEC_2I but has ${attrib.type}" }
    return vertexAttribInt2(attrib.name)
}

fun KslVertexStage.vertexAttribInt3(attrib: Attribute): KslExprInt3 {
    check(attrib.type == GlslType.VEC_3I) { "Attribute $attrib is expected to have type VEC_3I but has ${attrib.type}" }
    return vertexAttribInt3(attrib.name)
}

fun KslVertexStage.vertexAttribInt4(attrib: Attribute): KslExprInt4 {
    check(attrib.type == GlslType.VEC_4I) { "Attribute $attrib is expected to have type VEC_4I but has ${attrib.type}" }
    return vertexAttribInt4(attrib.name)
}

fun KslVertexStage.instanceAttribFloat1(attrib: Attribute): KslExprFloat1 {
    check(attrib.type == GlslType.FLOAT) { "Attribute $attrib is expected to have type FLOAT but has ${attrib.type}" }
    return instanceAttribFloat1(attrib.name)
}

fun KslVertexStage.instanceAttribFloat2(attrib: Attribute): KslExprFloat2 {
    check(attrib.type == GlslType.VEC_2F) { "Attribute $attrib is expected to have type VEC_2F but has ${attrib.type}" }
    return instanceAttribFloat2(attrib.name)
}

fun KslVertexStage.instanceAttribFloat3(attrib: Attribute): KslExprFloat3 {
    check(attrib.type == GlslType.VEC_3F) { "Attribute $attrib is expected to have type VEC_3F but has ${attrib.type}" }
    return instanceAttribFloat3(attrib.name)
}

fun KslVertexStage.instanceAttribFloat4(attrib: Attribute): KslExprFloat4 {
    check(attrib.type == GlslType.VEC_4F) { "Attribute $attrib is expected to have type VEC_4F but has ${attrib.type}" }
    return instanceAttribFloat4(attrib.name)
}

fun KslVertexStage.instanceAttribMat3(attrib: Attribute): KslExprMat3 {
    check(attrib.type == GlslType.MAT_3F) { "Attribute $attrib is expected to have type MAT_3F but has ${attrib.type}" }
    return instanceAttribMat3(attrib.name)
}

fun KslVertexStage.instanceAttribMat4(attrib: Attribute): KslExprMat4 {
    check(attrib.type == GlslType.MAT_4F) { "Attribute $attrib is expected to have type MAT_4F but has ${attrib.type}" }
    return instanceAttribMat4(attrib.name)
}

