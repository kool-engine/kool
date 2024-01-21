package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.ComputeRenderPass
import de.fabmax.kool.util.BaseReleasable

class CompiledComputeShader(val pipeline: ComputePipeline, program: GlProgram, backend: RenderBackendGl) :
    CompiledShader(pipeline, program, backend)
{
    private val instances = mutableMapOf<ComputeRenderPass.Task, ComputePassInstance>()

    init {
        pipeline.pipelineBackend = this
    }

    fun bindComputePass(task: ComputeRenderPass.Task): ComputePassInstance? {
        val inst = instances.getOrPut(task) { ComputePassInstance(task) }
        return if (inst.bindInstance()) { inst } else { null }
    }

    override fun release() {
        if (!isReleased) {
            instances.values.forEach { it.release() }
            instances.clear()
            backend.shaderMgr.removeComputeShader(this)
            super.release()
        }
    }

    inner class ComputePassInstance(val task: ComputeRenderPass.Task) : BaseReleasable() {
        init {
            pipelineInfo.numInstances++
        }

        fun bindInstance(): Boolean {
            pipeline.update(task.pass)
            return bindUniforms(task.pass, null)
        }

        override fun release() {
            super.release()
            pipelineInfo.numInstances--
        }
    }
}