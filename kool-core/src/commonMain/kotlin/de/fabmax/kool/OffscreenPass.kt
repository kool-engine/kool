package de.fabmax.kool

import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

abstract class OffscreenPass(val texWidth: Int, val texHeight: Int, val mipLevels: Int, nQueues: Int) {
    var clearColor = Color.BLACK
    var scene: Scene? = null

    val drawQueues: List<DrawQueue>

    var frameIdx = 0
    var isSingleShot = false

    init {
        drawQueues = List(nQueues) { DrawQueue() }
    }

    abstract fun render(ctx: KoolContext)

}

class OffscreenPass2d(texWidth: Int, texHeight: Int, mipLevels: Int) : OffscreenPass(texWidth, texHeight, mipLevels, 1) {
    val impl = OffscreenPass2dImpl(texWidth, texHeight, mipLevels)

    var onRender: ((KoolContext) -> Unit)? = null

    override fun render(ctx: KoolContext) {
        scene?.let { scene ->
            ctx.pushAttributes()
            ctx.viewport = KoolContext.Viewport(0, 0, texWidth, texHeight)

            scene.drawQueue = drawQueues[0].also { it.clear() }
            onRender?.invoke(ctx)
            scene.renderScene(ctx)

            ctx.popAttributes()
        }
        frameIdx++
    }
}

class OffscreenPassCube(texWidth: Int, texHeight: Int, mipLevels: Int) : OffscreenPass(texWidth, texHeight, mipLevels, 6) {
    val impl = OffscreenPassCubeImpl(texWidth, texHeight, mipLevels)

    var onRender: ((ViewDirection, KoolContext) -> Unit)? = null

    init {
        defaultCubeMapCameraConfig()
    }

    override fun render(ctx: KoolContext) {
        scene?.let { scene ->
            ctx.pushAttributes()
            ctx.viewport = KoolContext.Viewport(0, 0, texWidth, texHeight)
            for (v in ViewDirection.values()) {
                scene.drawQueue = drawQueues[v.index].also { it.clear() }
                onRender?.invoke(v, ctx)
                scene.renderScene(ctx)
            }
            ctx.popAttributes()
        }
        frameIdx++
    }

    private fun defaultCubeMapCameraConfig() {
        val camDirs = mutableMapOf(
                ViewDirection.FRONT to Vec3f(0f, 0f, 1f),
                ViewDirection.BACK to Vec3f(0f, 0f, -1f),
                ViewDirection.LEFT to Vec3f(-1f, 0f, 0f),
                ViewDirection.RIGHT to Vec3f(1f, 0f, 0f),
                ViewDirection.UP to Vec3f(0f, -1f, 0f),
                ViewDirection.DOWN to Vec3f(0f, 1f, 0f)
        )

        onRender = { viewDir, _ ->
            scene?.apply {
                if (camera !is PerspectiveCamera) {
                    camera = PerspectiveCamera()
                }
                (camera as PerspectiveCamera).let {
                    it.position.set(Vec3f.ZERO)
                    it.fovy = 90f
                    it.clipNear = 0.1f
                    it.clipFar = 10f
                }

                camera.lookAt.set(camDirs[viewDir]!!)
                when (viewDir) {
                    ViewDirection.UP -> camera.up.set(Vec3f.NEG_Z_AXIS)
                    ViewDirection.DOWN -> camera.up.set(Vec3f.Z_AXIS)
                    else -> camera.up.set(Vec3f.NEG_Y_AXIS)
                }
            }
        }
    }

    enum class ViewDirection(val index: Int) {
        FRONT(0),
        BACK(1),
        LEFT(2),
        RIGHT(3),
        UP(4),
        DOWN(5)
    }
}

expect class OffscreenPass2dImpl(texWidth: Int, texHeight: Int, mipLevels: Int) {
    val texture: Texture
}

expect class OffscreenPassCubeImpl(texWidth: Int, texHeight: Int, mipLevels: Int) {
    val texture: CubeMapTexture
}