@file:Suppress("UnsafeCastFromDynamic", "FunctionName")

package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.TrsTransformF
import physx.*
import kotlin.contracts.contract

fun PxBounds3.toBoundingBox(result: BoundingBoxF): BoundingBoxF {
    val min = minimum
    val max = maximum
    return result.set(min.x, min.y, min.z, max.x, max.y, max.z)
}

fun BoundingBoxF.toPxBounds3(result: PxBounds3): PxBounds3 {
    val v = PxVec3()
    result.minimum = min.toPxVec3(v)
    result.maximum = max.toPxVec3(v)
    PhysXJsLoader.destroy(v)
    return result
}

fun PxTransform() = PxTransform(PxIDENTITYEnum.PxIdentity)
fun PxTransform.toMat4f(result: MutableMat4f): Mat4f {
    result.setIdentity().rotate(q.toQuatF())
    result[0, 3] = p.x
    result[1, 3] = p.y
    result[2, 3] = p.z
    return result
}

fun PxTransform.toPoseF(result: MutablePoseF): MutablePoseF {
    p.toVec3f(result.position)
    q.toQuatF(result.rotation)
    return result
}

fun PxTransform.toTrsTransform(result: TrsTransformF): TrsTransformF {
    result.translation.set(p.x, p.y, p.z)
    result.rotation.set(q.toQuatF())
    result.scale.set(Vec3d.ONES)
    result.markDirty()
    return result
}

fun PxTransform.set(mat: Mat4f): PxTransform {
    val qq = MutableQuatF()
    mat.decompose(rotation = qq)
    qq.toPxQuat(q)
    p.x = mat[0, 3]
    p.y = mat[1, 3]
    p.z = mat[2, 3]
    return this
}

fun PxTransform.set(pose: PoseF): PxTransform {
    pose.position.toPxVec3(p)
    pose.rotation.toPxQuat(q)
    return this
}

fun PxTransform.setIdentity(): PxTransform {
    q.setIdentity()
    p.set(Vec3f.ZERO)
    return this
}

fun Mat4f.toPxTransform(t: PxTransform) = t.set(this)
fun PoseF.toPxTransform(t: PxTransform) = t.set(this)

fun PxQuat.setIdentity(): PxQuat { x = 0f; y = 0f; z = 0f; w = 1f; return this }
fun PxQuat.toQuatF(result: MutableQuatF = MutableQuatF()) = result.set(x, y, z, w)
fun PxQuat.set(q: QuatF): PxQuat { x = q.x; y = q.y; z = q.z; w = q.w; return this }
fun QuatF.toPxQuat(result: PxQuat) = result.set(this)

fun PxVec3.toVec3f(result: MutableVec3f = MutableVec3f()) = result.set(x, y, z)
fun PxVec3.set(v: Vec3f): PxVec3 { x = v.x; y = v.y; z = v.z; return this }
fun Vec3f.toPxVec3(result: PxVec3) = result.set(this)

fun PxExtendedVec3.toVec3d(result: MutableVec3d = MutableVec3d()) = result.set(x, y, z)
fun PxExtendedVec3.set(v: Vec3d): PxExtendedVec3 { x = v.x; y = v.y; z = v.z; return this }
fun Vec3d.toPxExtendedVec3(result: PxExtendedVec3) = result.set(this)

@Suppress("FunctionName")
fun List<Vec3f>.toPxArray_PxVec3(): PxArray_PxVec3 {
    val vector = PxArray_PxVec3(size)
    forEachIndexed { i, v -> v.toPxVec3(vector.get(i)) }
    return vector
}

fun PxFilterData(w0: Int = 0, w1: Int = 0, w2: Int = 0): PxFilterData = PxFilterData(w0, w1, w2, 0)
fun PxFilterData(filterData: FilterData): PxFilterData =
    PxFilterData(filterData.word0, filterData.word1, filterData.word2, filterData.word3)

fun FilterData.toPxFilterData(target: PxFilterData): PxFilterData {
    target.word0 = word0
    target.word1 = word1
    target.word2 = word2
    target.word3 = word3
    return target
}

