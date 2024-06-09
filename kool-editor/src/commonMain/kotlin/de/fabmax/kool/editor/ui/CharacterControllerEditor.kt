package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.CharacterControllerComponent
import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.physics.character.NonWalkableMode

class CharacterControllerEditor : ComponentEditor<CharacterControllerComponent>() {

    override fun UiScope.compose() = componentPanel(
        title = "Character Controller",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        components.forEach { it.dataState.use() }

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
            valueGetter = { it.maxFallSpeed },
            valueSetter = { oldData, newValue -> oldData.copy(maxFallSpeed = newValue) },
            label = "Max fall speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.slopeLimit },
            valueSetter = { oldData, newValue -> oldData.copy(slopeLimit = newValue) },
            label = "Slope limit:",
            maxValue = 90.0
        )
        choicePropertyEditor(
            choices = nonWalkableOptions,
            dataGetter = { it.data },
            valueGetter = { it.nonWalkableMode },
            valueSetter = { oldData, newValue -> oldData.copy(nonWalkableMode = newValue) },
            actionMapper = setCharProps,
            label = "Slope behavior:"
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

        menuDivider()

        charBooleanPropertyEditor(
            valueGetter = { it.enableDefaultControls },
            valueSetter = { oldData, newValue -> oldData.copy(enableDefaultControls = newValue) },
            "Default keyboard controls"
        )

        if (components.all { it.data.enableDefaultControls }) {
            charBooleanPropertyEditor(
                valueGetter = { it.runByDefault },
                valueSetter = { oldData, newValue -> oldData.copy(runByDefault = newValue) },
                "Run by default"
            )
        }
    }

    private fun UiScope.charDoublePropertyEditor(
        valueGetter: (CharacterControllerComponentData) -> Double,
        valueSetter: (oldData: CharacterControllerComponentData, newValue: Double) -> CharacterControllerComponentData,
        label: String,
        minValue: Double = 0.0,
        maxValue: Double = Double.POSITIVE_INFINITY,
    ) = doublePropertyEditor(
        dataGetter = { it.data },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = setCharProps,
        label = label,
        minValue = minValue,
        maxValue = maxValue
    )

    private fun UiScope.charBooleanPropertyEditor(
        valueGetter: (CharacterControllerComponentData) -> Boolean,
        valueSetter: (oldData: CharacterControllerComponentData, newValue: Boolean) -> CharacterControllerComponentData,
        label: String,
    ) = booleanPropertyEditor(
        dataGetter = { it.data },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = setCharProps,
        label
    )

    companion object {
        private val setCharProps: (
            component: CharacterControllerComponent,
            undoData: CharacterControllerComponentData,
            applyData: CharacterControllerComponentData
        ) -> EditorAction = { component, undoData, applyData ->
            SetComponentDataAction(component, undoData, applyData)
        }

        private val nonWalkableOptions = ComboBoxItems(NonWalkableMode.entries) {
            when (it) {
                NonWalkableMode.PREVENT_CLIMBING -> "Prevent climbing"
                NonWalkableMode.PREVENT_CLIMBING_AND_FORCE_SLIDING -> "Sliding"
            }
        }
    }
}