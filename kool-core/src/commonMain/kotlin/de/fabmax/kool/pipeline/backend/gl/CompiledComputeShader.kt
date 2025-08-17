package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.PipelineBackend

class CompiledComputeShader(val pipeline: ComputePipeline, program: GlProgram, backend: RenderBackendGl) :
    CompiledShader(pipeline, program, backend),
    PipelineBackend
{
    fun bindComputePass(task: ComputePass.Task): Boolean {
        return bindUniforms(task.pass, null, null)
    }

    override fun doRelease() {
        backend.shaderMgr.removeComputeShader(this)
        super.doRelease()
    }
}