package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePassImpl
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.releaseWith

class ComputePassGl(
    val parent: ComputePass,
    private val backend: RenderBackendGl
) : BaseReleasable(), ComputePassImpl {

    private val gl: GlApi
        get() = backend.gl

    private val timeQuery: TimeQuery by lazy { TimeQuery(gl).also { it.releaseWith(this) } }

    fun dispatch() {
        val q = if (parent.isProfileTimes) timeQuery else null
        q?.let {
            if (it.isAvailable) {
                parent.tGpu = it.getQueryResult()
            }
            it.begin()
        }

        val tasks = parent.tasks
        for (i in tasks.indices) {
            val task = tasks[i]
            if (task.isEnabled) {
                val pipeline = tasks[i].pipeline
                task.beforeDispatch()

                if (backend.shaderMgr.bindComputeShader(pipeline, task)) {
                    val maxCnt = gl.capabilities.maxWorkGroupCount
                    if (task.numGroups.x > maxCnt.x || task.numGroups.y > maxCnt.y || task.numGroups.z > maxCnt.z) {
                        logE { "Maximum compute shader workgroup count exceeded: max count = $maxCnt, requested count: (${task.numGroups.x}, ${task.numGroups.y}, ${task.numGroups.z})" }
                    }
                    gl.dispatchCompute(task.numGroups.x, task.numGroups.y, task.numGroups.z)
                    gl.memoryBarrier(gl.SHADER_IMAGE_ACCESS_BARRIER_BIT or gl.SHADER_STORAGE_BARRIER_BIT)

                    task.afterDispatch()
                }
            }
        }

        q?.end()
    }
}