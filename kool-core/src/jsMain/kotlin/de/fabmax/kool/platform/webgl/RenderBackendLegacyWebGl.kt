package de.fabmax.kool.platform.webgl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.generator.GlslGenerator
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.gl.GlImpl
import de.fabmax.kool.pipeline.backend.gl.WebGL2RenderingContext.Companion.RGBA8
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logE
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.WebGLRenderingContext.Companion.BACK
import org.khronos.webgl.WebGLRenderingContext.Companion.MAX_TEXTURE_IMAGE_UNITS
import org.khronos.webgl.WebGLRenderingContext.Companion.RGBA
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_BYTE

@Suppress("UnsafeCastFromDynamic")
class RenderBackendLegacyWebGl(val ctx: JsContext) : RenderBackend {
    override val name = "RenderBackendLegacyWebGl"
    override val apiName = "WebGL 2.0"
    override val deviceName = ""
    override val projCorrectionMatrix = Mat4f.IDENTITY
    override val depthBiasMatrix: Mat4f = MutableMat4f().translate(0.5f, 0.5f, 0.5f).scale(0.5f)

    internal val queueRenderer = QueueRendererWebGl(ctx)
    internal val afterRenderActions = mutableListOf<() -> Unit>()

    val glCapabilities = GlCapabilities()

    private val openRenderPasses = mutableListOf<OffscreenRenderPass>()
    private val doneRenderPasses = mutableSetOf<OffscreenRenderPass>()

    private val gl = GlImpl.gl

    init {
        glCapabilities.maxTexUnits = gl.getParameter(MAX_TEXTURE_IMAGE_UNITS).asDynamic()
        glCapabilities.hasFloatTextures = gl.getExtension("EXT_color_buffer_float") != null

        val extAnisotropic = gl.getExtension("EXT_texture_filter_anisotropic") ?:
                gl.getExtension("MOZ_EXT_texture_filter_anisotropic") ?:
                gl.getExtension("WEBKIT_EXT_texture_filter_anisotropic")
        if (extAnisotropic != null) {
            glCapabilities.maxAnisotropy = gl.getParameter(extAnisotropic.MAX_TEXTURE_MAX_ANISOTROPY_EXT) as Int
            glCapabilities.glTextureMaxAnisotropyExt = extAnisotropic.TEXTURE_MAX_ANISOTROPY_EXT
        }
    }

