package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.platform.imageAtlasTextureData
import java.io.ByteArrayInputStream

class FileSystemAssetLoaderAndroid(baseDir: FileSystemDirectory): FileSystemAssetLoader(baseDir) {
    override suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val refCopy = TextureData2dRef(textureRef.path, textureRef.props)
        val texData = loadTextureData2d(refCopy).data as TextureData2d?
        val atlasData = texData?.let {
            imageAtlasTextureData(it, textureRef.tilesX, textureRef.tilesY)
        }
        return LoadedTextureAsset(textureRef, atlasData)
    }

    override suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset {
        val tex = loadData(textureData2dRef.path)
        val texData = tex?.toArray()?.let { bytes ->
            ByteArrayInputStream(bytes).use {
                PlatformAssetsImpl.readImageData(it, MimeType.forFileName(textureData2dRef.path), textureData2dRef.props)
            }
        }
        return LoadedTextureAsset(textureData2dRef, texData)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        val blob = loadBlob(BlobAssetRef(audioRef.path))
        val clip = blob.data?.let { buf ->
            ByteArrayInputStream(buf.toArray()).use {
                AudioClipImpl(it, audioRef.path, KoolSystem.configAndroid.appContext)
            }
        }
        return LoadedAudioClipAsset(audioRef, clip)
    }
}