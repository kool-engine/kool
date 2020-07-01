package de.fabmax.kool.scene.animation

import de.fabmax.kool.math.*
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.TreeMap

class Animation(val name: String?) {
    val channels = mutableListOf<AnimationChannel>()

    var speed = 1f
    var duration = 1f
        private set
    private val animationNodes = mutableListOf<AnimationNode>()

    fun prepareAnimation() {
        duration = channels.map { it.duration }.max() ?: 0f
        animationNodes += channels.map { it.animationNode }.distinct()
    }

    fun apply(time: Double) {
        val t = ((time * speed) % (duration)).toFloat()
        for (i in animationNodes.indices) {
            animationNodes[i].initTransform()
        }
        for (i in channels.indices) {
            channels[i].apply(t)
        }
        for (i in animationNodes.indices) {
            animationNodes[i].applyTransform()
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
        val key = keys.floorValue(time) ?: keys.lastValue()
        key.apply(time, keys.higherValue(time), animationNode)
    }
}

class RotationAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel(name, animationNode) {
    val keys = TreeMap<Float, RotationKey>()
    override val duration: Float
        get() = keys.lastKey()

    override fun apply(time: Float) {
        val key = keys.floorValue(time) ?: keys.lastValue()
        key.apply(time, keys.higherValue(time), animationNode)
    }
}

class ScaleAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel(name, animationNode) {
    val keys = TreeMap<Float, ScaleKey>()
    override val duration: Float
        get() = keys.lastKey()

    override fun apply(time: Float) {
        val key = keys.floorValue(time) ?: keys.lastValue()
        key.apply(time, keys.higherValue(time), animationNode)
    }
}

interface AnimationNode {
    fun initTransform()
    fun applyTransform()

    fun setTranslation(translation: Vec3d)
    fun setRotation(rotation: Vec4d)
    fun setScale(scale: Vec3d)
}

class AnimatedTransformGroup(val target: TransformGroup): AnimationNode {
    private val initTranslation = MutableVec3d()
    private val initRotation = MutableVec4d()
    private val initScale = MutableVec3d(1.0, 1.0, 1.0)

    private val animTranslation = MutableVec3d()
    private val animRotation = MutableVec4d()
    private val animScale = MutableVec3d()

    private val quatRotMat = Mat4d()

    init {
        val vec4 = MutableVec4d()
        target.transform.getCol(3, vec4)
        initTranslation.set(vec4.x, vec4.y, vec4.z)
        target.transform.getRotation(initRotation)
        val sx = target.transform.getCol(0, vec4).length()
        val sy = target.transform.getCol(0, vec4).length()
        val sz = target.transform.getCol(0, vec4).length()
        initScale.set(sx, sy, sz)
    }

    override fun initTransform() {
        animTranslation.set(initTranslation)
        animRotation.set(initRotation)
        animScale.set(initScale)
    }

    override fun applyTransform() {
        target.setIdentity()
        target.translate(animTranslation)
        target.mul(quatRotMat.setRotate(animRotation))
        target.scale(animScale.x, animScale.y, animScale.z)

        //println("apply transform: ${target.name}")
        //target.transform.dump()
    }

    override fun setTranslation(translation: Vec3d) {
        //println("translate: $translation")
        animTranslation.set(translation)
    }

    override fun setRotation(rotation: Vec4d) {
        //println("rotate: $rotation")
        animRotation.set(rotation)
    }

    override fun setScale(scale: Vec3d) {
        //println("scale: $scale")
        animScale.set(scale)
    }

}
