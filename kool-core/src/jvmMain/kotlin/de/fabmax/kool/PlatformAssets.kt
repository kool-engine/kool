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
import org.lwjgl.system.MemoryUtil
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

    internal actual suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile> {
        return openFileChooser(filterList, multiSelect).map { LoadableFile(it) }
    }

    internal actual suspend fun saveFileByUser(
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

    suspend fun openFileChooser(filterList: List<FileFilterItem> = emptyList(), multiSelect: Boolean = false): List<File> {
        // apparently file dialog functions need to be called from main thread
        // unfortunately, this means the main loop is blocked while the dialog is open
        return withContext(Dispatchers.RenderLoop) {
            memStack {
                var fileFilters: NFDFilterItem.Buffer? = null
                if (filterList.isNotEmpty()) {
                    fileFilters = NFDFilterItem.calloc(filterList.size)
                    filterList.forEachIndexed { i, filterItem ->
                        // make sure file extensions do not contain leading '.' and are separated by ',' with no space
                        val extensions = filterItem.fileExtensions
                            .split(',')
                            .joinToString(",") { it.trim().removePrefix(".") }
                        fileFilters[i].set(filterItem.name.toByteBuffer(), extensions.toByteBuffer())
                    }
                }

                val files = mutableListOf<File>()
                val outPath = callocPointer(1)
                if (multiSelect) {
                    val result = NativeFileDialog.NFD_OpenDialogMultiple(outPath, fileFilters, loadFileChooserPath)
                    if (result == NativeFileDialog.NFD_OKAY) {
                        val pathSetPtr = outPath.get(0)
                        val count = IntArray(1)
                        NativeFileDialog.NFD_PathSet_GetCount(pathSetPtr, count)
                        for (i in 0 until count[0]) {
                            if (NativeFileDialog.NFD_PathSet_GetPath(pathSetPtr, i, outPath) == NativeFileDialog.NFD_OKAY) {
                                files += File(outPath.getStringUTF8(0))
                                MemoryUtil.memFree(outPath)
                            }
                        }
                        NativeFileDialog.NFD_PathSet_Free(pathSetPtr)
                    }
                } else {
                    val result = NativeFileDialog.NFD_OpenDialog(outPath, fileFilters, loadFileChooserPath)
                    if (result == NativeFileDialog.NFD_OKAY) {
                        files += File(outPath.getStringUTF8(0))
                        MemoryUtil.memFree(outPath)
                    }
                }

                if (files.isNotEmpty()) {
                    loadFileChooserPath = files.first().parent
                }
                files
            }
        }
    }

    suspend fun saveFileChooser(defaultFileName: String? = null, filterList: List<FileFilterItem> = emptyList()): File? {
        // apparently file dialog functions need to be called from main thread
        // unfortunately, this means the main loop is blocked while the dialog is open
        return withContext(Dispatchers.RenderLoop) {
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
                if (result == NativeFileDialog.NFD_OKAY) {
                    val file = File(outPath.stringUTF8)
                    saveFileChooserPath = file.parent
                    file
                } else {
                    null
                }
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