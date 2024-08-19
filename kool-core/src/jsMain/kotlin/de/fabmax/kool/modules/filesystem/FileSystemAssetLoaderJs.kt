package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.ImageAtlasTextureData
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.await
import org.w3c.dom.url.URL
import org.w3c.files.Blob

class FileSystemAssetLoaderJs(baseDir: FileSystemDirectory) : FileSystemAssetLoader(baseDir) {
    override suspend fun loadImageAtlas(ref: AssetRef.ImageAtlas): LoadedAsset.Image {
        val resize = ref.props?.resolveSize
        val result = loadData(ref.path).mapCatching { texData ->
            val bytes = (texData as Uint8BufferImpl).buffer
            val imgBlob = Blob(arrayOf(bytes))
            val bmp = createImageBitmap(imgBlob, ImageBitmapOptions(resize)).await()
            ImageAtlasTextureData(bmp, ref.tilesX, ref.tilesY, ref.props?.format)
        }
        return LoadedAsset.Image(ref, result)
    }

    override suspend fun loadImageBuffer(ref: AssetRef.ImageBuffer): LoadedAsset.ImageBuffer {
        val imageBuffer = loadData(ref.path).mapCatching { data ->
            val mime = MimeType.forFileName(ref.path)
            val props = ref.props ?: TextureProps()
            val texData = PlatformAssetsImpl.loadTextureDataFromBuffer(data, mime, props)
            TextureData2d(
                ImageTextureData.imageBitmapToBuffer(texData.data, props),
                texData.width,
                texData.height,
                props.format
            )
        }
        return LoadedAsset.ImageBuffer(ref, imageBuffer)
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
