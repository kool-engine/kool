package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f

expect open class RigidDynamic(mass: Float = 1f, pose: Mat4f = Mat4f()) : RigidBody {

    fun wakeUp()

    fun putToSleep()

}
