package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import org.lwjgl.system.MemoryStack
import physx.common.PxVec3
import physx.extensions.PxRigidBodyExt
import physx.physics.PxRigidDynamic

actual open class RigidDynamic actual constructor(mass: Float, pose: Mat4f) : RigidActor() {

    protected val pxRigidDynamic: PxRigidDynamic
    private val pxTmpVec = PxVec3()

    private val bufInertia = MutableVec3f()
    private val bufLinVelocity = MutableVec3f()
    private val bufAngVelocity = MutableVec3f()

    actual var mass: Float
        get() = pxRigidDynamic.mass
        set(value) { pxRigidDynamic.mass = value }

    private var isInertiaSet = false
    actual var inertia: Vec3f
        get() = pxRigidDynamic.massSpaceInertiaTensor.toVec3f(bufInertia)
        set(value) {
            pxRigidDynamic.massSpaceInertiaTensor = value.toPxVec3(pxTmpVec)
            isInertiaSet = true
        }

    actual var linearVelocity: Vec3f
        get() = pxRigidDynamic.linearVelocity.toVec3f(bufLinVelocity)
        set(value) { pxRigidDynamic.linearVelocity = value.toPxVec3(pxTmpVec) }

    actual var angularVelocity: Vec3f
        get() = pxRigidDynamic.angularVelocity.toVec3f(bufAngVelocity)
        set(value) { pxRigidDynamic.angularVelocity = value.toPxVec3(pxTmpVec) }

    actual var maxLinearVelocity: Float
        get() = pxRigidDynamic.maxLinearVelocity
        set(value) { pxRigidDynamic.maxLinearVelocity = value }

    actual var maxAngularVelocity: Float
        get() = pxRigidDynamic.maxAngularVelocity
        set(value) { pxRigidDynamic.maxAngularVelocity = value }

    actual var linearDamping: Float
        get() = pxRigidDynamic.linearDamping
        set(value) { pxRigidDynamic.linearDamping = value }

    actual var angularDamping: Float
        get() = pxRigidDynamic.angularDamping
        set(value) { pxRigidDynamic.angularDamping = value }

    init {
        MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            pxRigidDynamic = Physics.physics.createRigidDynamic(pxPose)
            pxRigidActor = pxRigidDynamic
            this.mass = mass
        }
    }

    actual fun updateInertiaFromShapesAndMass() {
        PxRigidBodyExt.setMassAndUpdateInertia(pxRigidDynamic, mass)
    }

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
}