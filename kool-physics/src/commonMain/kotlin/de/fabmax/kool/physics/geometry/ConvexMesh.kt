package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Releasable

expect fun ConvexMesh(points: List<Vec3f>): ConvexMesh

interface ConvexMesh : Releasable {
    val points: List<Vec3f>
    val convexHull: IndexedVertexList

    var releaseWithGeometry: Boolean
}