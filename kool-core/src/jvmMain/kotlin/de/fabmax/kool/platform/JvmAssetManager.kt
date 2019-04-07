package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logE
import java.io.*
import java.util.zip.GZIPInputStream
import javax.imageio.ImageIO

class JvmAssetManager internal constructor(assetsBaseDir: String) : AssetManager(assetsBaseDir) {

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    init {
        // inits http cache if not already happened
        HttpCache.initCache(File(".httpCache"))
    }

    override suspend fun loadLocalRaw(localRawRef: LocalRawAssetRef): LoadedRawAsset {
        var data: ByteArray? = null
        try {
            openLocalStream(localRawRef.url).use { data = it.readBytes() }
        } catch (e: Exception) {
            logE { "Failed loading asset ${localRawRef.url}: $e" }
        }
        return LoadedRawAsset(localRawRef, data)
    }

    override suspend fun loadHttpRaw(httpRawRef: HttpRawAssetRef): LoadedRawAsset {
        var data: ByteArray? = null
        try {
            FileInputStream(HttpCache.loadHttpResource(httpRawRef.url)).use { data = it.readBytes() }
        } catch (e: Exception) {
            logE { "Failed loading asset ${httpRawRef.url}: $e" }
        }
        return LoadedRawAsset(httpRawRef, data)
    }

    override suspend fun loadLocalTexture(localTextureRef: LocalTextureAssetRef): LoadedTextureAsset {
        var data: ImageTextureData? = null
        try {
            openLocalStream(localTextureRef.url).use { data = ImageTextureData(ImageIO.read(it)) }
        } catch (e: Exception) {
            logE { "Failed loading texture ${localTextureRef.url}: $e" }
        }
        return LoadedTextureAsset(localTextureRef, data)
    }

    override suspend fun loadHttpTexture(httpTextureRef: HttpTextureAssetRef): LoadedTextureAsset {
        var data: ImageTextureData? = null
        try {
            val f = HttpCache.loadHttpResource(httpTextureRef.url)!!
            FileInputStream(f).use {
                data = ImageTextureData(ImageIO.read(it))
            }
        } catch (e: Exception) {
            logE { "Failed loading texture ${httpTextureRef.url}: $e" }
        }
        return LoadedTextureAsset(httpTextureRef, data)
    }

    private fun openLocalStream(assetPath: String): InputStream {
        var inStream = ClassLoader.getSystemResourceAsStream(assetPath)
        if (inStream == null) {
            // if asset wasn't found in resources try to load it from file system
            inStream = FileInputStream(assetPath)
        }
        return inStream
    }

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.createCharMap(fontProps)

    override fun inflate(zipData: ByteArray): ByteArray {
        val inflateIn = GZIPInputStream(ByteArrayInputStream(zipData))
        val bos = ByteArrayOutputStream()
        inflateIn.copyTo(bos)
        return bos.toByteArray()
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}