package de.fabmax.kool.physics

import ammo.*
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.shapes.CollisionShape

actual class RigidBody actual constructor(actual val collisionShape: CollisionShape, actual val mass: Float) : CommonRigidBody() {
    val btRigidBody: btRigidBody

    actual val transform = Mat4f()

    private val bufOrigin = MutableVec3f()
    private val bufRotation = MutableVec4f()

    actual var origin: Vec3f
        get() = btRigidBody.getWorldTransform().getOrigin().toVec3f(bufOrigin)
        set(value) {
            btRigidBody.getWorldTransform().getOrigin().set(value)
        }

    actual var rotation: Vec4f
        get() = btRigidBody.getWorldTransform().getRotation().toVec4f(bufRotation)
        set(value) {
            btRigidBody.getWorldTransform().setRotation(value.toBtQuaternion())
        }

    init {
        Physics.checkIsLoaded()

        val motionState = Ammo.btDefaultMotionState()
        val boxInertia = Ammo.btVector3(0f, 0f, 0f)
        val btShape = collisionShape.shape
        if (mass > 0f) {
            btShape.calculateLocalInertia(mass, boxInertia)
        }
        val constructionInfo = Ammo.btRigidBodyConstructionInfo(mass, motionState, btShape, boxInertia)

        btRigidBody = Ammo.btRigidBody(constructionInfo)
    }

    actual fun setRotation(rotation: Mat3f) {
        val quat = rotation.getRotation(bufRotation)
        btRigidBody.getWorldTransform().setRotation(quat.toBtQuaternion())
    }

    override fun fixedUpdate(timeStep: Float) {
        super.fixedUpdate(timeStep)
        btRigidBody.getWorldTransform().toMat4f(transform)
    }
}