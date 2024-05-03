package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f
import org.lwjgl.system.MemoryStack
import physx.physics.PxRigidBodyFlagEnum
import physx.physics.PxRigidDynamic
import physx.physics.PxRigidDynamicLockFlagEnum

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

    override val holder: PxRigidDynamic

    init {
        if (pxActor == null) {
            MemoryStack.stackPush().use { mem ->
                val pxPose = pose.toPxTransform(mem.createPxTransform())
                holder = PhysicsImpl.physics.createRigidDynamic(pxPose)
                this.mass = mass
            }
        } else {
            holder = pxActor
        }
        if (isKinematic) {
            holder.setRigidBodyFlag(PxRigidBodyFlagEnum.eKINEMATIC, true)
        }
        transform.setMatrix(pose)
    }

    override fun wakeUp() {
        holder.wakeUp()
    }

    override fun putToSleep() {
        holder.putToSleep()
    }

    override fun setKinematicTarget(pose: Mat4f) {
        MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            holder.setKinematicTarget(pxPose)
        }
    }

    override fun setKinematicTarget(position: Vec3f?, rotation: QuatF?) {
        MemoryStack.stackPush().use { mem ->
            val pxPose = mem.createPxTransform()
            pxPose.p = position?.toPxVec3(mem.createPxVec3()) ?: holder.globalPose.p
            pxPose.q = rotation?.toPxQuat(mem.createPxQuat()) ?: holder.globalPose.q
            holder.setKinematicTarget(pxPose)
        }
    }

    override fun setLinearLockFlags(lockLinearX: Boolean, lockLinearY: Boolean, lockLinearZ: Boolean) {
        holder.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_X, lockLinearX)
        holder.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Y, lockLinearY)
        holder.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Z, lockLinearZ)
    }

    override fun setAngularLockFlags(lockAngularX: Boolean, lockAngularY: Boolean, lockAngularZ: Boolean) {
        holder.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_X, lockAngularX)
        holder.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Y, lockAngularY)
        holder.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Z, lockAngularZ)
    }
}