package de.fabmax.kool.math.noise

import de.fabmax.kool.math.*
import kotlin.random.Random

class MultiPerlin2d(iterations: Int, baseFreq: Int, seed: Int = 19937, tableSz: Int = 1024) : Noise2d {

    private val perlins = mutableListOf<PerlinNoise2d>()

    init {
        val rand = Random(seed)
        var freq = baseFreq
        for (i in 0 until iterations) {
            perlins += PerlinNoise2d(rand.randomI(), tableSz).apply {
                tileSizeX = freq
                tileSizeY = freq
            }
            freq *= 2
        }
    }

    override fun eval(x: Float, y: Float): Float {
        var f = 0f
        var amplitude = 1f
        for (i in perlins.indices) {
            val p = perlins[i]
            f += p.eval(x * p.tileSizeX, y * p.tileSizeY) * amplitude
            amplitude *= 0.5f
        }
        return f
    }
}

class MultiPerlin3d(iterations: Int, baseFreq: Int, seed: Int = 19937, tableSz: Int = 1024) : Noise3d {

    private val perlins = mutableListOf<PerlinNoise3d>()

    init {
        val rand = Random(seed)
        var freq = baseFreq
        for (i in 0 until iterations) {
            perlins += PerlinNoise3d(rand.randomI(), tableSz).apply {
                tileSizeX = freq
                tileSizeY = freq
                tileSizeZ = freq
            }
            freq *= 2
        }
    }

    override fun eval(x: Float, y: Float, z: Float): Float {
        var f = 0f
        var amplitude = 1f
        for (i in perlins.indices) {
            val p = perlins[i]
            f += p.eval(x * p.tileSizeX, y * p.tileSizeY, z * p.tileSizeZ) * amplitude
            amplitude *= 0.5f
        }
        return f
    }
}

class PerlinNoise2d(seed: Int = 19937, private val tableSz: Int = 256) : Noise2d {
    private val gradients = Array(tableSz) { MutableVec2f() }
    private val permutationTab = IntArray(tableSz)

    var tileSizeX = 0
    var tileSizeY = 0

    init {
        val rand = Random(seed)

        for (i in 0 until tableSz) {
            gradients[i].apply {
                do {
                    x = rand.randomF(-1f, 1f)
                    y = rand.randomF(-1f, 1f)
                } while (sqrLength() > 1f)
                norm()
            }
        }

        permutationTab.indices.shuffled(Random(seed)).forEachIndexed { i, shuffled -> permutationTab[i] = shuffled }
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

    private fun wrapX(x: Int) = if (tileSizeX == 0) x else x.wrap(0, tileSizeX)
    private fun wrapY(y: Int) = if (tileSizeY == 0) y else y.wrap(0, tileSizeY)

    private fun wrapX(x: Float) = if (tileSizeX == 0) x else x.wrap(0f, tileSizeX.toFloat())
    private fun wrapY(y: Float) = if (tileSizeY == 0) y else y.wrap(0f, tileSizeY.toFloat())

    override fun eval(x: Float, y: Float): Float {
        val xw = wrapX(x)
        val yw = wrapY(y)

        val xi0 = modSize(xw.toInt())
        val yi0 = modSize(yw.toInt())

        val xi1 = modSize(wrapX(xi0 + 1))
        val yi1 = modSize(wrapY(yi0 + 1))

        val tx = xw - xw.toInt()
        val ty = yw - yw.toInt()

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

        val a = lerp(p00.dot(c00), p10.dot(c10), u)
        val b = lerp(p01.dot(c01), p11.dot(c11), u)

        return lerp(a, b, v)
    }
}

class PerlinNoise3d(seed: Int = 19937, private val tableSz: Int = 1024) : Noise3d {
    private val gradients = Array(tableSz) { MutableVec3f() }
    private val permutationTab = IntArray(tableSz)

    var tileSizeX = 0
    var tileSizeY = 0
    var tileSizeZ = 0

