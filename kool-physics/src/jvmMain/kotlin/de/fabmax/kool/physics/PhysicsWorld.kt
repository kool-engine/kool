package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.physics.geometry.CollisionGeometry
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.common.PxVec3
import physx.physics.*
import physx.support.SupportFunctions
import physx.support.Vector_PxContactPairPoint
import kotlin.collections.set

actual class PhysicsWorld(scene: Scene?, val isContinuousCollisionDetection: Boolean, val tryCuda: Boolean) : CommonPhysicsWorld(), Releasable {
    val pxScene: PxScene

    private val raycastResult = PxRaycastResult()
    private val sweepResult = PxSweepResult()
    private val bufPxGravity = Vec3f(0f, -9.81f, 0f).toPxVec3(PxVec3())
    private val bufGravity = MutableVec3f()
    actual var gravity: Vec3f
        get() = pxScene.gravity.toVec3f(bufGravity)
        set(value) {
            pxScene.gravity = value.toPxVec3(bufPxGravity)
        }

    private var mutActiveActors = 0
    actual val activeActors: Int
        get() = mutActiveActors

    private val pxActors = mutableMapOf<PxActor, RigidActor>()

    actual constructor(scene: Scene?, isContinuousCollisionDetection: Boolean): this(scene, isContinuousCollisionDetection, Physics.cudaManager != null)

    init {
        Physics.checkIsLoaded()

        MemoryStack.stackPush().use { mem ->
            if (tryCuda && Physics.cudaManager == null) {
                logW { "CUDA is not available (either CUDA runtime lib is missing or no CUDA capable device was found). Falling back to regular CPU physics." }
            }

            val sceneDesc = PxSceneDesc.createAt(mem, MemoryStack::nmalloc, Physics.physics.tolerancesScale)
            sceneDesc.gravity = bufPxGravity
            sceneDesc.cpuDispatcher = Physics.defaultCpuDispatcher
            sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
            sceneDesc.simulationEventCallback = SimEventCallback()
            sceneDesc.flags.raise(PxSceneFlagEnum.eENABLE_ACTIVE_ACTORS)
            if (isContinuousCollisionDetection) {
                sceneDesc.flags.raise(PxSceneFlagEnum.eENABLE_CCD)
            }
            if (tryCuda && Physics.cudaManager != null) {
                sceneDesc.cudaContextManager = Physics.cudaManager
                sceneDesc.flags.raise(PxSceneFlagEnum.eENABLE_GPU_DYNAMICS)
                sceneDesc.broadPhaseType = PxBroadPhaseTypeEnum.eGPU
                sceneDesc.gpuDynamicsConfig.maxRigidPatchCount *= 8
                logI { "Using CUDA acceleration for PhysX scene" }
            }
            pxScene = Physics.physics.createScene(sceneDesc)
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
            pxActors[activeActors.at(i)]?.isActive = true
        }

        super.fetchAsyncStepResults()
    }

    fun getActor(pxActor: PxActor): RigidActor? {
        return pxActors[pxActor]
    }

    override fun addActor(actor: RigidActor) {
        super.addActor(actor)
        pxScene.addActor(actor.pxRigidActor)
        pxActors[actor.pxRigidActor] = actor

        // set necessary ccd flags in case it is enabled for this scene
        val pxActor = actor.pxRigidActor
        if (isContinuousCollisionDetection && pxActor is PxRigidBody) {
            pxActor.setRigidBodyFlag(PxRigidBodyFlagEnum.eENABLE_CCD, true)
            actor.simulationFilterData = FilterData {
                set(actor.simulationFilterData)
                word2 = PxPairFlagEnum.eDETECT_CCD_CONTACT.value
            }
        }
    }

    override fun removeActor(actor: RigidActor) {
        super.removeActor(actor)
        pxScene.removeActor(actor.pxRigidActor)
        pxActors -= actor.pxRigidActor
    }

    override fun addArticulation(articulation: Articulation) {
        super.addArticulation(articulation)
        articulation.links.forEach { pxActors[it.pxLink] = it }
        pxScene.addArticulation(articulation.pxArticulation)
    }

    override fun removeArticulation(articulation: Articulation) {
        super.removeArticulation(articulation)
        articulation.links.forEach { pxActors -= it.pxLink }
        pxScene.removeArticulation(articulation.pxArticulation)
    }

    override fun release() {
        super.release()
        pxScene.release()
        bufPxGravity.destroy()
        raycastResult.destroy()
        sweepResult.destroy()
    }

    actual fun raycast(ray: Ray, maxDistance: Float, result: HitResult): Boolean {
        result.clear()
        MemoryStack.stackPush().use { mem ->
            synchronized(raycastResult) {
                val ori = ray.origin.toPxVec3(mem.createPxVec3())
                val dir = ray.direction.toPxVec3(mem.createPxVec3())
                if (pxScene.raycast(ori, dir, maxDistance, raycastResult)) {
                    var minDist = maxDistance
                    var nearestHit: PxRaycastHit? = null
                    var nearestActor: RigidActor? = null

                    for (i in 0 until raycastResult.nbAnyHits) {
                        val hit = raycastResult.getAnyHit(i)
                        val actor = pxActors[hit.actor]
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

    actual fun sweepTest(testGeometry: CollisionGeometry, geometryPose: Mat4f, testDirection: Vec3f, distance: Float, result: HitResult): Boolean {
        result.clear()
        MemoryStack.stackPush().use { mem ->
            val sweepPose = geometryPose.toPxTransform(mem.createPxTransform())
            val sweepDir = testDirection.toPxVec3(mem.createPxVec3())

            if (pxScene.sweep(testGeometry.pxGeometry, sweepPose, sweepDir, distance, sweepResult)) {
                var minDist = distance
                var nearestHit: PxSweepHit? = null
                var nearestActor: RigidActor? = null

                for (i in 0 until sweepResult.nbAnyHits) {
                    val hit = sweepResult.getAnyHit(i)
                    val actor = pxActors[hit.actor]
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

    private inner class SimEventCallback : PxSimulationEventCallbackImpl() {
        val contacts = Vector_PxContactPairPoint(64)

        override fun onTrigger(pairs: PxTriggerPair, count: Int) {
            for (i in 0 until count) {
                val pair = PxTriggerPair.arrayGet(pairs.address, i)
                val isEnter = pair.status == PxPairFlagEnum.eNOTIFY_TOUCH_FOUND
                val trigger = pxActors[pair.triggerActor]
                val actor = pxActors[pair.otherActor]

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
            val actorA = pxActors[pairHeader.getActors(0)]
            val actorB = pxActors[pairHeader.getActors(1)]

            if (actorA == null || actorB == null) {
                logW { "onContact: actor reference not found" }
                return
            }

            for (i in 0 until nbPairs) {
                val pair = PxContactPair.arrayGet(pairs.address, i)
                val evts = pair.events

                if (evts.isSet(PxPairFlagEnum.eNOTIFY_TOUCH_FOUND)) {
                    val contactPoints: MutableList<ContactPoint>?
                    val pxContactPoints = pair.extractContacts(contacts.data(), 64)
                    if (pxContactPoints > 0) {
                        contactPoints = mutableListOf()
                        for (iPt in 0 until pxContactPoints) {
                            val contact = contacts.at(iPt)
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