package de.fabmax.kool.platform.webgl

import de.fabmax.kool.platform.JsContext
import org.khronos.webgl.WebGLRenderingContext.Companion.FLOAT
import org.khronos.webgl.WebGLRenderingContext.Companion.INT
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_INT


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
        val type: Int = FLOAT) {

    /**
     * Is called by the used shader to bind a vertex attribute buffer to the specified target.
     *
     * @param target    the buffer target index as used in glVertexAttribPointer()
     */
    fun bindAttribute(target: Int, ctx: JsContext) {
        vbo.bind(ctx)
        if (type == INT || type == UNSIGNED_INT) {
            ctx.gl.vertexAttribIPointer(target, elemSize, type, strideBytes, offset * 4)
        } else {
            ctx.gl.vertexAttribPointer(target, elemSize, type, false, strideBytes, offset * 4)
        }
    }
}
