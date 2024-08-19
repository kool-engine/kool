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
    override suspend fun loadBlob(ref: AssetRef.Blob): LoadedAsset.Blob {
        val result = withContext(Dispatchers.IO) {
            try {
                val data = openStream(ref).use { Uint8BufferImpl(it.readBytes()) }
                Result.success(data)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
        return LoadedAsset.Blob(ref, result)
    }

    override suspend fun loadImage(ref: AssetRef.Image): LoadedAsset.Image {
        val refCopy = AssetRef.ImageBuffer(ref.path, ref.props)
        return LoadedAsset.Image(ref, loadImageBuffer(refCopy).result)
    }

    override suspend fun loadImageAtlas(ref: AssetRef.ImageAtlas): LoadedAsset.Image {
        val refCopy = AssetRef.ImageBuffer(ref.path, ref.props)
        val result = loadImageBuffer(refCopy).result.mapCatching {
            imageAtlasTextureData(it, ref.tilesX, ref.tilesY)
        }
        return LoadedAsset.Image(ref, result)
    }

    override suspend fun loadImageBuffer(ref: AssetRef.ImageBuffer): LoadedAsset.ImageBuffer {
        val data: Result<TextureData2d> = withContext(Dispatchers.IO) {
            loadTexture(ref, ref.props)
        }
        return LoadedAsset.ImageBuffer(ref, data)
    }

    override suspend fun loadAudio(ref: AssetRef.Audio): LoadedAsset.Audio {
        val blob = loadBlob(AssetRef.Blob(ref.path))
        return LoadedAsset.Audio(ref, blob.result.map {
            AudioClipImpl(it.toArray(), ref.path.substringAfterLast('.').lowercase())
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