package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.BaseReleasable

expect fun ConvexMesh(points: List<Vec3f>): ConvexMesh

abstract class ConvexMesh : BaseReleasable() {
    abstract val points: List<Vec3f>
    abstract val convexHull: IndexedVertexList

    abstract var releaseWithGeometry: Boolean
}