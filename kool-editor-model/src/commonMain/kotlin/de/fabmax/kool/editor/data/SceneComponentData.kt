package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class SceneComponentData(
    var cameraEntityId: EntityId = EntityId(-1L),
    var maxNumLights: Int = 4
) : ComponentData