package de.fabmax.kool.modules.mesh

import de.fabmax.kool.math.*
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.util.Edge
import de.fabmax.kool.util.SpatialTree
import de.fabmax.kool.util.SpatialTreeTraverser
import de.fabmax.kool.util.logD
import kotlin.math.max
import kotlin.math.sqrt

class MeshCutXy(val meshData: MeshData) {

    private val ocTreeHandler = OcTreeEdgeHandler(meshData)
    private val halfEdgeMesh = HalfEdgeMesh(meshData, ocTreeHandler)

    private val eps: Float = max(1f, meshData.bounds.min.distance(meshData.bounds.max)) / 1e4f
    var shortEdgeThresh: Float

    init {
        var edLen = 0.0
        for (ed in ocTreeHandler) {
            edLen += ed.computeLength()
        }
        shortEdgeThresh = (edLen.toFloat() / ocTreeHandler.numEdges) * 0.2f
    }

    fun cutXy(cutPoly: List<Vec2f>) {
        val splitVerts = splitIntersectingEdges(cutPoly)
        deleteInside(cutPoly, splitVerts)
        fitInsideEdges(cutPoly)
        collapseShortEdges(splitVerts)

        halfEdgeMesh.rebuild(generateNormals = false, generateTangents = false)
    }

    private fun collapseShortEdges(splitVerts: Set<HalfEdgeMesh.HalfEdgeVertex>) {
        val cutBorders = halfEdgeMesh.selectBorders().filter { it.any { e -> e.from in splitVerts } }
        var collapseCnt = 0

        cutBorders.forEach { border ->
            border.forEach { edge ->
                val edgeLen = edge.computeLength()

                if (edgeLen.isFuzzyZero(eps)) {
                    edge.collapse(0f)
                    collapseCnt++

                } else if (edge.computeLength() < shortEdgeThresh) {
                    val nextEdge = edge.to.edges.find { it.opp == null }
                    val e0 = MutableVec2f(edge.to.x - edge.from.x, edge.to.y - edge.from.y).norm()
                    val e1 = MutableVec2f()
                    if (nextEdge != null) {
                        e1.set(nextEdge.to.x - nextEdge.from.x, nextEdge.to.y - nextEdge.from.y).norm()
                    }
                    if (isFuzzyEqual(e0 * e1, 1f)) {
                        edge.collapse(0f)
                        collapseCnt++
                    } else {
                        val prevEdge = edge.from.edges.find { it.next.next.opp == null }?.next?.next
                        e1.set(Vec2f.ZERO)
                        if (prevEdge != null) {
                            e1.set(prevEdge.to.x - prevEdge.from.x, prevEdge.to.y - prevEdge.from.y).norm()
                        }
                        if (isFuzzyEqual(e0 * e1, 1f)) {
                            edge.collapse(1f)
                            collapseCnt++
                        }
                    }
                }
            }
        }
        logD { "Collapsed $collapseCnt short edges" }
    }

    /**
     * split any mesh edge that intersects cutPoly
     */
    private fun splitIntersectingEdges(cutPoly: List<Vec2f>): Set<HalfEdgeMesh.HalfEdgeVertex> {
        val cutEdges = mutableListOf<Edge>()
        for (i in 0 until cutPoly.size) {
            val i1 = (i + 1) % cutPoly.size
            cutEdges += Edge(Vec3f(cutPoly[i].x, cutPoly[i].y, 0f), Vec3f(cutPoly[i1].x, cutPoly[i1].y, 0f))
        }

        val splitVerts = mutableSetOf<HalfEdgeMesh.HalfEdgeVertex>()
        val intersectionTrav = EdgeXyIntersectionTrav()
        for (i in 0..100) {
            var anyCut = false
            for (cutEdge in cutEdges) {
                intersectionTrav.setup(cutEdge).traverse(ocTreeHandler.edgeTree)
                val ce = intersectionTrav.result ?: continue
                val sv = ce.split(0.5f)
                sv.updatePosition(intersectionTrav.intersectionPt)
                splitVerts += sv
                anyCut = true
            }

            if (!anyCut || i == 100) {
                logD { "Edge splitting done, took $i iterations" }
                break
            }
        }
        return splitVerts
    }

