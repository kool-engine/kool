package de.fabmax.kool.pipeline

/**
 * @author fabmax
 */

enum class GlslType(val size: Int, val isInt: Boolean, val glslType: String) {
    FLOAT(4, false, "float"),
    VEC_2F(8, false, "vec2"),
    VEC_3F(12, false, "vec3"),
    VEC_4F(16, false, "vec4"),

    INT(4, true, "int"),
    VEC_2I(8, true, "ivec2"),
    VEC_3I(12, true, "ivec3"),
    VEC_4I(16, true, "ivec4"),

    MAT_2F(16, false, "mat2"),
    MAT_3F(36, false, "mat3"),
    MAT_4F(64, false, "mat4")
}

data class Attribute(val name: String, val type: GlslType) {
    val props = PlatformAttributeProps(this)

    override fun toString(): String {
        return name
    }

    companion object {
        val POSITIONS = Attribute("attrib_positions", GlslType.VEC_3F)
        val NORMALS = Attribute("attrib_normals", GlslType.VEC_3F)
        val TANGENTS = Attribute("attrib_tangents", GlslType.VEC_4F)
        val TEXTURE_COORDS = Attribute("attrib_texture_coords", GlslType.VEC_2F)
        val COLORS = Attribute("attrib_colors", GlslType.VEC_4F)
    }
}

expect class PlatformAttributeProps(attribute: Attribute) {
    val nSlots: Int
}