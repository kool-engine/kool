package de.fabmax.kool.util

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.*
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

object PolyUtil {

    fun fillPolygon(vertices: List<Vec3f>, holes: List<List<Vec3f>> = emptyList()): TriangulatedPolygon {
        val n = computeNormal(vertices)
        var xyPoints = projectXy(vertices, n)
        val area = xyArea(xyPoints)
        if (area < 0) {
            n.mul(-1f)
            xyPoints = projectXy(vertices, n)
        }
        val projHoles = holes.map { projectXy(it, n) }
        val impl = Triangulator(xyPoints, projHoles)
        val points = mutableListOf<Vec3f>()
        points += vertices
        holes.forEach { points += it }

        val indices = mutableListOf<Int>()
        impl.triangles.forEach { indices += it.v1.index; indices += it.v2.index; indices += it.v3.index }
        return TriangulatedPolygon(points.toTypedArray(), indices.toIntArray(), n)
    }

    fun computeNormal(points: List<Vec3f>): MutableVec3f {
        val noDuplicates = mutableListOf(points[0])
        points.forEach { p ->
            if (!p.distance(noDuplicates.last()).isFuzzyZero()) {
                noDuplicates += p
            }
        }

        val e0 = MutableVec3f(noDuplicates[1]).subtract(noDuplicates[0]).norm()
        val e1 = MutableVec3f(noDuplicates[2]).subtract(noDuplicates[1]).norm()
        var i = 2
        while (abs(e1.dot(e0)) > 0.8 && i < noDuplicates.size) {
            e1.set(noDuplicates[(i + 1) % noDuplicates.size]).subtract(noDuplicates[i++]).norm()
        }
        return e0.cross(e1, MutableVec3f()).norm()
    }

    fun projectXy(points: List<Vec3f>, n: Vec3f): List<Vec3f> {
        val b1 = if (abs(n.dot(Vec3f.X_AXIS)) < 0.7) MutableVec3f(Vec3f.X_AXIS) else MutableVec3f(Vec3f.Z_AXIS)
        val b2 = n.cross(b1, MutableVec3f()).norm()
        b2.cross(n, b1).norm()
        return points.map { p -> Vec3f(p.dot(b1), p.dot(b2), 0f) }
    }

    fun xyArea(xyPoints: List<Vec3f>): Float {
        var area = 0f
        for (i in xyPoints.indices) {
            val p1 = xyPoints[i]
            val p2 = xyPoints[(i + 1) % xyPoints.size]
            area += p1.x * p2.y - p1.y * p2.x
        }
        return area * 0.5f
    }

    private fun interiorAngle(v1: PolyVertex, v2: PolyVertex, v3: PolyVertex): Float {
        val aPrev = atan2(v1.y - v2.y, v1.x - v2.x)
        val aNext = atan2(v3.y - v2.y, v3.x - v2.x)
        val a = (aPrev - aNext).toDeg()
        return if (a < 0) a + 360f else a
    }

    class TriangulatedPolygon(val vertices: Array<Vec3f>, val indices: IntArray, val normal: Vec3f) {
        val numTriangles: Int
            get() = indices.size / 3
    }

    private class Triangulator(xyPoints: List<Vec3f>, holes: List<List<Vec3f>>) {
        val outerRing: PolyRing
        val innerRings = mutableListOf<PolyRing>()

        val verts = mutableListOf<PolyVertex>()

        val vertTree: KdTree<PolyVertex>
        val edgeTree: KdTree<PolyEdge>
        val vertInTriTrav = VertsInTriTraverser()
        val edgeIntersectionTrav = EdgeIntersectTraverser()

        val triangles = mutableListOf<PolyTri>()
        val triEdges = mutableMapOf<Long, PolyTri>()

        init {
            var iVertex = 0
            outerRing = PolyRing(xyPoints.map { p -> PolyVertex(iVertex++, p) })
            holes.forEach { h -> innerRings += PolyRing(h.map { p -> PolyVertex(iVertex++, p) }) }

            verts += outerRing.points
            innerRings.forEach { h -> verts += h.points }

            val edges = mutableListOf<PolyEdge>()
            edges.addRingEdges(outerRing)
            innerRings.forEach { edges.addRingEdges(it) }

            edgeTree = KdTree(edges, EdgeAdapter())
            vertTree = KdTree(verts, Vec3fAdapter())

            innerRings.forEach { hole -> insertBridgeEdge(hole) }
            verts.forEach { it.updateIsEar() }

            var nTris = verts.size - 2
            while (nTris > 0) {
                val ear = nextEarTip()
                if (ear == null) {
                    logE { "Triangulation failed: No more ear clipping candidates" }
                    break
                }
                clip(ear)
                nTris--
            }
        }

