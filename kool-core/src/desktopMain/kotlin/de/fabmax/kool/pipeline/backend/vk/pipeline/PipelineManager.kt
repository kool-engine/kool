package de.fabmax.kool.pipeline.backend.vk.pipeline

import de.fabmax.kool.pipeline.DrawPipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.vk.SwapChain
import de.fabmax.kool.pipeline.backend.vk.VkRenderPass
import de.fabmax.kool.pipeline.backend.vk.VkSystem
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.logD

class PipelineManager(val sys: VkSystem) {

    private val onScreenPipelineConfigs = mutableSetOf<PipelineAndRenderPass>()

    private var swapChain: SwapChain? = null
    private val createdPipelines = mutableMapOf<LongHash, CreatedPipeline>()

    fun onSwapchainDestroyed() {
        swapChain = null
        createdPipelines.clear()
    }

    fun onSwapchainCreated(swapChain: SwapChain) {
        this.swapChain = swapChain
        onScreenPipelineConfigs.forEach { createOnScreenPipeline(it.pipeline, it.koolRenderPass, swapChain.renderPass) }
    }

    fun hasPipeline(pipeline: DrawPipeline, renderPass: Long): Boolean =
            createdPipelines[pipeline.pipelineHash]?.existsForRenderPass(renderPass) ?: false

    fun addPipelineConfig(pipeline: DrawPipeline, nImages: Int, koolRenderPass: RenderPass, renderPass: VkRenderPass, dynVp: Boolean) {
        if (renderPass === swapChain?.renderPass) {
            if (onScreenPipelineConfigs.add(PipelineAndRenderPass(pipeline, koolRenderPass))) {
                logD { "New on screen pipeline: ${pipeline.name}" }
                createOnScreenPipeline(pipeline, koolRenderPass, renderPass)
            }
        } else {
            val numSamples = 1
//                if (koolRenderPass is OffscreenRenderPass2d
//                    && koolRenderPass.colorAttachments is OffscreenRenderPass.RenderBufferColorAttachment
//                    && koolRenderPass.colorAttachments.isMultiSampled
//                ) {
//                    sys.physicalDevice.msaaSamples
//                } else {
//                    1
//                }

            val gp = GraphicsPipeline(sys, koolRenderPass, renderPass, numSamples, dynVp, pipeline, nImages)
            sys.logicalDevice.addDependingResource(gp)
            val createdPipeline = createdPipelines.getOrPut(pipeline.pipelineHash) { CreatedPipeline(false) }
            createdPipeline.addRenderPassPipeline(renderPass, gp)
        }
    }

    private fun createOnScreenPipeline(pipeline: DrawPipeline, koolRenderPass: RenderPass, renderPass: VkRenderPass) {
        val swapChain = this.swapChain ?: return
        val gp = GraphicsPipeline(sys, koolRenderPass, renderPass, sys.physicalDevice.msaaSamples, true, pipeline, swapChain.nImages)
        swapChain.addDependingResource(gp)
        val createdPipeline = createdPipelines.getOrPut(pipeline.pipelineHash) { CreatedPipeline(true) }
        createdPipeline.addRenderPassPipeline(renderPass, gp)
    }

    fun getPipeline(pipeline: DrawPipeline, renderPass: Long): GraphicsPipeline {
        return createdPipelines[pipeline.pipelineHash]?.graphicsPipelines?.get(renderPass)
                ?: throw NoSuchElementException("Unknown pipeline config: ${pipeline.pipelineHash}")
    }

    fun getPipeline(pipeline: DrawPipeline): CreatedPipeline? {
        return createdPipelines[pipeline.pipelineHash]
    }

    inner class CreatedPipeline(private val isOnScreen: Boolean) {
        val graphicsPipelines = mutableMapOf<Long, GraphicsPipeline>()

        fun existsForRenderPass(renderPass: Long) = graphicsPipelines.containsKey(renderPass)

        fun addRenderPassPipeline(renderPass: VkRenderPass, graphicsPipeline: GraphicsPipeline) {
            graphicsPipelines[renderPass.vkRenderPass] = graphicsPipeline
        }

        fun freeDescriptorSetInstance(pipeline: DrawPipeline) {
            // find GraphicsPipeline containing pipelineInstance
            val iterator = graphicsPipelines.values.iterator()
            for (gp in iterator) {
                if (gp.freeDescriptorSetInstance(pipeline) && gp.isEmpty()) {
                    // all descriptor set instances of this particular pipeline are freed, destroy it..
                    if (isOnScreen) {
                        swapChain?.removeDependingResource(gp)
                    } else {
                        sys.logicalDevice.removeDependingResource(gp)
                    }
                    gp.destroy()
                    iterator.remove()
                }
            }
            if (graphicsPipelines.isEmpty()) {
                // all pipelines destroyed, remove CreatedPipeline instance
                createdPipelines.remove(pipeline.pipelineHash) != null
                onScreenPipelineConfigs.removeIf { it.pipeline == pipeline }
            }
        }
    }

    private class PipelineAndRenderPass(val pipeline: DrawPipeline, val koolRenderPass: RenderPass)
}