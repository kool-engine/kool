package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.util.Time

class SceneRenderPass(val numSamples: Int, val backend: RenderBackendGl) {
    private val gl = backend.gl

    private val renderFbo: GlFramebuffer by lazy { gl.createFramebuffer() }
    private val renderColor: GlRenderbuffer by lazy { gl.createRenderbuffer() }
    private val renderDepth: GlRenderbuffer by lazy { gl.createRenderbuffer() }

    private val resolveFbo: GlFramebuffer by lazy { gl.createFramebuffer() }
    private val resolvedColor = Texture2d(TextureProps(generateMipMaps = false, defaultSamplerSettings = SamplerSettings().clamped().nearest()))
    private val resolveDepth: GlRenderbuffer by lazy { gl.createRenderbuffer() }

    private val renderSize = MutableVec2i()

    internal var resolveDirect = false

    private val blitScene: Scene by lazy {
        Scene().apply {
            addTextureMesh {
                generateFullscreenQuad()
                shader = KslUnlitShader {
                    pipeline { depthTest = DepthCompareOp.ALWAYS }
                    color { textureData(resolvedColor) }
                    modelCustomizer = { fullscreenQuadVertexStage(null) }
                }
            }
        }
    }

    private val frameBufferSetter = QueueRenderer.FrameBufferSetter { viewIndex, _ ->
        if (viewIndex == 0) {
            gl.bindFramebuffer(gl.FRAMEBUFFER, renderFbo)
        }
    }

    fun draw(scene: Scene) {
        val scenePass = scene.mainRenderPass
        val t = if (scenePass.isProfileTimes) Time.precisionTime else 0.0

        backend.queueRenderer.renderViews(scenePass, frameBufferSetter)

        if (scenePass.isProfileTimes) {
            scenePass.tDraw = Time.precisionTime - t
        }
        scenePass.afterDraw()
    }

    fun captureFramebuffer(scene: Scene, ctx: KoolContext) {
        resolve(ctx)

        val viewport = scene.mainRenderPass.viewport
        val targetTex = scene.capturedFramebuffer

        if (targetTex.loadedTexture == null) {
            targetTex.loadedTexture = LoadedTextureGl(
                target = gl.TEXTURE_2D,
                glTexture = gl.createTexture(),
                backend = backend,
                texture = targetTex,
                estimatedSize = viewport.width * viewport.height * 4L
            )
        }
        val tex = targetTex.loadedTexture as LoadedTextureGl
        tex.bind()

        if (tex.width != viewport.width || tex.height != viewport.height) {
            tex.setSize(viewport.width, viewport.height, 1)
            tex.applySamplerSettings(targetTex.props.defaultSamplerSettings)
            targetTex.loadingState = Texture.LoadingState.LOADED
            gl.texImage2D(tex.target, 0, gl.RGBA8, viewport.width, viewport.height, 0, gl.RGBA, gl.UNSIGNED_BYTE, null)
        }

        gl.bindFramebuffer(gl.READ_FRAMEBUFFER, gl.DEFAULT_FRAMEBUFFER)
        gl.readBuffer(gl.BACK)
        gl.copyTexSubImage2D(tex.target, 0, 0, 0, viewport.x, viewport.y, viewport.width, viewport.height)
    }

