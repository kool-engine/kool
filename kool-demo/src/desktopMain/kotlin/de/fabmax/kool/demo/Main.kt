package de.fabmax.kool.demo

import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.math.Vec2i

/**
 * @author fabmax
 */
fun main() = KoolApplication(
    config = KoolConfigJvm(
        renderBackend = KoolConfigJvm.Backend.OPEN_GL,
        windowTitle = "Kool Demo",
        windowSize = Vec2i(1600, 900)
    )
) {
    // uncomment to load assets locally instead of from web
    //Demo.setProperty("assets.base", ".")

    // subdirectories for individual asset classes within asset base dir
    //Demo.setProperty("assets.hdri", "hdri")
    //Demo.setProperty("assets.materials", "materials")
    //Demo.setProperty("assets.models", "models")

    // launch demo
    demo(null, ctx)
}
