package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class RigidBodyComponentData(var properties: RigidBodyProperties = RigidBodyProperties()) : ComponentData

@Serializable
data class RigidBodyProperties(
    val type: RigidBodyType = RigidBodyType.DYNAMIC,
    val shape: RigidBodyShape = RigidBodyShape.UseMesh,
    val mass: Float = 1f,
)

enum class RigidBodyType {
    DYNAMIC,
    KINEMATIC,
    STATIC
}

@Serializable
sealed class RigidBodyShape {
    @Serializable
    data object UseMesh : RigidBodyShape()

    @Serializable
    data class Box(val size: Vec3Data) : RigidBodyShape()

    @Serializable
    data class Sphere(val radius: Float) : RigidBodyShape()

    @Serializable
    data class Cylinder(val radius: Float, val length: Float) : RigidBodyShape()

    @Serializable
    data class Capsule(val radius: Float, val length: Float) : RigidBodyShape()
}
