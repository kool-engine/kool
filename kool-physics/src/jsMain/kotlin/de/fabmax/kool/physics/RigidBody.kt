package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.geometry.CollisionGeometry
import physx.PhysxJsLoader
import physx.PxRigidActor
import physx.PxRigidDynamic
import physx.PxShape

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

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    override var mass: Float
        get() = if (isStatic) 0f else (pxActor as PxRigidDynamic).getMass()
        set(value) { if (!isStatic) (pxActor as PxRigidDynamic).setMass(value) }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    override var inertia: Vec3f
        get() = if (isStatic) Vec3f.ZERO else (pxActor as PxRigidDynamic).getMassSpaceInertiaTensor().toVec3f()
        set(value) {
            if (!isStatic) (pxActor as PxRigidDynamic).setMassSpaceInertiaTensor(value.toPxVec3())
            isInertiaSet = true
        }
    private var isInertiaSet = false

    init {
        Physics.checkIsLoaded()

        val pose = PxTransform()
        pxActor = if (mass > 0f) {
            val rigidBody = Physics.physics.createRigidDynamic(pose)
            rigidBody.setMass(mass)
            rigidBody.setAngularDamping(bodyProperties.angularDamping)
            rigidBody.setLinearDamping(bodyProperties.linearDamping)
            rigidBody
        } else {
            Physics.physics.createRigidStatic(pose)
        }
    }

    actual fun attachShape(geometry: CollisionGeometry, material: Material, localPose: Mat4f): RigidBody {
        val shape = Physics.physics.createShape(geometry.pxGeometry, material.pxMaterial, true)
        shape.setFilterDatas()
        shape.setLocalPose(localPose.toPxTransform(shape.getLocalPose()))
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
        val pxFilterData = physx.PxFilterData()
        simFilterData.toPxFilterData(pxFilterData)
        setSimulationFilterData(pxFilterData)
        queryFilterData.toPxFilterData(pxFilterData)
        setQueryFilterData(pxFilterData)
        PhysxJsLoader.destroy(pxFilterData)
    }
}