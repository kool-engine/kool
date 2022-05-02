package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import physx.*

actual abstract class RigidBody : RigidActor() {

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    internal val pxRigidBody: PxRigidBody
        get() = pxRigidActor as PxRigidBody

    actual var mass: Float
        get() = pxRigidBody.mass
        set(value) { pxRigidBody.mass = value }

    private var isInertiaSet = false
    actual var inertia: Vec3f
        get() = pxRigidBody.massSpaceInertiaTensor.toVec3f(bufInertia)
        set(value) {
            pxRigidBody.massSpaceInertiaTensor = value.toPxVec3(pxTmpVec)
            isInertiaSet = true
        }

    actual var linearVelocity: Vec3f
        get() = pxRigidBody.linearVelocity.toVec3f(bufLinVelocity)
        set(value) { pxRigidBody.linearVelocity = value.toPxVec3(pxTmpVec) }

    actual var angularVelocity: Vec3f
        get() = pxRigidBody.angularVelocity.toVec3f(bufAngVelocity)
        set(value) { pxRigidBody.angularVelocity = value.toPxVec3(pxTmpVec) }

    actual var maxLinearVelocity: Float
        get() = pxRigidBody.maxLinearVelocity
        set(value) { pxRigidBody.maxLinearVelocity = value }

    actual var maxAngularVelocity: Float
        get() = pxRigidBody.maxAngularVelocity
        set(value) { pxRigidBody.maxAngularVelocity = value }

    actual var linearDamping: Float
        get() = pxRigidBody.linearDamping
        set(value) { pxRigidBody.linearDamping = value }

    actual var angularDamping: Float
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

    actual fun updateInertiaFromShapesAndMass() {
        Physics.PxRigidBodyExt.setMassAndUpdateInertia(pxRigidBody, mass)
    }

    actual fun addForceAtPos(force: Vec3f, pos: Vec3f, isLocalForce: Boolean, isLocalPos: Boolean) {
        MemoryStack.stackPush().use { mem ->
            val pxForce = force.toPxVec3(mem.createPxVec3())
            val pxPos = pos.toPxVec3(mem.createPxVec3())
            when {
                isLocalForce && isLocalPos -> Physics.PxRigidBodyExt.addLocalForceAtLocalPos(pxRigidBody, pxForce, pxPos)
                isLocalForce && !isLocalPos -> Physics.PxRigidBodyExt.addLocalForceAtPos(pxRigidBody, pxForce, pxPos)
                !isLocalForce && isLocalPos -> Physics.PxRigidBodyExt.addForceAtLocalPos(pxRigidBody, pxForce, pxPos)
                else -> Physics.PxRigidBodyExt.addForceAtPos(pxRigidBody, pxForce, pxPos)
            }
        }
    }

    actual fun addImpulseAtPos(impulse: Vec3f, pos: Vec3f, isLocalImpulse: Boolean, isLocalPos: Boolean) {
        MemoryStack.stackPush().use { mem ->
            val pxImpulse = impulse.toPxVec3(mem.createPxVec3())
            val pxPos = pos.toPxVec3(mem.createPxVec3())
            when {
                isLocalImpulse && isLocalPos -> Physics.PxRigidBodyExt.addLocalForceAtLocalPos(pxRigidBody, pxImpulse, pxPos, PxForceModeEnum.eIMPULSE)
                isLocalImpulse && !isLocalPos -> Physics.PxRigidBodyExt.addLocalForceAtPos(pxRigidBody, pxImpulse, pxPos, PxForceModeEnum.eIMPULSE)
                !isLocalImpulse && isLocalPos -> Physics.PxRigidBodyExt.addForceAtLocalPos(pxRigidBody, pxImpulse, pxPos, PxForceModeEnum.eIMPULSE)
                else -> Physics.PxRigidBodyExt.addForceAtPos(pxRigidBody, pxImpulse, pxPos, PxForceModeEnum.eIMPULSE)
            }
        }
    }

    actual fun addTorque(torque: Vec3f, isLocalTorque: Boolean) {
        tmpVec.set(torque)
        if (isLocalTorque) {
            transform.transform(tmpVec, 0f)
        }
        pxRigidBody.addTorque(tmpVec.toPxVec3(pxTmpVec))
    }
}