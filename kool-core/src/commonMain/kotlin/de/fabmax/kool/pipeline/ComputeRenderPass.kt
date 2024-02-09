package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec3i

fun ComputeRenderPass(computeShader: ComputeShader, width: Int, height: Int = 1, depth: Int = 1): ComputeRenderPass {
    val pass = ComputeRenderPass(computeShader.name, width, height, depth)
    pass.addTask(computeShader)
    return pass
}

class ComputeRenderPass(name: String, width: Int, height: Int = 1, depth: Int = 1) :
    OffscreenRenderPass(renderPassAttachmentConfig, Vec3i(width, height, depth), name)
{
    override val views: List<View> = emptyList()

    private var isTasksDirty = false
    private val _tasks = mutableListOf<Task>()
    val tasks: List<Task> get() {
        if (isTasksDirty) {
            _tasks
                .filter { !it.isCreated }
                .forEach { it.create(it.shader.getOrCreatePipeline(this)) }
            isTasksDirty = false
        }
        return _tasks
    }

    fun addTask(computeShader: ComputeShader): Task {
        val task = Task(this, computeShader)
        _tasks += task
        isTasksDirty = true
        return task
    }

    override fun release() {
        super.release()
        _tasks
            .filter { it.isCreated }
            .map { it.pipeline }
            .distinct()
            .filter { !it.isReleased }
            .forEach { it.release() }
    }

    companion object {
        private val renderPassAttachmentConfig = AttachmentConfig(ColorAttachmentNone, DepthAttachmentNone)
    }

    class Task(val pass: ComputeRenderPass, val shader: ComputeShader) {
        var isEnabled = true
        var isCreated = false
            private set
        lateinit var pipeline: ComputePipeline
            private set

        private val beforeDispatch = mutableListOf<() -> Unit>()
        private val afterDispatch = mutableListOf<() -> Unit>()

        fun create(pipeline: ComputePipeline) {
            this.pipeline = pipeline
            isCreated = true
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