    init {
        val rand = Random(seed)

        for (i in 0 until tableSz) {
            gradients[i].apply {
                do {
                    x = rand.randomF(-1f, 1f)
                    y = rand.randomF(-1f, 1f)
                    z = rand.randomF(-1f, 1f)
                } while (sqrLength() > 1f)
                norm()
            }
        }

        permutationTab.indices.shuffled(Random(seed)).forEachIndexed { i, shuffled -> permutationTab[i] = shuffled }
    }

    private fun hash(x: Int, y: Int, z: Int) = permutationTab[(permutationTab[(permutationTab[x] + y) % permutationTab.size] + z) % permutationTab.size]

    private fun smoothStep(t: Float) = (t * t * (3 - 2 * t)).clamp()

    private fun lerp(a: Float, b: Float, l: Float) = a * (1 - l) + b * l

    private fun modSize(x: Int): Int {
        val m = x % tableSz
        if (m < 0) {
            return m + tableSz
        }
        return m
    }

    private fun wrapX(x: Int) = if (tileSizeX == 0) x else x.wrap(0, tileSizeX)
    private fun wrapY(y: Int) = if (tileSizeY == 0) y else y.wrap(0, tileSizeY)
    private fun wrapZ(z: Int) = if (tileSizeZ == 0) z else z.wrap(0, tileSizeZ)

    private fun wrapX(x: Float) = if (tileSizeX == 0) x else x.wrap(0f, tileSizeX.toFloat())
    private fun wrapY(y: Float) = if (tileSizeY == 0) y else y.wrap(0f, tileSizeY.toFloat())
    private fun wrapZ(z: Float) = if (tileSizeZ == 0) z else z.wrap(0f, tileSizeZ.toFloat())

    override fun eval(x: Float, y: Float, z: Float): Float {
        val xw = wrapX(x)
        val yw = wrapY(y)
        val zw = wrapZ(z)

        val xi0 = modSize(xw.toInt())
        val yi0 = modSize(yw.toInt())
        val zi0 = modSize(zw.toInt())

        val xi1 = modSize(wrapX(xi0 + 1))
        val yi1 = modSize(wrapY(yi0 + 1))
        val zi1 = modSize(wrapZ(zi0 + 1))

        val tx = xw - xw.toInt()
        val ty = yw - yw.toInt()
        val tz = zw - zw.toInt()

        val u = smoothStep(tx)
        val v = smoothStep(ty)
        val w = smoothStep(tz)

        // gradients at the corner of the cell
        val c000 = gradients[hash(xi0, yi0, zi0)]
        val c100 = gradients[hash(xi1, yi0, zi0)]
        val c010 = gradients[hash(xi0, yi1, zi0)]
        val c110 = gradients[hash(xi1, yi1, zi0)]

        val c001 = gradients[hash(xi0, yi0, zi1)]
        val c101 = gradients[hash(xi1, yi0, zi1)]
        val c011 = gradients[hash(xi0, yi1, zi1)]
        val c111 = gradients[hash(xi1, yi1, zi1)]

        // generate vectors going from the grid points to p
        val x0 = tx
        val y0 = ty
        val z0 = tz
        val x1 = tx - 1
        val y1 = ty - 1
        val z1 = tz - 1

        val p000 = Vec3f(x0, y0, z0)
        val p100 = Vec3f(x1, y0, z0)
        val p010 = Vec3f(x0, y1, z0)
        val p110 = Vec3f(x1, y1, z0)

        val p001 = Vec3f(x0, y0, z1)
        val p101 = Vec3f(x1, y0, z1)
        val p011 = Vec3f(x0, y1, z1)
        val p111 = Vec3f(x1, y1, z1)

        val a = lerp(p000.dot(c000), p100.dot(c100), u)
        val b = lerp(p010.dot(c010), p110.dot(c110), u)
        val c = lerp(p001.dot(c001), p101.dot(c101), u)
        val d = lerp(p011.dot(c011), p111.dot(c111), u)

        val e = lerp(a, b, v)
        val f = lerp(c, d, v)

        return lerp(e, f, w)
    }
}