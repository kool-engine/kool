package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.util.UniqueId

sealed interface StorageTexture {
    val asTexture: Texture<*>
}

fun StorageTexture1d(
    width: Int,
    format: TexFormat,
    mipMapping: MipMapping = MipMapping.Off,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("StorageTexture1d")
): StorageTexture1d = StorageTexture1d(width, format, mipMapping, samplerSettings, KoolSystem.requireContext().backend, name)

fun StorageTexture2d(
    width: Int,
    height: Int,
    format: TexFormat,
    mipMapping: MipMapping = MipMapping.Off,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("StorageTexture2d")
): StorageTexture2d = StorageTexture2d(width, height, format, mipMapping, samplerSettings, KoolSystem.requireContext().backend, name)

fun StorageTexture3d(
    width: Int,
    height: Int,
    depth: Int,
    format: TexFormat,
    mipMapping: MipMapping = MipMapping.Off,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("StorageTexture3d")
):  StorageTexture3d = StorageTexture3d(width, height, depth, format, mipMapping, samplerSettings, KoolSystem.requireContext().backend, name)

class StorageTexture1d(
    width: Int,
    format: TexFormat,
    mipMapping: MipMapping,
    samplerSettings: SamplerSettings,
    private val backend: RenderBackend,
    name: String = UniqueId.nextId("StorageTexture1d")
) : Texture1d(format, mipMapping, samplerSettings, name), StorageTexture {
    override val asTexture: Texture1d get() = this
    override var mipMapping: MipMapping = mipMapping; private set

    init {
        backend.initStorageTexture(this, width, 1, 1)
    }

    fun resize(width: Int, mipMapping: MipMapping = this.mipMapping) {
        if (width != this.width || mipMapping != this.mipMapping) {
            this.mipMapping = mipMapping
            backend.initStorageTexture(this, width, 1, 1)
        }
    }
}

class StorageTexture2d(
    width: Int,
    height: Int,
    format: TexFormat,
    mipMapping: MipMapping,
    samplerSettings: SamplerSettings,
    private val backend: RenderBackend,
    name: String
) : Texture2d(format, mipMapping, samplerSettings, name), StorageTexture {
    override val asTexture: Texture2d get() = this
    override var mipMapping: MipMapping = mipMapping; private set

    init {
        backend.initStorageTexture(this, width, height, 1)
    }

    fun resize(width: Int, height: Int, mipMapping: MipMapping = this.mipMapping) {
        if (width != this.width || height != this.height || mipMapping != this.mipMapping) {
            this.mipMapping = mipMapping
            backend.initStorageTexture(this, width, height, 1)
        }
    }
}

class StorageTexture3d(
    width: Int,
    height: Int,
    depth: Int,
    format: TexFormat,
    mipMapping: MipMapping,
    samplerSettings: SamplerSettings,
    private val backend: RenderBackend,
    name: String
) : Texture3d(format, mipMapping, samplerSettings, name), StorageTexture {
    override val asTexture: Texture3d get() = this
    override var mipMapping: MipMapping = mipMapping; private set

    init {
        backend.initStorageTexture(this, width, height, depth)
    }

    fun resize(width: Int, height: Int, depth: Int, mipMapping: MipMapping = this.mipMapping) {
        if (width != this.width || height != this.height || depth != this.depth || mipMapping != this.mipMapping) {
            this.mipMapping = mipMapping
            backend.initStorageTexture(this, width, height, depth)
        }
    }
}