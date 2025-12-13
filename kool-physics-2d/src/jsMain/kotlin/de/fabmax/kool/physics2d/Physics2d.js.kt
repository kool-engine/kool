@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package de.fabmax.kool.physics2d

import box2d.Box2dWasmLoader
import box2d.prototypes.B2_Base
import de.fabmax.kool.util.logI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual fun PhysicsSystem(): PhysicsSystem = PhysicsSystemJs

internal object PhysicsSystemJs : PhysicsSystem {
    override var isLoaded = false; private set
    override suspend fun loadPhysics2d() {
        Box2dWasmLoader.loadModule()
        isLoaded = true
    }

    override val physicsDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    init {
        val version = B2_Base.getVersion()
        logI("Box2D") { "Box2D loaded: ${version.major}.${version.minor}.${version.revision}" }
    }

    internal fun isPhysicsThread(): Boolean = true
}