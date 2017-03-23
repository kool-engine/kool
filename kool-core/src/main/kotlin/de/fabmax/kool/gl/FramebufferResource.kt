package de.fabmax.kool.gl

import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.TextureProps
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext

fun colorAttachmentTex(width: Int, height: Int, minFilter: Int, magFilter: Int): Texture {
    val id = Math.random()
    val texProps = TextureProps("framebuffer-colorAttachment-$id",
            minFilter, magFilter, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE)
    return Texture(texProps, { FbTexData(width, height) })
}

class FramebufferResource private constructor(glRef: Any, val colorAttachment: Texture, ctx: RenderContext) :
        GlResource(glRef, Type.FRAMEBUFFER) {
    companion object {
        fun create(width: Int, height: Int, ctx: RenderContext): FramebufferResource {
            val id = ctx.generateUniqueId()
            val texProps = TextureProps("framebuffer-colorAttachment-$id",
                    GL.LINEAR, GL.LINEAR, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE)
            return create(Texture(texProps, { FbTexData(width, height) }), ctx)
        }

        fun create(colorAttachment: Texture, ctx: RenderContext): FramebufferResource {
            return FramebufferResource(GL.createFramebuffer(), colorAttachment, ctx)
        }
    }

    private var isFbComplete = false

    override fun delete(ctx: RenderContext) {
        GL.deleteFramebuffer(this)
        super.delete(ctx)
    }

    fun bind(ctx: RenderContext) {
        GL.bindFramebuffer(GL.FRAMEBUFFER, this)
        if (!isFbComplete) {
            isFbComplete = true

            ctx.textureMgr.bindTexture(colorAttachment, ctx)
            GL.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, colorAttachment.res!!, 0)
        }

        ctx.pushAttributes()
        ctx.viewportX = 0
        ctx.viewportY = 0
        ctx.viewportWidth = colorAttachment.width
        ctx.viewportHeight = colorAttachment.height
        ctx.applyAttributes()
    }

    fun unbind(ctx: RenderContext) {
        GL.bindFramebuffer(GL.FRAMEBUFFER, null)
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

        GL.texImage2D(GL.TEXTURE_2D, 0, GL.RGBA, width, height, 0, GL.RGBA, GL.UNSIGNED_BYTE, null)
    }
}