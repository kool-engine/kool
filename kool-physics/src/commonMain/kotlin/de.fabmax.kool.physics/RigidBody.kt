package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.shapes.CollisionShape
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.group
import de.fabmax.kool.util.Color

expect class RigidBody(collisionShape: CollisionShape, mass: Float, bodyProperties: RigidBodyProperties = RigidBodyProperties()): CommonRigidBody

abstract class CommonRigidBody(val collisionShape: CollisionShape, val isStatic: Boolean, bodyProperties: RigidBodyProperties) {

    val collisionGroup = bodyProperties.collisionGroupBits
    val collisionMask = bodyProperties.collisionMask

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    val transform = Mat4f()
    abstract var origin: Vec3f
    abstract var rotation: Vec4f

    abstract var mass: Float
    abstract var inertia: Vec3f

    private val bufRotation = MutableVec4f()

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

    fun toMesh(meshColor: Color, materialCfg: PbrMaterialConfig.() -> Unit = { }) = group {
        +colorMesh {
            generate {
                color = meshColor
                collisionShape.generateGeometry(this)
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
