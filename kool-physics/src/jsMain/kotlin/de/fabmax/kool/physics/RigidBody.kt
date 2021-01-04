package de.fabmax.kool.physics

import ammo.*
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.physics.shapes.CollisionShape

actual class RigidBody actual constructor(collisionShape: CollisionShape, mass: Float, bodyProperties: RigidBodyProperties) : CommonRigidBody(collisionShape, mass) {
    val btRigidBody: btRigidBody

    private val bufOrigin = MutableVec3f()
    private val bufRotation = MutableVec4f()

    override var origin: Vec3f
        get() = btRigidBody.getWorldTransform().getOrigin().toVec3f(bufOrigin)
        set(value) {
            btRigidBody.getWorldTransform().getOrigin().set(value)
            updateTransform()
        }

    override var rotation: Vec4f
        get() = btRigidBody.getWorldTransform().getRotation().toVec4f(bufRotation)
        set(value) {
            btRigidBody.getWorldTransform().setRotation(value.toBtQuaternion())
            updateTransform()
        }

    init {
        Physics.checkIsLoaded()

        val motionState = Ammo.btDefaultMotionState()
        val boxInertia = Ammo.btVector3(0f, 0f, 0f)
        val btShape = collisionShape.btShape
        if (mass > 0f) {
            btShape.calculateLocalInertia(mass, boxInertia)
        }
        val constructionInfo = Ammo.btRigidBodyConstructionInfo(mass, motionState, btShape, boxInertia)
        constructionInfo.m_friction = bodyProperties.friction
        constructionInfo.m_rollingFriction = bodyProperties.rollingFriction
        constructionInfo.m_restitution = bodyProperties.restitution
        constructionInfo.m_linearDamping = bodyProperties.linearDamping
        constructionInfo.m_angularDamping = bodyProperties.angularDamping
        constructionInfo.m_linearSleepingThreshold *= bodyProperties.sleepThreshold
        constructionInfo.m_angularSleepingThreshold *= bodyProperties.sleepThreshold

        btRigidBody = Ammo.btRigidBody(constructionInfo)
    }

    override fun fixedUpdate(timeStep: Float) {
        super.fixedUpdate(timeStep)
        updateTransform()
    }

    private fun updateTransform() {
        btRigidBody.getWorldTransform().toMat4f(transform)
    }
}