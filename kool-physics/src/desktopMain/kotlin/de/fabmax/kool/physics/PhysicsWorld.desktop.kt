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
import de.fabmax.kool.util.memStack
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import physx.PxTopLevelFunctions
import physx.common.PxVec3
import physx.physics.*
import physx.support.PxArray_PxContactPairPoint
import physx.support.SupportFunctions

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

    private val pxActors = mutableMapOf<Long, RigidActor>()
    private val addActors = mutableSetOf<RigidActor>()
    private val removeActors = mutableMapOf<RigidActor, Boolean>()
    private val addArticulations = mutableSetOf<Articulation>()
    private val removeArticulations = mutableMapOf<Articulation, Boolean>()
    private val addRemoveLock = SynchronizedObject()

    init {
        PhysicsImpl.checkIsLoaded()

        memStack {
            val sceneDesc = createPxSceneDesc(PhysicsImpl.physics.tolerancesScale)
            sceneDesc.gravity = bufPxGravity
            sceneDesc.cpuDispatcher = PhysicsImpl.defaultCpuDispatcher
            sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
            sceneDesc.simulationEventCallback = SimEventCallback()
            sceneDesc.flags.raise(PxSceneFlagEnum.eENABLE_ACTIVE_ACTORS)
            if (isContinuousCollisionDetection) {
                sceneDesc.flags.raise(PxSceneFlagEnum.eENABLE_CCD)
            }
            pxScene = PhysicsImpl.physics.createScene(sceneDesc)
        }
        scene?.let { registerHandlers(it) }
    }

    override fun simulate(timeStep: Float) {
        addAndRemoveActors()
        pxScene.simulate(timeStep)
        pxScene.fetchResults(true)

        for (i in actors.indices) {
            actors[i].isActive = false
        }
        val activeActors = SupportFunctions.PxScene_getActiveActors(pxScene)
        mutActiveActors = activeActors.size()
        for (i in 0 until mutActiveActors) {
            pxActors[activeActors.get(i).ptr]?.let {
                it.isActive = true
                it.syncSimulationData()
            }
        }
    }

    fun getActor(pxActor: PxActor): RigidActor? {
        return pxActors[pxActor.ptr]
    }

    override fun addActor(actor: RigidActor) {
        super.addActor(actor)
        synchronized(addRemoveLock) {
            removeActors -= actor
            addActors += actor
        }
    }

    override fun removeActor(actor: RigidActor, releaseActor: Boolean) {
        super.removeActor(actor, releaseActor)
        synchronized(addRemoveLock) {
            if (!addActors.remove(actor)) {
                removeActors += actor to releaseActor
            }
        }
    }

    override fun addArticulation(articulation: Articulation) {
        super.addArticulation(articulation)
        synchronized(addRemoveLock) {
            removeArticulations -= articulation
            addArticulations += articulation
        }
    }

    override fun removeArticulation(articulation: Articulation, releaseArticulation: Boolean) {
        super.removeArticulation(articulation, releaseArticulation)
        synchronized(addRemoveLock) {
            if (!addArticulations.remove(articulation)) {
                removeArticulations += articulation to releaseArticulation
            }
        }
    }

    override fun releaseWorld() {
        pxScene.release()
        bufPxGravity.destroy()
        raycastResult.destroy()
        sweepResult.destroy()
    }

    override fun raycast(ray: RayF, maxDistance: Float, result: HitResult): Boolean {
        result.clear()
        memStack {
            synchronized(raycastResult) {
                val ori = ray.origin.toPxVec3(createPxVec3())
                val dir = ray.direction.toPxVec3(createPxVec3())
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
        }
        return result.isHit
    }

    override fun sweepTest(testGeometry: CollisionGeometry, geometryPose: Mat4f, testDirection: Vec3f, distance: Float, result: HitResult): Boolean {
        result.clear()
        memStack {
            val sweepPose = geometryPose.toPxTransform(createPxTransform())
            val sweepDir = testDirection.toPxVec3(createPxVec3())
            if (pxScene.sweep(testGeometry.holder.px, sweepPose, sweepDir, distance, sweepResult)) {
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

    internal fun registerActorReference(actor: RigidActor) {
        pxActors[actor.holder.px.ptr] = actor
    }

    internal fun deleteActorReference(actor: RigidActor) {
        pxActors -= actor.holder.px.ptr
    }

    private fun addAndRemoveActors() {
        synchronized(addRemoveLock) {
            if (addActors.isNotEmpty()) {
                addActors.forEach { actor ->
                    pxScene.addActor(actor.holder.px)
                    (actor as RigidActorImpl).isAttachedToSimulation = true
                    registerActorReference(actor)
                    if (isContinuousCollisionDetection) {
                        actor.enableCcd()
                    }
                }
                addActors.clear()
            }
            if (removeActors.isNotEmpty()) {
                removeActors.forEach { (actor, release) ->
                    pxScene.removeActor(actor.holder.px)
                    (actor as RigidActorImpl).isAttachedToSimulation = false
                    deleteActorReference(actor)
                    if (release) {
                        actor.release()
                    }
                }
                removeActors.clear()
            }
            if (addArticulations.isNotEmpty()) {
                addArticulations.forEach { articulation ->
                    articulation.links.forEach { registerActorReference(it) }
                    pxScene.addArticulation((articulation as ArticulationImpl).pxArticulation)
                }
                addArticulations.clear()
            }
            if (removeArticulations.isNotEmpty()) {
                removeArticulations.forEach { (articulation, release) ->
                    articulation.links.forEach { deleteActorReference(it) }
                    pxScene.removeArticulation((articulation as ArticulationImpl).pxArticulation)
                    if (release) {
                        articulation.release()
                    }
                }
                removeArticulations.clear()
            }
        }
    }

    private fun RigidActor.enableCcd() {
        val pxActor = holder.px
        if (this !is RigidBody) {
            return
        }
        if (this is RigidDynamic && !isKinematic) {
            (pxActor as PxRigidBody).setRigidBodyFlag(PxRigidBodyFlagEnum.eENABLE_CCD, true)
        }
        simulationFilterData = FilterData {
            set(simulationFilterData)
            word2 = PxPairFlagEnum.eDETECT_CCD_CONTACT.value
        }
    }

    private inner class SimEventCallback : PxSimulationEventCallbackImpl() {
        val contacts = PxArray_PxContactPairPoint(64)

        override fun onTrigger(pairs: PxTriggerPair, count: Int) {
            for (i in 0 until count) {
                val pair = PxTriggerPair.arrayGet(pairs.address, i)
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

        override fun onContact(pairHeader: PxContactPairHeader, pairs: PxContactPair, nbPairs: Int) {
            val actorA = pxActors[pairHeader.getActors(0).ptr]
            val actorB = pxActors[pairHeader.getActors(1).ptr]

            if (actorA == null || actorB == null) {
                logW { "onContact: actor reference not found" }
            } else {
                for (i in 0 until nbPairs) {
                    val pair = PxContactPair.arrayGet(pairs.address, i)
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