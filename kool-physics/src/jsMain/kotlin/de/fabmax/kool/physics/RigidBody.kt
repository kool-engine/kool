package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import physx.*

abstract class RigidBodyImpl : RigidActorImpl(), RigidBody {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    private val pxRigidBody: PxRigidBody
        get() = holder.px as PxRigidBody

    override var mass: Float
        get() = pxRigidBody.mass
        set(value) { pxRigidBody.mass = value }

    private var isInertiaSet = false
    override var inertia: Vec3f
        get() = pxRigidBody.massSpaceInertiaTensor.toVec3f(bufInertia)
        set(value) {
            pxRigidBody.massSpaceInertiaTensor = value.toPxVec3(pxTmpVec)
            isInertiaSet = true
        }

    override var linearVelocity: Vec3f
        get() = pxRigidBody.linearVelocity.toVec3f(bufLinVelocity)
        set(value) {
            PxRigidDynamicFromPointer(pxRigidBody.ptr).setLinearVelocity(value.toPxVec3(pxTmpVec))
        }

    override var angularVelocity: Vec3f
        get() = pxRigidBody.angularVelocity.toVec3f(bufAngVelocity)
        set(value) {
            PxRigidDynamicFromPointer(pxRigidBody.ptr).setAngularVelocity(value.toPxVec3(pxTmpVec))
        }

    override var maxLinearVelocity: Float
        get() = pxRigidBody.maxLinearVelocity
        set(value) { pxRigidBody.maxLinearVelocity = value }

    override var maxAngularVelocity: Float
        get() = pxRigidBody.maxAngularVelocity
        set(value) { pxRigidBody.maxAngularVelocity = value }

    override var linearDamping: Float
        get() = pxRigidBody.linearDamping
        set(value) { pxRigidBody.linearDamping = value }

    override var angularDamping: Float
        get() = pxRigidBody.angularDamping
        set(value) { pxRigidBody.angularDamping = value }

    private val bufInertia = MutableVec3f()
    private val bufLinVelocity = MutableVec3f()
    private val bufAngVelocity = MutableVec3f()
    private val tmpVec = MutableVec3f()
    private val pxTmpVec = PxVec3()

    override fun attachShape(shape: Shape) {
        super.attachShape(shape)
        if (!isInertiaSet) {
            inertia = shape.geometry.estimateInertiaForMass(mass)
        }
    }

    override fun release() {
        super.release()
        pxTmpVec.destroy()
    }

    override fun updateInertiaFromShapesAndMass() {
        PxRigidBodyExt.setMassAndUpdateInertia(pxRigidBody, mass)
    }

    override fun addForceAtPos(force: Vec3f, pos: Vec3f, isLocalForce: Boolean, isLocalPos: Boolean) {
        MemoryStack.stackPush().use { mem ->
            val pxForce = force.toPxVec3(mem.createPxVec3())
            val pxPos = pos.toPxVec3(mem.createPxVec3())
            when {
                isLocalForce && isLocalPos -> PxRigidBodyExt.addLocalForceAtLocalPos(pxRigidBody, pxForce, pxPos)
                isLocalForce && !isLocalPos -> PxRigidBodyExt.addLocalForceAtPos(pxRigidBody, pxForce, pxPos)
                !isLocalForce && isLocalPos -> PxRigidBodyExt.addForceAtLocalPos(pxRigidBody, pxForce, pxPos)
                else -> PxRigidBodyExt.addForceAtPos(pxRigidBody, pxForce, pxPos)
            }
        }
    }

    override fun addImpulseAtPos(impulse: Vec3f, pos: Vec3f, isLocalImpulse: Boolean, isLocalPos: Boolean) {
        MemoryStack.stackPush().use { mem ->
            val pxImpulse = impulse.toPxVec3(mem.createPxVec3())
            val pxPos = pos.toPxVec3(mem.createPxVec3())
            when {
                isLocalImpulse && isLocalPos -> PxRigidBodyExt.addLocalForceAtLocalPos(pxRigidBody, pxImpulse, pxPos, PxForceModeEnum.eIMPULSE)
                isLocalImpulse && !isLocalPos -> PxRigidBodyExt.addLocalForceAtPos(pxRigidBody, pxImpulse, pxPos, PxForceModeEnum.eIMPULSE)
                !isLocalImpulse && isLocalPos -> PxRigidBodyExt.addForceAtLocalPos(pxRigidBody, pxImpulse, pxPos, PxForceModeEnum.eIMPULSE)
                else -> PxRigidBodyExt.addForceAtPos(pxRigidBody, pxImpulse, pxPos, PxForceModeEnum.eIMPULSE)
            }
        }
    }

    override fun addTorque(torque: Vec3f, isLocalTorque: Boolean) {
        tmpVec.set(torque)
        if (isLocalTorque) {
            transform.transform(tmpVec, 0f)
        }
        pxRigidBody.addTorque(tmpVec.toPxVec3(pxTmpVec))
    }
}