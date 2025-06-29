package de.fabmax.kool

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Base64
import com.caverock.androidsvg.SVG
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoader
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoaderAndroid
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.ImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.HttpCache
import de.fabmax.kool.util.AtlasFont
import de.fabmax.kool.util.CharMetrics
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import kotlin.math.ceil

internal actual fun PlatformAssets(): PlatformAssets = PlatformAssetsImpl

actual fun fileSystemAssetLoader(baseDir: FileSystemDirectory): FileSystemAssetLoader = FileSystemAssetLoaderAndroid(baseDir)

object PlatformAssetsImpl : PlatformAssets {

    init {
        HttpCache.initCache(File(KoolSystem.configAndroid.appContext.cacheDir, "httpCache"))
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

    fun readImageData(
        inStream: InputStream,
        mimeType: MimeType,
        format: TexFormat,
        resolveSize: Vec2i?
    ): BufferedImageData2d {
        return inStream.use {
            when (mimeType) {
                MimeType.IMAGE_SVG -> renderSvg(inStream, format, resolveSize)
                else -> {
                    var bmp = BitmapFactory.decodeStream(inStream)
                    if (resolveSize != null) {
                        bmp = Bitmap.createScaledBitmap(bmp, resolveSize.x, resolveSize.y, true)
                    }
                    bmp.toImageData2d(format).also { bmp.recycle() }
                }
            }
        }
    }

    private fun renderSvg(inStream: InputStream, format: TexFormat, resolveSize: Vec2i?): BufferedImageData2d {
        val svg = SVG.getFromInputStream(inStream)
        var width = resolveSize?.x ?: ceil(svg.documentViewBox.width()).toInt()
        var height = resolveSize?.y ?: ceil(svg.documentViewBox.height()).toInt()
        if (width <= 0) {
            width = 100
        }
        if (height <= 0) {
            height = 100
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        svg.renderToCanvas(canvas)
        return bitmap.toImageData2d(format).also { bitmap.recycle() }
    }

    private fun Bitmap.toImageData2d(format: TexFormat): BufferedImageData2d {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        if (format != TexFormat.RGBA) {
            logE("PlatformAssetsImpl") { "Currently, only TexFormat.RGBA can be loaded as texture" }
        }

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
        return BufferedImageData2d(buffer, width, height, TexFormat.RGBA)
    }

    override suspend fun waitForFonts() {
        // on JVM all fonts should be immediately available -> nothing to wait for
    }

    override fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): BufferedImageData2d {
        error("AtlasFont is not supported on Android, use MsdfFont instead")
    }

    override suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile> {
        error("File choosing is not supported on Android")
    }

    override suspend fun saveFileByUser(data: Uint8Buffer, defaultFileName: String?, filterList: List<FileFilterItem>, mimeType: MimeType): String? {
        error("File choosing is not supported on Android")
    }
}

actual suspend fun decodeDataUri(dataUri: String): Uint8Buffer {
    return Uint8BufferImpl(dataUriToByteArray(dataUri))
}

internal fun dataUriToByteArray(dataUri: String): ByteArray {
    val dataIdx = dataUri.indexOf(";base64,") + 8
    return Base64.decode(dataUri.substring(dataIdx), Base64.DEFAULT)
}