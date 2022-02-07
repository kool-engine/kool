package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.scene.geometry.MeshBuilder

expect class BoxGeometry(size: Vec3f) : CommonBoxGeometry, CollisionGeometry

abstract class CommonBoxGeometry(val size: Vec3f) {
    open fun generateMesh(target: MeshBuilder) {
        target.cube {
            size.set(this@CommonBoxGeometry.size)
            origin.set(size).scale(-0.5f)
        }
    }

    open fun getBounds(result: BoundingBox): BoundingBox {
        result.set(-size.x * 0.5f, -size.y * 0.5f, -size.z * 0.5f,
            size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)
        return result
    }

    open fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        result.x = (mass / 12f) * (size.y * size.y + size.z * size.z)
        result.y = (mass / 12f) * (size.x * size.x + size.z * size.z)
        result.z = (mass / 12f) * (size.x * size.x + size.y * size.y)
        return result
    }
}
