package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.platform.ImageAtlasTextureData
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.await
import org.w3c.dom.url.URL
import org.w3c.files.Blob

class FileSystemAssetLoaderJs(baseDir: FileSystemDirectory) : FileSystemAssetLoader(baseDir) {
    override suspend fun loadImageAtlas(ref: AssetRef.ImageAtlas): LoadedAsset.ImageAtlas {
        val result = loadData(ref.path).mapCatching { texData ->
            val bytes = (texData as Uint8BufferImpl).buffer
            val imgBlob = Blob(arrayOf(bytes))
            val bmp = createImageBitmap(imgBlob, ImageBitmapOptions(ref.resolveSize)).await()
            ImageAtlasTextureData(
                image = bmp,
                tilesX = ref.tilesX,
                tilesY = ref.tilesY,
                id = trimAssetPath(ref.path),
                format = ref.format
            )
        }
        return LoadedAsset.ImageAtlas(ref, result)
    }

    override suspend fun loadBufferedImage2d(ref: AssetRef.BufferedImage2d): LoadedAsset.BufferedImage2d {
        val imageBuffer = loadData(ref.path).mapCatching { data ->
            val mime = MimeType.forFileName(ref.path)
            val texData = PlatformAssetsImpl.loadImageFromBuffer(data, mime, ref.format, ref.resolveSize)
            BufferedImageData2d(
                ImageTextureData.imageBitmapToBuffer(texData.data, ref.format, ref.resolveSize),
                texData.width,
                texData.height,
                ref.format,
                trimAssetPath(ref.path)
            )
        }
        return LoadedAsset.BufferedImage2d(ref, imageBuffer)
    }

    override suspend fun loadAudio(ref: AssetRef.Audio): LoadedAsset.Audio {
        val clip = loadData(ref.path).mapCatching { audioData ->
            val bytes = (audioData as Uint8BufferImpl).buffer
            val audioBlob = Blob(arrayOf(bytes))
            val url = URL.createObjectURL(audioBlob)
            AudioClipImpl(url)
        }
        return LoadedAsset.Audio(ref, clip)
    }
}
