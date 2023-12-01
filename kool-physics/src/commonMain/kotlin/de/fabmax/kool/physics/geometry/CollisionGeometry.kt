package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Releasable

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class GeometryHolder

interface CollisionGeometry : Releasable {

    val holder: GeometryHolder

    fun generateMesh(target: MeshBuilder)

    /**
     * Returns the axis-aligned bounding box of this shape in local coordinates.
     */
    fun getBounds(result: BoundingBoxF): BoundingBoxF

    fun estimateInertiaForMass(mass: Float, result: MutableVec3f = MutableVec3f()): MutableVec3f
}
