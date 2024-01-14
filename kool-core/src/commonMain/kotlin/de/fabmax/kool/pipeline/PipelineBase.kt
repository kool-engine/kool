package de.fabmax.kool.pipeline

import de.fabmax.kool.util.LongHash

/**
 * Base class for regular (graphics) and compute pipelines. A pipeline includes the shader and additional attributes
 * like the corresponding data layout, etc.
 */
abstract class PipelineBase(val name: String, val bindGroupLayouts: List<BindGroupLayout>) {

    /**
     * pipelineHash is used to determine pipeline equality. In contrast to standard java hashCode() a 64-bit hash is
     * used to make collisions less likely. For fast equality checks the equals() method only uses this value to
     * determine equality.
     */
    protected val hash = LongHash()
    val pipelineHash: Long
        get() = hash.hash
    val pipelineInstanceId = instanceId++

    @Deprecated("Use bindGroupLayouts instead", ReplaceWith("bindGroupLayouts[0]"))
    val bindGroupLayout: BindGroupLayout get() = bindGroupLayouts[0]

    private val _bindGroupData = mutableListOf<BindGroupData>()
    val bindGroupData: List<BindGroupData> get() = _bindGroupData

    init {
        bindGroupLayouts.forEach {
            hash += it.hash
            _bindGroupData += it.createData()
        }
    }

    fun setBindGroupData(index: Int, data: BindGroupData) {
        check(data.layout.hash == bindGroupLayouts[index].hash) {
            "Given BindGroupData does not match the corresponding layout"
        }
        _bindGroupData[index] = data
    }

    fun findBindGroupItemByName(name: String): BindingLayout? {
        return bindGroupLayouts.firstNotNullOfOrNull { grp -> grp.bindings.find { it.name == name } }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PipelineBase) return false
        return pipelineHash == other.pipelineHash
    }

    override fun hashCode(): Int {
        return pipelineHash.hashCode()
    }

    companion object {
        private var instanceId = 1L
    }
}