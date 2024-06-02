package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.physics.articulations.ArticulationImpl
import de.fabmax.kool.physics.geometry.CollisionGeometry
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import physx.*

actual fun PhysicsWorld(scene: Scene?, isContinuousCollisionDetection: Boolean) : PhysicsWorld {
    return PhysicsWorldImpl(scene, isContinuousCollisionDetection)
}

class PhysicsWorldImpl(scene: Scene?, val isContinuousCollisionDetection: Boolean) : PhysicsWorld(), Releasable {
    val pxScene: PxScene

    private val raycastResult = PxRaycastResult()
    private val sweepResult = PxSweepResult()
    private val bufPxGravity = Vec3f(0f, -9.81f, 0f).toPxVec3(PxVec3())
    private val bufGravity = MutableVec3f()
    override var gravity: Vec3f
        get() = pxScene.gravity.toVec3f(bufGravity)
        set(value) {
            pxScene.gravity = value.toPxVec3(bufPxGravity)
        }

    private var mutActiveActors = 0
    override val activeActors: Int
        get() = mutActiveActors

    private val pxActors = mutableMapOf<Int, RigidActor>()

    init {
        PhysicsImpl.checkIsLoaded()

        MemoryStack.stackPush().use { mem ->
            val sceneDesc = mem.createPxSceneDesc(PhysicsImpl.physics.tolerancesScale)
            sceneDesc.gravity = bufPxGravity
            // ignore numWorkers parameter and set numThreads to 0, since multi-threading is disabled for wasm
            sceneDesc.cpuDispatcher = PhysicsImpl.defaultCpuDispatcher
            sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
            sceneDesc.simulationEventCallback = simEventCallback()
            sceneDesc.flags.raise(PxSceneFlagEnum.eENABLE_ACTIVE_ACTORS)
            if (isContinuousCollisionDetection) {
                sceneDesc.flags.raise(PxSceneFlagEnum.eENABLE_CCD)
            }
            pxScene = PhysicsImpl.physics.createScene(sceneDesc)
        }
        scene?.let { registerHandlers(it) }
    }

    override fun singleStepAsync(timeStep: Float) {
        super.singleStepAsync(timeStep)
        pxScene.simulate(timeStep)
    }

    override fun fetchAsyncStepResults() {
        pxScene.fetchResults(true)

        for (i in actors.indices) {
            actors[i].isActive = false
        }
        val activeActors = SupportFunctions.PxScene_getActiveActors(pxScene)
        mutActiveActors = activeActors.size()
        for (i in 0 until mutActiveActors) {
            pxActors[activeActors.get(i).ptr]?.isActive = true
        }

        super.fetchAsyncStepResults()
    }

    internal fun registerActorReference(actor: RigidActor) {
        pxActors[actor.holder.px.ptr] = actor
    }

    internal fun deleteActorReference(actor: RigidActor) {
        pxActors -= actor.holder.px.ptr
    }

    fun getActor(pxActor: PxActor): RigidActor? {
        return pxActors[pxActor.ptr]
    }

    override fun addActor(actor: RigidActor) {
        super.addActor(actor)
        pxScene.addActor(actor.holder.px)
        registerActorReference(actor)

        // set necessary ccd flags in case it is enabled for this scene
        val pxActor = actor.holder.px
        if (isContinuousCollisionDetection && actor is RigidBody) {
            if (actor is RigidDynamic && !actor.isKinematic) {
                // in javascript we cannot check for pxActor being an instance of PxRigidBody (because it's an external
                // interface), however if actor is RigidBody pxActor must be PxRigidBody...
                pxActor.unsafeCast<PxRigidBody>().setRigidBodyFlag(PxRigidBodyFlagEnum.eENABLE_CCD, true)
            }
            actor.simulationFilterData = FilterData {
                set(actor.simulationFilterData)
                word2 = PxPairFlagEnum.eDETECT_CCD_CONTACT
            }
        }
    }

    override fun removeActor(actor: RigidActor) {
        super.removeActor(actor)
        pxScene.removeActor(actor.holder.px)
        deleteActorReference(actor)
    }

    override fun addArticulation(articulation: Articulation) {
        super.addArticulation(articulation)
        articulation.links.forEach { pxActors[it.holder.px.ptr] = it }
        pxScene.addArticulation((articulation as ArticulationImpl).pxArticulation)
    }

    override fun removeArticulation(articulation: Articulation) {
        super.removeArticulation(articulation)
        articulation.links.forEach { pxActors -= it.holder.px.ptr }
        pxScene.removeArticulation((articulation as ArticulationImpl).pxArticulation)
    }

    override fun release() {
        super.release()
        pxScene.release()
        bufPxGravity.destroy()
        raycastResult.destroy()
        sweepResult.destroy()
    }

