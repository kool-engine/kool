package de.fabmax.kool.physics2d

import kotlinx.coroutines.CoroutineDispatcher

object Physics2d {
    internal val system = PhysicsSystem()

    suspend fun loadPhysics2d() = system.loadPhysics2d()
    fun checkIsLoaded() = system.checkIsLoaded()
}

internal expect fun PhysicsSystem(): PhysicsSystem

internal interface PhysicsSystem {
    val isLoaded: Boolean

    suspend fun loadPhysics2d()

    fun checkIsLoaded() = check(isLoaded) {
        "Physics2d subsystem is not loaded. Call Physics2d.loadPhysics2d() before using any physics functions."
    }

    val physicsDispatcher: CoroutineDispatcher
}