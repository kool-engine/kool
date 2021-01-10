package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.joints.Joint
import de.fabmax.kool.physics.shapes.PlaneShape
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.util.PerfTimer
import kotlin.math.min

expect class PhysicsWorld() : CommonPhysicsWorld {

    var gravity: Vec3f

}

abstract class CommonPhysicsWorld {
    var physicsTime = 0.0
    var physicsTimeDesired = 0.0

    var smartSubSteps = true
    private var smartSubStepLimit = 5

    private val perfTimer = PerfTimer()
    var cpuTime = 0f
        private set
    var timeFactor = 1f
        private set

    private val mutBodies = mutableListOf<RigidBody>()
    val bodies: List<RigidBody>
        get() = mutBodies

    private val mutJoints = mutableListOf<Joint>()
    val joints: List<Joint>
        get() = mutJoints

    private val mutVehicles = mutableListOf<Vehicle>()
    val vehicles: List<Vehicle>
        get() = mutVehicles

    fun addRigidBody(rigidBody: RigidBody) {
        mutBodies += rigidBody
        addRigidBodyImpl(rigidBody)
    }

    fun removeRigidBody(rigidBody: RigidBody) {
        mutBodies -= rigidBody
        removeRigidBodyImpl(rigidBody)
    }

    fun addJoint(joint: Joint) {
        mutJoints += joint
        addJointImpl(joint)
    }

    fun removeJoint(joint: Joint) {
        mutJoints -= joint
        removeJointImpl(joint)
    }

    fun addVehicle(vehicle: Vehicle) {
        mutVehicles += vehicle
        addVehicleImpl(vehicle)
    }

    fun removeVehicle(vehicle: Vehicle) {
        mutVehicles -= vehicle
        removeVehicleImpl(vehicle)
    }

    fun clear() {
        mutBodies.forEach {
            removeRigidBodyImpl(it)
        }
        mutBodies.clear()
        mutJoints.forEach {
            removeJointImpl(it)
        }
        mutJoints.clear()
        mutVehicles.forEach {
            removeVehicleImpl(it)
        }
        mutVehicles.clear()
    }

    fun stepPhysics(timeStep: Float, maxSubSteps: Int = 5, fixedStep: Float = 1f / 60f): Float {
        var steps = 0
        var stepLimit = if (smartSubSteps) min(smartSubStepLimit, maxSubSteps) else maxSubSteps

        physicsTimeDesired += timeStep
        while (physicsTime < physicsTimeDesired && stepLimit > 0) {
            perfTimer.reset()
            singleStepPhysics(fixedStep)
            steps++
            val ms = perfTimer.takeMs().toFloat()
            cpuTime = cpuTime * 0.8f + ms * 0.2f

            physicsTime += fixedStep
            if (physicsTime < physicsTimeDesired) {
                stepLimit--
            }
        }

        if (stepLimit == 0) {
            physicsTime = physicsTimeDesired
            if (smartSubStepLimit > 1) {
                smartSubStepLimit--
            }
        } else if (smartSubStepLimit < maxSubSteps) {
            smartSubStepLimit++
        }

        val timeInc = fixedStep * steps
        timeFactor = timeFactor * 0.9f + timeInc / timeStep * 0.1f

        return timeInc
    }

    fun singleStepPhysics(timeStep: Float) {
        singleStepPhysicsImpl(timeStep)
        for (i in mutBodies.indices) {
            mutBodies[i].fixedUpdate(timeStep)
        }
    }

    protected abstract fun addRigidBodyImpl(rigidBody: RigidBody)
    protected abstract fun removeRigidBodyImpl(rigidBody: RigidBody)

    protected abstract fun addJointImpl(joint: Joint)
    protected abstract fun removeJointImpl(joint: Joint)

    protected abstract fun addVehicleImpl(vehicle: Vehicle)
    protected abstract fun removeVehicleImpl(vehicle: Vehicle)

    protected abstract fun singleStepPhysicsImpl(timeStep: Float)

    /**
     * Adds a static plane with y-axis as surface normal (i.e. xz-plane) at y = 0.
     */
    fun addDefaultGroundPlane(): RigidBody {
        val groundPlane = RigidBody(PlaneShape(), 0f)
        groundPlane.setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
        addRigidBody(groundPlane)
        return groundPlane
    }
}