package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.logE
import org.lwjgl.system.MemoryStack
import physx.common.PxVec3
import physx.extensions.PxRigidBodyExt
import physx.physics.PxForceModeEnum
import physx.physics.PxRigidBody
import physx.physics.PxRigidDynamic
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.KProperty

@OptIn(ExperimentalAtomicApi::class)
abstract class RigidBodyImpl : RigidActorImpl(), RigidBody {
    private val pxRigidBody: PxRigidBody
        get() = holder as PxRigidBody

    private val bufInertia = SyncedVec3(Vec3f.ONES)
    private val bufMass = SyncedFloat(1f)
    private val bufLinVelocity = SyncedVec3(Vec3f.ZERO)
    private val bufAngVelocity = SyncedVec3(Vec3f.ZERO)
    private val bufMaxLinVelocity = SyncedFloat(100f)
    private val bufMaxAngVelocity = SyncedFloat(50f)
    private val bufLinDamping = SyncedFloat(0.05f)
    private val bufAngDamping = SyncedFloat(0.05f)

    private var isInertiaSet = false
    override var inertia: Vec3f
        get() = bufInertia.readBuffer
        set(value) {
            bufInertia.set(value)
            isInertiaSet = true
        }

    override var mass: Float by bufMass
    override var linearVelocity: Vec3f by bufLinVelocity
    override var angularVelocity: Vec3f by bufAngVelocity
    override var maxLinearVelocity: Float by bufMaxLinVelocity
    override var maxAngularVelocity: Float by bufMaxAngVelocity
    override var linearDamping: Float by bufLinDamping
    override var angularDamping: Float by bufAngDamping

    private var anyDirty = AtomicBoolean(false)
    private val tmpVec = MutableVec3f()
    private val pxTmpVec = PxVec3()

    override fun syncSimulationData() {
        super.syncSimulationData()
        if (anyDirty.exchange(false)) {
            bufInertia.writeIfDirty { pxRigidBody.massSpaceInertiaTensor = it.toPxVec3(pxTmpVec) }
            bufMass.writeIfDirty { pxRigidBody.mass = it }
            bufLinVelocity.writeIfDirty { (pxRigidBody as? PxRigidDynamic)?.linearVelocity = it.toPxVec3(pxTmpVec) }
            bufAngVelocity.writeIfDirty { (pxRigidBody as? PxRigidDynamic)?.angularVelocity = it.toPxVec3(pxTmpVec) }
            bufMaxLinVelocity.writeIfDirty { pxRigidBody.maxLinearVelocity = it }
            bufMaxAngVelocity.writeIfDirty { pxRigidBody.maxAngularVelocity = it }
            bufLinDamping.writeIfDirty { pxRigidBody.linearDamping = it }
            bufAngDamping.writeIfDirty { pxRigidBody.angularDamping = it }
        }
        bufInertia.read(pxRigidBody.massSpaceInertiaTensor)
        bufMass.read(pxRigidBody.mass)
        bufLinVelocity.read(pxRigidBody.linearVelocity)
        bufAngVelocity.read(pxRigidBody.angularVelocity)
        bufMaxLinVelocity.read(pxRigidBody.maxLinearVelocity)
        bufMaxAngVelocity.read(pxRigidBody.maxAngularVelocity)
        bufLinDamping.read(pxRigidBody.linearDamping)
        bufAngDamping.read(pxRigidBody.angularDamping)
    }

    override fun attachShape(shape: Shape) {
        super.attachShape(shape)
        if (!isInertiaSet) {
            inertia = shape.geometry.estimateInertiaForMass(mass)
        }
    }

    override fun doRelease() {
        super.doRelease()
        pxTmpVec.destroy()
    }

    override fun updateInertiaFromShapesAndMass() {
        PxRigidBodyExt.setMassAndUpdateInertia(pxRigidBody, mass)
        inertia = pxRigidBody.massSpaceInertiaTensor.toVec3f()
    }

    override fun addForceAtPos(force: Vec3f, pos: Vec3f, isLocalForce: Boolean, isLocalPos: Boolean) {
        if (!PhysicsImpl.isPhysicsThread()) {
            logE { "addForceAtPos must be called from PhysicsThread / PhysicsStepListener.onUpdatePhysics" }
            return
        }
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
        if (!PhysicsImpl.isPhysicsThread()) {
            logE { "addImpulseAtPos must be called from PhysicsThread / PhysicsStepListener.onUpdatePhysics" }
            return
        }
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
        if (!PhysicsImpl.isPhysicsThread()) {
            logE { "addTorque must be called from PhysicsThread / PhysicsStepListener.onUpdatePhysics" }
            return
        }
        tmpVec.set(torque)
        if (isLocalTorque) {
            transform.transform(tmpVec, 0f)
        }
        pxRigidBody.addTorque(tmpVec.toPxVec3(pxTmpVec))
    }

    private inner class SyncedFloat(initial: Float) {
        var writeBuffer = initial
        var readBuffer = initial
        val isDirty = AtomicBoolean(false)

        fun set(value: Float) {
            writeBuffer = value
            readBuffer = value
            isDirty.store(true)
            anyDirty.store(true)
        }

        inline fun writeIfDirty(block: (Float) -> Unit) {
            if (isDirty.exchange(false)) {
                block(writeBuffer)
            }
        }

        fun read(px: Float) {
            readBuffer = px
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Float = readBuffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) { set(value) }
    }

    private inner class SyncedVec3(initial: Vec3f) {
        val writeBuffer = MutableVec3f(initial)
        val readBuffer = MutableVec3f(initial)
        val isDirty = AtomicBoolean(false)

        fun set(value: Vec3f) {
            writeBuffer.set(value)
            readBuffer.set(value)
            isDirty.store(true)
            anyDirty.store(true)
        }

        inline fun writeIfDirty(block: (Vec3f) -> Unit) {
            if (isDirty.exchange(false)) {
                block(writeBuffer)
            }
        }

        fun read(px: PxVec3) = px.toVec3f(readBuffer)

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec3f = readBuffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec3f) { set(value) }
    }
}