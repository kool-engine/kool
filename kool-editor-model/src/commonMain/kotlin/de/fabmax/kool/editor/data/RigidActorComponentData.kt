package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class RigidActorComponentData(var properties: RigidActorProperties = RigidActorProperties()) : ComponentData

@Serializable
data class RigidActorProperties(
    val type: RigidActorType = RigidActorType.DYNAMIC,
    val shape: RigidActorShape = RigidActorShape.UseMesh,
    val mass: Float = 1f,
)

enum class RigidActorType {
    DYNAMIC,
    KINEMATIC,
    STATIC
}

@Serializable
sealed class RigidActorShape {
    @Serializable
    data object UseMesh : RigidActorShape()

    @Serializable
    data class Box(val size: Vec3Data) : RigidActorShape()

    @Serializable
    data class Sphere(val radius: Float) : RigidActorShape()

    @Serializable
    data class Cylinder(val radius: Float, val length: Float) : RigidActorShape()

    @Serializable
    data class Capsule(val radius: Float, val length: Float) : RigidActorShape()

    @Serializable
    data class Heightmap(
        val mapPath: String,
        val heightOffset: Float = 0f,
        val heightScale: Float = 0.01f,
        val rowScale: Float = 1f,
        val colScale: Float = 1f,
    ) : RigidActorShape()
}
