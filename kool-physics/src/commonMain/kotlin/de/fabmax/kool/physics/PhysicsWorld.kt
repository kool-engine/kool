package de.fabmax.kool.physics

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.physics.vehicle.VehicleManager
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import kotlin.math.min

expect class PhysicsWorld(gravity: Vec3f = Vec3f(0f, -9.81f, 0f), numWorkers: Int = 4) : CommonPhysicsWorld {
    var gravity: Vec3f

    fun raycast(ray: Ray, maxDistance: Float, result: RaycastResult): Boolean
}

abstract class CommonPhysicsWorld : Releasable {
    var physicsTime = 0.0
    var physicsTimeDesired = 0.0

    var singleStepTime = 1f / 60f
    var simTimeFactor = 1f
    var isStepAsync = true
    var isStepInProgress = false
    var smartSubSteps = true
    private var smartSubStepLimit = 5

    private val perfTimer = PerfTimer()
    var cpuTime = 0f
        private set
    var currentTimeFactor = 1f
        private set

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    protected val mutActors = mutableListOf<RigidActor>()
    val actors: List<RigidActor>
        get() = mutActors
    protected val mutArticulations = mutableListOf<Articulation>()
    val articulations: List<Articulation>
        get() = mutArticulations
    protected var vehicleManager: VehicleManager? = null

    protected val triggerListeners = mutableMapOf<RigidActor, TriggerListenerContext>()

    private var registeredAtScene: Scene? = null
    private val onRenderSceneHook: (KoolContext) -> Unit = { ctx ->
        if (isStepInProgress) {
            fetchStepResults()
        }

        stepPhysics(ctx.deltaT)

        if (isStepAsync && isContinueStep(physicsTime, physicsTimeDesired + singleStepTime * simTimeFactor, singleStepTime * simTimeFactor)) {
            singleStepPhysics()
            physicsTime += singleStepTime * simTimeFactor
        }
    }

    open fun registerHandlers(scene: Scene) {
        unregisterHandlers()
        registeredAtScene = scene
        scene.onRenderScene += onRenderSceneHook
    }

    open fun unregisterHandlers() {
        registeredAtScene?.let { it.onRenderScene -= onRenderSceneHook }
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

    override fun release() {
        if (isStepInProgress) {
            fetchStepResults()
        }
        unregisterHandlers()
        clear(true)
        vehicleManager?.release()
    }

    open fun addActor(actor: RigidActor) {
        mutActors += actor
        if (actor is Vehicle) {
            getOrDefaultVehicleManager().addVehicle(actor)
        }
    }

    open fun removeActor(actor: RigidActor) {
        mutActors -= actor
        if (actor is Vehicle) {
            vehicleManager?.addVehicle(actor)
        }
    }

    open fun addArticulation(articulation: Articulation) {
        mutArticulations += articulation
    }

    open fun removeArticulation(articulation: Articulation) {
        mutArticulations -= articulation
    }

    open fun createVehicleManager(maxVehicles: Int, surfaceFrictions: Map<Material, Float> = emptyMap()): VehicleManager {
        if (vehicleManager != null) {
            throw IllegalStateException("VehicleManager was already created (only one instance per VehicleWorld allowed)")
        }
        vehicleManager = VehicleManager(maxVehicles, this, surfaceFrictions)
        return vehicleManager!!
    }

    private fun getOrDefaultVehicleManager(): VehicleManager {
        if (vehicleManager == null) {
            logI { "Creating default VehicleManager instance with maxVehicles = $DEFAULT_MAX_NUM_VEHICLES. Consider " +
                    "calling PhysicsWorld.createVehicleManager() for more or less vehicles" }
            createVehicleManager(DEFAULT_MAX_NUM_VEHICLES)
        }
        return vehicleManager!!
    }

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
        val removeActors = mutableListOf<RigidActor>().apply { this += actors }
        for (i in removeActors.lastIndex downTo 0) {
            removeActor(removeActors[i])
            if (releaseActors) {
                removeActors[i].release()
            }
        }
        val removeArticulations = mutableListOf<Articulation>().apply { this += articulations }
        for (i in removeArticulations.lastIndex downTo 0) {
            removeArticulation(removeArticulations[i])
            if (releaseActors) {
                removeArticulations[i].release()
            }
        }
        triggerListeners.clear()
    }

    fun stepPhysics(timeStep: Float, maxSubSteps: Int = 5): Float {
        var steps = 0
        var stepLimit = if (smartSubSteps) min(smartSubStepLimit, maxSubSteps) else maxSubSteps

        physicsTimeDesired += timeStep * simTimeFactor
        while (isContinueStep(physicsTime, physicsTimeDesired, singleStepTime * simTimeFactor) && stepLimit > 0) {
            perfTimer.reset()

            singleStepPhysics()
            fetchStepResults()
            steps++

            val ms = perfTimer.takeMs().toFloat()
            cpuTime = cpuTime * 0.8f + ms * 0.2f

            physicsTime += singleStepTime * simTimeFactor
            if (isContinueStep(physicsTime, physicsTimeDesired, singleStepTime)) {
                stepLimit--
            }
        }

        if (stepLimit == 0) {
            physicsTime = physicsTimeDesired
            if (smartSubStepLimit > 1) {
                smartSubStepLimit--
            }
        } else if (smartSubStepLimit < maxSubSteps) {
            smartSubStepLimit++
        }

        val timeInc = singleStepTime * steps
        currentTimeFactor = currentTimeFactor * 0.9f + timeInc / timeStep * 0.1f

        return timeInc
    }

    private fun isContinueStep(physicsTime: Double, physicsTimeDesired: Double, step: Float): Boolean {
        return physicsTimeDesired - physicsTime > step * 0.5
    }

    protected open fun singleStepPhysics() {
        isStepInProgress = true
    }

    protected open fun fetchStepResults() {
        isStepInProgress = false
        onFixedUpdate(singleStepTime * simTimeFactor)
    }

    protected open fun onFixedUpdate(timeStep: Float) {
        vehicleManager?.onFixedUpdate(timeStep)
        for (i in mutActors.indices) {
            mutActors[i].fixedUpdate(timeStep)
        }
        for (i in mutArticulations.indices) {
            mutArticulations[i].fixedUpdate(timeStep)
        }
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }

    /**
     * Adds a static plane with y-axis as surface normal (i.e. xz-plane) at y = 0.
     */
    fun addDefaultGroundPlane(): RigidStatic {
        val groundPlane = RigidStatic()
        val shape = Shape(PlaneGeometry(), Material(0.5f, 0.5f, 0.2f))
        groundPlane.attachShape(shape)
        groundPlane.setRotation(0f, 0f, 90f)
        addActor(groundPlane)
        return groundPlane
    }

    protected class TriggerListenerContext {
        val listeners = mutableListOf<TriggerListener>()
        val actorEnterCounts = mutableMapOf<RigidActor, Int>()
    }

    companion object {
        private const val DEFAULT_MAX_NUM_VEHICLES = 64
    }
}