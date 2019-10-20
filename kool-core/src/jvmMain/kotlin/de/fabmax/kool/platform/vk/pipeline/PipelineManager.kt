package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.platform.vk.SwapChain
import de.fabmax.kool.platform.vk.VkSystem

class PipelineManager(val sys: VkSystem) {

    private val pipelineConfigs = mutableListOf<VkPipelineConfig>()

    private var swapChain: SwapChain? = null
    private val mutPipelines = mutableMapOf<VkPipelineConfig, GraphicsPipeline>()
    val pipelines: Map<VkPipelineConfig, GraphicsPipeline> = mutPipelines

    fun onSwapchainDestroyed() {
        swapChain = null
        mutPipelines.clear()
    }

    fun onSwapchainCreated(swapChain: SwapChain) {
        this.swapChain = swapChain

        pipelineConfigs.forEach {
            mutPipelines[it] = GraphicsPipeline(swapChain, it)
        }
    }

    fun hasPipeline(pipelineConfig: VkPipelineConfig): Boolean = pipelines.containsKey(pipelineConfig)

    fun addPipelineConfig(pipelineConfig: VkPipelineConfig) {
        pipelineConfigs += pipelineConfig

        swapChain?.let {
            mutPipelines[pipelineConfig] = GraphicsPipeline(it, pipelineConfig)
        }
    }

    fun getPipeline(pipelineConfig: VkPipelineConfig): GraphicsPipeline {
        return mutPipelines[pipelineConfig] ?: throw NoSuchElementException("Unknown pipeline config")
    }
}