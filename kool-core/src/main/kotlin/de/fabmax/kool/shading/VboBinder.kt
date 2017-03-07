package de.fabmax.kool.shading

import de.fabmax.kool.gl.BufferResource
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.platform.GL


/**
 * A VboBinder for the specified buffer. By default the type is set to GL_FLOAT and the offset to 0.
 *
 * @author fabmax
 */
class VboBinder(
        val vbo: BufferResource,
        var elemSize: Int,
        var strideBytes: Int,
        var offset: Int = 0,
        var type: Int = GL.FLOAT) {

    /**
     * Is called by the used shader to bind a vertex attribute buffer to the specified target.
     *
     * @param target    the buffer target index as used in glVertexAttribPointer()
     */
    fun bindAttribute(target: Int, ctx: RenderContext) {
        vbo.bind(ctx)
        GL.vertexAttribPointer(target, elemSize, type, false, strideBytes, offset * 4)
    }
}
