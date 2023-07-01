package de.fabmax.kool.math

import de.fabmax.kool.util.Time
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.nextInt

val defaultRandomInstance = Random((Time.precisionTime * 1e3).toInt())

/**
 * Returns a random integer in range of [Int.MIN_VALUE] .. [Int.MAX_VALUE]
 */
fun randomI(): Int = defaultRandomInstance.randomI()

/**
 * Returns a random integer in range of [min] .. [max] (inclusive!)
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

fun randomGaussianF(mu: Float, sigma: Float) = defaultRandomInstance.randomGaussianF(mu, sigma)
fun randomGaussianF() = defaultRandomInstance.randomGaussianF()

fun randomInUnitCube(result: MutableVec3f = MutableVec3f()) = defaultRandomInstance.randomInUnitCube(result)
fun randomInUnitSphere(result: MutableVec3f = MutableVec3f()) = defaultRandomInstance.randomInUnitSphere(result)

/**
 * Returns a random integer in range of [Int.MIN_VALUE] .. [Int.MAX_VALUE]. Same as [Random.nextInt].
 */
fun Random.randomI() = nextInt()

/**
 * Returns a random integer in range of [min] .. [max] (inclusive!).
 */
fun Random.randomI(min: Int, max: Int) = nextInt(min, max + 1)

/**
 * Returns a random integer in the given range.
 */
fun Random.randomI(rng: IntRange) = nextInt(rng)

fun Random.randomF() = nextFloat()
fun Random.randomF(min: Float, max: Float) = nextFloat() * (max - min) + min

fun Random.randomD() = nextDouble()
fun Random.randomD(min: Double, max: Double) = nextDouble() * (max - min) + min

fun Random.randomGaussianF(mu: Float, sigma: Float) = mu + randomGaussianF() * sigma
fun Random.randomGaussianF(): Float {
    var x1: Float
    var x2: Float
    var w: Float
    do {
        x1 = randomF(-1f, 1f)
        x2 = randomF(-1f, 1f)
        w = x1 * x1 + x2 * x2
    } while (w >= 1f || w == 0f)
    w = sqrt(-2 * ln(w) / w)

    return x1 * w
}

fun Random.randomInUnitCube(result: MutableVec3f = MutableVec3f()): MutableVec3f {
    result.x = randomF(-1f, 1f)
    result.y = randomF(-1f, 1f)
    result.z = randomF(-1f, 1f)
    return result
}

fun Random.randomInUnitSphere(result: MutableVec3f = MutableVec3f()): MutableVec3f {
    var guard = 0
    do {
        result.x = randomF(-1f, 1f)
        result.y = randomF(-1f, 1f)
        result.z = randomF(-1f, 1f)
    } while (result.sqrLength() > 1f && guard++ < 100)
    return result
}

class KissRandom(seed: Int) : Random() {
    private var x = seed
    private var y = 362436000
    private var z = 521288629
    private var c = 7654321

    private var hasNextGaussian = false
    private var nextGaussian = 0f

    /**
     * 32-bit KISS RNG
     * https://de.wikipedia.org/wiki/KISS_(Zufallszahlengenerator)
     *
     * Slower than the default Random implementation (on JVM slightly, Javascript drastically) but is
     * supposed to have better statistical properties.
     */
    override fun nextBits(bitCount: Int): Int {
        // linear congruential generator
        x = 69069 * x + 12345

        // xorshift
        y = y xor (y shl 13)
        y = y xor (y ushr 17)
        y = y xor (y shl 5)

        // multiply with carry
        val t = 698769069L * z + c
        c = (t ushr 32).toInt()
        z = t.toInt()

        val r = x + y + z
        return if (bitCount == 32) r else r ushr (32 - bitCount)
    }

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
        hasNextGaussian = true
        return x1 * w
    }
}

class TableRandom(tableSize: Int = 1024 * 1024, generator: Random = defaultRandomInstance) : Random() {
    private val random = IntArray(tableSize) { generator.nextInt() }
    private var index = 0

    override fun nextBits(bitCount: Int): Int {
        val r = random[index++]
        if (index == random.size) {
            index = 0
        }

        return if (bitCount == 32) r else r ushr (32 - bitCount)
    }
}