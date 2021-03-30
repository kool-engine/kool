package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.util.logE
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.common.PxVec3
import physx.physics.*
import physx.support.TypeHelpers

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

    private val pxActors = mutableMapOf<PxRigidActor, RigidActor>()

    init {
        Physics.checkIsLoaded()

        val sceneDesc = PxSceneDesc(Physics.physics.tolerancesScale)
        sceneDesc.gravity = bufPxGravity
        sceneDesc.cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(8)
        sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
        sceneDesc.flags.set(PxSceneFlagEnum.eENABLE_CCD)
        sceneDesc.simulationEventCallback = SimEventCallback()
        scene = Physics.physics.createScene(sceneDesc)
    }

    override fun singleStepPhysics() {
        super.singleStepPhysics()
        scene.simulate(singleStepTime * simTimeFactor)
    }

    override fun fetchStepResults() {
        scene.fetchResults(true)
        super.fetchStepResults()
    }

    override fun addActor(actor: RigidActor) {
        super.addActor(actor)
        scene.addActor(actor.pxRigidActor)
        pxActors[actor.pxRigidActor] = actor
    }

    override fun removeActor(actor: RigidActor) {
        super.removeActor(actor)
        scene.removeActor(actor.pxRigidActor)
        pxActors -= actor.pxRigidActor
    }

    override fun addArticulation(articulation: Articulation) {
        super.addArticulation(articulation)
        articulation.links.forEach { pxActors[it.pxLink] = it }
        scene.addArticulation(articulation.pxArticulation)
    }

    override fun removeArticulation(articulation: Articulation) {
        super.removeArticulation(articulation)
        articulation.links.forEach { pxActors -= it.pxLink }
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
            synchronized(raycastResult) {
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
                        result.hitActor = pxActors[nearestHit.actor]
                        result.hitDistance = minDist
                        nearestHit.position.toVec3f(result.hitPosition)
                        nearestHit.normal.toVec3f(result.hitNormal)
                    } else {
                        result.clear()
                    }
                }
            }
        }
        return result.isHit
    }

    private inner class SimEventCallback : JavaSimulationEventCallback() {
        override fun onTrigger(pairs: PxTriggerPair, count: Int) {
            for (i in 0 until count) {
                val pair = TypeHelpers.getTriggerPairAt(pairs, i)
                val isEnter = pair.status == PxPairFlagEnum.eNOTIFY_TOUCH_FOUND
                val trigger = pxActors[pair.triggerActor]
                val actor = pxActors[pair.otherActor]
                if (trigger != null && actor != null) {
                    triggerListeners[trigger]?.apply {
                        var cnt = actorEnterCounts.getOrPut(actor) { 0 }
                        val shape = actor.shapes.find { it.pxShape == pair.otherShape }
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