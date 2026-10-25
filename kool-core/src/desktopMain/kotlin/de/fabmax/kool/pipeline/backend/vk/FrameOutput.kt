package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import org.lwjgl.system.MemoryStack

/**
 * Final destination of rendered frames. Either a [Swapchain] (regular windowed mode) or a [HeadlessOutput]
 * (headless mode without any window / surface).
 */
abstract class FrameOutput : BaseReleasable() {
    abstract val width: Int
    abstract val height: Int

    /**
     * Image format used by the screen render pass. Draw pipelines are created for this format, so it must not
     * change when the output is recreated.
     */
    abstract val renderFormat: Int

    /**
     * Semaphore the frame submission has to wait for (if any).
     */
    abstract val waitSemaphore: VkSemaphore?

    /**
     * Semaphore signaled by the frame submission (if any).
     */
    abstract val signalSemaphore: VkSemaphore?

    /**
     * Called at the beginning of a frame, after the previous frame with the same frame index was completed.
     * Returns false if the output is out of date and needs to be recreated.
     */
    abstract fun acquireNextImage(): Boolean

    /**
     * Records the commands needed to transfer the final screen pass output into this output.
     */
    abstract fun recordOutput(screenPass: ScreenPassVk, passEncoderState: PassEncoderState)

    /**
     * Called after the frame was submitted. Returns false if the output is out of date and needs to be recreated.
     */
    abstract fun present(stack: MemoryStack): Boolean
}
