package de.fabmax.kool.math

import kotlin.math.*

/**
 * Collection of various easing functions. All of them map an input value in the range [0..1] to an output value
 * in the range [0..1]. These are particularly useful for animations.
 *
 * Implementation based on https://easings.net/
 */
object Easing {

    val linear = Easing { it }

    val easeInSine = Easing { 1f - cos((it * PI.toFloat()) / 2f) }
    val easeOutSine = Easing { sin((it * PI.toFloat()) / 2f) }
    val easeInOutSine = Easing { -(cos(PI.toFloat() * it) - 1f) / 2f }

    val easeInQuad = Easing { it * it }
    val easeOutQuad = Easing { 1f - (1f - it) * (1f - it) }
    val easeInOutQuad = Easing {
        if (it < 0.5f) 2f * it * it else 1f - (-2f * it + 2f).pow(2) / 2f
    }

    val easeInCubic = Easing { it * it * it }
    val easeOutCubic = Easing { 1f - (1f - it).pow(3) }
    val easeInOutCubic = Easing {
        if (it < 0.5f) 4f * it * it * it else 1f - (-2f * it + 2f).pow(3) / 2f
    }

    val easeInQuart = Easing { it * it * it * it }
    val easeOutQuart = Easing { 1f - (1f - it).pow(4) }
    val easeInOutQuart = Easing {
        if (it < 0.5f) 8f * it * it * it * it else 1f - (-2f * it + 2f).pow(4) / 2f
    }

    val easeInQuint = Easing { it * it * it * it * it }
    val easeOutQuint = Easing { 1f - (1f - it).pow(5) }
    val easeInOutQuint = Easing {
        if (it < 0.5f) 16f * it * it * it * it * it else 1f - (-2f * it + 2f).pow(5) / 2f
    }

    val easeInExpo = Easing { if (it == 0f) 0f else 2f.pow(10f * it - 10f) }
    val easeOutExpo = Easing { if (it == 1f) 1f else 1f - 2f.pow(-10f * it) }
    val easeInOutExpo = Easing {
        when {
            it == 0f -> 0f
            it == 1f -> 1f
            it < 0.5f -> 2f.pow(20f * it - 10f) / 2f
            else -> (2f - 2f.pow(-20f * it + 10f)) / 2f
        }
    }

    val easeInCirc = Easing { 1f - sqrt(1f - it.pow(2)) }
    val easeOutCirc = Easing { sqrt(1f - (it - 1f).pow(2)) }
    val easeInOutCirc = Easing {
        if (it < 0.5f) {
            (1f - sqrt(1f - (2f * it).pow(2))) / 2f
        } else {
            (sqrt(1f - (-2f * it + 2f).pow(2)) + 1f) / 2f
        }
    }

    private const val c1 = 1.70158f
    private const val c2 = c1 * 1.525f
    private const val c3 = c1 + 1f

    val easeInBack = Easing { c3 * it * it * it - c1 * it * it }
    val easeOutBack = Easing {
        val x = it - 1f
        1f + c3 * x.pow(3) + c1 * x.pow(2)
    }
    val easeInOutBack = Easing {
        if (it < 0.5f) {
            val x = 2f * it
            (x * x * ((c2 + 1f) * x - c2)) / 2f
        } else {
            val x = 2f * it - 2f
            (x * x * ((c2 + 1f) * x + c2) + 2f) / 2f
        }
    }

    private const val c4 = (2f * PI.toFloat()) / 3f
    private const val c5 = (2f * PI.toFloat()) / 4.5f

    val easeInElastic = Easing {
        when (it) {
            0f -> 0f
            1f -> 1f
            else -> -(2f.pow(10f * it - 10f)) * sin((it * 10f - 10.75f) * c4)
        }
    }
    val easeOutElastic = Easing {
        when (it) {
            0f -> 0f
            1f -> 1f
            else -> 2f.pow(-10f * it) * sin((it * 10f - 0.75f) * c4) + 1f
        }
    }
    val easeInOutElastic = Easing {
        when {
            it == 0f -> 0f
            it == 1f -> 1f
            it < 0.5f -> -(2f.pow(20f * it - 10f) * sin((20f * it - 11.125f) * c5)) / 2f
            else -> (2f.pow(-20f * it + 10f) * sin((20f * it - 11.125f) * c5)) / 2f + 1f
        }
    }

    private const val n1 = 7.5625f
    private const val d1 = 2.75f

    val easeOutBounce = Easing {
        var x = it
        if (x < 1f / d1) {
            n1 * x * x
        } else if (x < 2f / d1) {
            x -= 1.5f / d1
            n1 * x * x + 0.75f
        } else if (x < 2.5f / d1) {
            x -= 2.25f / d1
            n1 * x * x + 0.9375f
        } else {
            x -= 2.625f / d1
            n1 * x * x + 0.984375f
        }
    }
    val easeInBounce = Easing { 1f - easeOutBounce.eased(1f - it) }
    val easeInOutBounce = Easing {
        if (it < 0.5f) {
            (1f - easeOutBounce.eased(1f - 2f * it)) / 2f
        } else {
            (1f + easeOutBounce.eased(2f * it - 1f)) / 2f
        }
    }

