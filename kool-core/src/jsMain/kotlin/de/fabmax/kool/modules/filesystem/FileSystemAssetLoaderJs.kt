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

class FileSystemAssetLoaderJs(baseDir: FileSystemDirectory): FileSystemAssetLoader(baseDir) {
    override suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val resize = textureRef.props?.resolveSize
        val data = loadData(textureRef.path)?.let { texData ->
            val bytes = (texData as Uint8BufferImpl).buffer
            val imgBlob = Blob(arrayOf(bytes))
            val bmp = createImageBitmap(imgBlob, ImageBitmapOptions(resize)).await()
            ImageAtlasTextureData(
                bmp,
                textureRef.tilesX,
                textureRef.tilesY,
                textureRef.props?.format
            )
        }
        return LoadedTextureAsset(textureRef, data)
    }

    override suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset {
        val refCopy = TextureAssetRef(textureData2dRef.path, textureData2dRef.props)
        val texData = loadTexture(refCopy).data?.let { bmpData ->
            val props = textureData2dRef.props ?: TextureProps()
            val texData = bmpData as ImageTextureData
            val buffer = ImageTextureData.imageBitmapToBuffer(texData.data, props)

            TextureData2d(
                buffer,
                texData.width,
                texData.height,
                props.format
            )
        }
        return LoadedTextureAsset(textureData2dRef, texData)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        val clip = loadData(audioRef.path)?.let { audioData ->
            val bytes = (audioData as Uint8BufferImpl).buffer
            val audioBlob = Blob(arrayOf(bytes))
            val url = URL.createObjectURL(audioBlob)
            AudioClipImpl(url)
        }
        return LoadedAudioClipAsset(audioRef, clip)
    }
}
