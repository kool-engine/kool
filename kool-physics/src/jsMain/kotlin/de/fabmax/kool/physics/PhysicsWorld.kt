package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.util.logE
import physx.*

actual class PhysicsWorld actual constructor(gravity: Vec3f, numWorkers: Int) : CommonPhysicsWorld(), Releasable {
    val scene: PxScene

    private val raycastResult = PxRaycastBuffer10()
    private val bufPxGravity = gravity.toPxVec3(PxVec3())
    private val bufGravity = MutableVec3f()
    actual var gravity: Vec3f
        get() = scene.gravity.toVec3f(bufGravity)
        set(value) {
            scene.gravity = value.toPxVec3(bufPxGravity)
        }

    private val pxActors = mutableMapOf<Int, RigidActor>()

    init {
        Physics.checkIsLoaded()

        val sceneDesc = PxSceneDesc(Physics.physics.tolerancesScale)
        sceneDesc.gravity = bufPxGravity
        // ignore numWorkers parameter and set numThreads to 0, since multi-threading is disabled for wasm
        sceneDesc.cpuDispatcher = Physics.Px.DefaultCpuDispatcherCreate(0)
        sceneDesc.filterShader = Physics.Px.DefaultFilterShader()
        sceneDesc.flags.clear(PxSceneFlagEnum.eENABLE_PCM)
        sceneDesc.simulationEventCallback = simEventCallback()
        scene = Physics.physics.createScene(sceneDesc)
    }

    override fun singleStepAsync(timeStep: Float) {
        super.singleStepAsync(timeStep)
        scene.simulate(timeStep)
    }

    override fun fetchAsyncStepResults() {
        scene.fetchResults(true)
        super.fetchAsyncStepResults()
    }

    override fun addActor(actor: RigidActor) {
        super.addActor(actor)
        scene.addActor(actor.pxRigidActor)
        pxActors[actor.pxRigidActor.address] = actor
    }

    override fun removeActor(actor: RigidActor) {
        super.removeActor(actor)
        scene.removeActor(actor.pxRigidActor)
        pxActors -= actor.pxRigidActor.address
    }

    override fun addArticulation(articulation: Articulation) {
        super.addArticulation(articulation)
        articulation.links.forEach { pxActors[it.pxLink.address] = it }
        scene.addArticulation(articulation.pxArticulation)
    }

    override fun removeArticulation(articulation: Articulation) {
        super.removeArticulation(articulation)
        articulation.links.forEach { pxActors -= it.pxLink.address }
        scene.removeArticulation(articulation.pxArticulation)
    }

    override fun release() {
        super.release()
        scene.release()
        bufPxGravity.destroy()
        raycastResult.destroy()
    }

    actual fun raycast(ray: Ray, maxDistance: Float, result: RaycastResult): Boolean {
        MemoryStack.stackPush().use { mem ->
            val ori = ray.origin.toPxVec3(mem.createPxVec3())
            val dir = ray.direction.toPxVec3(mem.createPxVec3())
            if (scene.raycast(ori, dir, maxDistance, raycastResult)) {
                var minDist = maxDistance
                var nearestHit: PxRaycastHit? = null

                for (i in 0 until raycastResult.nbAnyHits) {
                    val hit = raycastResult.getAnyHit(i)
                    if (hit.distance < minDist) {
                        minDist = hit.distance
                        nearestHit = hit
                    }
                }
                if (nearestHit != null) {
                    result.hitActor = pxActors[nearestHit.actor.address]
                    result.hitDistance = minDist
                    nearestHit.position.toVec3f(result.hitPosition)
                    nearestHit.normal.toVec3f(result.hitNormal)
                } else {
                    result.clear()
                }
            }
        }
        return result.isHit
    }

    private fun simEventCallback() = JavaSimulationEventCallback().apply {
        onConstraintBreak = { _, _ -> }
        onWake = { _, _ -> }
        onSleep = { _, _ -> }
        onContact = { _, _, _ -> }

        onTrigger = { pairs: PxTriggerPair, count: Int ->
            for (i in 0 until count) {
                val pair = Physics.TypeHelpers.getTriggerPairAt(pairs, i)
                val isEnter = pair.status == PxPairFlagEnum.eNOTIFY_TOUCH_FOUND
                val trigger = pxActors[pair.triggerActor.address]
                val actor = pxActors[pair.otherActor.address]
                if (trigger != null && actor != null) {
                    triggerListeners[trigger]?.apply {
                        var cnt = actorEnterCounts.getOrPut(actor) { 0 }
                        val shapeAddr = pair.otherShape.address
                        val shape = actor.shapes.find { it.pxShape?.address == shapeAddr }
                        if (shape == null) {
                            logE { "shape reference not found" }
                        }
                        if (isEnter) {
                            cnt++
                            if (cnt == 1) {
                                listeners.forEach { it.onActorEntered(trigger, actor) }
                            }
                            shape?.let { s -> listeners.forEach { it.onShapeEntered(trigger, actor, s) } }
                        } else {
                            cnt--
                            shape?.let { s -> listeners.forEach { it.onShapeExited(trigger, actor, s) } }
                            if (cnt == 0) {
                                listeners.forEach { it.onActorExited(trigger, actor) }
                            }
                        }
                        actorEnterCounts[actor] = cnt
                    }
                } else {
                    logE { "actor reference not found" }
                }
            }
        }
    }
}