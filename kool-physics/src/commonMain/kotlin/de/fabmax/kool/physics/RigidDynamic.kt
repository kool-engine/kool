package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f

expect class RigidDynamic(mass: Float = 1f, pose: Mat4f = Mat4f(), isKinematic: Boolean = false) : RigidBody {

    fun wakeUp()

    fun putToSleep()

    fun setKinematicTarget(pose: Mat4f)

    fun setKinematicTarget(position: Vec3f? = null, rotation: Vec4f? = null)

}
