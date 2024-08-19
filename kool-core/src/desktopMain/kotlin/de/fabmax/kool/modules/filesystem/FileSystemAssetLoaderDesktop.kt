package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.platform.imageAtlasTextureData
import java.io.ByteArrayInputStream

class FileSystemAssetLoaderDesktop(baseDir: FileSystemDirectory): FileSystemAssetLoader(baseDir) {
    override suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val refCopy = TextureData2dRef(textureRef.path, textureRef.props)
        val result = loadTextureData2d(refCopy).result.mapCatching {
            imageAtlasTextureData(it as TextureData2d, textureRef.tilesX, textureRef.tilesY)
        }
        return LoadedTextureAsset(textureRef, result)
    }

    override suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset {
        val tex = loadData(textureData2dRef.path)
        val result = tex.mapCatching { buf ->
            ByteArrayInputStream(buf.toArray()).use {
                PlatformAssetsImpl.readImageData(it, MimeType.forFileName(textureData2dRef.path), textureData2dRef.props)
            }
        }
        return LoadedTextureAsset(textureData2dRef, result)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        val blob = loadBlob(BlobAssetRef(audioRef.path))
        val result = blob.result.mapCatching {
            AudioClipImpl(it.toArray(), audioRef.path.substringAfterLast('.').lowercase())
        }
        return LoadedAudioClipAsset(audioRef, result)
    }
}