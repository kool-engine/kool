package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.Lwjgl3Context

actual class OffscreenPass2dImpl actual constructor(val offscreenPass: OffscreenRenderPass2D) {
    actual val texture: Texture = Texture(loader = null)
    actual val depthTexture: Texture = Texture(loader = null)

    internal var backendImpl: BackendImpl? = null

    fun draw(ctx: Lwjgl3Context) {
        if (backendImpl == null) {
            backendImpl = ctx.renderBackend.createOffscreenPass2d(this)
        }
        backendImpl!!.draw(ctx)
    }

    actual fun dispose(ctx: KoolContext) {
        backendImpl?.let {
            it.dispose(ctx as Lwjgl3Context)
        }
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

actual class OffscreenPassCubeImpl actual constructor(val offscreenPass: OffscreenRenderPassCube) {
    actual val texture = CubeMapTexture(TextureProps(addressModeU = AddressMode.CLAMP_TO_EDGE,
            addressModeV = AddressMode.CLAMP_TO_EDGE, addressModeW = AddressMode.CLAMP_TO_EDGE),
            loader = null)

    internal var backendImpl: BackendImpl? = null

    fun draw(ctx: Lwjgl3Context) {
        if (backendImpl == null) {
            backendImpl = ctx.renderBackend.createOffscreenPassCube(this)
        }
        backendImpl!!.draw(ctx)
    }

    actual fun dispose(ctx: KoolContext) {
        backendImpl?.let {
            it.dispose(ctx as Lwjgl3Context)
        }
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