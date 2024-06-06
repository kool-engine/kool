package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.SsaoComponentData

class SetSsaoSettingsAction(
    entityId: EntityId,
    val oldSettings: SsaoComponentData,
    val newSettings: SsaoComponentData
) : ComponentAction<SsaoComponent>(entityId, SsaoComponent::class) {

    override fun doAction() {
        component?.setPersistent(newSettings)
    }

    override fun undoAction() {
        component?.setPersistent(oldSettings)
    }
}