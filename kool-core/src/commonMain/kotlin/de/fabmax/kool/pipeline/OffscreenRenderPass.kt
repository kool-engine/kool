package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera

abstract class OffscreenRenderPass(drawNode: Node, val texWidth: Int, val texHeight: Int, val mipLevels: Int, val colorFormat: TexFormat) : RenderPass(drawNode) {
    var targetMipLevel = -1
    var isFinished = false

    override var camera: Camera = PerspectiveCamera().apply { projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN }

    init {
        viewport = KoolContext.Viewport(0, 0, texWidth, texHeight)
    }

    fun mipWidth(mipLevel: Int): Int {
        return if (mipLevel <= 0) {
            texWidth
        } else {
            texWidth shr mipLevel
        }
    }

    fun mipHeight(mipLevel: Int): Int {
        return if (mipLevel <= 0) {
            texHeight
        } else {
            texHeight shr mipLevel
        }
    }
}

open class OffscreenRenderPass2D(drawNode: Node, texWidth: Int, texHeight: Int, mipLevels: Int = 1, colorFormat: TexFormat = TexFormat.RGBA) :
        OffscreenRenderPass(drawNode, texWidth, texHeight, mipLevels, colorFormat) {

    internal val impl = OffscreenPass2dImpl(this)

    val colorTexture: Texture
        get() = impl.texture
    val depthTexture: Texture
        get() = impl.depthTexture

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        impl.dispose(ctx)
    }
}

open class OffscreenRenderPassCube(drawNode: Node, texWidth: Int, texHeight: Int, mipLevels: Int, colorFormat: TexFormat = TexFormat.RGBA) :
        OffscreenRenderPass(drawNode, texWidth, texHeight, mipLevels, colorFormat) {

    internal val impl = OffscreenPassCubeImpl(this)

    val colorTextureCube: CubeMapTexture
        get() = impl.texture

    lateinit var onSetupView: ((ViewDirection, KoolContext) -> Unit)

    val drawQueues = Array(6) { DrawQueue(this) }

    init {
        defaultCubeMapCameraConfig()
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        for (v in ViewDirection.values()) {
            drawQueue = drawQueues[v.index]
            onSetupView.invoke(v, ctx)
            super.collectDrawCommands(ctx)
        }
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        impl.dispose(ctx)
    }

    private fun defaultCubeMapCameraConfig() {
        val camDirs = mutableMapOf(
                ViewDirection.FRONT to ViewConfig(Vec3f(0f, 0f, 1f),  Vec3f.NEG_Y_AXIS),
                ViewDirection.BACK  to ViewConfig(Vec3f(0f, 0f, -1f), Vec3f.NEG_Y_AXIS),
                ViewDirection.LEFT  to ViewConfig(Vec3f(-1f, 0f, 0f), Vec3f.NEG_Y_AXIS),
                ViewDirection.RIGHT to ViewConfig(Vec3f(1f, 0f, 0f),  Vec3f.NEG_Y_AXIS),
                ViewDirection.UP    to ViewConfig(Vec3f(0f, 1f, 0f),  Vec3f.Z_AXIS),
                ViewDirection.DOWN  to ViewConfig(Vec3f(0f, -1f, 0f), Vec3f.NEG_Z_AXIS)
        )

        val cam = camera
        if (cam is PerspectiveCamera) {
            cam.position.set(Vec3f.ZERO)
            cam.fovY = 90f
            cam.clipNear = 0.1f
            cam.clipFar = 10f
            cam.projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN
        }

        onSetupView = { viewDir, _ ->
            val viewCfg = camDirs[viewDir]!!
            camera.lookAt.set(viewCfg.lookAt)
            camera.up.set(viewCfg.up)
        }
    }

    private class ViewConfig(val lookAt: Vec3f, val up: Vec3f)

    enum class ViewDirection(val index: Int) {
        FRONT(0),
        BACK(1),
        LEFT(2),
        RIGHT(3),
        UP(4),
        DOWN(5)
    }
}

expect class OffscreenPass2dImpl(offscreenPass: OffscreenRenderPass2D) {
    val texture: Texture
    val depthTexture: Texture

    fun dispose(ctx: KoolContext)
}

expect class OffscreenPassCubeImpl(offscreenPass: OffscreenRenderPassCube) {
    val texture: CubeMapTexture

    fun dispose(ctx: KoolContext)
}