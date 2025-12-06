package de.fabmax.kool.physics2d

import box2d.b2WorldDef
import box2d.prototypes.B2_World
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.logW
import de.fabmax.kool.util.memStack

internal actual fun createWorld(worldDef: WorldDef): Pair<WorldId, Releasable?> {
    return memStack {
        val b2WorldDef = allocWordDef()
        B2_World.defaultWorldDef(b2WorldDef)
        b2WorldDef.gravity = allocVec2(worldDef.gravity)
        b2WorldDef.restitutionThreshold = worldDef.restitutionThreshold
        b2WorldDef.hitEventThreshold = worldDef.hitEventThreshold
        b2WorldDef.contactHertz = worldDef.contactHertz
        b2WorldDef.contactDampingRatio = worldDef.contactDampingRatio
        b2WorldDef.maxContactPushSpeed = worldDef.maxContactPushSpeed
        b2WorldDef.maximumLinearSpeed = worldDef.maximumLinearSpeed
        b2WorldDef.enableSleep = worldDef.enableSleep
        b2WorldDef.enableContinuous = worldDef.enableContinuous
        if (worldDef.numThreads > 1) {
            logW { "No multi-threaded physics simulation available on JS platform." }
        }
        WorldId(B2_World.createWorld(b2WorldDef)) to null
    }
}

internal actual fun WorldId.destroy() = B2_World.destroyWorld(id)

internal actual fun WorldId.step(timeStep: Float, substeps: Int) = B2_World.step(id, timeStep, substeps)

internal actual fun defaultNumThreads(): Int = 1

internal actual object WorldDefDefaults {
    private val defaults = b2WorldDef().also { B2_World.defaultWorldDef(it) }

    actual val gravity: Vec2f = Vec2f(defaults.gravity.x, defaults.gravity.y)
    actual val restitutionThreshold: Float = defaults.restitutionThreshold
    actual val hitEventThreshold: Float = defaults.hitEventThreshold
    actual val contactHertz: Float = defaults.contactHertz
    actual val contactDampingRatio: Float = defaults.contactDampingRatio
    actual val maxContactPushSpeed: Float = defaults.maxContactPushSpeed
    actual val maximumLinearSpeed: Float = defaults.maximumLinearSpeed
    actual val enableSleep: Boolean = defaults.enableSleep
    actual val enableContinuous: Boolean = defaults.enableContinuous
}