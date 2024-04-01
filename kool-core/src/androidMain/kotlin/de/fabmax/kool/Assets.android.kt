package de.fabmax.kool

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Base64
import com.caverock.androidsvg.SVG
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoader
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoaderAndroid
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.HttpCache
import de.fabmax.kool.util.AtlasFont
import de.fabmax.kool.util.CharMetrics
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
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

    override suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String, props: TextureProps?): TextureData {
        return withContext(Dispatchers.IO) {
            readImageData(ByteArrayInputStream(texData.toArray()), mimeType, props)
        }
    }

    fun readImageData(inStream: InputStream, mimeType: String, props: TextureProps?): TextureData2d {
        return inStream.use {
            when (mimeType) {
                MimeType.IMAGE_SVG -> renderSvg(inStream, props)
                else -> {
                    var bmp = BitmapFactory.decodeStream(inStream)
                    if (props?.resolveSize != null) {
                        bmp = Bitmap.createScaledBitmap(bmp, props.resolveSize.x, props.resolveSize.y, true)
                    }
                    bmp.toTextureData().also { bmp.recycle() }
                }
            }
        }
    }

    private fun renderSvg(inStream: InputStream, props: TextureProps?): TextureData2d {
        val svg = SVG.getFromInputStream(inStream)
        var width = props?.resolveSize?.x ?: ceil(svg.documentViewBox.width()).toInt()
        var height = props?.resolveSize?.y ?: ceil(svg.documentViewBox.height()).toInt()
        if (width <= 0) {
            width = 100
        }
        if (height <= 0) {
            height = 100
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        svg.renderToCanvas(canvas)
        return bitmap.toTextureData().also { bitmap.recycle() }
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

    override suspend fun waitForFonts() {
        // on JVM all fonts should be immediately available -> nothing to wait for
    }

    override fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d {
        error("AtlasFont is not supported on Android, use MsdfFont instead")
    }

    override suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile> {
        error("File choosing is not supported on Android")
    }

    override suspend fun saveFileByUser(data: Uint8Buffer, defaultFileName: String?, filterList: List<FileFilterItem>, mimeType: String): String? {
        error("File choosing is not supported on Android")
    }
}

actual suspend fun decodeDataUri(dataUri: String): Uint8Buffer {
    val dataIdx = dataUri.indexOf(";base64,") + 8
    return Uint8BufferImpl(Base64.decode(dataUri.substring(dataIdx), Base64.DEFAULT))
}