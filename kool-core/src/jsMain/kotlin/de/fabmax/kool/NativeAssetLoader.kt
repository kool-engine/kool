package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
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
    override suspend fun loadBlob(ref: AssetRef.Blob): LoadedAsset.Blob {
        val url = ref.path
        val prefixedUrl = if (Assets.isHttpAsset(url)) url else "${basePath}/$url"
        val result = fetchData(prefixedUrl).map { Uint8BufferImpl(Uint8Array(it.arrayBuffer().await())) }
        return LoadedAsset.Blob(ref, result)
    }

    override suspend fun loadImage2d(ref: AssetRef.Image2d): LoadedAsset.Image2d {
        val resolveSz = ref.props?.resolveSize
        val result = loadImageBitmap(ref.path, ref.isHttp, resolveSz).map {
            ImageTextureData(it, ref.props?.format ?: TexFormat.RGBA)
        }
        return LoadedAsset.Image2d(ref, result)
    }

    override suspend fun loadImageAtlas(ref: AssetRef.ImageAtlas): LoadedAsset.ImageAtlas {
        val resolveSz = ref.props?.resolveSize
        val result = loadImageBitmap(ref.path, ref.isHttp, resolveSz).map {
            ImageAtlasTextureData(it, ref.tilesX, ref.tilesY, ref.props?.format ?: TexFormat.RGBA)
        }
        return LoadedAsset.ImageAtlas(ref, result)
    }

    override suspend fun loadBufferedImage2d(ref: AssetRef.BufferedImage2d): LoadedAsset.BufferedImage2d {
        val props = ref.props ?: TextureProps()
        val texRef = AssetRef.Image2d(ref.path, props)
        val result = loadImage2d(texRef).result.mapCatching {
            val texData = it as ImageTextureData
            BufferedImageData2d(
                ImageTextureData.imageBitmapToBuffer(texData.data, props),
                texData.width,
                texData.height,
                props.format
            )
        }
        return LoadedAsset.BufferedImage2d(ref, result)
    }

    override suspend fun loadAudio(ref: AssetRef.Audio): LoadedAsset.Audio {
        val assetPath = ref.path
        val clip = if (Assets.isHttpAsset(assetPath)) {
            AudioClipImpl(assetPath)
        } else {
            AudioClipImpl("${basePath}/$assetPath")
        }
        return LoadedAsset.Audio(ref, Result.success(clip))
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