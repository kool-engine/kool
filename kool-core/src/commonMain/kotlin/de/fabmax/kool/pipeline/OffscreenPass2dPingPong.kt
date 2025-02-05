package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.scene.Node

@Deprecated("Will be removed")
open class OffscreenPass2dPingPong(
    attachmentConfig: AttachmentConfig,
    initialSize: Vec2i,
    name: String,
    numSamples: Int = 1,
    mipMode: MipMode = MipMode.Single,
) : OffscreenPass(numSamples, mipMode, Vec3i(initialSize, 1), name) {

    var pingPongPasses = 1

    val pingContent = Node()
    val pongContent = Node()

    val ping: OffscreenPass2d = OffscreenPass2d(pingContent, attachmentConfig, initialSize, "${name}-ping")
    val pong: OffscreenPass2d = OffscreenPass2d(pongContent, attachmentConfig, initialSize, "${name}-pong")

    override var isReverseDepth: Boolean
        get() = ping.isReverseDepth && pong.isReverseDepth
        set(value) {
            ping.isReverseDepth = value
            pong.isReverseDepth = value
        }

    var onDrawPing: ((Int) -> Unit)? = null
    var onDrawPong: ((Int) -> Unit)? = null

    override val colors: List<RenderPassColorAttachment> = emptyList()
    override val depth: RenderPassDepthAttachment? = null
    override val views: List<View> = emptyList()

    fun setSize(width: Int, height: Int) {
        super.setSize(width, height, 1)
        ping.setSize(width, height)
        pong.setSize(width, height)
    }

    override fun update(ctx: KoolContext) {
        super.update(ctx)
        ping.update(ctx)
        pong.update(ctx)
    }

    override fun afterPass() {
        super.afterPass()
        ping.afterPass()
        pong.afterPass()
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