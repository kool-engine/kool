package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.util.IndexedVertexList

expect class TriangleMesh(geometry: IndexedVertexList) : Releasable {
    val geometry: IndexedVertexList

    var releaseWithGeometry: Boolean
}
