package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT
import org.lwjgl.vulkan.VK10.vkQueueWaitIdle
import org.lwjgl.vulkan.VkCommandBuffer
import java.nio.ByteBuffer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class RenderBackendVk(val ctx: Lwjgl3Context) : RenderBackendJvm {
    override val name = "Vulkan backend"
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
    var swapchain: Swapchain; private set
    val textureLoader: TextureLoaderVk
    val pipelineManager: PipelineManager
    val screenRenderPass: ScreenRenderPassVk
    val clearHelper: ClearHelper
    val timestampQueryPool: TimestampQueryPool

    override var frameGpuTime: Duration = 0.0.seconds

    private val frameTimer: Timer
    private val passEncoderState = PassEncoderState(this)
    private var windowResized = false
    private val gpuReadbacks = mutableListOf<GpuReadback>()

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
            depthOnlyShaderColorOutput = null,
            maxComputeWorkGroupsPerDimension = Vec3i(
                physicalDevice.deviceProperties.limits().maxComputeWorkGroupCount(0),
                physicalDevice.deviceProperties.limits().maxComputeWorkGroupCount(1),
                physicalDevice.deviceProperties.limits().maxComputeWorkGroupCount(2),
            ),
            maxComputeWorkGroupSize = Vec3i(
                physicalDevice.deviceProperties.limits().maxComputeWorkGroupSize(0),
                physicalDevice.deviceProperties.limits().maxComputeWorkGroupSize(1),
                physicalDevice.deviceProperties.limits().maxComputeWorkGroupSize(2),
            ),
            maxComputeInvocationsPerWorkgroup = physicalDevice.deviceProperties.limits().maxComputeWorkGroupInvocations(),
        )

        if (device.computeQueue != null && device.computeQueue != device.graphicsQueue) {
            logW { "Compute queue is available but differs from graphics queue, which is currently not supported. Compute features are disabled." }
        }

        memManager = MemoryManager(this)
        commandPool = CommandPool(this, device.graphicsQueue)
        commandBuffers = commandPool.allocateCommandBuffers(Swapchain.MAX_FRAMES_IN_FLIGHT)
        swapchain = Swapchain(this)
        timestampQueryPool = TimestampQueryPool(this)
        textureLoader = TextureLoaderVk(this)
        pipelineManager = PipelineManager(this)
        clearHelper = ClearHelper(this)
        screenRenderPass = ScreenRenderPassVk(this)

        glfwWindow.onResize += GlfwVkWindow.OnWindowResizeListener { _, _ -> windowResized = true }

        frameTimer = Timer(timestampQueryPool) { delta -> frameGpuTime = delta }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        memStack {
            var imgOk = swapchain.acquireNextImage()
            if (imgOk) {
                DeferredRelease.processTasks()

                passEncoderState.beginFrame(this)
                frameTimer.begin(passEncoderState.commandBuffer)

                preparePipelines(passEncoderState, ctx)

                ctx.backgroundScene.renderOffscreenPasses(passEncoderState)
                if (ctx.scenes.isEmpty()) {
                    screenRenderPass.renderScene(ctx.backgroundScene.mainRenderPass, passEncoderState)
                }

                for (i in ctx.scenes.indices) {
                    val scene = ctx.scenes[i]
                    scene.checkIsNotReleased()
                    if (scene.isVisible) {
                        scene.sceneRecordTime += measureTime {
                            scene.renderOffscreenPasses(passEncoderState)
                            screenRenderPass.renderScene(scene.mainRenderPass, passEncoderState)
                        }
                    }
                }

                passEncoderState.ensureRenderPassInactive()
                if (gpuReadbacks.isNotEmpty()) {
                    // copy all buffers requested for readback to temporary buffers using the current command encoder
                    copyReadbacks(passEncoderState)
                }
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

    private fun preparePipelines(passEncoderState: PassEncoderState, ctx: KoolContext) {
        for (i in ctx.backgroundScene.sortedOffscreenPasses.indices) {
            preparePipelines(ctx.backgroundScene.sortedOffscreenPasses[i], passEncoderState)
        }
        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            scene.sceneRecordTime = measureTime {
                for (j in scene.sortedOffscreenPasses.indices) {
                    preparePipelines(scene.sortedOffscreenPasses[j], passEncoderState)
                }
                preparePipelines(scene.mainRenderPass, passEncoderState)
            }
        }
    }

    private fun preparePipelines(renderPass: RenderPass, passEncoderState: PassEncoderState) {
        for (i in renderPass.views.indices) {
            val queue = renderPass.views[i].drawQueue
            queue.forEach { cmd -> pipelineManager.prepareDrawPipeline(cmd, passEncoderState) }
        }
    }

    private fun Scene.renderOffscreenPasses(passEncoderState: PassEncoderState) {
        for (i in sortedOffscreenPasses.indices) {
            val pass = sortedOffscreenPasses[i]
            if (pass.isEnabled) {
                pass.render(passEncoderState)
            }
        }
    }

    private fun OffscreenRenderPass.render(passEncoderState: PassEncoderState) {
        when (this) {
            is OffscreenRenderPass2d -> draw(passEncoderState)
            is OffscreenRenderPassCube -> draw(passEncoderState)
            is OffscreenRenderPass2dPingPong -> draw(passEncoderState)
            is ComputePass -> dispatch(passEncoderState)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
    }

    private fun OffscreenRenderPass2dPingPong.draw(passEncoderState: PassEncoderState) {
        for (i in 0 until pingPongPasses) {
            onDrawPing?.invoke(i)
            ping.draw(passEncoderState)
            onDrawPong?.invoke(i)
            pong.draw(passEncoderState)
        }
    }

    private fun OffscreenRenderPass2d.draw(passEncoderState: PassEncoderState) {
        (impl as OffscreenPass2dVk).draw(passEncoderState)
    }

    private fun OffscreenRenderPassCube.draw(passEncoderState: PassEncoderState) {
        (impl as OffscreenPassCubeVk).draw(passEncoderState)
    }

    private fun ComputePass.dispatch(passEncoderState: PassEncoderState) {
        (impl as ComputePassVk).dispatch(passEncoderState)
    }

    override fun cleanup(ctx: KoolContext) {
        device.waitForIdle()
        DeferredRelease.processTasks(true)
        instance.release()
        DeferredRelease.processTasks(true)
    }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode {
        val src = KslGlslGeneratorVk().generateProgram(shader.program, pipeline)
        return ShaderCodeVk.drawShaderCode(src.vertexSrc, src.fragmentSrc)
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val src = KslGlslGeneratorVk().generateComputeProgram(shader.program, pipeline)
        return ShaderCodeVk.computeShaderCode(src.computeSrc)
    }

    override fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl {
        return OffscreenPass2dVk(parentPass, 1, this)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
        return OffscreenPassCubeVk(parentPass, 1, this)
    }

    override fun createComputePass(parentPass: ComputePass): ComputePassImpl {
        return ComputePassVk(parentPass, this)
    }

    override fun <T : ImageData> uploadTextureData(tex: Texture<T>) = textureLoader.loadTexture(tex)

    override fun downloadStorageBuffer(storage: StorageBuffer, deferred: CompletableDeferred<Unit>) {
        gpuReadbacks += ReadbackStorageBuffer(storage, deferred)
    }

    override fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>) {
        gpuReadbacks += ReadbackTexture(texture, deferred)
    }

    private fun copyReadbacks(passEncoderState: PassEncoderState) {
        gpuReadbacks.filterIsInstance<ReadbackStorageBuffer>().forEach { readback ->
            val gpuBuf = readback.storage.gpuBuffer as BufferVk?
            if (gpuBuf == null) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading buffer"))
            } else {
                val size = gpuBuf.bufferSize
                val mapBuffer = BufferVk(
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
            if (gpuTex == null || readback.texture.props.format.isF16) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading texture"))
            } else {
                val format = readback.texture.props.format
                val size = format.pxSize.toLong() * gpuTex.width * gpuTex.height * gpuTex.depth
                val mapBuffer = BufferVk(
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
            readback.storage.buffer.copyFrom(mapped)
            mapBuffer.release()
            readback.deferred.complete(Unit)
        }

        gpuReadbacks.filterIsInstance<ReadbackTexture>().filter { it.mapBuffer != null }.forEach { readback ->
            val mapBuffer = readback.mapBuffer!!
            val mapped = checkNotNull(mapBuffer.vkBuffer.mapped) { "readback buffer was not created mapped" }
            val gpuTex = readback.texture.gpuTexture as ImageVk
            val format = readback.texture.props.format
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

    private fun recreateSwapchain() {
        // Theoretically it might be possible for the swapchain image format to change (e.g. because the window
        // is moved to another monitor with HDR) in that case the screen render pass would also need to be
        // recreated.
        // However, currently, image format is more or less hardcoded to 8-bit SRGB in PhysicalDevice, so no need
        // for all the fuzz.

        device.waitForIdle()
        swapchain.release()
        swapchain = Swapchain(this)
        screenRenderPass.onSwapchainRecreated()
    }

    private interface GpuReadback

    private class ReadbackStorageBuffer(val storage: StorageBuffer, val deferred: CompletableDeferred<Unit>) : GpuReadback {
        var mapBuffer: BufferVk? = null
    }

    private class ReadbackTexture(val texture: Texture<*>, val deferred: CompletableDeferred<ImageData>) : GpuReadback {
        var mapBuffer: BufferVk? = null
    }
}