package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.Color

/**
 * @author fabmax
 */

fun multiSceneDemo(ctx: RenderContext) {
    val leftScene = simpleShapesScene()
    val rightScene = uiDemoScene()

    leftScene.preRender += { ctx ->
        val width = (ctx.viewportWidth * 0.5).toInt()
        ctx.pushAttributes()
        ctx.viewportWidth = width
        ctx.applyAttributes()
    }
    leftScene.postRender += { ctx ->
        ctx.popAttributes()
    }

    rightScene.clearMask = 0
    rightScene.preRender += { ctx ->
        val width = (ctx.viewportWidth * 0.5).toInt()
        ctx.pushAttributes()
        ctx.viewportX = width
        ctx.viewportWidth = width
        ctx.applyAttributes()
    }
    rightScene.postRender += { ctx ->
        ctx.popAttributes()
    }

    ctx.scenes += leftScene
    ctx.scenes += rightScene

    ctx.run()
}
