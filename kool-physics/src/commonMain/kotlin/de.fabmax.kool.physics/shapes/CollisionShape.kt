package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder

expect interface CollisionShape {

    fun generateGeometry(target: MeshBuilder)

    /**
     * Returns the axis-aligned bounding box of this shape in local coordinates.
     */
    fun getAabb(result: BoundingBox): BoundingBox

    /**
     * Returns the bounding sphere of this shape in local coordinates. Sphere center is stored in x, y, z components,
     * sphere radius is stored in w component of the given Vec4.
     */
    fun getBoundingSphere(result: MutableVec4f): MutableVec4f

    fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f
}
