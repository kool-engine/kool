package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.joints.Joint
import de.fabmax.kool.physics.vehicle.Vehicle
import physx.*

actual class PhysicsWorld : CommonPhysicsWorld() {
    val scene: PxScene

    private val bufGravity = MutableVec3f()
    actual var gravity: Vec3f
        get() = scene.getGravity().toVec3f(bufGravity)
        set(value) {
            scene.setGravity(value.toPxVec3())
        }

    init {
        //val cb = PhysX.PxSimulationEventCallback()
        //cb.cbFun = { cnt -> numContacts += cnt }

        val sceneDesc = PxSceneDesc(Physics.physics.getTolerancesScale())
        sceneDesc.gravity = PxVec3(0f, -9.81f, 0f)
        //sceneDesc.simulationEventCallback = cb
        sceneDesc.cpuDispatcher = Physics.Px.DefaultCpuDispatcherCreate(0)
        sceneDesc.filterShader = Physics.Px.DefaultFilterShader()
        sceneDesc.flags.set(PxSceneFlagEnum.eENABLE_CCD)
        scene = Physics.physics.createScene(sceneDesc)

        Physics.PxVehicle.InitVehicleSDK(Physics.physics)
        Physics.PxVehicle.VehicleSetBasisVectors(Vec3f.Y_AXIS.toPxVec3(), Vec3f.Z_AXIS.toPxVec3())
        Physics.PxVehicle.VehicleSetUpdateMode(PxVehicleUpdateModeEnum.eVELOCITY_CHANGE)
    }

    override fun singleStepPhysicsImpl(timeStep: Float) {
        scene.simulate(timeStep)
        scene.fetchResults(true)
    }

    override fun addRigidBodyImpl(rigidBody: RigidBody) {
        scene.addActor(rigidBody.pxActor)
    }

    override fun removeRigidBodyImpl(rigidBody: RigidBody) {
        scene.removeActor(rigidBody.pxActor, true)
    }

    override fun addJointImpl(joint: Joint) {
        // nothing to do here
    }

    override fun removeJointImpl(joint: Joint) {
        // nothing to do here
    }

    override fun addVehicleImpl(vehicle: Vehicle) {
        scene.addActor(vehicle.vehicle.getRigidDynamicActor())
    }

    override fun removeVehicleImpl(vehicle: Vehicle) {
        // nothing to do here
    }
}