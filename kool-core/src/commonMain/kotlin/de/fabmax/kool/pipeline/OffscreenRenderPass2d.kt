package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Node

open class OffscreenRenderPass2d(drawNode: Node, texWidth: Int, texHeight: Int, mipLevels: Int = 1, val colorFormat: TexFormat = TexFormat.RGBA) :
        OffscreenRenderPass(drawNode, texWidth, texHeight, mipLevels) {

    internal val impl = OffscreenPass2dImpl(this)

    val colorTexture: Texture
        get() = impl.texture
    val depthTexture: Texture
        get() = impl.depthTexture

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        impl.dispose(ctx)
    }

    override fun resize(width: Int, height: Int, ctx: KoolContext) {
        super.resize(width, height, ctx)
        impl.resize(width, height, ctx)
    }
}

expect class OffscreenPass2dImpl(offscreenPass: OffscreenRenderPass2d) {
    val texture: Texture
    val depthTexture: Texture

    fun resize(width: Int, height: Int, ctx: KoolContext)

    fun dispose(ctx: KoolContext)
}