package de.fabmax.kool.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.copy

class DescriptorSetLayout private constructor(val set: Int, val descriptors: List<Descriptor>) {

    val longHash: ULong

    init {
        var hash = 0UL
        descriptors.forEach {
            hash = (hash * 71023UL) + it.longHash
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

        fun uniformBuffer(name: String, vararg stages: ShaderStage, block: UniformBuffer.Builder.() -> Unit) {
            val uniformBuilder = UniformBuffer.Builder()
            uniformBuilder.name = name
            uniformBuilder.stages += stages
            uniformBuilder.block()
            +uniformBuilder
        }

        fun create(set: Int): DescriptorSetLayout {
            return DescriptorSetLayout(set, List(descriptors.size) { i -> descriptors[i].create(i) })
        }
    }
}

abstract class Descriptor(builder: Builder<*>, val binding: Int, val type: DescriptorType, hash: ULong) {
    val name: String = builder.name
    val stages: Set<ShaderStage> = builder.stages.copy()

    val longHash: ULong

    init {
        var h = hash
        h = (h * 71023UL) + builder.name.hashCode().toULong()
        h = (h * 71023UL) + type.hashCode().toULong()
        longHash = h
    }

    abstract class Builder<T : Descriptor> {
        var name = ""
        val stages = mutableSetOf<ShaderStage>()

        abstract fun create(binding: Int): T
    }
}

class TextureSampler private constructor(builder: Builder, binding: Int, hash: ULong) :
        Descriptor(builder, binding, DescriptorType.IMAGE_SAMPLER, hash) {

    val onUpdate: ((TextureSampler, DrawCommand) -> Unit) ? = builder.onUpdate
    var texture: Texture? = null

    class Builder : Descriptor.Builder<TextureSampler>() {
        var onUpdate: ((TextureSampler, DrawCommand) -> Unit) ? = null

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler {
            return TextureSampler(this, binding, DescriptorType.IMAGE_SAMPLER.hashCode().toULong() * 71023UL)
        }
    }
}

class UniformBuffer private constructor(builder: Builder, binding: Int, val uniforms: List<Uniform<*>>, hash: ULong) :
        Descriptor(builder, binding, DescriptorType.UNIFORM_BUFFER, hash) {

    val instanceName = builder.instanceName
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

    @Suppress("UNCHECKED_CAST")
    fun <T> uniform(index: Int): T = uniforms[index] as T

    fun updateMvp(modelIdx: Int, viewIdx: Int, projIdx: Int, cmd: DrawCommand) {
        (uniforms[modelIdx] as UniformMat4f).value.set(cmd.modelMat)
        (uniforms[viewIdx] as UniformMat4f).value.set(cmd.viewMat)
        (uniforms[projIdx] as UniformMat4f).value.set(cmd.projMat)
    }

    class Builder : Descriptor.Builder<UniformBuffer>() {
        var instanceName: String? = null

        val uniforms = mutableListOf<() -> Uniform<*>>()
        var onUpdate: ((UniformBuffer, DrawCommand) -> Unit) ? = null

        init {
            name = "Ubo"
        }

        operator fun (() -> Uniform<*>).unaryPlus() {
            uniforms.add(this)
        }

        override fun create(binding: Int): UniformBuffer {
            val uniforms = List(uniforms.size) { uniforms[it]() }
            var hash = DescriptorType.UNIFORM_BUFFER.hashCode().toULong() * 71023UL
            uniforms.forEach {
                hash = (hash * 71023UL) + it::class.hashCode().toULong()
                hash = (hash * 71023UL) + it.name.hashCode().toULong()
            }
            return UniformBuffer(this, binding, uniforms, hash)
        }
    }

    companion object {
        fun uboMvp() = Builder().apply {
            name = "ubo"
            stages += ShaderStage.VERTEX_SHADER
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

