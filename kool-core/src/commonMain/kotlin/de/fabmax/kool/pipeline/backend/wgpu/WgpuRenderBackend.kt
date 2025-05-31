package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.BufferedImageData1d
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.BufferedImageData3d
import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePassImpl
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.ComputeShaderCode
import de.fabmax.kool.pipeline.DrawPipeline
import de.fabmax.kool.pipeline.GpuBuffer
import de.fabmax.kool.pipeline.GpuPass
import de.fabmax.kool.pipeline.ImageData
import de.fabmax.kool.pipeline.MipMapping
import de.fabmax.kool.pipeline.OffscreenPass2d
import de.fabmax.kool.pipeline.OffscreenPass2dImpl
import de.fabmax.kool.pipeline.OffscreenPassCube
import de.fabmax.kool.pipeline.OffscreenPassCubeImpl
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.pipeline.StorageTexture
import de.fabmax.kool.pipeline.StorageTexture1d
import de.fabmax.kool.pipeline.StorageTexture2d
import de.fabmax.kool.pipeline.StorageTexture3d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.Texture1d
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture3d
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.pipeline.backend.wgsl.WgslGenerator
import de.fabmax.kool.pipeline.isF16
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.checkIsNotReleased
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import io.ygdrasil.webgpu.BufferDescriptor
import io.ygdrasil.webgpu.DeviceDescriptor
import io.ygdrasil.webgpu.Extent3D
import io.ygdrasil.webgpu.GPUAdapter
import io.ygdrasil.webgpu.GPUBuffer
import io.ygdrasil.webgpu.GPUBufferDescriptor
import io.ygdrasil.webgpu.GPUBufferUsage
import io.ygdrasil.webgpu.GPUCommandEncoder
import io.ygdrasil.webgpu.GPUDevice
import io.ygdrasil.webgpu.GPUFeatureName
import io.ygdrasil.webgpu.GPUMapMode
import io.ygdrasil.webgpu.GPUTextureDescriptor
import io.ygdrasil.webgpu.GPUTextureDimension
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.GPUTextureUsage
import io.ygdrasil.webgpu.TexelCopyBufferInfo
import io.ygdrasil.webgpu.TexelCopyTextureInfo
import io.ygdrasil.webgpu.TextureDescriptor
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class WgpuRenderBackend(
    val ctx: KoolContext,
    val adapter: GPUAdapter,
    val surface: WgpuSurface,
    numSamples: Int,
) : RenderBackend, GPUBackend {
    override val name: String = "WebGPU Backend"
    override val apiName: String = "WebGPU"
    override val deviceName: String = "WebGPU"
    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.WEB_GPU
    override lateinit var features: BackendFeatures; private set

    var renderSize = Vec2i(surface.width.toInt(), surface.height.toInt())

    override lateinit var device: GPUDevice
        private set

    val canvasFormat: GPUTextureFormat
        get() = surface.format

    internal lateinit var textureLoader: WgpuTextureLoader
        private set

    var isTimestampQuerySupported = false
        private set
    internal val timestampQuery: WgpuTimestamps by lazy { WgpuTimestamps(128, this) }

    val pipelineManager = WgpuPipelineManager(this)
    internal val screenPass = WgpuScreenPass(this, numSamples)

    private val gpuReadbacks = mutableListOf<GpuReadback>()

    override val frameGpuTime: Duration = 0.0.seconds

    val clearHelper: ClearHelper by lazy { ClearHelper(this) }
    private val passEncoderState = RenderPassEncoderState(this)


    internal suspend fun initContext() {
        val availableFeatures = mutableSetOf<GPUFeatureName>()
        adapter.features
            .forEach { feature -> availableFeatures.add(feature) }
        logD { "Available GPUAdapter features:" }
        availableFeatures.forEach { logD { it.name } }

        val requiredFeatures = mutableListOf<GPUFeatureName>()
        if (GPUFeatureName.TimestampQuery in availableFeatures) {
            logI { "Enabling WebGPU timestamp-query feature" }
            requiredFeatures.add(GPUFeatureName.TimestampQuery)
            isTimestampQuerySupported = true
        }
        if (GPUFeatureName.RG11B10UfloatRenderable in availableFeatures) {
            logI { "Enabling rg11b10ufloat-renderable feature" }
            requiredFeatures.add(GPUFeatureName.RG11B10UfloatRenderable)
        }

        val deviceDesc = DeviceDescriptor(requiredFeatures)
        device = adapter.requestDevice(deviceDesc)
            .getOrThrow()

        features = BackendFeatures(
            computeShaders = true,
            cubeMapArrays = true,
            reversedDepth = true,
            maxSamples = 4,
            readWriteStorageTextures = false,
            depthOnlyShaderColorOutput = Color.BLACK,
            maxComputeWorkGroupsPerDimension = Vec3i(
                device.limits.maxComputeWorkgroupsPerDimension.toInt(),
                device.limits.maxComputeWorkgroupsPerDimension.toInt(),
                device.limits.maxComputeWorkgroupsPerDimension.toInt(),
            ),
            maxComputeWorkGroupSize = Vec3i(
                device.limits.maxComputeWorkgroupSizeX.toInt(),
                device.limits.maxComputeWorkgroupSizeY.toInt(),
                device.limits.maxComputeWorkgroupSizeZ.toInt(),
            ),
            maxComputeInvocationsPerWorkgroup = device.limits.maxComputeInvocationsPerWorkgroup.toInt(),
        )


        textureLoader = WgpuTextureLoader(this)
        logI { "WebGPU context created" }

    }

    override suspend fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        if (surface.width.toInt() != renderSize.x || surface.height.toInt() != renderSize.y) {
            renderSize = Vec2i(surface.width.toInt(), surface.height.toInt())
            screenPass.applySize(surface.width.toInt(), surface.height.toInt())
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

        surface.present()
    }

    private suspend fun KoolContext.preparePipelines(passEncoderState: RenderPassEncoderState) {
        ctx.backgroundScene.prepareDrawPipelines(passEncoderState)
        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
                scene.prepareDrawPipelines(passEncoderState)
            }
        }
    }

    private suspend fun Scene.prepareDrawPipelines(passEncoderState: RenderPassEncoderState) {
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

    private suspend fun RenderPass.prepareDrawPipelines(passEncoderState: RenderPassEncoderState) {
        if (isEnabled) {
            for (i in views.indices) {
                val queue = views[i].drawQueue
                queue.forEach { cmd -> pipelineManager.prepareDrawPipeline(cmd) }
            }
        }
    }

    private suspend fun KoolContext.executePasses(passEncoderState: RenderPassEncoderState) {
        ctx.backgroundScene.executePasses(passEncoderState)
        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
                scene.executePasses(passEncoderState)
            }
        }
    }

    private suspend fun Scene.executePasses(passEncoderState: RenderPassEncoderState) {
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

    private suspend fun GpuPass.execute(passEncoderState: RenderPassEncoderState) {
        when (this) {
            is Scene.ScreenPass -> screenPass.renderScene(this, passEncoderState)
            is OffscreenPass2d -> draw(passEncoderState)
            is OffscreenPassCube -> draw(passEncoderState)
            is ComputePass -> dispatch(passEncoderState)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
    }

    private suspend fun OffscreenPass2d.draw(passEncoderState: RenderPassEncoderState) {
        (impl as WgpuOffscreenPass2d).draw(passEncoderState)
    }

    private suspend fun OffscreenPassCube.draw(passEncoderState: RenderPassEncoderState) {
        (impl as WgpuOffscreenPassCube).draw(passEncoderState)
    }

    private suspend fun ComputePass.dispatch(passEncoderState: RenderPassEncoderState) {
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
        val usage = setOf(GPUTextureUsage.StorageBinding,
                GPUTextureUsage.CopySrc,
                GPUTextureUsage.CopyDst,
                GPUTextureUsage.TextureBinding)
        val dimension = when (storageTexture) {
            is StorageTexture1d -> GPUTextureDimension.OneD
            is StorageTexture2d -> GPUTextureDimension.TwoD
            is StorageTexture3d -> GPUTextureDimension.ThreeD
        }
        val size = when (storageTexture) {
            is StorageTexture1d -> Extent3D(width.toUInt())
            is StorageTexture2d -> Extent3D(width.toUInt(), height.toUInt())
            is StorageTexture3d -> Extent3D(width.toUInt(), height.toUInt(), depth.toUInt())
        }
        val levels = when (val mipMapping = storageTexture.mipMapping) {
            MipMapping.Full -> numMipLevels(width, height, depth)
            is MipMapping.Limited -> mipMapping.numLevels
            MipMapping.Off -> 1
        }

        if (storageTexture.format == TexFormat.RG11B10_F) {
            logW { "Storage texture format RG11B10_F is not supported by WebGPU, using RGBA_F16 instead" }
        }
        val texDesc = TextureDescriptor(
            size = size,
            format = storageTexture.format.wgpuStorage,
            dimension = dimension,
            usage = usage,
            mipLevelCount = levels.toUInt(),
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
                    BufferDescriptor(
                        label = "storage-buffer-readback",
                        size = size.toULong(),
                        usage = setOf(GPUBufferUsage.MapRead, GPUBufferUsage.CopyDst)
                    )
                )
                encoder.copyBufferToBuffer(gpuBuf.buffer, 0uL, mapBuffer, 0uL, size.toULong())
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
                    BufferDescriptor(
                        label = "texture-readback",
                        size = size.toULong(),
                        usage = setOf(GPUBufferUsage.MapRead, GPUBufferUsage.CopyDst)
                    )
                )
                encoder.copyTextureToBuffer(
                    source = TexelCopyTextureInfo(gpuTex.gpuTexture),
                    destination = TexelCopyBufferInfo(
                        buffer = mapBuffer,
                        bytesPerRow = (format.pxSize * gpuTex.width).toUInt(),
                        rowsPerImage = gpuTex.height.toUInt()
                    ),
                    copySize = Extent3D(gpuTex.width.toUInt(), gpuTex.height.toUInt(), gpuTex.depth.toUInt())
                )
                readback.mapBuffer = mapBuffer
            }
        }
    }

    private suspend fun mapReadbacks() {
        gpuReadbacks.filterIsInstance<ReadbackStorageBuffer>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            mapBuffer.mapAsync(setOf(GPUMapMode.Read)).onSuccess {
                mapBuffer.getMappedRange()
                    .writeInto(readback.resultBuffer)
                mapBuffer.unmap()
                mapBuffer.close()
                readback.deferred.complete(Unit)
            }
        }

        gpuReadbacks.filterIsInstance<ReadbackTexture>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            mapBuffer.mapAsync(setOf(GPUMapMode.Read)).onSuccess {
                val gpuTex = readback.texture.gpuTexture as WgpuTextureResource
                val format = readback.texture.format
                val dst = ImageData.createBuffer(format, gpuTex.width, gpuTex.height, gpuTex.depth)
                mapBuffer.getMappedRange()
                    .writeInto(dst)
                mapBuffer.unmap()
                mapBuffer.close()
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

    override fun createBuffer(descriptor: GPUBufferDescriptor, info: String?): GpuBufferWgpu {
        return GpuBufferWgpu(device.createBuffer(descriptor), descriptor.size.toLong(), info)
    }

    override fun createTexture(descriptor: GPUTextureDescriptor): WgpuTextureResource {
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

}