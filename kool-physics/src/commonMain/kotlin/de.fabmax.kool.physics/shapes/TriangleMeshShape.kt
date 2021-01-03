package de.fabmax.kool.physics.shapes

import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder

expect class TriangleMeshShape(geometry: IndexedVertexList) : CommonTriangleMeshShape, CollisionShape

abstract class CommonTriangleMeshShape(val geometry: IndexedVertexList) {

    open fun generateGeometry(target: MeshBuilder) {
        target.geometry.addGeometry(this.geometry)
    }

}