    fun resolve(ctx: KoolContext) {
        if (resolveDirect) {
            // direct resolve method: directly blit multi-sampled frame buffer into the default frame buffer.
            // should be a bit faster, but does not work on web gl

            gl.bindFramebuffer(gl.READ_FRAMEBUFFER, renderFbo)
            gl.bindFramebuffer(gl.DRAW_FRAMEBUFFER, gl.DEFAULT_FRAMEBUFFER)

            gl.blitFramebuffer(
                0, 0, renderSize.x, renderSize.y,
                0, 0, renderSize.x, renderSize.y,
                gl.COLOR_BUFFER_BIT, gl.NEAREST
            )

        } else {
            // default resolve method: blit multi-sampled frame buffer into non-multi-sampled frame buffer with texture
            // attachment and render that texture to the default frame buffer.
            // Comes with some overhead but works on all platforms.

            gl.bindFramebuffer(gl.READ_FRAMEBUFFER, renderFbo)
            gl.bindFramebuffer(gl.DRAW_FRAMEBUFFER, resolveFbo)

            gl.blitFramebuffer(
                0, 0, renderSize.x, renderSize.y,
                0, 0, renderSize.x, renderSize.y,
                gl.COLOR_BUFFER_BIT, gl.NEAREST
            )

            gl.bindFramebuffer(gl.FRAMEBUFFER, gl.DEFAULT_FRAMEBUFFER)
            blitScene.mainRenderPass.update(ctx)
            blitScene.mainRenderPass.collectDrawCommands(ctx)
            backend.queueRenderer.renderView(blitScene.mainRenderPass.screenView, 0)
        }
    }

    fun applySize(width: Int, height: Int) {
        if (width == renderSize.x && height == renderSize.y) {
            return
        }
        renderSize.set(width, height)

        val colorFormat = TexFormat.RGBA.glInternalFormat(gl)
        val depthFormat = gl.DEPTH_COMPONENT32F
        val isMultiSampled = numSamples > 1

        gl.bindRenderbuffer(gl.RENDERBUFFER, renderColor)
        if (isMultiSampled) {
            gl.renderbufferStorageMultisample(gl.RENDERBUFFER, backend.numSamples, colorFormat, width, height)
        } else {
            gl.renderbufferStorage(gl.RENDERBUFFER, colorFormat, width, height)
        }

        gl.bindRenderbuffer(gl.RENDERBUFFER, renderDepth)
        if (isMultiSampled) {
            gl.renderbufferStorageMultisample(gl.RENDERBUFFER, backend.numSamples, depthFormat, width, height)
        } else {
            gl.renderbufferStorage(gl.RENDERBUFFER, depthFormat, width, height)
        }

        gl.bindFramebuffer(gl.FRAMEBUFFER, renderFbo)
        gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.RENDERBUFFER, renderColor)
        gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.RENDERBUFFER, renderDepth)

        if (!resolveDirect) {
            makeResolveFbo(width, height)
        }
    }

    private fun makeResolveFbo(width: Int, height: Int) {
        var loadedTex = resolvedColor.loadedTexture as LoadedTextureGl?
        loadedTex?.release()

        val estSize = Texture.estimatedTexSize(renderSize.x, renderSize.y, 1, 1, 4).toLong()
        loadedTex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, resolvedColor, estSize)
        resolvedColor.loadedTexture = loadedTex
        resolvedColor.loadingState = Texture.LoadingState.LOADED

        loadedTex.setSize(width, height, 1)
        loadedTex.bind()
        gl.texStorage2D(gl.TEXTURE_2D, 1, TexFormat.RGBA.glInternalFormat(gl), renderSize.x, renderSize.y)
        loadedTex.applySamplerSettings(resolvedColor.props.defaultSamplerSettings)

        gl.bindRenderbuffer(gl.RENDERBUFFER, resolveDepth)
        gl.renderbufferStorage(gl.RENDERBUFFER, gl.DEPTH_COMPONENT32F, width, height)

        gl.bindFramebuffer(gl.FRAMEBUFFER, resolveFbo)
        gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.RENDERBUFFER, resolveDepth)
        gl.framebufferTexture2D(
            target = gl.FRAMEBUFFER,
            attachment = gl.COLOR_ATTACHMENT0,
            textarget = gl.TEXTURE_2D,
            texture = (resolvedColor.loadedTexture as LoadedTextureGl).glTexture,
            level = 0
        )
        gl.drawBuffers(intArrayOf(gl.COLOR_ATTACHMENT0))
    }
}