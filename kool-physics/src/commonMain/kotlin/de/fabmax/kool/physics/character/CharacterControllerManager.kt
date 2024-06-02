package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.util.BaseReleasable

expect fun CharacterControllerManager(world: PhysicsWorld): CharacterControllerManager

abstract class CharacterControllerManager : BaseReleasable() {
    protected val _controllers = mutableListOf<CharacterController>()
    val controllers: List<CharacterController>
        get() = _controllers

    protected val onAdvanceListener: (Float) -> Unit = { timeStep ->
        for (i in controllers.indices) {
            controllers[i].onAdvancePhysics(timeStep)
        }
    }

    protected val onUpdateListener: (Float) -> Unit = { timeStep ->
        for (i in controllers.indices) {
            controllers[i].onPhysicsUpdate(timeStep)
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

    override fun release() {
        val copyControllers = mutableListOf<CharacterController>()
        copyControllers += controllers
        copyControllers.forEach { it.release() }
        _controllers.clear()
        super.release()
    }
}
