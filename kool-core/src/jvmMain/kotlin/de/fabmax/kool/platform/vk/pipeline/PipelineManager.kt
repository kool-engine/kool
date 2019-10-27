package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.platform.vk.SwapChain
import de.fabmax.kool.platform.vk.VkSystem

class PipelineManager(val sys: VkSystem) {

    private val pipelineConfigs = mutableListOf<Pipeline>()

    private var swapChain: SwapChain? = null
    private val mutPipelines = mutableMapOf<Pipeline, GraphicsPipeline>()
    val pipelines: Map<Pipeline, GraphicsPipeline> = mutPipelines

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

    fun hasPipeline(pipeline: Pipeline): Boolean = pipelines.containsKey(pipeline)

    fun addPipelineConfig(pipeline: Pipeline) {
        pipelineConfigs += pipeline

        swapChain?.let {
            mutPipelines[pipeline] = GraphicsPipeline(it, pipeline)
        }
    }

    fun getPipeline(pipeline: Pipeline): GraphicsPipeline {
        return mutPipelines[pipeline] ?: throw NoSuchElementException("Unknown pipeline config")
    }
}