package de.fabmax.kool.modules.mesh.simplification

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.modules.mesh.HalfEdgeMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.toString
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.PriorityQueue
import de.fabmax.kool.util.logD
import kotlin.math.max

fun Mesh.simplify(termCrit: TermCriterion) {
    meshData.simplify(termCrit)
}

fun MeshData.simplify(termCrit: TermCriterion) {
    HalfEdgeMesh(this, HalfEdgeMesh.ListEdgeHandler()).simplify(termCrit)
}

fun HalfEdgeMesh.simplify(termCrit: TermCriterion) {
    MeshSimplifier(termCrit).simplifyMesh(this)
}

class MeshSimplifier(val termCrit: TermCriterion, val quality: Float = 3f, val collapseStrategy: CollapseStrategy = defaultCollapseStrategy()) {

    private class CollapseCandidate(var error: Float, val edge: HalfEdgeMesh.HalfEdge)

    private val candidates = PriorityQueue<CollapseCandidate>(Comparator { a, b -> a.error.compareTo(b.error) })
    private val quadrics = mutableMapOf<Int, ErrorQuadric>()
    private val tmpVec = MutableVec3f()


    fun simplifyMesh(mesh: HalfEdgeMesh, generateNormals: Boolean = true, generateTangents: Boolean = true) {
        logD { "Simplifying mesh: ${mesh.faceCount} faces / ${mesh.vertCount} vertices..." }

        val perf = PerfTimer()
        rebuildCollapseQueue(mesh)
        termCrit.init(mesh)

        var rebuildQueue = (mesh.faceCount / quality).toInt()
        var lastError = 0.0f
        while (candidates.isNotEmpty() && candidates.peek().error < Float.MAX_VALUE) {
            if (--rebuildQueue <= 0) {
                rebuildQueue = max(10, (mesh.faceCount / quality).toInt())
                rebuildCollapseQueue(mesh)
            }

            val candidate = candidates.poll()
            if (candidate.edge.isDeleted) {
                // edge was already deleted by previous simplification operations
                continue
            }

            val q1 = quadrics[candidate.edge.from.index]!!
            val q2 = quadrics[candidate.edge.to.index]!!

            // update error (might has changed because of previous operations)
            candidate.error = collapseStrategy.computeCollapsePosition(q1, q2, tmpVec)
            if (candidate.error > candidates.peek().error) {
                // update error is greater than next best candidate's error, re-add it to the queue and try next
                candidates += candidate
            } else if (candidate.error < Float.MAX_VALUE) {
                // collapse edge
                quadrics -= q2.vertex.index
                q1.consume(q2)
                mesh.collapseEdge(candidate.edge, 0f)
                q1.vertex.updatePosition(tmpVec)
                lastError = candidate.error

                if (termCrit.isFinished(mesh, lastError)) {
                    // termination criterion stopped simplification
                    break
                }
            } else {
                // no more collapse candidates
                logD { "No more collapsable edges" }
                break
            }
        }

        quadrics.clear()
        candidates.clear()
        mesh.rebuild(generateNormals, generateTangents)

        logD { "Mesh simplification done! ${mesh.faceCount} faces / ${mesh.vertCount} vertices remain, last error: $lastError, took ${perf.takeSecs().toString(3)} s" }
    }

    fun rebuildCollapseQueue(mesh: HalfEdgeMesh) {
        candidates.clear()
        for (edge in mesh.edgeHandler) {
            // only add one half edge per edge
            if (edge.from.index < edge.to.index || edge.opp == null) {
                val q1 = quadrics.getOrPut(edge.from.index) { ErrorQuadric(edge.from) }
                val q2 = quadrics.getOrPut(edge.to.index) { ErrorQuadric(edge.to) }

                //if (!isEdgeVertex(q1.vertex) && !isEdgeVertex(q2.vertex)) {
                    val err = collapseStrategy.computeCollapsePosition(q1, q2, tmpVec)
                    candidates += CollapseCandidate(err, edge)
                //}
            }
        }
    }

    private fun isEdgeVertex(v: HalfEdgeMesh.HalfEdgeVertex): Boolean {
        for (i in v.edges.indices) {
            if (v.edges[i].opp == null) {
                return true
            }
        }
        return false
    }
}
