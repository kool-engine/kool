package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.pipeline.ImageData
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logE
import kotlinx.coroutines.*
import kotlinx.io.IOException
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream
import javax.imageio.ImageIO

class JvmAssetManager internal constructor(props: Lwjgl3Context.InitProps) : AssetManager(props.assetsBaseDir) {

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT, props, this)

    init {
        // inits http cache if not already happened
        HttpCache.initCache(File(".httpCache"))
    }

    override suspend fun loadLocalRaw(localRawRef: LocalRawAssetRef): LoadedRawAsset {
        var data: ByteArray? = null
        withContext(Dispatchers.IO) {
            try {
                openLocalStream(localRawRef.url).use { data = it.readBytes() }
            } catch (e: Exception) {
                logE { "Failed loading asset ${localRawRef.url}: $e" }
            }
        }
        return LoadedRawAsset(localRawRef, data)
    }

    override suspend fun loadHttpRaw(httpRawRef: HttpRawAssetRef): LoadedRawAsset {
        var data: ByteArray? = null
        withContext(Dispatchers.IO) {
            try {
                val f = HttpCache.loadHttpResource(httpRawRef.url) ?: throw IOException("Failed downloading ${httpRawRef.url}")
                FileInputStream(f).use { data = it.readBytes() }
            } catch (e: Exception) {
                logE { "Failed loading asset ${httpRawRef.url}: $e" }
            }
        }
        return LoadedRawAsset(httpRawRef, data)
    }

    override suspend fun loadLocalTexture(localTextureRef: LocalTextureAssetRef): LoadedTextureAsset {
        var data: ImageTextureData? = null
        withContext(Dispatchers.IO) {
            try {
                openLocalStream(localTextureRef.url).use { data = ImageTextureData(ImageIO.read(it)) }
            } catch (e: Exception) {
                logE { "Failed loading texture ${localTextureRef.url}: $e" }
            }
        }
        return LoadedTextureAsset(localTextureRef, data)
    }

    override suspend fun loadHttpTexture(httpTextureRef: HttpTextureAssetRef): LoadedTextureAsset {
        var data: ImageTextureData? = null
        withContext(Dispatchers.IO) {
            try {
                val f = HttpCache.loadHttpResource(httpTextureRef.url)!!
                FileInputStream(f).use {
                    data = ImageTextureData(ImageIO.read(it))
                }
            } catch (e: Exception) {
                logE { "Failed loading texture ${httpTextureRef.url}: $e" }
            }
        }
        return LoadedTextureAsset(httpTextureRef, data)
    }

    internal fun openLocalStream(assetPath: String): InputStream {
        var inStream = ClassLoader.getSystemResourceAsStream(assetPath)
        if (inStream == null) {
            // if asset wasn't found in resources try to load it from file system
            inStream = FileInputStream(assetPath)
        }
        return inStream
    }

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.createCharMap(fontProps)

    override fun inflate(zipData: ByteArray): ByteArray = GZIPInputStream(ByteArrayInputStream(zipData)).readBytes()

    override suspend fun loadImageData(assetPath: String): ImageData {
        val ref = if (isHttpAsset(assetPath)) {
            loadHttpTexture(HttpTextureAssetRef(assetPath))
        } else {
            loadLocalTexture(LocalTextureAssetRef("$assetsBaseDir/$assetPath"))
        }
        val data = ref.data as ImageTextureData
        return ImageData(data)
    }

    fun deferredTexLoad(loader: suspend CoroutineScope.(AssetManager) -> ImageData): Deferred<ImageData> {
        return async { loader(this@JvmAssetManager) }
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}