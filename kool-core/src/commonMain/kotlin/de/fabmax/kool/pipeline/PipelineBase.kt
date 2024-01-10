package de.fabmax.kool.pipeline

import de.fabmax.kool.util.LongHash

/**
 * Base class for regular (graphics) and compute pipelines. A pipeline includes the shader and additional attributes
 * like the corresponding data layout, etc.
 */
abstract class PipelineBase(builder: Builder) {

    val name = builder.name

    /**
     * pipelineHash is used to determine pipeline equality. In contrast to standard java hashCode() a 64-bit hash is
     * used to make collisions less likely. For fast equality checks the equals() method only uses this value to
     * determine equality.
     */
    protected val hash = LongHash()
    val pipelineHash: Long
        get() = hash.hash
    val pipelineInstanceId = instanceId++

    val bindGroupLayouts: List<BindGroupLayout>
    @Deprecated("Use bindGroupLayouts instead", ReplaceWith("bindGroupLayouts[0]"))
    val bindGroupLayout: BindGroupLayout get() = bindGroupLayouts[0]

    private val _bindGroupData = mutableListOf<BindGroupData>()
    val bindGroupData: List<BindGroupData> get() = _bindGroupData

    init {
        val layout = requireNotNull(builder.bindGroupLayout) { "Builder.bindGroupLayout must be set" }
        bindGroupLayouts = listOf(layout.create(0))
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

    fun findBindGroupItemByName(name: String): Binding? {
        return bindGroupLayouts.firstNotNullOfOrNull { it.findBindingByName(name) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PipelineBase) return false
        return pipelineHash == other.pipelineHash
    }

    override fun hashCode(): Int {
        return pipelineHash.hashCode()
    }

    abstract class Builder {
        var name = "pipeline"
        var bindGroupLayout: BindGroupLayout.Builder? = null

        abstract fun create(): PipelineBase
    }

    companion object {
        private var instanceId = 1L
    }
}