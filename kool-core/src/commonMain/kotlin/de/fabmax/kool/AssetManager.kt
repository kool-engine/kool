package de.fabmax.kool

import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps

abstract class AssetManager(var assetsBaseDir: String) {

    abstract fun createCharMap(fontProps: FontProps): CharMap

    open fun loadAsset(assetPath: String, onLoad: (ByteArray?) -> Unit) {
        return if (isHttpAsset(assetPath)) {
            loadHttpAsset(assetPath, onLoad)
        } else {
            loadLocalAsset("$assetsBaseDir/$assetPath", onLoad)
        }
    }

    open fun loadTextureAsset(assetPath: String): TextureData  {
        return if (isHttpAsset(assetPath)) {
            loadHttpTexture(assetPath)
        } else {
            loadLocalTexture("$assetsBaseDir/$assetPath")
        }
    }

    protected abstract fun loadHttpAsset(assetPath: String, onLoad: (ByteArray?) -> Unit)

    protected abstract fun loadLocalAsset(assetPath: String, onLoad: (ByteArray?) -> Unit)

    protected abstract fun loadHttpTexture(assetPath: String): TextureData

    protected abstract fun loadLocalTexture(assetPath: String): TextureData

    protected open fun isHttpAsset(assetPath: String): Boolean =
            // todo: use something less naive here
            assetPath.startsWith("http://", true) ||
            assetPath.startsWith("https://", true)
}

fun assetTexture(assetPath: String, delayLoading: Boolean = true): Texture {
    return assetTexture(defaultProps(assetPath), delayLoading)
}

fun assetTexture(props: TextureProps, delayLoading: Boolean = true): Texture {
    return Texture(props) { ctx ->
        this.delayLoading = delayLoading
        ctx.assetMgr.loadTextureAsset(props.id)
    }
}
