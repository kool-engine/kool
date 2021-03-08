package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.TriangleMeshGeometry
import de.fabmax.kool.physics.vehicle.VehicleUtils
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.ibl.EnvironmentMaps

class VehicleWorld(
    val scene: Scene,
    val physics: PhysicsWorld,
    val envMaps: EnvironmentMaps,
    val shadows: List<ShadowMap>,
    val aoMap: Texture2d) {

    val defaultMaterial = Material(0.5f)
    val groundSimFilterData = FilterData(VehicleUtils.COLLISION_FLAG_GROUND, VehicleUtils.COLLISION_FLAG_GROUND_AGAINST)
    val groundQryFilterData = FilterData().apply { VehicleUtils.setupDrivableSurface(this) }
    val obstacleSimFilterData = FilterData(VehicleUtils.COLLISION_FLAG_DRIVABLE_OBSTACLE, VehicleUtils.COLLISION_FLAG_DRIVABLE_OBSTACLE_AGAINST)
    val obstacleQryFilterData = FilterData().apply { VehicleUtils.setupDrivableSurface(this) }

    fun toPrettyMesh(actor: RigidActor, color: Color, rough: Float = 0.8f, metal: Float = 0f) = actor.toMesh(color) {
        roughness = rough
        metallic = metal
        useImageBasedLighting(envMaps)
        useScreenSpaceAmbientOcclusion(aoMap)
        shadowMaps += shadows
    }

    fun addStaticCollisionBody(mesh: IndexedVertexList) {
        val body = RigidStatic().apply {
            setSimulationFilterData(obstacleSimFilterData)
            setQueryFilterData(obstacleQryFilterData)
            attachShape(Shape(TriangleMeshGeometry(mesh), defaultMaterial))
        }
        physics.addActor(body)
    }

    fun release() {
        physics.clear()
        physics.release()
        defaultMaterial.release()
    }
}