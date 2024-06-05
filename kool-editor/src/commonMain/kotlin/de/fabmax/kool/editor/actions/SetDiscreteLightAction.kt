package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.LightTypeData

class SetDiscreteLightAction(
    entityId: EntityId,
    val setLightData: LightTypeData,
    val undoLightData: LightTypeData
) : ComponentAction<DiscreteLightComponent>(entityId, DiscreteLightComponent::class) {

    override fun doAction() {
        component?.lightState?.set(setLightData)
    }

    override fun undoAction() {
        component?.lightState?.set(undoLightData)
    }
}