package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.*
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.pipeline.backend.wgsl.WgslGenerator
import de.fabmax.kool.platform.WasmContext
import de.fabmax.kool.platform.navigator
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.khronos.webgl.*
import org.w3c.dom.HTMLCanvasElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual class RenderBackendWebGpu(val ctx: WasmContext) : RenderBackend {
    override actual val name: String = "WebGPU"
    override actual val apiName: String = "WebGPU"
    override actual val deviceName: String = "WebGPU"
    override actual val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.WEB_GPU
    private lateinit var _features: BackendFeatures
    override actual val features: BackendFeatures get() = _features

    actual val numSamples: Int = KoolSystem.configWasm.numSamples
    actual val framebufferSize: Vec2i get() = ctx.window.framebufferSize

    private val canvas: HTMLCanvasElement get() = ctx.window.canvas
    private var _adapter: GPUAdapter? = null
    private var _device: GPUDevice? = null
    private var _canvasContext: GPUCanvasContext? = null
    private var _canvasFormat: GPUTextureFormat? = null
    actual val adapter: GPUAdapter get() = requireNotNull(_adapter)
    actual val device: GPUDevice get() = requireNotNull(_device)
    actual val canvasContext: GPUCanvasContext get() = requireNotNull(_canvasContext)
    actual val canvasFormat: GPUTextureFormat get() = _canvasFormat!!

    private var _textureLoader: WgpuTextureLoader? = null
    internal actual val textureLoader: WgpuTextureLoader get() = requireNotNull(_textureLoader)

    private var _isTimestampQuerySupported = false
    actual val isTimestampQuerySupported: Boolean get() = _isTimestampQuerySupported

    internal actual val timestampQuery: WgpuTimestamps by lazy { WgpuTimestamps(128, this) }

    actual val pipelineManager = WgpuPipelineManager(this)
    private val screenPass = WgpuScreenPass(this)

    private var renderSize = Vec2i(canvas.width, canvas.height)

    private val gpuReadbacks = mutableListOf<GpuReadback>()

    override actual val frameGpuTime: Duration = 0.0.seconds

    override actual val isAsyncRendering: Boolean = false

    actual val clearHelper: ClearHelper by lazy { ClearHelper(this) }
    private val passEncoderState = RenderPassEncoderState(this)

    init {
        check(isSupported()) { "WebGPU not supported on this browser." }
    }

    internal suspend fun createWebGpuContext(): Boolean {
        val powerPref = when (KoolSystem.configWasm.powerPreference) {
            PowerPreference.HighPerformance -> GPUPowerPreference.highPerformance
            PowerPreference.LowPower -> GPUPowerPreference.lowPower
        }
        val selectedAdapter = navigator.gpu.requestAdapter(GPURequestAdapterOptions(powerPref)).await<GPUAdapter?>()
            ?: navigator.gpu.requestAdapter().await<GPUAdapter?>()
        if (selectedAdapter == null) {
            logE { "No appropriate GPUAdapter found." }
            return false
        }
        _adapter = selectedAdapter

//        val availableFeatures = adapter.features.toList()
//        logD { "Available GPUAdapter features:" }
//        availableFeatures.forEach { logD { it } }

        val requiredFeatures = mutableListOf<String>()
//        if ("timestamp-query" in availableFeatures) {
            logI { "Enabling WebGPU timestamp-query feature" }
            requiredFeatures.add("timestamp-query")
            _isTimestampQuerySupported = true
//        }
//        if ("rg11b10ufloat-renderable" in availableFeatures) {
            logI { "Enabling rg11b10ufloat-renderable feature" }
            requiredFeatures.add("rg11b10ufloat-renderable")
//        }

        try {
            val deviceDesc = GPUDeviceDescriptor(requiredFeatures)
            _device = adapter.requestDevice(deviceDesc).await()
        } catch (e: Exception) {
            logE { "requestDevice() failed: $e" }
            return false
        }

        _features = BackendFeatures(
            computeShaders = true,
            cubeMapArrays = true,
            reversedDepth = true,
            maxSamples = 4,
            readWriteStorageTextures = false,
            depthOnlyShaderColorOutput = Color.BLACK,
            maxComputeWorkGroupsPerDimension = Vec3i(
                device.limits.maxComputeWorkgroupsPerDimension,
                device.limits.maxComputeWorkgroupsPerDimension,
                device.limits.maxComputeWorkgroupsPerDimension,
            ),
            maxComputeWorkGroupSize = Vec3i(
                device.limits.maxComputeWorkgroupSizeX,
                device.limits.maxComputeWorkgroupSizeY,
                device.limits.maxComputeWorkgroupSizeZ,
            ),
            maxComputeInvocationsPerWorkgroup = device.limits.maxComputeInvocationsPerWorkgroup,
        )

        _canvasContext = canvas.getContext("webgpu") as GPUCanvasContext
        _canvasFormat = GPUTextureFormat.forValue(navigator.gpu.getPreferredCanvasFormat())
        canvasContext.configure(
            GPUCanvasConfiguration(device, canvasFormat)
        )
        _textureLoader = WgpuTextureLoader(this)
        logI { "WebGPU context created" }
        return true
    }

    actual override fun renderFrame(frameData: FrameData, ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        if (ctx.window.framebufferSize != renderSize) {
            renderSize = ctx.window.framebufferSize
            screenPass.applySize(renderSize.x, renderSize.y)
        }

        passEncoderState.beginFrame()

        frameData.preparePipelines()
        frameData.executePasses()

        passEncoderState.ensureRenderPassInactive()
        if (gpuReadbacks.isNotEmpty()) {
            // copy all buffers requested for readback to temporary buffers using the current command encoder
            copyReadbacks(passEncoderState.encoder)
        }
        timestampQuery.resolve(passEncoderState.encoder)
        passEncoderState.endFrame()

        timestampQuery.readTimestamps()
        if (gpuReadbacks.isNotEmpty()) {
            // after encoder is finished and submitted, temp buffers can be mapped for readback
            mapReadbacks()
        }
    }

    private fun FrameData.preparePipelines() {
        forEachPass { passData ->
            val t = Time.precisionTime
            passData.forEachView { viewData ->
                viewData.drawQueue.forEach { cmd -> pipelineManager.prepareDrawPipeline(cmd) }
            }
            passData.gpuPass.tRecord = (Time.precisionTime - t).seconds
        }
    }

    private fun FrameData.executePasses() {
        forEachPass { passData -> passData.executePass(passEncoderState) }
    }

    private fun PassData.executePass(passEncoderState: RenderPassEncoderState) {
        val pass = gpuPass
        val t = Time.precisionTime
        when (pass) {
            is Scene.ScreenPass -> screenPass.renderScene(this, passEncoderState)
            is OffscreenPass2d -> pass.draw(this, passEncoderState)
            is OffscreenPassCube -> pass.draw(this, passEncoderState)
            is ComputePass -> pass.dispatch(passEncoderState)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
        pass.tRecord = (Time.precisionTime - t).seconds
    }

    private fun OffscreenPass2d.draw(passData: PassData, passEncoderState: RenderPassEncoderState) {
        (impl as WgpuOffscreenPass2d).draw(passData, passEncoderState)
    }

    private fun OffscreenPassCube.draw(passData: PassData, passEncoderState: RenderPassEncoderState) {
        (impl as WgpuOffscreenPassCube).draw(passData, passEncoderState)
    }

    private fun ComputePass.dispatch(passEncoderState: RenderPassEncoderState) {
        passEncoderState.ensureRenderPassInactive()
        (impl as WgpuComputePass).dispatch(passEncoderState.encoder)
    }

    actual override fun cleanup(ctx: KoolContext) {
        // do nothing for now
    }

    actual override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode {
        val output = WgslGenerator.generateProgram(shader.program, pipeline)
        return WebGpuShaderCode(
            vertexSrc = output.vertexSrc,
            vertexEntryPoint = output.vertexEntryPoint,
            fragmentSrc = output.fragmentSrc,
            fragmentEntryPoint = output.fragmentEntryPoint
        )
    }

    actual override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val output = WgslGenerator.generateComputeProgram(shader.program, pipeline)
        return WebGpuComputeShaderCode(
            computeSrc = output.computeSrc,
            computeEntryPoint = output.computeEntryPoint
        )
    }

    actual override fun createOffscreenPass2d(parentPass: OffscreenPass2d): OffscreenPass2dImpl {
        return WgpuOffscreenPass2d(parentPass, this)
    }

    actual override fun createOffscreenPassCube(parentPass: OffscreenPassCube): OffscreenPassCubeImpl {
        return WgpuOffscreenPassCube(parentPass, this)
    }

    actual override fun createComputePass(parentPass: ComputePass): ComputePassImpl {
        return WgpuComputePass(parentPass, this)
    }

    actual override fun <T: ImageData> uploadTextureData(tex: Texture<T>) = textureLoader.loadTexture(tex)

    actual override fun downloadBuffer(buffer: GpuBuffer, deferred: CompletableDeferred<Unit>, resultBuffer: Buffer) {
        gpuReadbacks += ReadbackStorageBuffer(buffer, deferred, resultBuffer)
    }

    actual override fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>) {
        gpuReadbacks += ReadbackTexture(texture, deferred)
    }

    private fun copyReadbacks(encoder: GPUCommandEncoder) {
        gpuReadbacks.filterIsInstance<ReadbackStorageBuffer>().forEach { readback ->
            val gpuBuf = readback.storage.gpuBuffer as GpuBufferWgpu?
            if (gpuBuf == null) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading buffer"))
            } else {
                val size = readback.resultBuffer.limit.toLong() * 4
                val mapBuffer = device.createBuffer(
                    GPUBufferDescriptor(
                        label = "storage-buffer-readback",
                        size = size.toJsNumber(),
                        usage = GPUBufferUsage.MAP_READ or GPUBufferUsage.COPY_DST
                    )
                )
                encoder.copyBufferToBuffer(gpuBuf.buffer, 0.toJsNumber(), mapBuffer, 0.toJsNumber(), size.toJsNumber())
                readback.mapBuffer = mapBuffer
            }
        }

        gpuReadbacks.filterIsInstance<ReadbackTexture>().forEach { readback ->
            val gpuTex = readback.texture.gpuTexture as WgpuTextureResource?
            if (gpuTex == null || readback.texture.format.isF16) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading texture"))
            } else {
                val format = readback.texture.format
                val size = format.pxSize.toLong() * gpuTex.width * gpuTex.height * gpuTex.depth
                val mapBuffer = device.createBuffer(
                    GPUBufferDescriptor(
                        label = "texture-readback",
                        size = size.toJsNumber(),
                        usage = GPUBufferUsage.MAP_READ or GPUBufferUsage.COPY_DST
                    )
                )
                encoder.copyTextureToBuffer(
                    source = GPUImageCopyTexture(gpuTex.gpuTexture),
                    destination = GPUImageCopyBuffer(
                        buffer = mapBuffer,
                        bytesPerRow = format.pxSize * gpuTex.width,
                        rowsPerImage = gpuTex.height
                    ),
                    copySize = intArrayOf(gpuTex.width, gpuTex.height, gpuTex.depth).toJsArray()
                )
                readback.mapBuffer = mapBuffer
            }
        }
    }

    private fun mapReadbacks() {
        gpuReadbacks.filterIsInstance<ReadbackStorageBuffer>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            mapBuffer.mapAsync(GPUMapMode.READ).then {
                readback.resultBuffer.copyFrom(mapBuffer.getMappedRange())
                mapBuffer.unmap()
                mapBuffer.destroy()
                readback.deferred.complete(Unit)
                it
            }
        }

        gpuReadbacks.filterIsInstance<ReadbackTexture>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            mapBuffer.mapAsync(GPUMapMode.READ).then {
                val gpuTex = readback.texture.gpuTexture as WgpuTextureResource
                val format = readback.texture.format
                val dst = ImageData.createBuffer(format, gpuTex.width, gpuTex.height, gpuTex.depth)
                dst.copyFrom(mapBuffer.getMappedRange())
                mapBuffer.unmap()
                mapBuffer.destroy()
                when (readback.texture) {
                    is Texture1d -> readback.deferred.complete(BufferedImageData1d(dst, gpuTex.width, format))
                    is Texture2d -> readback.deferred.complete(BufferedImageData2d(dst, gpuTex.width, gpuTex.height, format))
                    is Texture3d -> readback.deferred.complete(BufferedImageData3d(dst, gpuTex.width, gpuTex.height, gpuTex.depth, format))
                    else -> readback.deferred.completeExceptionally(IllegalArgumentException("Unsupported texture type"))
                }
                it
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

    actual fun createBuffer(descriptor: GPUBufferDescriptor, info: String?): GpuBufferWgpu {
        return GpuBufferWgpu(device.createBuffer(descriptor), descriptor.size.toLong(), info)
    }

    actual fun createTexture(descriptor: GPUTextureDescriptor): WgpuTextureResource {
        return WgpuTextureResource(descriptor, device.createTexture(descriptor))
    }

    private interface GpuReadback

    private class ReadbackStorageBuffer(val storage: GpuBuffer, val deferred: CompletableDeferred<Unit>, val resultBuffer: Buffer) : GpuReadback {
        var mapBuffer: GPUBuffer? = null
    }

    private class ReadbackTexture(val texture: Texture<*>, val deferred: CompletableDeferred<ImageData>) : GpuReadback {
        var mapBuffer: GPUBuffer? = null
    }

    companion object : BackendProvider {
        override val displayName: String = "WebGPU"

        override suspend fun createBackend(ctx: KoolContext): Result<RenderBackend> {
            return if (isSupported()) {
                val backend = RenderBackendWebGpu(ctx as WasmContext)
                if (backend.createWebGpuContext()) {
                    Result.success(backend)
                } else {
                    Result.failure(IllegalStateException("Failed to create WebGPU context"))
                }
            } else {
                Result.failure(IllegalStateException("WebGPU is not supported by this browser"))
            }
        }

        fun isSupported(): Boolean {
            return !isNoWgpuSupport()
        }
    }
}

private fun isNoWgpuSupport(): Boolean = js("!navigator.gpu")
