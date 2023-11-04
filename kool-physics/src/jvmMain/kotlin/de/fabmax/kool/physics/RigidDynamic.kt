package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f
import org.lwjgl.system.MemoryStack
import physx.physics.PxRigidActor
import physx.physics.PxRigidBodyFlagEnum
import physx.physics.PxRigidDynamic
import physx.physics.PxRigidDynamicLockFlagEnum

actual class RigidDynamic internal constructor(
    mass: Float,
    pose: Mat4f,
    isKinematic: Boolean,
    pxActor: PxRigidDynamic?
) : RigidBody() {

    actual constructor(mass: Float, pose: Mat4f, isKinematic: Boolean) : this(mass, pose, isKinematic, null)

    private val pxRigidDynamic: PxRigidDynamic
        get() = pxRigidActor as PxRigidDynamic

    override val pxRigidActor: PxRigidActor

    init {
        if (pxActor == null) {
            MemoryStack.stackPush().use { mem ->
                val pxPose = pose.toPxTransform(mem.createPxTransform())
                pxRigidActor = Physics.physics.createRigidDynamic(pxPose)
                this.mass = mass
            }
        } else {
            pxRigidActor = pxActor
        }
        if (isKinematic) {
            pxRigidBody.setRigidBodyFlag(PxRigidBodyFlagEnum.eKINEMATIC, true)
        }
        transform.setMatrix(pose)
    }

    actual fun wakeUp() {
        pxRigidDynamic.wakeUp()
    }

    actual fun putToSleep() {
        pxRigidDynamic.putToSleep()
    }

    actual fun setKinematicTarget(pose: Mat4f) {
        MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            pxRigidDynamic.setKinematicTarget(pxPose)
        }
    }

    actual fun setKinematicTarget(position: Vec3f?, rotation: QuatF?) {
        MemoryStack.stackPush().use { mem ->
            val pxPose = mem.createPxTransform()
            pxPose.p = position?.toPxVec3(mem.createPxVec3()) ?: pxRigidActor.globalPose.p
            pxPose.q = rotation?.toPxQuat(mem.createPxQuat()) ?: pxRigidActor.globalPose.q
            pxRigidDynamic.setKinematicTarget(pxPose)
        }
    }

    actual fun setLinearLockFlags(lockLinearX: Boolean, lockLinearY: Boolean, lockLinearZ: Boolean) {
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_X, lockLinearX)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Y, lockLinearY)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Z, lockLinearZ)
    }

    actual fun setAngularLockFlags(lockAngularX: Boolean, lockAngularY: Boolean, lockAngularZ: Boolean) {
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_X, lockAngularX)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Y, lockAngularY)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Z, lockAngularZ)
    }
}