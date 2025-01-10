package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.DrawPipeline
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.util.*

class PipelineManager(val backend: RenderBackendVk) : BaseReleasable() {

    private val vertexShaderModules = mutableMapOf<LongHash, UsedShaderModule>()
    private val fragmentShaderModules = mutableMapOf<LongHash, UsedShaderModule>()
    private val computeShaderModules = mutableMapOf<LongHash, UsedShaderModule>()

    private val drawPipelines = mutableSetOf<VkDrawPipeline>()
    private val computePipelines = mutableSetOf<VkComputePipeline>()

    init {
        releaseWith(backend.device)
    }

    fun bindDrawPipeline(cmd: DrawCommand, passEncoderState: RenderPassEncoderState<*>): Boolean {
        val vkPipeline = cmd.pipeline.getVkPipeline()
        cmd.pipeline.update(cmd)
        return vkPipeline.bind(cmd, passEncoderState)
    }

    private fun DrawPipeline.getVkPipeline(): VkDrawPipeline {
        (pipelineBackend as VkDrawPipeline?)?.let { return it }

        val vertexShader = getOrCreateVertexShaderModule(this)
        val fragmentShader = getOrCreateFragmentShaderModule(this)
        val pipeline = VkDrawPipeline(this, vertexShader, fragmentShader, backend)
        pipelineBackend = pipeline
        drawPipelines += pipeline
        return pipeline
    }

    private fun getOrCreateVertexShaderModule(pipeline: DrawPipeline): VkShaderModule {
        val shaderCode = pipeline.shaderCode as ShaderCodeVk
        val stage = checkNotNull(shaderCode.vertexStage) { "Draw pipeline has no vertex stage!" }
        return getOrCreateShaderModule(pipeline, stage, vertexShaderModules)
    }

    private fun getOrCreateFragmentShaderModule(pipeline: DrawPipeline): VkShaderModule {
        val shaderCode = pipeline.shaderCode as ShaderCodeVk
        val stage = checkNotNull(shaderCode.fragmentStage) { "Draw pipeline has no fragment stage!" }
        return getOrCreateShaderModule(pipeline, stage, fragmentShaderModules)
    }

    private fun getOrCreateComputeShaderModule(pipeline: ComputePipeline): VkShaderModule {
        val shaderCode = pipeline.shaderCode as ShaderCodeVk
        val stage = checkNotNull(shaderCode.computeStage) { "Compute pipeline has no compute stage!" }
        return getOrCreateShaderModule(pipeline, stage, fragmentShaderModules)
    }

