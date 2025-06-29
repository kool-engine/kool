package de.fabmax.kool

import com.github.weisj.jsvg.parser.LoaderContext
import com.github.weisj.jsvg.parser.SVGLoader
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoader
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoaderDesktop
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.FontMapGenerator
import de.fabmax.kool.platform.HttpCache
import de.fabmax.kool.platform.ImageDecoder
import de.fabmax.kool.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NFDFilterItem
import org.lwjgl.util.nfd.NativeFileDialog
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.math.roundToInt

internal actual fun PlatformAssets(): PlatformAssets = PlatformAssetsImpl

actual fun fileSystemAssetLoader(baseDir: FileSystemDirectory): FileSystemAssetLoader = FileSystemAssetLoaderDesktop(baseDir)

object PlatformAssetsImpl : PlatformAssets {

    private const val MAX_GENERATED_TEX_WIDTH = 2048
    private const val MAX_GENERATED_TEX_HEIGHT = 2048

    private val fontGenerator: FontMapGenerator by lazy {
        FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT).apply {
            loadCustomFonts(KoolSystem.configJvm.customTtfFonts)
        }
    }
    private var saveFileChooserPath = System.getProperty("user.home")
    private var loadFileChooserPath = System.getProperty("user.home")

    init {
        HttpCache.initCache(File(KoolSystem.configJvm.httpCacheDir))
    }

    override suspend fun waitForFonts() {
        // on JVM all fonts should be immediately available -> nothing to wait for
    }

    override fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): BufferedImageData2d {
        return fontGenerator.createFontMapData(font, fontScale, outMetrics)
    }

    override suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile> {
        return openFileChooser(filterList, multiSelect).map { LoadableFileImpl(it) }
    }

    override suspend fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String?,
        filterList: List<FileFilterItem>,
        mimeType: MimeType
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
                        val extensions = filterItem.fileExtensions.joinToString(",") { it.trim().removePrefix(".") }
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
                        // make sure file extensions do not contain leading '.' and are separated by ',' with no space
                        val extensions = filterItem.fileExtensions.joinToString(",") { it.trim().removePrefix(".") }
                        fileFilters[i].set(filterItem.name.toByteBuffer(), extensions.toByteBuffer())
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

    override suspend fun loadImageFromBuffer(
        texData: Uint8Buffer,
        mimeType: MimeType,
        format: TexFormat,
        resolveSize: Vec2i?
    ): BufferedImageData2d {
        return withContext(Dispatchers.IO) {
            readImageData(ByteArrayInputStream(texData.toArray()), mimeType, format, resolveSize)
        }
    }

    fun readImageData(inStream: InputStream, mimeType: MimeType, format: TexFormat, resolveSize: Vec2i?): BufferedImageData2d {
        return inStream.use {
            when (mimeType) {
                MimeType.IMAGE_SVG -> renderSvg(inStream, format, resolveSize)
                else -> ImageDecoder.loadImage(inStream, format, resolveSize)
            }
        }
    }

    private fun renderSvg(inStream: InputStream, format: TexFormat, resolveSize: Vec2i?): BufferedImageData2d {
        val svgDoc = SVGLoader().load(inStream, null, LoaderContext.createDefault()) ?: error("Failed loading SVG image")
        val size = svgDoc.size()
        val scaleX = if (resolveSize != null) resolveSize.x / size.width else 1f
        val scaleY = if (resolveSize != null) resolveSize.y / size.height else 1f

        val img = BufferedImage((size.width * scaleX).roundToInt(), (size.height * scaleY).roundToInt(), BufferedImage.TYPE_4BYTE_ABGR)
        val g = img.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.scale(scaleX.toDouble(), scaleY.toDouble())
        svgDoc.render(null, g)
        g.dispose()

        return ImageDecoder.loadBufferedImage(img, format, resolveSize)
    }
}

actual suspend fun decodeDataUri(dataUri: String): Uint8Buffer {
    return Uint8BufferImpl(dataUriToByteArray(dataUri))
}

internal fun dataUriToByteArray(dataUri: String): ByteArray {
    val dataIdx = dataUri.indexOf(";base64,") + 8
    return Base64.getDecoder().decode(dataUri.substring(dataIdx))
}
