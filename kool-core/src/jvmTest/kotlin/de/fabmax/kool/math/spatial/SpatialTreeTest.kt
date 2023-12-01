package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.math.randomI
import kotlin.random.Random
import kotlin.test.Test

class SpatialTreeTest {

    private fun makeOcTreeAndPoints(n: Int): Pair<OcTree<Vec3f>, List<Vec3f>> {
        val r = Random(1338)
        val allPoints = mutableListOf<Vec3f>()
        val tree = OcTree(Vec3fAdapter(), bounds = BoundingBoxD(Vec3d(-10.0), Vec3d(10.0)))
        repeat(n) {
            val pt = Vec3f(r.randomF(-10f, 10f), r.randomF(-10f, 10f), r.randomF(-10f, 10f))
            allPoints += pt
            tree += pt
        }
        return tree to allPoints
    }

    @Test
    fun testNearest() {
        val (ocTree, allPoints) = makeOcTreeAndPoints(101)
        val kdTree = KdTree(allPoints, Vec3fAdapter())

        val r = Random(1337)
        val trav = NearestTraverser<Vec3f>()
        repeat(100) {
            val query = Vec3f(r.randomF(-10f, 10f), r.randomF(-10f, 10f), r.randomF(-10f, 10f))
            val check = allPoints.minBy { it.distance(query) }

            trav.setup(query).traverse(ocTree)
            assert(trav.nearest == check)

            trav.setup(query).traverse(kdTree)
            assert(trav.nearest == check)
        }
    }

    @Test
    fun testKNearest() {
        val (ocTree, allPoints) = makeOcTreeAndPoints(102)
        val kdTree = KdTree(allPoints, Vec3fAdapter())

        val r = Random(1337)
        val trav = KNearestTraverser<Vec3f>()
        repeat(100) {
            val queryPt = Vec3f(r.randomF(-10f, 10f), r.randomF(-10f, 10f), r.randomF(-10f, 10f))
            val k = r.randomI(5, 20)
            val check = allPoints.sortedBy { it.distance(queryPt) }.subList(0, k).toSet()

            trav.setup(queryPt, k).traverse(ocTree)
            assert(trav.result.size == check.size && trav.result.all { it in check })

            trav.setup(queryPt, k).traverse(kdTree)
            assert(trav.result.size == check.size && trav.result.all { it in check })
        }
    }

    @Test
    fun testInRadius() {
        val (ocTree, allPoints) = makeOcTreeAndPoints(103)
        val kdTree = KdTree(allPoints, Vec3fAdapter())

        val r = Random(1337)
        val trav = InRadiusTraverser<Vec3f>()
        repeat(100) {
            val queryPt = Vec3f(r.randomF(-10f, 10f), r.randomF(-10f, 10f), r.randomF(-10f, 10f))
            val queryRadius = r.randomF(1f, 2f)
            val check = allPoints.filter { it.distance(queryPt) < queryRadius }.toSet()

            trav.setup(queryPt, queryRadius).traverse(ocTree)
            assert(trav.result.size == check.size && trav.result.all { it in check })

            trav.setup(queryPt, queryRadius).traverse(kdTree)
            assert(trav.result.size == check.size && trav.result.all { it in check })
        }
    }

    @Test
    fun testOcTreeMaxDepth() {
        val tree = OcTree(Vec3fAdapter(), bounds = BoundingBoxD(Vec3d(-2.0), Vec3d(2.0)), bucketSz = 4, maxDepth = 2)

        // add more than bucket size points with identical position -> impossible to split
        repeat(5) {
            tree += Vec3f(1f)
        }

        // grow tree by adding an out-of-bounds point
        tree += Vec3f(10f, 10f, 10f)

        // again add more than bucket size points with identical position -> impossible to split
        repeat(5) {
            tree += Vec3f(-1f)
        }
    }
}