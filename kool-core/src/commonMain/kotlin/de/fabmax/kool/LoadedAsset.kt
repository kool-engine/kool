package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.util.Uint8Buffer

sealed class LoadedAsset<T: Any>(val result: Result<T>, val ref: AssetRef)

class LoadedBlobAsset(ref: AssetRef, data: Result<Uint8Buffer>) : LoadedAsset<Uint8Buffer>(data, ref)
class LoadedTextureAsset(ref: AssetRef, data: Result<TextureData>) : LoadedAsset<TextureData>(data, ref)
class LoadedAudioClipAsset(ref: AssetRef, data: Result<AudioClip>) : LoadedAsset<AudioClip>(data, ref)

sealed class AssetRef(path: String) {
    val isHttp: Boolean = Assets.isHttpAsset(path)
    abstract val path: String
}

data class BlobAssetRef(
    override val path: String
) : AssetRef(path)

data class TextureAssetRef(
    override val path: String,
    val props: TextureProps?
) : AssetRef(path)

data class TextureAtlasAssetRef(
    override val path: String,
    val props: TextureProps?,
    val tilesX: Int = 1,
    val tilesY: Int = 1
) : AssetRef(path)

data class TextureData2dRef(
    override val path: String,
    val props: TextureProps?
) : AssetRef(path)

data class AudioClipRef(
    override val path: String
) : AssetRef(path)
