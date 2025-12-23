package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.ScopedMemory
import physx.*

object SIZEOF {
    val PxVec3 = 12
    val PxHeightFieldSample = 4
}

object WrapPointer {
    fun PxCapsuleController(ptr: Int) = PxCapsuleControllerFromPointer(ptr)
    fun PxRigidDynamic(ptr: Int) = PxRigidDynamicFromPointer(ptr)
}

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

fun PxTransform() = PxTransform(PxIDENTITYEnum.PxIdentity.value)
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

var PxRigidDynamic.linearVelocity: PxVec3
    get() = getLinearVelocity()
    set(value) { setLinearVelocity(value) }
var PxRigidDynamic.angularVelocity: PxVec3
    get() = getAngularVelocity()
    set(value) { setAngularVelocity(value) }


fun ScopedMemory.createPxArray_PxShapePtr(size: Int) = autoDelete(PxArray_PxShapePtr(size), PxArray_PxShapePtr::destroy)
fun ScopedMemory.createPxArticulationDrive() = autoDelete(PxArticulationDrive(), PxArticulationDrive::destroy)
fun ScopedMemory.createPxArticulationLimit(low: Float, high: Float) = autoDelete(PxArticulationLimit(low, high), PxArticulationLimit::destroy)
fun ScopedMemory.createPxBoundedData() = autoDelete(PxBoundedData(), PxBoundedData::destroy)
fun ScopedMemory.createPxFilterData() = autoDelete(PxFilterData(), PxFilterData::destroy)
fun ScopedMemory.createPxFilterData(w0: Int, w1: Int, w2: Int, w3: Int) = autoDelete(PxFilterData(w0, w1, w2, w3), PxFilterData::destroy)
fun ScopedMemory.createPxQueryFilterData(fd: PxFilterData, f: PxQueryFlags) = autoDelete(PxQueryFilterData(fd, f), PxQueryFilterData::destroy)
fun ScopedMemory.createPxQueryFlags(flags: PxQueryFlagEnum) = autoDelete(PxQueryFlags(flags.value.toShort()), PxQueryFlags::destroy)
fun ScopedMemory.createPxHeightFieldSample() = autoDelete(PxHeightFieldSample(), PxHeightFieldSample::destroy)
fun ScopedMemory.createPxHullPolygon() = autoDelete(PxHullPolygon(), PxHullPolygon::destroy)
fun ScopedMemory.createPxMeshScale(s: PxVec3, r: PxQuat) = autoDelete(PxMeshScale(s, r), PxMeshScale::destroy)
fun ScopedMemory.createPxMeshScale(s: Vec3f, r: QuatF = QuatF.IDENTITY) = autoDelete(PxMeshScale(s.toPxVec3(createPxVec3()), r.toPxQuat(createPxQuat())), PxMeshScale::destroy)
fun ScopedMemory.createPxVec3() = autoDelete(PxVec3(), PxVec3::destroy)
fun ScopedMemory.createPxVec3(x: Float, y: Float, z: Float) = autoDelete(PxVec3(x, y, z), PxVec3::destroy)

fun ScopedMemory.createPxQuat() = autoDelete(PxQuat(), PxQuat::destroy)
fun ScopedMemory.createPxQuat(x: Float, y: Float, z: Float, w: Float) = autoDelete(PxQuat(x, y, z, w), PxQuat::destroy)

fun ScopedMemory.createPxTransform() = autoDelete(PxTransform(PxIDENTITYEnum.PxIdentity.value), PxTransform::destroy)
fun ScopedMemory.createPxTransform(p: PxVec3, q: PxQuat) = autoDelete(PxTransform(p, q), PxTransform::destroy)
fun ScopedMemory.createPxTransform(p: Vec3f, q: QuatF) = autoDelete(PxTransform(p.toPxVec3(createPxVec3()), q.toPxQuat(createPxQuat())), PxTransform::destroy)

