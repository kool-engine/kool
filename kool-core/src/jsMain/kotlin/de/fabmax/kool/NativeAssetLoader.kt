package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.ImageAtlasTextureData
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logE
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap

class NativeAssetLoader(val basePath: String) : AssetLoader() {
    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        val url = blobRef.path
        val prefixedUrl = if (Assets.isHttpAsset(url)) url else "${basePath}/$url"
        val result = fetchData(prefixedUrl).map { Uint8BufferImpl(Uint8Array(it.arrayBuffer().await())) }
        return LoadedBlobAsset(blobRef, result)
    }

    override suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        val resolveSz = textureRef.props?.resolveSize
        val result = loadImageBitmap(textureRef.path, textureRef.isHttp, resolveSz).map {
            ImageTextureData(it, textureRef.props?.format)
        }
        return LoadedTextureAsset(textureRef, result)
    }

    override suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val resolveSz = textureRef.props?.resolveSize
        val result = loadImageBitmap(textureRef.path, textureRef.isHttp, resolveSz).map {
            ImageAtlasTextureData(it, textureRef.tilesX, textureRef.tilesY, textureRef.props?.format)
        }
        return LoadedTextureAsset(textureRef, result)
    }

    override suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset {
        val props = textureData2dRef.props ?: TextureProps()
        val texRef = TextureAssetRef(textureData2dRef.path, props)


        val result = loadTexture(texRef).result.mapCatching {
            val texData = it.data as ImageTextureData
            TextureData2d(
                ImageTextureData.imageBitmapToBuffer(texData.data, props),
                texData.width,
                texData.height,
                props.format
            )
        }
        return LoadedTextureAsset(textureData2dRef, result)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        val assetPath = audioRef.path
        val clip = if (Assets.isHttpAsset(assetPath)) {
            AudioClipImpl(assetPath)
        } else {
            AudioClipImpl("${basePath}/$assetPath")
        }
        return LoadedAudioClipAsset(audioRef, Result.success(clip))
    }

    private suspend fun loadImageBitmap(path: String, isHttp: Boolean, resize: Vec2i?): Result<ImageBitmap> {
        val mime = MimeType.forFileName(path)
        val prefixedUrl = if (isHttp) path else "${basePath}/${path}"

        return if (mime != MimeType.IMAGE_SVG) {
            // raster image type -> fetch blob and create ImageBitmap directly
            fetchData(prefixedUrl).mapCatching {
                val imgBlob = it.blob().await()
                createImageBitmap(imgBlob, ImageBitmapOptions(resize)).await()
            }

        } else {
            // svg image -> use an Image element to convert it to an ImageBitmap
            val deferredBitmap = CompletableDeferred<ImageBitmap>()
            val img = resize?.let { Image(it.x, it.y) } ?: Image()
            img.onload = {
                createImageBitmap(img, ImageBitmapOptions(resize)).then { bmp -> deferredBitmap.complete(bmp) }
            }
            img.onerror = { _, _, _, _, _ ->
                deferredBitmap.completeExceptionally(IllegalStateException("Failed loading tex from $prefixedUrl"))
            }
            img.crossOrigin = ""
            img.src = prefixedUrl
            try {
                Result.success(deferredBitmap.await())
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }

    private suspend fun fetchData(path: String): Result<Response> {
        val response = fetch(path).await()
        return if (!response.ok) {
            logE { "Failed loading resource $path: ${response.status} ${response.statusText}" }
            Result.failure(IllegalStateException("Failed loading resource $path: ${response.status} ${response.statusText}"))
        } else {
            Result.success(response)
        }
    }
}