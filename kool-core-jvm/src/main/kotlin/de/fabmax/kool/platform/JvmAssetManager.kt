package de.fabmax.kool.platform

import de.fabmax.kool.AssetManager
import de.fabmax.kool.TextureData
import de.fabmax.kool.use
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

class JvmAssetManager : AssetManager() {

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    override fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) {
        launch {
            // try to load asset from resources
            var inStream = ClassLoader.getSystemResourceAsStream(assetPath)
            if (inStream == null) {
                // if asset wasn't found in resources try to load it from file system
                inStream = FileInputStream(assetPath)
            }

            inStream.use {
                val data = ByteArrayOutputStream()
                val buf = ByteArray(1024 * 1024)
                while (it.available() > 0) {
                    val len = it.read(buf)
                    data.write(buf, 0, len)
                }
                onLoad(data.toByteArray())
            }
        }
    }

    override fun loadTextureAsset(assetPath: String): TextureData  = ImageTextureData(assetPath)

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.createCharMap(fontProps)

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}