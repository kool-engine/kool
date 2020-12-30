package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f
import kotlin.coroutines.CoroutineContext

typealias btBoxShape = com.bulletphysics.collision.shapes.BoxShape
typealias btCollisionShape = com.bulletphysics.collision.shapes.CollisionShape
typealias btRigidBody = com.bulletphysics.dynamics.RigidBody

actual object Physics : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    actual val isLoaded = true

    actual fun loadPhysics() { }

    actual suspend fun awaitLoaded() { }

    fun Vec3f.toVector3f() = Vector3f(x, y, z)
    fun Vector3f.toVec3f(result: MutableVec3f = MutableVec3f()) = result.set(x, y, z)
    fun Vector3f.set(v: Vec3f): Vector3f {
        set(v.x, v.y, v.z)
        return this
    }

    fun Vec4f.toQuat4f() = Quat4f(x, y, z, w)
    fun Quat4f.toVec4f(result: MutableVec4f = MutableVec4f()) = result.set(x, y, z, w)
    fun Quat4f.set(q: Vec4f): Quat4f {
        set(q.x, q.y, q.z, q.w)
        return this
    }
}