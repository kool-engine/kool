package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.util.Uint8Buffer

sealed class LoadedAsset<T: Any>(val result: Result<T>, val ref: AssetRef) {
    class Blob(ref: AssetRef, data: Result<Uint8Buffer>) : LoadedAsset<Uint8Buffer>(data, ref)
    class Image(ref: AssetRef, result: Result<TextureData>) : LoadedAsset<TextureData>(result, ref)
    class ImageBuffer(ref: AssetRef, result: Result<TextureData2d>) : LoadedAsset<TextureData2d>(result, ref)
    class Audio(ref: AssetRef, data: Result<AudioClip>) : LoadedAsset<AudioClip>(data, ref)
}

sealed class AssetRef(path: String) {
    val isHttp: Boolean = Assets.isHttpAsset(path)
    abstract val path: String

    data class Blob(
        override val path: String
    ) : AssetRef(path)

    data class Image(
        override val path: String,
        val props: TextureProps?
    ) : AssetRef(path)

    data class ImageBuffer(
        override val path: String,
        val props: TextureProps?
    ) : AssetRef(path)

    data class ImageAtlas(
        override val path: String,
        val props: TextureProps?,
        val tilesX: Int = 1,
        val tilesY: Int = 1
    ) : AssetRef(path)

    data class Audio(
        override val path: String
    ) : AssetRef(path)
}
