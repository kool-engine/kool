package de.fabmax.kool.modules.mesh

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.logD
import kotlin.math.min
import kotlin.math.sqrt

class MeshCutXy(val geometry: IndexedVertexList) {

    val ocTreeHandler = OcTreeEdgeHandler(geometry)
    val halfEdgeMesh = HalfEdgeMesh(geometry, ocTreeHandler)

    private val eps: Float = geometry.bounds.min.distance(geometry.bounds.max) * FUZZY_EQ_F
    var shortEdgeThresh: Float

    init {
        var edLen = 0.0
        for (ed in ocTreeHandler) {
            edLen += ed.computeLength()
        }
        shortEdgeThresh = (edLen.toFloat() / ocTreeHandler.numEdges) * 0.05f
    }

    fun cutXy(cutPoly: List<Vec2f>) {
        val cutEdges = mutableListOf<Edge<Vec3f>>()
        for (i in 0 until cutPoly.size) {
            val j = (i + 1) % cutPoly.size
            cutEdges += Edge(Vec3f(cutPoly[i].x, cutPoly[i].y, 0f), Vec3f(cutPoly[j].x, cutPoly[j].y, 0f))
        }

        insertVertices(cutPoly)
        splitIntersectingEdges(cutEdges)
        deleteInside(cutPoly)

        // fixme: lots of errors if we do this, apparently edge octree is not correctly updated...
        //collapseShortEdges(cutPoly)

        halfEdgeMesh.rebuild(generateNormals = false, generateTangents = false)
    }

    private fun insertVertices(cutPoly: List<Vec2f>) {
        val trav = CoveringTriXyTrav()
        cutPoly.forEach { pt ->
            trav.setup(pt)
            trav.traverse(ocTreeHandler.edgeTree)
            trav.result.forEach { ed ->
                if (!ed.from.distXy(pt).isFuzzyZero(eps) && !ed.to.distXy(pt).isFuzzyZero(eps)) {
                    val edTo = ed.to
                    val insertedA = ed.split(pt.computeSplitFraction(ed))

                    // find inserted edge inside of original triangle
                    val newEdge = insertedA.edges.first { it.to === edTo }.next.next

                    val f = newEdge.from.distXy(pt) / newEdge.to.distXy(newEdge.from)
                    newEdge.split(f).apply { updatePosition(pt.x, pt.y, z) }

                } else {
                    if (ed.from.distXy(pt).isFuzzyZero(eps)) {
                        ed.from.updatePosition(pt.x, pt.y, ed.from.z)
                    } else {
                        ed.to.updatePosition(pt.x, pt.y, ed.to.z)
                    }
                }
            }
        }
        ocTreeHandler.edgeTree
    }

    private fun Vec2f.computeSplitFraction(edge: HalfEdgeMesh.HalfEdge): Float {
        val pt = Vec3f(x, y, 0f)
        val a = Vec3f(edge.from.x, edge.from.y, 0f)
        val b = Vec3f(edge.to.x, edge.to.y, 0f)
        val r = pt.nearestPointOnEdge(a, b, MutableVec3f())
        return r.distance(a) / b.distance(a)
    }

    /**
     * split any mesh edge that intersects cutPoly
     */
    private fun splitIntersectingEdges(cutEdges: List<Edge<Vec3f>>) {
        val intersectionTrav = EdgeXyIntersectionTrav()
        for (cutEdge in cutEdges) {
            for (pass in 1..2) {
                intersectionTrav.setup(cutEdge).traverse(ocTreeHandler.edgeTree)
                if (intersectionTrav.splitEdges.isNotEmpty()) {
                    //println("[$pass] split ${intersectionTrav.result.size} edges...")
                    intersectionTrav.splitEdges.forEach { (splitEd, _) ->
                        val splitPos = MutableVec3f()
                        if (computeXyEdgeIntersectionPoint(cutEdge.pt0, cutEdge.pt1, splitEd.from, splitEd.to, splitPos)) {
                            val f = splitPos.distXy(splitEd.from) / splitEd.to.distXy(splitEd.from)
                            splitEd.split(f).apply { updatePosition(splitPos.x, splitPos.y, z) }
                        }
                    }
                }
            }
        }
    }

    private fun collapseShortEdges(cutEdges: List<Edge<Vec3f>>) {
        var collapseCnt = 0
        val edgeTrav = ShortEdgeOnEdgeTraverser()
        for (cutEdge in cutEdges) {
            var remove = true
            while (remove) {
                remove = false
                edgeTrav.setup(cutEdge).traverse(ocTreeHandler.edgeTree)
                if (edgeTrav.result.isNotEmpty()) {
                    val ed = edgeTrav.result[0]
                    val minFromD = min(ed.from.distance(cutEdge.pt0), ed.from.distance(cutEdge.pt1))
                    val minToD = min(ed.to.distance(cutEdge.pt0), ed.to.distance(cutEdge.pt1))
                    if (minFromD < minToD) {
                        ed.collapse(0f)
                    } else {
                        ed.collapse(1f)
                    }
                    collapseCnt++
                    remove = true
                }
            }
        }

        logD { "Collapsed $collapseCnt short edges" }
    }

