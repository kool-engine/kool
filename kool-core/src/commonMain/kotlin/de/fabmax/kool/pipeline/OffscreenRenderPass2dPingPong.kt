package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Node

open class OffscreenRenderPass2dPingPong(config: Config) : OffscreenRenderPass(Empty(),
    renderPassConfig {
        name = config.name
        setSize(1, 1)
        clearDepthTexture()
        clearColorTexture()
    }) {

    var pingPongPasses = 1

    val pingContent = Node()
    val pongContent = Node()

    val ping: OffscreenRenderPass2d = OffscreenRenderPass2d(pingContent, config)
    val pong: OffscreenRenderPass2d = OffscreenRenderPass2d(pongContent, config)

    var onDrawPing: ((Int) -> Unit)? = null
    var onDrawPong: ((Int) -> Unit)? = null

    override fun resize(width: Int, height: Int, ctx: KoolContext) {
        this.width = width
        this.height = height
        ping.resize(width, height, ctx)
        pong.resize(width, height, ctx)
    }

    override fun update(ctx: KoolContext) {
        ping.update(ctx)
        pong.update(ctx)
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        super.collectDrawCommands(ctx)
        ping.collectDrawCommands(ctx)
        pong.collectDrawCommands(ctx)
    }

    override fun afterDraw(ctx: KoolContext) {
        super.afterDraw(ctx)
        ping.afterDraw(ctx)
        pong.afterDraw(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        ping.dispose(ctx)
        pong.dispose(ctx)
    }

    private class Empty : Node()
}