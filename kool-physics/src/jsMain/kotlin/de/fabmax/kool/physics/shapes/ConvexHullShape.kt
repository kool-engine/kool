package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btConvexHullShape
import ammo.toBtVector3
import ammo.toVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList

actual class ConvexHullShape actual constructor(points: List<Vec3f>) : CommonConvexHullShape(points), CollisionShape {

    override val btShape: btConvexHullShape

    override val geometry: IndexedVertexList

    init {
        Physics.checkIsLoaded()

        // create temporary ConvexHullShape from supplied points
        val tmpShape = Ammo.btConvexHullShape()
        for (pt in points) {
            tmpShape.addPoint(pt.toBtVector3(), false)
        }
        tmpShape.recalcLocalAabb()
        tmpShape.setMargin(0f)

        // build convex hull from temp shape
        // -> reduces number of vertices if supplied points contain non-hull vertices
        val hull = Ammo.btShapeHull(tmpShape)
        hull.buildHull(0f)

        // create final ConvexHullShape containing only hull vertices
        //shape = tmpShape
        btShape = Ammo.btConvexHullShape()
        val nVerts = hull.numVertices()
        for (i in 0 until nVerts) {
            btShape.addPoint(hull.getVertexAt(i), false)
        }
        btShape.recalcLocalAabb()
        btShape.setMargin(0f)

        // build triangle mesh from convex hull
        geometry = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)
        val nIndices = hull.numIndices()
        for (i in 0 until nIndices step 3) {
            val i0 = geometry.addVertex(hull.getVertexAt(hull.getIndexAt(i)).toVec3f())
            val i1 = geometry.addVertex(hull.getVertexAt(hull.getIndexAt(i + 1)).toVec3f())
            val i2 = geometry.addVertex(hull.getVertexAt(hull.getIndexAt(i + 2)).toVec3f())
            geometry.addTriIndices(i0, i1, i2)
        }
        geometry.generateNormals()
    }

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

}