package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
data class PhysicsWorldComponentData(
    val gravity: Vec3Data = Vec3Data(0.0, -9.81, 0.0),
    val isContinuousCollisionDetection: Boolean = true
) : ComponentData
