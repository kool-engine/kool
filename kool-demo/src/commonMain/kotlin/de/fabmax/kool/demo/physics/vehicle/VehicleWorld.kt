package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.TriangleMeshGeometry
import de.fabmax.kool.physics.vehicle.VehicleUtils
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Color

class VehicleWorld(val scene: Scene, val physics: PhysicsWorld, val deferredPipeline: DeferredPipeline) {

    val defaultMaterial = Material(0.5f)
    val groundSimFilterData = FilterData(VehicleUtils.COLLISION_FLAG_GROUND, VehicleUtils.COLLISION_FLAG_GROUND_AGAINST)
    val groundQryFilterData = FilterData { VehicleUtils.setupDrivableSurface(this) }
    val obstacleSimFilterData =
        FilterData(VehicleUtils.COLLISION_FLAG_DRIVABLE_OBSTACLE, VehicleUtils.COLLISION_FLAG_DRIVABLE_OBSTACLE_AGAINST)
    val obstacleQryFilterData = FilterData { VehicleUtils.setupDrivableSurface(this) }

    fun toPrettyMesh(actor: RigidActor, meshColor: Color, rough: Float = 0.8f, metal: Float = 0f): Node = Node().apply {
        addColorMesh {
            generate {
                color = meshColor
                actor.shapes.forEach { shape ->
                    withTransform {
                        transform.mul(shape.localPose)
                        shape.geometry.generateMesh(this)
                    }
                }
            }
            shader = deferredKslPbrShader {
                color { vertexColor() }
                roughness(rough)
                metallic(metal)
            }
            transform = actor.transform
        }
    }

    fun addStaticCollisionBody(mesh: IndexedVertexList): RigidStatic {
        val body = RigidStatic().apply {
            simulationFilterData = obstacleSimFilterData
            queryFilterData = obstacleQryFilterData
            attachShape(Shape(TriangleMeshGeometry(mesh), defaultMaterial))
        }
        physics.addActor(body)
        return body
    }

    fun release() {
        physics.clear()
        physics.release()
        defaultMaterial.release()
    }
}