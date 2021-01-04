package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.constraints.Constraint
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

    private val mutConstraints = mutableListOf<Constraint>()
    val constraints: List<Constraint>
        get() = mutConstraints

    fun addRigidBody(rigidBody: RigidBody) {
        mutBodies += rigidBody
        addRigidBodyImpl(rigidBody)
    }

    fun removeRigidBody(rigidBody: RigidBody) {
        mutBodies -= rigidBody
        removeRigidBodyImpl(rigidBody)
    }

    fun addConstraint(constraint: Constraint, disableCollisionBetweenBodies: Boolean = false) {
        mutConstraints += constraint
        addConstraintImpl(constraint, disableCollisionBetweenBodies)
    }

    fun removeConstraint(constraint: Constraint) {
        mutConstraints -= constraint
        removeConstraintImpl(constraint)
    }

    fun clear() {
        mutBodies.forEach {
            removeRigidBodyImpl(it)
        }
        mutConstraints.forEach {
            removeConstraintImpl(it)
        }
        mutBodies.clear()
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

    protected abstract fun addConstraintImpl(constraint: Constraint, disableCollisionBetweenBodies: Boolean)
    protected abstract fun removeConstraintImpl(constraint: Constraint)

    protected abstract fun singleStepPhysicsImpl(timeStep: Float)
}