package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera

open class OffscreenRenderPassCube(drawNode: Node, texWidth: Int, texHeight: Int, mipLevels: Int, val colorFormat: TexFormat = TexFormat.RGBA) :
        OffscreenRenderPass(drawNode, texWidth, texHeight, mipLevels) {

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

    override fun resize(width: Int, height: Int, ctx: KoolContext) {
        super.resize(width, height, ctx)
        impl.resize(width, height, ctx)
    }

    private fun defaultCubeMapCameraConfig() {
        val camDirs = mutableMapOf(
                ViewDirection.FRONT to ViewConfig(Vec3f(0f, 0f, 1f), Vec3f.NEG_Y_AXIS),
                ViewDirection.BACK  to ViewConfig(Vec3f(0f, 0f, -1f), Vec3f.NEG_Y_AXIS),
                ViewDirection.LEFT  to ViewConfig(Vec3f(-1f, 0f, 0f), Vec3f.NEG_Y_AXIS),
                ViewDirection.RIGHT to ViewConfig(Vec3f(1f, 0f, 0f), Vec3f.NEG_Y_AXIS),
                ViewDirection.UP    to ViewConfig(Vec3f(0f, 1f, 0f), Vec3f.Z_AXIS),
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

expect class OffscreenPassCubeImpl(offscreenPass: OffscreenRenderPassCube) {
    val texture: CubeMapTexture

    fun resize(width: Int, height: Int, ctx: KoolContext)

    fun dispose(ctx: KoolContext)
}