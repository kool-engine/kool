package de.fabmax.kool.physics2d

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.Releasable

internal actual fun createWorld(worldDef: WorldDef): Pair<WorldId, Releasable?> = TODO()

internal actual fun WorldId.destroy(): Unit = TODO()

internal actual fun WorldId.step(timeStep: Float, substeps: Int): Unit = TODO()

internal actual fun defaultNumThreads(): Int = 1

internal actual object WorldDefDefaults {
    actual val gravity: Vec2f = TODO()
    actual val restitutionThreshold: Float = TODO()
    actual val hitEventThreshold: Float = TODO()
    actual val contactHertz: Float = TODO()
    actual val contactDampingRatio: Float = TODO()
    actual val maxContactPushSpeed: Float = TODO()
    actual val maximumLinearSpeed: Float = TODO()
    actual val enableSleep: Boolean = TODO()
    actual val enableContinuous: Boolean = TODO()
}