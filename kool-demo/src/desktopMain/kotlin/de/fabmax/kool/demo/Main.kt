package de.fabmax.kool.demo

import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.platform.Lwjgl3Context

/**
 * @author fabmax
 */
fun main() = KoolApplication(
    config = KoolConfigJvm(
        renderBackend = Lwjgl3Context.Backend.OPEN_GL,
        windowTitle = "Kool Demo",
        windowSize = Vec2i(1600, 900)
    )
) { ctx ->
    // uncomment to load assets locally instead of from web
    //Demo.setProperty("assets.base", ".")

    // sub-directories for individual asset classes within asset base dir
    //Demo.setProperty("assets.hdri", "hdri")
    //Demo.setProperty("assets.materials", "materials")
    //Demo.setProperty("assets.models", "models")

    // launch demo
    demo(null, ctx)
}
