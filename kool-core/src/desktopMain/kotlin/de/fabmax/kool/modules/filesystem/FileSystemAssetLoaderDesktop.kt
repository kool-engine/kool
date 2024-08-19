package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.AssetRef
import de.fabmax.kool.LoadedAsset
import de.fabmax.kool.MimeType
import de.fabmax.kool.PlatformAssetsImpl
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.platform.imageAtlasTextureData
import java.io.ByteArrayInputStream

class FileSystemAssetLoaderDesktop(baseDir: FileSystemDirectory): FileSystemAssetLoader(baseDir) {
    override suspend fun loadImageAtlas(ref: AssetRef.ImageAtlas): LoadedAsset.Image {
        val refCopy = AssetRef.ImageBuffer(ref.path, ref.props)
        val result = loadImageBuffer(refCopy).result.mapCatching {
            imageAtlasTextureData(it, ref.tilesX, ref.tilesY)
        }
        return LoadedAsset.Image(ref, result)
    }

    override suspend fun loadImageBuffer(ref: AssetRef.ImageBuffer): LoadedAsset.ImageBuffer {
        val tex = loadData(ref.path)
        val result = tex.mapCatching { buf ->
            ByteArrayInputStream(buf.toArray()).use {
                PlatformAssetsImpl.readImageData(it, MimeType.forFileName(ref.path), ref.props)
            }
        }
        return LoadedAsset.ImageBuffer(ref, result)
    }

    override suspend fun loadAudio(ref: AssetRef.Audio): LoadedAsset.Audio {
        val blob = loadBlob(AssetRef.Blob(ref.path))
        val result = blob.result.mapCatching {
            AudioClipImpl(it.toArray(), ref.path.substringAfterLast('.').lowercase())
        }
        return LoadedAsset.Audio(ref, result)
    }
}