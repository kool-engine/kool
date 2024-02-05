package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import kotlin.math.ceil

abstract class RenderBackendGl(internal val gl: GlApi, internal val ctx: KoolContext) : RenderBackend {
    override val name = "Common GL Backend"
    override val apiName: String
        get() = gl.version.versionName
    override val deviceName: String
        get() = gl.version.deviceInfo

    abstract val glslGeneratorHints: GlslGenerator.Hints

    var numSamples = 1
        private set

    override var deviceCoordinates: DeviceCoordinates = DeviceCoordinates.OPEN_GL
        protected set

    override val hasComputeShaders: Boolean get() = gl.capabilities.hasComputeShaders
    override val canBlitRenderPasses = true
    override val isOnscreenInfiniteDepthCapable = false

    internal val shaderMgr = ShaderManager(this)
    internal val queueRenderer = QueueRenderer(this)

    protected fun setupGl() {
        numSamples = gl.getInteger(gl.SAMPLES)
        //gl.enable(gl.SCISSOR_TEST)

        if (gl.capabilities.hasClipControl) {
            logD { "Setting depth range to zero-to-one" }
            gl.clipControl(gl.LOWER_LEFT, gl.ZERO_TO_ONE)
            deviceCoordinates = DeviceCoordinates.OPEN_GL_ZERO_TO_ONE
        }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        doOffscreenPasses(ctx.backgroundScene)

        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
                if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.BeforeRender) {
                    captureFramebuffer(scene)
                }
                doOffscreenPasses(scene)
                doForegroundPass(scene)
            }
        }
    }

    internal open fun blitFrameBuffers(
        src: OffscreenRenderPass2d,
        dst: OffscreenRenderPass2dGl?,
        srcViewport: Viewport,
        dstViewport: Viewport,
        mipLevel: Int
    ) {
        val dstBuffer = dst?.fbos?.get(mipLevel) ?: gl.DEFAULT_FRAMEBUFFER
        val srcPassImpl = src.impl as OffscreenRenderPass2dGl
        if (srcPassImpl.fbos.isEmpty()) {
            logE { "blit source framebuffer is not available" }
            return
        }

        gl.bindFramebuffer(gl.READ_FRAMEBUFFER, srcPassImpl.fbos[0])
        gl.bindFramebuffer(gl.DRAW_FRAMEBUFFER, dstBuffer)

        val filter = if (srcViewport.width == dstViewport.width && srcViewport.height == dstViewport.height) {
            gl.NEAREST
        } else {
            gl.LINEAR
        }
        gl.blitFramebuffer(
            srcViewport.x, srcViewport.y, srcViewport.width, srcViewport.height,
            dstViewport.x, dstViewport.y, dstViewport.width, dstViewport.height,
            gl.COLOR_BUFFER_BIT, filter
        )
    }

    override fun uploadTextureToGpu(tex: Texture, data: TextureData) {
        tex.loadedTexture = when (tex) {
            is Texture1d -> TextureLoaderGl.loadTexture1dCompat(tex, data, this)
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

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCodeGl {
        val src = GlslGenerator(glslGeneratorHints).generateProgram(shader.program, pipeline)
        if (shader.program.dumpCode) {
            src.dump()
        }
        return ShaderCodeGl(src.vertexSrc, src.fragmentSrc)
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCodeGl {
        check(gl.capabilities.hasComputeShaders) {
            "Compute shaders require OpenGL 4.3 or higher"
        }
        val src = GlslGenerator(glslGeneratorHints).generateComputeProgram(shader.program, pipeline)
        if (shader.program.dumpCode) {
            src.dump()
        }
        return ComputeShaderCodeGl(src.computeSrc)
    }

    private fun doOffscreenPasses(scene: Scene) {
        for (i in scene.sortedOffscreenPasses.indices) {
            val pass = scene.sortedOffscreenPasses[i]
            if (pass.isEnabled) {
                val t = if (pass.isProfileTimes) Time.precisionTime else 0.0
                drawOffscreen(pass)
                pass.afterDraw()
                if (pass.isProfileTimes) {
                    pass.tDraw = Time.precisionTime - t
                }
            }
        }
    }

    private fun doForegroundPass(scene: Scene) {
        val scenePass = scene.mainRenderPass
        val t = if (scenePass.renderPass.isProfileTimes) Time.precisionTime else 0.0

        when (scenePass) {
            is Scene.OffscreenSceneRenderPass -> {
                scenePass.blitRenderPass?.let {
                    val srcViewport = Viewport(0, 0, it.width, it.height)
                    val dst = scenePass.impl as OffscreenRenderPass2dGl
                    blitFrameBuffers(it, dst, srcViewport, scenePass.viewport, 0)
                }
                drawOffscreen(scenePass)
                blitFrameBuffers(scenePass, null, scenePass.viewport, scenePass.viewport, 0)
            }

            is Scene.OnscreenSceneRenderPass -> {
                scenePass.blitRenderPass?.let {
                    val srcViewport = Viewport(0, 0, it.width, it.height)
                    blitFrameBuffers(it, null, srcViewport, scenePass.viewport, 0)
                }
                queueRenderer.renderViews(scenePass.renderPass, null)
            }
        }

        if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.AfterRender) {
            captureFramebuffer(scene)
        }

        if (scenePass.renderPass.isProfileTimes) {
            scenePass.renderPass.tDraw = Time.precisionTime - t
        }
        scenePass.renderPass.afterDraw()
    }

    private fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
        when (offscreenPass) {
            is OffscreenRenderPass2d -> offscreenPass.impl.draw()
            is OffscreenRenderPassCube -> offscreenPass.impl.draw()
            is OffscreenRenderPass2dPingPong -> drawOffscreenPingPong(offscreenPass)
            is ComputeRenderPass -> dispatchCompute(offscreenPass)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $offscreenPass")
        }
    }

    private fun drawOffscreenPingPong(offscreenPass: OffscreenRenderPass2dPingPong) {
        for (i in 0 until offscreenPass.pingPongPasses) {
            offscreenPass.onDrawPing?.invoke(i)
            offscreenPass.ping.impl.draw()
            offscreenPass.onDrawPong?.invoke(i)
            offscreenPass.pong.impl.draw()
        }
    }

    protected fun OffscreenPass2dImpl.draw() = (this as OffscreenRenderPass2dGl).draw()
    protected fun OffscreenPassCubeImpl.draw() = (this as OffscreenRenderPassCubeGl).draw()

    private fun dispatchCompute(computePass: ComputeRenderPass) {
        val tasks = computePass.tasks

        for (i in tasks.indices) {
            val task = tasks[i]
            if (task.isEnabled) {
                val pipeline = tasks[i].pipeline
                val numGroupsX = ceil(computePass.width.toFloat() / pipeline.workGroupSize.x).toInt()
                val numGroupsY = ceil(computePass.height.toFloat() / pipeline.workGroupSize.y).toInt()
                val numGroupsZ = ceil(computePass.depth.toFloat() / pipeline.workGroupSize.z).toInt()

                task.beforeDispatch()

                if (shaderMgr.bindComputeShader(pipeline, task)) {
                    val maxCnt = gl.capabilities.maxWorkGroupCount
                    if (numGroupsX > maxCnt.x || numGroupsY > maxCnt.y || numGroupsZ > maxCnt.z) {
                        logE { "Maximum compute shader workgroup count exceeded: max count = $maxCnt, requested count: ($numGroupsX, $numGroupsY, $numGroupsZ)" }
                    }
                    gl.dispatchCompute(numGroupsX, numGroupsY, numGroupsZ)
                    gl.memoryBarrier(gl.SHADER_IMAGE_ACCESS_BARRIER_BIT)

                    task.afterDispatch()
                }
            }
        }
    }

    private fun captureFramebuffer(scene: Scene) {
        val viewport = scene.mainRenderPass.viewport
        val targetTex = scene.capturedFramebuffer

        if (targetTex.loadedTexture == null) {
            targetTex.loadedTexture = LoadedTextureGl(
                target = gl.TEXTURE_2D,
                glTexture = gl.createTexture(),
                backend = this,
                texture = targetTex,
                estimatedSize = viewport.width * viewport.height * 4L
            ).apply { applySamplerSettings(targetTex.props.defaultSamplerSettings) }
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
}