        private fun MutableList<PolyEdge>.addRingEdges(ring: PolyRing) {
            for (i in ring.points.indices) {
                this += PolyEdge(ring.points[i], ring.points[(i + 1) % ring.points.size])
            }
        }

        private fun insertBridgeEdge(innerRing: PolyRing) {
            var bridgeCandidate: Edge<PolyVertex>? = null

            for (i in innerRing.points.indices) {
                for (j in outerRing.points.indices) {
                    val edge = Edge(innerRing.points[i], outerRing.points[j])
                    if ((bridgeCandidate == null || edge.length < bridgeCandidate.length) && noEdgeIntersect(edge)) {
                        bridgeCandidate = edge
                    }
                }
            }

            if (bridgeCandidate != null) {
                val innerV = bridgeCandidate.pt0
                val innerPrev = innerV.prevEdge.pt0
                val innerCp = PolyVertex(innerV.index, innerV)
                verts += innerCp

                val outerV = bridgeCandidate.pt1
                val outerNext = outerV.nextEdge.pt1
                val outerCp = PolyVertex(outerV.index, outerV)
                verts += outerCp

                PolyEdge(innerPrev, innerCp)
                PolyEdge(outerCp, outerNext)
                PolyEdge(outerV, innerV)
                PolyEdge(innerCp, outerCp)
            } else {
                logE { "No suitable bridge edge for hole insertion" }
            }
        }

        private fun nextEarTip(): PolyVertex? {
            var best: PolyVertex? = null
            verts.forEach { v ->
                if (v.isOpen && v.isEar && (best == null || best!!.interiorAngle > v.interiorAngle)) {
                    best = v
                }
            }
            return best
        }

        private fun clip(ear: PolyVertex) {
            ear.isOpen = false
            val tri = PolyTri(ear.prevEdge.pt0, ear, ear.nextEdge.pt1)
            addTriangle(tri)

            val newEdge = PolyEdge(ear.prevEdge.pt0, ear.nextEdge.pt1)
            newEdge.pt0.updateIsEar()
            newEdge.pt1.updateIsEar()

            if (tri.minA < 30f) {
                trySwap(tri)
            }
        }

        private fun addTriangle(tri: PolyTri) {
            triangles += tri
            triEdges[tri.k1] = tri
            triEdges[tri.k2] = tri
            triEdges[tri.k3] = tri
        }

        private fun removeTriangle(tri: PolyTri) {
            triangles -= tri
            triEdges.remove(tri.k1)
            triEdges.remove(tri.k2)
            triEdges.remove(tri.k3)
        }

        private fun trySwap(tri: PolyTri) {
            val adj = triEdges[tri.longestK.opp()]
            if (adj != null) {
                val quad = when {
                    !adj.containsIndex(tri.v1.index) -> listOf(tri.v1, tri.v2, adj.oppVertex(tri.v2.index, tri.v3.index), tri.v3)
                    !adj.containsIndex(tri.v2.index) -> listOf(tri.v2, tri.v3, adj.oppVertex(tri.v3.index, tri.v1.index), tri.v1)
                    else -> listOf(tri.v3, tri.v1, adj.oppVertex(tri.v1.index, tri.v2.index), tri.v2)
                }

                val swap1: PolyTri
                val swap2: PolyTri
                if (tri.containsIndex(quad[0].index) && tri.containsIndex(quad[1].index) && tri.containsIndex(quad[2].index)) {
                    swap1 = PolyTri(quad[0], quad[1], quad[3])
                    swap2 = PolyTri(quad[3], quad[1], quad[2])
                } else {
                    swap1 = PolyTri(quad[0], quad[1], quad[2])
                    swap2 = PolyTri(quad[0], quad[2], quad[3])
                }

                if (min(swap1.minA, swap2.minA) > min(tri.minA, adj.minA)) {
                    removeTriangle(tri)
                    removeTriangle(adj)
                    addTriangle(swap1)
                    addTriangle(swap2)
                }
            }
        }

        private fun Long.opp(): Long {
            val upper = (this shr 32) and 0xffffffff
            val lower = this and 0xffffffff
            return (lower shl 32) or upper
        }

