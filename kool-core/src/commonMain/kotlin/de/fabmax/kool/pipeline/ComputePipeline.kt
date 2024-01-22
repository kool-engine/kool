package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.util.BufferedList

/**
 * Compute pipeline: Compute shader + data layout.
 */
class ComputePipeline(
    name: String,
    bindGroupLayouts: BindGroupLayouts,
    val workGroupSize: Vec3i,
    shaderCodeGenerator: (ComputePipeline) -> ComputeShaderCode
) : PipelineBase(name, bindGroupLayouts) {

    override val shaderCode: ComputeShaderCode = shaderCodeGenerator(this)
    val onUpdate: BufferedList<(ComputeRenderPass) -> Unit> = BufferedList()

    init {
        hash += shaderCode.hash
    }

    fun update(computePass: ComputeRenderPass) {
        onUpdate.update()
        for (i in onUpdate.indices) {
            onUpdate[i].invoke(computePass)
        }
    }

    fun onUpdate(block: (ComputeRenderPass) -> Unit) {
        onUpdate += block
    }

    fun removeUser(task: ComputeRenderPass.Task) {
        pipelineBackend?.removeUser(task)
    }

    override fun release() {
        super.release()
        pipelineBackend?.release()
    }
}