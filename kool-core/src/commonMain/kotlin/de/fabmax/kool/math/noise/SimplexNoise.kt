package de.fabmax.kool.math.noise

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomI
import kotlin.random.Random

/**
 * Similar to Perlin noise, but better?
 *
 * Ported from OpenSimplex2:
 * https://github.com/KdotJPG/OpenSimplex2
 */
class SimplexNoise3d(seed: Int) : Noise3d {

    private val perm = IntArray(PSIZE)
    private val permGrad3 = Array(PSIZE) { MutableVec3f() }

    init {
        val source = IntArray(PSIZE)
        for (i in 0 until PSIZE) {
            source[i] = i
        }
        val rand = Random(seed)
        for (i in PSIZE - 1 downTo 0) {
            var r = (rand.randomI() + 31) % (i + 1)
            if (r < 0) {
                r += i + 1
            }
            perm[i] = source[r]
            permGrad3[i].set(GRADIENTS_3D[perm[i]])
            source[r] = source[i]
        }
    }

    override fun eval(x: Float, y: Float, z: Float) = noise3Classic(x, y, z)

    /**
     * 3D Re-oriented 4-point BCC noise, classic orientation.
     * Proper substitute for 3D Simplex in light of Forbidden Formulae.
     * Use noise3XYBeforeZ or noise3XZBeforeY instead, wherever appropriate.
     */
    fun noise3Classic(x: Float, y: Float, z: Float): Float {
        // Re-orient the cubic lattices via rotation, to produce the expected look on cardinal planar slices.
        // If texturing objects that don't tend to have cardinal plane faces, you could even remove this.
        // Orthonormal rotation. Not a skew transform.
        val r = 2f / 3f * (x + y + z)
        val xr = r - x
        val yr = r - y
        val zr = r - z

        // Evaluate both lattices to form a BCC lattice.
        return noise3BCC(xr, yr, zr)
    }

    /**
     * 3D Re-oriented 4-point BCC noise, with better visual isotropy in (X, Y).
     * Recommended for 3D terrain and time-varied animations.
     * The Z coordinate should always be the "different" coordinate in your use case.
     * If Y is vertical in world coordinates, call noise3XYBeforeZ(x, z, Y) or use noise3XZBeforeY.
     * If Z is vertical in world coordinates, call noise3XYBeforeZ(x, y, Z).
     * For a time varied animation, call noise3XYBeforeZ(x, y, T).
     */
    fun noise3XYBeforeZ(x: Float, y: Float, z: Float): Float {
        // Re-orient the cubic lattices without skewing, to make X and Y triangular like 2D.
        // Orthonormal rotation. Not a skew transform.
        val xy = x + y
        val s2 = xy * -0.211324865405187f
        val zz = z * 0.577350269189626f
        val xr = x + s2 - zz
        val yr = y + s2 - zz
        val zr = xy * 0.577350269189626f + zz

        // Evaluate both lattices to form a BCC lattice.
        return noise3BCC(xr, yr, zr)
    }

    /**
     * 3D Re-oriented 4-point BCC noise, with better visual isotropy in (X, Z).
     * Recommended for 3D terrain and time-varied animations.
     * The Y coordinate should always be the "different" coordinate in your use case.
     * If Y is vertical in world coordinates, call noise3XZBeforeY(x, Y, z).
     * If Z is vertical in world coordinates, call noise3XZBeforeY(x, Z, y) or use noise3XYBeforeZ.
     * For a time varied animation, call noise3XZBeforeY(x, T, y) or use noise3XYBeforeZ.
     */
    fun noise3XZBeforeY(x: Float, y: Float, z: Float): Float {
        // Re-orient the cubic lattices without skewing, to make X and Z triangular like 2D.
        // Orthonormal rotation. Not a skew transform.
        val xz = x + z
        val s2 = xz * -0.211324865405187f
        val yy = y * 0.577350269189626f
        val xr = x + s2 - yy
        val zr = z + s2 - yy
        val yr = xz * 0.577350269189626f + yy

        // Evaluate both lattices to form a BCC lattice.
        return noise3BCC(xr, yr, zr)
    }

