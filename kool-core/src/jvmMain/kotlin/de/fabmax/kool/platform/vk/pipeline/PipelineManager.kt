package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.platform.vk.SwapChain
import de.fabmax.kool.platform.vk.VkSystem

class PipelineManager(val sys: VkSystem) {

    private val pipelineConfigs = mutableSetOf<Pipeline>()

    private var swapChain: SwapChain? = null
    private val mutPipelines = mutableMapOf<Long, GraphicsPipeline>()
    private val pipelines: Map<Long, GraphicsPipeline> = mutPipelines

    fun onSwapchainDestroyed() {
        swapChain = null
        mutPipelines.clear()
    }

    fun onSwapchainCreated(swapChain: SwapChain) {
        this.swapChain = swapChain

        pipelineConfigs.forEach {
            mutPipelines[it.pipelineHash] = GraphicsPipeline(swapChain, it)
        }
    }

    fun hasPipeline(pipeline: Pipeline): Boolean = pipelines.containsKey(pipeline.pipelineHash)

    fun addPipelineConfig(pipeline: Pipeline) {
        if (pipelineConfigs.add(pipeline)) {
            swapChain?.let {
                mutPipelines[pipeline.pipelineHash] = GraphicsPipeline(it, pipeline)
            }
        }
    }

    fun getPipeline(pipeline: Pipeline): GraphicsPipeline {
        return mutPipelines[pipeline.pipelineHash] ?: throw NoSuchElementException("Unknown pipeline config: ${pipeline.pipelineHash}")
    }
}