    private fun getOrCreateShaderModule(
        pipeline: PipelineBase,
        stage: ShaderStage,
        cache: MutableMap<LongHash, UsedShaderModule>
    ): VkShaderModule {
        val usedModule = cache.getOrPut(stage.hash) {
            val buf = (stage.code.toBuffer() as Uint8BufferImpl).getRawBuffer()
            val shaderModule = backend.device.createShaderModule { pCode(buf) }
            UsedShaderModule(shaderModule)
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    internal fun removeDrawPipeline(pipeline: VkDrawPipeline) {
        pipeline.drawPipeline.pipelineBackend = null
        val shaderCode = pipeline.drawPipeline.shaderCode as ShaderCodeVk

        shaderCode.vertexStage?.let { vertexStage ->
            vertexShaderModules[vertexStage.hash]?.let { usedModule ->
                usedModule.users -= pipeline.drawPipeline
                if (usedModule.users.isEmpty()) {
                    vertexShaderModules.remove(vertexStage.hash)
                    backend.device.destroyShaderModule(usedModule.shaderModule)
                }
            }
        }
        shaderCode.fragmentStage?.let { fragmentStage ->
            fragmentShaderModules[fragmentStage.hash]?.let { usedModule ->
                usedModule.users -= pipeline.drawPipeline
                if (usedModule.users.isEmpty()) {
                    fragmentShaderModules.remove(fragmentStage.hash)
                    backend.device.destroyShaderModule(usedModule.shaderModule)
                }
            }
        }
    }

    internal fun removeComputePipeline(pipeline: VkComputePipeline) {
        pipeline.computePipeline.pipelineBackend = null
        val shaderCode = pipeline.computePipeline.shaderCode as ShaderCodeVk

        shaderCode.computeStage?.let { computeStage ->
            computeShaderModules[computeStage.hash]?.let { usedModule ->
                usedModule.users -= pipeline.computePipeline
                if (usedModule.users.isEmpty()) {
                    computeShaderModules.remove(computeStage.hash)
                    backend.device.destroyShaderModule(usedModule.shaderModule)
                }
            }
        }
    }

    override fun release() {
        super.release()
        drawPipelines.toList().forEach { it.release() }
        computePipelines.toList().forEach { it.release() }
    }

    private class UsedShaderModule(val shaderModule: VkShaderModule) {
        val users = mutableSetOf<PipelineBase>()
    }





//    private val onScreenPipelineConfigs = mutableSetOf<PipelineAndRenderPass>()
//
//    private var swapChain: Swapchain? = null
//    private val createdPipelines = mutableMapOf<LongHash, CreatedPipeline>()
//
//    fun onSwapchainDestroyed() {
//        swapChain = null
//        createdPipelines.clear()
//    }
//
//    fun onSwapchainCreated(swapChain: Swapchain) {
//        this.swapChain = swapChain
//        onScreenPipelineConfigs.forEach { createOnScreenPipeline(it.pipeline, it.koolRenderPass, sys.backend.screenRenderPass) }
//    }
//
//    fun hasPipeline(pipeline: DrawPipeline, renderPass: Long): Boolean =
//            createdPipelines[pipeline.pipelineHash]?.existsForRenderPass(renderPass) ?: false
//
//    fun addPipelineConfig(pipeline: DrawPipeline, nImages: Int, koolRenderPass: RenderPass, renderPass: RenderPassVk<*>, dynVp: Boolean) {
//        if (renderPass === sys.backend.screenRenderPass) {
//            if (onScreenPipelineConfigs.add(PipelineAndRenderPass(pipeline, koolRenderPass))) {
//                logD { "New on screen pipeline: ${pipeline.name}" }
//                createOnScreenPipeline(pipeline, koolRenderPass, renderPass)
//            }
//        } else {
//            val numSamples = 1
////                if (koolRenderPass is OffscreenRenderPass2d
////                    && koolRenderPass.colorAttachments is OffscreenRenderPass.RenderBufferColorAttachment
////                    && koolRenderPass.colorAttachments.isMultiSampled
////                ) {
////                    sys.physicalDevice.msaaSamples
////                } else {
////                    1
////                }
//
//            val gp = GraphicsPipeline(sys, koolRenderPass, renderPass, numSamples, pipeline, nImages)
//            sys.device.addDependingResource(gp)
//            val createdPipeline = createdPipelines.getOrPut(pipeline.pipelineHash) { CreatedPipeline(false) }
//            createdPipeline.addRenderPassPipeline(renderPass, gp)
//        }
//    }
//
//    private fun createOnScreenPipeline(pipeline: DrawPipeline, koolRenderPass: RenderPass, renderPass: RenderPassVk<*>) {
//        val swapChain = this.swapChain ?: return
//        val gp = GraphicsPipeline(sys, koolRenderPass, renderPass, sys.physicalDevice.msaaSamples, pipeline, swapChain.nImages)
//        swapChain.addDependingResource(gp)
//        val createdPipeline = createdPipelines.getOrPut(pipeline.pipelineHash) { CreatedPipeline(true) }
//        createdPipeline.addRenderPassPipeline(renderPass, gp)
//    }
//
//    fun getPipeline(pipeline: DrawPipeline, renderPass: Long): GraphicsPipeline {
//        return createdPipelines[pipeline.pipelineHash]?.graphicsPipelines?.get(renderPass)
//                ?: throw NoSuchElementException("Unknown pipeline config: ${pipeline.pipelineHash}")
//    }
//
//    fun getPipeline(pipeline: DrawPipeline): CreatedPipeline? {
//        return createdPipelines[pipeline.pipelineHash]
//    }
//
//    inner class CreatedPipeline(private val isOnScreen: Boolean) {
//        val graphicsPipelines = mutableMapOf<Long, GraphicsPipeline>()
//
//        fun existsForRenderPass(renderPass: Long) = graphicsPipelines.containsKey(renderPass)
//
//        fun addRenderPassPipeline(renderPass: RenderPassVk<*>, graphicsPipeline: GraphicsPipeline) {
//            graphicsPipelines[renderPass.vkRenderPass.handle] = graphicsPipeline
//        }
//
//        fun freeDescriptorSetInstance(pipeline: DrawPipeline) {
//            // find GraphicsPipeline containing pipelineInstance
//            val iterator = graphicsPipelines.values.iterator()
//            for (gp in iterator) {
//                if (gp.freeDescriptorSetInstance(pipeline) && gp.isEmpty()) {
//                    // all descriptor set instances of this particular pipeline are freed, destroy it..
//                    if (isOnScreen) {
//                        swapChain?.removeDependingResource(gp)
//                    } else {
//                        sys.device.removeDependingResource(gp)
//                    }
//                    gp.destroy()
//                    iterator.remove()
//                }
//            }
//            if (graphicsPipelines.isEmpty()) {
//                // all pipelines destroyed, remove CreatedPipeline instance
//                createdPipelines.remove(pipeline.pipelineHash) != null
//                onScreenPipelineConfigs.removeIf { it.pipeline == pipeline }
//            }
//        }
//    }
//
//    private class PipelineAndRenderPass(val pipeline: DrawPipeline, val koolRenderPass: RenderPass)
}