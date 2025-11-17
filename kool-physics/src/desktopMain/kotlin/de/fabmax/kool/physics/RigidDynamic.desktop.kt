package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.logE
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

    override val holder: RigidActorHolder
    private val pxRigidDynamic: PxRigidDynamic
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
        syncSimulationData()
    }

    override fun wakeUp() {
        pxRigidDynamic.wakeUp()
    }

    override fun putToSleep() {
        pxRigidDynamic.putToSleep()
    }

    override fun setKinematicTarget(pose: Mat4f) {
        if (!PhysicsImpl.isPhysicsThread()) {
            logE { "setKinematicTarget must be called from PhysicsThread / PhysicsStepListener.onUpdatePhysics" }
            return
        }
        if (!isAttachedToSimulation) {
            logE { "Body needs to be attached to simulation before setKinematicTarget can be called" }
            return
        }
        MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            pxRigidDynamic.setKinematicTarget(pxPose)
        }
    }

    override fun setKinematicTarget(position: Vec3f?, rotation: QuatF?) {
        if (!PhysicsImpl.isPhysicsThread()) {
            logE { "setKinematicTarget must be called from PhysicsThread / PhysicsStepListener.onUpdatePhysics" }
            return
        }
        if (!isAttachedToSimulation) {
            logE { "Body needs to be attached to simulation before setKinematicTarget can be called" }
            return
        }
        MemoryStack.stackPush().use { mem ->
            val pxPose = mem.createPxTransform()
            pxPose.p = position?.toPxVec3(mem.createPxVec3()) ?: pxRigidDynamic.globalPose.p
            pxPose.q = rotation?.toPxQuat(mem.createPxQuat()) ?: pxRigidDynamic.globalPose.q
            pxRigidDynamic.setKinematicTarget(pxPose)
        }
    }

    override fun setLinearLockFlags(lockLinearX: Boolean, lockLinearY: Boolean, lockLinearZ: Boolean) {
        if (!PhysicsImpl.isPhysicsThread()) {
            logE { "setLinearLockFlags must be called from PhysicsThread / PhysicsStepListener.onUpdatePhysics" }
            return
        }
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_X, lockLinearX)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Y, lockLinearY)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Z, lockLinearZ)
    }

    override fun setAngularLockFlags(lockAngularX: Boolean, lockAngularY: Boolean, lockAngularZ: Boolean) {
        if (!PhysicsImpl.isPhysicsThread()) {
            logE { "setLinearLockFlags must be called from PhysicsThread / PhysicsStepListener.onUpdatePhysics" }
            return
        }
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_X, lockAngularX)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Y, lockAngularY)
        pxRigidDynamic.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Z, lockAngularZ)
    }
}