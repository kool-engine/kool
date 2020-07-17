package de.fabmax.kool.util.animation

import de.fabmax.kool.math.*
import de.fabmax.kool.toString
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

abstract class AnimationKey<T: AnimationKey<T>>(val time: Float) {
    var interpolation = Interpolation.LINEAR

    abstract fun apply(time: Float, next: T?, node: AnimationNode)

    protected fun interpolationPos(pos: Float, nextTime: Float): Float {
        return if (time != nextTime) {
            val t = if (time < nextTime) { time } else { 0f }
            interpolation.getInterpolationPos((pos - t) / (nextTime - t))
        } else {
            0f
        }
    }

    override fun toString() = "${time.toString(2)} -> [$interpolation]"

    enum class Interpolation {
        LINEAR {
            override fun getInterpolationPos(linearPos: Float) = linearPos
        },
        STEP {
            override fun getInterpolationPos(linearPos: Float) = 0f
        },
        CUBICSPLINE {
            // actual cubic interpolation depends on more variables, but takes linear value as an input
            override fun getInterpolationPos(linearPos: Float) = linearPos
        };

        abstract fun getInterpolationPos(linearPos: Float): Float
    }
}

open class RotationKey(time: Float, val rotation: Vec4d) : AnimationKey<RotationKey>(time) {
    private val tmpRotation = MutableVec4d()

    private val qa = MutableVec4d()
    private val qb = MutableVec4d()
    private val qc = MutableVec4d()

    override fun apply(time: Float, next: RotationKey?, node: AnimationNode) {
        if (next == null) {
            tmpRotation.set(rotation)
        } else {
            slerp(rotation, next.rotation, interpolationPos(time, next.time).toDouble(), tmpRotation)
        }
        node.setRotation(tmpRotation)
    }

    override fun toString() = "${time.toString(2)} -> rotation: $rotation [$interpolation]"

    private fun slerp(quatA: Vec4d, quatB: Vec4d, f: Double, result: MutableVec4d): MutableVec4d {
        quatA.norm(qa)
        quatB.norm(qb)

        val t = f.clamp(0.0, 1.0)

        var dot = qa.dot(qb).clamp(-1.0, 1.0)
        if (dot < 0) {
            qa.scale(-1.0)
            dot = -dot
        }

        if (dot > 0.9999995) {
            qb.subtract(qa, result).scale(t).add(qa).norm()
        } else {
            val theta0 = acos(dot)
            val theta = theta0 * t

            qa.scale(-dot, qc).add(qb).norm()

            qa.scale(cos(theta))
            qc.scale(sin(theta))
            result.set(qa).add(qc)
        }
        return result
    }
}

class CubicRotationKey(time: Float, rotation: Vec4d, val startTan: Vec4d, val endTan: Vec4d) : RotationKey(time, rotation) {
    private val p0 = MutableVec4d()
    private val p1 = MutableVec4d()
    private val m0 = MutableVec4d()
    private val m1 = MutableVec4d()

    override fun apply(time: Float, next: RotationKey?, node: AnimationNode) {
        if (next == null) {
            node.setRotation(rotation)
        } else {
            val t = interpolationPos(time, next.time).toDouble()
            val t2 = t * t
            val t3 = t * t * t

            val f1 = 2*t3 - 3*t2 + 1
            val f2 = t3 - 2*t2 + t
            val f3 = -2*t3 + 3*t2
            val f4 = t3 - t2
            p0.set(rotation).scale(f1)
            m0.set(startTan).scale(f2)
            p1.set(next.rotation).scale(f3)
            m1.set(endTan).scale(f4)
            node.setRotation(p0.add(m0).add(p1).add(m1).norm())
        }
    }
}

open class TranslationKey(time: Float, val translation: Vec3d) : AnimationKey<TranslationKey>(time) {
    private val tmpTranslation = MutableVec3d()

    override fun apply(time: Float, next: TranslationKey?, node: AnimationNode) {
        if (next == null) {
            node.setTranslation(translation)
        } else {
            next.translation.subtract(translation, tmpTranslation).scale(interpolationPos(time, next.time).toDouble()).add(translation)
            node.setTranslation(tmpTranslation)
        }
    }

    override fun toString() = "${time.toString(2)} -> translation: $translation [$interpolation]"
}

