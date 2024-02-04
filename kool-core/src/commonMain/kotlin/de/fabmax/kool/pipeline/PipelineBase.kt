package de.fabmax.kool.pipeline

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.Time

/**
 * Base class for regular (graphics) and compute pipelines. A pipeline includes the shader and additional attributes
 * like the corresponding data layout, etc.
 */
abstract class PipelineBase(val name: String, val bindGroupLayouts: BindGroupLayouts) : BaseReleasable() {

    /**
     * pipelineHash is used to determine pipeline equality. In contrast to standard java hashCode() a 64-bit hash is
     * used to make collisions less likely. For fast equality checks the equals() method only uses this value to
     * determine equality.
     */
    protected val hash = LongHash()
    val pipelineHash: Long
        get() = hash.hash

    abstract val shaderCode: ShaderCode

    val pipelineDataLayout = bindGroupLayouts[BindGroupScope.PIPELINE]
    var pipelineData = pipelineDataLayout.createData()
        set(value) {
            check(value.layout == pipelineDataLayout) {
                "Given BindGroupData does not match this pipeline's data bind group layout"
            }
            field = value
        }

    internal var pipelineBackend: PipelineBackend? = null

    init {
        hash += bindGroupLayouts.viewScope.hash
        hash += bindGroupLayouts.pipelineScope.hash
        hash += bindGroupLayouts.meshScope.hash
    }

    override fun release() {
        super.release()
        if (pipelineBackend?.isReleased == false) {
            pipelineBackend?.release()
        }
        pipelineData.release()
    }

    inline fun <reified T: BindingLayout> findBindingLayout(predicate: (T) -> Boolean): Pair<BindGroupLayout, T>? {
        for (group in bindGroupLayouts.asList) {
            group.bindings.filterIsInstance<T>().find(predicate)?.let {
                return group to it
            }
        }
        return null
    }

    fun findBindGroupItemByName(name: String): BindingLayout? {
        return bindGroupLayouts.asList.firstNotNullOfOrNull { grp -> grp.bindings.find { it.name == name } }
    }
}

interface PipelineBackend : Releasable {
    fun removeUser(user: Any)
}

class PipelineData(val scope: BindGroupScope) : BaseReleasable() {
    private val bindGroupData = mutableMapOf<Long, UpdateAwareBindGroupData>()

    fun getPipelineData(pipeline: PipelineBase): BindGroupData {
        val layout = pipeline.bindGroupLayouts[scope]
        val data = bindGroupData.getOrPut(layout.hash) { UpdateAwareBindGroupData(layout.createData()) }
        return data.data
    }

    fun getPipelineDataUpdating(pipeline: PipelineBase, binding: Int): BindGroupData? {
        val layout = pipeline.bindGroupLayouts[scope]
        val data = bindGroupData.getOrPut(layout.hash) { UpdateAwareBindGroupData(layout.createData()) }
        return if (data.markBindingUpdate(binding)) data.data else null
    }

    fun setPipelineData(data: BindGroupData, pipeline: PipelineBase) {
        val layout = pipeline.bindGroupLayouts[scope]
        check(layout == data.layout) {
            "Given BindGroupData does not match this pipeline's $scope data bind group layout"
        }
        bindGroupData[layout.hash] = UpdateAwareBindGroupData(layout.createData())
    }

    fun discardPipelineData(pipeline: PipelineBase) {
        val layout = pipeline.bindGroupLayouts[scope]
        bindGroupData.remove(layout.hash)?.data?.release()
    }

    override fun release() {
        super.release()
        bindGroupData.values.forEach { it.data.release() }
    }

    private class UpdateAwareBindGroupData(val data: BindGroupData) {
        val updateFrames = IntArray(data.bindings.size)

        fun markBindingUpdate(binding: Int): Boolean {
            val frame = Time.frameCount
            val lastUpdate = updateFrames[binding]
            updateFrames[binding] = frame
            return lastUpdate != frame
        }
    }
}