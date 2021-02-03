@file:Suppress("UnsafeCastFromDynamic", "FunctionName")

package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.util.BoundingBox
import physx.*

fun PxBounds3.toBoundingBox(result: BoundingBox): BoundingBox {
    val min = minimum
    val max = maximum
    return result.set(min.x, min.y, min.z, max.x, max.y, max.z)
}
fun BoundingBox.toPxBounds3(result: PxBounds3): PxBounds3 {
    val v = PxVec3()
    result.minimum = min.toPxVec3(v)
    result.maximum = max.toPxVec3(v)
    PhysxJsLoader.destroy(v)
    return result
}

fun PxTransform() = PxTransform(PxIDENTITYEnum.PxIdentity)
fun PxTransform.toMat4f(result: Mat4f): Mat4f {
    result.setRotate(q.toVec4f())
    result[0, 3] = p.x
    result[1, 3] = p.y
    result[2, 3] = p.z
    return result
}
fun PxTransform.set(mat: Mat4f): PxTransform {
    mat.getRotation(MutableVec4f()).toPxQuat(q)
    p.x = mat[0, 3]
    p.y = mat[1, 3]
    p.z = mat[2, 3]
    return this
}
fun PxTransform.setIdentity(): PxTransform {
    q.setIdentity()
    p.set(Vec3f.ZERO)
    return this
}
fun Mat4f.toPxTransform(t: PxTransform) = t.set(this)

fun PxQuat.toVec4f(result: MutableVec4f = MutableVec4f()) = result.set(x, y, z, w)
fun PxQuat.set(v: Vec4f): PxQuat { x = v.x; y = v.y; z = v.z; w = v.w; return this }
fun PxQuat.setIdentity(): PxQuat { x = 0f; y = 0f; z = 0f; w = 1f; return this }
fun Vec4f.toPxQuat(result: PxQuat) = result.set(this)

fun PxVec3.toVec3f(result: MutableVec3f = MutableVec3f()) = result.set(x, y, z)
fun PxVec3.set(v: Vec3f): PxVec3 { x = v.x; y = v.y; z = v.z; return this }
fun Vec3f.toPxVec3(result: PxVec3) = result.set(this)

@Suppress("FunctionName")
fun List<Vec3f>.toVector_PxVec3(): Vector_PxVec3 {
    val vector = Vector_PxVec3(size)
    forEachIndexed { i, v -> v.toPxVec3(vector.at(i)) }
    return vector
}

fun PxFilterData(w0: Int = 0, w1: Int = 0, w2: Int = 0): PxFilterData = PxFilterData(w0, w1, w2, 0)
fun PxFilterData(filterData: FilterData): PxFilterData =
    PxFilterData(filterData.data[0], filterData.data[1], filterData.data[2], filterData.data[3])
fun FilterData.toPxFilterData(target: PxFilterData) {
    target.word0 = data[0]
    target.word1 = data[1]
    target.word2 = data[2]
    target.word3 = data[3]
}

fun PxVehicleDrivableSurfaceToTireFrictionPairs_allocate(maxNbTireTypes: Int, maxNbSurfaceTypes: Int): PxVehicleDrivableSurfaceToTireFrictionPairs =
    PhysxJsLoader.physxJs.PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.allocate(maxNbTireTypes, maxNbSurfaceTypes)

fun PxVehicleDrive4W_allocate(nbWheels: Int): PxVehicleDrive4W = PhysxJsLoader.physxJs.PxVehicleDrive4W.prototype.allocate(nbWheels)

fun PxVehicleWheelsSimData_allocate(nbWheels: Int): PxVehicleWheelsSimData = PhysxJsLoader.physxJs.PxVehicleWheelsSimData.prototype.allocate(nbWheels)