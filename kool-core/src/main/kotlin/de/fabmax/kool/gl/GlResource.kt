package de.fabmax.kool.gl

import de.fabmax.kool.platform.*

/**
 * @author fabmax
 */
abstract class GlResource constructor(glRef: Any, val type: Type) {
    enum class Type {
        BUFFER,
        FRAMEBUFFER,
        PROGRAM,
        RENDERBUFFER,
        SHADER,
        TEXTURE
    }

    var glRef: Any? = glRef
        protected set

    val isValid: Boolean
        get() = glRef != null

    open fun delete(ctx: RenderContext) {
        ctx.memoryMgr.deleted(this)
        glRef = null
    }
}
