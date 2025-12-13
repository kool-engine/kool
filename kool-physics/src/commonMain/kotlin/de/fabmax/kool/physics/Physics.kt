package de.fabmax.kool.physics

import kotlinx.coroutines.CoroutineDispatcher

object Physics {

    private val system = PhysicsSystem()

    val isLoaded: Boolean get() = system.isLoaded
    internal val physicsDispatcher: CoroutineDispatcher get() = system.physicsDispatcher

    val defaultMaterial: Material get() = system.defaultMaterial

    suspend fun loadPhysics() = system.loadPhysics()
    fun checkIsLoaded() = system.checkIsLoaded()

    val NOTIFY_TOUCH_FOUND: Int get() = system.NOTIFY_TOUCH_FOUND
    val NOTIFY_TOUCH_LOST: Int get() = system.NOTIFY_TOUCH_LOST
    val NOTIFY_CONTACT_POINTS: Int get() = system.NOTIFY_CONTACT_POINTS
}

internal expect fun PhysicsSystem(): PhysicsSystem

internal interface PhysicsSystem {
    val isLoaded: Boolean

    val defaultMaterial: Material

    suspend fun loadPhysics()

    fun checkIsLoaded() = check(isLoaded) {
        "Physics subsystem is not loaded. Call Physics.loadPhysics() before using any physics functions."
    }

    val physicsDispatcher: CoroutineDispatcher

    val NOTIFY_TOUCH_FOUND: Int
    val NOTIFY_TOUCH_LOST: Int
    val NOTIFY_CONTACT_POINTS: Int
}
