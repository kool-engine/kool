package de.fabmax.kool

import de.fabmax.kool.gl.GlObject
import de.fabmax.kool.gl.TextureResource
import de.fabmax.kool.platform.*

/**
 * @author fabmax
 */

fun defaultProps(id: String): TextureProps {
    return TextureProps(id, GL.LINEAR_MIPMAP_LINEAR, GL.LINEAR, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE)
}

data class TextureProps(val id: String, val minFilter: Int, val magFilter: Int, val xWrapping: Int, val yWrapping: Int) {
    companion object {
        val DEFAULT_MIN = GL.LINEAR_MIPMAP_LINEAR
        val DEFAULT_MAG = GL.LINEAR
        val DEFAULT_X_WRAP = GL.CLAMP_TO_EDGE
        val DEFAULT_Y_WRAP = GL.CLAMP_TO_EDGE
    }
}

abstract class TextureData {
    open var isAvailable = false
        protected set

    var width = 0
        protected set
    var height = 0
        protected set

    internal fun loadData(texture: Texture, ctx: RenderContext) {
        onLoad(texture, ctx)
        texture.res!!.isLoaded = true
        if (texture.props.minFilter == GL.LINEAR_MIPMAP_LINEAR) {
            GL.generateMipmap(texture.res!!.target)
        }
    }

    abstract fun onLoad(texture: Texture, ctx: RenderContext)
}

class BufferedTextureData(val buffer: Uint8Buffer, width: Int, height: Int, val format: Int) : TextureData() {
    init {
        this.isAvailable = true
        this.width = width
        this.height = height
    }

    override fun onLoad(texture: Texture, ctx: RenderContext) {
        val res = texture.res ?: throw KoolException("Texture wasn't created")
        val limit = buffer.limit
        val pos = buffer.position
        buffer.flip()
        GL.texImage2D(res.target, 0, format, width, height, 0, format, GL.UNSIGNED_BYTE, buffer)
        buffer.limit = limit
        buffer.position = pos
        ctx.memoryMgr.memoryAllocated(res, buffer.position)
    }
}

open class Texture(val props: TextureProps, val generator: Texture.() -> TextureData) :
        GlObject<TextureResource>() {

    var width = 0
        private set
    var height = 0
        private set

    internal fun onCreate(ctx: RenderContext) {
        res = ctx.textureMgr.createTexture(props, ctx)
    }

    override fun dispose(ctx: RenderContext) {
        // do not call super, as this will immediately delete the texture on the GPU. However, texture resource is
        // shared and might be used by other Texture objects...
        if (isValid) {
            ctx.textureMgr.deleteTexture(this, ctx)
            res = null
        }
    }

    internal fun loadData(texData: TextureData, ctx: RenderContext) {
        if (!texData.isAvailable) {
            throw KoolException("Texture data is not available")
        }
        width = texData.width
        height = texData.height
        texData.loadData(this, ctx)
    }
}

fun assetTexture(assetPath: String): Texture {
    return Texture(defaultProps(assetPath)) {
        Platform.loadTextureAsset(props.id)
    }
}

fun assetTexture(assetPath: String, minFilter: Int, magFilter: Int, xWrapping: Int, yWrapping: Int): Texture {
    return Texture(TextureProps(assetPath, minFilter, magFilter, xWrapping, yWrapping)) {
        Platform.loadTextureAsset(props.id)
    }
}
