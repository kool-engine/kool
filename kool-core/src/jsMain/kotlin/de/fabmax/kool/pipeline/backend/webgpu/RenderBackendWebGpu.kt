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
import de.fabmax.kool.util.*
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Int32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.Uint8Array
import org.w3c.dom.HTMLCanvasElement

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

    private val awaitedStorageBuffers = mutableListOf<AwaitedStorageBuffers>()

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

        if (awaitedStorageBuffers.isNotEmpty()) {
            // copy all buffers requested for readback to temporary buffers using the current command encoder
            copyStorageBuffers(encoder)
        }
        device.queue.submit(arrayOf(encoder.finish()))
        if (awaitedStorageBuffers.isNotEmpty()) {
            // after encoder is finished and submitted, temp buffers can be mapped for readback
            mapCopiedStorageBuffers()
        }
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
                if (task.numGroups.x > maxNumGroups || task.numGroups.y > maxNumGroups || task.numGroups.z > maxNumGroups) {
                    logE { "Maximum compute shader workgroup count exceeded: max count = $maxNumGroups, requested count: (${task.numGroups.x}, ${task.numGroups.y}, ${task.numGroups.z})" }
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
                        computePassEncoderState.passEncoder.dispatchWorkgroups(task.numGroups.x, task.numGroups.y, task.numGroups.z)
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

    override fun readStorageBuffer(storage: StorageBuffer, deferred: CompletableDeferred<Buffer>) {
        awaitedStorageBuffers += AwaitedStorageBuffers(storage, deferred)
    }

    private fun copyStorageBuffers(encoder: GPUCommandEncoder) {
        awaitedStorageBuffers.forEach { awaited ->
            val gpuBuf = awaited.storage.gpuBuffer as WgpuBufferResource?
            if (gpuBuf == null) {
                awaited.deferred.completeExceptionally(IllegalStateException("Failed reading buffer"))
            } else {
                val size = awaited.storage.buffer.limit.toLong() * 4
                val mapBuffer = device.createBuffer(
                    GPUBufferDescriptor(
                        label = "map-read-copy-dst",
                        size = size,
                        usage = GPUBufferUsage.MAP_READ or GPUBufferUsage.COPY_DST
                    )
                )
                encoder.copyBufferToBuffer(gpuBuf.buffer, 0L, mapBuffer, 0L, size)
                awaited.mapBuffer = mapBuffer
            }
        }
    }

    private fun mapCopiedStorageBuffers() {
        awaitedStorageBuffers.forEach { awaited ->
            val dst = awaited.storage.buffer
            awaited.mapBuffer?.let { mapBuffer ->
                mapBuffer.mapAsync(GPUMapMode.READ).then {
                    val arrayBuffer = awaited.mapBuffer!!.getMappedRange()
                    when (dst) {
                        is Uint8BufferImpl -> dst.buffer.set(Uint8Array(arrayBuffer))
                        is Uint16BufferImpl -> dst.buffer.set(Uint16Array(arrayBuffer))
                        is Int32BufferImpl -> dst.buffer.set(Int32Array(arrayBuffer))
                        is Float32BufferImpl -> dst.buffer.set(Float32Array(arrayBuffer))
                        is MixedBufferImpl -> Uint8Array(dst.buffer.buffer).set(Uint8Array(arrayBuffer))
                        else -> {
                            logE { "Unexpected buffer type: ${dst::class.simpleName}" }
                        }
                    }
                    mapBuffer.unmap()
                    mapBuffer.destroy()
                    awaited.deferred.complete(dst)
                }
            }
        }
        awaitedStorageBuffers.clear()
    }

    fun createBuffer(descriptor: GPUBufferDescriptor, info: String?): WgpuBufferResource {
        return WgpuBufferResource(device.createBuffer(descriptor), descriptor.size, info)
    }

    fun createTexture(descriptor: GPUTextureDescriptor, texture: Texture): WgpuTextureResource {
        return WgpuTextureResource(device.createTexture(descriptor), texture)
    }

    private class AwaitedStorageBuffers(val storage: StorageBuffer, val deferred: CompletableDeferred<Buffer>) {
        var mapBuffer: GPUBuffer? = null
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