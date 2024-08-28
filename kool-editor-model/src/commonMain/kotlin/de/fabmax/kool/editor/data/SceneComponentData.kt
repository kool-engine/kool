package de.fabmax.kool.editor.data

import de.fabmax.kool.modules.ksl.blocks.ToneMapping
import kotlinx.serialization.Serializable

@Serializable
data class SceneComponentData(
    val cameraEntityId: EntityId = EntityId.NULL,
    val maxNumLights: Int = 4,
    val toneMapping: ToneMapping = ToneMapping.AcesApproximated,
    val isFloatingOrigin: Boolean = false,
) : ComponentData