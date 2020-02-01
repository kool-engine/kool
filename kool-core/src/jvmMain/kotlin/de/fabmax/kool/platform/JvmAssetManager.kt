package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.pipeline.BufferedTextureData
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.platform.vk.TextureLoader
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

class JvmAssetManager internal constructor(props: Lwjgl3ContextVk.InitProps, val ctx: KoolContext) : AssetManager(props.assetsBaseDir) {

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

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.getCharMap(fontProps)

    override fun inflate(zipData: ByteArray): ByteArray = GZIPInputStream(ByteArrayInputStream(zipData)).readBytes()

    /**
     * fixme: this belongs in the not yet existing all new texture manager which should check if the texture was already loaded before
     */
    override fun loadAndPrepareTexture(assetPath: String, recv: (Texture) -> Unit) {
        val tex = Texture { it.loadTextureData(assetPath) }
        launch {
            val data = loadTextureData(assetPath) as BufferedTextureData
            val sys = (ctx as Lwjgl3ContextVk).vkSystem
            ctx.runOnGpuThread {
                tex.loadedTexture = TextureLoader.loadTexture(sys, data)
                tex.loadingState = Texture.LoadingState.LOADED
                sys.device.addDependingResource(tex.loadedTexture!!)
                recv(tex)
            }
        }
    }

    override fun loadAndPrepareCubeMap(ft: String, bk: String, lt: String, rt: String, up: String, dn: String, recv: (CubeMapTexture) -> Unit) {
        val tex = CubeMapTexture { it.loadCubeMapTextureData(ft, bk, lt, rt, up, dn) }
        launch {
            val data = loadCubeMapTextureData(ft, bk, lt, rt, up, dn)
            val sys = (ctx as Lwjgl3ContextVk).vkSystem
            ctx.runOnGpuThread {
                tex.loadedTexture = TextureLoader.loadCubeMap(sys, data)
                tex.loadingState = Texture.LoadingState.LOADED
                sys.device.addDependingResource(tex.loadedTexture!!)
                recv(tex)
            }
        }
    }

    fun loadTextureAsync(loader: suspend CoroutineScope.(AssetManager) -> TextureData): Deferred<TextureData> {
        return async { loader(this@JvmAssetManager) }
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}