package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Plane
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.logW

expect class PlaneShape(planeNormal: Vec3f, planeConstant: Float) : CommonPlaneShape, CollisionShape {
    constructor(plane: Plane)
}

abstract class CommonPlaneShape(val planeNormal: Vec3f, val planeConstant: Float) {

    open fun generateGeometry(target: MeshBuilder) {
        logW { "Plane geometry generation is not yet implemented" }
    }

}
