package de.fabmax.kool.platform.gl

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glVertexAttribIPointer


/**
 * A VboBinder for the specified buffer. By default the type is set to GL_FLOAT and the offset to 0.
 *
 * @author fabmax
 */
class VboBinder(
        val vbo: BufferResource,
        val elemSize: Int,
        val strideBytes: Int,
        val offset: Int = 0,
        val type: Int = GL_FLOAT) {

    /**
     * Is called by the used shader to bind a vertex attribute buffer to the specified target.
     *
     * @param target    the buffer target index as used in glVertexAttribPointer()
     */
    fun bindAttribute(target: Int) {
        vbo.bind()
        if (type == GL_INT || type == GL_UNSIGNED_INT) {
            glVertexAttribIPointer(target, elemSize, type, strideBytes, (offset * 4).toLong())
        } else {
            glVertexAttribPointer(target, elemSize, type, false, strideBytes, (offset * 4).toLong())
        }
    }
}
