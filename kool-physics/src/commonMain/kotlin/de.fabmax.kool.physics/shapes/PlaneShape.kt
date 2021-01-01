package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Plane
import de.fabmax.kool.math.Vec3f

expect class PlaneShape(planeNormal: Vec3f, planeConstant: Float) : CollisionShape {
    val planeNormal: Vec3f
    val planeConstant: Float

    constructor(plane: Plane)
}