package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import kotlinx.coroutines.*
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import java.util.zip.GZIPInputStream
import javax.imageio.ImageIO

class JvmAssetManager internal constructor(props: Lwjgl3Context.InitProps, val ctx: Lwjgl3Context) : AssetManager(props.assetsBaseDir) {

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT, props, this)

    private val imageIoLock = Any()

    init {
        // inits http cache if not already happened
        HttpCache.initCache(File(".httpCache"))
    }

    override suspend fun loadLocalRaw(localRawRef: LocalRawAssetRef): LoadedRawAsset {
        var data: Uint8BufferImpl? = null
        withContext(Dispatchers.IO) {
            try {
                openLocalStream(localRawRef.url).use { data = Uint8BufferImpl(it.readBytes()) }
            } catch (e: Exception) {
                logE { "Failed loading asset ${localRawRef.url}: $e" }
            }
        }
        return LoadedRawAsset(localRawRef, data)
    }

    override suspend fun loadHttpRaw(httpRawRef: HttpRawAssetRef): LoadedRawAsset {
        var data: Uint8BufferImpl? = null

        if (httpRawRef.url.startsWith("data:", true)) {
            data = decodeDataUrl(httpRawRef.url)
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val f = HttpCache.loadHttpResource(httpRawRef.url)
                            ?: throw IOException("Failed downloading ${httpRawRef.url}")
                    FileInputStream(f).use { data = Uint8BufferImpl(it.readBytes()) }
                } catch (e: Exception) {
                    logE { "Failed loading asset ${httpRawRef.url}: $e" }
                }
            }
        }
        return LoadedRawAsset(httpRawRef, data)
    }

    private fun decodeDataUrl(dataUrl: String): Uint8BufferImpl {
        val dataIdx = dataUrl.indexOf(";base64,") + 8
        return Uint8BufferImpl(Base64.getDecoder().decode(dataUrl.substring(dataIdx)))
    }

    override suspend fun loadLocalTexture(localTextureRef: LocalTextureAssetRef): LoadedTextureAsset {
        var data: ImageTextureData? = null
        withContext(Dispatchers.IO) {
            try {
                openLocalStream(localTextureRef.url).use {
                    //data = ImageTextureData(ImageIO.read(it))
                    // ImageIO.read is not thread safe!
                    val img = synchronized(imageIoLock) {
                        ImageIO.read(it)
                    }
                    data = ImageTextureData(img, localTextureRef.fmt)
                }
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
                    //data = ImageTextureData(ImageIO.read(it))
                    // ImageIO.read is not thread safe!
                    val img = synchronized(imageIoLock) {
                        ImageIO.read(it)
                    }
                    data = ImageTextureData(img, httpTextureRef.fmt)
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

    override fun inflate(zipData: Uint8Buffer): Uint8Buffer = Uint8BufferImpl(GZIPInputStream(ByteArrayInputStream(zipData.toArray())).readBytes())

    override suspend fun createTextureData(texData: Uint8Buffer, mimeType: String): TextureData {
        var img: BufferedImage?
        withContext(Dispatchers.IO) {
            img = synchronized(imageIoLock) {
                ImageIO.read(ByteArrayInputStream(texData.toArray()))
            }
        }
        return ImageTextureData(img!!, null)
    }

    override suspend fun loadAndPrepareTexture(assetPath: String, props: TextureProps): Texture2d {
        val tex = Texture2d(props, assetPathToName(assetPath)) { it.loadTextureData(assetPath) }
        val data = loadTextureData(assetPath, props.format)
        val deferred = CompletableDeferred<Texture2d>(job)
        ctx.runOnMainThread {
            ctx.renderBackend.loadTex2d(tex, data)
            deferred.complete(tex)
        }
        return deferred.await()
    }

    override suspend fun loadAndPrepareCubeMap(ft: String, bk: String, lt: String, rt: String, up: String, dn: String,
                                       props: TextureProps): TextureCube {
        val name = cubeMapAssetPathToName(ft, bk, lt, rt, up, dn)
        val tex = TextureCube(props, name) { it.loadCubeMapTextureData(ft, bk, lt, rt, up, dn) }
        val data = loadCubeMapTextureData(ft, bk, lt, rt, up, dn)
        val deferred = CompletableDeferred<TextureCube>(job)
        ctx.runOnMainThread {
            ctx.renderBackend.loadTexCube(tex, data)
            deferred.complete(tex)
        }
        return deferred.await()
    }

    override fun loadAndPrepareTexture(texData: TextureData, props: TextureProps, name: String?): Texture2d {
        val tex = Texture2d(props, name) { texData }
        ctx.renderBackend.loadTex2d(tex, texData)
        return tex
    }

    override fun loadAndPrepareCubeMap(texData: CubeMapTextureData, props: TextureProps, name: String?): TextureCube {
        val tex = TextureCube(props, name) { texData }
        ctx.renderBackend.loadTexCube(tex, texData)
        return tex
    }

    fun loadTextureAsync(loader: suspend CoroutineScope.(AssetManager) -> TextureData): Deferred<TextureData> {
        return async { loader(this@JvmAssetManager) }
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}