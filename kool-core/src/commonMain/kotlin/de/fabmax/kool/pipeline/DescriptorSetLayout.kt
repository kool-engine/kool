package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.MixedBuffer
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

        fun texture2d(name: String, vararg stages: ShaderStage, block: TextureSampler2d.Builder.() -> Unit) {
            val sampler = TextureSampler2d.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            +sampler
        }

        fun texture3d(name: String, vararg stages: ShaderStage, block: TextureSampler3d.Builder.() -> Unit) {
            val sampler = TextureSampler3d.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            +sampler
        }

        fun textureCube(name: String, vararg stages: ShaderStage, block: TextureSamplerCube.Builder.() -> Unit) {
            val sampler = TextureSamplerCube.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            +sampler
        }

        fun create(set: Int): DescriptorSetLayout {
            return DescriptorSetLayout(set, List(descriptors.size) { i -> descriptors[i].create(i) })
        }

        fun clear() {
            descriptors.clear()
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

class TextureSampler2d private constructor(builder: Builder, binding: Int, hash: ULong) :
        Descriptor(builder, binding, DescriptorType.SAMPLER_2D, hash) {

    val arraySize = builder.arraySize
    val isDepthSampler = builder.isDepthSampler

    val onUpdate: ((TextureSampler2d, DrawCommand) -> Unit) ? = builder.onUpdate
    val textures = Array<Texture2d?>(arraySize) { null }
    var texture: Texture2d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Descriptor.Builder<TextureSampler2d>() {
        var arraySize = 1
        var isDepthSampler = false
        var onUpdate: ((TextureSampler2d, DrawCommand) -> Unit) ? = null
        var onCreate: ((TextureSampler2d) -> Unit) ? = null

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler2d {
            val sampler = TextureSampler2d(this, binding, DescriptorType.SAMPLER_2D.hashCode().toULong() * 71023UL)
            onCreate?.invoke(sampler)
            return sampler
        }
    }
}

class TextureSampler3d private constructor(builder: Builder, binding: Int, hash: ULong) :
        Descriptor(builder, binding, DescriptorType.SAMPLER_3D, hash) {

    val arraySize = builder.arraySize

    val onUpdate: ((TextureSampler3d, DrawCommand) -> Unit) ? = builder.onUpdate
    val textures = Array<Texture3d?>(arraySize) { null }
    var texture: Texture3d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Descriptor.Builder<TextureSampler3d>() {
        var arraySize = 1
        var onUpdate: ((TextureSampler3d, DrawCommand) -> Unit) ? = null
        var onCreate: ((TextureSampler3d) -> Unit) ? = null

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler3d {
            val sampler = TextureSampler3d(this, binding, DescriptorType.SAMPLER_3D.hashCode().toULong() * 71023UL)
            onCreate?.invoke(sampler)
            return sampler
        }
    }
}

class TextureSamplerCube private constructor(builder: Builder, binding: Int, hash: ULong) :
        Descriptor(builder, binding, DescriptorType.SAMPLER_CUBE, hash) {

    val arraySize = builder.arraySize
    val isDepthSampler = builder.isDepthSampler

    val onUpdate: ((TextureSamplerCube, DrawCommand) -> Unit) ? = builder.onUpdate
    val textures = Array<TextureCube?>(arraySize) { null }
    var texture: TextureCube?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Descriptor.Builder<TextureSamplerCube>() {
        var arraySize = 1
        var isDepthSampler = false
        var onUpdate: ((TextureSamplerCube, DrawCommand) -> Unit) ? = null
        var onCreate: ((TextureSamplerCube) -> Unit) ? = null

        init {
            name = "cubeTexture"
        }

        override fun create(binding: Int): TextureSamplerCube {
            val sampler = TextureSamplerCube(this, binding, DescriptorType.SAMPLER_CUBE.hashCode().toULong() * 71023UL)
            onCreate?.invoke(sampler)
            return sampler
        }
    }
}

class UniformBuffer private constructor(builder: Builder, binding: Int, val uniforms: List<Uniform<*>>, hash: ULong) :
        Descriptor(builder, binding, DescriptorType.UNIFORM_BUFFER, hash) {

    val instanceName = builder.instanceName
    val onUpdate: ((UniformBuffer, DrawCommand) -> Unit) ? = builder.onUpdate

    private val layout = Std140Layout(uniforms)

    /**
     * Overall size of buffer in bytes (i.e. all containing uniforms, including padding)
     */
    val size = layout.size

    fun putTo(buffer: MixedBuffer) {
        layout.putTo(buffer)
        buffer.flip()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uniform(index: Int): T = uniforms[index] as T

    class Builder : Descriptor.Builder<UniformBuffer>() {
        var instanceName: String? = null

        val uniforms = mutableListOf<() -> Uniform<*>>()
        var onUpdate: ((UniformBuffer, DrawCommand) -> Unit) ? = null
        var onCreate: ((UniformBuffer) -> Unit) ? = null

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
            val ubo = UniformBuffer(this, binding, uniforms, hash)
            onCreate?.invoke(ubo)
            return ubo
        }
    }
}


enum class DescriptorType {
    SAMPLER_2D,
    SAMPLER_3D,
    SAMPLER_CUBE,
    UNIFORM_BUFFER
}

