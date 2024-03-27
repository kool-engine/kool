package de.fabmax.kool

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

actual fun fileSystemAssetLoader(baseDir: FileSystemDirectory): AssetLoader {
    TODO("Not yet implemented")
}

internal actual fun PlatformAssets(): PlatformAssets = PlatformAssetsImpl

object PlatformAssetsImpl : PlatformAssets {

    private const val MAX_GENERATED_TEX_WIDTH = 2048
    private const val MAX_GENERATED_TEX_HEIGHT = 2048

    //private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)
    private var saveFileChooserPath = System.getProperty("user.home")
    private var loadFileChooserPath = System.getProperty("user.home")

    init {
        //HttpCache.initCache(File(KoolSystem.configJvm.httpCacheDir))
        //fontGenerator.loadCustomFonts(KoolSystem.configJvm.customTtfFonts)
    }

    override suspend fun waitForFonts() {
        // on JVM all fonts should be immediately available -> nothing to wait for
    }

    override fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d {
        TODO()
        //return fontGenerator.createFontMapData(font, fontScale, outMetrics)
    }

    override suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile> {
        TODO()
        //return openFileChooser(filterList, multiSelect).map { LoadableFileImpl(it) }
    }

    override suspend fun saveFileByUser(
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
        TODO()
    }

    suspend fun saveFileChooser(defaultFileName: String? = null, filterList: List<FileFilterItem> = emptyList()): File? {
        TODO()
    }

    override suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String, props: TextureProps?): TextureData {
        return withContext(Dispatchers.IO) {
            readImageData(ByteArrayInputStream(texData.toArray()), mimeType, props)
        }
    }

    fun readImageData(inStream: InputStream, mimeType: String, props: TextureProps?): TextureData2d {
        return inStream.use {
            when (mimeType) {
                MimeType.IMAGE_SVG -> renderSvg(inStream, props)
                else -> BitmapFactory.decodeStream(inStream).toTextureData()
            }
        }
    }

    private fun renderSvg(inStream: InputStream, props: TextureProps?): TextureData2d {
        TODO()
    }

    private fun Bitmap.toTextureData(): TextureData2d {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        val buffer = Uint8Buffer(width * height * 4)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val px = pixels[y * width + x]
                val a = if (hasAlpha()) px shr 24 else 255
                val r = (px shr 16) and 0xff
                val g = (px shr 8) and 0xff
                val b = px  and 0xff
                buffer.put(r.toByte())
                buffer.put(g.toByte())
                buffer.put(b.toByte())
                buffer.put(a.toByte())
            }
        }

        return TextureData2d(buffer, width, height, TexFormat.RGBA)
    }
}

actual suspend fun decodeDataUri(dataUri: String): Uint8Buffer {
    val dataIdx = dataUri.indexOf(";base64,") + 8
    return Uint8BufferImpl(Base64.decode(dataUri.substring(dataIdx), Base64.DEFAULT))
}