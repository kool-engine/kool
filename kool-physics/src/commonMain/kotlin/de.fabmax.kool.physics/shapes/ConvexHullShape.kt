package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.IndexedVertexList

expect class ConvexHullShape(points: List<Vec3f>) : CollisionShape {
    val points: List<Vec3f>
    val geometry: IndexedVertexList
}