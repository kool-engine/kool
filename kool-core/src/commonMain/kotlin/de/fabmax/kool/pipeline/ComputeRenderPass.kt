package de.fabmax.kool.pipeline

class ComputeRenderPass(val computeShader: ComputeShader, width: Int, height: Int = 1, depth: Int = 1) :
    OffscreenRenderPass(renderPassConfig {
        name = computeShader.name
        size(width, height, depth)

        // color and depth target don't apply to ComputeRenderPass and are ignored...
        colorTargetNone()
        depthTargetRenderBuffer()
    })
{
    override val views: List<View> = emptyList()
    override val isReverseDepth: Boolean = false

    private var pipeline: ComputePipeline? = null

    fun getOrCreatePipeline(): ComputePipeline {
        return pipeline ?: computeShader.getOrCreatePipeline(this).also { pipeline = it }
    }
}