package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.Physics
import physx.PhysX
import physx.PxMaterial
import physx.PxVehicleDrivableSurfaceToTireFrictionPairs

class FrictionPairs(val nbTireTypes: Int, val materials: List<PxMaterial>, val surfaceTypes: List<Int>) {

    constructor(nbTireTypes: Int, materials: List<PxMaterial>) :
            this(nbTireTypes, materials, materials.indices.toList())

    val frictionPairs: PxVehicleDrivableSurfaceToTireFrictionPairs

    init {
        Physics.checkIsLoaded()

        val materials = Array(materials.size) { materials[it] }
        val nbSurfaceTypes = surfaceTypes.size

        frictionPairs = PhysX.PxVehicleDrivableSurfaceToTireFrictionPairs(nbTireTypes, nbSurfaceTypes)

        val surfaceTypes = PhysX.Vector_PxVehicleDrivableSurfaceType()
        surfaceTypes.push_back(PhysX.PxVehicleDrivableSurfaceType().apply { mType = 1 })

        frictionPairs.setup(nbTireTypes, nbSurfaceTypes, materials, surfaceTypes.data())

        // initialize friction pairs with 1.0
        for (s in 0 until nbSurfaceTypes) {
            for (t in 0 until nbTireTypes) {
                frictionPairs.setTypePairFriction(s, t, 1f)
            }
        }
    }

    fun setTypePairFriction(material: PxMaterial, tireType: Int, friction: Float) {
        val iMaterial = materials.indexOf(material)
        if (iMaterial < 0) {
            throw IllegalArgumentException("Unknown material, material is not present in List passed on creation of this object")
        }
        if (tireType !in 0 until nbTireTypes) {
            throw IllegalArgumentException("tireType out of range: $tireType !in [0 .. ${nbTireTypes - 1}]")
        }
        val surfaceType = surfaceTypes[iMaterial]
        frictionPairs.setTypePairFriction(surfaceType, tireType, friction)
    }

    companion object {
        const val TIRE_TYPE_NORMAL = 0
    }
}