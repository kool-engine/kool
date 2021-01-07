package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.physics.shapes.CollisionShape
import physx.*

actual class RigidBody actual constructor(collisionShape: CollisionShape, mass: Float, bodyProperties: RigidBodyProperties)
    : CommonRigidBody(collisionShape, mass, bodyProperties)
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

    init {
        Physics.checkIsLoaded()

        val collisionFilter = PhysX.PxFilterData(bodyProperties.collisionGroupBits, bodyProperties.collisionMask, 0, 0)
        val material = PhysX.physics.createMaterial(bodyProperties.friction, bodyProperties.friction, bodyProperties.restitution)
        val flags = PhysX.PxShapeFlags(PhysX.PxShapeFlag.eSCENE_QUERY_SHAPE.value or PhysX.PxShapeFlag.eSIMULATION_SHAPE.value)

        val pose = PxTransform()
        if (mass > 0f) {
            pxActor = PhysX.physics.createRigidDynamic(pose)
            //pxActor.
        } else {
            pxActor = PhysX.physics.createRigidStatic(pose)
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