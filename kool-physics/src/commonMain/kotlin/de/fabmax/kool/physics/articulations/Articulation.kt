package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.util.BaseReleasable

expect fun Articulation(isFixedBase: Boolean): Articulation

abstract class Articulation : BaseReleasable() {
    protected val _links = mutableListOf<ArticulationLink>()
    val links: List<ArticulationLink> get() = _links

    abstract var minPositionIterations: Int
    abstract var minVelocityIterations: Int

    abstract fun createLink(parent: ArticulationLink?, pose: PoseF): ArticulationLink

    abstract fun wakeUp()

    abstract fun putToSleep()
}
