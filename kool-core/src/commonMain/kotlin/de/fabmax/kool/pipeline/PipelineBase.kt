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

    abstract val shaderCode: ShaderCode

    init {
        bindGroupLayouts = builder.bindGroupLayouts.mapIndexed { i, b -> b.create(i) }
        bindGroupLayouts.forEach { hash += it.hash }
    }

    fun findBindGroupItemByName(name: String): Pair<BindGroupLayout, Binding>? {
        for (group in bindGroupLayouts) {
            val binding = group.findItemsByName(name)
            if (binding != null) {
                return group to binding
            }
        }
        return null
    }

    abstract class Builder {
        var name = "pipeline"
        val bindGroupLayouts = mutableListOf<BindGroupLayout.Builder>()

        abstract fun create(): PipelineBase
    }

    companion object {
        private var instanceId = 1L
    }
}