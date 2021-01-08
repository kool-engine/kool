package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.physics.shapes.CollisionShape
import physx.*

actual class RigidBody actual constructor(collisionShape: CollisionShape, mass: Float, bodyProperties: RigidBodyProperties)
    : CommonRigidBody(collisionShape, mass == 0f, bodyProperties)
{
    val pxActor: PxRigidActor

    private val bufOrigin = MutableVec3f()
    private val bufRotation = MutableVec4f()

    override var origin: Vec3f
        get() = pxActor.getGlobalPose().translation.toVec3f(bufOrigin)
        set(value) {
            val t = pxActor.getGlobalPose()
            t.translation.set(value)
            pxActor.setGlobalPose(t, true)
            updateTransform()
        }

    override var rotation: Vec4f
        get() = pxActor.getGlobalPose().rotation.toVec4f(bufRotation)
        set(value) {
            val t = pxActor.getGlobalPose()
            t.rotation.set(value)
            pxActor.setGlobalPose(t, true)
            updateTransform()
        }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    override var mass: Float
        get() = if (isStatic) 0f else (pxActor as PxRigidDynamic).getMass()
        set(value) { if (!isStatic) (pxActor as PxRigidDynamic).setMass(value) }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    override var inertia: Vec3f
        get() = if (isStatic) Vec3f.ZERO else (pxActor as PxRigidDynamic).getMassSpaceInertiaTensor().toVec3f()
        set(value) { if (!isStatic) (pxActor as PxRigidDynamic).setMassSpaceInertiaTensor(value.toPxVec3()) }

    init {
        Physics.checkIsLoaded()

        val collisionFilter = PhysX.PxFilterData(bodyProperties.collisionGroupBits, bodyProperties.collisionMask, 0, 0)
        val material = PhysX.physics.createMaterial(bodyProperties.friction, bodyProperties.friction, bodyProperties.restitution)
        val flags = PhysX.PxShapeFlags(PhysX.PxShapeFlag.eSCENE_QUERY_SHAPE.value or PhysX.PxShapeFlag.eSIMULATION_SHAPE.value)

        val pose = PxTransform()
        pxActor = if (mass > 0f) {
            val rigidBody = PhysX.physics.createRigidDynamic(pose)
            rigidBody.setMass(mass)
            rigidBody.setMassSpaceInertiaTensor(collisionShape.estimateInertiaForMass(mass, MutableVec3f()).toPxVec3())
            rigidBody
        } else {
            PhysX.physics.createRigidStatic(pose)
        }

        collisionShape.attachTo(pxActor, material, flags, collisionFilter)
    }

    override fun fixedUpdate(timeStep: Float) {
        super.fixedUpdate(timeStep)
        updateTransform()
    }

    private fun updateTransform() {
        pxActor.getGlobalPose().toMat4f(transform)
    }
}