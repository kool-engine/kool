package de.fabmax.kool.physics

import com.bulletphysics.linearmath.Transform
import de.fabmax.kool.math.*
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f

// assign type aliases to JBullet classes to distinguish them from our own classes

typealias BtCollisionShape = com.bulletphysics.collision.shapes.CollisionShape
typealias BtBoxShape = com.bulletphysics.collision.shapes.BoxShape
typealias BtCapsuleShape = com.bulletphysics.collision.shapes.CapsuleShape
typealias BtConvexHullShape = com.bulletphysics.collision.shapes.ConvexHullShape
typealias BtCylinderShape = com.bulletphysics.collision.shapes.CylinderShape
typealias BtSphereShape = com.bulletphysics.collision.shapes.SphereShape
typealias BtStaticPlaneShape = com.bulletphysics.collision.shapes.StaticPlaneShape

typealias BtHingeConstraint = com.bulletphysics.dynamics.constraintsolver.HingeConstraint

typealias BtRigidBody = com.bulletphysics.dynamics.RigidBody

// conversion functions from / to bullet types

fun Vec3f.toBtVector3f(result: Vector3f = Vector3f()) = result.set(this)
fun Vector3f.toVec3f(result: MutableVec3f = MutableVec3f()) = result.set(x, y, z)
fun Vector3f.set(v: Vec3f): Vector3f {
    set(v.x, v.y, v.z)
    return this
}

fun Vec4f.toBtQuat4f(result: Quat4f = Quat4f()) = result.set(this)
fun Quat4f.toVec4f(result: MutableVec4f = MutableVec4f()) = result.set(x, y, z, w)
fun Quat4f.set(q: Vec4f): Quat4f {
    set(q.x, q.y, q.z, q.w)
    return this
}

fun Mat4f.toBtTransform(result: Transform = Transform()) = result.set(this)
fun Transform.toMat4f(result: Mat4f = Mat4f()): Mat4f {
    getOpenGLMatrix(result.matrix)
    return result
}
fun Transform.set(mat: Mat4f): Transform {
    setFromOpenGLMatrix(mat.matrix)
    return this
}

internal object BulletTypes {
    val IDENTITY = Transform().apply { setIdentity() }
}
