package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ScenePropertiesComponentData(
    var cameraNodeId: Long = -1L,
    var maxNumLights: Int = 4
) : ComponentData