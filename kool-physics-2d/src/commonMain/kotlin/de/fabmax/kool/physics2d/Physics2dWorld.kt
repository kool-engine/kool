package de.fabmax.kool.physics2d

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.scene.OnRenderScene
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*

class Physics2dWorld(worldDef: WorldDef, private val substeps: Int = 4) : BaseReleasable(), InterpolatableSimulation {
    internal val worldId: WorldId
    private val taskManager: Releasable?

    private val bodies = mutableMapOf<BodyId, Body>()

    val simStepper: SimulationStepper = AsyncSimulationStepper(this, Physics2d.system.physicsDispatcher)

    private var registeredAtScene: Scene? = null
    private val onRenderSceneHook = OnRenderScene { simStepper.stepPhysics() }

    init {
        val (id, taskMgr) = createWorld(worldDef)
        worldId = id
        taskManager = taskMgr
        taskManager?.releaseWith(this)
    }

    fun registerHandlers(scene: Scene) {
        unregisterHandlers()
        registeredAtScene = scene
        scene.onRenderScene += onRenderSceneHook
    }

    fun unregisterHandlers() {
        registeredAtScene?.let { it.onRenderScene -= onRenderSceneHook }
        registeredAtScene = null
    }

    fun createBody(bodyDef: BodyDef): Body {
        val body = Body(bodyDef, this)
        bodies[body.bodyId] = body
        return body
    }

    override fun simulateStep(timeStep: Float) {
        worldId.step(timeStep, substeps)
        // todo: interpolation and stuff
        bodies.values.forEach { it.fetchPose() }
    }

    override fun captureStepResults(simulationTime: Double) {
        TODO("Not yet implemented")
    }

    override fun interpolateSteps(
        simulationTimePrev: Double,
        simulationTimeNext: Double,
        simulationTimeLerp: Double,
        weightNext: Float
    ) {
        TODO("Not yet implemented")
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
): Physics2dWorld = Physics2dWorld(
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

fun Physics2dWorld.createBody(
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
