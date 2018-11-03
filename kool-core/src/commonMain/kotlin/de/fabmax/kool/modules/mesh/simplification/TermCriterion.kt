package de.fabmax.kool.modules.mesh.simplification

import de.fabmax.kool.modules.mesh.HalfEdgeMesh
import kotlin.math.round

/**
 * Determines when to stop simplification of a mesh.
 *
 * @author fabmax
 */
interface TermCriterion {
    fun init(mesh: HalfEdgeMesh) { }
    fun isFinished(mesh: HalfEdgeMesh, nextError: Float): Boolean
}

fun terminateOnFaceCountRel(factor: Float) = object : TermCriterion {
    var targetFaceCnt = Int.MAX_VALUE
    override fun init(mesh: HalfEdgeMesh) { targetFaceCnt = round(mesh.faceCount * factor).toInt() }
    override fun isFinished(mesh: HalfEdgeMesh, nextError: Float) = mesh.faceCount <= targetFaceCnt
}

fun terminateOnFaceCountAbs(targetFaceCnt: Int) = object : TermCriterion {
    override fun isFinished(mesh: HalfEdgeMesh, nextError: Float) = mesh.faceCount <= targetFaceCnt
}

fun terminateOnError(targetError: Double) = object : TermCriterion {
    override fun isFinished(mesh: HalfEdgeMesh, nextError: Float) = nextError > targetError
}
