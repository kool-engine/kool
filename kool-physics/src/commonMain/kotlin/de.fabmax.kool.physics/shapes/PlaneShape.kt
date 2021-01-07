package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.MeshBuilder

/**
 * Plane collision shape. Plane normal is x-axis, origin is (0, 0, 0).
 */
expect class PlaneShape() : CommonPlaneShape, CollisionShape

abstract class CommonPlaneShape {

    open fun generateGeometry(target: MeshBuilder) {
        // plane is infinitely large, generate a mesh with a reasonable size
        target.apply {
            withTransform {
                rotate(90f, Vec3f.Z_AXIS)
                rect {
                    size.set(1000f, 1000f)
                    origin.set(size.x * -0.5f, size.y * -0.5f, 0f)
                }
            }
        }
    }

}
