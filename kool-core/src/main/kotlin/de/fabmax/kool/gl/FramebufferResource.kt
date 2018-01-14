package de.fabmax.kool.gl

import de.fabmax.kool.RenderContext
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.TextureProps
import de.fabmax.kool.math.random

fun colorAttachmentTex(width: Int, height: Int, minFilter: Int, magFilter: Int): Texture {
    val id = random()
    val texProps = TextureProps("framebuffer-colorAttachment-$id",
            minFilter, magFilter, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0)
    return Texture(texProps, { FbTexData(width, height) })
}

class FramebufferResource private constructor(glRef: Any, val colorAttachment: Texture, ctx: RenderContext) :
        GlResource(glRef, Type.FRAMEBUFFER, ctx) {
    companion object {
        fun create(width: Int, height: Int, ctx: RenderContext): FramebufferResource {
            val id = ctx.generateUniqueId()
            val texProps = TextureProps("framebuffer-colorAttachment-$id",
                    GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0)
            return create(Texture(texProps, { FbTexData(width, height) }), ctx)
        }

        fun create(colorAttachment: Texture, ctx: RenderContext): FramebufferResource {
            return FramebufferResource(glCreateFramebuffer(), colorAttachment, ctx)
        }
    }

    private var isFbComplete = false

    override fun delete(ctx: RenderContext) {
        glDeleteFramebuffer(this)
        super.delete(ctx)
    }

    fun bind(ctx: RenderContext) {
        glBindFramebuffer(GL_FRAMEBUFFER, this)
        if (!isFbComplete) {
            isFbComplete = true

            ctx.textureMgr.bindTexture(colorAttachment, ctx)
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment.res!!, 0)
        }

        ctx.pushAttributes()
        ctx.viewport = RenderContext.Viewport(0, 0, colorAttachment.width, colorAttachment.height)
        ctx.applyAttributes()
    }

    fun unbind(ctx: RenderContext) {
        glBindFramebuffer(GL_FRAMEBUFFER, null)
        ctx.popAttributes()
    }
}

private class FbTexData(width: Int, height: Int) : TextureData() {
    init {
        this.isAvailable = true
        this.width = width
        this.height = height
    }

    override fun onLoad(texture: Texture, ctx: RenderContext) {
        // texture data is not loaded but generated when drawn to the framebuffer - do nothing here

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
    }
}