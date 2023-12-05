package de.fabmax.kool.pipeline

fun ComputeRenderPass(computeShader: ComputeShader, width: Int, height: Int = 1, depth: Int = 1): ComputeRenderPass {
    val pass = ComputeRenderPass(computeShader.name, width, height, depth)
    pass.addTask(computeShader)
    return pass
}

class ComputeRenderPass(name: String, width: Int, height: Int = 1, depth: Int = 1) :
    OffscreenRenderPass(renderPassConfig {
        this.name = name
        size(width, height, depth)

        // color and depth target don't apply to ComputeRenderPass and are ignored...
        colorTargetNone()
        depthTargetRenderBuffer()
    })
{
    override val views: List<View> = emptyList()
    override val isReverseDepth: Boolean = false

    private var isTasksDirty = false
    private val _tasks = mutableListOf<Task>()
    val tasks: List<Task> get() {
        if (isTasksDirty) {
            _tasks
                .filter { it.isCreated == false }
                .forEach { it.create(it.shader.getOrCreatePipeline(this)) }
            isTasksDirty = false
        }
        return _tasks
    }

    fun addTask(computeShader: ComputeShader): Task {
        val task = Task(computeShader)
        _tasks += task
        isTasksDirty = true
        return task
    }

    class Task(val shader: ComputeShader) {
        var isEnabled = true
        var isCreated = false
            internal set
        lateinit var pipeline: ComputePipeline
            private set

        fun create(pipeline: ComputePipeline) {
            this.pipeline = pipeline
        }
    }
}