package de.fabmax.kool.pipeline.backend.vk

import java.nio.LongBuffer

interface VkScene {

    fun onLoad(sys: VkSystem)

    fun onSwapChainCreated(swapChain: SwapChain)

    fun onDrawFrame(swapChain: SwapChain, imageIndex: Int, fence: LongBuffer, waitSema: LongBuffer, signalSema: LongBuffer)

    fun onDestroy(sys: VkSystem)

}