package de.fabmax.kool.platform.webgl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.OffscreenPassCubeImpl
import de.fabmax.kool.pipeline.OffscreenRenderPassCube
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureCube
import de.fabmax.kool.pipeline.backend.gl.GlImpl
import de.fabmax.kool.pipeline.backend.gl.WebGL2RenderingContext.Companion.DEPTH_COMPONENT24
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.glInternalFormat
import de.fabmax.kool.platform.pxSize
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLRenderbuffer
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_ATTACHMENT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.RENDERBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_X
import org.khronos.webgl.WebGLTexture

class OffscreenPassCubeWebGl(val offscreenPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
    private val fbos = mutableListOf<WebGLFramebuffer?>()
    private val rbos = mutableListOf<WebGLRenderbuffer?>()

    private var isCreated = false
    private var colorTex: WebGLTexture? = null

    override fun draw(ctx: KoolContext) {
        ctx as JsContext

        if (!isCreated) {
            create(ctx)
        }

        for (mipLevel in 0 until offscreenPass.mipLevels) {
            offscreenPass.onSetupMipLevel?.invoke(mipLevel, ctx)
            for (i in offscreenPass.views.indices) {
                offscreenPass.views[i].viewport.set(
                    0,
                    0,
                    offscreenPass.getMipWidth(mipLevel),
                    offscreenPass.getMipHeight(mipLevel)
                )
            }
            GlImpl.gl.bindFramebuffer(FRAMEBUFFER, fbos[mipLevel])

            for (i in CUBE_VIEWS.indices) {
                val cubeView = CUBE_VIEWS[i]
                val passView = offscreenPass.views[cubeView.index]
                GlImpl.gl.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_CUBE_MAP_POSITIVE_X + i, colorTex, mipLevel)
                (ctx.backend as RenderBackendLegacyWebGl).queueRenderer.renderView(passView)
                copyToTextures(i, mipLevel, ctx)
            }
        }
        GlImpl.gl.bindFramebuffer(FRAMEBUFFER, null)
    }

    private fun copyToTextures(face: Int, mipLevel: Int, ctx: JsContext) {
        GlImpl.gl.readBuffer(COLOR_ATTACHMENT0)

        for (i in offscreenPass.copyTargetsColor.indices) {
            val copyTarget = offscreenPass.copyTargetsColor[i]
            var width = copyTarget.loadedTexture?.width ?: 0
            var height = copyTarget.loadedTexture?.height ?: 0
            if (width != offscreenPass.width || height != offscreenPass.height) {
                copyTarget.loadedTexture?.dispose()
                copyTarget.createCopyTexColor(ctx)
                width = copyTarget.loadedTexture!!.width
                height = copyTarget.loadedTexture!!.height
            }
            width = width shr mipLevel
            height = height shr mipLevel
            val target = copyTarget.loadedTexture as LoadedTextureWebGl
            GlImpl.gl.bindTexture(TEXTURE_CUBE_MAP, target.texture)
            GlImpl.gl.copyTexSubImage2D(TEXTURE_CUBE_MAP_POSITIVE_X + face, mipLevel, 0, 0, 0, 0, width, height)
        }
    }

    override fun dispose(ctx: KoolContext) {
        ctx as JsContext
        fbos.forEach { GlImpl.gl.deleteFramebuffer(it) }
        rbos.forEach { GlImpl.gl.deleteRenderbuffer(it) }
        fbos.clear()
        rbos.clear()

        offscreenPass.colorTextures.forEach { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }
        offscreenPass.depthTexture?.let { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }

        colorTex = null
        isCreated = false
    }

    override fun applySize(width: Int, height: Int, ctx: KoolContext) {
        dispose(ctx)
        create(ctx as JsContext)
    }

    private fun create(ctx: JsContext) {
        val gl = GlImpl.gl

        createColorTex(ctx)

        for (i in 0 until offscreenPass.mipLevels) {
            val fbo = gl.createFramebuffer()
            val rbo = gl.createRenderbuffer()

            val mipWidth = offscreenPass.getMipWidth(i)
            val mipHeight = offscreenPass.getMipHeight(i)

            gl.bindFramebuffer(FRAMEBUFFER, fbo)
            gl.bindRenderbuffer(RENDERBUFFER, rbo)
            gl.renderbufferStorage(RENDERBUFFER, DEPTH_COMPONENT24, mipWidth, mipHeight)
            gl.framebufferRenderbuffer(FRAMEBUFFER, DEPTH_ATTACHMENT, RENDERBUFFER, rbo)

            fbos += fbo
            rbos += rbo
        }

        isCreated = true
    }

    private fun createColorTex(ctx: JsContext) {
        val format = offscreenPass.colorAttachments[0].colorFormat
        val intFormat = format.glInternalFormat
        val width = offscreenPass.width
        val height = offscreenPass.height
        val mipLevels = offscreenPass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 6, mipLevels, format.pxSize)
        val tex = LoadedTextureWebGl(ctx, TEXTURE_CUBE_MAP, GlImpl.gl.createTexture(), estSize)
        tex.setSize(width, height, 1)
        tex.applySamplerProps(offscreenPass.colorTexture!!.props)
        GlImpl.gl.texStorage2D(TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)

        colorTex = tex.texture
        offscreenPass.colorTexture!!.loadedTexture = tex
        offscreenPass.colorTexture!!.loadingState = Texture.LoadingState.LOADED
    }

    private fun TextureCube.createCopyTexColor(ctx: JsContext) {
        val intFormat = props.format.glInternalFormat
        val width = offscreenPass.width
        val height = offscreenPass.height
        val mipLevels = offscreenPass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 6, mipLevels, props.format.pxSize)
        val tex = LoadedTextureWebGl(ctx, TEXTURE_CUBE_MAP, GlImpl.gl.createTexture(), estSize)
        tex.setSize(width, height, 1)
        tex.applySamplerProps(props)
        GlImpl.gl.texStorage2D(TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }

    companion object {
        private val CUBE_VIEWS = Array(6) { i ->
            when (i) {
                0 -> OffscreenRenderPassCube.ViewDirection.RIGHT
                1 -> OffscreenRenderPassCube.ViewDirection.LEFT
                2 -> OffscreenRenderPassCube.ViewDirection.UP
                3 -> OffscreenRenderPassCube.ViewDirection.DOWN
                4 -> OffscreenRenderPassCube.ViewDirection.FRONT
                5 -> OffscreenRenderPassCube.ViewDirection.BACK
                else -> throw IllegalStateException()
            }
        }
    }
}