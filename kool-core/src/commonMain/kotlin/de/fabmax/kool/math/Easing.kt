package de.fabmax.kool.math

/**
 * Collection of various easing functions. All of them map an input value in the range [0..1] to an output value
 * in the range [0..1]. These are particularly useful for animations.
 */
object Easing {

    val linear = Easing { it }
    val smooth = Easing { smoothStep(0f, 1f, it) }
    val sqr = Easing { it * it }
    val sqrRev = Easing { 1f - sqr(1f - it) }
    val cubic = Easing { it * it * it }
    val cubicRev = Easing { 1f - cubic(1f - it) }
    val quad = Easing { it * it * it * it }
    val quadRev = Easing { 1f - quad(1f - it) }

    /**
     * Starts slow, ends slow, fast in the middle: `x -> smoothStep(0.0, 1.0, x)`
     */
    fun smooth(x: Float) = smooth.eased(x)

    /**
     * Square interpolation: Starts slow, ends fast. `x -> x * x`
     */
    fun sqr(x: Float) = sqr.eased(x)

    /**
     * Reversed square interpolation: Starts fast, ends slow. `x -> 1.0 - sqr(1.0 - x)`
     */
    fun sqrRev(x: Float) = sqrRev.eased(x)

    /**
     * Cubic interpolation: Starts slow, ends faster. `x -> x ^ 3`
     */
    fun cubic(x: Float) = cubic.eased(x)

    /**
     * Reversed cubic interpolation: Starts faster, ends slow. `x -> 1.0 - cubic(1.0 - x)`
     */
    fun cubicRev(x: Float) = cubicRev.eased(x)

    /**
     * Quad interpolation: Starts slow, ends very fast. `x -> x ^ 4`
     */
    fun quad(x: Float) = quad.eased(x)

    /**
     * Reversed quad interpolation: Starts very fast, ends slow. `x -> 1.0 - quad(1.0 - x)`
     */
    fun quadRev(x: Float) = quadRev.eased(x)

    fun interface Easing {
        fun eased(input: Float): Float
    }
}