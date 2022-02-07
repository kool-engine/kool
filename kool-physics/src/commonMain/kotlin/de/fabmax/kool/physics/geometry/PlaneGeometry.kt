package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.scene.geometry.MeshBuilder

/**
 * Plane collision shape. Plane normal is x-axis, origin is (0, 0, 0).
 */
expect class PlaneGeometry() : CommonPlaneGeometry, CollisionGeometry

abstract class CommonPlaneGeometry {
    open fun generateMesh(target: MeshBuilder) {
        // plane is infinitely large, generate a mesh with a reasonable size
        target.apply {
            withTransform {
                rotate(90f, Vec3f.Y_AXIS)
                rect {
                    size.set(1000f, 1000f)
                    origin.set(size.x * -0.5f, size.y * -0.5f, 0f)
                }
            }
        }
    }

    open fun getBounds(result: BoundingBox) = result.set(0f, -1e10f, -1e10f, 0f, 1e10f, 1e10f)

    open fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // plane does not have a meaningful inertia
        return result.set(1f, 1f, 1f)
    }
}
