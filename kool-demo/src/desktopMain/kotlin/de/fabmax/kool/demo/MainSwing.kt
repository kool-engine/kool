package de.fabmax.kool.demo

import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.platform.swing.SwingWindowSubsystem

fun main() {
    KoolApplication(
        config = KoolConfigJvm(
            renderBackend = RenderBackendVk,
            windowTitle = "Kool Demo",
            windowSize = Vec2i(1600, 900),
            windowSubsystem = SwingWindowSubsystem.simpleWindow("Kool Demo @ Swing")
        )
    ) {
        // launch demo
        demo(null, ctx)
    }
}