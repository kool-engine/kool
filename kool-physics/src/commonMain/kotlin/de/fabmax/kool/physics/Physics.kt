package de.fabmax.kool.physics

import kotlinx.coroutines.CoroutineScope

expect object Physics : CoroutineScope {

    val isLoaded: Boolean

    fun loadPhysics()

    suspend fun awaitLoaded()

}