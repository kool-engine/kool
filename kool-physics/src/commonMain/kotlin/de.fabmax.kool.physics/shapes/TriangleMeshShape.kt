package de.fabmax.kool.physics.shapes

import de.fabmax.kool.util.IndexedVertexList

expect class TriangleMeshShape(geometry: IndexedVertexList) : CollisionShape {
    val geometry: IndexedVertexList
}