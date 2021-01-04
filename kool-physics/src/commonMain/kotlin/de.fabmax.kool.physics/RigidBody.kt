package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.shapes.CollisionShape
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.group
import de.fabmax.kool.util.Color

expect class RigidBody(collisionShape: CollisionShape, mass: Float, bodyProperties: RigidBodyProperties = RigidBodyProperties()): CommonRigidBody

abstract class CommonRigidBody(val collisionShape: CollisionShape, val mass: Float) {

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    val transform = Mat4f()
    abstract var origin: Vec3f
    abstract var rotation: Vec4f

    private val bufRotation = MutableVec4f()

    fun setRotation(rotation: Mat3f) {
        this.rotation = rotation.getRotation(bufRotation)
    }

    internal open fun fixedUpdate(timeStep: Float) {
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }

    fun toColorMesh(meshColor: Color) = group {
        +colorMesh {
            generate {
                color = meshColor
                collisionShape.generateGeometry(this)
            }
            shader = pbrShader { }
            onUpdate += {
                this@group.transform.set(this@CommonRigidBody.transform)
                this@group.setDirty()
            }
        }
    }
}
