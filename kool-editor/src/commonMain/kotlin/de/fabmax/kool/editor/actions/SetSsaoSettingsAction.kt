package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.data.SsaoSettings

class SetSsaoSettingsAction(
    nodeId: NodeId,
    val oldSettings: SsaoSettings,
    val newSettings: SsaoSettings
) : ComponentAction<SsaoComponent>(nodeId, SsaoComponent::class) {

    override fun doAction() {
        component?.ssaoState?.set(newSettings)
    }

    override fun undoAction() {
        component?.ssaoState?.set(oldSettings)
    }
}