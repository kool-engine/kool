package de.fabmax.kool.math

import de.fabmax.kool.now
import kotlin.math.abs

val defaultRandomInstance = Random(now().toInt())

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
        // t is a Long which is slow in javascript (because it must be emulated)
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

}