    /**
     * delete all triangles (and vertices) inside cutPoly
     */
    private fun deleteInside(cutPoly: List<Vec2f>, excludedVerts: Set<HalfEdgeMesh.HalfEdgeVertex>) {
        var delCnt = 0
        ocTreeHandler.filter {
            isInPolygon(MutableVec3f().add(it.from).add(it.next.from).add(it.next.next.from).scale(1/3f), cutPoly)
        }.forEach {
            if (!it.isDeleted) {
                it.deleteTriangle()
                delCnt++
            }
        }
        logD { "Deleted $delCnt inner triangles" }
    }

    /**
     * split remaining edges inside cutPoly and fit them to it
     */
    private fun fitInsideEdges(cutPoly: List<Vec2f>) {
        val movedVerts = mutableSetOf<Int>()
        val heightTest = MeshRayTest.geometryTest(halfEdgeMesh).apply { onMeshDataChanged(halfEdgeMesh) }
        val rayTest = RayTest().apply {
            ray.origin.set(0f, 0f, halfEdgeMesh.bounds.max.z + 1)
            ray.direction.set(0f, 0f, -1f)
        }
        val insideTrav = EdgeInsidePolyTrav(cutPoly)
        for (i in 0..100) {
            var anyCut = false
            for (i in cutPoly.indices) {
                val pt = cutPoly[i]
                val prev = cutPoly[if (i == 0) cutPoly.lastIndex else i - 1]
                val next = cutPoly[(i + 1) % cutPoly.size]
                insideTrav.setup(pt, prev, next).traverse(ocTreeHandler.edgeTree)

                val ce = insideTrav.closestEdge
                if (ce != null) {
                    rayTest.clear()
                    rayTest.ray.origin.apply {
                        x = insideTrav.splitPos.x
                        y = insideTrav.splitPos.y
                    }
                    heightTest.rayTest(rayTest)
                    if (rayTest.isHit) {
                        insideTrav.splitPos.z = rayTest.hitPosition.z
                    }
                    //println("${insideTrav.minDist}, ${Vec3f(pt.x, pt.y, 0f).distXy(insideTrav.splitPos)}, ${insideTrav.splitPos}")

                    val mvVertex = when {
                        insideTrav.splitPos.distXy(ce.from).isFuzzyZero(eps) -> ce.from
                        insideTrav.splitPos.distXy(ce.to).isFuzzyZero(eps) -> ce.to
                        else -> ce.split(0.5f)
                    }
                    if (mvVertex.index !in movedVerts) {
                        mvVertex.updatePosition(insideTrav.splitPos)
                        movedVerts += mvVertex.index
                        anyCut = true
                    }
                }
            }
            if (!anyCut || i == 100) {
                logD { "Inside fitting done, took $i iterations" }
                break
            }
        }
    }

