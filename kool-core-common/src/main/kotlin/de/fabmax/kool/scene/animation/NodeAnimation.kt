package de.fabmax.kool.scene.animation

import de.fabmax.kool.math.*
import kotlin.math.max

interface AnimatedNode {
    fun clearTransform()
    fun addTransform(transform: Mat4f, weight: Float)
}

class NodeAnimation(val name: String, val node: AnimatedNode) {
    val rotationKeys = mutableListOf<RotationKey>()
    val positionKeys = mutableListOf<PositionKey>()
    val scalingKeys = mutableListOf<ScalingKey>()

    private val tmpTransform = Mat4f()
    private val tmpMat = Mat4f()

    fun apply(time: Float, weight: Float, clearTransform: Boolean) {
        tmpTransform.setIdentity()
        mul(positionKeys, time)
        mul(rotationKeys, time)
        mul(scalingKeys, time)

        if (clearTransform) {
            node.clearTransform()
        }
        node.addTransform(tmpTransform, weight)
    }

    private fun <T, U : AnimationKey<T>> mul(keys: List<U>, time: Float) {
        if (!keys.isEmpty()) {
            val idx = findIndex(time, keys)
            val next = if (idx + 1 < keys.size) { keys[idx + 1] } else { null }
            tmpTransform.mul(keys[idx].mixAndSet(time, next, tmpMat))
        }
    }

    private fun findIndex(time: Float, keys: List<AnimationKey<*>>): Int {
        for (i in keys.indices) {
            if (keys[i].time > time) {
                return max(0, i-1)
            }
        }
        return keys.size - 1
    }
}

abstract class AnimationKey<T>(val time: Float, val value: T) {
    abstract fun mixAndSet(time: Float, next: AnimationKey<T>?, result: Mat4f): Mat4f

    fun weight(pos: Float, nextTime: Float) = (pos - time) / (nextTime - time)
}

class RotationKey(time: Float, rotation: Vec4f) : AnimationKey<Vec4f>(time, rotation) {
    private val tmpRotation = MutableVec4f()

    override fun mixAndSet(time: Float, next: AnimationKey<Vec4f>?, result: Mat4f): Mat4f {
        if (next == null) {
            result.setRotate(value)
        } else {
            slerp(value, next.value, weight(time, next.time), tmpRotation)
            result.setRotate(tmpRotation)
        }
        return result
    }
}

class PositionKey(time: Float, position: Vec3f) : AnimationKey<Vec3f>(time, position) {
    private val tmpPosition = MutableVec3f()

    override fun mixAndSet(time: Float, next: AnimationKey<Vec3f>?, result: Mat4f): Mat4f {
        if (next == null) {
            result.setIdentity().translate(value)
        } else {
            next.value.subtract(value, tmpPosition).scale(weight(time, next.time)).add(value)
            result.setIdentity().translate(tmpPosition)
        }
        return result
    }
}

class ScalingKey(time: Float, scaling: Vec3f) : AnimationKey<Vec3f>(time, scaling) {
    private val tmpScaling = MutableVec3f()

    override fun mixAndSet(time: Float, next: AnimationKey<Vec3f>?, result: Mat4f): Mat4f {
        if (next == null) {
            result.setIdentity().scale(value)
        } else {
            next.value.subtract(value, tmpScaling).scale(weight(time, next.time)).add(value)
            result.setIdentity().scale(tmpScaling)
        }
        return result
    }
}
