package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import physx.PxScene
import physx.PxSceneDesc
import physx.PxSceneFlagEnum
import physx.PxVec3

actual class PhysicsWorld actual constructor(gravity: Vec3f, numWorkers: Int) : CommonPhysicsWorld(), Releasable {
    val scene: PxScene

    private val bufPxGravity = gravity.toPxVec3(PxVec3())
    private val bufGravity = MutableVec3f()
    actual var gravity: Vec3f
        get() = scene.getGravity().toVec3f(bufGravity)
        set(value) {
            scene.setGravity(value.toPxVec3(bufPxGravity))
        }

    init {
        val sceneDesc = PxSceneDesc(Physics.physics.getTolerancesScale())
        sceneDesc.gravity = bufPxGravity
        // ignore numWorkers parameter and set numThreads to 0, since multi-threading is disabled for wasm
        sceneDesc.cpuDispatcher = Physics.Px.DefaultCpuDispatcherCreate(0)
        sceneDesc.filterShader = Physics.Px.DefaultFilterShader()
        sceneDesc.flags.set(PxSceneFlagEnum.eENABLE_CCD)
        scene = Physics.physics.createScene(sceneDesc)
    }

    override fun singleStepPhysics() {
        super.singleStepPhysics()
        scene.simulate(singleStepTime)
    }

    override fun fetchStepResults() {
        scene.fetchResults(true)
        super.fetchStepResults()
    }

    override fun addActor(actor: RigidActor) {
        super.addActor(actor)
        scene.addActor(actor.pxRigidActor)
    }

    override fun removeActor(actor: RigidActor) {
        super.removeActor(actor)
        scene.removeActor(actor.pxRigidActor)
    }

    override fun release() {
        scene.release()
    }
}