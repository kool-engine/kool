package de.fabmax.kool.scene.animation

import de.fabmax.kool.math.*
import de.fabmax.kool.scene.MatrixTransformF
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.TreeMap
import de.fabmax.kool.util.logE
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

    fun setTranslation(translation: Vec3f) { }
    fun setRotation(rotation: QuatF) { }
    fun setScale(scale: Vec3f) { }

    fun setWeights(weights: FloatArray) { }
}

class AnimatedTransformGroup(val target: Node): AnimationNode {
    override val name: String
        get() = target.name

    private val initTranslation = MutableVec3f()
    private val initRotation = MutableQuatF()
    private val initScale = MutableVec3f(Vec3f.ONES)

    private val animTranslation = MutableVec3f()
    private val animRotation = MutableQuatF()
    private val animScale = MutableVec3f()

    private val quatRotMat = MutableMat4f()
    private val weightedTransformMat = MutableMat4f()

    init {
        val vec4 = MutableVec4f()
        target.transform.matrixF.getColumn(3, vec4)
        initTranslation.set(vec4.x, vec4.y, vec4.z)
        target.transform.matrixF.getRotation(initRotation)
        val sx = target.transform.matrixF.getColumn(0, vec4).length()
        val sy = target.transform.matrixF.getColumn(1, vec4).length()
        val sz = target.transform.matrixF.getColumn(2, vec4).length()
        initScale.set(sx, sy, sz)
    }

    override fun initTransform() {
        animTranslation.set(initTranslation)
        animRotation.set(initRotation)
        animScale.set(initScale)
    }

    override fun applyTransform() {
        target.transform.setCompositionOf(animTranslation, animRotation, animScale)
    }

    override fun applyTransformWeighted(weight: Float, firstWeightedTransform: Boolean) {
        weightedTransformMat.setIdentity()
        weightedTransformMat.translate(animTranslation)
        weightedTransformMat.rotate(animRotation)
        weightedTransformMat.scale(animScale)

        var t = target.transform as? MatrixTransformF
        if (t == null) {
            t = MatrixTransformF()
            target.transform = t
        }

        val wm = if (firstWeightedTransform) 0f else 1f

        t.matrixF.m00 = t.matrixF.m00 * wm + weightedTransformMat.m00 * weight
        t.matrixF.m01 = t.matrixF.m01 * wm + weightedTransformMat.m01 * weight
        t.matrixF.m02 = t.matrixF.m02 * wm + weightedTransformMat.m02 * weight
        t.matrixF.m03 = t.matrixF.m03 * wm + weightedTransformMat.m03 * weight

        t.matrixF.m10 = t.matrixF.m10 * wm + weightedTransformMat.m10 * weight
        t.matrixF.m11 = t.matrixF.m11 * wm + weightedTransformMat.m11 * weight
        t.matrixF.m12 = t.matrixF.m12 * wm + weightedTransformMat.m12 * weight
        t.matrixF.m13 = t.matrixF.m13 * wm + weightedTransformMat.m13 * weight

        t.matrixF.m20 = t.matrixF.m20 * wm + weightedTransformMat.m20 * weight
        t.matrixF.m21 = t.matrixF.m21 * wm + weightedTransformMat.m21 * weight
        t.matrixF.m22 = t.matrixF.m22 * wm + weightedTransformMat.m22 * weight
        t.matrixF.m23 = t.matrixF.m23 * wm + weightedTransformMat.m23 * weight

        t.matrixF.m30 = t.matrixF.m30 * wm + weightedTransformMat.m30 * weight
        t.matrixF.m31 = t.matrixF.m31 * wm + weightedTransformMat.m31 * weight
        t.matrixF.m32 = t.matrixF.m32 * wm + weightedTransformMat.m32 * weight
        t.matrixF.m33 = t.matrixF.m33 * wm + weightedTransformMat.m33 * weight

        target.transform.markDirty()
    }

    override fun setTranslation(translation: Vec3f) {
        animTranslation.set(translation)
    }

    override fun setRotation(rotation: QuatF) {
        animRotation.set(rotation)
    }

    override fun setScale(scale: Vec3f) {
        animScale.set(scale)
    }
}

class MorphAnimatedMesh(val target: Mesh): AnimationNode {
    override val name: String
        get() = target.name

    private var weights = FloatArray(1)

    init {
        if (target.morphWeights == null) {
            logE { "Morph animation target mesh has no morph weight attribute" }
        }
    }

    override fun applyTransform() {
        target.morphWeights?.let { w ->
            if (w.size < weights.size) {
                logE { "Morph animation target mesh has too small weight array size (${w.size} != ${weights.size})" }
            }

            for (i in 0 until min(w.size, weights.size)) {
                w[i] = weights[i]
            }
        }
    }

    override fun applyTransformWeighted(weight: Float, firstWeightedTransform: Boolean) {
        target.morphWeights?.let { w ->
            if (w.size < weights.size) {
                logE { "Morph animation target mesh has too small weight array size (${w.size} != ${weights.size})" }
            }
            val n = min(w.size, weights.size)

            for (i in weights.indices) {
                if (firstWeightedTransform) {
                    for (j in 0 until n) {
                        w[j] = weights[j] * weight
                    }
                } else {
                    for (j in 0 until n) {
                        w[j] += weights[j] * weight
                    }
                }
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