    /**
     * Starts slow, ends slow, fast in the middle: `x -> smoothStep(0.0, 1.0, x)`
     */
    val smooth = Easing { smoothStep(0f, 1f, it) }

    @Deprecated("Use easeInQuad instead", ReplaceWith("easeInQuad"))
    val sqr = easeInQuad
    @Deprecated("Use easeOutQuad instead", ReplaceWith("easeOutQuad"))
    val sqrRev = easeOutQuad

    @Deprecated("Use easeInCubic instead", ReplaceWith("easeInCubic"))
    val cubic = easeInCubic
    @Deprecated("Use easeOutCubic instead", ReplaceWith("easeOutCubic"))
    val cubicRev = easeOutCubic

    @Deprecated("Use easeInQuart instead", ReplaceWith("easeInQuart"))
    val quad = easeInQuart
    @Deprecated("Use easeOutQuart instead", ReplaceWith("easeOutQuart"))
    val quadRev = easeOutQuart


    fun linear(x: Float) = linear.eased(x)
    fun smooth(x: Float) = smooth.eased(x)

    fun easeInSine(x: Float) = easeInSine.eased(x)
    fun easeOutSine(x: Float) = easeOutSine.eased(x)
    fun easeInOutSine(x: Float) = easeInOutSine.eased(x)

    fun easeInQuad(x: Float) = easeInQuad.eased(x)
    fun easeOutQuad(x: Float) = easeOutQuad.eased(x)
    fun easeInOutQuad(x: Float) = easeInOutQuad.eased(x)

    fun easeInCubic(x: Float) = easeInCubic.eased(x)
    fun easeOutCubic(x: Float) = easeOutCubic.eased(x)
    fun easeInOutCubic(x: Float) = easeInOutCubic.eased(x)

    fun easeInQuart(x: Float) = easeInQuart.eased(x)
    fun easeOutQuart(x: Float) = easeOutQuart.eased(x)
    fun easeInOutQuart(x: Float) = easeInOutQuart.eased(x)

    fun easeInQuint(x: Float) = easeInQuint.eased(x)
    fun easeOutQuint(x: Float) = easeOutQuint.eased(x)
    fun easeInOutQuint(x: Float) = easeInOutQuint.eased(x)

    fun easeInExpo(x: Float) = easeInExpo.eased(x)
    fun easeOutExpo(x: Float) = easeOutExpo.eased(x)
    fun easeInOutExpo(x: Float) = easeInOutExpo.eased(x)

    fun easeInCirc(x: Float) = easeInCirc.eased(x)
    fun easeOutCirc(x: Float) = easeOutCirc.eased(x)
    fun easeInOutCirc(x: Float) = easeInOutCirc.eased(x)

    fun easeInBack(x: Float) = easeInBack.eased(x)
    fun easeOutBack(x: Float) = easeOutBack.eased(x)
    fun easeInOutBack(x: Float) = easeInOutBack.eased(x)

    fun easeInElastic(x: Float) = easeInElastic.eased(x)
    fun easeOutElastic(x: Float) = easeOutElastic.eased(x)
    fun easeInOutElastic(x: Float) = easeInOutElastic.eased(x)

    fun easeInBounce(x: Float) = easeInBounce.eased(x)
    fun easeOutBounce(x: Float) = easeOutBounce.eased(x)
    fun easeInOutBounce(x: Float) = easeInOutBounce.eased(x)


    @Deprecated("Use easeInQuad instead", ReplaceWith("easeInQuad(x)"))
    fun sqr(x: Float) = easeInQuad(x)

    @Deprecated("Use easeOutQuad instead", ReplaceWith("easeOutQuad(x)"))
    fun sqrRev(x: Float) = easeOutQuad(x)

    @Deprecated("Use easeInCubic instead", ReplaceWith("easeInCubic(x)"))
    fun cubic(x: Float) = easeInCubic(x)

    @Deprecated("Use easeOutCubic instead", ReplaceWith("easeOutCubic(x)"))
    fun cubicRev(x: Float) = easeOutCubic(x)

    @Deprecated("Use easeInQuart instead", ReplaceWith("easeInQuart(x)"))
    fun quad(x: Float) = easeInQuart(x)

    @Deprecated("Use easeOutQuart instead", ReplaceWith("easeOutQuart(x)"))
    fun quadRev(x: Float) = easeOutQuart(x)

    fun interface Easing {
        fun eased(input: Float): Float
    }
}