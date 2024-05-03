package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ShadowMapComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.ShadowMapTypeData

class SetShadowMapTypeAction(
    nodeId: NodeId,
    private val newShadowMapTypeData: ShadowMapTypeData
) : ComponentAction<ShadowMapComponent>(nodeId, ShadowMapComponent::class) {

    private val oldShadowMapTypeData = component?.shadowMapState?.value

    override fun doAction() {
        component?.shadowMapState?.set(newShadowMapTypeData)
    }

    override fun undoAction() {
        oldShadowMapTypeData?.let { component?.shadowMapState?.set(it) }
    }
}