package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.HttpCache
import de.fabmax.kool.platform.imageAtlasTextureData
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class NativeAssetLoader(val basePath: String) : AssetLoader() {
    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        return if (blobRef.isHttp) {
            loadHttpBlob(blobRef)
        } else {
            loadLocalBlob(blobRef)
        }
    }

    private suspend fun loadLocalBlob(localRawRef: BlobAssetRef): LoadedBlobAsset {
        var data: Uint8BufferImpl? = null
        withContext(Dispatchers.IO) {
            try {
                openLocalStream(localRawRef.path).use { data = Uint8BufferImpl(it.readBytes()) }
            } catch (e: Exception) {
                logE { "Failed loading asset ${localRawRef.path}: $e" }
            }
        }
        return LoadedBlobAsset(localRawRef, data)
    }

    private suspend fun loadHttpBlob(httpRawRef: BlobAssetRef): LoadedBlobAsset {
        var data: Uint8BufferImpl? = null
        if (httpRawRef.path.startsWith("data:", true)) {
            data = decodeDataUrl(httpRawRef.path)
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val f = HttpCache.loadHttpResource(httpRawRef.path)
                        ?: throw IOException("Failed downloading ${httpRawRef.path}")
                    FileInputStream(f).use { data = Uint8BufferImpl(it.readBytes()) }
                } catch (e: Exception) {
                    logE { "Failed loading asset ${httpRawRef.path}: $e" }
                }
            }
        }
        return LoadedBlobAsset(httpRawRef, data)
    }

    private fun decodeDataUrl(dataUrl: String): Uint8BufferImpl {
        val dataIdx = dataUrl.indexOf(";base64,") + 8
        return Uint8BufferImpl(Base64.getDecoder().decode(dataUrl.substring(dataIdx)))
    }

    private fun openLocalStream(assetPath: String): InputStream {
        val resPath = (KoolSystem.configJvm.classloaderAssetPath + "/" + assetPath.replace('\\', '/'))
            .removePrefix("/")
        var inStream = this::class.java.classLoader.getResourceAsStream(resPath)
        if (inStream == null) {
            // if asset wasn't found in resources try to load it from file system
            inStream = FileInputStream("${basePath}/$assetPath")
        }
        return inStream
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
        var data: TextureData2d? = null
        withContext(Dispatchers.IO) {
            try {
                data = if (textureData2dRef.isHttp) {
                    loadHttpTexture(textureData2dRef.path, textureData2dRef.props)
                } else {
                    loadLocalTexture(textureData2dRef.path, textureData2dRef.props)
                }
            } catch (e: Exception) {
                logE { "Failed loading texture ${textureData2dRef.path}: $e" }
            }
        }
        return LoadedTextureAsset(textureData2dRef, data)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        val blob = loadBlob(BlobAssetRef(audioRef.path))
        val clip = blob.data?.let { buf ->
            AudioClipImpl(buf.toArray(), audioRef.path.substringAfterLast('.').lowercase())
        }
        return LoadedAudioClipAsset(audioRef, clip)
    }

    private fun loadLocalTexture(path: String, props: TextureProps?): TextureData2d {
        return PlatformAssetsImpl.readImageData(openLocalStream(path), MimeType.forFileName(path), props)
    }

    private fun loadHttpTexture(path: String, props: TextureProps?): TextureData2d {
        val f = HttpCache.loadHttpResource(path)!!
        return PlatformAssetsImpl.readImageData(FileInputStream(f), MimeType.forFileName(path), props)
    }
}