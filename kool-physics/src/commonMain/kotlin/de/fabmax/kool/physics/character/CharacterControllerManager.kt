package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.InterpolatableSimulation

expect fun CharacterControllerManager(world: PhysicsWorld): CharacterControllerManager

abstract class CharacterControllerManager : BaseReleasable() {
    protected val _controllers = mutableListOf<CharacterController>()
    val controllers: List<CharacterController>
        get() = _controllers

    protected val onUpdateListener = object : InterpolatableSimulation {
        override fun simulateStep(timeStep: Float) {
            for (i in controllers.indices) {
                controllers[i].simulateStep(timeStep)
            }
        }

        override fun captureStepResults(simulationTime: Double) {
            for (i in controllers.indices) {
                controllers[i].captureStepResults(simulationTime)
            }
        }

        override fun interpolateSteps(simulationTimePrev: Double, simulationTimeNext: Double, simulationTimeLerp: Double, weightNext: Float) {
            for (i in controllers.indices) {
                controllers[i].interpolateSteps(simulationTimePrev, simulationTimeNext, simulationTimeLerp, weightNext)
            }
        }
    }

    fun createController(charProperties: CharacterControllerProperties = CharacterControllerProperties()): CharacterController {
        val ctrl = doCreateController(charProperties)
        _controllers += ctrl
        return ctrl
    }

    open fun removeController(charController: CharacterController) {
        _controllers -= charController
    }

    protected abstract fun doCreateController(charProperties: CharacterControllerProperties): CharacterController

    override fun doRelease() {
        val copyControllers = mutableListOf<CharacterController>()
        copyControllers += controllers
        copyControllers.forEach { it.release() }
        _controllers.clear()
    }
}
