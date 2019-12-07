package de.fabmax.kool.shading

import de.fabmax.kool.gl.GL_FLOAT
import de.fabmax.kool.gl.GL_INT

/**
 * @author fabmax
 */

enum class AttributeType(val size: Int, val isInt: Boolean, val glType: Int, val glslTypeName: String) {
    FLOAT(1, false, GL_FLOAT, "float"),
    VEC_2F(2, false, GL_FLOAT, "vec2"),
    VEC_3F(3, false, GL_FLOAT, "vec3"),
    VEC_4F(4, false, GL_FLOAT, "vec4"),
    COLOR_4F(4, false, GL_FLOAT, "vec4"),
    INT(1, true, GL_INT, "int"),
    VEC_2I(2, true, GL_INT, "ivec2"),
    VEC_3I(3, true, GL_INT, "ivec3"),
    VEC_4I(4, true, GL_INT, "ivec4")
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