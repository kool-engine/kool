package de.fabmax.kool

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.platform.Uint8Buffer

/**
 * @author fabmax
 */

abstract class Texture2d(val props: TextureResource.Props) :
        GlObject<TextureResource>() {

    open var isAvailable = false
        protected set
    open var isLoaded = false
        protected set

    protected abstract fun loadData(target: Int, level: Int, ctx: RenderContext)

    internal open fun create(ctx: RenderContext) {
        res = TextureResource.create(GL.TEXTURE_2D, props, ctx)
    }

    internal open fun load(ctx: RenderContext) {
        val res = this.res
        if (res != null && isAvailable) {
            loadData(res.target, 0, ctx)
            if (props.minFilter == GL.LINEAR_MIPMAP_LINEAR) {
                GL.generateMipmap(res.target)
            }
            isLoaded = true
        }
    }
}

class BufferedTexture2d(val buffer: Uint8Buffer, val width: Int, val height: Int, val format: Int,
                      props: TextureResource.Props = TextureResource.DEFAULT_PROPERTIES) : Texture2d(props) {

    init {
        isAvailable = true
    }

    override fun loadData(target: Int, level: Int, ctx: RenderContext) {
        val limit = buffer.limit
        val pos = buffer.position
        buffer.flip()
        GL.texImage2D(target, level, format, width, height, 0, format, GL.UNSIGNED_BYTE, buffer)
        buffer.limit = limit
        buffer.position = pos

        ctx.memoryMgr.memoryAllocated(res!!, buffer.position)
    }
}

abstract class SharedTexture(props: TextureResource.Props) : Texture2d(props) {
    protected var texture: Texture2d? = null

    override var res: TextureResource?
        get() = texture?.res
        set(value) {}

    override var isAvailable: Boolean
        get() = texture?.isAvailable ?: false
        set(value) {}
    override var isLoaded: Boolean
        get() = texture?.isLoaded ?: false
        set(value) {}

    override fun loadData(target: Int, level: Int, ctx: RenderContext) {
        throw UnsupportedOperationException("SharedTexture doesn't load any data, call must be forwarded to texture")
    }

    override fun load(ctx: RenderContext) {
        texture?.load(ctx)
    }

    override fun create(ctx: RenderContext) {
        if (!(texture?.isValid ?: true)) {
            texture?.create(ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        // clear reference but don't delete anything, GL resource is a shared object
        texture = null
    }
}

class SharedAssetTexture(val assetPath: String, props: TextureResource.Props = TextureResource.DEFAULT_PROPERTIES) :
        SharedTexture(props) {

    override fun create(ctx: RenderContext) {
        if (texture == null) {
            texture = ctx.textureMgr.getAssetTexture(assetPath, props)
        }
        super.create(ctx)
    }

    override fun delete(ctx: RenderContext) {
        // decrease reference counter for asset texture (actual texture object is deleted as soon as there are
        // no more references to it
        ctx.textureMgr.deleteReference(texture, ctx)
        super.delete(ctx)
    }
}
