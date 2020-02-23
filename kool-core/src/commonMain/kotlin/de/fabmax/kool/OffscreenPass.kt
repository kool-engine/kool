package de.fabmax.kool

import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

abstract class OffscreenPass(val texWidth: Int, val texHeight: Int, val mipLevels: Int, val colorFormat: TexFormat, nQueues: Int) {
    var clearColor = Color.BLACK
    var scene: Scene? = null

    val drawQueues: List<DrawQueue> = List(nQueues) { DrawQueue() }

    var frameIdx = 0
    var isSingleShot = false
    var isMainPass = true
    var targetMipLevel = -1

    abstract fun render(ctx: KoolContext)

    open fun dispose(ctx: KoolContext) {
        if (isMainPass) {
            scene?.dispose(ctx)
        }
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

class OffscreenPass2d(texWidth: Int, texHeight: Int, mipLevels: Int = 1, colorFormat: TexFormat = TexFormat.RGBA) :
        OffscreenPass(texWidth, texHeight, mipLevels, colorFormat, 1) {
    val impl = OffscreenPass2dImpl(this)

    var onSetup: ((KoolContext) -> Unit)? = null

    val beforeRender = mutableListOf<((KoolContext) -> Unit)>()
    val afterRender = mutableListOf<((KoolContext) -> Unit)>()

    override fun render(ctx: KoolContext) {
        scene?.let { scene ->
            // fixme: viewport management is worse than before
            val vp = ctx.viewport
            ctx.viewport = KoolContext.Viewport(0, 0, mipWidth(targetMipLevel), mipHeight(targetMipLevel))
            onSetup?.invoke(ctx)
            scene.drawQueue = drawQueues[0].also { it.clear() }

            beforeRender.forEach { it(ctx) }

            if (isMainPass) {
                scene.renderScene(ctx)
            } else {
                scene.camera.updateCamera(ctx)
                scene.render(ctx)
            }
            ctx.viewport = vp

            afterRender.forEach { it(ctx) }
        }
        frameIdx++
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        impl.dispose(ctx)
    }
}

class OffscreenPassCube(texWidth: Int, texHeight: Int, mipLevels: Int, colorFormat: TexFormat = TexFormat.RGBA) :
        OffscreenPass(texWidth, texHeight, mipLevels, colorFormat, 6) {
    val impl = OffscreenPassCubeImpl(this)

    var onSetup: ((KoolContext) -> Unit)? = null
    var onSetupView: ((ViewDirection, KoolContext) -> Unit)? = null

    init {
        defaultCubeMapCameraConfig()
    }

    override fun render(ctx: KoolContext) {
        scene?.let { scene ->
            // fixme: viewport management is worse than before
            val vp = ctx.viewport
            ctx.viewport = KoolContext.Viewport(0, 0, mipWidth(targetMipLevel), mipHeight(targetMipLevel))
            onSetup?.invoke(ctx)
            for (v in ViewDirection.values()) {
                onSetupView?.invoke(v, ctx)
                scene.drawQueue = drawQueues[v.index].also { it.clear() }

                if (isMainPass) {
                    scene.renderScene(ctx)
                } else {
                    scene.camera.updateCamera(ctx)
                    scene.render(ctx)
                }

            }
            ctx.viewport = vp
        }
        frameIdx++
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        impl.dispose(ctx)
    }

    private fun defaultCubeMapCameraConfig() {
        val camDirs = mutableMapOf(
                ViewDirection.FRONT to Vec3f(0f, 0f, 1f),
                ViewDirection.BACK to Vec3f(0f, 0f, -1f),
                ViewDirection.LEFT to Vec3f(-1f, 0f, 0f),
                ViewDirection.RIGHT to Vec3f(1f, 0f, 0f),
                ViewDirection.UP to Vec3f(0f, 1f, 0f),
                ViewDirection.DOWN to Vec3f(0f, -1f, 0f)
        )

        onSetupView = { viewDir, _ ->
            scene?.apply {
                if (camera !is PerspectiveCamera) {
                    camera = PerspectiveCamera()
                }
                (camera as PerspectiveCamera).let {
                    it.position.set(Vec3f.ZERO)
                    it.fovY = 90f
                    it.clipNear = 0.1f
                    it.clipFar = 10f
                }
                camera.isApplyProjCorrection = false

                camera.lookAt.set(camDirs[viewDir]!!)
                when (viewDir) {
                    ViewDirection.UP -> camera.up.set(Vec3f.Z_AXIS)
                    ViewDirection.DOWN -> camera.up.set(Vec3f.NEG_Z_AXIS)
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
    val depthTexture: Texture

    fun dispose(ctx: KoolContext)
}

expect class OffscreenPassCubeImpl(offscreenPass: OffscreenPassCube) {
    val texture: CubeMapTexture

    fun dispose(ctx: KoolContext)
}