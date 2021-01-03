package de.fabmax.kool.physics.shapes

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.PrimitiveType
import java.nio.ByteBuffer

@Suppress("CanBeParameter")
actual class TriangleMeshShape actual constructor(geometry: IndexedVertexList) : CommonTriangleMeshShape(geometry), CollisionShape {

    override val btShape: BvhTriangleMeshShape

    init {
        if (geometry.primitiveType != PrimitiveType.TRIANGLES) {
            throw IllegalArgumentException("Supplied geometry must have primitive type TRIANGLES")
        }

        val vertices = ByteBuffer.allocate(geometry.numVertices * 12)
        vertices.asFloatBuffer().apply {
            geometry.forEach {
                put(it.x)
                put(it.y)
                put(it.z)
            }
        }
        val indices = ByteBuffer.allocate(geometry.numIndices * 4)
        indices.asIntBuffer().apply {
            for (i in 0 until geometry.numIndices) {
                put(geometry.indices[i])
            }
        }
        val indexedVertexArray = TriangleIndexVertexArray(geometry.numPrimitives, indices, 12,
            geometry.numVertices, vertices, 12)
        btShape = BvhTriangleMeshShape(indexedVertexArray, true)
    }

}