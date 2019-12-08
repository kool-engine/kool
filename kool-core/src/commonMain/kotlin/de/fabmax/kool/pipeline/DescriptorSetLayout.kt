package de.fabmax.kool.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.copy

class DescriptorSetLayout private constructor(val descriptors: List<Descriptor>) {

    val longHash: Long

    init {
        var hash = 0L
        descriptors.forEach {
            hash = (hash * 71023L) + it.longHash
        }
        longHash = hash
    }

    fun getUniformBuffer(name: String): UniformBuffer {
        return descriptors.first { it is UniformBuffer && it.name == name } as UniformBuffer
    }

    fun getTextureSampler(name: String): TextureSampler {
        return descriptors.first { it is TextureSampler && it.name == name } as TextureSampler
    }

    class Builder {
        val descriptors = mutableListOf<Descriptor.Builder<*>>()

        operator fun <T: Descriptor> Descriptor.Builder<T>.unaryPlus() {
            descriptors += this
        }

        fun create(): DescriptorSetLayout {
            return DescriptorSetLayout(List(descriptors.size) { descriptors[it].create() })
        }
    }
}

abstract class Descriptor(builder: Builder<*>, val type: DescriptorType, hash: Long) {
    val name: String = builder.name
    val stages: Set<Stage> = builder.stages.copy()

    val longHash: Long

    init {
        var h = hash
        h = (h * 71023L) + builder.name.hashCode().toLong()
        h = (h * 71023L) + type.hashCode().toLong()
        longHash = h
    }

    abstract class Builder<T : Descriptor> {
        var name = ""
        val stages = mutableSetOf<Stage>()

        abstract fun create(): T
    }
}

class TextureSampler private constructor(builder: Builder, hash: Long) : Descriptor(builder, DescriptorType.IMAGE_SAMPLER, hash) {

    val onUpdate: ((TextureSampler, DrawCommand) -> Unit) ? = builder.onUpdate
    var texture: Texture? = null

    class Builder : Descriptor.Builder<TextureSampler>() {
        var onUpdate: ((TextureSampler, DrawCommand) -> Unit) ? = null

        init {
            name = "texture"
        }

        override fun create(): TextureSampler {
            return TextureSampler(this, DescriptorType.IMAGE_SAMPLER.hashCode() * 71023L)
        }
    }
}

class UniformBuffer private constructor(builder: Builder, val uniforms: List<Uniform<*>>, hash: Long) :
        Descriptor(builder, DescriptorType.UNIFORM_BUFFER, hash) {

    val onUpdate: ((UniformBuffer, DrawCommand) -> Unit) ? = builder.onUpdate

    /**
     * Overall size of buffer in bytes (i.e. all containing uniforms)
     */
    val size = uniforms.sumBy { it.size }

    // fixme: uniforms can contain all kinds of types, not only floats...
    fun putTo(buffer: Float32Buffer) {
        // fixme: ensure proper alignment! also might be platform specific...
        for (i in uniforms.indices) {
            uniforms[i].putTo(buffer)
        }
        buffer.flip()
    }

    fun updateMvp(modelIdx: Int, viewIdx: Int, projIdx: Int, cmd: DrawCommand) {
        (uniforms[modelIdx] as UniformMat4f).value.set(cmd.modelMat)
        (uniforms[viewIdx] as UniformMat4f).value.set(cmd.viewMat)
        (uniforms[projIdx] as UniformMat4f).value.set(cmd.projMat)
    }

    class Builder : Descriptor.Builder<UniformBuffer>() {
        val uniforms = mutableListOf<() -> Uniform<*>>()
        var onUpdate: ((UniformBuffer, DrawCommand) -> Unit) ? = null

        init {
            name = "ubo"
        }

        operator fun (() -> Uniform<*>).unaryPlus() {
            uniforms.add(this)
        }

        override fun create(): UniformBuffer {
            val uniforms = List(uniforms.size) { uniforms[it]() }
            var hash = DescriptorType.UNIFORM_BUFFER.hashCode() * 71023L
            uniforms.forEach {
                hash = (hash * 71023L) + it::class.hashCode().toLong()
                hash = (hash * 71023L) + it.name.hashCode().toLong()
            }
            return UniformBuffer(this, uniforms, hash)
        }
    }

    companion object {
        fun uboMvp() = Builder().apply {
            name = "ubo"
            stages += Stage.VERTEX_SHADER
            +{ UniformMat4f("model") }
            +{ UniformMat4f("view") }
            +{ UniformMat4f("proj") }

            onUpdate = { ubo, cmd ->
                ubo.updateMvp(0, 1, 2, cmd)
            }
        }
    }
}


enum class DescriptorType {
    IMAGE_SAMPLER,
    UNIFORM_BUFFER
}

enum class Stage {
    VERTEX_SHADER,
    GEOMETRY_SHADER,
    FRAGMENT_SHADER
}
