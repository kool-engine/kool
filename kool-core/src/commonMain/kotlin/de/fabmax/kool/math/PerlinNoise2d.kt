package de.fabmax.kool.math

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PerlinNoise2d(seed: Int = 19937, private val tableSz: Int = 256) {
    private val gradients = Array(tableSz) { MutableVec2f() }
    private val permutationTab = IntArray(tableSz)

    var wrapSizeX = 0
    var wrapSizeY = 0

    init {
        val rand = Random(seed)

        for (i in 0 until tableSz) {
            gradients[i].apply {
                val a = rand.randomF(0f, 2 * PI.toFloat())
                x = cos(a)
                y = sin(a)
            }
        }

        permutationTab.indices.shuffled().forEachIndexed { i, shuffled -> permutationTab[i] = shuffled }
    }

    private fun hash(x: Int, y: Int) = permutationTab[(permutationTab[x] + y) % permutationTab.size]

    private fun smoothStep(t: Float) = (t * t * (3 - 2 * t)).clamp()

    private fun lerp(a: Float, b: Float, l: Float) = a * (1 - l) + b * l

    private fun modSize(x: Int): Int {
        val m = x % tableSz
        if (m < 0) {
            return m + tableSz
        }
        return m
    }

    private fun wrappedXi(x: Int): Int {
        return if (wrapSizeX > 0) {
            x % wrapSizeX
        } else {
            x
        }
    }

    private fun wrappedYi(y: Int): Int {
        return if (wrapSizeY > 0) {
            y % wrapSizeY
        } else {
            y
        }
    }

    private fun wrappedXf(x: Float): Float {
        return if (wrapSizeX > 0) {
            x % wrapSizeX
        } else {
            x
        }
    }

    private fun wrappedYf(y: Float): Float {
        return if (wrapSizeY > 0) {
            y % wrapSizeY
        } else {
            y
        }
    }

    fun eval(xx: Float, yy: Float): Float {
        val x = wrappedXf(xx)
        val y = wrappedYf(yy)

        val xi0 = modSize(x.toInt())
        val yi0 = modSize(y.toInt())

        val xi1 = modSize(wrappedXi(xi0 + 1))
        val yi1 = modSize(wrappedYi(yi0 + 1))

        val tx = x - x.toInt()
        val ty = y - y.toInt()

        val u = smoothStep(tx)
        val v = smoothStep(ty)

        // gradients at the corner of the cell
        val c00 = gradients[hash(xi0, yi0)]
        val c10 = gradients[hash(xi1, yi0)]
        val c01 = gradients[hash(xi0, yi1)]
        val c11 = gradients[hash(xi1, yi1)]

        // generate vectors going from the grid points to p
        val x0 = tx
        val y0 = ty
        val x1 = tx - 1
        val y1 = ty - 1

        val p00 = Vec2f(x0, y0)
        val p10 = Vec2f(x1, y0)
        val p01 = Vec2f(x0, y1)
        val p11 = Vec2f(x1, y1)

        val a = lerp(p00 * c00, p10 * c10, u)
        val b = lerp(p01 * c01, p11 * c11, u)

        return lerp(a, b, v)
    }
}