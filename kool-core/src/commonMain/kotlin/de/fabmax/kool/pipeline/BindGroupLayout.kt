package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.KslFloat1
import de.fabmax.kool.modules.ksl.lang.KslNumericType
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.copy

class BindGroupLayout private constructor(val group: Int, val items: List<Binding>) {

    val hash = LongHash()

    init {
        items.forEach {
            hash += it.hash
        }
    }

    fun findItemsByName(name: String): Binding? {
        return items.find { it.name == name }
    }

    class Builder {
        val ubos = mutableListOf<UniformBuffer.Builder>()
        val samplers = mutableListOf<Binding.Builder<*>>()
        val storage = mutableListOf<Binding.Builder<*>>()

        fun uniformBuffer(name: String, vararg stages: ShaderStage, block: UniformBuffer.Builder.() -> Unit) {
            val uniformBuilder = UniformBuffer.Builder()
            uniformBuilder.name = name
            uniformBuilder.stages += stages
            uniformBuilder.block()
            ubos += uniformBuilder
        }

        fun texture1d(name: String, vararg stages: ShaderStage, block: TextureSampler1d.Builder.() -> Unit) {
            val sampler = TextureSampler1d.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            samplers += sampler
        }

        fun texture2d(name: String, vararg stages: ShaderStage, block: TextureSampler2d.Builder.() -> Unit) {
            val sampler = TextureSampler2d.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            samplers += sampler
        }

        fun texture3d(name: String, vararg stages: ShaderStage, block: TextureSampler3d.Builder.() -> Unit) {
            val sampler = TextureSampler3d.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            samplers += sampler
        }

        fun textureCube(name: String, vararg stages: ShaderStage, block: TextureSamplerCube.Builder.() -> Unit) {
            val sampler = TextureSamplerCube.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            samplers += sampler
        }

        fun storage1d(name: String, vararg stages: ShaderStage, block: Storage1d.Builder.() -> Unit) {
            val sampler = Storage1d.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            storage += sampler
        }

        fun storage2d(name: String, vararg stages: ShaderStage, block: Storage2d.Builder.() -> Unit) {
            val sampler = Storage2d.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            storage += sampler
        }

        fun storage3d(name: String, vararg stages: ShaderStage, block: Storage3d.Builder.() -> Unit) {
            val sampler = Storage3d.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            storage += sampler
        }

        fun create(group: Int): BindGroupLayout {
            val groupItems = mutableListOf<Binding>()
            ubos.forEach { builder ->
                groupItems += builder.create(groupItems.size)
            }
            samplers.forEach { builder ->
                groupItems += builder.create(groupItems.size)
            }
            storage.forEach { builder ->
                groupItems += builder.create(groupItems.size)
            }
            return BindGroupLayout(group, groupItems)
        }
    }
}

sealed class Binding(builder: Builder<*>, val binding: Int, val type: BindingType) {
    val name: String = builder.name
    val stages: Set<ShaderStage> = builder.stages.copy()

    val hash = LongHash()

    init {
        hash += builder.name
        hash += binding
        hash += type
    }

    abstract class Builder<T : Binding> {
        var name = ""
        val stages = mutableSetOf<ShaderStage>()

        abstract fun create(binding: Int): T
    }
}

class TextureSampler1d private constructor(builder: Builder, binding: Int) :
    Binding(builder, binding, BindingType.SAMPLER_1D)
{

    val arraySize = builder.arraySize

    val onUpdate: ((TextureSampler1d, DrawCommand) -> Unit) ? = builder.onUpdate
    val textures = Array<Texture1d?>(arraySize) { null }
    var texture: Texture1d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureSampler1d>() {
        var arraySize = 1
        var onUpdate: ((TextureSampler1d, DrawCommand) -> Unit)? = null
        var onCreate: ((TextureSampler1d) -> Unit)? = null

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler1d {
            val sampler = TextureSampler1d(this, binding)
            onCreate?.invoke(sampler)
            return sampler
        }
    }
}

class TextureSampler2d private constructor(builder: Builder, binding: Int) :
    Binding(builder, binding, BindingType.SAMPLER_2D)
{

    val arraySize = builder.arraySize
    val isDepthSampler = builder.isDepthSampler

    val onUpdate: ((TextureSampler2d, DrawCommand) -> Unit)? = builder.onUpdate
    val textures = Array<Texture2d?>(arraySize) { null }
    var texture: Texture2d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureSampler2d>() {
        var arraySize = 1
        var isDepthSampler = false
        var onUpdate: ((TextureSampler2d, DrawCommand) -> Unit)? = null
        var onCreate: ((TextureSampler2d) -> Unit)? = null

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler2d {
            val sampler = TextureSampler2d(this, binding)
            onCreate?.invoke(sampler)
            return sampler
        }
    }
}

