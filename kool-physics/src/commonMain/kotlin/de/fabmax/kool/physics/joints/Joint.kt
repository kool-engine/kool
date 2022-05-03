package de.fabmax.kool.physics.joints

import de.fabmax.kool.physics.Releasable

expect abstract class Joint : Releasable {

    val isBroken: Boolean
    var debugVisualize: Boolean

    fun setBreakForce(force: Float, torque: Float)


}