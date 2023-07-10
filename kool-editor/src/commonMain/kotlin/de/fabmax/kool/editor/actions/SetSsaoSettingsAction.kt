package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.SsaoSettings

class SetSsaoSettingsAction(
    val ssaoComponent: SsaoComponent,
    val oldSettings: SsaoSettings,
    val newSettings: SsaoSettings
) : EditorAction {

    override fun doAction() {
        ssaoComponent.ssaoState.set(newSettings)
    }

    override fun undoAction() {
        ssaoComponent.ssaoState.set(oldSettings)
    }
}