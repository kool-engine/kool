package de.fabmax.kool.math.noise

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.KdTree
import de.fabmax.kool.math.spatial.NearestTraverser
import de.fabmax.kool.math.spatial.Vec3fAdapter
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class WorleyNoise2d(val gridSizeX: Int, val gridSizeY: Int, seed: Int = 19937) : Noise2d {

    private val grid = Array(gridSizeX * gridSizeY) { MutableVec2f() }

    init {
        val rand = Random(seed)

        for (y in 0 until gridSizeY) {
            for (x in 0 until gridSizeX) {
                grid(x, y).set(x + rand.randomF(), y + rand.randomF())
            }
        }
    }

    private fun grid(x: Int, y: Int): MutableVec2f {
        val xw = x.wrap(0, gridSizeX)
        val yw = y.wrap(0, gridSizeY)
        return grid[yw * gridSizeX + xw]
    }

    override fun eval(x: Float, y: Float): Float {
        val px = x * gridSizeX
        val py = y * gridSizeY
        val ix = px.toInt()
        val iy = py.toInt()

        var d = MAX_D
        for (gy in iy-1 .. iy+1) {
            for (gx in ix - 1..ix + 1) {
                val v = grid(gx, gy)
                val dx = px - v.x
                val dy = py - v.y
                d = min(d, sqrt(dx*dx + dy*dy))
            }
        }
        return d / MAX_D
    }

    companion object {
        private val MAX_D = sqrt(2f)
    }
}

class GridWorleyNoise3d(val gridSizeX: Int, val gridSizeY: Int, val gridSizeZ: Int, seed: Int = 19937) : Noise3d {

    private val grid = Array(gridSizeX * gridSizeY * gridSizeZ) { MutableVec3f() }

    init {
        val rand = Random(seed)

        for (z in 0 until gridSizeZ) {
            for (y in 0 until gridSizeY) {
                for (x in 0 until gridSizeX) {
                    grid(x, y, z).set(rand.randomF(), rand.randomF(), rand.randomF())
                }
            }
        }
    }

    private fun grid(x: Int, y: Int, z: Int): MutableVec3f {
        val xw = x.wrap(0, gridSizeX)
        val yw = y.wrap(0, gridSizeY)
        val zw = z.wrap(0, gridSizeZ)
        return grid[zw * (gridSizeX * gridSizeY) + yw * gridSizeX + xw]
    }

    override fun eval(x: Float, y: Float, z: Float): Float {
        val px = x * gridSizeX
        val py = y * gridSizeY
        val pz = z * gridSizeZ
        val ix = px.toInt()
        val iy = py.toInt()
        val iz = pz.toInt()

        var d = MAX_D
        for (gz in iz-1 .. iz+1) {
            for (gy in iy-1 .. iy+1) {
                for (gx in ix-1 .. ix+1) {
                    val v = grid(gx, gy, gz)
                    val dx = px - (v.x + gx)
                    val dy = py - (v.y + gy)
                    val dz = pz - (v.z + gz)
                    d = min(d, sqrt(dx*dx + dy*dy + dz*dz))
                }
            }
        }
        return d / MAX_D
    }

    companion object {
        private val MAX_D = sqrt(3f)
    }
}

class FreeWorleyNoise2d(nPoints: Int, seed: Int = 19937) : Noise2d {

    private val pointTree: KdTree<Vec3f>
    private val trav = NearestTraverser<Vec3f>()

    private val tmpVec = MutableVec3f()
    private val scale = nPoints.toFloat().pow(1f / 2f) / MAX_D

    init {
        val rand = Random(seed)
        val points = List(nPoints * 9) { MutableVec3f() }
        for (i in 0 until nPoints) {
            val p = Vec3f(rand.randomF(), rand.randomF(), 0f)
            var j = i * 9
            for (py in -1..1) {
                for (px in -1..1) {
                    points[j++].set(p.x + px, p.y + py, 0f)
                }
            }
        }
        pointTree = KdTree(points, Vec3fAdapter())
    }

    override fun eval(x: Float, y: Float): Float {
        trav.setup(Vec3f.ZERO)
        trav.center.set(x.toDouble(), y.toDouble(), 0.0)
        trav.traverse(pointTree)

        val n = trav.nearest
        return if (n != null) {
            n.distance(trav.center.toMutableVec3f(tmpVec)) * scale
        } else {
            1f
        }
    }

    companion object {
        private val MAX_D = sqrt(2f)
    }
}

class FreeWorleyNoise3d(nPoints: Int, seed: Int = 19937) : Noise3d {

    private val pointTree: KdTree<Vec3f>
    private val trav = NearestTraverser<Vec3f>()

    private val tmpVec = MutableVec3f()
    private val scale = nPoints.toFloat().pow(1f / 3f) / MAX_D

    init {
        val rand = Random(seed)
        val points = List(nPoints * 27) { MutableVec3f() }
        for (i in 0 until nPoints) {
            val p = Vec3f(rand.randomF(), rand.randomF(), rand.randomF())
            var j = i * 27
            for (pz in -1..1) {
                for (py in -1..1) {
                    for (px in -1..1) {
                        points[j++].set(p.x + px, p.y + py, p.z + pz)
                    }
                }
            }
        }
        pointTree = KdTree(points, Vec3fAdapter())
    }

    override fun eval(x: Float, y: Float, z: Float): Float {
        trav.setup(Vec3f.ZERO)
        trav.center.set(x.toDouble(), y.toDouble(), z.toDouble())
        trav.traverse(pointTree)

        val n = trav.nearest
        return if (n != null) {
            n.distance(trav.center.toMutableVec3f(tmpVec)) * scale
        } else {
            1f
        }
    }

    companion object {
        private val MAX_D = sqrt(3f)
    }
}