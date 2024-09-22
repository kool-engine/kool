package de.fabmax.kool.math

// <template> Changes made within the template section will also affect the other type variants of this class

fun PoseF.toMutablePoseF(result: MutablePoseF = MutablePoseF()): MutablePoseF = result.set(this)

fun Mat4f.getPose(result: MutablePoseF = MutablePoseF()): MutablePoseF {
    decompose(result.position, result.rotation)
    return result
}

fun PoseF(other: PoseF): PoseF = PoseF(other.position, other.rotation)

open class PoseF(position: Vec3f = Vec3f.ZERO, rotation: QuatF = QuatF.IDENTITY) {
    protected val _pos = MutableVec3f(position)
    protected val _rot = MutableQuatF(rotation)

    open val position: Vec3f get() = _pos
    open val rotation: QuatF get() = _rot

    operator fun component1(): Vec3f = position
    operator fun component2(): QuatF = rotation
}

fun PoseF.toMat4f(result: MutableMat4f = MutableMat4f()): MutableMat4f {
    return result.setIdentity().translate(position).rotate(rotation)
}

open class MutablePoseF(position: Vec3f = Vec3f.ZERO, rotation: QuatF = QuatF.IDENTITY) : PoseF(position, rotation) {
    override val position: MutableVec3f
        get() = _pos

    override val rotation: MutableQuatF
        get() = _rot

    fun set(other: PoseF): MutablePoseF {
        _pos.set(other.position)
        _rot.set(other.rotation)
        return this
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


fun PoseD.toMutablePoseF(result: MutablePoseD = MutablePoseD()): MutablePoseD = result.set(this)

fun Mat4d.getPose(result: MutablePoseD = MutablePoseD()): MutablePoseD {
    decompose(result.position, result.rotation)
    return result
}

fun PoseD(other: PoseD): PoseD = PoseD(other.position, other.rotation)

open class PoseD(position: Vec3d = Vec3d.ZERO, rotation: QuatD = QuatD.IDENTITY) {
    protected val _pos = MutableVec3d(position)
    protected val _rot = MutableQuatD(rotation)

    open val position: Vec3d get() = _pos
    open val rotation: QuatD get() = _rot

    operator fun component1(): Vec3d = position
    operator fun component2(): QuatD = rotation
}

fun PoseD.toMat4f(result: MutableMat4d = MutableMat4d()): MutableMat4d {
    return result.setIdentity().translate(position).rotate(rotation)
}

open class MutablePoseD(position: Vec3d = Vec3d.ZERO, rotation: QuatD = QuatD.IDENTITY) : PoseD(position, rotation) {
    override val position: MutableVec3d
        get() = _pos

    override val rotation: MutableQuatD
        get() = _rot

    fun set(other: PoseD): MutablePoseD {
        _pos.set(other.position)
        _rot.set(other.rotation)
        return this
    }
}
