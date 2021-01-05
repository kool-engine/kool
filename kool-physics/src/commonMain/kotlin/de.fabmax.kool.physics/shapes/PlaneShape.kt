package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.*
import de.fabmax.kool.util.MeshBuilder

expect class PlaneShape(planeNormal: Vec3f, planeConstant: Float) : CommonPlaneShape, CollisionShape {
    constructor(plane: Plane)
}

abstract class CommonPlaneShape(val planeNormal: Vec3f, val planeConstant: Float) {

    open fun generateGeometry(target: MeshBuilder) {
        val bx = if (planeNormal * Vec3f.X_AXIS < planeNormal * Vec3f.Y_AXIS) {
            MutableVec3f(1f, 0f, 0f)
        } else {
            MutableVec3f(0f, 1f, 0f)
        }
        val bz = bx.cross(planeNormal, MutableVec3f())
        planeNormal.cross(bz, bx)

        val base = Mat3f()
        base.setColVec(0, bx)
        base.setColVec(1, planeNormal)
        base.setColVec(2, bz)

        // plane is infinitely large, generate a mesh with a reasonable size
        target.apply {
            withTransform {
                rotate(90f, Vec3f.NEG_X_AXIS)
                transform.mul(Mat4f().setRotation(base))
                rect {
                    size.set(1000f, 1000f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                }
            }
        }

    }

}
