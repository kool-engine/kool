package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.Releasable

expect class Articulation() : CommonArticulation {

    fun createLink(parent: ArticulationLink?, pose: Mat4f): ArticulationLink

    fun wakeUp()

    fun putToSleep()
}

abstract class CommonArticulation : Releasable {

    protected val mutLinks = mutableListOf<ArticulationLink>()
    val links: List<ArticulationLink>
        get() = mutLinks

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    internal open fun fixedUpdate(timeStep: Float) {
        for (i in mutLinks.indices) {
            mutLinks[i].fixedUpdate(timeStep)
        }
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }

}
