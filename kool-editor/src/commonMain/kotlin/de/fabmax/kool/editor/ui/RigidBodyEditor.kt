package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetRigidBodyPropertiesAction
import de.fabmax.kool.editor.components.RigidBodyComponent
import de.fabmax.kool.editor.data.RigidBodyType
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

            val bodyProps = component.bodyState.use()

            labeledCombobox(
                label = "Body type:",
                items = bodyOptions,
                bodyOptions.indexOfFirst { it.type == bodyProps.bodyType }
            ) {
                SetRigidBodyPropertiesAction(component, bodyProps, bodyProps.copy(bodyType = it.type)).apply()
            }

            labeledDoubleTextField(
                label = "Mass:",
                value = bodyProps.mass.toDouble(),
                minValue = 0.001,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetRigidBodyPropertiesAction(component, bodyProps.copy(mass = undo.toFloat()), bodyProps.copy(mass = apply.toFloat()))
                }
            )
        }
    }

    private class BodyTypeOption(val label: String, val type: RigidBodyType) {
        override fun toString(): String = label
    }

    companion object {
        private val bodyOptions = listOf(
            BodyTypeOption("Dynamic", RigidBodyType.DYNAMIC),
            BodyTypeOption("Kinematic", RigidBodyType.KINEMATIC),
            BodyTypeOption("Static", RigidBodyType.STATIC),
        )
    }
}