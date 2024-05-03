package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f
import physx.PxRigidBodyFlagEnum
import physx.PxRigidDynamic
import physx.PxRigidDynamicLockFlagEnum
import physx.globalPose

actual fun RigidDynamic(mass: Float, pose: Mat4f, isKinematic: Boolean): RigidDynamic {
    return RigidDynamicImpl(mass, pose, isKinematic)
}

class RigidDynamicImpl(
    mass: Float,
    pose: Mat4f,
    override val isKinematic: Boolean,
    pxActor: PxRigidDynamic?
) : RigidBodyImpl(), RigidDynamic {

    constructor(mass: Float, pose: Mat4f, isKinematic: Boolean) : this(mass, pose, isKinematic, null)

    override val holder: RigidActorHolder

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    protected val pxRigidDynamic: PxRigidDynamic
        get() = holder.px as PxRigidDynamic

    init {
        if (pxActor == null) {
            MemoryStack.stackPush().use { mem ->
                val pxPose = pose.toPxTransform(mem.createPxTransform())
                holder = RigidActorHolder(PhysicsImpl.physics.createRigidDynamic(pxPose))
                this.mass = mass
            }
        } else {
            holder = RigidActorHolder(pxActor)
        }
        if (isKinematic) {
            pxRigidDynamic.setRigidBodyFlag(PxRigidBodyFlagEnum.eKINEMATIC, true)
        }

        transform.setMatrix(pose)
    }

    override fun wakeUp() {
        pxRigidDynamic.wakeUp()
    }

    override fun putToSleep() {
        pxRigidDynamic.putToSleep()
    }

    override fun setKinematicTarget(pose: Mat4f) {
        MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            pxRigidDynamic.setKinematicTarget(pxPose)
        }
    }

    override fun setKinematicTarget(position: Vec3f?, rotation: QuatF?) {
        MemoryStack.stackPush().use { mem ->
            val pxPose = mem.createPxTransform()
            pxPose.p = position?.toPxVec3(mem.createPxVec3()) ?: pxRigidDynamic.globalPose.p
            pxPose.q = rotation?.toPxQuat(mem.createPxQuat()) ?: pxRigidDynamic.globalPose.q
            pxRigidDynamic.setKinematicTarget(pxPose)
        }
    }

    override fun setLinearLockFlags(lockLinearX: Boolean, lockLinearY: Boolean, lockLinearZ: Boolean) {
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_X, lockLinearX)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Y, lockLinearY)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Z, lockLinearZ)
    }

    override fun setAngularLockFlags(lockAngularX: Boolean, lockAngularY: Boolean, lockAngularZ: Boolean) {
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_X, lockAngularX)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Y, lockAngularY)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Z, lockAngularZ)
    }
}