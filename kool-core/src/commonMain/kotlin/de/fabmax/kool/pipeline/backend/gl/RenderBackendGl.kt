package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.generator.GlslGenerator
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logE

abstract class RenderBackendGl(internal val gl: GlApi, internal val ctx: KoolContext) : RenderBackend {
    override val name = "Common GL Backend"
    override val apiName: String
        get() = gl.version.versionName
    override val deviceName: String
        get() = gl.version.deviceInfo

    var numSamples = 1
        private set

    private val _projCorrectionMatrix = MutableMat4f()
    override val projCorrectionMatrix: Mat4f get() = _projCorrectionMatrix
    override val depthBiasMatrix: Mat4f = MutableMat4f().translate(0.5f, 0.5f, 0.5f).scale(0.5f)
    final override var isReversedDepthAvailable = false
        private set

    internal val queueRenderer = QueueRenderer(this)

    protected fun setupGl() {
        numSamples = gl.getInteger(gl.SAMPLES)
        gl.enable(gl.SCISSOR_TEST)

        if (gl.capabilities.hasClipControl) {
            // use zero-to-one depth clip space if available
            gl.clipControl(gl.LOWER_LEFT, gl.ZERO_TO_ONE)
            isReversedDepthAvailable = true
            _projCorrectionMatrix.set(
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.5f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        if (ctx.disposablePipelines.isNotEmpty()) {
            queueRenderer.disposePipelines(ctx.disposablePipelines)
            ctx.disposablePipelines.clear()
        }

        doOffscreenPasses(ctx.backgroundScene, ctx)

        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
                if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.BeforeRender) {
                    captureFramebuffer(scene)
                }
                doOffscreenPasses(scene, ctx)

                val t = if (scene.mainRenderPass.isProfileTimes) Time.precisionTime else 0.0
                scene.mainRenderPass.blitRenderPass?.let { blitFrameBuffers(it, scene.mainRenderPass, 0) }
                queueRenderer.renderViews(scene.mainRenderPass)
                scene.mainRenderPass.tDraw = if (scene.mainRenderPass.isProfileTimes) Time.precisionTime - t else 0.0

                if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.AfterRender) {
                    captureFramebuffer(scene)
                }
                scene.mainRenderPass.afterDraw(ctx)
            }
        }
    }

    internal open fun blitFrameBuffers(src: OffscreenRenderPass2d, dst: RenderPass, mipLevel: Int) {
        val srcPassImpl = src.impl as OffscreenRenderPass2dGl
        val dstBuffer = when (dst) {
            is OffscreenRenderPass2d -> (dst.impl as OffscreenRenderPass2dGl).fbos[mipLevel]
            is ScreenRenderPass -> gl.DEFAULT_FRAMEBUFFER
            else -> throw IllegalArgumentException("dst RenderPass has to be an OffscreenRenderPass2d or ScreenRenderPass")
        }

        if (srcPassImpl.fbos.isEmpty()) {
            logE { "blit source framebuffer is not available" }
            return
        }

        gl.bindFramebuffer(gl.READ_FRAMEBUFFER, srcPassImpl.fbos[0])
        gl.bindFramebuffer(gl.DRAW_FRAMEBUFFER, dstBuffer)

        val filter = if (src.width == dst.width && src.height == dst.height) {
            gl.NEAREST
        } else {
            gl.LINEAR
        }
        //println("${src.width} x ${src.height} -> ${dst.width} x ${dst.height} (linear: ${filter == gl.LINEAR})")
        gl.blitFramebuffer(
            0, 0, src.width, src.height,
            0, 0, dst.width, dst.height,
            gl.COLOR_BUFFER_BIT, filter
        )
        //println("blit buffers (src: ${src.name}/${srcPassImpl.fbos[0]}, dst: ${dst.name}/$dstBuffer), ${gl.getError()}")
    }

    override fun uploadTextureToGpu(tex: Texture, data: TextureData) {
        tex.loadedTexture = when (tex) {
            is Texture1d -> TextureLoaderGl.loadTexture1d(tex, data, this)
            is Texture2d -> TextureLoaderGl.loadTexture2d(tex, data, this)
            is Texture3d -> TextureLoaderGl.loadTexture3d(tex, data, this)
            is TextureCube -> TextureLoaderGl.loadTextureCube(tex, data, this)
            else -> throw IllegalArgumentException("Unsupported texture type: $tex")
        }
        tex.loadingState = Texture.LoadingState.LOADED
    }

    override fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl {
        return OffscreenRenderPass2dGl(parentPass, this)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
        return OffscreenRenderPassCubeGl(parentPass, this)
    }

    override fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout): ShaderCodeGl {
        val src = GlslGenerator().generateProgram(shader.program)
        if (shader.program.dumpCode) {
            src.dump()
        }
        return ShaderCodeGl(src.vertexSrc, src.fragmentSrc)
    }

    private fun doOffscreenPasses(scene: Scene, ctx: KoolContext) {
        for (i in scene.sortedOffscreenPasses.indices) {
            val pass = scene.sortedOffscreenPasses[i]
            if (pass.isEnabled) {
                val t = if (pass.isProfileTimes) Time.precisionTime else 0.0
                drawOffscreen(pass)
                pass.afterDraw(ctx)
                pass.tDraw = if (pass.isProfileTimes) Time.precisionTime - t else 0.0
            }
        }
    }

    private fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
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

    private fun captureFramebuffer(scene: Scene) {
        val targetTex = scene.capturedFramebuffer

        if (targetTex.loadedTexture == null) {
            targetTex.loadedTexture = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), this, targetTex, 4096 * 2048 * 4)
        }
        val tex = targetTex.loadedTexture as LoadedTextureGl

        val requiredTexWidth = nextPow2(scene.mainRenderPass.viewport.width)
        val requiredTexHeight = nextPow2(scene.mainRenderPass.viewport.height)
        if (tex.width != requiredTexWidth || tex.height != requiredTexHeight) {
            tex.setSize(requiredTexWidth, requiredTexHeight, 1)
            tex.applySamplerProps(
                TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE,
                addressModeV = AddressMode.CLAMP_TO_EDGE,
                mipMapping = false,
                maxAnisotropy = 0)
            )
            targetTex.loadingState = Texture.LoadingState.LOADED
            gl.texImage2D(tex.target, 0, gl.RGBA8, requiredTexWidth, requiredTexHeight, 0, gl.RGBA, gl.UNSIGNED_BYTE, null)
        }

        val viewport = scene.mainRenderPass.viewport
        gl.bindTexture(tex.target, tex.glTexture)
        gl.readBuffer(gl.BACK)
        gl.copyTexSubImage2D(tex.target, 0, 0, 0, viewport.x, viewport.y, viewport.width, viewport.height)
    }

    private fun nextPow2(x: Int): Int {
        var pow2 = x.takeHighestOneBit()
        if (pow2 < x) {
            pow2 = pow2 shl 1
        }
        return pow2
    }
}