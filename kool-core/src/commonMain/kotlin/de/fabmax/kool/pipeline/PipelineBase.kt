package de.fabmax.kool.pipeline

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logE

/**
 * Base class for regular (graphics) and compute pipelines. A pipeline includes the shader and additional attributes
 * like the corresponding data layout, etc.
 */
abstract class PipelineBase(val name: String, val bindGroupLayouts: List<BindGroupLayout>) : BaseReleasable() {

    /**
     * pipelineHash is used to determine pipeline equality. In contrast to standard java hashCode() a 64-bit hash is
     * used to make collisions less likely. For fast equality checks the equals() method only uses this value to
     * determine equality.
     */
    protected val hash = LongHash()
    val pipelineHash: Long
        get() = hash.hash

    abstract val shaderCode: ShaderCode

    @Deprecated("Use bindGroupLayouts instead", ReplaceWith("bindGroupLayouts[0]"))
    val bindGroupLayout: BindGroupLayout get() {
        if (bindGroupLayouts.size > 1) {
            logE { "Pipeline has multiple bind groups, access via deprecated bindGroupLayout will likely not work" }
        }
        return bindGroupLayouts[0]
    }

    val pipelineDataLayout = bindGroupLayouts.find { it.scope == BindGroupScope.PIPELINE } ?: emptyPipelineLayout
    var pipelineData = pipelineDataLayout.createData()
        set(value) {
            check(value.layout == pipelineDataLayout) {
                "Given BindGroupData does not match this pipeline's data bind group layout"
            }
            field = value
        }

    init {
        bindGroupLayouts.forEach { hash += it.hash }
    }

    inline fun <reified T: BindingLayout> findBindingLayout(predicate: (T) -> Boolean): Pair<BindGroupLayout, T>? {
        for (group in bindGroupLayouts) {
            group.bindings.filterIsInstance<T>().find(predicate)?.let {
                return group to it
            }
        }
        return null
    }

    fun findBindGroupItemByName(name: String): BindingLayout? {
        return bindGroupLayouts.firstNotNullOfOrNull { grp -> grp.bindings.find { it.name == name } }
    }

    companion object {
        private val emptyPipelineLayout = BindGroupLayout(BindGroupScope.PIPELINE, emptyList())
    }
}

class PipelineData(val scope: BindGroupScope) {
    private val bindGroupData = mutableMapOf<Long, UpdateAwareBindGroupData>()

    fun getPipelineData(pipeline: PipelineBase): BindGroupData {
        val layout = pipeline.bindGroupLayouts[scope.group]
        val data = bindGroupData.getOrPut(layout.hash) { UpdateAwareBindGroupData(layout.createData()) }
        return data.data
    }

    fun getPipelineDataUpdating(pipeline: PipelineBase, binding: Int): BindGroupData? {
        val layout = pipeline.bindGroupLayouts[scope.group]
        val data = bindGroupData.getOrPut(layout.hash) { UpdateAwareBindGroupData(layout.createData()) }
        return if (data.markBindingUpdate(binding)) data.data else null
    }

    fun setPipelineData(data: BindGroupData, pipeline: PipelineBase) {
        val layout = pipeline.bindGroupLayouts[scope.group]
        check(layout == data.layout) {
            "Given BindGroupData does not match this pipeline's $scope data bind group layout"
        }
        bindGroupData[layout.hash] = UpdateAwareBindGroupData(layout.createData())
    }

    fun discardPipelineData(pipeline: PipelineBase) {
        val layout = pipeline.bindGroupLayouts[scope.group]
        bindGroupData.remove(layout.hash)
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