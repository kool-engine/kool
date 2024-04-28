package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class PhysicsWorldComponentData(var settings: PhysicsWorldSettings = PhysicsWorldSettings()) : ComponentData

@Serializable
data class PhysicsWorldSettings(
    val isContinuousCollisionDetection: Boolean = true
)