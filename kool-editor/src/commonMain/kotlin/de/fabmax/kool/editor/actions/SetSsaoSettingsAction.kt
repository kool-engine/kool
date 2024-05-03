package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.SsaoSettings

class SetSsaoSettingsAction(
    component: SsaoComponent,
    val oldSettings: SsaoSettings,
    val newSettings: SsaoSettings
) : ComponentAction<SsaoComponent>(component) {

    private val component: SsaoComponent? get() = nodeModel?.getComponent()

    override fun doAction() {
        component?.ssaoState?.set(newSettings)
    }

    override fun undoAction() {
        component?.ssaoState?.set(oldSettings)
    }
}