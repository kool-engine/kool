package de.fabmax.kool.platform

import android.content.Context
import android.util.Log
import de.fabmax.kool.AssetManager
import de.fabmax.kool.TextureData
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream

class AndroidAssetManager(val context: Context) : AssetManager() {
    private val fontMapGenerator = FontMapGenerator(context, 1024, 1024)

    override fun createCharMap(fontProps: FontProps): CharMap = fontMapGenerator.createCharMap(fontProps)

    override fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) {
        launch {
            this@AndroidAssetManager.context.assets.open(assetPath)?.use {
                val t = System.nanoTime()
                val data = ByteArrayOutputStream()
                val buf = ByteArray(128 * 1024)
                while (it.available() > 0) {
                    val len = it.read(buf)
                    data.write(buf, 0, len)
                }
                val bytes = data.toByteArray()
                Log.d("KoolActivity", "Loaded asset \"$assetPath\" in ${(System.nanoTime() - t) / 1e6} ms (${bytes.size / (1024.0*1024.0)} MB)")

                onLoad(bytes)
            }
        }
    }

    override fun loadTextureAsset(assetPath: String): TextureData = ImageTextureData(assetPath, context)

}