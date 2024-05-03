package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.editor.data.NodeId

class SetDiscreteLightAction(
    nodeId: NodeId,
    val setLightData: LightTypeData,
    val undoLightData: LightTypeData
) : ComponentAction<DiscreteLightComponent>(nodeId, DiscreteLightComponent::class) {

    override fun doAction() {
        component?.lightState?.set(setLightData)
    }

    override fun undoAction() {
        component?.lightState?.set(undoLightData)
    }
}