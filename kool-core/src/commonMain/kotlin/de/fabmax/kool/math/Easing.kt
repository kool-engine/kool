package de.fabmax.kool.math

/**
 * Collection of various easing functions. All of them map an input value in the range [0..1] to an output value
 * in the range [0..1]. These are particularly useful for animations.
 */
object Easing {

    /**
     * Starts slow, ends slow, fast in the middle: `x -> smoothStep(0.0, 1.0, x)`
     */
    fun smooth(x: Float) = smoothStep(0f, 1f, x)

    /**
     * Square interpolation: Starts slow, ends fast. `x -> x * x`
     */
    fun sqr(x: Float) = x * x

    /**
     * Reversed square interpolation: Starts fast, ends slow. `x -> 1.0 - sqr(1.0 - x)`
     */
    fun sqrRev(x: Float) = 1f - sqr(1f - x)

    /**
     * Cubic interpolation: Starts slow, ends faster. `x -> x ^ 3`
     */
    fun cubic(x: Float) = x * x * x

    /**
     * Reversed cubic interpolation: Starts faster, ends slow. `x -> 1.0 - cubic(1.0 - x)`
     */
    fun cubicRev(x: Float) = 1f - cubic(1f - x)

    /**
     * Quad interpolation: Starts slow, ends very fast. `x -> x ^ 4`
     */
    fun quad(x: Float) = x * x * x * x

    /**
     * Reversed quad interpolation: Starts very fast, ends slow. `x -> 1.0 - quad(1.0 - x)`
     */
    fun quadRev(x: Float) = 1f - quad(1f - x)

}