    /**
     * delete all triangles (and vertices) inside cutPoly
     */
    private fun deleteInside(cutPoly: List<Vec2f>) {
        var delCnt = 0
        ocTreeHandler.distinctTriangleEdges().filter {
            isInPolygon(MutableVec3f(it.from).add(it.next.from).add(it.next.next.from).mul(1 / 3f), cutPoly)
        }.forEach {
            it.deleteTriangle()
            delCnt++
        }
        //logD { "Deleted $delCnt inner triangles" }
    }

    private inner class CoveringTriXyTrav : KNearestTraverser<HalfEdgeMesh.HalfEdge>() {
        val triPts = MutableList(3) { MutableVec2f() }
        private val tmpVec = MutableVec3f()

        init {
            pointDistance = object : PointDistance<HalfEdgeMesh.HalfEdge> {
                override fun nodeSqrDistanceToPoint(node: SpatialTree<HalfEdgeMesh.HalfEdge>.Node, point: Vec3d): Double {
                    val pt = Vec3d(point.x, point.y, node.bounds.center.z)
                    return node.bounds.pointDistanceSqr(pt)
                }

                override fun itemSqrDistanceToPoint(tree: SpatialTree<HalfEdgeMesh.HalfEdge>, item: HalfEdgeMesh.HalfEdge, point: Vec3d): Double {
                    triPts[0].set(item.from.x, item.from.y)
                    triPts[1].set(item.next.from.x, item.next.from.y)
                    triPts[2].set(item.next.next.from.x, item.next.next.from.y)
                    return if (isInPolygon(center.toMutableVec3f(tmpVec), triPts)) {
                        return point.distanceToEdge(
                            Vec3d(item.from.x.toDouble(), item.from.y.toDouble(), 0.0),
                            Vec3d(item.to.x.toDouble(), item.to.y.toDouble(), 0.0))
                    } else {
                        Double.MAX_VALUE
                    }
                }
            }
        }

        fun setup(pt: Vec2f) {
            super.setup(Vec3f(pt.x, pt.y, 0f), 1, 1e6)
        }
    }

    private inner class ShortEdgeOnEdgeTraverser : InRadiusTraverser<HalfEdgeMesh.HalfEdge>() {
        lateinit var edge: Edge<Vec3f>

        fun setup(edge: Edge<Vec3f>): ShortEdgeOnEdgeTraverser {
            super.setup(MutableVec3f(edge.pt0).add(edge.pt1).mul(0.5f), edge.length / 2)
            this.edge = edge

            pointDistance = object : PointDistance<HalfEdgeMesh.HalfEdge> {
                override fun nodeSqrDistanceToPoint(node: SpatialTree<HalfEdgeMesh.HalfEdge>.Node, point: Vec3d): Double {
                    val pt = Vec3d(point.x, point.y, node.bounds.center.z)
                    return super.nodeSqrDistanceToPoint(node, pt)
                }

                override fun itemSqrDistanceToPoint(tree: SpatialTree<HalfEdgeMesh.HalfEdge>, item: HalfEdgeMesh.HalfEdge, point: Vec3d): Double {
                    if (/*item.opp == null &&*/ item.computeLength() < shortEdgeThresh) {
                        val d0 = Vec3f(item.from.x, item.from.y, 0f).distanceToEdge(edge.pt0, edge.pt1)
                        val d1 = Vec3f(item.to.x, item.to.y, 0f).distanceToEdge(edge.pt0, edge.pt1)
                        if (d0.isFuzzyZero(eps) && d1.isFuzzyZero(eps) && !isFuzzyEqual(item.computeLength(), edge.length, eps)) {
                            return 0.0
                        }
                    }
                    return Double.MAX_VALUE
                }
            }
            return this
        }
    }

