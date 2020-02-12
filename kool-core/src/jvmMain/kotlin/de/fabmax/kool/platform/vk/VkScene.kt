package de.fabmax.kool.platform.vk

import org.lwjgl.vulkan.VkCommandBuffer

interface VkScene {

    fun onLoad(sys: VkSystem)

    fun onSwapChainCreated(swapChain: SwapChain)

    fun onDrawFrame(swapChain: SwapChain, imageIndex: Int): VkCommandBuffer

}