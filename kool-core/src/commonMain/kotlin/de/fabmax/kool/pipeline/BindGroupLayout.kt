package de.fabmax.kool.pipeline

import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.indexOf

class BindGroupLayout(val group: Int, val scope: BindGroupScope, val bindings: List<BindingLayout>) {

    val hash: LongHash = LongHash {
        this += scope
        bindings.forEachIndexed { i, binding ->
            binding.bindingIndex = i
            this += binding.hash
        }
    }

    fun createData(): BindGroupData = BindGroupData(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BindGroupLayout
        return hash == other.hash
    }

    override fun hashCode(): Int = hash.hashCode()

    class Builder(val group: Int, val scope: BindGroupScope) {
        val ubos = mutableListOf<UniformBufferLayout<*>>()
        val textures = mutableListOf<TextureLayout>()
        val storage = mutableListOf<StorageBufferLayout>()
        val storageTextures = mutableListOf<StorageTextureLayout>()

        fun create(): BindGroupLayout {
            return BindGroupLayout(group, scope, ubos + textures + storage + storageTextures)
        }
    }
}

data class BindGroupLayouts(val viewScope: BindGroupLayout, val pipelineScope: BindGroupLayout, val meshScope: BindGroupLayout) {
    val asList = listOf(viewScope, pipelineScope, meshScope)

    init {
        check(
            viewScope.scope == BindGroupScope.VIEW &&
            pipelineScope.scope == BindGroupScope.PIPELINE &&
            meshScope.scope == BindGroupScope.MESH
        )
    }

    operator fun get(scope: BindGroupScope): BindGroupLayout {
        return when(scope) {
            BindGroupScope.VIEW -> viewScope
            BindGroupScope.PIPELINE -> pipelineScope
            BindGroupScope.MESH -> meshScope
        }
    }
}

enum class BindGroupScope {
    VIEW,
    PIPELINE,
    MESH
}

sealed class BindingLayout(
    val name: String,
    val stages: Set<ShaderStage>,
    val type: BindingType
) {
    abstract val hash: LongHash

    /**
     * Index of this binding within it's parent [BindGroupLayout]. Only valid after the [BindGroupLayout] is created.
     */
    var bindingIndex: Int = -1
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BindingLayout
        return hash == other.hash
    }

    override fun hashCode(): Int = hash.hashCode()
}

class UniformBufferLayout<T: Struct>(
    name: String,
    stages: Set<ShaderStage>,
    val structProvider: () -> T,
) : BindingLayout(name, stages, BindingType.UNIFORM_BUFFER) {

    @PublishedApi
    internal val proto = structProvider()

    init {
        require(proto.layout == MemoryLayout.Std140) {
            "Uniform buffer / struct layout must be Std140"
        }
    }

    inline fun <reified S: Struct> isStructInstanceOf() = proto is S

    fun indexOfMember(memberName: String) = proto.indexOf(memberName)

    inline fun <reified S: Struct> struct() = structProvider() as S

    override val hash: LongHash = LongHash {
        this += name
        this += type
        this += proto.hash
        stages.forEach { s -> this += s }
    }

    fun hasUniform(name: String) = proto.members.any { it.memberName == name }
}

class StorageBufferLayout(
    name: String,
    val format: GpuType,
    val size: Int?,
    val accessType: StorageAccessType,
    stages: Set<ShaderStage>
) : BindingLayout(name, stages, BindingType.STORAGE_BUFFER_1D) {

    override val hash: LongHash = LongHash {
        this += name
        this += accessType
        this += format
        this += type
        stages.forEach { s -> this += s }
        this.hash
    }
}

sealed class TextureLayout(
    name: String,
    stages: Set<ShaderStage>,
    type: BindingType,
    val sampleType: TextureSampleType
) : BindingLayout(name, stages, type) {

    override val hash: LongHash = LongHash {
        this += name
        this += type
        stages.forEach { s -> this += s }
        this.hash
    }
}

class Texture1dLayout(
    name: String,
    stages: Set<ShaderStage>,
    sampleType: TextureSampleType = TextureSampleType.FLOAT,
) : TextureLayout(name, stages, BindingType.TEXTURE_1D, sampleType)

class Texture2dLayout(
    name: String,
    stages: Set<ShaderStage>,
    sampleType: TextureSampleType = TextureSampleType.FLOAT,
) : TextureLayout(name, stages, BindingType.TEXTURE_2D, sampleType)

class Texture3dLayout(
    name: String,
    stages: Set<ShaderStage>,
    sampleType: TextureSampleType = TextureSampleType.FLOAT,
) : TextureLayout(name, stages, BindingType.TEXTURE_3D, sampleType)

class TextureCubeLayout(
    name: String,
    stages: Set<ShaderStage>,
    sampleType: TextureSampleType = TextureSampleType.FLOAT,
) : TextureLayout(name, stages, BindingType.TEXTURE_CUBE, sampleType)

class Texture2dArrayLayout(
    name: String,
    stages: Set<ShaderStage>,
    sampleType: TextureSampleType = TextureSampleType.FLOAT,
) : TextureLayout(name, stages, BindingType.TEXTURE_2D_ARRAY, sampleType)

class TextureCubeArrayLayout(
    name: String,
    stages: Set<ShaderStage>,
    sampleType: TextureSampleType = TextureSampleType.FLOAT,
) : TextureLayout(name, stages, BindingType.TEXTURE_CUBE_ARRAY, sampleType)

sealed class StorageTextureLayout(
    name: String,
    val accessType: StorageAccessType,
    val texFormat: TexFormat,
    stages: Set<ShaderStage>,
    type: BindingType,
) : BindingLayout(name, stages, type) {

    override val hash: LongHash = LongHash {
        this += name
        this += accessType
        this += type
        stages.forEach { s -> this += s }
        this.hash
    }
}

class StorageTexture1dLayout(
    name: String,
    accessType: StorageAccessType,
    texFormat: TexFormat,
    stages: Set<ShaderStage>,
) : StorageTextureLayout(name, accessType, texFormat, stages, BindingType.STORAGE_TEXTURE_1D)

class StorageTexture2dLayout(
    name: String,
    accessType: StorageAccessType,
    texFormat: TexFormat,
    stages: Set<ShaderStage>,
) : StorageTextureLayout(name, accessType, texFormat, stages, BindingType.STORAGE_TEXTURE_2D)

class StorageTexture3dLayout(
    name: String,
    accessType: StorageAccessType,
    texFormat: TexFormat,
    stages: Set<ShaderStage>,
) : StorageTextureLayout(name, accessType, texFormat, stages, BindingType.STORAGE_TEXTURE_3D)

enum class BindingType {
    UNIFORM_BUFFER,
    STORAGE_BUFFER_1D,
    STORAGE_BUFFER_2D,
    STORAGE_BUFFER_3D,
    TEXTURE_1D,
    TEXTURE_2D,
    TEXTURE_3D,
    TEXTURE_CUBE,
    TEXTURE_2D_ARRAY,
    TEXTURE_CUBE_ARRAY,
    STORAGE_TEXTURE_1D,
    STORAGE_TEXTURE_2D,
    STORAGE_TEXTURE_3D,
}

enum class StorageAccessType {
    READ_ONLY,
    WRITE_ONLY,
    READ_WRITE
}

enum class TextureSampleType {
    FLOAT,
    UNFILTERABLE_FLOAT,
    DEPTH
}
