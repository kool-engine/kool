package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.SsaoComponentData
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*

class SsaoEditor : ComponentEditor<SsaoComponent>() {

    override fun UiScope.compose() = componentPanel("Screen-Space Ambient Occlusion", Icons.small.shadowedSphere, ::removeComponent) {
        val ssao = component.dataState.use()

        labeledDoubleTextField(
            label = "Radius:",
            value = ssao.radius.toDouble(),
            precision = 3,
            minValue = 0.0,
            maxValue = if (ssao.isRelativeRadius) 1.0 else Double.POSITIVE_INFINITY,
            dragChangeSpeed = if (ssao.isRelativeRadius) DragChangeRates.RANGE_0_TO_1 else DragChangeRates.RANGE_0_TO_1 * 10.0,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val oldSettings = component.data.copy(radius = undoValue.toFloat())
                val newSettings = component.data.copy(radius = applyValue.toFloat())
                SetComponentDataAction(component, oldSettings, newSettings)
            }
        )

        labeledCheckbox("Distance-based radius:", ssao.isRelativeRadius) {
            val oldSettings = component.data
            val allowedMax = if (it) 1f else Float.POSITIVE_INFINITY
            val clampedRadius = oldSettings.radius.clamp(0f, allowedMax)
            val newSettings = component.data.copy(isRelativeRadius = it, radius = clampedRadius)
            SetComponentDataAction(component, oldSettings, newSettings).apply()
        }

        labeledDoubleTextField(
            label = "Strength:",
            value = ssao.strength.toDouble(),
            precision = 3,
            minValue = 0.0,
            maxValue = 5.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 5.0,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val oldSettings = component.data.copy(strength = undoValue.toFloat())
                val newSettings = component.data.copy(strength = applyValue.toFloat())
                SetComponentDataAction(component, oldSettings, newSettings)
            }
        )

        labeledDoubleTextField(
            label = "Falloff:",
            value = ssao.power.toDouble(),
            precision = 3,
            minValue = 0.0,
            maxValue = 10.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 10.0,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val oldSettings = component.data.copy(power = undoValue.toFloat())
                val newSettings = component.data.copy(power = applyValue.toFloat())
                SetComponentDataAction(component, oldSettings, newSettings)
            }
        )

        menuDivider()

        labeledDoubleTextField(
            label = "Map resolution:",
            value = ssao.mapSize.toDouble(),
            precision = 3,
            minValue = 0.0,
            maxValue = 1.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val oldSettings = component.data.copy(mapSize = undoValue.toFloat())
                val newSettings = component.data.copy(mapSize = applyValue.toFloat())
                SetComponentDataAction(component, oldSettings, newSettings)
            }
        )

        labeledIntTextField(
            label = "Number of samples:",
            value = ssao.samples,
            minValue = 2,
            maxValue = 64,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 64.0,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val oldSettings = component.data.copy(samples = undoValue)
                val newSettings = component.data.copy(samples = applyValue)
                SetComponentDataAction(component, oldSettings, newSettings)
            }
        )

        Button("Restore defaults") {
            defaultButtonStyle()
            modifier
                .width(sizes.baseSize * 5)
                .height(sizes.editItemHeight)
                .margin(vertical = sizes.smallGap)
                .alignX(AlignmentX.Center)
                .onClick {
                    SetComponentDataAction(component, component.data, SsaoComponentData()).apply()
                }
        }
    }
}