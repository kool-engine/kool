package de.fabmax.kool.physics.joints

import de.fabmax.kool.physics.Releasable

expect interface Joint : Releasable {

    val isBroken: Boolean

    fun setBreakForce(force: Float, torque: Float)

}