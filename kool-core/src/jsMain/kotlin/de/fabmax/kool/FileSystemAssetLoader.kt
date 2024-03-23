package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.modules.filesystem.getFileOrNull
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.ImageAtlasTextureData
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.await
import org.w3c.dom.url.URL
import org.w3c.files.Blob

actual fun fileSystemAssetLoader(baseDir: FileSystemDirectory): AssetLoader {
    return FileSystemAssetLoader(baseDir)
}

class FileSystemAssetLoader(val baseDir: FileSystemDirectory): AssetLoader() {
    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        val blob = loadData(blobRef.path)
        return LoadedBlobAsset(blobRef, blob)
    }

    override suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        val data = loadData(textureRef.path)?.let { texData ->
            PlatformAssetsImpl.loadTextureDataFromBuffer(texData, MimeType.forFileName(textureRef.path), textureRef.props)
        }
        return LoadedTextureAsset(textureRef, data)
    }

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

    private suspend fun loadData(path: String): Uint8Buffer? {
        return if (Assets.isDataUri(path)) {
            decodeDataUri(path)
        } else {
            baseDir.getFileOrNull(path)?.read()
        }
    }
}