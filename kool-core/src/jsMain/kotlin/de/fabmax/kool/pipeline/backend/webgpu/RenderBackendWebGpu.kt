package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.PowerPreference
import de.fabmax.kool.configJs
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.math.numMipLevels
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
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.navigator
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.khronos.webgl.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class RenderBackendWebGpu(val ctx: JsContext) : RenderBackend {
    override val name: String = "WebGPU"
    override val apiName: String = "WebGPU"
    override val deviceName: String = "WebGPU"
    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.WEB_GPU
    override lateinit var features: BackendFeatures; private set

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

    var isTimestampQuerySupported = false
        private set
    internal val timestampQuery: WgpuTimestamps by lazy { WgpuTimestamps(128, this) }

    val pipelineManager = WgpuPipelineManager(this)
    private val screenPass = WgpuScreenPass(this)

    private var renderSize = Vec2i(ctx.canvas.width, ctx.canvas.height)

    private val gpuReadbacks = mutableListOf<GpuReadback>()

    override val frameGpuTime: Duration = 0.0.seconds

    val clearHelper: ClearHelper by lazy { ClearHelper(this) }
    private val passEncoderState = RenderPassEncoderState(this)

    init {
        check(isSupported()) {
            val txt = "WebGPU not supported on this browser."
            js("alert(txt)")
            txt
        }
    }

    internal suspend fun createWebGpuContext(): Boolean {
        val powerPref = when (KoolSystem.configJs.powerPreference) {
            PowerPreference.HighPerformance -> GPUPowerPreference.highPerformance
            PowerPreference.LowPower -> GPUPowerPreference.lowPower
        }
        val selectedAdapter = navigator.gpu.requestAdapter(GPURequestAdapterOptions(powerPref)).await()
            ?: navigator.gpu.requestAdapter().await()
        if (selectedAdapter == null) {
            logE { "No appropriate GPUAdapter found." }
            return false
        }
        adapter = selectedAdapter

        val availableFeatures = mutableSetOf<String>()
        adapter.features.forEach { s: String -> availableFeatures.add(s) }
        logD { "Available GPUAdapter features:" }
        availableFeatures.forEach { logD { it } }

        val requiredFeatures = mutableListOf<String>()
        if ("timestamp-query" in availableFeatures) {
            logI { "Enabling WebGPU timestamp-query feature" }
            requiredFeatures.add("timestamp-query")
            isTimestampQuerySupported = true
        }
        if ("rg11b10ufloat-renderable" in availableFeatures) {
            logI { "Enabling rg11b10ufloat-renderable feature" }
            requiredFeatures.add("rg11b10ufloat-renderable")
        }

        try {
            val deviceDesc = GPUDeviceDescriptor(requiredFeatures.toTypedArray())
            device = adapter.requestDevice(deviceDesc).await()
        } catch (e: Exception) {
            logE { "requestDevice() failed: $e" }
            return false
        }

        features = BackendFeatures(
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

        canvasContext = ctx.canvas.getContext("webgpu") as GPUCanvasContext
        _canvasFormat = navigator.gpu.getPreferredCanvasFormat()
        canvasContext.configure(
            GPUCanvasConfiguration(device, canvasFormat)
        )
        textureLoader = WgpuTextureLoader(this)
        logI { "WebGPU context created" }
        return true
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        if (this.ctx.canvas.width != renderSize.x || this.ctx.canvas.height != renderSize.y) {
            renderSize = Vec2i(this.ctx.canvas.width, this.ctx.canvas.height)
            screenPass.applySize(this.ctx.canvas.width, this.ctx.canvas.height)
        }

        passEncoderState.beginFrame()

        ctx.preparePipelines(passEncoderState)
        ctx.executePasses(passEncoderState)

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

    private fun KoolContext.preparePipelines(passEncoderState: RenderPassEncoderState) {
        backgroundScene.prepareDrawPipelines(passEncoderState)
        for (i in scenes.indices) {
            val scene = scenes[i]
            if (scene.isVisible) {
                scene.prepareDrawPipelines(passEncoderState)
            }
        }
    }

    private fun Scene.prepareDrawPipelines(passEncoderState: RenderPassEncoderState) {
        checkIsNotReleased()
        sceneRecordTime = measureTime {
            for (i in sortedPasses.indices) {
                val pass = sortedPasses[i]
                if (pass.isEnabled && pass is RenderPass) {
                    pass.prepareDrawPipelines(passEncoderState)
                }
            }
        }
    }

    private fun RenderPass.prepareDrawPipelines(passEncoderState: RenderPassEncoderState) {
        if (isEnabled) {
            for (i in views.indices) {
                val queue = views[i].drawQueue
                queue.forEach { cmd -> pipelineManager.prepareDrawPipeline(cmd) }
            }
        }
    }

    private fun KoolContext.executePasses(passEncoderState: RenderPassEncoderState) {
        backgroundScene.executePasses(passEncoderState)
        for (i in scenes.indices) {
            val scene = scenes[i]
            if (scene.isVisible) {
                scene.executePasses(passEncoderState)
            }
        }
    }

    private fun Scene.executePasses(passEncoderState: RenderPassEncoderState) {
        sceneRecordTime += measureTime {
            for (i in sortedPasses.indices) {
                val pass = sortedPasses[i]
                if (pass.isEnabled) {
                    pass.beforePass()
                    pass.execute(passEncoderState)
                    pass.afterPass()
                }
            }
        }
    }

    private fun GpuPass.execute(passEncoderState: RenderPassEncoderState) {
        when (this) {
            is Scene.ScreenPass -> screenPass.renderScene(this, passEncoderState)
            is OffscreenPass2d -> draw(passEncoderState)
            is OffscreenPassCube -> draw(passEncoderState)
            is ComputePass -> dispatch(passEncoderState)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
    }

    private fun OffscreenPass2d.draw(passEncoderState: RenderPassEncoderState) {
        (impl as WgpuOffscreenPass2d).draw(passEncoderState)
    }

    private fun OffscreenPassCube.draw(passEncoderState: RenderPassEncoderState) {
        (impl as WgpuOffscreenPassCube).draw(passEncoderState)
    }

    private fun ComputePass.dispatch(passEncoderState: RenderPassEncoderState) {
        passEncoderState.ensureRenderPassInactive()
        (impl as WgpuComputePass).dispatch(passEncoderState.encoder)
    }

    override fun cleanup(ctx: KoolContext) {
        // do nothing for now
    }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode {
        val output = WgslGenerator.generateProgram(shader.program, pipeline)
        return WebGpuShaderCode(
            vertexSrc = output.vertexSrc,
            vertexEntryPoint = output.vertexEntryPoint,
            fragmentSrc = output.fragmentSrc,
            fragmentEntryPoint = output.fragmentEntryPoint
        )
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val output = WgslGenerator.generateComputeProgram(shader.program, pipeline)
        return WebGpuComputeShaderCode(
            computeSrc = output.computeSrc,
            computeEntryPoint = output.computeEntryPoint
        )
    }

    override fun createOffscreenPass2d(parentPass: OffscreenPass2d): OffscreenPass2dImpl {
        return WgpuOffscreenPass2d(parentPass, this)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenPassCube): OffscreenPassCubeImpl {
        return WgpuOffscreenPassCube(parentPass, this)
    }

    override fun createComputePass(parentPass: ComputePass): ComputePassImpl {
        return WgpuComputePass(parentPass, this)
    }

    override fun initStorageTexture(storageTexture: StorageTexture, width: Int, height: Int, depth: Int) {
        val usage = GPUTextureUsage.STORAGE_BINDING or
                GPUTextureUsage.COPY_SRC or
                GPUTextureUsage.COPY_DST or
                GPUTextureUsage.TEXTURE_BINDING
        val dimension = when (storageTexture) {
            is StorageTexture1d -> GPUTextureDimension.texture1d
            is StorageTexture2d -> GPUTextureDimension.texture2d
            is StorageTexture3d -> GPUTextureDimension.texture3d
        }
        val size = when (storageTexture) {
            is StorageTexture1d -> intArrayOf(width)
            is StorageTexture2d -> intArrayOf(width, height)
            is StorageTexture3d -> intArrayOf(width, height, depth)
        }
        val levels = when (val mipMapping = storageTexture.mipMapping) {
            MipMapping.Full -> numMipLevels(width, height, depth)
            is MipMapping.Limited -> mipMapping.numLevels
            MipMapping.Off -> 1
        }

        if (storageTexture.format == TexFormat.RG11B10_F) {
            logW { "Storage texture format RG11B10_F is not supported by WebGPU, using RGBA_F16 instead" }
        }
        val texDesc = GPUTextureDescriptor(
            size = size,
            format = storageTexture.format.wgpuStorage,
            dimension = dimension,
            usage = usage,
            mipLevelCount = levels,
            label = storageTexture.name
        )
        storageTexture.gpuTexture?.release()
        storageTexture.gpuTexture = createTexture(texDesc)
    }

    override fun <T: ImageData> uploadTextureData(tex: Texture<T>) = textureLoader.loadTexture(tex)

    override fun downloadBuffer(buffer: GpuBuffer, deferred: CompletableDeferred<Unit>, resultBuffer: Buffer) {
        gpuReadbacks += ReadbackStorageBuffer(buffer, deferred, resultBuffer)
    }

    override fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>) {
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
                        size = size,
                        usage = GPUBufferUsage.MAP_READ or GPUBufferUsage.COPY_DST
                    )
                )
                encoder.copyBufferToBuffer(gpuBuf.buffer, 0L, mapBuffer, 0L, size)
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
                        size = size,
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
                readback.resultBuffer.copyFrom(mapBuffer.getMappedRange())
                mapBuffer.unmap()
                mapBuffer.destroy()
                readback.deferred.complete(Unit)
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

    fun createBuffer(descriptor: GPUBufferDescriptor, info: String?): GpuBufferWgpu {
        return GpuBufferWgpu(device.createBuffer(descriptor), descriptor.size, info)
    }

    fun createTexture(descriptor: GPUTextureDescriptor): WgpuTextureResource {
        return WgpuTextureResource(descriptor, device.createTexture(descriptor))
    }

    private interface GpuReadback

    private class ReadbackStorageBuffer(val storage: GpuBuffer, val deferred: CompletableDeferred<Unit>, val resultBuffer: Buffer) : GpuReadback {
        var mapBuffer: GPUBuffer? = null
    }

    private class ReadbackTexture(val texture: Texture<*>, val deferred: CompletableDeferred<ImageData>) : GpuReadback {
        var mapBuffer: GPUBuffer? = null
    }

    data class WebGpuShaderCode(
        val vertexSrc: String,
        val vertexEntryPoint: String,
        val fragmentSrc: String,
        val fragmentEntryPoint: String
    ): ShaderCode {
        override val hash = LongHash {
            this += vertexSrc.hashCode().toLong() shl 32 or fragmentSrc.hashCode().toLong()
        }
    }

    data class WebGpuComputeShaderCode(val computeSrc: String, val computeEntryPoint: String): ComputeShaderCode {
        override val hash = LongHash {
            this += computeSrc
        }
    }

    companion object : BackendProvider {
        override val displayName: String = "WebGPU"

        override suspend fun createBackend(ctx: KoolContext): Result<RenderBackend> {
            return if (isSupported()) {
                val backend = RenderBackendWebGpu(ctx as JsContext)
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
            return !js("!navigator.gpu") as Boolean
        }
    }
}