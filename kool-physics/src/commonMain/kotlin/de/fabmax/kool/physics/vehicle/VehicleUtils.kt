package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.FilterData
import de.fabmax.kool.physics.Material
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.*

object VehicleUtils {

    fun setupDrivableSurface(queryFilterData: FilterData): FilterData {
        queryFilterData.data[3] = SURFACE_FLAG_DRIVABLE
        return queryFilterData
    }

    fun setupNonDrivableSurface(queryFilterData: FilterData): FilterData {
        queryFilterData.data[3] = SURFACE_FLAG_NON_DRIVABLE
        return queryFilterData
    }

    fun defaultChassisShape(boxSize: Vec3f) = defaultChassisShape(BoxGeometry(boxSize))

    fun defaultChassisShape(geometry: CollisionGeometry): Shape {
        val simFilterData = FilterData(COLLISION_FLAG_CHASSIS, COLLISION_FLAG_CHASSIS_AGAINST)
        val qryFilterData = setupNonDrivableSurface(FilterData())
        return Shape(geometry, defaultChassisMaterial, simFilterData = simFilterData, queryFilterData = qryFilterData)
    }

    fun defaultWheelShape(radius: Float, width: Float): Shape {
        val mesh = defaultWheelMesh
        val geom = ConvexMeshGeometry(mesh, Vec3f(width, radius, radius))
        val simFilterData = FilterData(COLLISION_FLAG_WHEEL, COLLISION_FLAG_WHEEL_AGAINST)
        val qryFilterData = setupNonDrivableSurface(FilterData())
        return Shape(geom, defaultWheelMaterial, simFilterData = simFilterData, queryFilterData = qryFilterData)
    }

    val defaultChassisMaterial = Material(0.5f, 0.5f, 0.5f)
    val defaultWheelMaterial = Material(0.5f, 0.5f, 0.5f)
    val defaultWheelMesh by lazy {
        ConvexMesh(CommonCylinderGeometry.convexMeshPoints(1f, 1f)).apply {
            releaseWithGeometry = false
        }
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
