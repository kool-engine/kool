package de.fabmax.kool

import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import kotlin.math.max

abstract class OffscreenPass(val texWidth: Int, val texHeight: Int, val mipLevels: Int, nQueues: Int) {
    var clearColor = Color.BLACK
    var scene: Scene? = null

    val drawQueues: List<DrawQueue>

    var frameIdx = 0
    var isSingleShot = false
    var targetMipLevel = -1

    init {
        drawQueues = List(nQueues) { DrawQueue() }
    }

    abstract fun render(ctx: KoolContext)

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

class OffscreenPass2d(texWidth: Int, texHeight: Int, mipLevels: Int) : OffscreenPass(texWidth, texHeight, mipLevels, 1) {
    val impl = OffscreenPass2dImpl(this)

    var onSetup: ((KoolContext) -> Unit)? = null

    override fun render(ctx: KoolContext) {
        scene?.let { scene ->
            ctx.pushAttributes()
            ctx.viewport = KoolContext.Viewport(0, 0, mipWidth(targetMipLevel), mipHeight(targetMipLevel))
            onSetup?.invoke(ctx)

            scene.drawQueue = drawQueues[0].also { it.clear() }
            scene.renderScene(ctx)

            ctx.popAttributes()
        }
        frameIdx++
    }
}

class OffscreenPassCube(texWidth: Int, texHeight: Int, mipLevels: Int) : OffscreenPass(texWidth, texHeight, mipLevels, 6) {
    val impl = OffscreenPassCubeImpl(this)

    var onSetup: ((KoolContext) -> Unit)? = null
    var onSetupView: ((ViewDirection, KoolContext) -> Unit)? = null

    init {
        defaultCubeMapCameraConfig()
    }

    override fun render(ctx: KoolContext) {
        scene?.let { scene ->
            ctx.pushAttributes()
            ctx.viewport = KoolContext.Viewport(0, 0, mipWidth(targetMipLevel), mipHeight(targetMipLevel))
            onSetup?.invoke(ctx)

            for (v in ViewDirection.values()) {
                onSetupView?.invoke(v, ctx)
                scene.drawQueue = drawQueues[v.index].also { it.clear() }
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

        onSetupView = { viewDir, _ ->
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

expect class OffscreenPass2dImpl(offscreenPass: OffscreenPass2d) {
    val texture: Texture
}

expect class OffscreenPassCubeImpl(offscreenPass: OffscreenPassCube) {
    val texture: CubeMapTexture
}