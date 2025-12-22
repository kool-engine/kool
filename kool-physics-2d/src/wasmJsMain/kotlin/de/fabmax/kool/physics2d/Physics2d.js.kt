@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package de.fabmax.kool.physics2d

import de.fabmax.kool.util.logE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual fun PhysicsSystem(): PhysicsSystem = PhysicsSystemJs

internal object PhysicsSystemJs : PhysicsSystem {
    override var isLoaded = false; private set
    override suspend fun loadPhysics2d() {
        logE("Box2D") { "Box2D NOT loaded" }
    }

    override val physicsDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    internal fun isPhysicsThread(): Boolean = true
}