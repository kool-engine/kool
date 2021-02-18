package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.util.IndexedVertexList

expect class ConvexMesh(points: List<Vec3f>) : Releasable {
    val points: List<Vec3f>
    val convexHull: IndexedVertexList

    var releaseWithGeometry: Boolean
}