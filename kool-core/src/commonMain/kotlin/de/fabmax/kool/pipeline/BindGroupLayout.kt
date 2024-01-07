package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.KslFloat1
import de.fabmax.kool.modules.ksl.lang.KslNumericType
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.copy

class BindGroupLayout private constructor(val group: Int, val items: List<Binding>) {

    val hash = LongHash()

    init {
        items.forEach {
            hash += it.hash
        }
    }

    fun findBindingByName(name: String): Binding? {
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
            // fixme: binding number should be generated differently for OpenGL and Vulkan:
            //  - for Vulkan binding number has to be unique for each binding in the group
            //  - for OpenGL binding number is currently only relevant for storage bindings and should start at 0 for those
            //  we achieve this somewhat hacky by adding storage bindings first
            storage.forEach { builder ->
                groupItems += builder.create(groupItems.size)
            }
            samplers.forEach { builder ->
                groupItems += builder.create(groupItems.size)
            }
            ubos.forEach { builder ->
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

    val textures = Array<Texture1d?>(arraySize) { null }
    var texture: Texture1d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureSampler1d>() {
        var arraySize = 1

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler1d {
            return TextureSampler1d(this, binding)
        }
    }
}

class TextureSampler2d private constructor(builder: Builder, binding: Int) :
    Binding(builder, binding, BindingType.SAMPLER_2D)
{
    val arraySize = builder.arraySize
    val isDepthSampler = builder.isDepthSampler

    val textures = Array<Texture2d?>(arraySize) { null }
    var texture: Texture2d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureSampler2d>() {
        var arraySize = 1
        var isDepthSampler = false

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler2d {
            return TextureSampler2d(this, binding)
        }
    }
}

class TextureSampler3d private constructor(builder: Builder, binding: Int) :
    Binding(builder, binding, BindingType.SAMPLER_3D)
{
    val arraySize = builder.arraySize
    val textures = Array<Texture3d?>(arraySize) { null }
    var texture: Texture3d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureSampler3d>() {
        var arraySize = 1

        init {
            name = "texture"
        }

        override fun create(binding: Int): TextureSampler3d {
            return TextureSampler3d(this, binding)
        }
    }
}

class TextureSamplerCube private constructor(builder: Builder, binding: Int) :
    Binding(builder, binding, BindingType.SAMPLER_CUBE)
{
    val arraySize = builder.arraySize
    val isDepthSampler = builder.isDepthSampler

    val textures = Array<TextureCube?>(arraySize) { null }
    var texture: TextureCube?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureSamplerCube>() {
        var arraySize = 1
        var isDepthSampler = false

        init {
            name = "cubeTexture"
        }

        override fun create(binding: Int): TextureSamplerCube {
            return TextureSamplerCube(this, binding)
        }
    }
}

class UniformBuffer private constructor(builder: Builder, binding: Int, val uniforms: List<Uniform<*>>) :
    Binding(builder, binding, BindingType.UNIFORM_BUFFER)
{
    val isShared = builder.isShared

    init {
        hash += builder.isShared
        uniforms.forEach {
            hash += it.name
            hash += it::class.hashCode()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uniform(index: Int): T = uniforms[index] as T

    class Builder : Binding.Builder<UniformBuffer>() {
        val uniforms = mutableListOf<() -> Uniform<*>>()
        var isShared = true

        init {
            name = "Ubo"
        }

        operator fun (() -> Uniform<*>).unaryPlus() {
            uniforms.add(this)
        }

        override fun create(binding: Int): UniformBuffer {
            val uniforms = List(uniforms.size) { uniforms[it]() }
            return UniformBuffer(this, binding, uniforms)
        }
    }
}

sealed class StorageBinding<T: StorageBinding<T>>(builder: Builder<T>, binding: Int, type: BindingType) :
    Binding(builder, binding, type)
{
    val accessType = builder.accessType
    val format = builder.format
    var level = 0

    abstract class Builder<T: StorageBinding<T>> : Binding.Builder<T>() {
        var accessType = StorageAccessType.READ_WRITE
        var format: KslNumericType = KslFloat1
    }
}

class Storage1d private constructor(builder: Builder, binding: Int) :
    StorageBinding<Storage1d>(builder, binding, BindingType.STORAGE_1D)
{
    var storageTex: StorageTexture1d? = null

    class Builder : StorageBinding.Builder<Storage1d>() {
        init {
            name = "storage1d"
        }

        override fun create(binding: Int): Storage1d {
            return Storage1d(this, binding)
        }
    }
}

class Storage2d private constructor(builder: Builder, binding: Int) :
    StorageBinding<Storage2d>(builder, binding, BindingType.STORAGE_2D)
{
    var storageTex: StorageTexture2d? = null

    class Builder : StorageBinding.Builder<Storage2d>() {
        init {
            name = "storage2d"
        }

        override fun create(binding: Int): Storage2d {
            return Storage2d(this, binding)
        }
    }
}

class Storage3d private constructor(builder: Builder, binding: Int) :
    StorageBinding<Storage3d>(builder, binding, BindingType.STORAGE_3D)
{
    var storageTex: StorageTexture3d? = null

    class Builder : StorageBinding.Builder<Storage3d>() {
        init {
            name = "storage3d"
        }

        override fun create(binding: Int): Storage3d {
            return Storage3d(this, binding)
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
