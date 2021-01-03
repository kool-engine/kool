package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder

expect class ConvexHullShape(points: List<Vec3f>) : CommonConvexHullShape, CollisionShape

abstract class CommonConvexHullShape(val points: List<Vec3f>) {

    abstract val geometry: IndexedVertexList

    open fun generateGeometry(target: MeshBuilder) {
        target.apply {
            geometry.addGeometry(this@CommonConvexHullShape.geometry)
        }
    }

}
