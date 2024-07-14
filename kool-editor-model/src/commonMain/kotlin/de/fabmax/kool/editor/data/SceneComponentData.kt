package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
data class SceneComponentData(
    val cameraEntityId: EntityId = EntityId.NULL,
    val maxNumLights: Int = 4
) : ComponentData