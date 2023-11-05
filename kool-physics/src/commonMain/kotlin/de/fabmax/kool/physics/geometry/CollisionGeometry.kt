package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.scene.geometry.MeshBuilder

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class GeometryHolder

interface CollisionGeometry : Releasable {

    val holder: GeometryHolder

    fun generateMesh(target: MeshBuilder)

    /**
     * Returns the axis-aligned bounding box of this shape in local coordinates.
     */
    fun getBounds(result: BoundingBox): BoundingBox

    fun estimateInertiaForMass(mass: Float, result: MutableVec3f = MutableVec3f()): MutableVec3f
}
