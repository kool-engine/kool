package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.LightTypeData

class SetDiscreteLightAction(
    entityId: EntityId,
    val applyLightData: LightTypeData,
    val undoLightData: LightTypeData
) : ComponentAction<DiscreteLightComponent>(entityId, DiscreteLightComponent::class) {

    override fun doAction() {
        component?.let { it.setPersistent(it.data.copy(light = applyLightData)) }
    }

    override fun undoAction() {
        component?.let { it.setPersistent(it.data.copy(light = undoLightData)) }
    }
}