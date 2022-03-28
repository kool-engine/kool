package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.common.PxVec3
import physx.extensions.PxDefaultCpuDispatcher
import physx.physics.*
import physx.support.SupportFunctions
import physx.support.TypeHelpers
import physx.support.Vector_PxContactPairPoint
import kotlin.collections.set

actual class PhysicsWorld actual constructor(scene: Scene?, val isContinuousCollisionDetection: Boolean, val numWorkers: Int) : CommonPhysicsWorld(), Releasable {
    val pxScene: PxScene

    private val cpuDispatcher: PxDefaultCpuDispatcher

    private val raycastResult = PxRaycastBuffer10()
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

    private val pxActors = mutableMapOf<PxRigidActor, RigidActor>()

    init {
        Physics.checkIsLoaded()
        cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(numWorkers)

        MemoryStack.stackPush().use { mem ->
            var flags = PxSceneFlagEnum.eENABLE_ACTIVE_ACTORS
            if (isContinuousCollisionDetection) {
                flags = flags or PxSceneFlagEnum.eENABLE_CCD
            }
            val sceneDesc = PxSceneDesc.createAt(mem, MemoryStack::nmalloc, Physics.physics.tolerancesScale)
            sceneDesc.gravity = bufPxGravity
            sceneDesc.cpuDispatcher = this.cpuDispatcher
            sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
            sceneDesc.simulationEventCallback = SimEventCallback()
            sceneDesc.flags.set(flags)
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

    fun getActor(pxActor: PxRigidActor): RigidActor? {
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
                word2 = PxPairFlagEnum.eDETECT_CCD_CONTACT
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
        cpuDispatcher.destroy()
    }

    actual fun raycast(ray: Ray, maxDistance: Float, result: RaycastResult): Boolean {
        MemoryStack.stackPush().use { mem ->
            synchronized(raycastResult) {
                val ori = ray.origin.toPxVec3(mem.createPxVec3())
                val dir = ray.direction.toPxVec3(mem.createPxVec3())
                if (pxScene.raycast(ori, dir, maxDistance, raycastResult)) {
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
        val contacts = Vector_PxContactPairPoint(64)

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

        override fun onContact(pairHeader: PxContactPairHeader, pairs: PxContactPair, nbPairs: Int) {
            val actorA = pxActors[pairHeader.getActors(0)]
            val actorB = pxActors[pairHeader.getActors(1)]

            if (actorA == null || actorB == null) {
                logW { "onContact: actor reference not found" }
                return
            }

            for (i in 0 until nbPairs) {
                val pair = TypeHelpers.getContactPairAt(pairs, i)
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