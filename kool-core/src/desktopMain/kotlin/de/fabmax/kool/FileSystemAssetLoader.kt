package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.modules.filesystem.getFileOrNull
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.platform.imageAtlasTextureData
import java.io.ByteArrayInputStream

actual fun fileSystemAssetLoader(baseDir: FileSystemDirectory): AssetLoader {
    return FileSystemAssetLoader(baseDir)
}

class FileSystemAssetLoader(val baseDir: FileSystemDirectory): AssetLoader() {
    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        val blob = baseDir.getFileOrNull(blobRef.path)
        return LoadedBlobAsset(blobRef, blob?.read())
    }

    override suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        val refCopy = TextureData2dRef(textureRef.path, textureRef.props)
        val texData = loadTextureData2d(refCopy).data as TextureData2d?
        return LoadedTextureAsset(textureRef, texData)
    }

    override suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val refCopy = TextureData2dRef(textureRef.path, textureRef.props)
        val texData = loadTextureData2d(refCopy).data as TextureData2d?
        val atlasData = texData?.let {
            imageAtlasTextureData(it, textureRef.tilesX, textureRef.tilesY)
        }
        return LoadedTextureAsset(textureRef, atlasData)
    }

    override suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset {
        val tex = baseDir.getFileOrNull(textureData2dRef.path)
        val texData = tex?.read()?.toArray()?.let { bytes ->
            ByteArrayInputStream(bytes).use {
                PlatformAssetsImpl.readImageData(it, MimeType.forFileName(textureData2dRef.path), textureData2dRef.props)
            }
        }
        return LoadedTextureAsset(textureData2dRef, texData)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        val blob = loadBlob(BlobAssetRef(audioRef.path))
        val clip = blob.data?.let { buf ->
            AudioClipImpl(buf.toArray(), audioRef.path.substringAfterLast('.').lowercase())
        }
        return LoadedAudioClipAsset(audioRef, clip)
    }
}