    private fun noise3BCC(xr: Float, yr: Float, zr: Float): Float {
        // Get base and offsets inside cube of first lattice.
        val xrb = fastFloor(xr)
        val yrb = fastFloor(yr)
        val zrb = fastFloor(zr)
        val xri = xr - xrb
        val yri = yr - yrb
        val zri = zr - zrb

        // Identify which octant of the cube we're in. This determines which cell
        // in the other cubic lattice we're in, and also narrows down one point on each.
        val xht = (xri + 0.5f).toInt()
        val yht = (yri + 0.5f).toInt()
        val zht = (zri + 0.5f).toInt()
        val index = (xht shl 0) or (yht shl 1) or (zht shl 2)

        // Point contributions
        var value = 0f
        var c: LatticePoint3d? = LOOKUP_3D[index]
        while (c != null) {
            val dxr = xri + c.dxr
            val dyr = yri + c.dyr
            val dzr = zri + c.dzr
            var attn = 0.5f - dxr * dxr - dyr * dyr - dzr * dzr
            if (attn < 0) {
                c = c.nextOnFailure
            } else {
                val pxm = (xrb + c.xrv) and PMASK
                val pym = (yrb + c.yrv) and PMASK
                val pzm = (zrb + c.zrv) and PMASK
                val grad = permGrad3[perm[perm[pxm] xor pym] xor pzm]
                val extrapolation = grad.x * dxr + grad.y * dyr + grad.z * dzr

                attn *= attn
                value += attn * attn * extrapolation
                c = c.nextOnSuccess
            }
        }
        return value
    }

    private fun fastFloor(x: Float): Int {
        val xi = x.toInt()
        return if (x < xi) xi - 1 else xi
    }

