package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.KslFloat1
import de.fabmax.kool.modules.ksl.lang.KslNumericType
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.copy

class BindGroupLayout private constructor(val group: Int, val bindings: List<Binding>) {

    val hash = LongHash()

    init {
        bindings.forEach {
            hash += it.hash
        }
    }

    fun findBindingByName(name: String): Binding? {
        return bindings.find { it.name == name }
    }

    fun createData(): BindGroupData = BindGroupData(this)

    class Builder {
        val ubos = mutableListOf<UniformBufferBinding.Builder>()
        val samplers = mutableListOf<Binding.Builder<*>>()
        val storage = mutableListOf<Binding.Builder<*>>()

        fun uniformBuffer(name: String, vararg stages: ShaderStage, block: UniformBufferBinding.Builder.() -> Unit) {
            val uniformBuilder = UniformBufferBinding.Builder()
            uniformBuilder.name = name
            uniformBuilder.stages += stages
            uniformBuilder.block()
            ubos += uniformBuilder
        }

        fun texture1d(name: String, vararg stages: ShaderStage, block: Texture1dBinding.Builder.() -> Unit) {
            val sampler = Texture1dBinding.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            samplers += sampler
        }

        fun texture2d(name: String, vararg stages: ShaderStage, block: Texture2dBinding.Builder.() -> Unit) {
            val sampler = Texture2dBinding.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            samplers += sampler
        }

        fun texture3d(name: String, vararg stages: ShaderStage, block: Texture3dBinding.Builder.() -> Unit) {
            val sampler = Texture3dBinding.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            samplers += sampler
        }

        fun textureCube(name: String, vararg stages: ShaderStage, block: TextureCubeBinding.Builder.() -> Unit) {
            val sampler = TextureCubeBinding.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            samplers += sampler
        }

        fun storage1d(name: String, vararg stages: ShaderStage, block: StorageTexture1dBinding.Builder.() -> Unit) {
            val sampler = StorageTexture1dBinding.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            storage += sampler
        }

        fun storage2d(name: String, vararg stages: ShaderStage, block: StorageTexture2dBinding.Builder.() -> Unit) {
            val sampler = StorageTexture2dBinding.Builder()
            sampler.name = name
            sampler.stages += stages
            sampler.block()
            storage += sampler
        }

