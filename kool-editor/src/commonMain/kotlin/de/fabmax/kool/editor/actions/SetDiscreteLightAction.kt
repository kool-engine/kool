package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.data.LightTypeData

class SetDiscreteLightAction(
    val lightComponent: DiscreteLightComponent,
    val setLightData: LightTypeData
) : EditorAction {

    private val prevLightData = lightComponent.lightState.value

    override fun doAction() {
        lightComponent.lightState.set(setLightData)
    }

    override fun undoAction() {
        lightComponent.lightState.set(prevLightData)
    }
}