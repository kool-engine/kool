package de.fabmax.kool.shading

/**
 * @author fabmax
 */

enum class AttributeType(val size: Int, val isInt: Boolean) {
    FLOAT(4, false),
    VEC_2F(8, false),
    VEC_3F(12, false),
    VEC_4F(16, false),
    COLOR_4F(16, false),
    INT(4, true),
    VEC_2I(8, true),
    VEC_3I(12, true),
    VEC_4I(16, true)
}

data class Attribute(val name: String, val type: AttributeType) {
    var glslSrcName = name
    var locationOffset = 0
    var divisor = 0

    override fun toString(): String {
        return name
    }

    companion object {
        val POSITIONS = Attribute("attrib_positions", AttributeType.VEC_3F)
        val NORMALS = Attribute("attrib_normals", AttributeType.VEC_3F)
        val TANGENTS = Attribute("attrib_tangents", AttributeType.VEC_3F)
        val TEXTURE_COORDS = Attribute("attrib_texture_coords", AttributeType.VEC_2F)
        val COLORS = Attribute("attrib_colors", AttributeType.COLOR_4F)
    }
}