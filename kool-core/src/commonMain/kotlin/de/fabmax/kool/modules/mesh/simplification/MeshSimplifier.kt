package de.fabmax.kool.modules.mesh.simplification

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.modules.mesh.HalfEdgeMesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.toString
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.TreeMap
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE

fun MeshData.simplify(termCrit: TermCriterion) {
    HalfEdgeMesh(this).simplify(termCrit, emptySet())
}

fun HalfEdgeMesh.simplify(termCrit: TermCriterion, excludedEdges: Set<HalfEdgeMesh.HalfEdge> = emptySet()) {
    MeshSimplifier(termCrit).apply { this.excludedEdges += excludedEdges }.simplifyMesh(this)
}

open class MeshSimplifier(val termCrit: TermCriterion, val collapseStrategy: CollapseStrategy = defaultCollapseStrategy()) {

    val quadrics = mutableMapOf<Int, ErrorQuadric>()
    private val candidates = CollapseCandidates()
    private val candidateMap = mutableMapOf<EdgeId, CollapseCandidate>()

    private lateinit var mesh: HalfEdgeMesh
    val excludedEdges = mutableSetOf<HalfEdgeMesh.HalfEdge>()
    var keepBorders = false

    fun simplifyMesh(mesh: HalfEdgeMesh, generateNormals: Boolean = true, generateTangents: Boolean = true) {
        logD { "Simplifying mesh: ${mesh.faceCount} faces / ${mesh.vertCount} vertices..." }

        quadrics.clear()
        candidates.clear()

        val perf = PerfTimer()
        rebuildCollapseQueue(mesh)
        termCrit.init(mesh)
        this.mesh = mesh

        var lastError = 0.0

        while (candidates.isNotEmpty() && candidates.peek().error < Double.MAX_VALUE && !termCrit.isFinished(mesh, lastError)) {
            val candidate = candidates.poll()
            if (candidate.edge.isDeleted || candidate.q1.isDeleted || candidate.q2.isDeleted) {
                logE { "invalid edge: already deleted!" }
                continue
            }

            // update error (might has changed because of previous operations)
            val oldError = candidate.error
            candidate.updateCollapsePosAndError()
            if (oldError != candidate.error) {
                // fixme: this should not happen since all affected edges are updated after each collapse operation
                // however, somehow this still happens
                rebuildCollapseQueue(mesh)
                continue
            }

            if (candidate.error > candidates.peek().error) {
                // updated error is greater than next best candidate's error, re-insert into queue and try next
                logE { "next error is less!" }
                candidates += candidate

            } else if (candidate.error < Float.MAX_VALUE) {
                // collapse edge
                lastError = candidate.collapse()

            } else {
                // no more collapse candidates
                logD { "No more collapsable edges" }
                break
            }
        }

        mesh.rebuild(generateNormals, generateTangents)

        logD { "Mesh simplification done! ${mesh.faceCount} faces / ${mesh.vertCount} vertices remain, last error: $lastError, took ${perf.takeSecs().toString(3)} s" }
    }

    private fun rebuildCollapseQueue(mesh: HalfEdgeMesh) {
        candidates.clear()
        candidateMap.clear()

//        val q = mutableListOf<ErrorQuadric>()
//        quadrics.values.forEach { q += it }
//        quadrics.clear()
//        mesh.rebuild(false, false)
//        q.forEach { quadrics[it.vertex.index] = it }

        for (edge in mesh.edgeHandler) {
            insertEdge(edge)
        }
    }

    private fun insertEdge(edge: HalfEdgeMesh.HalfEdge) {
        // only add one half edge per edge
        if (edge !in excludedEdges && (edge.from.index < edge.to.index || edge.opp == null)) {
            val q1 = quadrics.getOrPut(edge.from.index) { ErrorQuadric(edge.from) }
            val q2 = quadrics.getOrPut(edge.to.index) { ErrorQuadric(edge.to) }

            if (!keepBorders || (!q1.isBorder && !q2.isBorder)) {
                val c = CollapseCandidate(edge, q1, q2)
                candidates += c
                candidateMap[EdgeId(edge.from.index, edge.to.index)] = c
            }
        }
    }

    private fun collectAffectedEdges(v: HalfEdgeMesh.HalfEdgeVertex, result: MutableSet<HalfEdgeMesh.HalfEdge>) {
        // collect all collapse candidates adjacent to given vertex
        v.edges.forEach { ed ->
            result += ed
            result += ed.next.next
            result += ed.next
            ed.next.opp?.let { result += it }
        }
    }

    private inner class CollapseCandidate(val edge: HalfEdgeMesh.HalfEdge, val q1: ErrorQuadric, val q2: ErrorQuadric) {
        var error = 0.0
        val collapsePos = MutableVec3f()

        init {
            updateCollapsePosAndError()
        }

        fun updateCollapsePosAndError() {
            error = collapseStrategy.computeCollapsePosition(q1, q2, collapsePos)
        }

        fun collapse(): Double {
            val rem = quadrics.remove(q2.vertex.index)
            if (rem !== q2) {
                throw IllegalStateException("Quadric removal failed!")
            }

            val affectedEdges = mutableSetOf<HalfEdgeMesh.HalfEdge>()
            collectAffectedEdges(q1.vertex, affectedEdges)
            collectAffectedEdges(q2.vertex, affectedEdges)
            affectedEdges.forEach {
                candidateMap.remove(EdgeId(it.from.index, it.to.index))?.let { c ->
                    candidates -= c
                }
            }

            q1.consume(q2)
            edge.collapse(0f)
            q1.vertex.updatePosition(collapsePos)

            affectedEdges.clear()
            collectAffectedEdges(q1.vertex, affectedEdges)
            affectedEdges.forEach {
                insertEdge(it)
            }

            return error
        }

    }

    private data class EdgeId(val fromId: Int, val toId: Int)

    private class CollapseCandidates : TreeMap<Double, MutableList<CollapseCandidate>>() {
        fun isNotEmpty() = !isEmpty()

        fun peek(): CollapseCandidate = firstValue().first()

        fun poll(): CollapseCandidate {
            val v = firstValue()
            val c = v.removeAt(v.lastIndex)
            if (v.isEmpty()) {
                remove(firstKey())
            }
            return c
        }

        operator fun plusAssign(c: CollapseCandidate) {
            var v = get(c.error)
            if (v == null) {
                v = mutableListOf()
                put(c.error, v)
            }
            v.add(c)
        }

        operator fun minusAssign(c: CollapseCandidate) {
            val v = get(c.error)
            if (v != null) {
                v.remove(c)
                if (v.isEmpty()) {
                    remove(c.error)
                }
            }
        }
    }
}
