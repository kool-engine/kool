package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ShadowMapComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.ShadowMapTypeData

class SetShadowMapTypeAction(
    entityId: EntityId,
    private val newShadowMapTypeData: ShadowMapTypeData
) : ComponentAction<ShadowMapComponent>(entityId, ShadowMapComponent::class) {

    private val oldShadowMapTypeData = component?.shadowMapType

    override fun doAction() {
        component?.let { it.setPersistent(it.data.copy(shadowMap = newShadowMapTypeData)) }
    }

    override fun undoAction() {
        val oldData = oldShadowMapTypeData ?: return
        component?.let { it.setPersistent(it.data.copy(shadowMap = oldData)) }
    }
}