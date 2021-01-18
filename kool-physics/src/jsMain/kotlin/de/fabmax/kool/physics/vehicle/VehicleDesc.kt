package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_CHASSIS
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_CHASSIS_AGAINST
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_WHEEL
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_WHEEL_AGAINST
import physx.PhysX
import physx.PxFilterData
import physx.PxMaterial

class VehicleDesc {

    var chassisMass = 1500f
    var chassisDims = Vec3f(2f, 1f, 5f)
    var chassisMOI = Vec3f(
        (chassisDims.y * chassisDims.y + chassisDims.z * chassisDims.z) * chassisMass / 12.0f,
        (chassisDims.x * chassisDims.x + chassisDims.z * chassisDims.z) * chassisMass / 12.0f * 0.8f,
        (chassisDims.x * chassisDims.x + chassisDims.y * chassisDims.y) * chassisMass / 12.0f)
    var chassisCMOffset = Vec3f(0.0f, -chassisDims.y * 0.5f + 0.65f, 0.25f)
    var chassisMaterial: PxMaterial
    var chassisSimFilterData: PxFilterData  //word0 = collide type, word1 = collide against types, word2 = PxPairFlags

    var wheelMass = 20f
    var wheelWidth = 0.4f
    var wheelRadius = 0.5f
    var wheelMOI = 0.5f * wheelMass * wheelRadius * wheelRadius
    var numWheels = 4
    var wheelMaterial: PxMaterial
    var wheelSimFilterData: PxFilterData    //word0 = collide type, word1 = collide against types, word2 = PxPairFlags

    //var actorUserData: ActorUserData
    //var shapeUserDatas: ShapeUserData

    init {
        Physics.checkIsLoaded()

        val material = PhysX.physics.createMaterial(0.5f,0.5f, 0.6f)
        chassisMaterial = material
        wheelMaterial = material

        chassisSimFilterData = PhysX.PxFilterData(COLLISION_FLAG_CHASSIS, COLLISION_FLAG_CHASSIS_AGAINST, 0, 0)
        wheelSimFilterData = PhysX.PxFilterData(COLLISION_FLAG_WHEEL, COLLISION_FLAG_WHEEL_AGAINST, 0, 0)
    }
}