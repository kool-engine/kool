package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.platform.vk.RenderPass
import de.fabmax.kool.platform.vk.SwapChain
import de.fabmax.kool.platform.vk.VkSystem
import de.fabmax.kool.util.logD

class PipelineManager(val sys: VkSystem) {

    private val onScreenPipelineConfigs = mutableSetOf<Pipeline>()

    private var swapChain: SwapChain? = null
    private val createdPipelines = mutableMapOf<ULong, CreatedPipeline>()

    fun onSwapchainDestroyed() {
        swapChain = null
        createdPipelines.clear()
    }

    fun onSwapchainCreated(swapChain: SwapChain) {
        this.swapChain = swapChain
        onScreenPipelineConfigs.forEach { createOnScreenPipeline(it, swapChain.renderPass) }
    }

    fun hasPipeline(pipeline: Pipeline, renderPass: Long): Boolean =
            createdPipelines[pipeline.pipelineHash]?.existsForRenderPass(renderPass) ?: false

    fun addPipelineConfig(pipeline: Pipeline, nImages: Int, renderPass: RenderPass, dynVp: Boolean) {
        if (renderPass === swapChain?.renderPass) {
            if (onScreenPipelineConfigs.add(pipeline)) {
                logD { "New on screen pipeline: ${pipeline.name}" }
                createOnScreenPipeline(pipeline, renderPass)
            }
        } else {
            val gp = GraphicsPipeline(sys, renderPass, 1, dynVp, pipeline, nImages)
            sys.device.addDependingResource(gp)
            val createdPipeline = createdPipelines.getOrPut(pipeline.pipelineHash) { CreatedPipeline(false) }
            createdPipeline.addRenderPassPipeline(renderPass, gp)
        }
    }

    private fun createOnScreenPipeline(pipeline: Pipeline, renderPass: RenderPass) {
        val swapChain = this.swapChain ?: return
        val gp = GraphicsPipeline(sys, renderPass, sys.physicalDevice.msaaSamples, false, pipeline, swapChain.nImages)
        swapChain.addDependingResource(gp)
        val createdPipeline = createdPipelines.getOrPut(pipeline.pipelineHash) { CreatedPipeline(true) }
        createdPipeline.addRenderPassPipeline(renderPass, gp)
    }

    fun getPipeline(pipeline: Pipeline, renderPass: Long): GraphicsPipeline {
        return createdPipelines[pipeline.pipelineHash]?.graphicsPipelines?.get(renderPass)
                ?: throw NoSuchElementException("Unknown pipeline config: ${pipeline.pipelineHash}")
    }

    fun getPipeline(pipeline: Pipeline): CreatedPipeline? {
        return createdPipelines[pipeline.pipelineHash]
    }

    inner class CreatedPipeline(private val isOnScreen: Boolean) {
        val graphicsPipelines = mutableMapOf<Long, GraphicsPipeline>()

        fun existsForRenderPass(renderPass: Long) = graphicsPipelines.containsKey(renderPass)

        fun addRenderPassPipeline(renderPass: RenderPass, graphicsPipeline: GraphicsPipeline) {
            graphicsPipelines[renderPass.vkRenderPass] = graphicsPipeline
        }

        fun freeDescriptorSetInstance(pipeline: Pipeline) {
            // find GraphicsPipeline containing pipelineInstance
            val iterator = graphicsPipelines.values.iterator()
            for (gp in iterator) {
                if (gp.freeDescriptorSetInstance(pipeline) && gp.isEmpty()) {
                    // all descriptor set instances of this particular pipeline are freed, destroy it..
                    if (isOnScreen) {
                        swapChain?.removeDependingResource(gp)
                    } else {
                        sys.device.removeDependingResource(gp)
                    }
                    gp.destroy()
                    iterator.remove()
                }
            }
            if (graphicsPipelines.isEmpty()) {
                // all pipelines destroyed, remove CreatedPipeline instance
                val succ = createdPipelines.remove(pipeline.pipelineHash) != null
                onScreenPipelineConfigs.remove(pipeline)
            }
        }
    }
}