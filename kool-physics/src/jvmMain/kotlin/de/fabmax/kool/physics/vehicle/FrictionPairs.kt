package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.Releasable
import physx.physics.PxMaterial
import physx.support.Vector_PxMaterialConst
import physx.support.Vector_PxVehicleDrivableSurfaceType
import physx.vehicle.PxVehicleDrivableSurfaceToTireFrictionPairs
import physx.vehicle.PxVehicleDrivableSurfaceType

class FrictionPairs(val nbTireTypes: Int, val materials: List<PxMaterial>, val surfaceTypes: List<Int>) : Releasable {

    constructor(nbTireTypes: Int, materials: List<PxMaterial>) :
            this(nbTireTypes, materials, materials.indices.toList())

    val frictionPairs: PxVehicleDrivableSurfaceToTireFrictionPairs

    init {
        Physics.checkIsLoaded()

        val nbSurfaceTypes = surfaceTypes.size
        val materialVec = Vector_PxMaterialConst(materials.size)
        materials.forEach {
            materialVec.push_back(it)
        }

        frictionPairs = PxVehicleDrivableSurfaceToTireFrictionPairs.allocate(nbTireTypes, nbSurfaceTypes)

        val surfaceTypes = Vector_PxVehicleDrivableSurfaceType()
        surfaceTypes.push_back(PxVehicleDrivableSurfaceType().apply { mType = 1 })

        frictionPairs.setup(nbTireTypes, nbSurfaceTypes, materialVec.data(), surfaceTypes.data())

        // initialize friction pairs with 1.0
        for (s in 0 until nbSurfaceTypes) {
            for (t in 0 until nbTireTypes) {
                frictionPairs.setTypePairFriction(s, t, 1f)
            }
        }

        materialVec.destroy()
        surfaceTypes.destroy()
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

    override fun release() {
        frictionPairs.release()
    }

    companion object {
        const val TIRE_TYPE_NORMAL = 0
    }
}