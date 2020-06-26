package de.fabmax.kool.scene.animation

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec4d
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.TreeMap

class Animation(val name: String?) {
    val channels = mutableListOf<AnimationChannel>()

    var duration = 1f
        private set
    private val animationNodes = mutableSetOf<AnimationNode>()

    fun prepareAnimation() {
        duration = channels.map { it.duration }.max() ?: 0f
        channels.forEach { animationNodes += it.animationNode }
    }

    fun apply(time: Double) {
        val t = (time % duration).toFloat()
        animationNodes.forEach { it.clearTransform() }
        for (i in channels.indices) {
            channels[i].apply(t)
        }
    }
}

abstract class AnimationChannel(val name: String?, val animationNode: AnimationNode) {
    abstract val duration: Float

    abstract fun apply(time: Float)
}

class TranslationAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel(name, animationNode) {
    val keys = TreeMap<Float, TranslationKey>()
    override val duration: Float
        get() = keys.lastKey()

    override fun apply(time: Float) {
        keys.floorValue(time)?.apply(time, keys.higherValue(time), animationNode)
    }
}

class RotationAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel(name, animationNode) {
    val keys = TreeMap<Float, RotationKey>()
    override val duration: Float
        get() = keys.lastKey()

    override fun apply(time: Float) {
        keys.floorValue(time)?.apply(time, keys.higherValue(time), animationNode)
    }
}

class ScaleAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel(name, animationNode) {
    val keys = TreeMap<Float, ScaleKey>()
    override val duration: Float
        get() = keys.lastKey()

    override fun apply(time: Float) {
        keys.floorValue(time)?.apply(time, keys.higherValue(time), animationNode)
    }
}

interface AnimationNode {
    fun clearTransform()

    fun translate(translation: Vec3d)
    fun rotate(rotation: Vec4d)
    fun scale(scale: Vec3d)
}

class AnimatedTransformGroup(val target: TransformGroup): AnimationNode {
    private val initialTransform = Mat4d()
    private val rotMat = Mat4d()

    init {
        initialTransform.set(target.transform)
    }

    override fun clearTransform() {
        target.set(initialTransform)
    }

    override fun translate(translation: Vec3d) {
        target.translate(translation.x, translation.y, translation.z)
    }

    override fun rotate(rotation: Vec4d) {
        target.mul(rotMat.setRotate(rotation))
    }

    override fun scale(scale: Vec3d) {
        target.scale(scale.x, scale.y, scale.z)
    }

}
