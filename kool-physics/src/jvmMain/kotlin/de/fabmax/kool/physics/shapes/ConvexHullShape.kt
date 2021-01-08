package de.fabmax.kool.physics.shapes

import com.bulletphysics.collision.shapes.ShapeHull
import com.bulletphysics.util.ObjectArrayList
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.BtConvexHullShape
import de.fabmax.kool.physics.toBtVector3f
import de.fabmax.kool.physics.toVec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList
import javax.vecmath.Vector3f

actual class ConvexHullShape actual constructor(points: List<Vec3f>) : CommonConvexHullShape(points), CollisionShape {

    override val btShape: BtConvexHullShape

    override val geometry: IndexedVertexList

    init {
        // create temporary ConvexHullShape from supplied points
        val btPoints = ObjectArrayList<Vector3f>(points.size)
        points.forEach {
            btPoints.add(it.toBtVector3f())
        }
        val tmpShape = BtConvexHullShape(btPoints)
        tmpShape.margin = 0f

        // build convex hull from temp shape
        // -> reduces number of vertices if supplied points contain non-hull vertices
        val hull = ShapeHull(tmpShape)
        hull.buildHull(0f)

        // create final ConvexHullShape containing only hull vertices
        btShape = BtConvexHullShape(hull.vertexPointer)
        btShape.margin = 0f

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

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f) = getBtInertia(mass, result)
}