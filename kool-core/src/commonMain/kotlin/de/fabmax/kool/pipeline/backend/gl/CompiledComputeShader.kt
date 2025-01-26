package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.PipelineBackend

class CompiledComputeShader(val pipeline: ComputePipeline, program: GlProgram, backend: RenderBackendGl) :
    CompiledShader(pipeline, program, backend),
    PipelineBackend
{
    private val users = mutableSetOf<ComputePass.Task>()

    fun bindComputePass(task: ComputePass.Task): Boolean {
        users += task
        pipeline.update(task.pass)
        return bindUniforms(task.pass, null, null)
    }

    override fun removeUser(user: Any) {
        (user as? ComputePass.Task)?.let { users.remove(it) }
        if (users.isEmpty()) {
            release()
        }
    }

    override fun release() {
        if (!isReleased) {
            backend.shaderMgr.removeComputeShader(this)
            super.release()
        }
    }
}