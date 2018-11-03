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

fun Mesh.simplify(termCrit: TermCriterion, keepBorders: Boolean = false) {
    meshData.simplify(termCrit, keepBorders)
}

fun MeshData.simplify(termCrit: TermCriterion, keepBorders: Boolean = false) {
    HalfEdgeMesh(this, HalfEdgeMesh.ListEdgeHandler()).simplify(termCrit, keepBorders)
}

fun HalfEdgeMesh.simplify(termCrit: TermCriterion, keepBorders: Boolean = false) {
    MeshSimplifier(termCrit, keepBorders).simplifyMesh(this)
}

class MeshSimplifier(val termCrit: TermCriterion, val keepBorders: Boolean = false, val quality: Float = 3f, val collapseStrategy: CollapseStrategy = defaultCollapseStrategy()) {

    val quadrics = mutableMapOf<Int, ErrorQuadric>()
    private val candidates = PriorityQueue<CollapseCandidate>(Comparator { a, b -> a.error.compareTo(b.error) })
    private val tmpVec = MutableVec3f()

    private lateinit var mesh: HalfEdgeMesh

    fun simplifyMesh(mesh: HalfEdgeMesh, generateNormals: Boolean = true, generateTangents: Boolean = true) {
        logD { "Simplifying mesh: ${mesh.faceCount} faces / ${mesh.vertCount} vertices..." }

        quadrics.clear()
        candidates.clear()

        val perf = PerfTimer()
        rebuildCollapseQueue(mesh)
        termCrit.init(mesh)
        this.mesh = mesh

        var rebuildQueue = (mesh.faceCount / quality).toInt()
        var lastError = 0.0f

        while (candidates.isNotEmpty() && candidates.peek().error < Float.MAX_VALUE && !termCrit.isFinished(mesh, lastError)) {
            if (--rebuildQueue <= 0) {
                rebuildQueue = max(10, (mesh.faceCount / quality).toInt())
                rebuildCollapseQueue(mesh)
            }

            val candidate = candidates.poll()
            if (candidate.edge.isDeleted || candidate.q1.isDeleted || candidate.q2.isDeleted) {
                // edge was already modified by previous simplification operations
                continue
            }

            // update error (might has changed because of previous operations)
            candidate.updateCollapsePosAndError()
            if (candidate.error > candidates.peek().error) {
                // updated error is greater than next best candidate's error, re-insert into queue and try next
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

    fun rebuildCollapseQueue(mesh: HalfEdgeMesh) {
        candidates.clear()
        for (edge in mesh.edgeHandler) {
            // only add one half edge per edge
            if (edge.from.index < edge.to.index || edge.opp == null) {
                val q1 = quadrics.getOrPut(edge.from.index) { ErrorQuadric(edge.from) }
                val q2 = quadrics.getOrPut(edge.to.index) { ErrorQuadric(edge.to) }

                if (!keepBorders || (!q1.isBorder && !q2.isBorder)) {
                    candidates += CollapseCandidate(edge, q1, q2)
                }
            }
        }
    }

    private inner class CollapseCandidate(val edge: HalfEdgeMesh.HalfEdge, val q1: ErrorQuadric, val q2: ErrorQuadric) {
        var error = 0f

        init {
            updateCollapsePosAndError()
        }

        fun updateCollapsePosAndError() {
            error = collapseStrategy.computeCollapsePosition(q1, q2, tmpVec)
        }

        fun collapse(): Float {
            //quadrics -= q2.vertex.index
            val rem = quadrics.remove(q2.vertex.index)
            if (rem !== q2) {
                throw IllegalStateException("Quadric removal failed!")
            }
            q1.consume(q2)
            mesh.collapseEdge(edge, 0f)
            q1.vertex.updatePosition(tmpVec)
            return error
        }
    }
}
