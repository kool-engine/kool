package de.fabmax.kool.physics2d

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.releaseWith

class World(worldDef: WorldDef) : BaseReleasable() {
    internal val worldId: WorldId
    private val taskManager: Releasable?

    private val bodies = mutableMapOf<BodyId, Body>()

    init {
        val (id, taskMgr) = createWorld(worldDef)
        worldId = id
        taskManager = taskMgr
        taskManager?.releaseWith(this)
    }

    fun createBody(bodyDef: BodyDef): Body {
        val body = Body(bodyDef, this)
        bodies[body.bodyId] = body
        return body
    }

    fun simulate(timeStep: Float, substeps: Int = 4) {
        worldId.step(timeStep, substeps)
        // todo: interpolation and stuff
        bodies.values.forEach { it.fetchPose() }
    }

    override fun doRelease() {
        worldId.destroy()
    }
}

data class WorldDef(
    val gravity: Vec2f = WorldDefDefaults.gravity,
    val restitutionThreshold: Float = WorldDefDefaults.restitutionThreshold,
    val hitEventThreshold: Float = WorldDefDefaults.hitEventThreshold,
    val contactHertz: Float = WorldDefDefaults.contactHertz,
    val contactDampingRatio: Float = WorldDefDefaults.contactDampingRatio,
    val maxContactPushSpeed: Float = WorldDefDefaults.maxContactPushSpeed,
    val maximumLinearSpeed: Float = WorldDefDefaults.maximumLinearSpeed,
    val enableSleep: Boolean = WorldDefDefaults.enableSleep,
    val enableContinuous: Boolean = WorldDefDefaults.enableContinuous,
    val numThreads: Int = defaultNumThreads(),
) {
    companion object {
        val DEFAULT = WorldDef()
    }
}

fun World(
    gravity: Vec2f = WorldDefDefaults.gravity,
    restitutionThreshold: Float = WorldDefDefaults.restitutionThreshold,
    hitEventThreshold: Float = WorldDefDefaults.hitEventThreshold,
    contactHertz: Float = WorldDefDefaults.contactHertz,
    contactDampingRatio: Float = WorldDefDefaults.contactDampingRatio,
    maxContactPushSpeed: Float = WorldDefDefaults.maxContactPushSpeed,
    maximumLinearSpeed: Float = WorldDefDefaults.maximumLinearSpeed,
    enableSleep: Boolean = WorldDefDefaults.enableSleep,
    enableContinuous: Boolean = WorldDefDefaults.enableContinuous,
    numThreads: Int = defaultNumThreads(),
): World = World(
    WorldDef(
        gravity = gravity,
        restitutionThreshold = restitutionThreshold,
        hitEventThreshold = hitEventThreshold,
        contactHertz = contactHertz,
        contactDampingRatio = contactDampingRatio,
        maxContactPushSpeed = maxContactPushSpeed,
        maximumLinearSpeed = maximumLinearSpeed,
        enableSleep = enableSleep,
        enableContinuous = enableContinuous,
        numThreads = numThreads,
    )
)

fun World.createBody(
    type: BodyType,
    position: Vec2f,
    rotation: Rotation = Rotation.IDENTITY,
): Body = createBody(BodyDef(type, position, rotation))

internal expect fun createWorld(worldDef: WorldDef): Pair<WorldId, Releasable?>
internal expect fun WorldId.destroy()
internal expect fun WorldId.step(timeStep: Float, substeps: Int)

internal expect fun defaultNumThreads(): Int

internal expect object WorldDefDefaults {
    val gravity: Vec2f
    val restitutionThreshold: Float
    val hitEventThreshold: Float
    val contactHertz: Float
    val contactDampingRatio: Float
    val maxContactPushSpeed: Float
    val maximumLinearSpeed: Float
    val enableSleep: Boolean
    val enableContinuous: Boolean
}