        private fun PolyVertex.updateIsEar() {
            updateInteriorAngle()
            isEar = if (interiorAngle >= 180f) {
                false
            } else {
                noVertexInside(prevEdge.pt0, this, nextEdge.pt1)
            }
        }

        private fun noVertexInside(v1: PolyVertex, v2: PolyVertex, v3: PolyVertex): Boolean {
            vertInTriTrav.setup(v1, v2, v3).traverse(vertTree)
            return vertInTriTrav.result.isEmpty()
        }

        private fun noEdgeIntersect(candidate: Edge<PolyVertex>): Boolean {
            edgeIntersectionTrav.setup(candidate).traverse(edgeTree)
            return edgeIntersectionTrav.result.isEmpty()
        }
    }

    private class PolyTri(val v1: PolyVertex, val v2: PolyVertex, val v3: PolyVertex) {
        val k1 = (v1.index.toLong() shl 32) or v2.index.toLong()
        val k2 = (v2.index.toLong() shl 32) or v3.index.toLong()
        val k3 = (v3.index.toLong() shl 32) or v1.index.toLong()

        val a1 = interiorAngle(v3, v1, v2)
        val a2 = interiorAngle(v1, v2, v3)
        val a3 = 180f - a1 - a2

        val minA = min(a1, min(a2, a3))

        val longest: Float
        val longestK: Long

        init {
            val d1 = v1.distance(v2)
            val d2 = v2.distance(v3)
            val d3 = v3.distance(v1)
            when {
                d1 > d2 && d1 > d3 -> {
                    longest = d1
                    longestK = k1
                }
                d2 > d1 && d2 > d3 -> {
                    longest = d2
                    longestK = k2
                }
                else -> {
                    longest = d3
                    longestK = k3
                }
            }
        }

        fun oppVertex(ia: Int, ib: Int): PolyVertex {
            return when {
                v1.index != ia && v1.index != ib -> v1
                v2.index != ia && v2.index != ib -> v2
                else -> v3
            }
        }

        fun containsIndex(i: Int) = v1.index == i || v2.index == i || v3.index == i
    }

    private class PolyRing(xyPoints: List<PolyVertex>) {
        val points: List<PolyVertex>

        init {
            val pts = mutableListOf<PolyVertex>()

            val epsilon = 1e-4f
            xyPoints.forEach { p ->
                if (pts.isEmpty() || p.distance(pts.last()) > epsilon) {
                    pts += p
                }
            }
            if (pts.last().distance(pts.first()) < epsilon) {
                pts.removeAt(pts.lastIndex)
            }
            points = pts
        }
    }

    private class PolyEdge(from: PolyVertex, to: PolyVertex) : Edge<PolyVertex>(from, to) {
        init {
            from.nextEdge = this
            to.prevEdge = this
        }
    }

    private class PolyVertex(val index: Int, position: Vec3f): Vec3f(position) {
        var interiorAngle = 0f

        var isEar = false
        var isOpen = true

        lateinit var prevEdge: PolyEdge
        lateinit var nextEdge: PolyEdge

        fun updateInteriorAngle() {
            interiorAngle = interiorAngle(prevEdge.pt0, this, nextEdge.pt1)
        }
    }

    private class VertsInTriTraverser : SpatialTreeTraverser<PolyVertex>() {
        lateinit var v1: PolyVertex
        lateinit var v2: PolyVertex
        lateinit var v3: PolyVertex

        val min = MutableVec2f()
        val max = MutableVec2f()
        val result = mutableListOf<PolyVertex>()

        fun setup(v1: PolyVertex, v2: PolyVertex, v3: PolyVertex): VertsInTriTraverser {
            this.v1 = v1
            this.v2 = v2
            this.v3 = v3
            result.clear()
            min.set(min(v1.x, min(v2.x, v3.x)), min(v1.y, min(v2.y, v3.y)))
            max.set(max(v1.x, max(v2.x, v3.x)), max(v1.y, max(v2.y, v3.y)))
            return this
        }

        override fun traverseChildren(tree: SpatialTree<PolyVertex>, node: SpatialTree<PolyVertex>.Node) {
            for (i in node.children.indices) {
                val child = node.children[i]
                val aabb = child.bounds
                if (min.x <= aabb.max.x && max.x >= aabb.min.x && min.y <= aabb.max.y && max.y >= aabb.min.y) {
                    traverseNode(tree, child)
                }
            }
        }

