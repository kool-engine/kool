package de.fabmax.kool.physics2d

import box2dandroid.B2_World
import box2dandroid.b2WorldDef
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.memStack
import kotlin.math.max
import kotlin.math.min

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

        val taskManager = null
        // todo:
//        if (worldDef.numThreads <= 1) null else {
//            val taskManager = DefaultTaskManager(b2WorldDef, worldDef.numThreads)
//            Releasable { taskManager.destroy() }
//        }

        WorldId(B2_World.createWorld(b2WorldDef)) to taskManager
    }
}

internal actual fun WorldId.destroy() = B2_World.destroyWorld(id)

internal actual fun WorldId.step(timeStep: Float, substeps: Int) = B2_World.step(id, timeStep, substeps)

internal actual fun defaultNumThreads(): Int {
    // try to choose a sensible number of worker threads
    return min(16, max(1, Runtime.getRuntime().availableProcessors() - 2))
}

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