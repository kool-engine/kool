package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.data.LightTypeData

class SetDiscreteLightAction(
    component: DiscreteLightComponent,
    val setLightData: LightTypeData,
    val undoLightData: LightTypeData
) : ComponentAction<DiscreteLightComponent>(component) {

    private val component: DiscreteLightComponent? get() = nodeModel?.getComponent()

    override fun doAction() {
        component?.lightState?.set(setLightData)
    }

    override fun undoAction() {
        component?.lightState?.set(undoLightData)
    }
}