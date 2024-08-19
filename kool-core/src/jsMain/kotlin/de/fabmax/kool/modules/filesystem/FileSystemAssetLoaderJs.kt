package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.platform.ImageAtlasTextureData
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.await
import org.w3c.dom.url.URL
import org.w3c.files.Blob

class FileSystemAssetLoaderJs(baseDir: FileSystemDirectory) : FileSystemAssetLoader(baseDir) {
    override suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val resize = textureRef.props?.resolveSize
        val result = loadData(textureRef.path).mapCatching { texData ->
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
        return LoadedTextureAsset(textureRef, result)
    }

    override suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset {
        val data = loadData(textureData2dRef.path).mapCatching { texData ->
            val mime = MimeType.forFileName(textureData2dRef.path)
            PlatformAssetsImpl.loadTextureDataFromBuffer(texData, mime, textureData2dRef.props)
        }
        return LoadedTextureAsset(textureData2dRef, data)
    }

    override suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset {
        val clip = loadData(audioRef.path).mapCatching { audioData ->
            val bytes = (audioData as Uint8BufferImpl).buffer
            val audioBlob = Blob(arrayOf(bytes))
            val url = URL.createObjectURL(audioBlob)
            AudioClipImpl(url)
        }
        return LoadedAudioClipAsset(audioRef, clip)
    }
}
