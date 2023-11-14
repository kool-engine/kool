package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.util.Releasable

expect fun Articulation(isFixedBase: Boolean): Articulation

interface Articulation : Releasable {
    val links: List<ArticulationLink>

    val onFixedUpdate: MutableList<(Float) -> Unit>

    var minPositionIterations: Int
    var minVelocityIterations: Int

    fun createLink(parent: ArticulationLink?, pose: Mat4f): ArticulationLink

    fun wakeUp()

    fun putToSleep()

    fun onPhysicsUpdate(timeStep: Float) {
        for (i in links.indices) {
            links[i].onPhysicsUpdate(timeStep)
        }
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }
}
