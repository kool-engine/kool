package de.fabmax.kool.pipeline.backend.vk.pipeline

import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.vk.SwapChain
import de.fabmax.kool.pipeline.backend.vk.VkRenderPass
import de.fabmax.kool.pipeline.backend.vk.VkSystem
import de.fabmax.kool.util.logD

class PipelineManager(val sys: VkSystem) {

    private val onScreenPipelineConfigs = mutableSetOf<PipelineAndRenderPass>()

    private var swapChain: SwapChain? = null
    private val createdPipelines = mutableMapOf<PipelineKey, CreatedPipeline>()

    private data class PipelineKey(val pipelineHash: ULong, val renderPass: Long)

    fun onSwapchainDestroyed() {
        swapChain = null
        createdPipelines.clear()
    }

    fun onSwapchainCreated(swapChain: SwapChain) {
        this.swapChain = swapChain
        onScreenPipelineConfigs.forEach { createOnScreenPipeline(it.pipeline, it.koolRenderPass, swapChain.renderPass) }
    }

    fun hasPipeline(pipeline: Pipeline, renderPass: Long): Boolean =
            createdPipelines[PipelineKey(pipeline.pipelineHash, renderPass)]?.existsForRenderPass(renderPass) ?: false

    fun addPipelineConfig(pipeline: Pipeline, nImages: Int, koolRenderPass: RenderPass, renderPass: VkRenderPass, dynVp: Boolean) {
        if (renderPass === swapChain?.renderPass) {
            if (onScreenPipelineConfigs.add(PipelineAndRenderPass(pipeline, koolRenderPass))) {
                logD { "New on screen pipeline: ${pipeline.name}" }
                createOnScreenPipeline(pipeline, koolRenderPass, renderPass)
            }
        } else {
            val numSamples = if (koolRenderPass is OffscreenRenderPass2d
                && koolRenderPass.colorAttachment is OffscreenRenderPass.RenderBufferColorAttachment
                && koolRenderPass.colorAttachment.isMultiSampled) {
                sys.physicalDevice.msaaSamples
            } else {
                1
            }

            val gp = GraphicsPipeline(sys, koolRenderPass, renderPass, numSamples, dynVp, pipeline, nImages)
            sys.device.addDependingResource(gp)
            val key = PipelineKey(pipeline.pipelineHash, renderPass.vkRenderPass)
            val createdPipeline = createdPipelines.getOrPut(key) { CreatedPipeline(false, key.renderPass) }
            createdPipeline.addRenderPassPipeline(renderPass, gp)
        }
    }

    private fun createOnScreenPipeline(pipeline: Pipeline, koolRenderPass: RenderPass, renderPass: VkRenderPass) {
        val swapChain = this.swapChain ?: return
        val gp = GraphicsPipeline(sys, koolRenderPass, renderPass, sys.physicalDevice.msaaSamples, true, pipeline, swapChain.nImages)
        swapChain.addDependingResource(gp)
        val key = PipelineKey(pipeline.pipelineHash, renderPass.vkRenderPass)
        val createdPipeline = createdPipelines.getOrPut(key) { CreatedPipeline(true, key.renderPass) }
        createdPipeline.addRenderPassPipeline(renderPass, gp)
    }

    fun getPipeline(pipeline: Pipeline, renderPass: Long): GraphicsPipeline {
        return createdPipelines[PipelineKey(pipeline.pipelineHash, renderPass)]?.graphicsPipelines?.get(renderPass)
                ?: throw NoSuchElementException("Unknown pipeline config: ${pipeline.pipelineHash}")
    }

    fun getRenderpassPipelines(pipeline: Pipeline): List<CreatedPipeline> {
        return createdPipelines.keys.filter { it.pipelineHash == pipeline.pipelineHash }.map { createdPipelines[it]!! }
    }

    inner class CreatedPipeline(private val isOnScreen: Boolean, private val renderPass: Long) {
        val graphicsPipelines = mutableMapOf<Long, GraphicsPipeline>()

        fun existsForRenderPass(renderPass: Long) = graphicsPipelines.containsKey(renderPass)

        fun addRenderPassPipeline(renderPass: VkRenderPass, graphicsPipeline: GraphicsPipeline) {
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
                createdPipelines.remove(PipelineKey(pipeline.pipelineHash, renderPass)) != null
                onScreenPipelineConfigs.removeIf { it.pipeline == pipeline }
            }
        }
    }

    private class PipelineAndRenderPass(val pipeline: Pipeline, val koolRenderPass: RenderPass)
}