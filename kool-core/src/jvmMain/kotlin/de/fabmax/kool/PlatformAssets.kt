package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lwjgl.PointerBuffer
import org.lwjgl.util.nfd.NativeFileDialog
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import javax.imageio.ImageIO

actual object PlatformAssets {

    private const val MAX_GENERATED_TEX_WIDTH = 2048
    private const val MAX_GENERATED_TEX_HEIGHT = 2048

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)
    private val imageIoLock = Any()
    private var fileChooserPath = System.getProperty("user.home")

    init {
        HttpCache.initCache(File(KoolSetup.config.httpCacheDir))
        fontGenerator.loadCustomFonts(KoolSetup.config.customTtfFonts)
    }

    internal actual suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        return if (blobRef.isHttp) {
            loadHttpBlob(blobRef)
        } else {
            loadLocalBlob(blobRef)
        }
    }

    private suspend fun loadLocalBlob(localRawRef: BlobAssetRef): LoadedBlobAsset {
        var data: Uint8BufferImpl? = null
        withContext(Dispatchers.IO) {
            try {
                openLocalStream(localRawRef.path).use { data = Uint8BufferImpl(it.readBytes()) }
            } catch (e: Exception) {
                logE { "Failed loading asset ${localRawRef.path}: $e" }
            }
        }
        return LoadedBlobAsset(localRawRef, data)
    }

    private suspend fun loadHttpBlob(httpRawRef: BlobAssetRef): LoadedBlobAsset {
        var data: Uint8BufferImpl? = null
        if (httpRawRef.path.startsWith("data:", true)) {
            data = decodeDataUrl(httpRawRef.path)
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val f = HttpCache.loadHttpResource(httpRawRef.path)
                        ?: throw IOException("Failed downloading ${httpRawRef.path}")
                    FileInputStream(f).use { data = Uint8BufferImpl(it.readBytes()) }
                } catch (e: Exception) {
                    logE { "Failed loading asset ${httpRawRef.path}: $e" }
                }
            }
        }
        return LoadedBlobAsset(httpRawRef, data)
    }

    private fun decodeDataUrl(dataUrl: String): Uint8BufferImpl {
        val dataIdx = dataUrl.indexOf(";base64,") + 8
        return Uint8BufferImpl(Base64.getDecoder().decode(dataUrl.substring(dataIdx)))
    }

    fun openLocalStream(assetPath: String): InputStream {
        var resPath = assetPath.replace('\\', '/')
        if (resPath.startsWith("/")) {
            resPath = resPath.substring(1)
        }
        var inStream = ClassLoader.getSystemResourceAsStream(resPath)
        if (inStream == null) {
            // if asset wasn't found in resources try to load it from file system
            inStream = FileInputStream("${KoolSetup.config.assetPath}/$assetPath")
        }
        return inStream
    }

    internal actual suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        var data: ImageTextureData? = null
        withContext(Dispatchers.IO) {
            try {
                data = if (textureRef.isHttp) {
                    loadHttpTexture(textureRef.path, textureRef.fmt)
                } else {
                    loadLocalTexture(textureRef.path, textureRef.fmt)
                }
            } catch (e: Exception) {
                logE { "Failed loading texture ${textureRef.path}: $e" }
            }
        }
        return LoadedTextureAsset(textureRef, data)
    }

    internal actual suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        var data: ImageTextureData? = null
        withContext(Dispatchers.IO) {
            try {
                data = if (textureRef.isHttp) {
                    loadHttpTexture(textureRef.path, textureRef.fmt)
                } else {
                    loadLocalTexture(textureRef.path, textureRef.fmt)
                }
            } catch (e: Exception) {
                logE { "Failed loading texture ${textureRef.path}: $e" }
            }
        }
        return LoadedTextureAsset(textureRef, ImageAtlasTextureData(data!!, textureRef.tilesX, textureRef.tilesY))
    }

    private fun loadLocalTexture(path: String, format: TexFormat?): ImageTextureData {
        return openLocalStream(path).use {
            // ImageIO.read is not thread safe!
            val img = synchronized(imageIoLock) { ImageIO.read(it) }
            ImageTextureData(img, format)
        }
    }

    private fun loadHttpTexture(path: String, format: TexFormat?): ImageTextureData {
        val f = HttpCache.loadHttpResource(path)!!
        return FileInputStream(f).use {
            // ImageIO.read is not thread safe!
            val img = synchronized(imageIoLock) { ImageIO.read(it) }
            ImageTextureData(img, format)
        }
    }

    internal actual suspend fun waitForFonts() {
        // on JVM all fonts should be immediately available -> nothing to wait for
    }

    internal actual fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d {
        return fontGenerator.createFontMapData(font, fontScale, outMetrics)
    }

    internal actual suspend fun loadFileByUser(filterList: String?): LoadedFile? {
        chooseFile(filterList)?.let { file ->
            try {
                return LoadedFile(
                    file.absolutePath,
                    Uint8BufferImpl(file.readBytes())
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun chooseFile(filterList: String? = null): File? {
        val outPath = PointerBuffer.allocateDirect(1)
        val result = NativeFileDialog.NFD_OpenDialog(filterList, fileChooserPath, outPath)
        if (result == NativeFileDialog.NFD_OKAY) {
            val file = File(outPath.stringUTF8)
            fileChooserPath = file.parent
            return file
        }
        return null
    }

    internal actual fun saveFileByUser(data: Uint8Buffer, fileName: String, mimeType: String): String? {
        val outPath = PointerBuffer.allocateDirect(1)
        val result = NativeFileDialog.NFD_SaveDialog(null, fileChooserPath, outPath)
        if (result == NativeFileDialog.NFD_OKAY) {
            val file = File(outPath.stringUTF8)
            file.parentFile.mkdirs()
            fileChooserPath = file.parent
            try {
                FileOutputStream(file).use { it.write(data.toArray()) }
                return file.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    internal actual suspend fun loadTextureData2d(imagePath: String, format: TexFormat?): TextureData2d {
        // JVM implementation always loads images as ImageTextureData, which is a subclass of TextureData2d
        return Assets.loadTextureData(imagePath, format) as ImageTextureData
    }

    internal actual suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String): TextureData {
        var img: BufferedImage?
        withContext(Dispatchers.IO) {
            img = synchronized(imageIoLock) {
                ImageIO.read(ByteArrayInputStream(texData.toArray()))
            }
        }
        return ImageTextureData(img!!, null)
    }

    internal actual suspend fun uploadTextureToGpu(texture: Texture, texData: TextureData) {
        withContext(Dispatchers.RenderLoop) {
            val ctx = KoolContext.requireContext() as Lwjgl3Context
            ctx.renderBackend.uploadTextureToGpu(texture, texData)
        }
    }

    internal actual suspend fun loadAudioClip(assetPath: String): AudioClip {
        val asset = Assets.loadBlobAsset(assetPath)
        return AudioClip(asset.toArray())
    }
}