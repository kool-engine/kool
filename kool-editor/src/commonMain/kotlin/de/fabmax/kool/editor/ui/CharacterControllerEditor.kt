package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.SetCharControllerPropertiesAction
import de.fabmax.kool.editor.components.CharacterControllerComponent
import de.fabmax.kool.editor.data.CharacterControllerComponentProperties
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.physics.character.HitActorBehavior

class CharacterControllerEditor : ComponentEditor<CharacterControllerComponent>() {

    override fun UiScope.compose() = componentPanel(
        title = "Character Controller",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        components.forEach { it.charControllerState.use() }

        charDoublePropertyEditor(
            valueGetter = { it.shape.radius },
            valueSetter = { oldData, newValue -> oldData.copy(shape = oldData.shape.copy(radius = newValue)) },
            label = "Radius:",
            minValue = CharacterControllerComponent.CHARACTER_CONTACT_OFFSET + 0.01
        )
        charDoublePropertyEditor(
            valueGetter = { it.shape.length },
            valueSetter = { oldData, newValue -> oldData.copy(shape = oldData.shape.copy(length = newValue)) },
            label = "Height:"
        )

        menuDivider()

        charDoublePropertyEditor(
            valueGetter = { it.walkSpeed },
            valueSetter = { oldData, newValue -> oldData.copy(walkSpeed = newValue) },
            label = "Walk speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.runSpeed },
            valueSetter = { oldData, newValue -> oldData.copy(runSpeed = newValue) },
            label = "Run speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.crouchSpeed },
            valueSetter = { oldData, newValue -> oldData.copy(crouchSpeed = newValue) },
            label = "Crouch speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.jumpSpeed },
            valueSetter = { oldData, newValue -> oldData.copy(jumpSpeed = newValue) },
            label = "Jump speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.slopeLimit },
            valueSetter = { oldData, newValue -> oldData.copy(slopeLimit = newValue) },
            label = "Slope limit:",
            maxValue = 90.0
        )
        charDoublePropertyEditor(
            valueGetter = { it.pushForce },
            valueSetter = { oldData, newValue -> oldData.copy(pushForce = newValue) },
            label = "Push force:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.downForce },
            valueSetter = { oldData, newValue -> oldData.copy(downForce = newValue) },
            label = "Down force:"
        )
        choicePropertyEditor(
            choices = hitBehaviorOptions,
            dataGetter = { it.charControllerState.value },
            valueGetter = { it.hitActorMode },
            valueSetter = { oldData, newValue -> oldData.copy(hitActorMode = newValue) },
            actionMapper = setCharProps,
            label = "Default hit behavior:",
            labelWidth = sizes.editorLabelWidthLarge
        )

        menuDivider()

        charBooleanPropertyEditor(
            valueGetter = { it.enableDefaultControls },
            valueSetter = { oldData, newValue -> oldData.copy(enableDefaultControls = newValue) },
            "Default keyboard controls"
        )

        if (components.all { it.charControllerState.value.enableDefaultControls }) {
            charBooleanPropertyEditor(
                valueGetter = { it.runByDefault },
                valueSetter = { oldData, newValue -> oldData.copy(runByDefault = newValue) },
                "Run by default"
            )
        }
    }

    private fun UiScope.charDoublePropertyEditor(
        valueGetter: (CharacterControllerComponentProperties) -> Double,
        valueSetter: (oldData: CharacterControllerComponentProperties, newValue: Double) -> CharacterControllerComponentProperties,
        label: String,
        minValue: Double = 0.0,
        maxValue: Double = Double.POSITIVE_INFINITY,
    ) = doublePropertyEditor(
        dataGetter = { it.charControllerState.value },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = setCharProps,
        label = label,
        minValue = minValue,
        maxValue = maxValue
    )

    private fun UiScope.charBooleanPropertyEditor(
        valueGetter: (CharacterControllerComponentProperties) -> Boolean,
        valueSetter: (oldData: CharacterControllerComponentProperties, newValue: Boolean) -> CharacterControllerComponentProperties,
        label: String,
    ) = booleanPropertyEditor(
        dataGetter = { it.charControllerState.value },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = setCharProps,
        label
    )

    companion object {
        private val setCharProps: (
            component: CharacterControllerComponent,
            undoData: CharacterControllerComponentProperties,
            applyData: CharacterControllerComponentProperties
        ) -> EditorAction = { component, undoData, applyData ->
            SetCharControllerPropertiesAction(component.nodeModel.nodeId, undoData, applyData)
        }

        private val hitBehaviorOptions = ComboBoxItems(HitActorBehavior.entries) {
            when (it) {
                HitActorBehavior.DEFAULT -> "Standard"
                HitActorBehavior.SLIDE -> "Slide"
                HitActorBehavior.RIDE -> "Ride"
            }
        }
    }
}