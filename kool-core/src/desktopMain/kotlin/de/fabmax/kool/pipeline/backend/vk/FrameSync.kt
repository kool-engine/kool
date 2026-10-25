package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.releaseWith
import de.fabmax.kool.util.scopedMem
import org.lwjgl.vulkan.VK10.*

/**
 * Manages the frames in flight: Keeps track of the current frame index and owns the fences used to wait for
 * frames to complete. Independent of the actual frame output (swapchain or headless).
 */
class FrameSync(val backend: RenderBackendVk) : BaseReleasable() {
    private val device: Device get() = backend.device

    var currentFrameIndex = 0
        private set

    private val inFlightFences: List<VkFence>
    val inFlightFence: VkFence get() = inFlightFences[currentFrameIndex]

    init {
        inFlightFences = scopedMem {
            List(MAX_FRAMES_IN_FLIGHT) {
                device.createFence(this) { flags(VK_FENCE_CREATE_SIGNALED_BIT) }
            }
        }
        releaseWith(backend.device)
        logT { "Created frame sync" }
    }

    /**
     * Blocks until the GPU finished the frame previously rendered with the current frame index.
     */
    fun waitForCurrentFrame() {
        vkWaitForFences(device.vkDevice, inFlightFence.handle, true, -1)
    }

    /**
     * Returns true if the GPU finished the frame previously rendered with the given frame index.
     */
    fun isFrameComplete(frameIndex: Int): Boolean {
        return vkGetFenceStatus(device.vkDevice, inFlightFences[frameIndex].handle) == VK_SUCCESS
    }

    /**
     * Resets the fence of the current frame. Must only be called right before the fence is used for a new submit,
     * otherwise the next [waitForCurrentFrame] call for this frame index would block forever.
     */
    fun resetCurrentFence() {
        vkResetFences(device.vkDevice, inFlightFence.handle)
    }

    fun advanceFrame() {
        currentFrameIndex = (currentFrameIndex + 1) % MAX_FRAMES_IN_FLIGHT
    }

    override fun doRelease() {
        inFlightFences.forEach { device.destroyFence(it) }
        logT { "Destroyed frame sync" }
    }

    companion object {
        const val MAX_FRAMES_IN_FLIGHT = 2
    }
}
