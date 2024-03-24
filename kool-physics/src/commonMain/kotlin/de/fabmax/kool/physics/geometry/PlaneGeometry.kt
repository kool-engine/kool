package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.geometry.MeshBuilder

/**
 * Plane collision shape. Plane normal is x-axis, origin is (0, 0, 0).
 */
expect fun PlaneGeometry(): CommonPlaneGeometry

interface CommonPlaneGeometry : CollisionGeometry {
    override fun generateMesh(target: MeshBuilder) {
        // plane is infinitely large, generate a mesh with a reasonable size
        target.apply {
            withTransform {
                rotate(90f.deg, Vec3f.Y_AXIS)
                rect {
                    size.set(1000f, 1000f)
                }
            }
        }
    }

    override fun getBounds(result: BoundingBoxF) = result.set(0f, -1e10f, -1e10f, 0f, 1e10f, 1e10f)

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // plane does not have a meaningful inertia
        return result.set(1f, 1f, 1f)
    }
}