class CubicTranslationKey(time: Float, translation: Vec3d, val startTan: Vec3d, val endTan: Vec3d) : TranslationKey(time, translation) {
    private val p0 = MutableVec3d()
    private val p1 = MutableVec3d()
    private val m0 = MutableVec3d()
    private val m1 = MutableVec3d()

    override fun apply(time: Float, next: TranslationKey?, node: AnimationNode) {
        if (next == null) {
            node.setTranslation(translation)
        } else {
            val t = interpolationPos(time, next.time).toDouble()
            val t2 = t * t
            val t3 = t * t * t

            val f1 = 2*t3 - 3*t2 + 1
            val f2 = t3 - 2*t2 + t
            val f3 = -2*t3 + 3*t2
            val f4 = t3 - t2
            p0.set(translation).scale(f1)
            m0.set(startTan).scale(f2)
            p1.set(next.translation).scale(f3)
            m1.set(endTan).scale(f4)
            node.setTranslation(p0.add(m0).add(p1).add(m1))
        }
    }
}

open class ScaleKey(time: Float, val scale: Vec3d) : AnimationKey<ScaleKey>(time) {
    private val tmpScale = MutableVec3d()

    override fun apply(time: Float, next: ScaleKey?, node: AnimationNode) {
        if (next == null) {
            node.setScale(scale)
        } else {
            next.scale.subtract(scale, tmpScale).scale(interpolationPos(time, next.time).toDouble()).add(scale)
            node.setScale(tmpScale)
        }
    }

    override fun toString() = "${time.toString(2)} -> scale: $scale [$interpolation]"
}

class CubicScaleKey(time: Float, scale: Vec3d, val startTan: Vec3d, val endTan: Vec3d) : ScaleKey(time, scale) {
    private val p0 = MutableVec3d()
    private val p1 = MutableVec3d()
    private val m0 = MutableVec3d()
    private val m1 = MutableVec3d()

    override fun apply(time: Float, next: ScaleKey?, node: AnimationNode) {
        if (next == null) {
            node.setScale(scale)
        } else {
            val t = interpolationPos(time, next.time).toDouble()
            val t2 = t * t
            val t3 = t * t * t

            val f1 = 2*t3 - 3*t2 + 1
            val f2 = t3 - 2*t2 + t
            val f3 = -2*t3 + 3*t2
            val f4 = t3 - t2
            p0.set(scale).scale(f1)
            m0.set(startTan).scale(f2)
            p1.set(next.scale).scale(f3)
            m1.set(endTan).scale(f4)
            node.setScale(p0.add(m0).add(p1).add(m1))
        }
    }
}

open class WeightKey(time: Float, val weights: FloatArray) : AnimationKey<WeightKey>(time) {
    protected var tmpW = FloatArray(1)

    override fun apply(time: Float, next: WeightKey?, node: AnimationNode) {
        if (tmpW.size != weights.size) {
            tmpW = FloatArray(weights.size)
        }

        if (next == null) {
            for (i in weights.indices) {
                tmpW[i] = weights[i]
            }
        } else {
            for (i in weights.indices) {
                tmpW[i] = (next.weights[i] - weights[i]) * interpolationPos(time, next.time) + weights[i]
            }
        }
        node.setWeights(tmpW)
    }

    override fun toString() = "${time.toString(2)} -> weight: (${weights.joinToString(", ")}) [$interpolation]"
}

class CubicWeightKey(time: Float, weights: FloatArray, val startTan: FloatArray, val endTan: FloatArray) : WeightKey(time, weights) {
    override fun apply(time: Float, next: WeightKey?, node: AnimationNode) {
        if (tmpW.size != weights.size) {
            tmpW = FloatArray(weights.size)
        }

        if (next == null) {
            for (i in weights.indices) {
                tmpW[i] = weights[i]
            }
        } else {
            val t = interpolationPos(time, next.time)
            val t2 = t * t
            val t3 = t * t * t

            val f1 = 2*t3 - 3*t2 + 1
            val f2 = t3 - 2*t2 + t
            val f3 = -2*t3 + 3*t2
            val f4 = t3 - t2

            for (i in weights.indices) {
                val p0 = weights[i] * f1
                val m0 = startTan[i] * f2
                val p1 = next.weights[i] * f3
                val m1 = endTan[i] * f4

                tmpW[i] = p0 + m0 + p1 + m1
            }
        }
        node.setWeights(tmpW)
    }
}