class MemoryStack private constructor() {
    val autoDeletables = mutableListOf<Any>()

    fun <T: Any> autoDelete(obj: T): T {
        autoDeletables += obj
        return obj
    }

    inline fun <R> use(block: (MemoryStack) -> R): R {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        try {
            return block(this)
        } finally {
            autoDeletables.forEach { PhysXJsLoader.destroy(it) }
            autoDeletables.clear()
        }
    }

    companion object {
        fun stackPush(): MemoryStack = MemoryStack()
    }

    fun createPxArray_PxShapePtr(size: Int) = autoDelete(PxArray_PxShapePtr(size))
    fun createPxArticulationDrive() = autoDelete(PxArticulationDrive())
    fun createPxArticulationLimit(low: Float, high: Float) = autoDelete(PxArticulationLimit(low, high))
    fun createPxBoundedData() = autoDelete(PxBoundedData())
    fun createPxFilterData() = autoDelete(PxFilterData())
    fun createPxFilterData(w0: Int, w1: Int, w2: Int, w3: Int) = autoDelete(PxFilterData(w0, w1, w2, w3))
    fun createPxQueryFilterData(fd: PxFilterData, f: PxQueryFlags) = autoDelete(PxQueryFilterData(fd, f))
    fun createPxQueryFlags(flags: Short) = autoDelete(PxQueryFlags(flags))
    fun createPxHeightFieldSample() = autoDelete(PxHeightFieldSample())
    fun createPxHullPolygon() = autoDelete(PxHullPolygon())
    fun createPxMeshScale(s: PxVec3, r: PxQuat) = autoDelete(PxMeshScale(s, r))
    fun createPxMeshScale(s: Vec3f, r: QuatF = QuatF.IDENTITY) = autoDelete(PxMeshScale(s.toPxVec3(createPxVec3()), r.toPxQuat(createPxQuat())))
    fun createPxVec3() = autoDelete(PxVec3())
    fun createPxVec3(x: Float, y: Float, z: Float) = autoDelete(PxVec3(x, y, z))

    fun createPxQuat() = autoDelete(PxQuat())
    fun createPxQuat(x: Float, y: Float, z: Float, w: Float) = autoDelete(PxQuat(x, y, z, w))

    fun createPxTransform() = autoDelete(PxTransform(PxIDENTITYEnum.PxIdentity))
    fun createPxTransform(p: PxVec3, q: PxQuat) = autoDelete(PxTransform(p, q))

    fun createPxSceneDesc(scale: PxTolerancesScale) = autoDelete(PxSceneDesc(scale))
    fun createPxConvexMeshDesc() = autoDelete(PxConvexMeshDesc())
    fun createPxHeightFieldDesc() = autoDelete(PxHeightFieldDesc())
    fun createPxTriangleMeshDesc() = autoDelete(PxTriangleMeshDesc())

    fun createPxActorFlags(flags: Int) = autoDelete(PxActorFlags(flags.toByte()))
    fun createPxBaseFlags(flags: Int) = autoDelete(PxBaseFlags(flags.toShort()))
    fun createPxConvexFlags(flags: Int) = autoDelete(PxConvexFlags(flags.toShort()))
    fun createPxConvexMeshGeometryFlags(flags: Int) = autoDelete(PxConvexMeshGeometryFlags(flags.toByte()))
    fun createPxHitFlags(flags: Int) = autoDelete(PxHitFlags(flags.toShort()))
    fun createPxMeshGeometryFlags(flags: Int) = autoDelete(PxMeshGeometryFlags(flags.toByte()))
    fun createPxRevoluteJointFlags(flags: Int) = autoDelete(PxRevoluteJointFlags(flags.toShort()))
    fun createPxRigidBodyFlags(flags: Int) = autoDelete(PxRigidBodyFlags(flags.toByte()))
    fun createPxRigidDynamicLockFlags(flags: Int) = autoDelete(PxRigidDynamicLockFlags(flags.toByte()))
    fun createPxSceneFlags(flags: Int) = autoDelete(PxSceneFlags(flags))
    fun createPxShapeFlags(flags: Int) = autoDelete(PxShapeFlags(flags.toByte()))
}