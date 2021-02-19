package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import physx.PxTopLevelFunctions
import physx.common.PxVec3
import physx.physics.PxScene
import physx.physics.PxSceneDesc
import physx.physics.PxSceneFlagEnum

actual class PhysicsWorld actual constructor(gravity: Vec3f, numWorkers: Int) : CommonPhysicsWorld(), Releasable {
    val scene: PxScene

    private val bufPxGravity = gravity.toPxVec3(PxVec3())
    private val bufGravity = MutableVec3f()
    actual var gravity: Vec3f
        get() = scene.gravity.toVec3f(bufGravity)
        set(value) {
            scene.gravity = value.toPxVec3(bufPxGravity)
        }

    init {
        Physics.checkIsLoaded()

        val sceneDesc = PxSceneDesc(Physics.physics.tolerancesScale)
        sceneDesc.gravity = bufPxGravity
        sceneDesc.cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(8)
        sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
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
        super.release()
        scene.release()
        bufPxGravity.destroy()
    }
}