package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.FilterData

object VehicleUtils {

    fun setupDrivableSurface(queryFilterData: FilterData) {
        queryFilterData.data[3] = SURFACE_FLAG_DRIVABLE
    }

    fun setupNonDrivableSurface(queryFilterData: FilterData) {
        queryFilterData.data[3] = SURFACE_FLAG_NON_DRIVABLE
    }

    const val SURFACE_FLAG_DRIVABLE = 0xffff0000.toInt()
    const val SURFACE_FLAG_NON_DRIVABLE = 0x0000ffff

    const val COLLISION_FLAG_GROUND = 1 shl 0
    const val COLLISION_FLAG_WHEEL = 1 shl 1
    const val COLLISION_FLAG_CHASSIS = 1 shl 2
    const val COLLISION_FLAG_OBSTACLE = 1 shl 3
    const val COLLISION_FLAG_DRIVABLE_OBSTACLE = 1 shl 4

    const val COLLISION_FLAG_GROUND_AGAINST =                                                             COLLISION_FLAG_CHASSIS or COLLISION_FLAG_OBSTACLE or COLLISION_FLAG_DRIVABLE_OBSTACLE
    const val COLLISION_FLAG_WHEEL_AGAINST =                                      COLLISION_FLAG_WHEEL or COLLISION_FLAG_CHASSIS or COLLISION_FLAG_OBSTACLE
    const val COLLISION_FLAG_CHASSIS_AGAINST =           COLLISION_FLAG_GROUND or COLLISION_FLAG_WHEEL or COLLISION_FLAG_CHASSIS or COLLISION_FLAG_OBSTACLE or COLLISION_FLAG_DRIVABLE_OBSTACLE
    const val COLLISION_FLAG_OBSTACLE_AGAINST =          COLLISION_FLAG_GROUND or COLLISION_FLAG_WHEEL or COLLISION_FLAG_CHASSIS or COLLISION_FLAG_OBSTACLE or COLLISION_FLAG_DRIVABLE_OBSTACLE
    const val COLLISION_FLAG_DRIVABLE_OBSTACLE_AGAINST = COLLISION_FLAG_GROUND                         or COLLISION_FLAG_CHASSIS or COLLISION_FLAG_OBSTACLE or COLLISION_FLAG_DRIVABLE_OBSTACLE
}
