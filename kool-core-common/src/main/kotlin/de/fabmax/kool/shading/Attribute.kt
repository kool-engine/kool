package de.fabmax.kool.shading

import de.fabmax.kool.gl.GL_FLOAT
import de.fabmax.kool.gl.GL_INT

/**
 * @author fabmax
 */

enum class AttributeType(val size: Int, val isInt: Boolean, val glType: Int) {
    FLOAT(1, false, GL_FLOAT),
    VEC_2F(2, false, GL_FLOAT),
    VEC_3F(3, false, GL_FLOAT),
    VEC_4F(4, false, GL_FLOAT),

    COLOR_4F(4, false, GL_FLOAT),

    INT(1, true, GL_INT),
    VEC_2I(2, true, GL_INT),
    VEC_3I(3, true, GL_INT),
    VEC_4I(4, true, GL_INT)
}

data class Attribute(val name: String, val type: AttributeType) {
    companion object {
        val POSITIONS = Attribute("attrib_positions", AttributeType.VEC_3F)
        val NORMALS = Attribute("attrib_normals", AttributeType.VEC_3F)
        val TANGENTS = Attribute("attrib_tangents", AttributeType.VEC_3F)
        val TEXTURE_COORDS = Attribute("attrib_texture_coords", AttributeType.VEC_2F)
        val COLORS = Attribute("attrib_colors", AttributeType.COLOR_4F)
    }

    override fun toString(): String {
        return name
    }
}