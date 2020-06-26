package de.fabmax.kool.scene.animation

import de.fabmax.kool.math.*

abstract class AnimationKey<T>(val time: Float, val value: T) {
    var interpolation = Interpolation.LINEAR

    abstract fun apply(time: Float, next: AnimationKey<T>?, node: AnimationNode)

    fun interpolationPos(pos: Float, nextTime: Float) = interpolation.getInterpolationPos((pos - time) / (nextTime - time))

    enum class Interpolation {
        LINEAR {
            override fun getInterpolationPos(linearPos: Float) = linearPos
        },
        STEP {
            override fun getInterpolationPos(linearPos: Float) = 0f
        },
        CUBICSPLINE {
            override fun getInterpolationPos(linearPos: Float): Float {
                TODO("Not yet implemented")
            }
        };

        abstract fun getInterpolationPos(linearPos: Float): Float
    }
}

class RotationKey(time: Float, rotation: Vec4d) : AnimationKey<Vec4d>(time, rotation) {
    private val tmpRotation = MutableVec4d()

    override fun apply(time: Float, next: AnimationKey<Vec4d>?, node: AnimationNode) {
        if (next == null) {
            tmpRotation.set(value)
        } else {
            slerp(value, next.value, interpolationPos(time, next.time).toDouble(), tmpRotation)
        }
        node.rotate(tmpRotation)
    }
}

class TranslationKey(time: Float, position: Vec3d) : AnimationKey<Vec3d>(time, position) {
    private val tmpPosition = MutableVec3d()

    override fun apply(time: Float, next: AnimationKey<Vec3d>?, node: AnimationNode) {
        if (next == null) {
            tmpPosition.set(value)
        } else {
            next.value.subtract(value, tmpPosition).scale(interpolationPos(time, next.time).toDouble()).add(value)
        }
        node.translate(tmpPosition)
    }
}

class ScaleKey(time: Float, scaling: Vec3d) : AnimationKey<Vec3d>(time, scaling) {
    private val tmpScale = MutableVec3d()

    override fun apply(time: Float, next: AnimationKey<Vec3d>?, node: AnimationNode) {
        if (next == null) {
            tmpScale.set(value)
        } else {
            next.value.subtract(value, tmpScale).scale(interpolationPos(time, next.time).toDouble()).add(value)
        }
        node.scale(tmpScale)
    }
}