    private inner class EdgeXyIntersectionTrav : InRadiusTraverser<HalfEdgeMesh.HalfEdge>() {
        lateinit var edge: Edge<Vec3f>
        val intersectionPt = MutableVec3f()

        val splitEdges = mutableListOf<Pair<HalfEdgeMesh.HalfEdge, Vec3f>>()

        override fun traverse(tree: SpatialTree<HalfEdgeMesh.HalfEdge>) {
            splitEdges.clear()
            super.traverse(tree)
            splitEdges.sortBy { it.second.distance(edge.pt0) }
        }

        fun setup(edge: Edge<Vec3f>): EdgeXyIntersectionTrav {
            super.setup(MutableVec3f(edge.pt0).add(edge.pt1).mul(0.5f), edge.length / 2)
            this.edge = edge

            pointDistance = object : PointDistance<HalfEdgeMesh.HalfEdge> {
                override fun nodeSqrDistanceToPoint(node: SpatialTree<HalfEdgeMesh.HalfEdge>.Node, point: Vec3d): Double {
                    val pt = Vec3d(point.x, point.y, node.bounds.center.z)
                    return super.nodeSqrDistanceToPoint(node, pt)
                }

                override fun itemSqrDistanceToPoint(tree: SpatialTree<HalfEdgeMesh.HalfEdge>, item: HalfEdgeMesh.HalfEdge, point: Vec3d): Double {
                    return if (computeXyEdgeIntersectionPoint(edge.pt0, edge.pt1, item.from, item.to, intersectionPt)) {
                        val d = intersectionPt.distXy(item.from) / item.to.distXy(item.from)

                        if (isFuzzyEqual(d, 0f) || isFuzzyEqual(d, 1f)) {
                            Double.MAX_VALUE
                        } else {
                            val v = MutableVec3f(item.to).subtract(item.from).mul(d).add(item.from)
                            //delCenters += v
                            splitEdges += item to v
                            0.0
                        }
                    } else {
                        Double.MAX_VALUE
                    }
                }
            }
            return this
        }
    }

    companion object {
        private fun Vec3f.distXy(other: Vec3f): Float {
            val dx = x.toDouble() - other.x.toDouble()
            val dy = y.toDouble() - other.y.toDouble()
            return sqrt(dx * dx + dy * dy).toFloat()
        }

        private fun Vec3f.distXy(other: Vec2f): Float {
            val dx = x.toDouble() - other.x.toDouble()
            val dy = y.toDouble() - other.y.toDouble()
            return sqrt(dx*dx + dy*dy).toFloat()
        }

        fun isInPolygon(point: Vec3f, poly: List<Vec2f>): Boolean {
            // https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html
            var i = 0
            var j = poly.size - 1
            var result = false
            while (i < poly.size) {
                if (poly[i].y > point.y != poly[j].y > point.y &&
                        point.x < (poly[j].x - poly[i].x) * (point.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x) {
                    result = !result
                }
                j = i++
            }
            return result
        }

        private fun computeXyLineIntersectionPoint(e11: Vec3f, e12: Vec3f, e21: Vec3f, e22: Vec3f, result: MutableVec3f): Boolean {
            // http://en.wikipedia.org/wiki/Line-line_intersection
            val x1 = e11.x.toDouble()
            val y1 = e11.y.toDouble()
            val x2 = e12.x.toDouble()
            val y2 = e12.y.toDouble()
            val x3 = e21.x.toDouble()
            val y3 = e21.y.toDouble()
            val x4 = e22.x.toDouble()
            val y4 = e22.y.toDouble()

            val denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
            if (!denom.isFuzzyZero()) {
                // lines are not parallel
                val a = x1 * y2 - y1 * x2
                val b = x3 * y4 - y3 * x4
                val x = (a * (x3 - x4) - b * (x1 - x2)) / denom
                val y = (a * (y3 - y4) - b * (y1 - y2)) / denom
                result.set(x.toFloat(), y.toFloat(), 0f)
                return true
            }
            return false
        }

        private fun computeXyEdgeIntersectionPoint(e11: Vec3f, e12: Vec3f, e21: Vec3f, e22: Vec3f, result: MutableVec3f): Boolean {
            if (!computeXyLineIntersectionPoint(e11, e12, e21, e22, result)) {
                return false
            }
            // check if result point is on both edges (between start and end points)
            var dx0 = result.x - e11.x
            var dy0 = result.y - e11.y
            var dx1 = e12.x - e11.x
            var dy1 = e12.y - e11.y
            var dot = dx0 * dx1 + dy0 * dy1
            var l0 = dx0 * dx0 + dy0 * dy0
            var l1 = dx1 * dx1 + dy1 * dy1
            if (dot < 0f || l0 > l1) {
                return false
            }

            dx0 = result.x - e21.x
            dy0 = result.y - e21.y
            dx1 = e22.x - e21.x
            dy1 = e22.y - e21.y
            dot = dx0 * dx1 + dy0 * dy1
            l0 = dx0 * dx0 + dy0 * dy0
            l1 = dx1 * dx1 + dy1 * dy1

            return dot > 0 && l0 < l1
        }
    }
}