class TextureSampler3d private constructor(builder: Builder, binding: Int) :
    Binding(builder, binding, BindingType.SAMPLER_3D)
{

    val arraySize = builder.arraySize

    val onUpdate: ((TextureSampler3d, DrawCommand) -> Unit)? = builder.onUpdate
    val textures = Array<Texture3d?>(arraySize) { null }
    var texture: Texture3d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureSampler3d>() {
        var arraySize = 1
        var onUpdate: ((TextureSampler3d, DrawCommand) -> Unit)? = null
        var onCreate: ((TextureSampler3d) -> Unit)? = null

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler3d {
            val sampler = TextureSampler3d(this, binding)
            onCreate?.invoke(sampler)
            return sampler
        }
    }
}

class TextureSamplerCube private constructor(builder: Builder, binding: Int) :
    Binding(builder, binding, BindingType.SAMPLER_CUBE)
{

    val arraySize = builder.arraySize
    val isDepthSampler = builder.isDepthSampler

    val onUpdate: ((TextureSamplerCube, DrawCommand) -> Unit)? = builder.onUpdate
    val textures = Array<TextureCube?>(arraySize) { null }
    var texture: TextureCube?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureSamplerCube>() {
        var arraySize = 1
        var isDepthSampler = false
        var onUpdate: ((TextureSamplerCube, DrawCommand) -> Unit)? = null
        var onCreate: ((TextureSamplerCube) -> Unit)? = null

        init {
            name = "cubeTexture"
        }

        override fun create(binding: Int): TextureSamplerCube {
            val sampler = TextureSamplerCube(this, binding)
            onCreate?.invoke(sampler)
            return sampler
        }
    }
}

class UniformBuffer private constructor(builder: Builder, binding: Int, val uniforms: List<Uniform<*>>) :
    Binding(builder, binding, BindingType.UNIFORM_BUFFER)
{

    val instanceName = builder.instanceName
    val onUpdate: ((UniformBuffer, DrawCommand) -> Unit)? = builder.onUpdate

    init {
        uniforms.forEach {
            hash += it.name
            hash += it::class.hashCode()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uniform(index: Int): T = uniforms[index] as T

    class Builder : Binding.Builder<UniformBuffer>() {
        var instanceName: String? = null

        val uniforms = mutableListOf<() -> Uniform<*>>()
        var onUpdate: ((UniformBuffer, DrawCommand) -> Unit)? = null
        var onCreate: ((UniformBuffer) -> Unit)? = null

        init {
            name = "Ubo"
        }

        operator fun (() -> Uniform<*>).unaryPlus() {
            uniforms.add(this)
        }

        override fun create(binding: Int): UniformBuffer {
            val uniforms = List(uniforms.size) { uniforms[it]() }
            val ubo = UniformBuffer(this, binding, uniforms)
            onCreate?.invoke(ubo)
            return ubo
        }
    }
}

sealed class StorageBinding<T: StorageBinding<T>>(builder: Builder<T>, binding: Int, type: BindingType) :
    Binding(builder, binding, type)
{
    val accessType = builder.accessType
    val format = builder.format
    val onUpdate: ((T) -> Unit) ? = builder.onUpdate

    abstract class Builder<T: StorageBinding<T>> : Binding.Builder<T>() {
        var accessType = StorageAccessType.READ_WRITE
        var format: KslNumericType = KslFloat1
        var onUpdate: ((T) -> Unit)? = null
        var onCreate: ((T) -> Unit)? = null
    }
}

class Storage1d private constructor(builder: Builder, binding: Int) :
    StorageBinding<Storage1d>(builder, binding, BindingType.STORAGE_1D)
{
    var storage: Texture1d? = null

    class Builder : StorageBinding.Builder<Storage1d>() {
        init {
            name = "storage1d"
        }

        override fun create(binding: Int): Storage1d {
            val storage = Storage1d(this, binding)
            onCreate?.invoke(storage)
            return storage
        }
    }
}

class Storage2d private constructor(builder: Builder, binding: Int) :
    StorageBinding<Storage2d>(builder, binding, BindingType.STORAGE_2D)
{
    var storage: Texture2d? = null

    class Builder : StorageBinding.Builder<Storage2d>() {
        init {
            name = "storage2d"
        }

        override fun create(binding: Int): Storage2d {
            val storage = Storage2d(this, binding)
            onCreate?.invoke(storage)
            return storage
        }
    }
}

class Storage3d private constructor(builder: Builder, binding: Int) :
    StorageBinding<Storage3d>(builder, binding, BindingType.STORAGE_3D)
{
    var storage: Texture3d? = null

    class Builder : StorageBinding.Builder<Storage3d>() {
        init {
            name = "storage3d"
        }

        override fun create(binding: Int): Storage3d {
            val storage = Storage3d(this, binding)
            onCreate?.invoke(storage)
            return storage
        }
    }
}

enum class BindingType {
    SAMPLER_1D,
    SAMPLER_2D,
    SAMPLER_3D,
    SAMPLER_CUBE,
    UNIFORM_BUFFER,
    STORAGE_1D,
    STORAGE_2D,
    STORAGE_3D
}

enum class StorageAccessType {
    READ_ONLY,
    WRITE_ONLY,
    READ_WRITE
}
