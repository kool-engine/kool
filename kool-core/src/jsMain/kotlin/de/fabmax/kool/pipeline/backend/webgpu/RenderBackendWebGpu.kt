package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logI
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.dom.HTMLCanvasElement

class RenderBackendWebGpu(val ctx: KoolContext, val canvas: HTMLCanvasElement) : RenderBackend, RenderBackendJs {
    override val name: String = "WebGPU Backend"
    override val apiName: String = "WebGPU"
    override val deviceName: String = "WebGPU"
    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.WEB_GPU
    override val canBlitRenderPasses: Boolean = false
    override val isOnscreenInfiniteDepthCapable: Boolean = false // actually it can...

    lateinit var adapter: GPUAdapter
        private set
    lateinit var device: GPUDevice
        private set
    lateinit var canvasContext: GPUCanvasContext
        private set
    private var _canvasFormat: GPUTextureFormat? = null
    val canvasFormat: GPUTextureFormat
        get() = _canvasFormat!!

    internal lateinit var textureLoader: WgpuTextureLoader
        private set

    val pipelineManager = WgpuPipelineManager(this)
    private val sceneRenderer = WgpuScreenRenderPass(this)

    private var renderSize = Vec2i(canvas.width, canvas.height)

    init {
        check(isSupported()) {
            val txt = "WebGPU not supported on this browser."
            js("alert(txt)")
            txt
        }
    }

    override suspend fun startRenderLoop() {
        adapter = checkNotNull(navigator.gpu.requestAdapter(
            GPURequestAdapterOptions(GPUPowerPreference.highPerformance)
        ).await()) {
            val txt = "No appropriate GPUAdapter found."
            js("alert(txt)")
            txt
        }

        device = adapter.requestDevice().await()

        canvasContext = canvas.getContext("webgpu") as GPUCanvasContext
        _canvasFormat = navigator.gpu.getPreferredCanvasFormat()
        canvasContext.configure(GPUCanvasConfiguration(device, canvasFormat))
        textureLoader = WgpuTextureLoader(this)
        logI { "WebGPU context created" }

        window.requestAnimationFrame { t -> (ctx as JsContext).renderFrame(t) }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        if (canvas.width != renderSize.x || canvas.height != renderSize.y) {
            renderSize = Vec2i(canvas.width, canvas.height)
            sceneRenderer.applySize(canvas.width, canvas.height)
        }

        val encoder = device.createCommandEncoder()

        ctx.backgroundScene.renderOffscreenPasses(encoder)

        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
//                if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.BeforeRender) {
//                    captureFramebuffer(scene)
//                }

                scene.renderOffscreenPasses(encoder)

                when (val scenePass = scene.mainRenderPass) {
                    is Scene.OffscreenSceneRenderPass -> TODO()
                    is Scene.OnscreenSceneRenderPass -> sceneRenderer.renderScene(scenePass.renderPass, encoder)
                }

//                if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.AfterRender) {
//                    captureFramebuffer(scene)
//                }
            }
        }

        device.queue.submit(arrayOf(encoder.finish()))
    }

    private fun Scene.renderOffscreenPasses(encoder: GPUCommandEncoder) {
        for (i in sortedOffscreenPasses.indices) {
            val pass = sortedOffscreenPasses[i]
            if (pass.isEnabled) {
                val t = if (pass.isProfileTimes) Time.precisionTime else 0.0
                pass.render(encoder)
                pass.afterDraw()
                if (pass.isProfileTimes) {
                    pass.tDraw = Time.precisionTime - t
                }
            }
        }
    }

    private fun OffscreenRenderPass.render(encoder: GPUCommandEncoder) {
        when (this) {
            is OffscreenRenderPass2d -> impl.draw(encoder)
            is OffscreenRenderPassCube -> impl.draw(encoder)
            is OffscreenRenderPass2dPingPong -> draw(encoder)
            is ComputeRenderPass -> TODO("ComputeRenderPass") //dispatchCompute(offscreenPass)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
    }

    private fun OffscreenRenderPass2dPingPong.draw(encoder: GPUCommandEncoder) {
        for (i in 0 until pingPongPasses) {
            onDrawPing?.invoke(i)
            ping.impl.draw(encoder)
            onDrawPong?.invoke(i)
            pong.impl.draw(encoder)
        }
    }

    private fun OffscreenPass2dImpl.draw(encoder: GPUCommandEncoder) = (this as WgpuOffscreenRenderPass2d).draw(encoder)
    private fun OffscreenPassCubeImpl.draw(encoder: GPUCommandEncoder) = (this as WgpuOffscreenRenderPassCube).draw(encoder)

    override fun cleanup(ctx: KoolContext) {
        // do nothing for now
    }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode {
        val output = WgslGenerator().generateProgram(shader.program, pipeline)
        if (shader.program.dumpCode) {
            output.dump()
        }
        return WebGpuShaderCode(
            vertexSrc = output.vertexSrc,
            vertexEntryPoint = output.vertexEntryPoint,
            fragmentSrc = output.fragmentSrc,
            fragmentEntryPoint = output.fragmentEntryPoint
        )
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val output = WgslGenerator().generateComputeProgram(shader.program, pipeline)
        if (shader.program.dumpCode) {
            output.dump()
        }
        return WebGpuComputeShaderCode(
            computeSrc = output.computeSrc,
            computeEntryPoint = output.computeEntryPoint
        )
    }

    override fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl {
        return WgpuOffscreenRenderPass2d(parentPass, 1, this)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
        return WgpuOffscreenRenderPassCube(parentPass, 1, this)
    }

    override fun uploadTextureToGpu(tex: Texture, data: TextureData) {
        when (tex) {
            is Texture1d -> textureLoader.loadTexture1d(tex, data)
            is Texture2d -> textureLoader.loadTexture2d(tex, data)
            is Texture3d -> textureLoader.loadTexture3d(tex, data)
            is TextureCube -> textureLoader.loadTextureCube(tex, data)
            else -> error("Unsupported texture type: $tex")
        }
    }

    fun createBuffer(descriptor: GPUBufferDescriptor, info: String?): WgpuBufferResource {
        return WgpuBufferResource(device.createBuffer(descriptor), descriptor.size, info)
    }

    fun createTexture(descriptor: GPUTextureDescriptor, texture: Texture): WgpuTextureResource {
        return WgpuTextureResource(device.createTexture(descriptor), texture)
    }

    data class WebGpuShaderCode(
        val vertexSrc: String,
        val vertexEntryPoint: String,
        val fragmentSrc: String,
        val fragmentEntryPoint: String
    ): ShaderCode {
        override val hash = LongHash().apply {
            this += vertexSrc.hashCode().toLong() shl 32 or fragmentSrc.hashCode().toLong()
        }
    }

    data class WebGpuComputeShaderCode(val computeSrc: String, val computeEntryPoint: String): ComputeShaderCode {
        override val hash = LongHash().apply {
            this += computeSrc
        }
    }

    companion object {
        fun isSupported(): Boolean {
            return !js("!navigator.gpu") as Boolean
        }
    }
}