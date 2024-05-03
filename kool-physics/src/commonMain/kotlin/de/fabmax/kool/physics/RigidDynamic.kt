package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f

expect fun RigidDynamic(mass: Float = 1f, pose: Mat4f = Mat4f.IDENTITY, isKinematic: Boolean = false): RigidDynamic

interface RigidDynamic : RigidBody {
    val isKinematic: Boolean

    fun wakeUp()
    fun putToSleep()

    fun setKinematicTarget(pose: Mat4f)
    fun setKinematicTarget(position: Vec3f? = null, rotation: QuatF? = null)

    fun setLinearLockFlags(lockLinearX: Boolean, lockLinearY: Boolean, lockLinearZ: Boolean)
    fun setAngularLockFlags(lockAngularX: Boolean, lockAngularY: Boolean, lockAngularZ: Boolean)
}
