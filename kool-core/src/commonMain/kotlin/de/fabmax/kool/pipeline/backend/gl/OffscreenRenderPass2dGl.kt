package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*

class OffscreenRenderPass2dGl(val parent: OffscreenRenderPass2d, val backend: RenderBackendGl) : OffscreenPass2dImpl {
    private val gl = backend.gl

    internal val fbos = mutableListOf<GlFramebuffer>()
    internal val rbos = mutableListOf<GlRenderbuffer>()

    internal val colorTextures = Array(parent.colorAttachments.size) { gl.NULL_TEXTURE }
    internal var depthTexture = gl.NULL_TEXTURE

    private val drawMipLevels = parent.drawMipLevels
    private val renderMipLevels: Int = if (drawMipLevels) { parent.mipLevels } else { 1 }

    private var isCreated = false

    override fun draw(ctx: KoolContext) {
        if (!isCreated) {
            create()
        }

        val needsCopy = parent.copyTargetsColor.isNotEmpty()

        if (isCreated) {
            val pass = parent
            for (mipLevel in 0 until renderMipLevels) {
                pass.onSetupMipLevel?.invoke(mipLevel, ctx)
                for (i in pass.views.indices) {
                    pass.views[i].viewport.set(0, 0, pass.getMipWidth(mipLevel), pass.getMipHeight(mipLevel))
                }
                gl.bindFramebuffer(gl.FRAMEBUFFER, fbos[mipLevel])
                backend.queueRenderer.renderViews(pass)

                if (needsCopy && !backend.capabilities.canFastCopyTextures) {
                    // use fallback / slightly slower texture copy method
                    copyToTexturesCompat(mipLevel, !parent.drawMipLevels)
                }
            }
            gl.bindFramebuffer(gl.FRAMEBUFFER, gl.NULL_FRAMEBUFFER)

            if (!drawMipLevels) {
                for (i in colorTextures.indices) {
                    gl.bindTexture(gl.TEXTURE_2D, colorTextures[i])
                    gl.generateMipmap(gl.TEXTURE_2D)
                }
            }

            if (needsCopy && backend.capabilities.canFastCopyTextures) {
                // use fast texture copy method, requires OpenGL 4.3 or higher
                backend.copyTexturesFast(this)
            }
        }
    }

    private fun copyToTexturesCompat(mipLevel: Int, generateMipMaps: Boolean) {
        gl.readBuffer(gl.COLOR_ATTACHMENT0)
        for (i in parent.copyTargetsColor.indices) {
            val copyTarget = parent.copyTargetsColor[i]
            var width = copyTarget.loadedTexture?.width ?: 0
            var height = copyTarget.loadedTexture?.height ?: 0
            if (width != parent.width || height != parent.height) {
                // recreate target texture if size has changed
                copyTarget.loadedTexture?.dispose()
                copyTarget.createCopyTexColor(parent, backend)
                width = copyTarget.loadedTexture!!.width
                height = copyTarget.loadedTexture!!.height
            }
            width = width shr mipLevel
            height = height shr mipLevel
            val target = copyTarget.loadedTexture as LoadedTextureGl
            gl.bindTexture(gl.TEXTURE_2D, target.texture)
            gl.copyTexSubImage2D(gl.TEXTURE_2D, mipLevel, 0, 0, 0, 0, width, height)
        }

        if (generateMipMaps) {
            for (i in parent.copyTargetsColor.indices) {
                val copyTarget = parent.copyTargetsColor[i]
                val target = copyTarget.loadedTexture as LoadedTextureGl
                gl.bindTexture(gl.TEXTURE_2D, target.texture)
                gl.generateMipmap(gl.TEXTURE_2D)
            }
        }
    }

    override fun dispose(ctx: KoolContext) {
        fbos.forEach { gl.deleteFramebuffer(it) }
        rbos.forEach { gl.deleteRenderbuffer(it) }
        fbos.clear()
        rbos.clear()

        parent.colorTextures.forEach { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }
        parent.depthTexture?.let { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }

        for (i in colorTextures.indices) { colorTextures[i] = gl.NULL_TEXTURE }
        depthTexture = gl.NULL_TEXTURE
        isCreated = false
    }

    override fun applySize(width: Int, height: Int, ctx: KoolContext) {
        dispose(ctx)
        create()
    }

