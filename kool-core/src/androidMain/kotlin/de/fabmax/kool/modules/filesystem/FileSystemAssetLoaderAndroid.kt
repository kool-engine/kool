package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.platform.imageAtlasTextureData
import java.io.ByteArrayInputStream

class FileSystemAssetLoaderAndroid(baseDir: FileSystemDirectory): FileSystemAssetLoader(baseDir) {
    override suspend fun loadImageAtlas(ref: AssetRef.ImageAtlas): LoadedAsset.ImageAtlas {
        val refCopy = AssetRef.BufferedImage2d(ref.path, ref.format, ref.resolveSize)
        val result = loadBufferedImage2d(refCopy).result.mapCatching {
            imageAtlasTextureData(it, ref.tilesX, ref.tilesY)
        }
        return LoadedAsset.ImageAtlas(ref, result)
    }

    override suspend fun loadBufferedImage2d(ref: AssetRef.BufferedImage2d): LoadedAsset.BufferedImage2d {
        val tex = loadData(ref.path)
        val result = tex.mapCatching { buf ->
            ByteArrayInputStream(buf.toArray()).use {
                PlatformAssetsImpl.readImageData(it, MimeType.forFileName(ref.path), ref.format, ref.resolveSize)
            }
        }
        return LoadedAsset.BufferedImage2d(ref, result)
    }

    override suspend fun loadAudio(ref: AssetRef.Audio): LoadedAsset.Audio {
        val blob = loadBlob(AssetRef.Blob(ref.path))
        val result = blob.result.mapCatching { buf ->
            ByteArrayInputStream(buf.toArray()).use {
                AudioClipImpl(it, ref.path, KoolSystem.configAndroid.appContext)
            }
        }
        return LoadedAsset.Audio(ref, result)
    }
}