package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.CharacterControllerComponent
import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.editor.ui.ComboBoxItems
import de.fabmax.kool.editor.ui.Icons
import de.fabmax.kool.editor.ui.menuDivider
import de.fabmax.kool.modules.ui2.ColumnScope
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.physics.character.NonWalkableMode

class CharacterControllerEditor : ComponentEditor<CharacterControllerComponent>() {

    override fun UiScope.compose() = componentPanel(
        title = "Character Controller",
        imageIcon = Icons.small.character,
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
            valueGetter = { it.walkSpeed.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(walkSpeed = newValue.toFloat()) },
            label = "Walk speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.runSpeed.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(runSpeed = newValue.toFloat()) },
            label = "Run speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.crouchSpeed.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(crouchSpeed = newValue.toFloat()) },
            label = "Crouch speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.jumpSpeed.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(jumpSpeed = newValue.toFloat()) },
            label = "Jump speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.maxFallSpeed.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(maxFallSpeed = newValue.toFloat()) },
            label = "Max fall speed:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.slopeLimit.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(slopeLimit = newValue.toFloat()) },
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
            valueGetter = { it.pushForce.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(pushForce = newValue.toFloat()) },
            label = "Push force:"
        )
        charDoublePropertyEditor(
            valueGetter = { it.downForce.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(downForce = newValue.toFloat()) },
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

    private fun ColumnScope.charDoublePropertyEditor(
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

    private fun ColumnScope.charBooleanPropertyEditor(
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