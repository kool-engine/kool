package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
data class SceneComponentData(
    val cameraEntityId: EntityId = EntityId(-1L),
    val maxNumLights: Int = 4
) : ComponentData