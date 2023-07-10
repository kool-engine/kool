package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ShadowMapComponent
import de.fabmax.kool.editor.data.ShadowMapTypeData

class SetShadowMapTypeAction(
    private val shadowMapComponent: ShadowMapComponent,
    private val newShadowMapTypeData: ShadowMapTypeData
) : EditorAction {

    private val oldShadowMapTypeData = shadowMapComponent.shadowMapState.value

    override fun doAction() {
        shadowMapComponent.shadowMapState.set(newShadowMapTypeData)
    }

    override fun undoAction() {
        shadowMapComponent.shadowMapState.set(oldShadowMapTypeData)
    }
}