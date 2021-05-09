package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.Releasable

abstract class CommonCharacterControllerManager(protected val world: PhysicsWorld) : Releasable {

    protected val mutControllers = mutableListOf<CharacterController>()
    val controllers: List<CharacterController>
        get() = mutControllers

    private val onAdvanceListener: (Float) -> Unit = { timeStep ->
        for (i in controllers.indices) {
            controllers[i].onAdvancePhysics(timeStep)
        }
    }

    private val onUpdateListener: (Float) -> Unit = { timeStep ->
        for (i in controllers.indices) {
            controllers[i].onPhysicsUpdate(timeStep)
        }
    }

    init {
        world.onAdvancePhysics += onAdvanceListener
        world.onPhysicsUpdate += onUpdateListener
    }

    fun createController(props: CharacterProperties): CharacterController {
        val ctrl = doCreateController(props)
        mutControllers += ctrl
        return ctrl
    }

    open fun removeController(charController: CharacterController) {
        mutControllers -= charController
    }

    protected abstract fun doCreateController(props: CharacterProperties): CharacterController

    override fun release() {
        val copyControllers = mutableListOf<CharacterController>()
        copyControllers += controllers
        copyControllers.forEach { it.release() }
        mutControllers.clear()

        world.onAdvancePhysics -= onAdvanceListener
        world.onPhysicsUpdate -= onUpdateListener
    }
}

expect class CharacterControllerManager(world: PhysicsWorld) : CommonCharacterControllerManager
