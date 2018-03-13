package de.fabmax.kool

import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps

abstract class AssetManager {

    abstract fun createCharMap(fontProps: FontProps): CharMap

    abstract fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit)

    abstract fun loadTextureAsset(assetPath: String): TextureData

}

fun assetTexture(assetPath: String, ctx: KoolContext, delayLoading: Boolean = true): Texture {
    return assetTexture(defaultProps(assetPath), ctx, delayLoading)
}

fun assetTexture(props: TextureProps, ctx: KoolContext, delayLoading: Boolean = true): Texture {
    return Texture(props) {
        this.delayLoading = delayLoading
        ctx.assetMgr.loadTextureAsset(props.id)
    }
}
