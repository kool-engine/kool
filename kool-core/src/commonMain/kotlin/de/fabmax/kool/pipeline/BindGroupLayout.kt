package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.KslNumericType
import de.fabmax.kool.util.LongHash

class BindGroupLayout private constructor(val group: Int, val bindings: List<BindingLayout>) {

    val hash = LongHash()

    // todo: bind group scope? (mesh / material / scene)

    init {
        bindings.forEachIndexed { i, binding ->
            binding.bindingIndex = i
            hash += binding.hash
        }
    }

    fun createData(): BindGroupData = BindGroupData(this)

    class Builder {
        val ubos = mutableListOf<UniformBufferLayout>()
        val textures = mutableListOf<TextureLayout>()
        val storage = mutableListOf<StorageTextureLayout>()

        fun create(group: Int): BindGroupLayout {
            val groupItems = mutableListOf<BindingLayout>()
            // fixme: binding number should be generated differently for OpenGL and Vulkan:
            //  - for Vulkan binding number has to be unique for each binding in the group
            //  - for OpenGL binding number is currently only relevant for storage bindings and should start at 0 for those
            //  we achieve this somewhat hacky by adding storage bindings first
            groupItems += storage
            groupItems += textures
            groupItems += ubos
            return BindGroupLayout(group, groupItems)
        }
    }
}

sealed class BindingLayout(
    val name: String,
    val stages: Set<ShaderStage>,
    val type: BindingType
) {
    abstract val hash: Long

    /**
     * Index of this binding within it's parent [BindGroupLayout]. Only valid after the [BindGroupLayout] is created.
     */
    var bindingIndex: Int = -1
        internal set
}

class UniformBufferLayout(
    name: String,
    val uniforms: List<Uniform>,
    stages: Set<ShaderStage>,
) :
    BindingLayout(name, stages, BindingType.UNIFORM_BUFFER)
{
    val layout: Std140BufferLayout = Std140BufferLayout(uniforms)

    override val hash: Long = LongHash().let {
        it += name
        it += type
        uniforms.forEach { u ->
            it += u.name
            it += u.type
            it += u.arraySize
        }
        it.hash
    }
}

sealed class TextureLayout(
    name: String,
    val arraySize: Int,
    stages: Set<ShaderStage>,
    type: BindingType,
) : BindingLayout(name, stages, type) {

    override val hash: Long = LongHash().let {
        it += name
        it += arraySize
        it += type
        it.hash
    }
}

class Texture1dLayout(
    name: String,
    stages: Set<ShaderStage>,
    arraySize: Int = 1,
) : TextureLayout(name, arraySize, stages, BindingType.TEXTURE_1D)

class Texture2dLayout(
    name: String,
    stages: Set<ShaderStage>,
    arraySize: Int = 1,
    val isDepthTexture: Boolean = false,
) : TextureLayout(name, arraySize, stages, BindingType.TEXTURE_2D)

class Texture3dLayout(
    name: String,
    stages: Set<ShaderStage>,
) : TextureLayout(name, 1, stages, BindingType.TEXTURE_3D)

class TextureCubeLayout(
    name: String,
    stages: Set<ShaderStage>,
    arraySize: Int = 1,
    val isDepthTexture: Boolean = false,
) : TextureLayout(name, arraySize, stages, BindingType.TEXTURE_CUBE)

sealed class StorageTextureLayout(
    name: String,
    val format: KslNumericType,
    val accessType: StorageAccessType,
    val level: Int,
    stages: Set<ShaderStage>,
    type: BindingType,
) : BindingLayout(name, stages, type) {

    override val hash: Long = LongHash().let {
        it += name
        it += accessType
        it += format.typeName
        it += level
        it += type
        it.hash
    }
}

class StorageTexture1dLayout(
    name: String,
    format: KslNumericType,
    accessType: StorageAccessType,
    stages: Set<ShaderStage>,
    level: Int = 0
) : StorageTextureLayout(name, format, accessType, level, stages, BindingType.STORAGE_TEXTURE_1D)

class StorageTexture2dLayout(
    name: String,
    format: KslNumericType,
    accessType: StorageAccessType,
    stages: Set<ShaderStage>,
    level: Int = 0
) : StorageTextureLayout(name, format, accessType, level, stages, BindingType.STORAGE_TEXTURE_2D)

class StorageTexture3dLayout(
    name: String,
    format: KslNumericType,
    accessType: StorageAccessType,
    stages: Set<ShaderStage>,
    level: Int = 0
) : StorageTextureLayout(name, format, accessType, level, stages, BindingType.STORAGE_TEXTURE_3D)

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
