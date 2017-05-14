package de.fabmax.kool.demo

import de.fabmax.kool.scene.Scene

/**
 * @author fabmax
 */

fun multiScene(): List<Scene> {
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

    // right scene must not clear the screen (otherwise, left scene is cleared as well)
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

    return listOf(leftScene, rightScene)
}
