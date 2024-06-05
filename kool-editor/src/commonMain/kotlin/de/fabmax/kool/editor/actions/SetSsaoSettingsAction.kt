package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.SsaoSettings

class SetSsaoSettingsAction(
    entityId: EntityId,
    val oldSettings: SsaoSettings,
    val newSettings: SsaoSettings
) : ComponentAction<SsaoComponent>(entityId, SsaoComponent::class) {

    override fun doAction() {
        component?.ssaoState?.set(newSettings)
    }

    override fun undoAction() {
        component?.ssaoState?.set(oldSettings)
    }
}