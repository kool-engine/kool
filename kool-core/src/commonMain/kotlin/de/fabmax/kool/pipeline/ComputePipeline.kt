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
    internal val users = mutableSetOf<ComputePass.Task>()

    override val shaderCode: ComputeShaderCode = shaderCodeGenerator(this)
    val onUpdatePipelineData: BufferedList<(ComputePass) -> Unit> = BufferedList()

    init {
        pipelineHashBuilder += shaderCode.hash
        pipelineHash = pipelineHashBuilder.build()
    }

    fun updatePipelineData(computePass: ComputePass) {
        onUpdatePipelineData.update()
        for (i in onUpdatePipelineData.indices) {
            onUpdatePipelineData[i].invoke(computePass)
        }
    }

    fun onUpdatePipelineData(block: (ComputePass) -> Unit) {
        onUpdatePipelineData += block
    }

    fun addUser(task: ComputePass.Task) {
        users.add(task)
    }

    fun removeUser(task: ComputePass.Task) {
        users.remove(task)
        if (users.isEmpty()) {
            release()
        }
    }
}