    override fun raycast(ray: RayF, maxDistance: Float, result: HitResult): Boolean {
        result.clear()
        MemoryStack.stackPush().use { mem ->
            val ori = ray.origin.toPxVec3(mem.createPxVec3())
            val dir = ray.direction.toPxVec3(mem.createPxVec3())
            if (pxScene.raycast(ori, dir, maxDistance, raycastResult)) {
                var minDist = maxDistance
                var nearestHit: PxRaycastHit? = null
                var nearestActor: RigidActor? = null

                for (i in 0 until raycastResult.nbAnyHits) {
                    val hit = raycastResult.getAnyHit(i)
                    val actor = pxActors[hit.actor.ptr]
                    if (actor != null && hit.distance < minDist) {
                        result.hitActors += actor
                        minDist = hit.distance
                        nearestHit = hit
                        nearestActor = actor
                    }
                }
                if (nearestHit != null) {
                    result.nearestActor = nearestActor
                    result.hitDistance = minDist
                    nearestHit.position.toVec3f(result.hitPosition)
                    nearestHit.normal.toVec3f(result.hitNormal)
                }
            }
        }
        return result.isHit
    }

    override fun sweepTest(testGeometry: CollisionGeometry, geometryPose: Mat4f, testDirection: Vec3f, distance: Float, result: HitResult): Boolean {
        result.clear()
        MemoryStack.stackPush().use { mem ->
            val sweepPose = geometryPose.toPxTransform(mem.createPxTransform())
            val sweepDir = testDirection.toPxVec3(mem.createPxVec3())
            val geom = testGeometry.holder.px

            if (pxScene.sweep(geom, sweepPose, sweepDir, distance, sweepResult)) {
                var minDist = distance
                var nearestHit: PxSweepHit? = null
                var nearestActor: RigidActor? = null

                for (i in 0 until sweepResult.nbAnyHits) {
                    val hit = sweepResult.getAnyHit(i)
                    val actor = pxActors[hit.actor.ptr]
                    if (actor != null && hit.distance < minDist) {
                        result.hitActors += actor
                        minDist = hit.distance
                        nearestHit = hit
                        nearestActor = actor
                    }
                }
                if (nearestHit != null) {
                    result.nearestActor = nearestActor
                    result.hitDistance = minDist
                    nearestHit.position.toVec3f(result.hitPosition)
                    nearestHit.normal.toVec3f(result.hitNormal)
                }
            }
        }
        return result.isHit
    }

    private fun simEventCallback() = PxSimulationEventCallbackImpl().apply {
        val contacts = PxArray_PxContactPairPoint(64)

        onConstraintBreak = { _, _ -> }
        onWake = { _, _ -> }
        onSleep = { _, _ -> }

        onTrigger = { pairs: Int, count: Int ->
            val pairsWrapped = PxTriggerPairFromPointer(pairs)
            for (i in 0 until count) {
                val pair = NativeArrayHelpers.getTriggerPairAt(pairsWrapped, i)
                val isEnter = pair.status == PxPairFlagEnum.eNOTIFY_TOUCH_FOUND
                val trigger = pxActors[pair.triggerActor.ptr]
                val actor = pxActors[pair.otherActor.ptr]
                if (trigger != null && actor != null) {
                    triggerListeners[trigger]?.apply {
                        var cnt = actorEnterCounts.getOrPut(actor) { 0 }
                        if (isEnter) {
                            cnt++
                            if (cnt == 1) {
                                listeners.forEach { it.onActorEntered(trigger, actor) }
                            }
                        } else {
                            cnt--
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

        onContact = { ph: Int, pairs: Int, nbPairs: Int ->
            val pairsWrapped = PxContactPairFromPointer(pairs)
            val pairHeader = PxContactPairHeaderFromPointer(ph)
            val actorA = pxActors[pairHeader.get_actors(0).ptr]
            val actorB = pxActors[pairHeader.get_actors(1).ptr]

            if (actorA == null || actorB == null) {
                logW { "onContact: actor reference not found" }

            } else {
                for (i in 0 until nbPairs) {
                    val pair = NativeArrayHelpers.getContactPairAt(pairsWrapped, i)
                    val evts = pair.events

                    if (evts.isSet(PxPairFlagEnum.eNOTIFY_TOUCH_FOUND)) {
                        val contactPoints: MutableList<ContactPoint>?
                        val pxContactPoints = pair.extractContacts(contacts.begin(), 64)
                        if (pxContactPoints > 0) {
                            contactPoints = mutableListOf()
                            for (iPt in 0 until pxContactPoints) {
                                val contact = contacts.get(iPt)
                                contactPoints += ContactPoint(contact.position.toVec3f(), contact.normal.toVec3f(), contact.impulse.toVec3f(), contact.separation)
                            }
                        } else {
                            contactPoints = null
                        }
                        fireOnTouchFound(actorA, actorB, contactPoints)

                    } else if (evts.isSet(PxPairFlagEnum.eNOTIFY_TOUCH_LOST)) {
                        fireOnTouchLost(actorA, actorB)
                    }
                }
            }
        }
    }
}