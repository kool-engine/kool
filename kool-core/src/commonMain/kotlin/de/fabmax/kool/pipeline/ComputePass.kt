package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.util.Releasable
import kotlin.math.ceil

fun ComputePass(computeShader: ComputeShader, numInvocationsX: Int, numInvocationsY: Int = 1, numInvocationsZ: Int = 1): ComputePass {
    val pass = ComputePass(computeShader.name)
    val task = pass.addTask(computeShader, Vec3i.ZERO)
    task.setNumGroupsByInvocations(numInvocationsX, numInvocationsY, numInvocationsZ)
    return pass
}

class ComputePass(name: String) : OffscreenRenderPass(renderPassAttachmentConfig, Vec3i.ZERO, name) {
    override val views: List<View> = emptyList()

    private val _tasks = mutableListOf<Task>()
    val tasks: List<Task> get() = _tasks

    internal val impl = KoolSystem.requireContext().backend.createComputePass(this)

    fun addTask(computeShader: ComputeShader, numGroups: Vec3i): Task {
        val task = Task(computeShader, numGroups)
        _tasks += task
        return task
    }

    override fun release() {
        super.release()
        _tasks
            .map { it.pipeline }
            .distinct()
            .filter { !it.isReleased }
            .forEach { it.release() }
    }

    companion object {
        private val renderPassAttachmentConfig = AttachmentConfig(ColorAttachmentNone, DepthAttachmentNone)
    }

    inner class Task(val shader: ComputeShader, numGroups: Vec3i) {
        val pass: ComputePass get() = this@ComputePass
        val numGroups = MutableVec3i(numGroups)
        var isEnabled = true
        val pipeline: ComputePipeline = shader.getOrCreatePipeline(this@ComputePass)

        private val beforeDispatch = mutableListOf<() -> Unit>()
        private val afterDispatch = mutableListOf<() -> Unit>()

        fun setNumGroupsByInvocations(numInvocationsX: Int, numInvocationsY: Int = 1, numInvocationsZ: Int = 1) {
            val numGroupsX = ceil(numInvocationsX.toFloat() / pipeline.workGroupSize.x).toInt()
            val numGroupsY = ceil(numInvocationsY.toFloat() / pipeline.workGroupSize.y).toInt()
            val numGroupsZ = ceil(numInvocationsZ.toFloat() / pipeline.workGroupSize.z).toInt()
            numGroups.set(numGroupsX, numGroupsY, numGroupsZ)
        }

        fun onBeforeDispatch(block: () -> Unit) {
            beforeDispatch += block
        }

        fun onAfterDispatch(block: () -> Unit) {
            afterDispatch += block
        }

        internal fun beforeDispatch() {
            for (i in beforeDispatch.indices) {
                beforeDispatch[i]()
            }
        }

        internal fun afterDispatch() {
            for (i in afterDispatch.indices) {
                afterDispatch[i]()
            }
        }
    }
}

interface ComputePassImpl : Releasable
