package de.fabmax.kool.physics.geometry

import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.BaseReleasable

expect fun TriangleMesh(geometry: IndexedVertexList): TriangleMesh

abstract class TriangleMesh : BaseReleasable() {
    abstract val geometry: IndexedVertexList

    abstract var releaseWithGeometry: Boolean
}