fun ScopedMemory.createPxSceneDesc(scale: PxTolerancesScale) = autoDelete(PxSceneDesc(scale), PxSceneDesc::destroy)
fun ScopedMemory.createPxConvexMeshDesc() = autoDelete(PxConvexMeshDesc(), PxConvexMeshDesc::destroy)
fun ScopedMemory.createPxHeightFieldDesc() = autoDelete(PxHeightFieldDesc(), PxHeightFieldDesc::destroy)
fun ScopedMemory.createPxTriangleMeshDesc() = autoDelete(PxTriangleMeshDesc(), PxTriangleMeshDesc::destroy)

fun ScopedMemory.createPxActorFlags(flags: Int) = autoDelete(PxActorFlags(flags.toByte()), PxActorFlags::destroy)
fun ScopedMemory.createPxBaseFlags(flags: Int) = autoDelete(PxBaseFlags(flags.toShort()), PxBaseFlags::destroy)
fun ScopedMemory.createPxConvexFlags(flags: Int) = autoDelete(PxConvexFlags(flags.toShort()), PxConvexFlags::destroy)
fun ScopedMemory.createPxConvexMeshGeometryFlags(flags: Int) = autoDelete(PxConvexMeshGeometryFlags(flags.toByte()), PxConvexMeshGeometryFlags::destroy)
fun ScopedMemory.createPxHitFlags(flags: Int) = autoDelete(PxHitFlags(flags.toShort()), PxHitFlags::destroy)
fun ScopedMemory.createPxMeshGeometryFlags(flags: Int) = autoDelete(PxMeshGeometryFlags(flags.toByte()), PxMeshGeometryFlags::destroy)
fun ScopedMemory.createPxRevoluteJointFlags(flags: Int) = autoDelete(PxRevoluteJointFlags(flags.toShort()), PxRevoluteJointFlags::destroy)
fun ScopedMemory.createPxRigidBodyFlags(flags: Int) = autoDelete(PxRigidBodyFlags(flags.toByte()), PxRigidBodyFlags::destroy)
fun ScopedMemory.createPxRigidDynamicLockFlags(flags: Int) = autoDelete(PxRigidDynamicLockFlags(flags.toByte()), PxRigidDynamicLockFlags::destroy)
fun ScopedMemory.createPxSceneFlags(flags: Int) = autoDelete(PxSceneFlags(flags), PxSceneFlags::destroy)
fun ScopedMemory.createPxShapeFlags(flags: Int) = autoDelete(PxShapeFlags(flags.toByte()), PxShapeFlags::destroy)

fun ScopedMemory.createPxSpring(stiffness: Float, damping: Float) = autoDelete(PxSpring(stiffness, damping), PxSpring::destroy)
fun ScopedMemory.createPxJointLinearLimitPair(lowerLimit: Float, upperLimit: Float, spring: PxSpring) =
    autoDelete(PxJointLinearLimitPair(lowerLimit, upperLimit, spring), PxJointLinearLimitPair::destroy)
fun ScopedMemory.createPxJointLinearLimit(extent: Float, spring: PxSpring) = autoDelete(PxJointLinearLimit(extent, spring), PxJointLinearLimit::destroy)
fun ScopedMemory.createPxJointAngularLimitPair(lowerLimit: AngleF, upperLimit: AngleF, spring: PxSpring) =
    autoDelete(PxJointAngularLimitPair(lowerLimit.rad, upperLimit.rad, spring), PxJointAngularLimitPair::destroy)
fun ScopedMemory.createPxJointLimitPyramid(yLimitAngleMin: Float, yLimitAngleMax: Float, zLimitAngleMin: Float, zLimitAngleMax: Float, spring: PxSpring) =
    autoDelete(PxJointLimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, spring), PxJointLimitPyramid::destroy)
fun ScopedMemory.createPxJointLimitCone(yLimitAngle: AngleF, zLimitAngle: AngleF) =
    autoDelete(PxJointLimitCone(yLimitAngle.rad, zLimitAngle.rad), PxJointLimitCone::destroy)

fun PxUserControllerHitReportImpl.destroy() { }