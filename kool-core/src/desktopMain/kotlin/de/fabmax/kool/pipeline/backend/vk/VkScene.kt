package de.fabmax.kool.pipeline.backend.vk

import java.nio.LongBuffer

interface VkScene {

    fun onLoad(sys: VkSystem)

    fun onSwapChainCreated(swapChain: Swapchain)

    fun onDrawFrame(swapChain: Swapchain, imageIndex: Int, fence: LongBuffer, waitSema: LongBuffer, signalSema: LongBuffer)

    fun onDestroy(sys: VkSystem)

}