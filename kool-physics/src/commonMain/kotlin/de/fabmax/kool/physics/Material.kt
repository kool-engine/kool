package de.fabmax.kool.physics

import de.fabmax.kool.util.Releasable

expect fun Material(staticFriction: Float, dynamicFriction: Float = staticFriction, restitution: Float = 0.2f): Material

interface Material : Releasable {
    var staticFriction: Float
    var dynamicFriction: Float
    var restitution: Float

    override fun release()
}
