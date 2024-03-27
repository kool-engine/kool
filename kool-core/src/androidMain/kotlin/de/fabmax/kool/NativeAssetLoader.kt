package de.fabmax.kool

import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class NativeAssetLoader(val basePath: String) : AssetLoader() {
    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        return if (blobRef.isHttp) {
            loadHttpBlob(blobRef)
        } else {
            loadLocalBlob(blobRef)
        }
    }

    private suspend fun loadLocalBlob(localRawRef: BlobAssetRef): LoadedBlobAsset {
        var data: Uint8Buffer? = null
        withContext(Dispatchers.IO) {
            try {
                openLocalStream(localRawRef.path)?.use { data = Uint8BufferImpl(it.readBytes()) }
            } catch (e: Exception) {
                logE { "Failed loading asset ${localRawRef.path}: $e" }
            }
        }
        return LoadedBlobAsset(localRawRef, data)
    }

    private suspend fun loadHttpBlob(httpRawRef: BlobAssetRef): LoadedBlobAsset {
        var data: Uint8Buffer? = null
        logE { "loadHttpBlob not yet implemented" }
        return LoadedBlobAsset(httpRawRef, data)
    }

    private fun openLocalStream(assetPath: String): InputStream? {
        return try {
            val resPath = assetPath.replace('\\', '/')
            var inStream: InputStream? = this::class.java.classLoader?.getResourceAsStream(resPath)
            if (inStream == null) {
                logE { "openLocalStream: $assetPath" }
                // if asset wasn't found in resources try to load it from file system
                //inStream = FileInputStream("${basePath}/$assetPath")
            }
            inStream
        } catch (e: Exception) {
            logE { "Failed opening asset $assetPath: $e" }
            null
        }
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
//            imageAtlasTextureData(it, textureRef.tilesX, textureRef.tilesY)
            null
        }
        return LoadedTextureAsset(textureRef, atlasData)
    }

    override suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset {
        val data: TextureData2d? = withContext(Dispatchers.IO) {
            if (textureData2dRef.isHttp) {
                loadHttpTexture(textureData2dRef.path, textureData2dRef.props)
            } else {
                loadLocalTexture(textureData2dRef.path, textureData2dRef.props)
            }
        }
        return LoadedTextureAsset(textureData2dRef, data)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        logE { "loadAudioClip not yet implemented" }
        return LoadedAudioClipAsset(audioRef, null)
    }

    private fun loadLocalTexture(path: String, props: TextureProps?): TextureData2d? {
        return openLocalStream(path)?.let {
            try {
                PlatformAssetsImpl.readImageData(it, MimeType.forFileName(path), props)
            } catch (e: Exception) {
                logE { "Failed reading image at $path: $e" }
                null
            }
        }
    }

    private fun loadHttpTexture(path: String, props: TextureProps?): TextureData2d? {
        logE { "loadHttpTexture not yet implemented" }
        return null
    }
}