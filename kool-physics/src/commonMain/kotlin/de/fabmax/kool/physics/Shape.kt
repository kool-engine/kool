package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.physics.geometry.CollisionGeometry

expect class Shape(
    geometry: CollisionGeometry,
    material: Material = Physics.defaultMaterial,
    localPose: Mat4f = MutableMat4f(),
    simFilterData: FilterData? = null,
    queryFilterData: FilterData? = null
) {
    val geometry: CollisionGeometry
    val material: Material
    val localPose: MutableMat4f
    val simFilterData: FilterData?
    val queryFilterData: FilterData?
}