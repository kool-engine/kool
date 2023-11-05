package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.scene.geometry.IndexedVertexList

expect fun ConvexMesh(points: List<Vec3f>): ConvexMesh

interface ConvexMesh : Releasable {
    val points: List<Vec3f>
    val convexHull: IndexedVertexList

    var releaseWithGeometry: Boolean

    override fun release()
}