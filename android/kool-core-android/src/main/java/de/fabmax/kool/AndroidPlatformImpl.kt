package de.fabmax.kool

import de.fabmax.kool.gl.GL_RGB
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.createUint8Buffer

/**
 * Kool for Android platform implementation
 */

class AndroidPlatformImpl : PlatformImpl {
    override fun createContext(props: RenderContext.InitProps): RenderContext {
        return AndroidRenderContext()
    }

    override fun createCharMap(fontProps: FontProps): CharMap {
        val texData = BufferedTextureData(createUint8Buffer(16*16*3), 16, 16, GL_RGB)
        return CharMap(texData, mapOf())
    }

    override fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadTextureAsset(assetPath: String): TextureData {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openUrl(url: String) {
        // todo
    }

    override fun getMemoryInfo(): String {
        // todo
        return ""
    }
}