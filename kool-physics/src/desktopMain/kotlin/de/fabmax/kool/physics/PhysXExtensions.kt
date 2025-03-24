@file:Suppress("UnsafeCastFromDynamic", "FunctionName")

package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.TrsTransformF
import org.lwjgl.system.MemoryStack
import physx.character.PxExtendedVec3
import physx.common.*
import physx.cooking.PxConvexFlags
import physx.cooking.PxConvexMeshDesc
import physx.cooking.PxTriangleMeshDesc
import physx.extensions.PxRevoluteJointFlags
import physx.geometry.*
import physx.physics.*
import physx.support.PxArray_PxShapePtr
import physx.support.PxArray_PxU32
import physx.support.PxArray_PxVec3

fun PxBounds3.toBoundingBox(result: BoundingBoxF): BoundingBoxF {
    val min = minimum
    val max = maximum
    return result.set(min.x, min.y, min.z, max.x, max.y, max.z)
}

fun BoundingBoxF.toPxBounds3(result: PxBounds3): PxBounds3 {
    val v = PxVec3()
    result.minimum = min.toPxVec3(v)
    result.maximum = max.toPxVec3(v)
    v.destroy()
    return result
}

fun PxTransform() = PxTransform(PxIDENTITYEnum.PxIdentity)
fun PxTransform.toMat4f(result: MutableMat4f): MutableMat4f {
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

fun MemoryStack.createPxArray_PxU32() = PxArray_PxU32.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxArray_PxU32(size: Int) = PxArray_PxU32.createAt(this, MemoryStack::nmalloc, size)
fun MemoryStack.createPxArray_PxVec3() = PxArray_PxVec3.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxArray_PxVec3(size: Int) = PxArray_PxVec3.createAt(this, MemoryStack::nmalloc, size)
fun MemoryStack.createPxArray_PxShapePtr(size: Int) = PxArray_PxShapePtr.createAt(this, MemoryStack::nmalloc, size)
fun MemoryStack.createPxArticulationDrive() = PxArticulationDrive.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxArticulationLimit(low: Float, high: Float) = PxArticulationLimit.createAt(this, MemoryStack::nmalloc, low, high)
fun MemoryStack.createPxBoundedData() = PxBoundedData.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxFilterData() = PxFilterData.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxFilterData(w0: Int, w1: Int, w2: Int, w3: Int) =
    PxFilterData.createAt(this, MemoryStack::nmalloc, w0, w1, w2, w3)
fun MemoryStack.createPxHeightFieldSample() = PxHeightFieldSample.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxHullPolygon() = PxHullPolygon.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxMeshScale(s: PxVec3, r: PxQuat) = PxMeshScale.createAt(this, MemoryStack::nmalloc, s, r)
fun MemoryStack.createPxMeshScale(s: Vec3f, r: QuatF = QuatF.IDENTITY) = PxMeshScale.createAt(this, MemoryStack::nmalloc, s.toPxVec3(createPxVec3()), r.toPxQuat(createPxQuat()))
fun MemoryStack.createPxVec3() = PxVec3.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxVec3(x: Float, y: Float, z: Float) = PxVec3.createAt(this, MemoryStack::nmalloc, x, y, z)

fun MemoryStack.createPxQuat() = PxQuat.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxQuat(x: Float, y: Float, z: Float, w: Float) =
    PxQuat.createAt(this, MemoryStack::nmalloc, x, y, z, w)

fun MemoryStack.createPxTransform() = PxTransform.createAt(this, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity)
fun MemoryStack.createPxTransform(p: PxVec3, q: PxQuat) =
    PxTransform.createAt(this, MemoryStack::nmalloc, p, q)

fun MemoryStack.createPxConvexMeshDesc() = PxConvexMeshDesc.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxHeightFieldDesc() = PxHeightFieldDesc.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.createPxTriangleMeshDesc() = PxTriangleMeshDesc.createAt(this, MemoryStack::nmalloc)

fun MemoryStack.createPxActorFlags(flags: Int) = PxActorFlags.createAt(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.createPxBaseFlags(flags: Int) = PxBaseFlags.createAt(this, MemoryStack::nmalloc, flags.toShort())
fun MemoryStack.createPxConvexFlags(flags: Int) = PxConvexFlags.createAt(this, MemoryStack::nmalloc, flags.toShort())
fun MemoryStack.createPxConvexMeshGeometryFlags(flags: Int) = PxConvexMeshGeometryFlags.createAt(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.createPxHitFlags(flags: Int) = PxHitFlags.createAt(this, MemoryStack::nmalloc, flags.toShort())
fun MemoryStack.createPxMeshGeometryFlags(flags: Int) = PxMeshGeometryFlags.createAt(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.createPxRevoluteJointFlags(flags: Int) = PxRevoluteJointFlags.createAt(this, MemoryStack::nmalloc, flags.toShort())
fun MemoryStack.createPxRigidBodyFlags(flags: Int) = PxRigidBodyFlags.createAt(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.createPxRigidDynamicLockFlags(flags: Int) = PxRigidDynamicLockFlags.createAt(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.createPxSceneFlags(flags: Int) = PxSceneFlags.createAt(this, MemoryStack::nmalloc, flags)
fun MemoryStack.createPxShapeFlags(flags: Int) = PxShapeFlags.createAt(this, MemoryStack::nmalloc, flags.toByte())
