package de.fabmax.kool.physics2d

import box2d.B2_Base
import de.fabmax.kool.util.logI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

internal actual fun PhysicsSystem(): PhysicsSystem = PhysicsSystemDesktop

internal object PhysicsSystemDesktop : PhysicsSystem {
    override val isLoaded = true
    override suspend fun loadPhysics2d() {
        // on JVM, there's nothing to do here
    }

    private var physicsThread: Thread? = null
    override val physicsDispatcher: CoroutineDispatcher = Executors
        .newSingleThreadExecutor { target ->
            Thread(target, "physics-thread").also {
                it.isDaemon = true
                physicsThread = it
            }
        }
        .asCoroutineDispatcher()

    init {
        val version = B2_Base.getVersion()
        logI("Box2D") { "Box2D loaded: ${version.major}.${version.minor}.${version.revision}" }
    }

    internal fun isPhysicsThread(): Boolean {
        return Thread.currentThread() === physicsThread
    }
}