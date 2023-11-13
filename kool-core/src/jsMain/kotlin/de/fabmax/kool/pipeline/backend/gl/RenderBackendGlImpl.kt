package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.gl.GlImpl.arrayBufferView
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logE
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.IMPLEMENTATION_COLOR_READ_FORMAT
import org.khronos.webgl.WebGLRenderingContext.Companion.IMPLEMENTATION_COLOR_READ_TYPE

class RenderBackendGlImpl(ctx: KoolContext) : RenderBackendGl(GlImpl, ctx) {
    override val name = "Common GL backend"
    override val version: ApiVersion = ApiVersion(2, 0, GlFlavor.WebGL, "2.0", null)
    override val capabilities: GlCapabilities = determineCapabilities()

    init {
        check(GlImpl.gl.getExtension("EXT_color_buffer_float") != null) {
            "WebGL 2 implementation lacks support for float textures (EXT_color_buffer_float)"
        }
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, 0, ctx.windowWidth, ctx.windowHeight)
    }

    override fun copyTexturesFast(renderPass: OffscreenRenderPass2dGl) {
        throw IllegalStateException("WebGL implementation cannot copy fast")
    }

    override fun copyTexturesFast(renderPass: OffscreenRenderPassCubeGl) {
        throw IllegalStateException("WebGL implementation cannot copy fast")
    }

    override fun readTexturePixels(src: LoadedTextureGl, dst: TextureData) {
        val fb = gl.createFramebuffer()
        gl.bindFramebuffer(gl.FRAMEBUFFER, fb)
        gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, src.glTexture, 0)

        if (gl.checkFramebufferStatus(gl.FRAMEBUFFER) == gl.FRAMEBUFFER_COMPLETE) {
            val format = GlImpl.gl.getParameter(IMPLEMENTATION_COLOR_READ_FORMAT) as Int
            val type = GlImpl.gl.getParameter(IMPLEMENTATION_COLOR_READ_TYPE) as Int
            GlImpl.gl.readPixels(0, 0, src.width, src.height, format, type, dst.arrayBufferView)
        } else {
            logE { "Failed reading pixels from framebuffer" }
        }
        gl.deleteFramebuffer(fb)
    }

    override fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
        when (offscreenPass) {
            is OffscreenRenderPass2d -> offscreenPass.impl.draw(ctx)
            is OffscreenRenderPassCube -> offscreenPass.impl.draw(ctx)
            is OffscreenRenderPass2dPingPong -> drawOffscreenPingPong(offscreenPass)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $offscreenPass")
        }
    }

    private fun drawOffscreenPingPong(offscreenPass: OffscreenRenderPass2dPingPong) {
        for (i in 0 until offscreenPass.pingPongPasses) {
            offscreenPass.onDrawPing?.invoke(i)
            offscreenPass.ping.impl.draw(ctx)

            offscreenPass.onDrawPong?.invoke(i)
            offscreenPass.pong.impl.draw(ctx)
        }
    }

    @Suppress("UnsafeCastFromDynamic")
    private fun determineCapabilities(): GlCapabilities {
        // check for anisotropic texture filtering support

        val extAnisotropic = GlImpl.gl.getExtension("EXT_texture_filter_anisotropic") ?:
            GlImpl.gl.getExtension("MOZ_EXT_texture_filter_anisotropic") ?:
            GlImpl.gl.getExtension("WEBKIT_EXT_texture_filter_anisotropic")

        val maxAnisotropy: Int
        val glTextureMaxAnisotropyExt: Int

        if (extAnisotropic != null) {
            maxAnisotropy = GlImpl.gl.getParameter(extAnisotropic.MAX_TEXTURE_MAX_ANISOTROPY_EXT) as Int
            glTextureMaxAnisotropyExt = extAnisotropic.TEXTURE_MAX_ANISOTROPY_EXT
        } else {
            maxAnisotropy = 1
            glTextureMaxAnisotropyExt = 0
        }

        val maxTexUnits = GlImpl.gl.getParameter(WebGLRenderingContext.MAX_TEXTURE_IMAGE_UNITS) as Int
        val canFastCopyTextures = false

        return GlCapabilities(
            maxTexUnits,
            maxAnisotropy,
            glTextureMaxAnisotropyExt,
            canFastCopyTextures
        )
    }

    override fun close(ctx: KoolContext) {
        // nothing to do here
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

}
