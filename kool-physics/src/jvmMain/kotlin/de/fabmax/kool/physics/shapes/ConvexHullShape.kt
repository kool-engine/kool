package de.fabmax.kool.physics.shapes

import com.bulletphysics.collision.shapes.ShapeHull
import com.bulletphysics.util.ObjectArrayList
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics.toVec3f
import de.fabmax.kool.physics.Physics.toVector3f
import de.fabmax.kool.physics.btConvexHullShape
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.IndexedVertexList
import javax.vecmath.Vector3f

@Suppress("CanBeParameter")
actual class ConvexHullShape actual constructor(actual val points: List<Vec3f>) : CollisionShape() {

    override val shape: btConvexHullShape

    actual val geometry: IndexedVertexList

    init {
        // create temporary ConvexHullShape from supplied points
        val btPoints = ObjectArrayList<Vector3f>(points.size)
        points.forEach {
            btPoints.add(it.toVector3f())
        }
        val tmpShape = btConvexHullShape(btPoints)
        tmpShape.margin = 0f

        // build convex hull from temp shape
        // -> reduces number of vertices if supplied points contain non-hull vertices
        val hull = ShapeHull(tmpShape)
        hull.buildHull(0f)

        // create final ConvexHullShape containing only hull vertices
        shape = btConvexHullShape(hull.vertexPointer)
        shape.margin = 0f

        // build triangle mesh from convex hull
        geometry = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)
        val indices = hull.indexPointer
        val verts = hull.vertexPointer
        for (i in 0 until indices.size() step 3) {
            val i0 = geometry.addVertex(verts[indices[i]].toVec3f())
            val i1 = geometry.addVertex(verts[indices[i + 1]].toVec3f())
            val i2 = geometry.addVertex(verts[indices[i + 2]].toVec3f())
            geometry.addTriIndices(i0, i1, i2)
        }
        geometry.generateNormals()
    }

}