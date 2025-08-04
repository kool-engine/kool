package de.fabmax.kool.pipeline

import de.fabmax.kool.util.*

/**
 * Base class for regular (graphics) and compute pipelines. A pipeline includes the shader and additional attributes
 * like the corresponding data layout, etc.
 */
abstract class PipelineBase(val name: String, val bindGroupLayouts: BindGroupLayouts) : BaseReleasable(), DoubleBuffered {

    protected val pipelineHashBuilder = LongHashBuilder()

    /**
     * pipelineHash is used to determine pipeline equality. In contrast to standard java hashCode() a 64-bit hash is
     * used to make collisions less likely.
     */
    abstract val pipelineHash: LongHash

    abstract val shaderCode: ShaderCode

    val pipelineDataLayout = bindGroupLayouts[BindGroupScope.PIPELINE]
    private val defaultPipelineData: BindGroupData = pipelineDataLayout.createData()
    private val pipelineSwapData = mutableMapOf<Any?, BindGroupData>(null to defaultPipelineData)

    var pipelineData = defaultPipelineData
        private set

    var pipelineBackend: PipelineBackend? = null

    init {
        pipelineHashBuilder += bindGroupLayouts.viewScope.hash
        pipelineHashBuilder += bindGroupLayouts.pipelineScope.hash
        pipelineHashBuilder += bindGroupLayouts.meshScope.hash
    }

    fun swapPipelineData(key: Any?) {
        pipelineData = pipelineSwapData.getOrPut(key) { pipelineData.copy() }
    }

    override fun captureBuffer() {
        pipelineData.captureBuffer()
    }

    override fun release() {
        super.release()
        if (pipelineBackend?.isReleased == false) {
            pipelineBackend?.release()
        }
        pipelineSwapData.values.forEach { it.release() }
    }

    inline fun <reified T: BindingLayout> findBindGroupItem(predicate: (T) -> Boolean): Pair<BindGroupLayout, T>? {
        for (group in bindGroupLayouts.asList) {
            group.bindings.filterIsInstance<T>().find(predicate)?.let {
                return group to it
            }
        }
        return null
    }

    fun findBindGroupItemByName(name: String): Pair<BindGroupLayout, BindingLayout>? {
        val group = bindGroupLayouts.asList.find { grp -> grp.bindings.any { it.name == name } }
        return group?.let { grp -> grp to grp.bindings.first { it.name == name } }
    }

    inline fun <reified T: BindingLayout> getBindGroupItem(predicate: (T) -> Boolean): Pair<BindGroupLayout, T> {
        return requireNotNull(findBindGroupItem(predicate)) { "Bind group item with type ${T::class} not found" }
    }

    fun getBindGroupItemByName(name: String): Pair<BindGroupLayout, BindingLayout> {
        return requireNotNull(findBindGroupItemByName(name)) { "Bind group item $name not found" }
    }
}

interface PipelineBackend : Releasable {
    fun removeUser(user: Any)
}

class MultiPipelineBindGroupData(val scope: BindGroupScope) : BaseReleasable(), DoubleBuffered {
    @PublishedApi
    internal val bindGroupData = mutableMapOf<LongHash, UpdateAwareBindGroupData>()

    fun getPipelineData(pipeline: PipelineBase): BindGroupData {
        val layout = pipeline.bindGroupLayouts[scope]
        val data = bindGroupData.getOrPut(layout.hash) { UpdateAwareBindGroupData(layout.createData()) }
        return data.data
    }

    inline fun updatePipelineData(pipeline: PipelineBase, binding: Int, block: (BindGroupData) -> Unit) {
        val layout = pipeline.bindGroupLayouts[scope]
        val data = bindGroupData.getOrPut(layout.hash) { UpdateAwareBindGroupData(layout.createData()) }
        if (data.markBindingUpdate(binding)) {
            block(data.data)
        }
    }

    fun discardPipelineData(pipeline: PipelineBase) {
        val layout = pipeline.bindGroupLayouts[scope]
        bindGroupData.remove(layout.hash)?.data?.release()
    }

    override fun captureBuffer() {
        // fixme don't forEach
        bindGroupData.values.forEach {
            it.data.captureBuffer()
        }
    }

    override fun release() {
        super.release()
        bindGroupData.values.forEach { it.data.release() }
    }

    @PublishedApi
    internal class UpdateAwareBindGroupData(val data: BindGroupData) {
        val updateFrames = IntArray(data.size)

        fun markBindingUpdate(binding: Int): Boolean {
            val frame = Time.frameCount
            val lastUpdate = updateFrames[binding]
            updateFrames[binding] = frame
            return lastUpdate != frame
        }
    }
}