package de.fabmax.kool.demo

import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk

fun main() = KoolApplication(
    config = KoolConfigJvm(
        renderBackend = RenderBackendVk,
        windowTitle = "Kool Demo",
        windowSize = Vec2i(1600, 900),

        // SdlWindowSubsystem is now used by default. Uncomment this to get the old GlfwWindowSubsystem back
        //windowSubsystem = GlfwWindowSubsystem,
    )
) {
    // uncomment to load assets locally instead of from web
    //DemoLoader.setProperty("assets.base", ".")

    // launch demo
    demo(null, ctx)
}
