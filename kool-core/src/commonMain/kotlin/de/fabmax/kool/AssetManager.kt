package de.fabmax.kool

import de.fabmax.kool.gl.GL_CLAMP_TO_EDGE
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_TEXTURE_CUBE_MAP
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

fun assetTextureCubeMap(frontPath: String, backPath: String, leftPath: String, rightPath: String, upPath: String,
                        downPath: String, delayLoading: Boolean = true): CubeMapTexture {
    val id = "$frontPath-$backPath-$leftPath-$rightPath-$upPath-$downPath"
    val props = TextureProps(id, GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0, GL_TEXTURE_CUBE_MAP)
    return CubeMapTexture(props) { ctx ->
        this.delayLoading = delayLoading
        val ft = ctx.assetMgr.loadTextureAsset(frontPath)
        val bk = ctx.assetMgr.loadTextureAsset(backPath)
        val lt = ctx.assetMgr.loadTextureAsset(leftPath)
        val rt = ctx.assetMgr.loadTextureAsset(rightPath)
        val up = ctx.assetMgr.loadTextureAsset(upPath)
        val dn = ctx.assetMgr.loadTextureAsset(downPath)
        CubeMapTextureData(ft, bk, lt, rt, up, dn)
    }
}
