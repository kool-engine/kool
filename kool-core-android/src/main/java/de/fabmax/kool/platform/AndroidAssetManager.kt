package de.fabmax.kool.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolException
import de.fabmax.kool.TextureData
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logW
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class AndroidAssetManager(private val context: Context, assetsBaseDir: String) : AssetManager(assetsBaseDir) {
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
            val bitmap = BitmapFactory.decodeStream(it, null, opts)
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
            val bitmap = BitmapFactory.decodeStream(it, null, opts)
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
        launch {
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
        launch(HttpCache.assetLoadingCtx) {
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
}