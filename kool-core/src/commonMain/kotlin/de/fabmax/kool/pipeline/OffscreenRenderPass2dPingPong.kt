package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.scene.Node

open class OffscreenRenderPass2dPingPong(attachmentConfig: AttachmentConfig, initialSize: Vec2i, name: String) :
    OffscreenRenderPass(attachmentConfig, Vec3i(initialSize.x, initialSize.y, 1), name)
{
    var pingPongPasses = 1

    val pingContent = Node()
    val pongContent = Node()

    val ping: OffscreenRenderPass2d = OffscreenRenderPass2d(pingContent, attachmentConfig, initialSize, "${name}-ping")
    val pong: OffscreenRenderPass2d = OffscreenRenderPass2d(pongContent, attachmentConfig, initialSize, "${name}-pong")

    override var isReverseDepth: Boolean
        get() = ping.isReverseDepth && pong.isReverseDepth
        set(value) {
            ping.isReverseDepth = value
            pong.isReverseDepth = value
        }

    var onDrawPing: ((Int) -> Unit)? = null
    var onDrawPong: ((Int) -> Unit)? = null

    override val views: List<View> = emptyList()

    override fun setSize(width: Int, height: Int, depth: Int) {
        super.setSize(width, height, 1)
        ping.setSize(width, height, 1)
        pong.setSize(width, height, 1)
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

    override fun afterDraw() {
        super.afterDraw()
        ping.afterDraw()
        pong.afterDraw()
    }

    override fun setupView(viewIndex: Int) {
        super.setupView(viewIndex)
        ping.setupView(viewIndex)
        pong.setupView(viewIndex)
    }

    override fun setupMipLevel(mipLevel: Int) {
        super.setupMipLevel(mipLevel)
        ping.setupMipLevel(mipLevel)
        pong.setupMipLevel(mipLevel)
    }

    override fun release() {
        super.release()
        ping.release()
        pong.release()
        pingContent.release()
        pongContent.release()
    }
}