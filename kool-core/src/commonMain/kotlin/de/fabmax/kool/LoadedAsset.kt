package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.ImageData2d
import de.fabmax.kool.pipeline.ImageData3d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.util.Uint8Buffer

sealed class LoadedAsset<T: Any>(val result: Result<T>, val ref: AssetRef) {
    class Blob(ref: AssetRef, data: Result<Uint8Buffer>) : LoadedAsset<Uint8Buffer>(data, ref)
    class BufferedImage2d(ref: AssetRef, result: Result<BufferedImageData2d>) : LoadedAsset<BufferedImageData2d>(result, ref)
    class ImageAtlas(ref: AssetRef, result: Result<ImageData3d>) : LoadedAsset<ImageData3d>(result, ref)
    class Image2d(ref: AssetRef, result: Result<ImageData2d>) : LoadedAsset<ImageData2d>(result, ref)
    class Audio(ref: AssetRef, data: Result<AudioClip>) : LoadedAsset<AudioClip>(data, ref)
}

sealed class AssetRef(path: String) {
    val isHttp: Boolean = Assets.isHttpAsset(path)
    abstract val path: String

    data class Blob(
        override val path: String
    ) : AssetRef(path)

    data class Image2d(
        override val path: String,
        val props: TextureProps?
    ) : AssetRef(path)

    data class BufferedImage2d(
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
