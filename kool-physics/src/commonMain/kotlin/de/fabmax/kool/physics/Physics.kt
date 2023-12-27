package de.fabmax.kool.physics

object Physics {

    private val system = PhysicsSystem()

    val isLoaded: Boolean get() = system.isLoaded

    val defaultMaterial: Material get() = system.defaultMaterial

    suspend fun loadAndAwaitPhysics() = system.loadAndAwaitPhysics()
    fun checkIsLoaded() = system.checkIsLoaded()

    val NOTIFY_TOUCH_FOUND: Int get() = system.NOTIFY_TOUCH_FOUND
    val NOTIFY_TOUCH_LOST: Int get() = system.NOTIFY_TOUCH_LOST
    val NOTIFY_CONTACT_POINTS: Int get() = system.NOTIFY_CONTACT_POINTS
}

internal expect fun PhysicsSystem(): PhysicsSystem

internal interface PhysicsSystem {
    val isLoaded: Boolean

    val defaultMaterial: Material

    suspend fun loadAndAwaitPhysics()

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Physics subsystem is not loaded. Call loadAndAwaitPhysics() before using any physics functions.")
        }
    }

    val NOTIFY_TOUCH_FOUND: Int
    val NOTIFY_TOUCH_LOST: Int
    val NOTIFY_CONTACT_POINTS: Int
}
