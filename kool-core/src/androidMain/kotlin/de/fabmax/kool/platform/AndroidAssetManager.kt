package de.fabmax.kool.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import de.fabmax.kool.*
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class AndroidAssetManager internal constructor(private val context: Context, assetsBaseDir: String) : AssetManager(assetsBaseDir) {

    private val fontGenerator = FontMapGenerator(context, MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    init {
        // inits http cache if not already happened
        HttpCache.initCache(File(context.cacheDir, ".httpCache"))
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
        val data = ImageTextureData()
        try {
            openLocalStream(localTextureRef.url).use {
                val opts = BitmapFactory.Options()
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeStream(it, null, opts)!!
                data.setTexImage(bitmap)
                bitmap.recycle()
            }
        } catch (e: Exception) {
            logE { "Failed loading texture ${localTextureRef.url}: $e" }
        }
        return LoadedTextureAsset(localTextureRef, data)
    }

    override suspend fun loadHttpTexture(httpTextureRef: HttpTextureAssetRef): LoadedTextureAsset {
        val data = ImageTextureData()
        try {
            val f = HttpCache.loadHttpResource(httpTextureRef.url)!!
            FileInputStream(f).use {
                val opts = BitmapFactory.Options()
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeStream(it, null, opts)!!
                data.setTexImage(bitmap)
                bitmap.recycle()
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
            val checkFile = File(assetPath)
            inStream = if (checkFile.canRead()) {
                // load from sdcard
                FileInputStream(checkFile)

            } else {
                // load from asset manager
                val path = if (assetPath.startsWith("./")) {
                    assetPath.substring(2)
                } else {
                    assetPath
                }
                context.assets.open(path)
            }
        }
        return inStream
    }

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.createCharMap(fontProps)

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}
/*class AndroidAssetManager(private val context: Context, assetsBaseDir: String) : AssetManager(assetsBaseDir) {
    private val fontGenerator = FontMapGenerator(context, MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    init {
        // inits http cache if not already happened
        HttpCache.initCache(File(context.cacheDir, ".httpCache"))
    }

    override fun loadHttpAsset(assetPath: String, onLoad: (ByteArray?) -> Unit) {
        loadHttp(assetPath, { onLoad(null) }) {
            onLoad(loadAllBytes(it))
        }
    }

    override fun loadLocalAsset(assetPath: String, onLoad: (ByteArray?) -> Unit) {
        loadLocal(assetPath, { onLoad(null) }) {
            onLoad(loadAllBytes(it))
        }
    }

    override fun loadHttpTexture(assetPath: String): TextureData {
        val texData = ImageTextureData()
        loadHttp(assetPath) {
            val opts = BitmapFactory.Options()
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeStream(it, null, opts)!!
            texData.setTexImage(bitmap)
            bitmap.recycle()
        }
        return texData
    }

    override fun loadLocalTexture(assetPath: String): TextureData {
        Log.e("testo", "load local tex")
        val texData = ImageTextureData()
        loadLocal(assetPath) {
            val opts = BitmapFactory.Options()
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeStream(it, null, opts)!!
            texData.setTexImage(bitmap)
            bitmap.recycle()
        }
        return texData
    }

    fun loadAssetAsStream(assetPath: String, onLoad: (InputStream?) -> Unit) {
        return if (isHttpAsset(assetPath)) {
            loadHttpAssetAsStream(assetPath, onLoad)
        } else {
            loadLocalAssetAsStream("$assetsBaseDir/$assetPath", onLoad)
        }
    }

    private fun loadHttpAssetAsStream(assetPath: String, onLoad: (InputStream?) -> Unit) {
        loadHttp(assetPath, { onLoad(null) }) {
            onLoad(it)
        }
    }

    private fun loadLocalAssetAsStream(assetPath: String, onLoad: (InputStream?) -> Unit) {
        loadLocal(assetPath, { onLoad(null) }) {
            onLoad(it)
        }
    }

    private fun loadLocal(assetUrl: String, onError: ((Exception) -> Unit)? = null, onLoad: (InputStream) -> Unit) {
        GlobalScope.launch {
            try {
                onLoad(openLocalStream(assetUrl))
            } catch (e: Exception) {
                onError?.invoke(e)
                Log.e("AndroidAssetManager", "Asset not found: $assetUrl")
                throw KoolException("Failed to load asset: \"$assetUrl\"", e)
            }
        }
    }

    private fun loadHttp(assetUrl: String, onError: ((Exception) -> Unit)? = null, onLoad: (InputStream) -> Unit) {
        GlobalScope.launch {
            var tries = 2
            while (tries > 0) {
                var file: File? = null
                try {
                    file = HttpCache.loadHttpResource(assetUrl)
                    onLoad(FileInputStream(file))
                    // asset loading succeeded, break retry loop
                    break

                } catch (e: Exception) {
                    // if exception is caused by a corrupted HTTP cache file, delete it and try again
                    if (--tries > 0) {
                        logW { "HTTP cache file load failed: $e, retrying" }
                        file?.delete()
                    } else {
                        onError?.invoke(e)
                        throw KoolException("Failed to load http asset: \"$assetUrl\"", e)
                    }
                }
            }
        }
    }

    private fun openLocalStream(assetPath: String): InputStream {
        val path = if (assetPath.startsWith("./")) {
            assetPath.substring(2)
        } else {
            assetPath
        }
        return context.assets.open(path)
    }

    private fun loadAllBytes(inputStream: InputStream): ByteArray {
        inputStream.use {
            val data = ByteArrayOutputStream()
            val buf = ByteArray(1024 * 1024)
            while (it.available() > 0) {
                val len = it.read(buf)
                data.write(buf, 0, len)
            }
            return data.toByteArray()
        }
    }

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.createCharMap(fontProps)

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 1024
        private const val MAX_GENERATED_TEX_HEIGHT = 1024
    }
}*/