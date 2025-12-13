package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.physics.geometry.CollisionGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.scene.OnRenderScene
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch

expect fun PhysicsWorld(scene: Scene?, isContinuousCollisionDetection: Boolean = false) : PhysicsWorld

abstract class PhysicsWorld : BaseReleasable(), InterpolatableSimulation {
    init {
        Physics.checkIsLoaded()
    }

    val simStepper: SimulationStepper = AsyncSimulationStepper(this, Physics.physicsDispatcher)
    val simulationTime: Double get() = simStepper.simulationTime

    val physicsStepListeners = BufferedList<InterpolatableSimulation>()

    protected val mutActors = mutableListOf<RigidActor>()
    val actors: List<RigidActor>
        get() = mutActors
    protected val mutArticulations = mutableListOf<Articulation>()
    val articulations: List<Articulation>
        get() = mutArticulations

    protected val triggerListeners = mutableMapOf<RigidActor, TriggerListenerContext>()
    protected val contactListeners = mutableListOf<ContactListener>()

    private var registeredAtScene: Scene? = null
    private val onRenderSceneHook = OnRenderScene { simStepper.stepPhysics() }

    abstract var gravity: Vec3f
    abstract val activeActors: Int

    open fun registerHandlers(scene: Scene) {
        unregisterHandlers()
        registeredAtScene = scene
        scene.onRenderScene += onRenderSceneHook
    }

    open fun unregisterHandlers() {
        registeredAtScene?.let { it.onRenderScene -= onRenderSceneHook }
        registeredAtScene = null
    }

    fun registerTriggerListener(trigger: RigidActor, listener: TriggerListener) {
        if (!trigger.isTrigger) {
            logW { "Given trigger actor is not a trigger (isTrigger == false)" }
        }
        triggerListeners.getOrPut(trigger) { TriggerListenerContext() }.listeners += listener
    }

    fun unregisterTriggerListener(listener: TriggerListener) {
        triggerListeners.values.forEach { it.listeners -= listener }
        triggerListeners.keys.removeAll { k -> triggerListeners[k]?.listeners?.isEmpty() ?: false }
    }

    fun registerContactListener(listener: ContactListener) {
        contactListeners += listener
    }

    fun unregisterContactListener(listener: ContactListener) {
        contactListeners -= listener
    }

    protected fun fireOnTouchFound(a: RigidActor, b: RigidActor, contactPoints: List<ContactPoint>?) {
        for (i in contactListeners.indices) {
            contactListeners[i].onTouchFound(a, b, contactPoints)
        }
    }

    protected fun fireOnTouchLost(a: RigidActor, b: RigidActor) {
        for (i in contactListeners.indices) {
            contactListeners[i].onTouchLost(a, b)
        }
    }

    override fun doRelease() {
        unregisterHandlers()
        FrontendScope.launch {
            simStepper.waitForSimulation()
            clear(true)
            releaseWorld()
        }
    }

    protected abstract fun releaseWorld()

    open fun addActor(actor: RigidActor) {
        mutActors += actor
    }

    open fun removeActor(actor: RigidActor, releaseActor: Boolean) {
        mutActors -= actor
    }

    open fun addArticulation(articulation: Articulation) {
        mutArticulations += articulation
        articulation.links.forEach { mutActors += it }
    }

    open fun removeArticulation(articulation: Articulation, releaseArticulation: Boolean) {
        mutArticulations -= articulation
        articulation.links.forEach { mutActors -= it }
    }

    abstract fun raycast(ray: RayF, maxDistance: Float, result: HitResult): Boolean
    abstract fun sweepTest(testGeometry: CollisionGeometry, geometryPose: Mat4f, testDirection: Vec3f, distance: Float, result: HitResult): Boolean

    fun wakeUpAll() {
        actors.forEach {
            if (it is RigidDynamic) {
                it.wakeUp()
            }
        }
        articulations.forEach {
            it.wakeUp()
        }
    }

    fun clear(releaseActors: Boolean = true) {
        actors.toList().forEach { removeActor(it, releaseActors) }
        articulations.toList().forEach { removeArticulation(it, releaseActors) }
        triggerListeners.clear()
    }

    override fun captureStepResults(simulationTime: Double) {
        for (i in mutActors.indices) {
            mutActors[i].capture(simulationTime)
        }
        physicsStepListeners.forEachUpdated { it.captureStepResults(simulationTime) }
    }

    override fun interpolateSteps(simulationTimePrev: Double, simulationTimeNext: Double, simulationTimeLerp: Double, weightNext: Float) {
        for (i in mutActors.indices) {
            mutActors[i].interpolateTransform(simulationTimePrev, simulationTimeNext, simulationTimeLerp, weightNext)
        }
        physicsStepListeners.forEachUpdated { it.interpolateSteps(simulationTimePrev, simulationTimeNext, simulationTimeLerp, weightNext) }
    }

    protected class TriggerListenerContext {
        val listeners = mutableListOf<TriggerListener>()
        val actorEnterCounts = mutableMapOf<RigidActor, Int>()
    }
}

/**
 * Adds a static plane with y-axis as surface normal (i.e., xz-plane) at y = 0.
 */
fun PhysicsWorld.addDefaultGroundPlane(): RigidStatic {
    val groundPlane = RigidStatic()
    val shape = Shape(PlaneGeometry(), Material(0.5f, 0.5f, 0.2f))
    groundPlane.attachShape(shape)
    groundPlane.pose = PoseF(rotation = QuatF.rotation(90f.deg, Vec3f.Z_AXIS))
    addActor(groundPlane)
    return groundPlane
}