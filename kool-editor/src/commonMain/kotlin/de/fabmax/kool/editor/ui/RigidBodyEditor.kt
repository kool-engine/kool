package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetRigidBodyMassAction
import de.fabmax.kool.editor.components.RigidBodyComponent
import de.fabmax.kool.modules.ui2.*

class RigidBodyEditor(component: RigidBodyComponent) : ComponentEditor<RigidBodyComponent>(component) {

    override fun UiScope.compose() = componentPanel(
        title = "Rigid Body",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            labeledDoubleTextField(
                label = "Mass:",
                value = component.massState.use().toDouble(),
                minValue = 0.001,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetRigidBodyMassAction(component, undo.toFloat(), apply.toFloat())
                }
            )
        }
    }

}