    companion object {
        private const val PSIZE = 2048
        private const val PMASK = 2047

        private const val N3 = 0.030485933181293584f

        private val GRADIENTS_3D: Array<Vec3f>
        private val LOOKUP_3D: Array<LatticePoint3d>


        init {
            val grad3 = arrayOf(
                MutableVec3f(-2.22474487139f,      -2.22474487139f,      -1.0f),
                MutableVec3f(-2.22474487139f,      -2.22474487139f,       1.0f),
                MutableVec3f(-3.0862664687972017f, -1.1721513422464978f,  0.0f),
                MutableVec3f(-1.1721513422464978f, -3.0862664687972017f,  0.0f),
                MutableVec3f(-2.22474487139f,      -1.0f,                -2.22474487139f),
                MutableVec3f(-2.22474487139f,       1.0f,                -2.22474487139f),
                MutableVec3f(-1.1721513422464978f,  0.0f,                -3.0862664687972017f),
                MutableVec3f(-3.0862664687972017f,  0.0f,                -1.1721513422464978f),
                MutableVec3f(-2.22474487139f,      -1.0f,                 2.22474487139f),
                MutableVec3f(-2.22474487139f,       1.0f,                 2.22474487139f),
                MutableVec3f(-3.0862664687972017f,  0.0f,                 1.1721513422464978f),
                MutableVec3f(-1.1721513422464978f,  0.0f,                 3.0862664687972017f),
                MutableVec3f(-2.22474487139f,       2.22474487139f,      -1.0f),
                MutableVec3f(-2.22474487139f,       2.22474487139f,       1.0f),
                MutableVec3f(-1.1721513422464978f,  3.0862664687972017f,  0.0f),
                MutableVec3f(-3.0862664687972017f,  1.1721513422464978f,  0.0f),
                MutableVec3f(-1.0f,                -2.22474487139f,      -2.22474487139f),
                MutableVec3f( 1.0f,                -2.22474487139f,      -2.22474487139f),
                MutableVec3f( 0.0f,                -3.0862664687972017f, -1.1721513422464978f),
                MutableVec3f( 0.0f,                -1.1721513422464978f, -3.0862664687972017f),
                MutableVec3f(-1.0f,                -2.22474487139f,       2.22474487139f),
                MutableVec3f( 1.0f,                -2.22474487139f,       2.22474487139f),
                MutableVec3f( 0.0f,                -1.1721513422464978f,  3.0862664687972017f),
                MutableVec3f( 0.0f,                -3.0862664687972017f,  1.1721513422464978f),
                MutableVec3f(-1.0f,                 2.22474487139f,      -2.22474487139f),
                MutableVec3f( 1.0f,                 2.22474487139f,      -2.22474487139f),
                MutableVec3f( 0.0f,                 1.1721513422464978f, -3.0862664687972017f),
                MutableVec3f( 0.0f,                 3.0862664687972017f, -1.1721513422464978f),
                MutableVec3f(-1.0f,                 2.22474487139f,       2.22474487139f),
                MutableVec3f( 1.0f,                 2.22474487139f,       2.22474487139f),
                MutableVec3f( 0.0f,                 3.0862664687972017f,  1.1721513422464978f),
                MutableVec3f( 0.0f,                 1.1721513422464978f,  3.0862664687972017f),
                MutableVec3f( 2.22474487139f,      -2.22474487139f,      -1.0f),
                MutableVec3f( 2.22474487139f,      -2.22474487139f,       1.0f),
                MutableVec3f( 1.1721513422464978f, -3.0862664687972017f,  0.0f),
                MutableVec3f( 3.0862664687972017f, -1.1721513422464978f,  0.0f),
                MutableVec3f( 2.22474487139f,      -1.0f,                -2.22474487139f),
                MutableVec3f( 2.22474487139f,       1.0f,                -2.22474487139f),
                MutableVec3f( 3.0862664687972017f,  0.0f,                -1.1721513422464978f),
                MutableVec3f( 1.1721513422464978f,  0.0f,                -3.0862664687972017f),
                MutableVec3f( 2.22474487139f,      -1.0f,                 2.22474487139f),
                MutableVec3f( 2.22474487139f,       1.0f,                 2.22474487139f),
                MutableVec3f( 1.1721513422464978f,  0.0f,                 3.0862664687972017f),
                MutableVec3f( 3.0862664687972017f,  0.0f,                 1.1721513422464978f),
                MutableVec3f( 2.22474487139f,       2.22474487139f,      -1.0f),
                MutableVec3f( 2.22474487139f,       2.22474487139f,       1.0f),
                MutableVec3f( 3.0862664687972017f,  1.1721513422464978f,  0.0f),
                MutableVec3f( 1.1721513422464978f,  3.0862664687972017f,  0.0f)
            )
            grad3.forEach { it.mul(1f / N3) }

            GRADIENTS_3D = Array(PSIZE) { grad3[it % grad3.size] }

            LOOKUP_3D = Array(8) { i ->
                val i1 = (i shr 0) and 1
                val j1 = (i shr 1) and 1
                val k1 = (i shr 2) and 1
                val i2 = i1 xor 1
                val j2 = j1 xor 1
                val k2 = k1 xor 1

                // The two points within this octant, one from each of the two cubic half-lattices.
                val c0 = LatticePoint3d(i1, j1, k1, 0)
                val c1 = LatticePoint3d(i1 + i2, j1 + j2, k1 + k2, 1)

                // Each single step away on the first half-lattice.
                val c2 = LatticePoint3d(i1 xor 1, j1, k1, 0)
                val c3 = LatticePoint3d(i1, j1 xor 1, k1, 0)
                val c4 = LatticePoint3d(i1, j1, k1 xor 1, 0)

                // Each single step away on the second half-lattice.
                val c5 = LatticePoint3d(i1 + (i2 xor 1), j1 + j2, k1 + k2, 1)
                val c6 = LatticePoint3d(i1 + i2, j1 + (j2 xor 1), k1 + k2, 1)
                val c7 = LatticePoint3d(i1 + i2, j1 + j2, k1 + (k2 xor 1), 1)

                // First two are guaranteed.
                c0.nextOnFailure = c1
                c0.nextOnSuccess = c1
                c1.nextOnFailure = c2
                c1.nextOnSuccess = c2

                // Once we find one on the first half-lattice, the rest are out.
                // In addition, knowing c2 rules out c5.
                c2.nextOnFailure = c3
                c2.nextOnSuccess = c6
                c3.nextOnFailure = c4
                c3.nextOnSuccess = c5
                c4.nextOnFailure = c5
                c4.nextOnSuccess = c5

                // Once we find one on the second half-lattice, the rest are out.
                c5.nextOnFailure = c6
                c5.nextOnSuccess = null
                c6.nextOnFailure = c7
                c6.nextOnSuccess = null
                c7.nextOnFailure = null
                c7.nextOnSuccess = null

                c0
            }
        }
    }

    private class LatticePoint3d(xrv: Int, yrv: Int, zrv: Int, lattice: Int) {
        val dxr: Float = -xrv + lattice * 0.5f
        val dyr: Float = -yrv + lattice * 0.5f
        val dzr: Float = -zrv + lattice * 0.5f

        val xrv: Int = xrv + lattice * 1024
        val yrv: Int = yrv + lattice * 1024
        val zrv: Int = zrv + lattice * 1024

        var nextOnFailure: LatticePoint3d? = null
        var nextOnSuccess: LatticePoint3d? = null
    }

}