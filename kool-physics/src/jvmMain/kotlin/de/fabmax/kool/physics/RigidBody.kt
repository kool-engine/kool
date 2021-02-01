package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.geometry.CollisionGeometry
import physx.physics.PxRigidActor
import physx.physics.PxRigidDynamic
import physx.physics.PxShape

actual class RigidBody actual constructor(mass: Float, bodyProperties: RigidBodyProperties)
    : CommonRigidBody(mass == 0f)
{
    val pxActor: PxRigidActor

    private val bufOrigin = MutableVec3f()
    private val bufRotation = MutableVec4f()

    private val simFilterData = FilterData().set(bodyProperties.simFilterData)
    private val queryFilterData = FilterData().set(bodyProperties.queryFilterData)

    override var origin: Vec3f
        get() = pxActor.getGlobalPose().p.toVec3f(bufOrigin)
        set(value) {
            val t = pxActor.getGlobalPose()
            t.p.set(value)
            pxActor.setGlobalPose(t)
            updateTransform()
        }

    override var rotation: Vec4f
        get() = pxActor.getGlobalPose().q.toVec4f(bufRotation)
        set(value) {
            val t = pxActor.getGlobalPose()
            t.q.set(value)
            pxActor.setGlobalPose(t)
            updateTransform()
        }

    override var mass: Float
        get() = if (isStatic) 0f else (pxActor as PxRigidDynamic).mass
        set(value) { if (!isStatic) (pxActor as PxRigidDynamic).mass = value }

    private var isInertiaSet = false
    override var inertia: Vec3f
        get() = if (isStatic) Vec3f.ZERO else (pxActor as PxRigidDynamic).massSpaceInertiaTensor.toVec3f()
        set(value) {
            if (!isStatic) (pxActor as PxRigidDynamic).massSpaceInertiaTensor = value.toPxVec3()
            isInertiaSet = true
        }

    init {
        val pose = PxTransform()
        pxActor = if (mass > 0f) {
            val rigidBody = Physics.physics.createRigidDynamic(pose)
            rigidBody.mass = mass
            rigidBody.angularDamping = bodyProperties.angularDamping
            rigidBody.linearDamping = bodyProperties.linearDamping
            rigidBody
        } else {
            Physics.physics.createRigidStatic(pose)
        }
    }

    actual fun attachShape(geometry: CollisionGeometry, material: Material, localPose: Mat4f): RigidBody {
        val shape = Physics.physics.createShape(geometry.pxGeometry, material.pxMaterial, true)
        shape.setFilterDatas()
        shape.localPose = localPose.toPxTransform(shape.localPose)
        pxActor.attachShape(shape)
        mutShapes += geometry to localPose

        if (!isInertiaSet) {
            inertia = geometry.estimateInertiaForMass(mass)
        }
        return this
    }

    override fun fixedUpdate(timeStep: Float) {
        super.fixedUpdate(timeStep)
        updateTransform()
    }

    private fun updateTransform() {
        pxActor.getGlobalPose().toMat4f(transform)
    }

    private fun PxShape.setFilterDatas() {
        val pxFilterData = PxFilterData()
        this@RigidBody.simFilterData.toPxFilterData(pxFilterData)
        simulationFilterData = pxFilterData
        this@RigidBody.queryFilterData.toPxFilterData(pxFilterData)
        queryFilterData = pxFilterData
        pxFilterData.destroy()
    }
}