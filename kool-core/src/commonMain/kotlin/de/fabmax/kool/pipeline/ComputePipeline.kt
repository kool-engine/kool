package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.LongHash

/**
 * Compute pipeline: Compute shader + data layout.
 */
class ComputePipeline(
    name: String,
    bindGroupLayouts: BindGroupLayouts,
    val workGroupSize: Vec3i,
    shaderCodeGenerator: (ComputePipeline) -> ComputeShaderCode
) : PipelineBase(name, bindGroupLayouts) {
    override val pipelineHash: LongHash

    override val shaderCode: ComputeShaderCode = shaderCodeGenerator(this)
    val onUpdate: BufferedList<(ComputePass) -> Unit> = BufferedList()

    init {
        pipelineHashBuilder += shaderCode.hash
        pipelineHash = pipelineHashBuilder.build()
    }

    fun update(computePass: ComputePass) {
        onUpdate.update()
        for (i in onUpdate.indices) {
            onUpdate[i].invoke(computePass)
        }
    }

    fun onUpdate(block: (ComputePass) -> Unit) {
        onUpdate += block
    }

    fun removeUser(task: ComputePass.Task) {
        pipelineBackend?.removeUser(task)
    }
}