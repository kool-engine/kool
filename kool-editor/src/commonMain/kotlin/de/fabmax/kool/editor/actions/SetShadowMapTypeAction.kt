package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ShadowMapComponent
import de.fabmax.kool.editor.data.ShadowMapTypeData

class SetShadowMapTypeAction(
    component: ShadowMapComponent,
    private val newShadowMapTypeData: ShadowMapTypeData
) : ComponentAction<ShadowMapComponent>(component) {

    private val component: ShadowMapComponent? get() = nodeModel?.getComponent()

    private val oldShadowMapTypeData = component.shadowMapState.value

    override fun doAction() {
        component?.shadowMapState?.set(newShadowMapTypeData)
    }

    override fun undoAction() {
        component?.shadowMapState?.set(oldShadowMapTypeData)
    }
}