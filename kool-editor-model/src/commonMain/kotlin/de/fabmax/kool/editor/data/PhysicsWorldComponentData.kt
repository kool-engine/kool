package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class PhysicsWorldComponentData(var properties: PhysicsWorldProperties = PhysicsWorldProperties()) : ComponentData

@Serializable
data class PhysicsWorldProperties(
    val isContinuousCollisionDetection: Boolean = true
)