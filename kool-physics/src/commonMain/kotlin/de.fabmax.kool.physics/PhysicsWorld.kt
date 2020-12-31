package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.PerfTimer

expect class PhysicsWorld() : CommonPhysicsWorld {

    var gravity: Vec3f

}

abstract class CommonPhysicsWorld {
    var physicsTime = 0.0

    private val perfTimer = PerfTimer()
    var cpuTime = 0.0

    private val mutBodies = mutableListOf<RigidBody>()
    val bodies: List<RigidBody>
        get() = mutBodies

    fun addRigidBody(rigidBody: RigidBody) {
        mutBodies += rigidBody
        addRigidBodyImpl(rigidBody)
    }

    fun removeRigidBody(rigidBody: RigidBody) {
        mutBodies -= rigidBody
        removeRigidBodyImpl(rigidBody)
    }

    fun clear() {
        mutBodies.forEach {
            removeRigidBodyImpl(it)
        }
        mutBodies.clear()
    }

    fun stepPhysics(timeStep: Float, maxSubSteps: Int = 10, fixedStep: Float = 1f / 60f): Float {
        val stopTime = physicsTime + timeStep
        var steps = 0
        while (physicsTime < stopTime && ++steps < maxSubSteps) {
            perfTimer.reset()
            singleStepPhysics(fixedStep)
            physicsTime += fixedStep
            val ms = perfTimer.takeMs()
            cpuTime = cpuTime * 0.8 + ms * 0.2
        }
        return steps * fixedStep
    }

    fun singleStepPhysics(timeStep: Float) {
        singleStepPhysicsImpl(timeStep)
        for (i in mutBodies.indices) {
            mutBodies[i].fixedUpdate(timeStep)
        }
    }

    protected abstract fun addRigidBodyImpl(rigidBody: RigidBody)
    protected abstract fun removeRigidBodyImpl(rigidBody: RigidBody)

    protected abstract fun singleStepPhysicsImpl(timeStep: Float)
}