    private fun create() {
        if (parent.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
            createColorTextures()
        }
        if (parent.depthRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
            createDepthTexture()
        }

        for (i in 0 until renderMipLevels) {
            val mipWidth = parent.getMipWidth(i)
            val mipHeight = parent.getMipHeight(i)
            val fbo = gl.createFramebuffer()
            gl.bindFramebuffer(gl.FRAMEBUFFER, fbo)

            if (parent.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
                colorTextures.forEachIndexed { iAttachment, tex ->
                    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0 + iAttachment, gl.TEXTURE_2D, tex, i)
                }
                val attachments = IntArray(colorTextures.size) { gl.COLOR_ATTACHMENT0 + it }
                gl.drawBuffers(attachments)
            } else {
                val rbo = gl.createRenderbuffer()
                gl.bindRenderbuffer(gl.RENDERBUFFER, rbo)
                gl.renderbufferStorage(gl.RENDERBUFFER, TexFormat.R.glInternalFormat(gl), mipWidth, mipHeight)
                gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.RENDERBUFFER, rbo)
                rbos += rbo
            }

            if (parent.depthRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
                gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.TEXTURE_2D, depthTexture, i)
            } else {
                val rbo = gl.createRenderbuffer()
                gl.bindRenderbuffer(gl.RENDERBUFFER, rbo)
                gl.renderbufferStorage(gl.RENDERBUFFER, gl.DEPTH_COMPONENT24, mipWidth, mipHeight)
                gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.RENDERBUFFER, rbo)
                rbos += rbo
            }
            fbos += fbo

            check(gl.checkFramebufferStatus(gl.FRAMEBUFFER) == gl.FRAMEBUFFER_COMPLETE) {
                "OffscreenRenderPass2dGl: Framebuffer incomplete: ${parent.name}, level: $i"
            }
        }
        isCreated = true
    }

    private fun createColorTextures() {
        for (i in parent.colorTextures.indices) {
            val cfg = parent.colorAttachments[i]

            if (cfg.providedTexture != null) {
                colorTextures[i] = (cfg.providedTexture.loadedTexture as LoadedTextureGl).texture

            } else {
                val format = cfg.colorFormat
                val intFormat = format.glInternalFormat(gl)
                val width = parent.width
                val height = parent.height
                val mipLevels = parent.mipLevels

                val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, format.pxSize)
                val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), estSize, backend)
                tex.setSize(width, height, 1)
                tex.applySamplerProps(parent.colorTextures[i].props)
                gl.texStorage2D(gl.TEXTURE_2D, mipLevels, intFormat, width, height)

                colorTextures[i] = tex.texture
                parent.colorTextures[i].loadedTexture = tex
                parent.colorTextures[i].loadingState = Texture.LoadingState.LOADED
            }
        }
    }

    private fun createDepthTexture() {
        val cfg = parent.depthAttachment!!

        if (cfg.providedTexture != null) {
            depthTexture = (cfg.providedTexture.loadedTexture as LoadedTextureGl).texture

        } else {
            val intFormat = gl.DEPTH_COMPONENT32F
            val width = parent.width
            val height = parent.height
            val mipLevels = parent.mipLevels
            val depthCfg = parent.depthAttachment

            val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, 4)
            val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), estSize, backend)
            tex.setSize(width, height, 1)
            tex.applySamplerProps(parent.depthTexture!!.props)
            if (depthCfg.depthCompareOp != DepthCompareOp.DISABLED) {
                gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_COMPARE_MODE, gl.COMPARE_REF_TO_TEXTURE)
                gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_COMPARE_FUNC, depthCfg.depthCompareOp.glOp(gl))
            }
            gl.texStorage2D(gl.TEXTURE_2D, mipLevels, intFormat, width, height)

            depthTexture = tex.texture
            parent.depthTexture.loadedTexture = tex
            parent.depthTexture.loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun Texture2d.createCopyTexColor(pass: OffscreenRenderPass2d, backend: RenderBackendGl) {
        val gl = backend.gl
        val intFormat = props.format.glInternalFormat(gl)
        val width = pass.width
        val height = pass.height
        val mipLevels = pass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, props.format.pxSize)
        val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), estSize, backend)
        tex.setSize(width, height, 1)
        tex.applySamplerProps(props)
        gl.texStorage2D(gl.TEXTURE_2D, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }
}