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
import org.lwjgl.util.nfd.NFDFilterItem
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
    private var saveFileChooserPath = System.getProperty("user.home")
    private var loadFileChooserPath = System.getProperty("user.home")

    init {
        HttpCache.initCache(File(KoolSystem.config.httpCacheDir))
        fontGenerator.loadCustomFonts(KoolSystem.config.customTtfFonts)
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
        val resPath = (KoolSystem.config.classloaderAssetPath + "/" + assetPath.replace('\\', '/'))
            .removePrefix("/")
        var inStream = ClassLoader.getSystemResourceAsStream(resPath)
        if (inStream == null) {
            // if asset wasn't found in resources try to load it from file system
            inStream = FileInputStream("${Assets.assetsBasePath}/$assetPath")
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

    internal actual suspend fun loadFileByUser(filterList: List<FileFilterItem>): LoadedFile? {
        loadFileChooser(filterList)?.let { loadFile ->
            try {
                return LoadedFile(loadFile.absolutePath, Uint8BufferImpl(loadFile.readBytes()))
            } catch (e: IOException) {
                logE { "Loading file $loadFile failed: $e" }
                e.printStackTrace()
            }
        }
        return null
    }

    internal actual fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String?,
        filterList: List<FileFilterItem>,
        mimeType: String
    ): String? {
        return saveFileChooser(defaultFileName, filterList)?.let { saveFile ->
            saveFile.parentFile?.mkdirs()
            try {
                saveFile.writeBytes(data.toArray())
            } catch (e: IOException) {
                logE { "Saving file $saveFile failed: $e" }
                e.printStackTrace()
            }
            saveFile.absolutePath
        }
    }

    fun loadFileChooser(filterList: List<FileFilterItem> = emptyList()): File? {
        memStack {
            val outPath = callocPointer(1)
            var fileFilters: NFDFilterItem.Buffer? = null
            if (filterList.isNotEmpty()) {
                fileFilters = NFDFilterItem.calloc(filterList.size)
                filterList.forEachIndexed { i, filterItem ->
                    fileFilters[i].set(filterItem.name.toByteBuffer(), filterItem.fileExtensions.toByteBuffer())
                }
            }

            val result = NativeFileDialog.NFD_OpenDialog(outPath, fileFilters, loadFileChooserPath)
            return if (result == NativeFileDialog.NFD_OKAY) {
                val file = File(outPath.stringUTF8)
                loadFileChooserPath = file.parent
                file
            } else {
                null
            }
        }
    }

    fun saveFileChooser(defaultFileName: String? = null, filterList: List<FileFilterItem> = emptyList()): File? {
        memStack {
            val outPath = callocPointer(1)
            var fileFilters: NFDFilterItem.Buffer? = null
            if (filterList.isNotEmpty()) {
                fileFilters = NFDFilterItem.calloc(filterList.size)
                filterList.forEachIndexed { i, filterItem ->
                    fileFilters[i].set(filterItem.name.toByteBuffer(), filterItem.fileExtensions.toByteBuffer())
                }
            }

            val result = NativeFileDialog.NFD_SaveDialog(outPath, fileFilters, saveFileChooserPath, defaultFileName)
            return if (result == NativeFileDialog.NFD_OKAY) {
                val file = File(outPath.stringUTF8)
                saveFileChooserPath = file.parent
                file
            } else {
                null
            }
        }
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
            val ctx = KoolSystem.requireContext() as Lwjgl3Context
            ctx.renderBackend.uploadTextureToGpu(texture, texData)
        }
    }

    internal actual suspend fun loadAudioClip(assetPath: String): AudioClip {
        val asset = Assets.loadBlobAsset(assetPath)
        return AudioClip(asset.toArray())
    }
}