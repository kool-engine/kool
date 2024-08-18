package de.fabmax.kool.math

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
