package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.*
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.*
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.createVkWindow
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import java.nio.ByteBuffer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RenderBackendVk(val ctx: Lwjgl3Context) : RenderBackendJvm {
    override val name = "Vulkan"
    override val apiName: String
    override val deviceName: String

    override val window: KoolWindowJvm

    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.VULKAN
    override val features: BackendFeatures

    val setup = KoolSystem.configJvm.vkSetup ?: VkSetup()

    val instance: Instance
    var surface: Surface
    val physicalDevice: PhysicalDevice
    val device: Device
    val memManager: MemoryManager
    val commandPool: CommandPool
    val commandBuffers: List<VkCommandBuffer>
    val descriptorPools: DescriptorPoolManager
    var swapchain: Swapchain; private set
    val textureLoader: TextureLoaderVk
    val pipelineManager: PipelineManager
    val screenPass: ScreenPassVk
    val clearHelper: ClearHelper
    val timestampQueryPool: TimestampQueryPool

    override var frameGpuTime: Duration = 0.0.seconds

    override val isAsyncRendering: Boolean = KoolSystem.configJvm.asyncSceneUpdate

    private val frameTimer: Timer
    private val passEncoderState = PassEncoderState(this)
    private var windowResized = false
    private val gpuReadbacks = mutableListOf<GpuReadback>()

    private val emptyScene = Scene("empty-scene")

    private var recreateSurfaceFlag = false

    init {
        window = ctx.windowSubsystem.createVkWindow(ctx)
        window.setFullscreen(KoolSystem.configJvm.isFullscreen)
        instance = Instance(this, KoolSystem.configJvm.windowTitle)
        surface = Surface(this)
        window.setVisible(KoolSystem.configJvm.showWindowOnStart)

        physicalDevice = PhysicalDevice(this)
        device = Device(this)
        apiName = "Vulkan ${physicalDevice.apiVersion}"
        deviceName = physicalDevice.deviceName

        features = BackendFeatures(
            computeShaders = device.computeQueue == device.graphicsQueue,
            cubeMapArrays = physicalDevice.cubeMapArrays,
            reversedDepth = true,
            maxSamples = physicalDevice.maxSamples,
            readWriteStorageTextures = true,
            depthOnlyShaderColorOutput = null,
            maxComputeWorkGroupsPerDimension = Vec3i(
                clampUint(physicalDevice.deviceProperties.limits().maxComputeWorkGroupCount(0)),
                clampUint(physicalDevice.deviceProperties.limits().maxComputeWorkGroupCount(1)),
                clampUint(physicalDevice.deviceProperties.limits().maxComputeWorkGroupCount(2)),
            ),
            maxComputeWorkGroupSize = Vec3i(
                clampUint(physicalDevice.deviceProperties.limits().maxComputeWorkGroupSize(0)),
                clampUint(physicalDevice.deviceProperties.limits().maxComputeWorkGroupSize(1)),
                clampUint(physicalDevice.deviceProperties.limits().maxComputeWorkGroupSize(2)),
            ),
            maxComputeInvocationsPerWorkgroup = clampUint(physicalDevice.deviceProperties.limits().maxComputeWorkGroupInvocations()),
        )

        if (device.computeQueue != null && device.computeQueue != device.graphicsQueue) {
            logW { "Compute queue is available but differs from graphics queue, which is currently not supported. Compute features are disabled." }
        }

        memManager = MemoryManager(this)
        commandPool = CommandPool(this, device.graphicsQueue)
        commandBuffers = commandPool.allocateCommandBuffers(Swapchain.MAX_FRAMES_IN_FLIGHT)
        descriptorPools = DescriptorPoolManager(this)
        swapchain = Swapchain(this)
        timestampQueryPool = TimestampQueryPool(this)
        textureLoader = TextureLoaderVk(this)
        pipelineManager = PipelineManager(this)
        clearHelper = ClearHelper(this)
        screenPass = ScreenPassVk(this)

        frameTimer = Timer(timestampQueryPool) { delta -> frameGpuTime = delta }

        window.onResize { windowResized = true }
        window.onScaleChange { windowResized = true }
    }

    fun recreateSurface() {
        recreateSurfaceFlag = true
    }

    private fun clampUint(unsignedCnt: Int): Int {
        return if (unsignedCnt < 0) Int.MAX_VALUE else unsignedCnt
    }

    override fun renderFrame(frameData: FrameData, ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        memStack {
            var imgOk = swapchain.acquireNextImage()
            if (imgOk) {
                ReleaseQueue.processQueue()

                passEncoderState.beginFrame(this)
                timestampQueryPool.onBeginFrame()
                frameTimer.begin(passEncoderState.commandBuffer)

                frameData.preparePipelines(passEncoderState)
                frameData.executePasses(passEncoderState)
                if (frameData.passData.isEmpty()) {
                    // make sure any scene is rendered, so that screen is correctly cleared
                    val passData = frameData.acquirePassData(emptyScene.mainRenderPass)
                    emptyScene.mainRenderPass.collect(passData, ctx)
                    passData.executePass(passEncoderState)
                }

                passEncoderState.ensureRenderPassInactive()
                if (gpuReadbacks.isNotEmpty()) {
                    // copy all buffers requested for readback to temporary buffers using the current command encoder
                    copyReadbacks(passEncoderState)
                }
                screenPass.blitOutputImage(passEncoderState)
                frameTimer.end(passEncoderState.commandBuffer)
                timestampQueryPool.pollResults(passEncoderState.commandBuffer, this)
                passEncoderState.endFrame()

                if (gpuReadbacks.isNotEmpty()) {
                    // wait until queue is completely processed before reading back the buffers
                    vkQueueWaitIdle(device.graphicsQueue)
                    mapReadbacks()
                }
                imgOk = swapchain.presentNextImage(this)
            }
            if (!imgOk || windowResized || recreateSurfaceFlag) {
                logD { "Recreate swapchain" }
                windowResized = false
                recreateSwapchain(setup.recreateSurfaceWithSwapchain || recreateSurfaceFlag)
                recreateSurfaceFlag = false
            }
        }
    }

    private fun FrameData.preparePipelines(passEncoderState: PassEncoderState) {
        forEachPass { passData ->
            val t = Time.precisionTime
            passData.forEachView { viewData ->
                viewData.drawQueue.forEach { cmd -> pipelineManager.prepareDrawPipeline(cmd, passEncoderState) }
            }
            passData.gpuPass.tRecord = (Time.precisionTime - t).seconds
        }
    }

    private fun FrameData.executePasses(passEncoderState: PassEncoderState) {
        forEachPass { passData -> passData.executePass(passEncoderState) }
    }

    private fun PassData.executePass(passEncoderState: PassEncoderState) {
        val pass = gpuPass
        val t = Time.precisionTime
        when (pass) {
            is Scene.ScreenPass -> screenPass.renderScene(this, passEncoderState)
            is OffscreenPass2d -> pass.draw(this, passEncoderState)
            is OffscreenPassCube -> pass.draw(this, passEncoderState)
            is ComputePass -> pass.dispatch(passEncoderState)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
        pass.tRecord += (Time.precisionTime - t).seconds
    }

    private fun OffscreenPass2d.draw(passData: PassData, passEncoderState: PassEncoderState) {
        (impl as OffscreenPass2dVk).draw(passData, passEncoderState)
    }

    private fun OffscreenPassCube.draw(passData: PassData, passEncoderState: PassEncoderState) {
        (impl as OffscreenPassCubeVk).draw(passData, passEncoderState)
    }

    private fun ComputePass.dispatch(passEncoderState: PassEncoderState) {
        (impl as ComputePassVk).dispatch(passEncoderState)
    }

    override fun cleanup(ctx: KoolContext) {
        device.waitForIdle()
        ReleaseQueue.processQueue(true)
        surface.release()
        instance.release()
        ReleaseQueue.processQueue(true)
    }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode {
        val src = GlslGeneratorVk.generateProgram(shader.program, pipeline)
        return ShaderCodeVk.drawShaderCode(src.vertexSrc, src.fragmentSrc)
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val src = GlslGeneratorVk.generateComputeProgram(shader.program, pipeline)
        return ShaderCodeVk.computeShaderCode(src.computeSrc)
    }

    override fun createOffscreenPass2d(parentPass: OffscreenPass2d): OffscreenPass2dImpl {
        return OffscreenPass2dVk(parentPass, this)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenPassCube): OffscreenPassCubeImpl {
        return OffscreenPassCubeVk(parentPass, this)
    }

    override fun createComputePass(parentPass: ComputePass): ComputePassImpl {
        return ComputePassVk(parentPass, this)
    }

    override fun <T : ImageData> uploadTextureData(tex: Texture<T>) = textureLoader.loadTexture(tex)

    override fun downloadBuffer(buffer: GpuBuffer, deferred: CompletableDeferred<Unit>, resultBuffer: Buffer) {
        gpuReadbacks += ReadbackStorageBuffer(buffer, deferred, resultBuffer)
    }

    override fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>) {
        val gpuTex = texture.gpuTexture as ImageVk?
        if (gpuTex == null) {
            deferred.completeExceptionally(IllegalArgumentException("Texture is not loaded"))
            return
        }
        if (gpuTex.imageInfo.usage and VK_IMAGE_USAGE_TRANSFER_SRC_BIT == 0) {
            deferred.completeExceptionally(IllegalArgumentException("Texture is not copyable (misses VK_IMAGE_USAGE_TRANSFER_SRC_BIT usage flag)"))
            return
        }
        gpuReadbacks += ReadbackTexture(texture, deferred)
    }

    private fun copyReadbacks(passEncoderState: PassEncoderState) {
        gpuReadbacks.filterIsInstance<ReadbackStorageBuffer>().forEach { readback ->
            val gpuBuf = readback.storage.gpuBuffer as GpuBufferVk?
            if (gpuBuf == null) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading buffer"))
            } else {
                val size = gpuBuf.bufferSize
                val mapBuffer = GpuBufferVk(
                    this,
                    MemoryInfo(
                        label = "storage-buffer-readback",
                        size = size,
                        usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                        isReadback = true
                    )
                )
                mapBuffer.copyFrom(gpuBuf.vkBuffer, passEncoderState.commandBuffer)
                readback.mapBuffer = mapBuffer
            }
        }

        gpuReadbacks.filterIsInstance<ReadbackTexture>().forEach { readback ->
            val gpuTex = readback.texture.gpuTexture as ImageVk?
            if (gpuTex == null || readback.texture.format.isF16) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading texture"))
            } else {
                val format = readback.texture.format
                val size = format.pxSize.toLong() * gpuTex.width * gpuTex.height * gpuTex.depth
                val mapBuffer = GpuBufferVk(
                    this,
                    MemoryInfo(
                        label = "texture-readback",
                        size = size,
                        usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                        isReadback = true
                    )
                )
                gpuTex.copyToBuffer(mapBuffer.vkBuffer, passEncoderState.commandBuffer)
                readback.mapBuffer = mapBuffer
            }
        }
    }

    private fun mapReadbacks() {
        gpuReadbacks.filterIsInstance<ReadbackStorageBuffer>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            val mapped = checkNotNull(mapBuffer.vkBuffer.mapped) { "readback buffer was not created mapped" }
            readback.resultBuffer.copyFrom(mapped)
            mapBuffer.release()
            readback.deferred.complete(Unit)
        }

        gpuReadbacks.filterIsInstance<ReadbackTexture>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            val mapped = checkNotNull(mapBuffer.vkBuffer.mapped) { "readback buffer was not created mapped" }
            val gpuTex = readback.texture.gpuTexture as ImageVk
            val format = readback.texture.format
            val dst = ImageData.createBuffer(format, gpuTex.width, gpuTex.height, gpuTex.depth)
            dst.copyFrom(mapped)
            mapBuffer.release()
            when (readback.texture) {
                is Texture1d -> readback.deferred.complete(BufferedImageData1d(dst, gpuTex.width, format))
                is Texture2d -> readback.deferred.complete(BufferedImageData2d(dst, gpuTex.width, gpuTex.height, format))
                is Texture3d -> readback.deferred.complete(BufferedImageData3d(dst, gpuTex.width, gpuTex.height, gpuTex.depth, format))
                else -> readback.deferred.completeExceptionally(IllegalArgumentException("Unsupported texture type"))
            }
        }

        gpuReadbacks.clear()
    }

    private fun Buffer.copyFrom(src: ByteBuffer) {
        when (this) {
            is Uint8BufferImpl -> useRaw { it.put(src) }
            is Uint16BufferImpl -> useRaw { it.put(src.asShortBuffer()) }
            is Int32BufferImpl -> useRaw { it.put(src.asIntBuffer()) }
            is Float32BufferImpl -> useRaw { it.put(src.asFloatBuffer()) }
            is MixedBufferImpl -> useRaw { it.put(src) }
            else -> logE { "Unexpected buffer type: ${this::class.simpleName}" }
        }
    }

    private fun recreateSwapchain(recreateSurface: Boolean) {
        // Theoretically it might be possible for the swapchain image format to change (e.g. because the window
        // is moved to another monitor with HDR) in that case the screen render pass would also need to be
        // recreated.
        // However, currently, image format is more or less hardcoded to 8-bit SRGB in PhysicalDevice, so no need
        // for all the fuzz.

        device.waitForIdle()
        swapchain.release()
        if (recreateSurface) {
            surface.release()
            surface = Surface(this)
        }
        swapchain = Swapchain(this)
        screenPass.onSwapchainRecreated()
    }

    private interface GpuReadback

    private class ReadbackStorageBuffer(val storage: GpuBuffer, val deferred: CompletableDeferred<Unit>, val resultBuffer: Buffer) : GpuReadback {
        var mapBuffer: GpuBufferVk? = null
    }

    private class ReadbackTexture(val texture: Texture<*>, val deferred: CompletableDeferred<ImageData>) : GpuReadback {
        var mapBuffer: GpuBufferVk? = null
    }

    companion object : BackendProvider {
        override val displayName: String = "Vulkan"

        override suspend fun createBackend(ctx: KoolContext): Result<RenderBackend> {
            return try {
                Result.success(RenderBackendVk(ctx as Lwjgl3Context))
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}