package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.pipeline.backend.wgsl.WgslGenerator
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.khronos.webgl.*
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

    private var renderSize = Vec2i(canvas.width, canvas.height)

    private val gpuReadbacks = mutableListOf<GpuReadback>()

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

        if (gpuReadbacks.isNotEmpty()) {
            // copy all buffers requested for readback to temporary buffers using the current command encoder
            copyReadbacks(encoder)
        }
        device.queue.submit(arrayOf(encoder.finish()))
        if (gpuReadbacks.isNotEmpty()) {
            // after encoder is finished and submitted, temp buffers can be mapped for readback
            mapReadbacks()
        }
    }

    private fun Scene.renderOffscreenPasses(encoder: GPUCommandEncoder) {
        for (i in sortedOffscreenPasses.indices) {
            val pass = sortedOffscreenPasses[i]
            if (pass.isEnabled) {
                pass.render(encoder)
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

    private fun ComputeRenderPass.dispatch(encoder: GPUCommandEncoder) = (impl as WgpuComputePass).dispatch(encoder)

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

    override fun createComputePass(parentPass: ComputeRenderPass): ComputePassImpl {
        return WgpuComputePass(parentPass, this)
    }

    override fun writeTextureData(tex: Texture, data: TextureData) {
        when (tex) {
            is Texture1d -> textureLoader.loadTexture1d(tex, data)
            is Texture2d -> textureLoader.loadTexture2d(tex, data)
            is Texture3d -> textureLoader.loadTexture3d(tex, data)
            is TextureCube -> textureLoader.loadTextureCube(tex, data)
            else -> error("Unsupported texture type: $tex")
        }
    }

    override fun readStorageBuffer(storage: StorageBuffer, deferred: CompletableDeferred<Unit>) {
        gpuReadbacks += ReadbackStorageBuffer(storage, deferred)
    }

    override fun readTextureData(texture: Texture, deferred: CompletableDeferred<TextureData>) {
        gpuReadbacks += ReadbackTexture(texture, deferred)
    }

    private fun copyReadbacks(encoder: GPUCommandEncoder) {
        gpuReadbacks.filterIsInstance<ReadbackStorageBuffer>().forEach { readback ->
            val gpuBuf = readback.storage.gpuBuffer as WgpuBufferResource?
            if (gpuBuf == null) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading buffer"))
            } else {
                val size = readback.storage.buffer.limit.toLong() * 4
                val mapBuffer = device.createBuffer(
                    GPUBufferDescriptor(
                        label = "storage-buffer-readback",
                        size = size,
                        usage = GPUBufferUsage.MAP_READ or GPUBufferUsage.COPY_DST
                    )
                )
                encoder.copyBufferToBuffer(gpuBuf.buffer, 0L, mapBuffer, 0L, size)
                readback.mapBuffer = mapBuffer
            }
        }

        gpuReadbacks.filterIsInstance<ReadbackTexture>().forEach { readback ->
            val gpuTex = readback.texture.gpuTexture as WgpuLoadedTexture?
            if (gpuTex == null || readback.texture.props.format.isF16) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading texture"))
            } else {
                val format = readback.texture.props.format
                val size = format.pxSize.toLong() * gpuTex.width * gpuTex.height * gpuTex.depth
                val mapBuffer = device.createBuffer(
                    GPUBufferDescriptor(
                        label = "texture-readback",
                        size = size,
                        usage = GPUBufferUsage.MAP_READ or GPUBufferUsage.COPY_DST
                    )
                )
                encoder.copyTextureToBuffer(
                    source = GPUImageCopyTexture(gpuTex.texture.gpuTexture),
                    destination = GPUImageCopyBuffer(
                        buffer = mapBuffer,
                        bytesPerRow = format.pxSize * gpuTex.width,
                        rowsPerImage = gpuTex.height
                    ),
                    copySize = intArrayOf(gpuTex.width, gpuTex.height, gpuTex.depth)
                )
                readback.mapBuffer = mapBuffer
            }
        }
    }

    private fun mapReadbacks() {
        gpuReadbacks.filterIsInstance<ReadbackStorageBuffer>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            mapBuffer.mapAsync(GPUMapMode.READ).then {
                readback.storage.buffer.copyFrom(mapBuffer.getMappedRange())
                mapBuffer.unmap()
                mapBuffer.destroy()
                readback.deferred.complete(Unit)
            }
        }

        gpuReadbacks.filterIsInstance<ReadbackTexture>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            mapBuffer.mapAsync(GPUMapMode.READ).then {
                val gpuTex = readback.texture.gpuTexture as WgpuLoadedTexture
                val format = readback.texture.props.format
                val dst = TextureData.createBuffer(format, gpuTex.width, gpuTex.height, gpuTex.depth)
                dst.copyFrom(mapBuffer.getMappedRange())
                mapBuffer.unmap()
                mapBuffer.destroy()
                when (readback.texture) {
                    is Texture1d -> readback.deferred.complete(TextureData1d(dst, gpuTex.width, format))
                    is Texture2d -> readback.deferred.complete(TextureData2d(dst, gpuTex.width, gpuTex.height, format))
                    is Texture3d -> readback.deferred.complete(TextureData3d(dst, gpuTex.width, gpuTex.height, gpuTex.depth, format))
                    else -> readback.deferred.completeExceptionally(IllegalArgumentException("Unsupported texture type"))
                }
            }
        }

        gpuReadbacks.clear()
    }

    private fun Buffer.copyFrom(src: ArrayBuffer) {
        when (this) {
            is Uint8BufferImpl -> this.buffer.set(Uint8Array(src))
            is Uint16BufferImpl -> this.buffer.set(Uint16Array(src))
            is Int32BufferImpl -> this.buffer.set(Int32Array(src))
            is Float32BufferImpl -> this.buffer.set(Float32Array(src))
            is MixedBufferImpl -> Uint8Array(this.buffer.buffer).set(Uint8Array(src))
            else -> logE { "Unexpected buffer type: ${this::class.simpleName}" }
        }
    }

    fun createBuffer(descriptor: GPUBufferDescriptor, info: String?): WgpuBufferResource {
        return WgpuBufferResource(device.createBuffer(descriptor), descriptor.size, info)
    }

    fun createTexture(descriptor: GPUTextureDescriptor, texture: Texture): WgpuTextureResource {
        return WgpuTextureResource(device.createTexture(descriptor), texture)
    }

    private interface GpuReadback

    private class ReadbackStorageBuffer(val storage: StorageBuffer, val deferred: CompletableDeferred<Unit>) : GpuReadback {
        var mapBuffer: GPUBuffer? = null
    }

    private class ReadbackTexture(val texture: Texture, val deferred: CompletableDeferred<TextureData>) : GpuReadback {
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