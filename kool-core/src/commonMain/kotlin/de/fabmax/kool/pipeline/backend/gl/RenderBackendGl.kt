package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.generator.GlslGenerator
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logE

abstract class RenderBackendGl(internal val gl: GlApi, internal val ctx: KoolContext) : RenderBackend {

    abstract val version: ApiVersion
    abstract val capabilities: GlCapabilities

    override val apiName: String
        get() = version.versionName
    override val deviceName: String
        get() = version.deviceInfo ?: "unknown"

    override val projCorrectionMatrix: Mat4f = Mat4f.IDENTITY
    override val depthBiasMatrix: Mat4f = MutableMat4f().translate(0.5f, 0.5f, 0.5f).scale(0.5f)

    internal val queueRenderer = QueueRenderer(this)
    internal val afterRenderActions = mutableListOf<() -> Unit>()
    private val openRenderPasses = mutableListOf<OffscreenRenderPass>()
    private val doneRenderPasses = mutableSetOf<OffscreenRenderPass>()

    override fun renderFrame(ctx: KoolContext) {
        ctx.engineStats.resetPerFrameCounts()

        if (ctx.disposablePipelines.isNotEmpty()) {
            queueRenderer.disposePipelines(ctx.disposablePipelines)
            ctx.disposablePipelines.clear()
        }

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

    override fun uploadTextureToGpu(tex: Texture, data: TextureData) {
        tex.loadedTexture = when (tex) {
            is Texture1d -> TextureLoaderGl.loadTexture1d(tex.props, data, this)
            is Texture2d -> TextureLoaderGl.loadTexture2d(tex.props, data, this)
            is Texture3d -> TextureLoaderGl.loadTexture3d(tex.props, data, this)
            is TextureCube -> TextureLoaderGl.loadTextureCube(tex.props, data, this)
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

    internal abstract fun copyTexturesFast(renderPass: OffscreenRenderPass2dGl)

    internal abstract fun copyTexturesFast(renderPass: OffscreenRenderPassCubeGl)

    internal abstract fun readTexturePixels(src: LoadedTextureGl, dst: TextureData)

    protected abstract fun drawOffscreen(offscreenPass: OffscreenRenderPass)

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
                openRenderPasses.forEach { p ->
                    val missingPasses = p.dependencies.filter { it !in doneRenderPasses }.map { it.name }
                    logE { "  ${p.name}, missing dependencies: $missingPasses" }
                }
                openRenderPasses.clear()
                break
            }
        }
    }

    private fun captureFramebuffer(scene: Scene) {
        val targetTex = scene.capturedFramebuffer

        if (targetTex.loadedTexture == null) {
            targetTex.loadedTexture = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), 4096 * 2048 * 4, this)
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
        gl.bindTexture(tex.target, tex.texture)
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

    data class ApiVersion(
        val major: Int,
        val minor: Int,
        val flavor: GlFlavor,
        val version: String,
        val deviceInfo: String?
    ) {
        val versionName: String = "${flavor.flavorName} $version"

        fun isHigherOrEqualThan(major: Int, minor: Int): Boolean {
            if (this.major < major) {
                return false
            }
            return this.major > major || this.minor >= minor
        }
    }

    data class GlCapabilities(
        val maxTexUnits: Int,
        val maxAnisotropy: Int,
        val glTextureMaxAnisotropyExt: Int,
        val canFastCopyTextures: Boolean
    )

    enum class GlFlavor(val flavorName: String) {
        OpenGL("OpenGL"),
        OpenGLES("OpenGL ES"),
        WebGL("WebGL")
    }
}