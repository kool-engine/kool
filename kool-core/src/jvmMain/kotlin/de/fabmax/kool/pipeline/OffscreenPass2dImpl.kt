package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.Lwjgl3Context

actual class OffscreenPass2dImpl actual constructor(val offscreenPass: OffscreenRenderPass2d) {
    actual val colorTexture: Texture = offscreenPass.setup.extColorTexture ?: Texture(loader = null)
    actual val depthTexture: Texture = offscreenPass.setup.extDepthTexture ?: Texture(loader = null)

    val isExtColorTexture = offscreenPass.setup.extColorTexture != null
    val isExtDepthTexture = offscreenPass.setup.extDepthTexture != null

    internal var backendImpl: BackendImpl? = null

    fun draw(ctx: Lwjgl3Context) {
        if (backendImpl == null) {
            backendImpl = ctx.renderBackend.createOffscreenPass2d(this)
        }
        backendImpl!!.draw(ctx)
    }

    actual fun dispose(ctx: KoolContext) {
        backendImpl?.dispose(ctx as Lwjgl3Context)
    }

    actual fun resize(width: Int, height: Int, ctx: KoolContext) {
        backendImpl?.resize(width, height, ctx as Lwjgl3Context)
    }

    interface BackendImpl {
        fun draw(ctx: Lwjgl3Context)
        fun dispose(ctx: Lwjgl3Context)
        fun resize(width: Int, height: Int, ctx: Lwjgl3Context)
    }
}
