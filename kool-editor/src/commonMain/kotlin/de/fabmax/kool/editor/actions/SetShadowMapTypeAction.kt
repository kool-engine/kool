package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ShadowMapComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.ShadowMapTypeData

class SetShadowMapTypeAction(
    entityId: EntityId,
    private val newShadowMapTypeData: ShadowMapTypeData
) : ComponentAction<ShadowMapComponent>(entityId, ShadowMapComponent::class) {

    private val oldShadowMapTypeData = component?.shadowMapState?.value

    override fun doAction() {
        component?.shadowMapState?.set(newShadowMapTypeData)
    }

    override fun undoAction() {
        oldShadowMapTypeData?.let { component?.shadowMapState?.set(it) }
    }
}