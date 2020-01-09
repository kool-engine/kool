package de.fabmax.kool

import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

abstract class OffscreenPass(val texWidth: Int, val texHeight: Int, val isCube: Boolean) {
    var clearColor = Color.BLACK
    var scene: Scene? = null

    val drawQueues: List<DrawQueue>

    var onRender: ((ViewDirection, KoolContext) -> Unit)? = null
    var frameIdx = 0
    var isSingleShot = false

    init {
        val views = if (isCube) { 6 } else { 1 }
        drawQueues = List(views) { DrawQueue() }
    }

    fun render(ctx: KoolContext) {
        scene?.let { scene ->
            ctx.pushAttributes()
            ctx.viewport = KoolContext.Viewport(0, 0, texWidth, texHeight)

            if (isCube) {
                for (v in ViewDirection.values()) {
                    scene.drawQueue = drawQueues[v.index].also { it.clear() }
                    onRender?.invoke(v, ctx)
                    scene.renderScene(ctx)
                }
            } else {
                scene.drawQueue = drawQueues[0].also { it.clear() }
                onRender?.invoke(ViewDirection.FRONT, ctx)
                scene.renderScene(ctx)
            }
            ctx.popAttributes()
        }
    }
}

expect class OffscreenPassImpl(texWidth: Int, texHeight: Int, isCube: Boolean = false) : OffscreenPass {
    val texture: Texture
    val textureCube: CubeMapTexture
}

enum class ViewDirection(val index: Int) {
    FRONT(0),
    BACK(1),
    LEFT(2),
    RIGHT(3),
    UP(4),
    DOWN(5)
}