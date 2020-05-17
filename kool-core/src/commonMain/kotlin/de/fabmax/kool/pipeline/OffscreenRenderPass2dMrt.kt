package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.copy

open class OffscreenRenderPass2dMrt(drawNode: Node, texWidth: Int, texHeight: Int, texFormats: List<TexFormat>) :
        OffscreenRenderPass(drawNode, texWidth, texHeight, 1) {

    val texFormats = texFormats.copy()
    val nAttachments = texFormats.size

    internal val impl = OffscreenPass2dMrtImpl(this)

    val textures: List<Texture>
        get() = impl.textures
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

expect class OffscreenPass2dMrtImpl(offscreenPass: OffscreenRenderPass2dMrt) {
    val textures: List<Texture>
    val depthTexture: Texture

    fun resize(width: Int, height: Int, ctx: KoolContext)

    fun dispose(ctx: KoolContext)
}