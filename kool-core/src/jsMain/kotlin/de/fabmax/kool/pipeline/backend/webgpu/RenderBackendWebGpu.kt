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
import de.fabmax.kool.pipeline.backend.wgsl.WgslGenerator
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.ceil

class RenderBackendWebGpu(val ctx: KoolContext, val canvas: HTMLCanvasElement) : RenderBackend, RenderBackendJs {
    override val name: String = "WebGPU Backend"
    override val apiName: String = "WebGPU"
    override val deviceName: String = "WebGPU"
    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.WEB_GPU
    override val hasComputeShaders: Boolean = true

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
    private val computePassEncoderState = ComputePassEncoderState()

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
        canvasContext.configure(
            GPUCanvasConfiguration(device, canvasFormat)
        )
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
                scene.renderOffscreenPasses(encoder)
                sceneRenderer.renderScene(scene.mainRenderPass, encoder)
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
                if (pass.isProfileTimes) {
                    pass.tDraw = Time.precisionTime - t
                }
            }
        }
    }

    private fun OffscreenRenderPass.render(encoder: GPUCommandEncoder) {
        when (this) {
            is OffscreenRenderPass2d -> draw(encoder)
            is OffscreenRenderPassCube -> draw(encoder)
            is OffscreenRenderPass2dPingPong -> draw(encoder)
            is ComputeRenderPass -> dispatch(encoder)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
    }

    private fun OffscreenRenderPass2dPingPong.draw(encoder: GPUCommandEncoder) {
        for (i in 0 until pingPongPasses) {
            onDrawPing?.invoke(i)
            ping.draw(encoder)
            onDrawPong?.invoke(i)
            pong.draw(encoder)
        }
    }

    private fun OffscreenRenderPass2d.draw(encoder: GPUCommandEncoder) = (impl as WgpuOffscreenRenderPass2d).draw(encoder)

    private fun OffscreenRenderPassCube.draw(encoder: GPUCommandEncoder) = (impl as WgpuOffscreenRenderPassCube).draw(encoder)

    private fun ComputeRenderPass.dispatch(encoder: GPUCommandEncoder) {
        val tasks = tasks

        val maxNumGroups = device.limits.maxComputeWorkgroupsPerDimension
        val maxWorkGroupSzX = device.limits.maxComputeWorkgroupSizeX
        val maxWorkGroupSzY = device.limits.maxComputeWorkgroupSizeY
        val maxWorkGroupSzZ = device.limits.maxComputeWorkgroupSizeZ
        val maxInvocations = device.limits.maxComputeInvocationsPerWorkgroup

        computePassEncoderState.setup(encoder, encoder.beginComputePass())
        for (i in tasks.indices) {
            val task = tasks[i]
            if (task.isEnabled) {
                val pipeline = tasks[i].pipeline

                var isInLimits = true

                val groupSize = pipeline.workGroupSize
                val numGroupsX = ceil(width.toFloat() / pipeline.workGroupSize.x).toInt()
                val numGroupsY = ceil(height.toFloat() / pipeline.workGroupSize.y).toInt()
                val numGroupsZ = ceil(depth.toFloat() / pipeline.workGroupSize.z).toInt()
                if (numGroupsX > maxNumGroups || numGroupsY > maxNumGroups || numGroupsZ > maxNumGroups) {
                    logE { "Maximum compute shader workgroup count exceeded: max count = $maxNumGroups, requested count: ($numGroupsX, $numGroupsY, $numGroupsZ)" }
                    isInLimits = false
                }
                if (groupSize.x > maxWorkGroupSzX || groupSize.y > maxWorkGroupSzY || groupSize.z > maxWorkGroupSzZ) {
                    logE { "Maximum compute shader workgroup size exceeded: max size = ($maxWorkGroupSzX, $maxWorkGroupSzY, $maxWorkGroupSzZ), requested size: $groupSize" }
                    isInLimits = false
                }
                if (groupSize.x * groupSize.y * groupSize.z > maxInvocations) {
                    logE { "Maximum compute shader workgroup invocations exceeded: max invocations = $maxInvocations, " +
                            "requested invocations: ${groupSize.x} x ${groupSize.y} x ${groupSize.z} = ${groupSize.x * groupSize.y * groupSize.z}" }
                    isInLimits = false
                }

                if (isInLimits) {
                    task.beforeDispatch()
                    if (pipelineManager.bindComputePipeline(task, computePassEncoderState)) {
                        computePassEncoderState.passEncoder.dispatchWorkgroups(numGroupsX, numGroupsY, numGroupsZ)
                        task.afterDispatch()
                    }
                }
            }
        }
        computePassEncoderState.end()
    }

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