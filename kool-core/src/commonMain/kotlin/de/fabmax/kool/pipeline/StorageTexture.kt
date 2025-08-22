package de.fabmax.kool.pipeline

import de.fabmax.kool.util.UniqueId

sealed interface StorageTexture {
    val asTexture: Texture<*>
}

class StorageTexture1d(
    width: Int,
    format: TexFormat,
    mipMapping: MipMapping = MipMapping.Off,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("StorageTexture1d"),
) : Texture1d(format, mipMapping, samplerSettings, name), StorageTexture {
    override val asTexture: Texture1d get() = this
    override var mipMapping: MipMapping = mipMapping; private set

    override var width: Int = width.coerceAtLeast(1)
        private set
    override val height: Int = 1
    override val depth: Int = 1

    fun resize(width: Int, mipMapping: MipMapping = this.mipMapping) {
        val w = width.coerceAtLeast(1)
        if (w != this.width || mipMapping != this.mipMapping) {
            this.mipMapping = mipMapping
            this.width = w
        }
    }
}

class StorageTexture2d(
    width: Int,
    height: Int,
    format: TexFormat,
    mipMapping: MipMapping = MipMapping.Off,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("StorageTexture2d"),
) : Texture2d(format, mipMapping, samplerSettings, name), StorageTexture {
    override val asTexture: Texture2d get() = this
    override var mipMapping: MipMapping = mipMapping; private set

    override var width: Int = width.coerceAtLeast(1)
        private set
    override var height: Int = height.coerceAtLeast(1)
        private set
    override val depth: Int = 1

    fun resize(width: Int, height: Int, mipMapping: MipMapping = this.mipMapping) {
        val w = width.coerceAtLeast(1)
        val h = height.coerceAtLeast(1)
        if (w != this.width || h != this.height || mipMapping != this.mipMapping) {
            this.mipMapping = mipMapping
            this.width = w
            this.height = h
        }
    }
}

class StorageTexture3d(
    width: Int,
    height: Int,
    depth: Int,
    format: TexFormat,
    mipMapping: MipMapping = MipMapping.Off,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("StorageTexture2d"),
) : Texture3d(format, mipMapping, samplerSettings, name), StorageTexture {
    override val asTexture: Texture3d get() = this
    override var mipMapping: MipMapping = mipMapping; private set

    override var width: Int = width.coerceAtLeast(1)
        private set
    override var height: Int = height.coerceAtLeast(1)
        private set
    override var depth: Int = depth.coerceAtLeast(1)
        private set

    fun resize(width: Int, height: Int, depth: Int, mipMapping: MipMapping = this.mipMapping) {
        val w = width.coerceAtLeast(1)
        val h = height.coerceAtLeast(1)
        val d = depth.coerceAtLeast(1)
        if (width != this.width || height != this.height || depth != this.depth || mipMapping != this.mipMapping) {
            this.mipMapping = mipMapping
            this.width = w
            this.height = h
            this.depth = d
        }
    }
}