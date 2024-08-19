package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.HttpCache
import de.fabmax.kool.platform.imageAtlasTextureData
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

class NativeAssetLoader(val basePath: String) : AssetLoader() {
    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        val result = withContext(Dispatchers.IO) {
            try {
                val data = openStream(blobRef).use { Uint8BufferImpl(it.readBytes()) }
                Result.success(data)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
        return LoadedBlobAsset(blobRef, result)
    }

    override suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        val refCopy = TextureData2dRef(textureRef.path, textureRef.props)
        val result = loadTextureData2d(refCopy).result.mapCatching { it as TextureData2d }
        return LoadedTextureAsset(textureRef, result)
    }

    override suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val refCopy = TextureData2dRef(textureRef.path, textureRef.props)
        val result = loadTextureData2d(refCopy).result.mapCatching {
            imageAtlasTextureData(it as TextureData2d, textureRef.tilesX, textureRef.tilesY)
        }
        return LoadedTextureAsset(textureRef, result)
    }

    override suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset {
        val data: Result<TextureData2d> = withContext(Dispatchers.IO) {
            loadTexture(textureData2dRef, textureData2dRef.props)
        }
        return LoadedTextureAsset(textureData2dRef, data)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        val blob = loadBlob(BlobAssetRef(audioRef.path))
        return LoadedAudioClipAsset(audioRef, blob.result.map {
            AudioClipImpl(it.toArray(), audioRef.path.substringAfterLast('.').lowercase())
        })
    }

    private fun loadTexture(assetRef: AssetRef, props: TextureProps?): Result<TextureData2d> {
        return try {
            openStream(assetRef).use {
                Result.success(PlatformAssetsImpl.readImageData(it, MimeType.forFileName(assetRef.path), props))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    private fun openStream(assetRef: AssetRef): InputStream =
        if (assetRef.isHttp) openHttpStream(assetRef) else openLocalStream(assetRef)

    private fun openLocalStream(assetRef: AssetRef): InputStream {
        val resPath = (KoolSystem.configJvm.classloaderAssetPath + "/" + assetRef.path.replace('\\', '/'))
            .removePrefix("/")
        var inStream = this::class.java.classLoader.getResourceAsStream(resPath)
        if (inStream == null) {
            // if asset wasn't found in resources try to load it from file system
            inStream = FileInputStream("${basePath}/${assetRef.path}")
        }
        return inStream
    }

    private fun openHttpStream(assetRef: AssetRef): InputStream {
        return if (assetRef.path.startsWith("data:", true)) {
            ByteArrayInputStream(dataUriToByteArray(assetRef.path))
        } else {
            HttpCache.loadHttpResource(assetRef.path)?.let { f -> FileInputStream(f) }
                ?: throw FileNotFoundException("Failed loading HTTP asset: ${assetRef.path}")
        }
    }
}