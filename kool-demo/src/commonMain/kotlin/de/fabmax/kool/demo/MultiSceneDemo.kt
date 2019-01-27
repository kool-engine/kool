package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Scene

/**
 * @author fabmax
 */

fun multiScene(koolCtx: KoolContext): List<Scene> {
    val leftScene = simpleShapesScene(koolCtx)
    val rightScene = uiDemoScene()

    leftScene.onRenderScene += { ctx ->
        val vp = ctx.viewport
        val width = (vp.width * 0.5).toInt()
        ctx.pushAttributes()
        ctx.viewport = KoolContext.Viewport(vp.x, vp.y, width, vp.height)
        ctx.applyAttributes()
    }
    leftScene.onPostRender += { ctx ->
        ctx.popAttributes()
    }

    // right scene must not clear the screen (otherwise, left scene is cleared as well)
    rightScene.clearMask = 0
    rightScene.onRenderScene += { ctx ->
        val vp = ctx.viewport
        val width = (vp.width * 0.5).toInt()
        ctx.pushAttributes()
        ctx.viewport = KoolContext.Viewport(vp.x + width, vp.y, width, vp.height)
        ctx.applyAttributes()
    }
    rightScene.onPostRender += { ctx ->
        ctx.popAttributes()
    }

    return listOf(leftScene, rightScene)
}
