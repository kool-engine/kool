package de.fabmax.kool.physics2d

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import kotlin.math.sqrt

interface Rotation {
    val sin: Float
    val cos: Float

    fun toAngle(): AngleF = Box2dMath.rotationToAngle(this)

    fun mix(that: Rotation, weight: Float, result: MutableRotation): MutableRotation = result.set(
        (that.sin - sin) * weight + sin,
        (that.cos - cos) * weight + cos
    ).normalize()

    companion object {
        val IDENTITY = Rotation(0f, 1f)
    }
}

fun Rotation(sin: Float, cos: Float): Rotation = MutableRotation(sin, cos)

fun AngleF.toRotation(): Rotation {
    val rot = MutableRotation()
    Box2dMath.angleToRotation(this, rot)
    return rot
}

fun MutableRotation() = MutableRotation(Rotation.IDENTITY)
fun MutableRotation(other: Rotation) = MutableRotation(other.sin, other.cos)

data class MutableRotation(override var sin: Float, override var cos: Float) : Rotation {
    fun set(sin: Float, cos: Float): MutableRotation {
        this.sin = sin
        this.cos = cos
        return this
    }

    fun set(other: Rotation): MutableRotation {
        sin = other.sin
        cos = other.cos
        return this
    }

    fun set(angle: AngleF): MutableRotation {
        Box2dMath.angleToRotation(angle, this)
        return this
    }

    fun normalize(): MutableRotation {
        val m = sqrt(sin * sin + cos * cos)
        val i = if (m > 0f) 1f / m else 0f
        sin *= i
        cos *= i
        return this
    }
}

interface Pose2f {
    val position: Vec2f
    val rotation: Rotation
}

fun Pose2f(position: Vec2f, rotation: Rotation): Pose2f {
    return MutablePose2f(MutableVec2f(position), MutableRotation(rotation))
}

data class MutablePose2f(
    override val position: MutableVec2f = MutableVec2f(),
    override val rotation: MutableRotation = MutableRotation()
) : Pose2f {
    fun set(other: Pose2f): MutablePose2f {
        position.set(other.position)
        rotation.set(other.rotation)
        return this
    }

    fun set(position: Vec2f, rotation: Rotation): MutablePose2f {
        this.position.set(position)
        this.rotation.set(rotation)
        return this
    }
}

internal expect object Box2dMath {
    fun angleToRotation(angle: AngleF, result: MutableRotation)
    fun rotationToAngle(rotation: Rotation): AngleF
}