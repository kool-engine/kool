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
    private val grid: Array<Vec2f>

    init {
        val rand = Random(seed)
        grid = Array(gridSizeX * gridSizeY) {
            Vec2f(rand.randomF(), rand.randomF())
        }
    }

    private fun grid(x: Int, y: Int): Vec2f {
        return grid[y * gridSizeX + x]
    }

    override fun eval(x: Float, y: Float): Float {
        val px = (x * gridSizeX).mod(gridSizeX.toFloat())
        val py = (y * gridSizeY).mod(gridSizeY.toFloat())
        val ix = px.toInt()
        val iy = py.toInt()

        var d = MAX_D
        for (gy in iy-1 .. iy+1) {
            for (gx in ix - 1..ix + 1) {
                val ogx = when {
                    gx < 0 -> gridSizeX
                    gx >= gridSizeX -> -gridSizeX
                    else -> 0
                }
                val ogy = when {
                    gy < 0 -> gridSizeY
                    gy >= gridSizeY -> -gridSizeY
                    else -> 0
                }

                val v = grid(gx + ogx, gy + ogy)
                val dx = px - (v.x + gx)
                val dy = py - (v.y + gy)
                d = min(d, sqrt(dx*dx + dy*dy))
            }
        }
        return d / MAX_D
    }

    companion object {
        private val MAX_D = sqrt(2f)
    }
}

class WorleyNoise3d(val gridSizeX: Int, val gridSizeY: Int, val gridSizeZ: Int, seed: Int = 19937) : Noise3d {
    private val grid: Array<Vec3f>

    init {
        val rand = Random(seed)
        grid = Array(gridSizeX * gridSizeY * gridSizeZ) {
            Vec3f(rand.randomF(), rand.randomF(), rand.randomF())
        }
    }

    private fun grid(x: Int, y: Int, z: Int): Vec3f {
        return grid[z * (gridSizeX * gridSizeY) + y * gridSizeX + x]
    }

    override fun eval(x: Float, y: Float, z: Float): Float {
        val px = (x * gridSizeX).mod(gridSizeX.toFloat())
        val py = (y * gridSizeY).mod(gridSizeY.toFloat())
        val pz = (z * gridSizeZ).mod(gridSizeZ.toFloat())
        val ix = px.toInt()
        val iy = py.toInt()
        val iz = pz.toInt()

        var d = MAX_D
        for (gz in iz-1 .. iz+1) {
            for (gy in iy-1 .. iy+1) {
                for (gx in ix-1 .. ix+1) {
                    val ogx = when {
                        gx < 0 -> gridSizeX
                        gx >= gridSizeX -> -gridSizeX
                        else -> 0
                    }
                    val ogy = when {
                        gy < 0 -> gridSizeY
                        gy >= gridSizeY -> -gridSizeY
                        else -> 0
                    }
                    val ogz = when {
                        gz < 0 -> gridSizeZ
                        gz >= gridSizeZ -> -gridSizeZ
                        else -> 0
                    }

                    val v = grid(gx + ogx, gy + ogy, gz + ogz)
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