package de.fabmax.kool.physics

import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.Physics.set
import de.fabmax.kool.physics.Physics.toQuat4f
import de.fabmax.kool.physics.Physics.toVec3f
import de.fabmax.kool.physics.Physics.toVec4f
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f

actual class RigidBody actual constructor(actual val collisionShape: CollisionShape, actual val mass: Float) : CommonRigidBody() {
    val btRigidBody: btRigidBody

    actual val transform = Mat4f()

    private val bufOrigin = MutableVec3f()
    private val bufRotation = MutableVec4f()
    private val bufTransform = Transform()
    private val bufQuat = Quat4f()

    actual var origin: Vec3f
        get() {
            btRigidBody.getWorldTransform(bufTransform)
            bufTransform.origin.toVec3f(bufOrigin)
            return bufOrigin
        }
        set(value) {
            btRigidBody.getWorldTransform(bufTransform)
            bufTransform.origin.set(value)
            btRigidBody.setWorldTransform(bufTransform)
        }

    actual var rotation: Vec4f
        get() {
            btRigidBody.getWorldTransform(bufTransform)
            bufTransform.getRotation(bufQuat).toVec4f(bufRotation)
            return bufRotation
        }
        set(value) {
            btRigidBody.getWorldTransform(bufTransform)
            bufTransform.setRotation(value.toQuat4f())
            btRigidBody.setWorldTransform(bufTransform)
        }

    init {
        val startTransform = Transform()
        startTransform.setIdentity()
        val motionState = DefaultMotionState(startTransform)
        val boxInertia = Vector3f(0f, 0f, 0f)
        val btShape = collisionShape.shape
        if (mass > 0f) {
            btShape.calculateLocalInertia(mass, boxInertia)
        }
        val constructionInfo = RigidBodyConstructionInfo(mass, motionState, btShape, boxInertia)

        btRigidBody = btRigidBody(constructionInfo)
    }

    actual fun setRotation(rotation: Mat3f) {
        val quat = rotation.getRotation(bufRotation)
        btRigidBody.getWorldTransform(bufTransform).setRotation(quat.toQuat4f())
        btRigidBody.setWorldTransform(bufTransform)
    }

    override fun fixedUpdate(timeStep: Float) {
        super.fixedUpdate(timeStep)
        btRigidBody.getWorldTransform(bufTransform)
        bufTransform.getOpenGLMatrix(transform.matrix)
    }
}