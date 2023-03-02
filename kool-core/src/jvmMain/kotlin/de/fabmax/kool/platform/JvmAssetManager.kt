package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import kotlinx.coroutines.*
import org.lwjgl.PointerBuffer
import org.lwjgl.util.nfd.NativeFileDialog
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import javax.imageio.ImageIO

class JvmAssetManager internal constructor(props: Lwjgl3Context.InitProps, val ctx: Lwjgl3Context) : AssetManager() {

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT, ctx)
    private val imageIoLock = Any()
    private val isWithHttp = props.isWithHttpAssets
    private val localAssetsPath = props.localAssetPath

    private var fileChooserPath = System.getProperty("user.home")

    override val storage = KeyValueStorageJvm(File(props.storageDir))

    init {
        // inits http cache if not already happened
        if (isWithHttp) {
            HttpCache.initCache(File(".httpCache"))
        }
        fontGenerator.loadCustomFonts(props, this)
    }

    override suspend fun loadRaw(rawRef: RawAssetRef): LoadedRawAsset {
        return if (rawRef.isLocal) {
            loadLocalRaw(rawRef)
        } else {
            loadHttpRaw(rawRef)
        }
    }

    private suspend fun loadLocalRaw(localRawRef: RawAssetRef): LoadedRawAsset {
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

    private suspend fun loadHttpRaw(httpRawRef: RawAssetRef): LoadedRawAsset {
        var data: Uint8BufferImpl? = null
        if (httpRawRef.url.startsWith("data:", true)) {
            data = decodeDataUrl(httpRawRef.url)
        } else if (isWithHttp) {
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

    override suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        var data: ImageTextureData? = null
        withContext(Dispatchers.IO) {
            try {
                data = if (textureRef.isLocal) {
                    loadLocalTexture(textureRef)
                } else {
                    loadHttpTexture(textureRef)
                }
            } catch (e: Exception) {
                logE { "Failed loading texture ${textureRef.url}: $e" }
            }
        }
        return if (textureRef.isAtlas) {
            LoadedTextureAsset(textureRef, ImageAtlasTextureData(data!!, textureRef.tilesX, textureRef.tilesY))
        } else {
            LoadedTextureAsset(textureRef, data)
        }
    }

    private fun loadLocalTexture(localTextureRef: TextureAssetRef): ImageTextureData {
        return openLocalStream(localTextureRef.url).use {
            // ImageIO.read is not thread safe!
            val img = synchronized(imageIoLock) { ImageIO.read(it) }
            ImageTextureData(img, localTextureRef.fmt)
        }
    }

    private fun loadHttpTexture(httpTextureRef: TextureAssetRef): ImageTextureData {
        if (!isWithHttp) {
            throw FileNotFoundException("HTTP loading is disabled")
        }

        val f = HttpCache.loadHttpResource(httpTextureRef.url)!!
        return FileInputStream(f).use {
            // ImageIO.read is not thread safe!
            val img = synchronized(imageIoLock) { ImageIO.read(it) }
            ImageTextureData(img, httpTextureRef.fmt)
        }
    }

    fun openLocalStream(assetPath: String): InputStream {
        var resPath = assetPath.replace('\\', '/')
        if (resPath.startsWith("/")) {
            resPath = resPath.substring(1)
        }
        var inStream = ClassLoader.getSystemResourceAsStream(resPath)
        if (inStream == null) {
            // if asset wasn't found in resources try to load it from file system
            inStream = FileInputStream("$localAssetsPath/$assetPath")
        }
        return inStream
    }

    override suspend fun waitForFonts() { }

    override fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>) =
        fontGenerator.createFontMapData(font, fontScale, outMetrics)

    override suspend fun loadFileByUser(filterList: String?): LoadedFile {
        chooseFile(filterList)?.let { file ->
            try {
                return LoadedFile(
                    file.absolutePath,
                    Uint8BufferImpl(file.readBytes())
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return LoadedFile(null, null)
    }

    fun chooseFile(filterList: String? = null): File? {
        val outPath = PointerBuffer.allocateDirect(1)
        val result = NativeFileDialog.NFD_OpenDialog(filterList, fileChooserPath, outPath)
        if (result == NativeFileDialog.NFD_OKAY) {
            val file = File(outPath.stringUTF8)
            fileChooserPath = file.parent
            return file
        }
        return null
    }

    override fun saveFileByUser(data: Uint8Buffer, fileName: String, mimeType: String): String? {
        val outPath = PointerBuffer.allocateDirect(1)
        val result = NativeFileDialog.NFD_SaveDialog(null, fileChooserPath, outPath)
        if (result == NativeFileDialog.NFD_OKAY) {
            val file = File(outPath.stringUTF8)
            file.parentFile.mkdirs()
            fileChooserPath = file.parent
            try {
                FileOutputStream(file).use { it.write(data.toArray()) }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return file.absolutePath
        }

        return null
    }

    override suspend fun loadTextureData2d(imagePath: String, format: TexFormat?): TextureData2d {
        // JVM implementation always loads images as ImageTextureData, which is a subclass of TextureData2d
        return loadTextureData(imagePath, format) as ImageTextureData
    }

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

    override suspend fun loadAndPrepareTexture(texData: TextureData, props: TextureProps, name: String?): Texture2d {
        val deferred = CompletableDeferred<Texture2d>(job)
        ctx.runOnMainThread {
            val tex = Texture2d(props, name) { texData }
            ctx.renderBackend.loadTex2d(tex, texData)
            deferred.complete(tex)
        }
        return deferred.await()
    }

    override suspend fun loadAndPrepareCubeMap(texData: TextureDataCube, props: TextureProps, name: String?): TextureCube {
        val deferred = CompletableDeferred<TextureCube>(job)
        ctx.runOnMainThread {
            val tex = TextureCube(props, name) { texData }
            ctx.renderBackend.loadTexCube(tex, texData)
            deferred.complete(tex)
        }
        return deferred.await()
    }

    override suspend fun loadAudioClip(assetPath: String): AudioClip {
        val asset = loadAsset(assetPath) ?: throw FileNotFoundException(assetPath)
        return AudioClip(asset.toArray())
    }

    fun loadTextureAsync(loader: suspend CoroutineScope.(AssetManager) -> TextureData): Deferred<TextureData> {
        return async { loader(this@JvmAssetManager) }
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}