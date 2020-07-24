package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.Lwjgl3Context

actual class OffscreenPassCubeImpl actual constructor(val offscreenPass: OffscreenRenderPassCube) {
    internal var backendImpl: BackendImpl? = null

    fun draw(ctx: Lwjgl3Context) {
        if (backendImpl == null) {
            backendImpl = ctx.renderBackend.createOffscreenPassCube(this)
        }
        backendImpl!!.draw(ctx)
    }

    actual fun dispose(ctx: KoolContext) {
        backendImpl?.dispose(ctx as Lwjgl3Context)
    }

    actual fun applySize(width: Int, height: Int, ctx: KoolContext) {
        backendImpl?.resize(width, height, ctx as Lwjgl3Context)
    }

    interface BackendImpl {
        fun draw(ctx: Lwjgl3Context)
        fun dispose(ctx: Lwjgl3Context)
        fun resize(width: Int, height: Int, ctx: Lwjgl3Context)
    }
}