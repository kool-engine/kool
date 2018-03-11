package de.fabmax.kool.math

import de.fabmax.kool.now
import kotlin.math.abs

val defaultRandomInstance = Random(now().toInt())

fun randomI(): Int = defaultRandomInstance.randomI()
fun randomI(min: Int, max: Int): Int = defaultRandomInstance.randomI(min, max)

fun randomD(): Double = defaultRandomInstance.randomD()
fun randomD(min: Double, max: Double): Double = defaultRandomInstance.randomD(min, max)

fun randomF(): Float = defaultRandomInstance.randomF()
fun randomF(min: Float, max: Float): Float = defaultRandomInstance.randomF(min, max)

open class Random(seed: Int) {
    private var x = seed
    private var y = 362436000
    private var z = 521288629
    private var c = 7654321

    private val lock = Any()

    open fun randomI(): Int {
        return synchronized(lock) {
            x = 69069 * x + 12345

            y = y xor (y shl 13)
            y = y xor (y shr 17)
            y = y xor (y shl 5)

            val t = 698769069L * z + c
            c = (t shr 32).toInt()
            z = t.toInt()

            x + y + z
        }
    }
    fun randomI(min: Int, max: Int): Int = abs(randomI()) % (max - min + 1) + min

    fun randomF(): Float = abs(randomI()) / Int.MAX_VALUE.toFloat()
    fun randomF(min: Float, max: Float): Float = randomF() * (max - min) + min

    fun randomD(): Double {
        val l = (abs(randomI().toLong()) shl 32) or abs(randomI().toLong())
        return abs(l) / Long.MAX_VALUE.toDouble()
    }
    fun randomD(min: Double, max: Double): Double = randomD() * (max - min) + min

}