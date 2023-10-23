package de.fabmax.kool.scene.animation

import de.fabmax.kool.math.*
import de.fabmax.kool.scene.MatrixTransform
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransform
import de.fabmax.kool.util.TreeMap
import kotlin.math.min

class Animation(val name: String?) {
    val channels = mutableListOf<AnimationChannel<*>>()

    var weight = 1f
    var speed = 1f
    var duration = 1f
        private set

    var progress = 0f

    private val animationNodes = mutableListOf<AnimationNode>()

    fun prepareAnimation() {
        duration = channels.map { it.lastKeyTime }.maxOrNull() ?: 0f
        channels.forEach { it.duration = duration }
        animationNodes += channels.map { it.animationNode }.distinct()
    }

    fun apply(deltaT: Float, firstWeightedTransform: Boolean = true) {
        progress = (progress + duration + deltaT * speed) % duration

        for (i in animationNodes.indices) {
            animationNodes[i].initTransform()
        }
        for (i in channels.indices) {
            channels[i].apply(progress)
        }
        if (weight == 1f) {
            for (i in animationNodes.indices) {
                animationNodes[i].applyTransform()
            }
        } else {
            for (i in animationNodes.indices) {
                animationNodes[i].applyTransformWeighted(weight, firstWeightedTransform)
            }
        }
    }

    fun printChannels() {
        println("$name channels:")
        channels.forEach { ch ->
            println("  ${ch.name} [node: ${ch.animationNode.name}]")
            ch.printKeys("    ")
        }
    }
}

abstract class AnimationChannel<T: AnimationKey<T>>(val name: String?, val animationNode: AnimationNode) {
    val keys = TreeMap<Float, T>()
    val lastKeyTime: Float
        get() = keys.lastKey()
    var duration = 0f

    fun apply(time: Float) {
        var key = keys.floorValue(time)
        if (key == null) {
            key = if (isFuzzyEqual(lastKeyTime, duration)) keys.lastValue() else keys.firstValue()
        }
        key.apply(time, keys.higherValue(time), animationNode)
    }

    fun printKeys(indent: String = "") {
        val animKeys = keys.values.toList()
        for (i in 0 until min(5, animKeys.size)) {
            println("$indent${animKeys[i]}")
        }
        if (animKeys.size > 5) {
            println("$indent  ...${animKeys.size-5} more")
        }
    }
}

class TranslationAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel<TranslationKey>(name, animationNode)

class RotationAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel<RotationKey>(name, animationNode)

class ScaleAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel<ScaleKey>(name, animationNode)

class WeightAnimationChannel(name: String?, animationNode: AnimationNode): AnimationChannel<WeightKey>(name, animationNode)

interface AnimationNode {
    val name: String

    fun initTransform() { }
    fun applyTransform()
    fun applyTransformWeighted(weight: Float, firstWeightedTransform: Boolean)

    fun setTranslation(translation: Vec3d) { }
    fun setRotation(rotation: Vec4d) { }
    fun setScale(scale: Vec3d) { }

    fun setWeights(weights: FloatArray) { }
}

class AnimatedTransformGroup(val target: Node): AnimationNode {
    override val name: String
        get() = target.name

    private val initTranslation = MutableVec3d()
    private val initRotation = MutableQuatD()
    private val initScale = MutableVec3d(1.0, 1.0, 1.0)

    private val animTranslation = MutableVec3d()
    private val animRotation = MutableQuatD()
    private val animScale = MutableVec3d()

    private val quatRotMat = Mat4d()
    private val weightedTransformMat = Mat4d()

    init {
        val vec4 = MutableVec4d()
        target.transform.matrix.getCol(3, vec4)
        initTranslation.set(vec4.x, vec4.y, vec4.z)
        target.transform.matrix.getRotation(initRotation)
        val sx = target.transform.matrix.getCol(0, vec4).length()
        val sy = target.transform.matrix.getCol(1, vec4).length()
        val sz = target.transform.matrix.getCol(2, vec4).length()
        initScale.set(sx, sy, sz)
    }

    override fun initTransform() {
        animTranslation.set(initTranslation)
        animRotation.set(initRotation)
        animScale.set(initScale)
    }

    override fun applyTransform() {
        val t = target.transform
        if (t is TrsTransform) {
            t.setPosition(animTranslation)
                .setRotation(animRotation)
                .setScale(animScale)
        } else {
            target.transform.setIdentity()
            target.transform.translate(animTranslation)
            target.transform.matrix.mul(quatRotMat.setRotate(animRotation))
            target.transform.scale(animScale.x, animScale.y, animScale.z)
        }
    }

    override fun applyTransformWeighted(weight: Float, firstWeightedTransform: Boolean) {
        weightedTransformMat.setIdentity()
        weightedTransformMat.translate(animTranslation)
        weightedTransformMat.mul(quatRotMat.setRotate(animRotation))
        weightedTransformMat.scale(animScale.x, animScale.y, animScale.z)

        var t = target.transform as? MatrixTransform
        if (t == null) {
            t = MatrixTransform()
            target.transform = t
        }

        if (firstWeightedTransform) {
            for (i in 0..15) {
                t.matrix.array[i] = weightedTransformMat.array[i] * weight
            }
        } else {
            for (i in 0..15) {
                t.matrix.array[i] += weightedTransformMat.array[i] * weight
            }
        }
        target.transform.markDirty()
    }

    override fun setTranslation(translation: Vec3d) {
        animTranslation.set(translation)
    }

    override fun setRotation(rotation: Vec4d) {
        animRotation.set(rotation)
    }

    override fun setScale(scale: Vec3d) {
        animScale.set(scale)
    }
}

class MorphAnimatedMesh(val target: Mesh): AnimationNode {
    override val name: String
        get() = target.name

    private var weights = FloatArray(1)

    override fun applyTransform() {
        target.morphWeights = weights
    }

    override fun applyTransformWeighted(weight: Float, firstWeightedTransform: Boolean) {
        var targetW = target.morphWeights
        if (targetW == null || targetW.size != weights.size) {
            targetW = FloatArray(weights.size)
            target.morphWeights = targetW
        }
        for (i in weights.indices) {
            if (firstWeightedTransform) {
                targetW[i] = weights[i] * weight
            } else {
                targetW[i] += weights[i] * weight
            }
        }
    }

    override fun setWeights(weights: FloatArray) {
        if (this.weights.size != weights.size) {
            this.weights = FloatArray(weights.size)
        }
        for (i in weights.indices) {
            this.weights[i] = weights[i]
        }
    }
}