        fun storage3d(name: String, vararg stages: ShaderStage, block: StorageTexture3dBinding.Builder.() -> Unit) {
            val sampler = StorageTexture3dBinding.Builder()
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

sealed class TextureBinding(builder: Builder<*>, binding: Int, type: BindingType) : Binding(builder, binding, type) {
    abstract val arraySize: Int
}

class Texture1dBinding private constructor(builder: Builder, binding: Int) :
    TextureBinding(builder, binding, BindingType.TEXTURE_1D)
{
    override val arraySize = builder.arraySize

    val textures = Array<Texture1d?>(arraySize) { null }
    var texture: Texture1d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<Texture1dBinding>() {
        var arraySize = 1

        init {
            name = "texture"
        }

        override fun create(binding: Int): Texture1dBinding {
            return Texture1dBinding(this, binding)
        }
    }
}

class Texture2dBinding private constructor(builder: Builder, binding: Int) :
    TextureBinding(builder, binding, BindingType.TEXTURE_2D)
{
    override val arraySize = builder.arraySize
    val isDepthSampler = builder.isDepthSampler

    val textures = Array<Texture2d?>(arraySize) { null }
    var texture: Texture2d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<Texture2dBinding>() {
        var arraySize = 1
        var isDepthSampler = false

        init {
            name = "texture"
        }

        override fun create(binding: Int): Texture2dBinding {
            return Texture2dBinding(this, binding)
        }
    }
}

class Texture3dBinding private constructor(builder: Builder, binding: Int) :
    TextureBinding(builder, binding, BindingType.TEXTURE_3D)
{
    override val arraySize = builder.arraySize
    val textures = Array<Texture3d?>(arraySize) { null }
    var texture: Texture3d?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<Texture3dBinding>() {
        var arraySize = 1

        init {
            name = "texture"
        }

        override fun create(binding: Int): Texture3dBinding {
            return Texture3dBinding(this, binding)
        }
    }
}

class TextureCubeBinding private constructor(builder: Builder, binding: Int) :
    TextureBinding(builder, binding, BindingType.TEXTURE_CUBE)
{
    override val arraySize = builder.arraySize
    val isDepthSampler = builder.isDepthSampler

    val textures = Array<TextureCube?>(arraySize) { null }
    var texture: TextureCube?
        get() = textures[0]
        set(value) { textures[0] = value }

    class Builder : Binding.Builder<TextureCubeBinding>() {
        var arraySize = 1
        var isDepthSampler = false

        init {
            name = "cubeTexture"
        }

        override fun create(binding: Int): TextureCubeBinding {
            return TextureCubeBinding(this, binding)
        }
    }
}

class UniformBufferBinding private constructor(builder: Builder, binding: Int, val uniforms: List<Uniform<*>>) :
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

    class Builder : Binding.Builder<UniformBufferBinding>() {
        val uniforms = mutableListOf<() -> Uniform<*>>()
        var isShared = true

        init {
            name = "Ubo"
        }

        operator fun (() -> Uniform<*>).unaryPlus() {
            uniforms.add(this)
        }

        override fun create(binding: Int): UniformBufferBinding {
            val uniforms = List(uniforms.size) { uniforms[it]() }
            return UniformBufferBinding(this, binding, uniforms)
        }
    }
}

sealed class StorageTextureBinding<T: StorageTextureBinding<T>>(builder: Builder<T>, binding: Int, type: BindingType) :
    Binding(builder, binding, type)
{
    val accessType = builder.accessType
    val format = builder.format
    var level = 0

    abstract class Builder<T: StorageTextureBinding<T>> : Binding.Builder<T>() {
        var accessType = StorageAccessType.READ_WRITE
        var format: KslNumericType = KslFloat1
    }
}

class StorageTexture1dBinding private constructor(builder: Builder, binding: Int) :
    StorageTextureBinding<StorageTexture1dBinding>(builder, binding, BindingType.STORAGE_TEXTURE_1D)
{
    var storageTex: StorageTexture1d? = null

    class Builder : StorageTextureBinding.Builder<StorageTexture1dBinding>() {
        init {
            name = "storage1d"
        }

        override fun create(binding: Int): StorageTexture1dBinding {
            return StorageTexture1dBinding(this, binding)
        }
    }
}

class StorageTexture2dBinding private constructor(builder: Builder, binding: Int) :
    StorageTextureBinding<StorageTexture2dBinding>(builder, binding, BindingType.STORAGE_TEXTURE_2D)
{
    var storageTex: StorageTexture2d? = null

    class Builder : StorageTextureBinding.Builder<StorageTexture2dBinding>() {
        init {
            name = "storage2d"
        }

        override fun create(binding: Int): StorageTexture2dBinding {
            return StorageTexture2dBinding(this, binding)
        }
    }
}

class StorageTexture3dBinding private constructor(builder: Builder, binding: Int) :
    StorageTextureBinding<StorageTexture3dBinding>(builder, binding, BindingType.STORAGE_TEXTURE_3D)
{
    var storageTex: StorageTexture3d? = null

    class Builder : StorageTextureBinding.Builder<StorageTexture3dBinding>() {
        init {
            name = "storage3d"
        }

        override fun create(binding: Int): StorageTexture3dBinding {
            return StorageTexture3dBinding(this, binding)
        }
    }
}

enum class BindingType {
    TEXTURE_1D,
    TEXTURE_2D,
    TEXTURE_3D,
    TEXTURE_CUBE,
    UNIFORM_BUFFER,
    STORAGE_TEXTURE_1D,
    STORAGE_TEXTURE_2D,
    STORAGE_TEXTURE_3D
}

enum class StorageAccessType {
    READ_ONLY,
    WRITE_ONLY,
    READ_WRITE
}
