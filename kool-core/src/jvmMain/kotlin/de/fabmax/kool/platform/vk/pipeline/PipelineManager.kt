package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.PipelineConfig
import de.fabmax.kool.platform.vk.SwapChain
import de.fabmax.kool.platform.vk.VkSystem

class PipelineManager(val sys: VkSystem) {

    private val pipelineConfigs = mutableListOf<PipelineConfig>()

    private var swapChain: SwapChain? = null
    private val mutPipelines = mutableMapOf<PipelineConfig, GraphicsPipeline>()
    val pipelines: Map<PipelineConfig, GraphicsPipeline> = mutPipelines

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

    fun hasPipeline(pipelineConfig: PipelineConfig): Boolean = pipelines.containsKey(pipelineConfig)

    fun addPipelineConfig(pipelineConfig: PipelineConfig) {
        pipelineConfigs += pipelineConfig

        swapChain?.let {
            mutPipelines[pipelineConfig] = GraphicsPipeline(it, pipelineConfig)
        }
    }

    fun getPipeline(pipelineConfig: PipelineConfig): GraphicsPipeline {
        return mutPipelines[pipelineConfig] ?: throw NoSuchElementException("Unknown pipeline config")
    }
}