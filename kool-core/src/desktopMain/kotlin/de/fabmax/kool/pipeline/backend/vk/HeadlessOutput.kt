package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.platform.headless.HeadlessFrame
import de.fabmax.kool.platform.headless.HeadlessPixelFormat
import de.fabmax.kool.platform.headless.HeadlessWindow
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.cancelReleaseWith
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.releaseWith
import kotlinx.coroutines.CompletableDeferred
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.KHRSynchronization2.vkCmdPipelineBarrier2KHR
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VK13.*
import org.lwjgl.vulkan.VkMemoryBarrier2

/**
 * Frame output used in headless mode: The screen pass output is blitted into an output image (converting it into
 * the requested pixel format), which is then copied into a host-visible readback buffer. There is one readback
 * buffer per frame in flight, so that frames can be read back asynchronously without stalling the render loop.
 */
class HeadlessOutput(val backend: RenderBackendVk, val window: HeadlessWindow) : FrameOutput() {
    private val subsystem = window.subsystem
    private val frameIndex: Int get() = backend.frameSync.currentFrameIndex

    override val width: Int = window.framebufferSize.x
    override val height: Int = window.framebufferSize.y

    // use the same format as in regular windowed mode (typical swapchain format), so that rendering is identical
    override val renderFormat: Int = VK_FORMAT_B8G8R8A8_UNORM

    override val waitSemaphore: VkSemaphore? get() = null
    override val signalSemaphore: VkSemaphore? get() = null

    private val pixelFormat = subsystem.pixelFormat
    private val outputImage: ImageVk
    private val readbackBuffers: List<VkBuffer>
    private val slots = List(FrameSync.MAX_FRAMES_IN_FLIGHT) { ReadbackSlot(it) }

    init {
        val outputFormat = when (pixelFormat) {
            HeadlessPixelFormat.RGBA8 -> VK_FORMAT_R8G8B8A8_UNORM
            HeadlessPixelFormat.BGRA8 -> VK_FORMAT_B8G8R8A8_UNORM
        }
        outputImage = ImageVk(
            backend = backend,
            imageInfo = ImageInfo(
                imageType = VK_IMAGE_TYPE_2D,
                format = outputFormat,
                width = width,
                height = height,
                depth = 1,
                arrayLayers = 1,
                mipLevels = 1,
                samples = VK_SAMPLE_COUNT_1_BIT,
                usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_TRANSFER_SRC_BIT,
                aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
                label = "headless-output"
            )
        )
        val bufferSize = width.toLong() * height * HeadlessFrame.BYTES_PER_PIXEL
        readbackBuffers = List(FrameSync.MAX_FRAMES_IN_FLIGHT) {
            backend.memManager.createBuffer(
                MemoryInfo(
                    label = "headless-readback-$it",
                    size = bufferSize,
                    usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                    isReadback = true
                )
            )
        }
        releaseWith(backend.device)
        logT { "Created headless output ($width x $height, $pixelFormat)" }
    }

    override fun acquireNextImage(): Boolean {
        // the frame previously rendered with the current frame index is complete -> deliver it if not done yet
        deliverIfPending(slots[frameIndex])
        return true
    }

    override fun recordOutput(screenPass: ScreenPassVk, passEncoderState: PassEncoderState) {
        if (!subsystem.isFrameRequested) {
            // nobody is interested in this frame, skip the readback
            return
        }

        val isSync = subsystem.isSyncReadback
        val captures = subsystem.takePendingCaptures()
        val slot = slots[frameIndex]
        check(!slot.isPending) { "Readback slot $frameIndex is still pending" }

        screenPass.blitOutputImage(
            dstImage = outputImage.vkImage,
            dstWidth = width,
            dstHeight = height,
            dstLayout = VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
            passEncoderState = passEncoderState
        )
        outputImage.lastKnownLayout = VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL
        outputImage.copyToBuffer(readbackBuffers[frameIndex], passEncoderState.commandBuffer, passEncoderState.stack)

        // make the transfer write visible to the host once the frame fence is signaled
        passEncoderState.stack.apply {
            val barrier = VkMemoryBarrier2.calloc(1, this)
            barrier[0]
                .`sType$Default`()
                .srcStageMask(VK_PIPELINE_STAGE_2_ALL_TRANSFER_BIT)
                .srcAccessMask(VK_ACCESS_2_TRANSFER_WRITE_BIT)
                .dstStageMask(VK_PIPELINE_STAGE_2_HOST_BIT)
                .dstAccessMask(VK_ACCESS_2_HOST_READ_BIT)
            val dependency = callocVkDependencyInfo { pMemoryBarriers(barrier) }
            vkCmdPipelineBarrier2KHR(passEncoderState.commandBuffer, dependency)
        }

        slot.isPending = true
        slot.isSync = isSync
        slot.frameCount = Time.frameCount
        slot.captures = captures
    }

    override fun present(stack: MemoryStack): Boolean {
        val prevSlot = slots[(frameIndex + FrameSync.MAX_FRAMES_IN_FLIGHT - 1) % FrameSync.MAX_FRAMES_IN_FLIGHT]
        val currentSlot = slots[frameIndex]

        // deliver frames in order: the previous frame first (if it is complete)
        if (prevSlot.isPending && (currentSlot.isSync || backend.frameSync.isFrameComplete(prevSlot.index))) {
            deliverIfPending(prevSlot)
        }
        if (currentSlot.isPending && currentSlot.isSync) {
            backend.frameSync.waitForCurrentFrame()
            deliverIfPending(currentSlot)
        }
        return true
    }

    private fun deliverIfPending(slot: ReadbackSlot) {
        if (!slot.isPending) {
            return
        }
        val buffer = readbackBuffers[slot.index]
        val mapped = checkNotNull(buffer.mapped) { "readback buffer was not created mapped" }
        backend.memManager.invalidateBuffer(buffer)

        val captures = slot.captures
        slot.isPending = false
        slot.captures = emptyList()

        val pixels = mapped.duplicate().clear().asReadOnlyBuffer()
        val frame = HeadlessFrame(width, height, pixelFormat, pixels, slot.frameCount)
        subsystem.deliverFrame(frame, captures)
    }

    override fun doRelease() {
        cancelReleaseWith(backend.device)
        // device is idle when the output is released (on resize or shutdown) -> deliver pending frames in order
        slots.filter { it.isPending }.sortedBy { it.frameCount }.forEach { deliverIfPending(it) }

        outputImage.release()
        readbackBuffers.forEach { backend.memManager.freeBuffer(it) }
        logT { "Destroyed headless output" }
    }

    private class ReadbackSlot(val index: Int) {
        var isPending = false
        var isSync = false
        var frameCount = 0
        var captures: List<CompletableDeferred<BufferedImageData2d>> = emptyList()
    }
}
