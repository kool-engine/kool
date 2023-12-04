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

    private val tasks = mutableListOf<Task>()
    private var isTasksDirty = false

    fun getTasks(): List<Task> {
        if (isTasksDirty) {
            tasks
                .filter { it.isCreated == false }
                .forEach { it.create(it.shader.getOrCreatePipeline(this)) }
            isTasksDirty = false
        }
        return tasks
    }

    fun addTask(computeShader: ComputeShader) {
        tasks += Task(computeShader)
        isTasksDirty = true
    }

    class Task(val shader: ComputeShader) {
        var isCreated = false
        lateinit var pipeline: ComputePipeline
            private set

        fun create(pipeline: ComputePipeline) {
            this.pipeline = pipeline
        }
    }
}