package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.*
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import java.nio.ByteBuffer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class RenderBackendVk(val ctx: Lwjgl3Context) : RenderBackendJvm {
    override val name = "Vulkan"
    override val apiName: String
    override val deviceName: String

    override val glfwWindow: GlfwVkWindow

    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.VULKAN
    override val features: BackendFeatures

    val setup = KoolSystem.configJvm.vkSetup ?: VkSetup()

    val instance: Instance
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

    private val frameTimer: Timer
    private val passEncoderState = PassEncoderState(this)
    private var windowResized = false
    private val gpuReadbacks = mutableListOf<GpuReadback>()

    private val emptyScene = Scene("empty-scene")

    init {
        // tell GLFW to not initialize default OpenGL API before we create the window
        check(GLFWVulkan.glfwVulkanSupported()) { "Cannot find a compatible Vulkan installable client driver (ICD)" }
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API)

        glfwWindow = GlfwVkWindow(this, ctx)
        glfwWindow.isFullscreen = KoolSystem.configJvm.isFullscreen
        instance = Instance(this, KoolSystem.configJvm.windowTitle)
        glfwWindow.createSurface()

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

        glfwWindow.onResize += GlfwVkWindow.OnWindowResizeListener { _, _ -> windowResized = true }

        frameTimer = Timer(timestampQueryPool) { delta -> frameGpuTime = delta }
    }

    private fun clampUint(unsignedCnt: Int): Int {
        return if (unsignedCnt < 0) Int.MAX_VALUE else unsignedCnt
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        memStack {
            var imgOk = swapchain.acquireNextImage()
            if (imgOk) {
                ReleaseQueue.processQueue()

                passEncoderState.beginFrame(this)
                timestampQueryPool.onBeginFrame()
                frameTimer.begin(passEncoderState.commandBuffer)

                ctx.preparePipelines(passEncoderState)
                ctx.executePasses(passEncoderState)
                if (ctx.scenes.isEmpty()) {
                    // make sure any scene is rendered, so that screen is correctly cleared
                    emptyScene.mainRenderPass.update(ctx)
                    emptyScene.executePasses(passEncoderState)
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
            if (!imgOk || windowResized) {
                windowResized = false
                recreateSwapchain()
            }
        }
    }

    private fun KoolContext.preparePipelines(passEncoderState: PassEncoderState) {
        backgroundScene.prepareDrawPipelines(passEncoderState)
        for (i in scenes.indices) {
            val scene = scenes[i]
            if (scene.isVisible) {
                scene.prepareDrawPipelines(passEncoderState)
            }
        }
    }

    private fun Scene.prepareDrawPipelines(passEncoderState: PassEncoderState) {
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

    private fun RenderPass.prepareDrawPipelines(passEncoderState: PassEncoderState) {
        if (isEnabled) {
            for (i in views.indices) {
                val queue = views[i].drawQueue
                queue.forEach { cmd -> pipelineManager.prepareDrawPipeline(cmd, passEncoderState) }
            }
        }
    }

    private fun KoolContext.executePasses(passEncoderState: PassEncoderState) {
        backgroundScene.executePasses(passEncoderState)
        for (i in scenes.indices) {
            val scene = scenes[i]
            if (scene.isVisible) {
                scene.executePasses(passEncoderState)
            }
        }
    }

    private fun Scene.executePasses(passEncoderState: PassEncoderState) {
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

    private fun GpuPass.execute(passEncoderState: PassEncoderState) {
        when (this) {
            is Scene.ScreenPass -> screenPass.renderScene(this, passEncoderState)
            is OffscreenPass2d -> draw(passEncoderState)
            is OffscreenPassCube -> draw(passEncoderState)
            is ComputePass -> dispatch(passEncoderState)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
    }

    private fun OffscreenPass2d.draw(passEncoderState: PassEncoderState) {
        (impl as OffscreenPass2dVk).draw(passEncoderState)
    }

    private fun OffscreenPassCube.draw(passEncoderState: PassEncoderState) {
        (impl as OffscreenPassCubeVk).draw(passEncoderState)
    }

    private fun ComputePass.dispatch(passEncoderState: PassEncoderState) {
        (impl as ComputePassVk).dispatch(passEncoderState)
    }

    override fun cleanup(ctx: KoolContext) {
        device.waitForIdle()
        ReleaseQueue.processQueue(true)
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

    override fun initStorageTexture(storageTexture: StorageTexture, width: Int, height: Int, depth: Int) {
        val usage = VK_IMAGE_USAGE_STORAGE_BIT or
                VK_IMAGE_USAGE_TRANSFER_SRC_BIT or
                VK_IMAGE_USAGE_TRANSFER_DST_BIT or
                VK_IMAGE_USAGE_SAMPLED_BIT
        val imageType = when (storageTexture) {
            is StorageTexture1d -> VK_IMAGE_TYPE_1D
            is StorageTexture2d -> VK_IMAGE_TYPE_2D
            is StorageTexture3d -> VK_IMAGE_TYPE_3D
        }
        val levels = when (val mipMapping = storageTexture.mipMapping) {
            MipMapping.Full -> numMipLevels(width, height, depth)
            is MipMapping.Limited -> mipMapping.numLevels
            MipMapping.Off -> 1
        }
        val imageInfo = ImageInfo(
            imageType = imageType,
            format = storageTexture.format.vk,
            width = width,
            height = height,
            depth = depth,
            arrayLayers = 1,
            mipLevels = levels,
            samples = 1,
            usage = usage,
            label = storageTexture.name,
            aspectMask = VK_IMAGE_ASPECT_COLOR_BIT
        )
        val storageImage = ImageVk(this, imageInfo)
        commandPool.singleShotCommands { commandBuffer ->
            storageImage.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_GENERAL, commandBuffer)
        }
        storageTexture.gpuTexture?.release()
        storageTexture.gpuTexture = storageImage
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

    fun recreateSwapchain() {
        // Theoretically it might be possible for the swapchain image format to change (e.g. because the window
        // is moved to another monitor with HDR) in that case the screen render pass would also need to be
        // recreated.
        // However, currently, image format is more or less hardcoded to 8-bit SRGB in PhysicalDevice, so no need
        // for all the fuzz.

        device.waitForIdle()
        swapchain.release()
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
                Result.failure(e)
            }
        }
    }
}