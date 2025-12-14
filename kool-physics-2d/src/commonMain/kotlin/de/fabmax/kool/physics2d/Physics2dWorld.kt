package de.fabmax.kool.physics2d

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.scene.OnRenderScene
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*

class Physics2dWorld(
    worldDef: WorldDef,
    simulationTimeStep: Float = 1f / 60f,
    private val substeps: Int = 4
) : BaseReleasable() {
    internal val worldId: WorldId
    private val taskManager: Releasable?

    private val bodies = mutableMapOf<BodyId, Body>()
    val simulationListeners = BufferedList<InterpolatableSimulation>()

    private var registeredAtScene: Scene? = null
    private val onRenderSceneHook = OnRenderScene { simStepper.stepPhysics() }

    val simStepper: SimulationStepper = AsyncSimulationStepper(
        simulation = SimStepCallbacks(),
        simulationCoroutineContext = Physics2d.system.physicsDispatcher,
        simulationTimeStep = simulationTimeStep
    )

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
        val bodyId = createBody(bodyDef, worldId)
        val body = Body(bodyDef, bodyId)
        bodies[body.bodyId] = body
        return body
    }

    fun removeBody(body: Body) {
        bodies -= body.bodyId
        body.destroy()
    }

    override fun doRelease() {
        worldId.destroy()
    }

    private inner class SimStepCallbacks : InterpolatableSimulation {
        override fun simulateStep(timeStep: Float) {
            simulationListeners.forEachUpdated { it.simulateStep(timeStep) }
            worldId.step(timeStep, substeps)
        }

        override fun captureStepResults(simulationTime: Double) {
            bodies.values.forEach { it.fetchData() }
            simulationListeners.forEachUpdated { it.captureStepResults(simulationTime) }
        }

        override fun interpolateSteps(simulationTimePrev: Double, simulationTimeNext: Double, simulationTimeLerp: Double, weightNext: Float) {
            bodies.values.forEach { it.lerpData(weightNext) }
            simulationListeners.forEachUpdated { it.interpolateSteps(simulationTimePrev, simulationTimeNext, simulationTimeLerp, weightNext) }
        }
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

fun Physics2dWorld(
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