    private inner class EdgeInsidePolyTrav(val cutPoly: List<Vec2f>) : SpatialTreeTraverser<HalfEdgeMesh.HalfEdge>() {
        val point = MutableVec3f()
        var radiusSqr = 0f

        var minDist = 0f
        var closestEdge: HalfEdgeMesh.HalfEdge? = null
        val splitPos = MutableVec3f()

        fun setup(point: Vec2f, next: Vec2f, prev: Vec2f): EdgeInsidePolyTrav {
            this.point.set(point.x, point.y, 0f)
            radiusSqr = max(point.sqrDistance(next), point.sqrDistance(prev))
            minDist = Float.MAX_VALUE
            closestEdge = null
            splitPos.set(this.point)
            return this
        }

        override fun traverseChildren(tree: SpatialTree<HalfEdgeMesh.HalfEdge>, node: SpatialTree<HalfEdgeMesh.HalfEdge>.Node) {
            for (c in node.children) {
                point.z = c.bounds.center.z
                if (c.bounds.pointDistanceSqr(point) < radiusSqr) {
                    traverseNode(tree, c)
                }
            }
        }

        override fun traverseLeaf(tree: SpatialTree<HalfEdgeMesh.HalfEdge>, leaf: SpatialTree<HalfEdgeMesh.HalfEdge>.Node) {
            val splitPt = MutableVec3f()

            for (i in leaf.nodeRange) {
                val e = leaf.items[i]
                if (e.opp == null) {
                    val c = MutableVec3f(e.to).subtract(e.from).scale(0.5f).add(e.from)
                    if (isInPolygon(c, cutPoly)) {
                        point.nearestPointOnEdge(e.from, e.to, splitPt)
                        val splitD = splitPt.distXy(point)
                        if (!splitPt.isFuzzyEqual(e.from, eps) && !splitPt.isFuzzyEqual(e.to, eps) && splitD < minDist && !splitD.isFuzzyZero(eps)) {
                            minDist = splitD
                            closestEdge = e
                            splitPos.z = splitPt.z
                        }
                    }
                }
            }
        }
    }

    private inner class EdgeXyIntersectionTrav : SpatialTreeTraverser<HalfEdgeMesh.HalfEdge>() {
        private lateinit var edge: Edge
        private val edgeRayXy = Ray()
        private var edgeLenXy = 0f
        var result: HalfEdgeMesh.HalfEdge? = null
        val intersectionPt = MutableVec3f()

        fun setup(edge: Edge): EdgeXyIntersectionTrav {
            this.edge = edge
            result = null
            intersectionPt.set(Vec3f.ZERO)

            edgeRayXy.origin.set(edge.pt0).apply { z = 0f }
            edgeRayXy.direction.set(edge.pt1).subtract(edge.pt0).apply { z = 0f }
            edgeLenXy = edgeRayXy.direction.length()
            edgeRayXy.direction.norm()

            return this
        }

        override fun traverseChildren(tree: SpatialTree<HalfEdgeMesh.HalfEdge>, node: SpatialTree<HalfEdgeMesh.HalfEdge>.Node) {
            node.children.forEach {
                edgeRayXy.origin.z = it.bounds.center.z
                if (result == null && sqrt(it.bounds.hitDistanceSqr(edgeRayXy)) < edgeLenXy) {
                    traverseNode(tree, it)
                }
            }
        }

        override fun traverseLeaf(tree: SpatialTree<HalfEdgeMesh.HalfEdge>, leaf: SpatialTree<HalfEdgeMesh.HalfEdge>.Node) {
            for (i in leaf.nodeRange) {
                val item = leaf.items[i]
                if (computeXyEdgeIntersectionPoint(edge.pt0, edge.pt1, item.from, item.to, intersectionPt) &&
                        !intersectionPt.distXy(item.from).isFuzzyZero(eps) && !intersectionPt.distXy(item.to).isFuzzyZero(eps)) {
                    result = item
                    val f = intersectionPt.distXy(item.from) / item.to.distXy(item.from)
                    intersectionPt.z = item.from.z + (item.to.z - item.from.z) * f
                    break
                }
            }
        }

    }

    companion object {
        private fun Vec3f.distXy(other: Vec3f): Float {
            val dx = x - other.x
            val dy = y - other.y
            return sqrt(dx * dx + dy * dy)
        }

        private fun isInPolygon(point: Vec3f, poly: List<Vec2f>): Boolean {
            // based on: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
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
            val x1 = e11.x
            val y1 = e11.y
            val x2 = e12.x
            val y2 = e12.y
            val x3 = e21.x
            val y3 = e21.y
            val x4 = e22.x
            val y4 = e22.y

            val denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
            if (!denom.isFuzzyZero()) {
                // lines are not parallel
                val a = x1 * y2 - y1 * x2
                val b = x3 * y4 - y3 * x4
                val x = (a * (x3 - x4) - b * (x1 - x2)) / denom
                val y = (a * (y3 - y4) - b * (y1 - y2)) / denom
                result.set(x, y, 0f)
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
