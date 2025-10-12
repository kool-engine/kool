package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.HttpCache
import de.fabmax.kool.platform.imageAtlasTextureData
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

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

    override suspend fun loadImage2d(ref: AssetRef.Image2d): LoadedAsset.Image2d {
        val refCopy = AssetRef.BufferedImage2d(ref.path, ref.format, ref.resolveSize)
        return LoadedAsset.Image2d(ref, loadBufferedImage2d(refCopy).result)
    }

    override suspend fun loadImageAtlas(ref: AssetRef.ImageAtlas): LoadedAsset.ImageAtlas {
        val refCopy = AssetRef.BufferedImage2d(ref.path, ref.format, ref.resolveSize)
        val result = loadBufferedImage2d(refCopy).result.mapCatching {
            imageAtlasTextureData(it, ref.tilesX, ref.tilesY)
        }
        return LoadedAsset.ImageAtlas(ref, result)
    }

    override suspend fun loadBufferedImage2d(ref: AssetRef.BufferedImage2d): LoadedAsset.BufferedImage2d {
        val data: Result<BufferedImageData2d> = withContext(Dispatchers.IO) {
            loadTexture(ref, ref.format, ref.resolveSize)
        }
        return LoadedAsset.BufferedImage2d(ref, data)
    }

    override suspend fun loadAudio(ref: AssetRef.Audio): LoadedAsset.Audio {
        val blob = loadBlob(AssetRef.Blob(ref.path))
        return LoadedAsset.Audio(ref, blob.result.map {
            AudioClipImpl(it.toArray(), ref.path.substringAfterLast('.').lowercase())
        })
    }

    private fun loadTexture(assetRef: AssetRef, format: TexFormat, resolveSize: Vec2i?): Result<BufferedImageData2d> {
        return try {
            openStream(assetRef).use {
                Result.success(PlatformAssetsImpl.readImageData(it, MimeType.forFileName(assetRef.path), format, resolveSize))
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
            val file = File("${basePath}/${assetRef.path}")
            if (!file.canRead()) {
                throw FileNotFoundException("File not found: ${file.absolutePath} (asset base path: ${File(basePath).absolutePath}, asset path: ${assetRef.path})")
            }
            inStream = FileInputStream(file)
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