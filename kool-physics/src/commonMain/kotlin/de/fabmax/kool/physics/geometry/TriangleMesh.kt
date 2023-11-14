package de.fabmax.kool.physics.geometry

import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Releasable

expect fun TriangleMesh(geometry: IndexedVertexList): TriangleMesh

interface TriangleMesh : Releasable {
    val geometry: IndexedVertexList

    var releaseWithGeometry: Boolean
}
