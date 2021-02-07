package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import physx.PhysXJsLoader
import physx.PxRigidDynamic
import physx.PxVec3

actual open class RigidDynamic actual constructor(mass: Float, pose: Mat4f) : RigidActor() {

    init {
        Physics.checkIsLoaded()
    }

    protected val pxRigidDynamic: PxRigidDynamic
    private val pxTmpVec = PxVec3()

    private val bufInertia = MutableVec3f()
    private val bufLinVelocity = MutableVec3f()
    private val bufAngVelocity = MutableVec3f()

    actual var mass: Float
        get() = pxRigidDynamic.getMass()
        set(value) { pxRigidDynamic.setMass(value) }

    private var isInertiaSet = false
    actual var inertia: Vec3f
        get() = pxRigidDynamic.getMassSpaceInertiaTensor().toVec3f(bufInertia)
        set(value) {
            pxRigidDynamic.setMassSpaceInertiaTensor(value.toPxVec3(pxTmpVec))
            isInertiaSet = true
        }

    actual var linearVelocity: Vec3f
        get() = pxRigidDynamic.getLinearVelocity().toVec3f(bufLinVelocity)
        set(value) { pxRigidDynamic.setLinearVelocity(value.toPxVec3(pxTmpVec)) }

    actual var angularVelocity: Vec3f
        get() = pxRigidDynamic.getAngularVelocity().toVec3f(bufAngVelocity)
        set(value) { pxRigidDynamic.setAngularVelocity(value.toPxVec3(pxTmpVec)) }

    actual var maxLinearVelocity: Float
        get() = pxRigidDynamic.getMaxLinearVelocity()
        set(value) { pxRigidDynamic.setMaxLinearVelocity(value) }

    actual var maxAngularVelocity: Float
        get() = pxRigidDynamic.getMaxAngularVelocity()
        set(value) { pxRigidDynamic.setMaxAngularVelocity(value) }

    actual var linearDamping: Float
        get() = pxRigidDynamic.getLinearDamping()
        set(value) { pxRigidDynamic.setLinearDamping(value) }

    actual var angularDamping: Float
        get() = pxRigidDynamic.getAngularDamping()
        set(value) { pxRigidDynamic.setAngularDamping(value) }

    init {
        pose.toPxTransform(pxPose)
        pxRigidDynamic = Physics.physics.createRigidDynamic(pxPose)
        pxRigidActor = pxRigidDynamic
        this.mass = mass
    }

    override fun attachShape(shape: Shape) {
        super.attachShape(shape)
        if (!isInertiaSet) {
            inertia = shape.geometry.estimateInertiaForMass(mass)
        }
    }

    override fun release() {
        super.release()
        PhysXJsLoader.destroy(pxTmpVec)
    }
}