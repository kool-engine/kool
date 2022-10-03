package de.fabmax.kool.math

import de.fabmax.kool.util.Time
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.sqrt

val defaultRandomInstance = Random((Time.precisionTime * 1e3).toInt())

/**
 * Returns a random integer in range of [Int.MIN_VALUE] .. [Int.MAX_VALUE]
 */
fun randomI(): Int = defaultRandomInstance.randomI()

/**
 * Returns a random integer in range of [min] .. [max] (inclusive)
 */
fun randomI(min: Int, max: Int): Int = defaultRandomInstance.randomI(min, max)

/**
 * Returns a random integer in the given range.
 */
fun randomI(rng: IntRange): Int = defaultRandomInstance.randomI(rng.first, rng.last)

fun randomD(): Double = defaultRandomInstance.randomD()
fun randomD(min: Double, max: Double): Double = defaultRandomInstance.randomD(min, max)

fun randomF(): Float = defaultRandomInstance.randomF()
fun randomF(min: Float, max: Float): Float = defaultRandomInstance.randomF(min, max)

open class Random(seed: Int) {
    private var x = seed
    private var y = 362436000
    private var z = 521288629
    private var c = 7654321

    private var hasNextGaussian = false
    private var nextGaussian = 0f

    /**
     * Implements 32-bit KISS RNG
     * https://de.wikipedia.org/wiki/KISS_(Zufallszahlengenerator)
     */
    open fun randomI(): Int {
        // linear congruential generator
        x = 69069 * x + 12345

        // xorshift
        y = y xor (y shl 13)
        y = y xor (y shr 17)
        y = y xor (y shl 5)

        // multiply with carry
        val t = 698769069L * z + c
        c = (t shr 32).toInt()
        z = t.toInt()

        return x + y + z
    }
    fun randomI(min: Int, max: Int): Int = abs(randomI()) % (max - min + 1) + min
    fun randomI(rng: IntRange): Int = randomI(rng.first, rng.last)

    fun randomF(): Float = abs(randomI()) / Int.MAX_VALUE.toFloat()
    fun randomF(min: Float, max: Float): Float = randomF() * (max - min) + min

    fun randomD(): Double {
        val l = (abs(randomI().toLong()) shl 32) or abs(randomI().toLong())
        return abs(l) / Long.MAX_VALUE.toDouble()
    }
    fun randomD(min: Double, max: Double): Double = randomD() * (max - min) + min

    fun randomGaussianF(mu: Float, sigma: Float) = mu + randomGaussianF() * sigma

    fun randomGaussianF(): Float {
        if (hasNextGaussian) {
            hasNextGaussian = false
            return nextGaussian
        }

        var x1: Float
        var x2: Float
        var w: Float
        do {
            x1 = randomF(-1f, 1f)
            x2 = randomF(-1f, 1f)
            w = x1 * x1 + x2 * x2
        } while (w >= 1f || w == 0f)
        w = sqrt(-2 * ln(w) / w)

        nextGaussian = x2 * w
        return x1 * w
    }
}