    override fun renderFrame(ctx: KoolContext) {
        if (ctx.disposablePipelines.isNotEmpty()) {
            queueRenderer.disposePipelines(ctx.disposablePipelines)
            ctx.disposablePipelines.clear()
        }

        ctx.engineStats.resetPerFrameCounts()

        for (j in ctx.backgroundPasses.indices) {
            if (ctx.backgroundPasses[j].isEnabled) {
                drawOffscreen(ctx.backgroundPasses[j])
                ctx.backgroundPasses[j].afterDraw(ctx)
            }
        }
        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
                if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.BeforeRender) {
                    captureFramebuffer(scene)
                }
                doOffscreenPasses(scene, ctx)
                queueRenderer.renderViews(scene.mainRenderPass)
                if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.AfterRender) {
                    captureFramebuffer(scene)
                }
                scene.mainRenderPass.afterDraw(ctx)
            }
        }

        if (afterRenderActions.isNotEmpty()) {
            afterRenderActions.forEach { it() }
            afterRenderActions.clear()
        }
    }

    private fun captureFramebuffer(scene: Scene) {
        val targetTex = scene.capturedFramebuffer

        if (targetTex.loadedTexture == null) {
            targetTex.loadedTexture = LoadedTextureWebGl(ctx, TEXTURE_2D, gl.createTexture(), 4096 * 2048 * 4)
        }
        val tex = targetTex.loadedTexture as LoadedTextureWebGl

        val requiredTexWidth = nextPow2(scene.mainRenderPass.viewport.width)
        val requiredTexHeight = nextPow2(scene.mainRenderPass.viewport.height)
        if (tex.width != requiredTexWidth || tex.height != requiredTexHeight) {
            tex.setSize(requiredTexWidth, requiredTexHeight, 1)
            tex.applySamplerProps(TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE,
                addressModeV = AddressMode.CLAMP_TO_EDGE,
                mipMapping = false,
                maxAnisotropy = 0)
            )
            targetTex.loadingState = Texture.LoadingState.LOADED
            val pixels: ArrayBufferView? = null
            gl.texImage2D(tex.target, 0, RGBA8, requiredTexWidth, requiredTexHeight, 0, RGBA, UNSIGNED_BYTE, pixels)
        }

        val viewport = scene.mainRenderPass.viewport
        gl.bindTexture(tex.target, tex.texture)
        gl.readBuffer(BACK)
        gl.copyTexSubImage2D(tex.target, 0, 0, 0, viewport.x, viewport.y, viewport.width, viewport.height)
    }

    private fun nextPow2(x: Int): Int {
        var pow2 = x.takeHighestOneBit()
        if (pow2 < x) {
            pow2 = pow2 shl 1
        }
        return pow2
    }

    private fun doOffscreenPasses(scene: Scene, ctx: KoolContext) {
        doneRenderPasses.clear()
        for (i in scene.offscreenPasses.indices) {
            val rp = scene.offscreenPasses[i]
            if (rp.isEnabled) {
                openRenderPasses += rp
            } else {
                doneRenderPasses += rp
            }
        }
        while (openRenderPasses.isNotEmpty()) {
            var anyDrawn = false
            for (i in openRenderPasses.indices) {
                val pass = openRenderPasses[i]
                var skip = false
                for (j in pass.dependencies.indices) {
                    val dep = pass.dependencies[j]
                    if (dep !in doneRenderPasses) {
                        skip = true
                        break
                    }
                }
                if (!skip) {
                    anyDrawn = true
                    openRenderPasses -= pass
                    doneRenderPasses += pass
                    drawOffscreen(pass)
                    pass.afterDraw(ctx)
                    break
                }
            }
            if (!anyDrawn) {
                logE { "Failed to render all offscreen passes, remaining:" }
                openRenderPasses.forEach { logE { "  ${it.name}" } }
                openRenderPasses.clear()
                break
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

    override fun close(ctx: KoolContext) {
        // nothing to do here...
    }

    override fun cleanup(ctx: KoolContext) {
        // nothing to do here...
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, 0, ctx.windowWidth, ctx.windowHeight)
    }

    override fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout): ShaderCode {
        val src = GlslGenerator().generateProgram(shader.program)
        if (shader.program.dumpCode) {
            src.dump()
        }
        return ShaderCodeImpl(src.vertexSrc, src.fragmentSrc)
    }

    override fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl {
        return OffscreenPass2dWebGl(parentPass)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
        return OffscreenPassCubeWebGl(parentPass)
    }

    override fun uploadTextureToGpu(tex: Texture, data: TextureData) {
        val ctx = KoolSystem.requireContext() as JsContext
        tex.loadedTexture = when (tex) {
            is Texture1d -> TextureLoader.loadTexture1d(ctx, tex.props, data)
            is Texture2d -> TextureLoader.loadTexture2d(ctx, tex.props, data)
            is Texture3d -> TextureLoader.loadTexture3d(ctx, tex.props, data)
            is TextureCube -> TextureLoader.loadTextureCube(ctx, tex.props, data)
            else -> throw IllegalArgumentException("Unsupported texture type: $data")
        }
        tex.loadingState = Texture.LoadingState.LOADED
    }
}

class GlCapabilities {
    var maxTexUnits = 16
        internal set
    var hasFloatTextures = false
        internal set
    var maxAnisotropy = 1
        internal set
    var glTextureMaxAnisotropyExt = 0
        internal set
}
