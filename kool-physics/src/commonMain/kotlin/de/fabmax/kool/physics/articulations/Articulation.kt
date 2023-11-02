package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.Releasable

expect class Articulation(isFixedBase: Boolean) : CommonArticulation {

    var minPositionIterations: Int
    var minVelocityIterations: Int

    fun createLink(parent: ArticulationLink?, pose: Mat4f): ArticulationLink

    fun wakeUp()

    fun putToSleep()

    override fun release()
}

abstract class CommonArticulation(val isFixedBase: Boolean) : Releasable {

    protected val mutLinks = mutableListOf<ArticulationLink>()
    val links: List<ArticulationLink>
        get() = mutLinks

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    internal open fun onPhysicsUpdate(timeStep: Float) {
        for (i in mutLinks.indices) {
            mutLinks[i].onPhysicsUpdate(timeStep)
        }
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }

}
