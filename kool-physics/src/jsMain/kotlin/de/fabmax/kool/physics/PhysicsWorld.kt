package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import physx.*

actual class PhysicsWorld actual constructor(scene: Scene?, val isContinuousCollisionDetection: Boolean, numWorkers: Int) : CommonPhysicsWorld(), Releasable {
    val pxScene: PxScene

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

    private val pxActors = mutableMapOf<Int, RigidActor>()

    init {
        Physics.checkIsLoaded()

        MemoryStack.stackPush().use { mem ->
            var flags = PxSceneFlagEnum.eENABLE_ACTIVE_ACTORS
            if (isContinuousCollisionDetection) {
                flags = flags or PxSceneFlagEnum.eENABLE_CCD
            }
            val sceneDesc = mem.createPxSceneDesc(Physics.physics.tolerancesScale)
            sceneDesc.gravity = bufPxGravity
            // ignore numWorkers parameter and set numThreads to 0, since multi-threading is disabled for wasm
            sceneDesc.cpuDispatcher = Physics.Px.DefaultCpuDispatcherCreate(0)
            sceneDesc.filterShader = Physics.Px.DefaultFilterShader()
            sceneDesc.simulationEventCallback = simEventCallback()
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
        val activeActors = Physics.SupportFunctions.PxScene_getActiveActors(pxScene)
        mutActiveActors = activeActors.size()
        for (i in 0 until mutActiveActors) {
            pxActors[activeActors.at(i).ptr]?.isActive = true
        }

        super.fetchAsyncStepResults()
    }

    fun getActor(pxActor: PxActor): RigidActor? {
        return pxActors[pxActor.ptr]
    }

    override fun addActor(actor: RigidActor) {
        super.addActor(actor)
        pxScene.addActor(actor.pxRigidActor)
        pxActors[actor.pxRigidActor.ptr] = actor

        // set necessary ccd flags in case it is enabled for this scene
        val pxActor = actor.pxRigidActor
        if (isContinuousCollisionDetection && actor is RigidBody) {
            // in javascript we cannot check for pxActor being an instance of PxRigidBody (because it's an external
            // interface), however if actor is RigidBody pxActor must be PxRigidBody...
            pxActor.unsafeCast<PxRigidBody>().setRigidBodyFlag(PxRigidBodyFlagEnum.eENABLE_CCD, true)
            actor.simulationFilterData = FilterData {
                set(actor.simulationFilterData)
                word2 = PxPairFlagEnum.eDETECT_CCD_CONTACT
            }
        }
    }

    override fun removeActor(actor: RigidActor) {
        super.removeActor(actor)
        pxScene.removeActor(actor.pxRigidActor)
        pxActors -= actor.pxRigidActor.ptr
    }

    override fun addArticulation(articulation: Articulation) {
        super.addArticulation(articulation)
        articulation.links.forEach { pxActors[it.pxLink.ptr] = it }
        pxScene.addArticulation(articulation.pxArticulation)
    }

    override fun removeArticulation(articulation: Articulation) {
        super.removeArticulation(articulation)
        articulation.links.forEach { pxActors -= it.pxLink.ptr }
        pxScene.removeArticulation(articulation.pxArticulation)
    }

    override fun release() {
        super.release()
        pxScene.release()
        bufPxGravity.destroy()
        raycastResult.destroy()
    }

    actual fun raycast(ray: Ray, maxDistance: Float, result: RaycastResult): Boolean {
        MemoryStack.stackPush().use { mem ->
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
                    result.hitActor = pxActors[nearestHit.actor.ptr]
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
        val contacts = Vector_PxContactPairPoint(64)

        onConstraintBreak = { _, _ -> }
        onWake = { _, _ -> }
        onSleep = { _, _ -> }

        onTrigger = { pairs: PxTriggerPair, count: Int ->
            for (i in 0 until count) {
                val pair = Physics.TypeHelpers.getTriggerPairAt(pairs, i)
                val isEnter = pair.status == PxPairFlagEnum.eNOTIFY_TOUCH_FOUND
                val trigger = pxActors[pair.triggerActor.ptr]
                val actor = pxActors[pair.otherActor.ptr]
                if (trigger != null && actor != null) {
                    triggerListeners[trigger]?.apply {
                        var cnt = actorEnterCounts.getOrPut(actor) { 0 }
                        val shapeAddr = pair.otherShape.ptr
                        val shape = actor.shapes.find { it.pxShape?.ptr == shapeAddr }
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

        onContact = { ph: PxContactPairHeader, pairs: PxContactPair, nbPairs: Int ->
            val actorA = pxActors[Physics.SupportFunctions.PxContactPairHeader_getActor(ph, 0).ptr]
            val actorB = pxActors[Physics.SupportFunctions.PxContactPairHeader_getActor(ph, 1).ptr]

            if (actorA == null || actorB == null) {
                logW { "onContact: actor reference not found" }

            } else {
                for (i in 0 until nbPairs) {
                    val pair = Physics.TypeHelpers.getContactPairAt(pairs, i)
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
}