package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btBvhTriangleMeshShape
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.PrimitiveType

actual class TriangleMeshShape actual constructor(geometry: IndexedVertexList) : CommonTriangleMeshShape(geometry), CollisionShape {

    override val btShape: btBvhTriangleMeshShape

    init {
        if (geometry.primitiveType != PrimitiveType.TRIANGLES) {
            throw IllegalArgumentException("Supplied geometry must have primitive type TRIANGLES")
        }

        Physics.checkIsLoaded()

        val numTris = geometry.numPrimitives
        val indices = IntArray(numTris * 3) { geometry.indices[it] }
        val numVerts = geometry.numVertices
        val verts = FloatArray(numVerts * 3)
        for (i in 0 until geometry.numVertices) {
            geometry.vertexIt.index = i
            verts[i * 3] = geometry.vertexIt.x
            verts[i * 3 + 1] = geometry.vertexIt.y
            verts[i * 3 + 2] = geometry.vertexIt.z
        }

        val mesh = Ammo.btTriangleIndexVertexArray(numTris, indices, 12, numVerts, verts, 12)
        btShape = Ammo.btBvhTriangleMeshShape(mesh, true)
    }
}