package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.logW

open class OffscreenRenderPass2d(drawNode: Node, texWidth: Int, texHeight: Int, val setup: Setup) :
        OffscreenRenderPass(drawNode, texWidth, texHeight, setup.mipLevels) {

    internal val impl = OffscreenPass2dImpl(this)

    val colorFormat = setup.colorFormat
    val copyTargetsColor = mutableListOf<Texture>()

    val colorTexture: Texture
        get() {
            if (setup.colorRenderTarget == RENDER_TARGET_RENDERBUFFER) {
                logW { "Color render target is renderbuffer, colorTexture won't be usable" }
            }
            return impl.colorTexture
        }
    val depthTexture: Texture
        get() {
            if (setup.depthRenderTarget == RENDER_TARGET_RENDERBUFFER) {
                logW { "Depth render target is renderbuffer, depthTexture won't be usable" }
            }
            return impl.depthTexture
        }

    constructor(drawNode: Node, texWidth: Int, texHeight: Int, colorFormat: TexFormat = TexFormat.RGBA, mipLevels: Int = 1) :
            this(drawNode, texWidth, texHeight, Setup().apply { this.colorFormat = colorFormat; this.mipLevels = mipLevels })

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        impl.dispose(ctx)
    }

    override fun resize(width: Int, height: Int, ctx: KoolContext) {
        super.resize(width, height, ctx)
        impl.resize(width, height, ctx)
    }

    class Setup {
        var colorFormat = TexFormat.RGBA
        var mipLevels = 1

        var colorRenderTarget = RENDER_TARGET_TEXTURE
        var depthRenderTarget = RENDER_TARGET_RENDERBUFFER

        var extColorTexture: Texture? = null
        var extDepthTexture: Texture? = null
    }

    companion object {
        const val RENDER_TARGET_TEXTURE = 1
        const val RENDER_TARGET_RENDERBUFFER = 2
    }
}

expect class OffscreenPass2dImpl(offscreenPass: OffscreenRenderPass2d) {
    val colorTexture: Texture
    val depthTexture: Texture

    fun resize(width: Int, height: Int, ctx: KoolContext)

    fun dispose(ctx: KoolContext)
}