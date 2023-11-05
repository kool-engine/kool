package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.scene.geometry.IndexedVertexList

expect fun TriangleMesh(geometry: IndexedVertexList): TriangleMesh

interface TriangleMesh : Releasable {
    val geometry: IndexedVertexList

    var releaseWithGeometry: Boolean

    override fun release()
}