        override fun traverseLeaf(tree: SpatialTree<PolyVertex>, leaf: SpatialTree<PolyVertex>.Node) {
            for (i in leaf.nodeRange) {
                val it = leaf.itemsUnbounded[i]
                val inBounds = it.x >= min.x && it.x <= max.x && it.y >= min.y && it.y <= max.y
                val isOther = it.index != v1.index && it.index != v2.index && it.index != v3.index

                if (inBounds && isOther && isPointInTriangle(it, v1, v2, v3)) {
                    result += it
                }
            }
        }

        private fun isPointInTriangle(pt: Vec3f, v1: Vec3f, v2: Vec3f, v3: Vec3f): Boolean {
            val d1 = pointEdgeSign(pt, v1, v2)
            val d2 = pointEdgeSign(pt, v2, v3)
            val d3 = pointEdgeSign(pt, v3, v1)
            val hasNeg = d1 < 0f || d2 < 0f || d3 < 0f
            val hasPos = d1 > 0f || d2 > 0f || d3 > 0f
            return !(hasNeg && hasPos)
        }

        private fun pointEdgeSign(p1: Vec3f, p2: Vec3f, p3: Vec3f) =
                (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
    }

    private class EdgeIntersectTraverser : SpatialTreeTraverser<PolyEdge>() {
        lateinit var edge: Edge<PolyVertex>

        val min = MutableVec2f()
        val max = MutableVec2f()
        val result = mutableListOf<PolyEdge>()

        fun setup(edge: Edge<PolyVertex>): EdgeIntersectTraverser {
            this.edge = edge
            result.clear()
            min.set(min(edge.pt0.x, edge.pt1.x), min(edge.pt0.y, edge.pt1.y))
            max.set(max(edge.pt0.x, edge.pt1.x), max(edge.pt0.y, edge.pt1.y))
            return this
        }

        override fun traverseChildren(tree: SpatialTree<PolyEdge>, node: SpatialTree<PolyEdge>.Node) {
            for (i in node.children.indices) {
                val child = node.children[i]
                val aabb = child.bounds
                if (min.x <= aabb.max.x && max.x >= aabb.min.x && min.y <= aabb.max.y && max.y >= aabb.min.y) {
                    traverseNode(tree, child)
                }
            }
        }

        override fun traverseLeaf(tree: SpatialTree<PolyEdge>, leaf: SpatialTree<PolyEdge>.Node) {
            for (i in leaf.nodeRange) {
                val it = leaf.itemsUnbounded[i]
                val minX = min(it.pt0.x, it.pt1.x)
                val minY = min(it.pt0.y, it.pt1.y)
                val maxX = max(it.pt0.x, it.pt1.x)
                val maxY = max(it.pt0.y, it.pt1.y)
                val inBounds = min.x <= maxX && max.x >= minX && min.y <= maxY && max.y >= minY
                if (inBounds && filter(it) && isEdgeIntersect(edge.pt0, edge.pt1, it.pt0, it.pt1)) {
                    result += it
                }
            }
        }


        private fun isEdgeIntersect(e11: Vec3f, e12: Vec3f, e21: Vec3f, e22: Vec3f): Boolean {
            val denom = (e11.x - e12.x) * (e21.y - e22.y) - (e11.y - e12.y) * (e21.x - e22.x)
            if (!denom.isFuzzyZero()) {
                val a = e11.x * e12.y - e11.y * e12.x
                val b = e21.x * e22.y - e21.y * e22.x
                val x = (a * (e21.x - e22.x) - b * (e11.x - e12.x)) / denom
                val y = (a * (e21.y - e22.y) - b * (e11.y - e12.y)) / denom

                val dxi1 = x - e11.x
                val dyi1 = y - e11.y
                val dx1 = e12.x - e11.x
                val dy1 = e12.y - e11.y

                val dxi2 = x - e21.x
                val dyi2 = y - e21.y
                val dx2 = e22.x - e21.x
                val dy2 = e22.y - e21.y

                return dxi1 * dxi1 + dyi1 * dyi1 < dx1 * dx1 + dy1 * dy1
                        && dxi2 * dxi2 + dyi2 * dyi2 < dx2 * dx2 + dy2 * dy2
                        && dxi1 * dx1 + dyi1 * dy1 > 0
                        && dxi2 * dx2 + dyi2 * dy2 > 0
            }
            return false
        }
    }
}