package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext

/**
 * @author fabmax
 */
abstract class GlResource constructor(glRef: Any, val type: Type, ctx: KoolContext) {
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

    init {
        @Suppress("LeakingThis")
        ctx.memoryMgr.memoryAllocated(this, 0)
    }

    open fun delete(ctx: KoolContext) {
        ctx.memoryMgr.deleted(this)
        glRef = null
    }
}
