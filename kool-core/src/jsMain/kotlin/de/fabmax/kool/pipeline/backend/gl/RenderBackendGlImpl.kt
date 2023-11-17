package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.OffscreenRenderPass2dPingPong
import de.fabmax.kool.pipeline.OffscreenRenderPassCube
import de.fabmax.kool.util.Viewport
import org.w3c.dom.HTMLCanvasElement

class RenderBackendGlImpl(ctx: KoolContext, canvas: HTMLCanvasElement) : RenderBackendGl(GlImpl, ctx) {
    override val deviceName = "WebGL"

    init {
        val webGlCtx = (canvas.getContext("webgl2") ?: canvas.getContext("experimental-webgl2")) as WebGL2RenderingContext?
        check(webGlCtx != null) {
            val txt = "Unable to initialize WebGL2 context. Your browser may not support it."
            js("alert(txt)")
            txt
        }
        GlImpl.initWebGl(webGlCtx)
        setupGl()
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, 0, ctx.windowWidth, ctx.windowHeight)
    }

    override fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
        when (offscreenPass) {
            is OffscreenRenderPass2d -> offscreenPass.impl.draw(ctx)
            is OffscreenRenderPassCube -> offscreenPass.impl.draw(ctx)
            is OffscreenRenderPass2dPingPong -> drawOffscreenPingPong(offscreenPass)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $offscreenPass")
        }
    }

    private fun drawOffscreenPingPong(offscreenPass: OffscreenRenderPass2dPingPong) {
        for (i in 0 until offscreenPass.pingPongPasses) {
            offscreenPass.onDrawPing?.invoke(i)
            offscreenPass.ping.impl.draw(ctx)

            offscreenPass.onDrawPong?.invoke(i)
            offscreenPass.pong.impl.draw(ctx)
        }
    }

    override fun close(ctx: KoolContext) {
        // nothing to do here
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }
}
