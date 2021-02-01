package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.geometry.CollisionGeometry
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.group
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Color

expect class RigidBody(mass: Float, bodyProperties: RigidBodyProperties = RigidBodyProperties()): CommonRigidBody {

    fun attachShape(geometry: CollisionGeometry, material: Material, localPose: Mat4f = Mat4f()): RigidBody

}

abstract class CommonRigidBody(val isStatic: Boolean) {

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    val transform = Mat4f()
    abstract var origin: Vec3f
    abstract var rotation: Vec4f

    abstract var mass: Float
    abstract var inertia: Vec3f

    protected val mutShapes = mutableListOf<Pair<CollisionGeometry, Mat4f>>()
    val shapes: List<Pair<CollisionGeometry, Mat4f>>
        get() = mutShapes

    private val bufRotation = MutableVec4f()
    private val bufBounds = BoundingBox()
    private val bufVec3 = MutableVec3f()

    fun setRotation(rotation: Mat3f) {
        this.rotation = rotation.getRotation(bufRotation)
    }

    fun setTransform(transform: Mat4f) {
        this.origin = transform.transform(MutableVec3f())
        this.rotation = transform.getRotation(bufRotation)
    }

    internal open fun fixedUpdate(timeStep: Float) {
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }

    fun getGeometryBounds(result: BoundingBox): BoundingBox {
        result.clear()
        shapes.forEach { (geom, pose) ->
            geom.getBounds(bufBounds)
            result.add(pose.transform(bufVec3.set(bufBounds.min.x, bufBounds.min.y, bufBounds.min.z)))
            result.add(pose.transform(bufVec3.set(bufBounds.min.x, bufBounds.min.y, bufBounds.max.z)))
            result.add(pose.transform(bufVec3.set(bufBounds.min.x, bufBounds.max.y, bufBounds.min.z)))
            result.add(pose.transform(bufVec3.set(bufBounds.min.x, bufBounds.max.y, bufBounds.max.z)))
            result.add(pose.transform(bufVec3.set(bufBounds.max.x, bufBounds.min.y, bufBounds.min.z)))
            result.add(pose.transform(bufVec3.set(bufBounds.max.x, bufBounds.min.y, bufBounds.max.z)))
            result.add(pose.transform(bufVec3.set(bufBounds.max.x, bufBounds.max.y, bufBounds.min.z)))
            result.add(pose.transform(bufVec3.set(bufBounds.max.x, bufBounds.max.y, bufBounds.max.z)))
        }
        return result
    }

    fun toMesh(meshColor: Color, materialCfg: PbrMaterialConfig.() -> Unit = { }) = group {
        +colorMesh {
            generate {
                color = meshColor
                mutShapes.forEach { (geom, pose) ->
                    withTransform {
                        transform.mul(pose)
                        geom.generateMesh(this)
                    }
                }
            }
            shader = pbrShader {
                materialCfg()
            }
            onUpdate += {
                this@group.transform.set(this@CommonRigidBody.transform)
                this@group.setDirty()
            }
        }
    }
}
