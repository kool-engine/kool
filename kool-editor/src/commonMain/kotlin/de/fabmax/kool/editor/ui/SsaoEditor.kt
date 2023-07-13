package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetSsaoSettingsAction
import de.fabmax.kool.editor.components.SsaoComponent
import de.fabmax.kool.editor.data.SsaoSettings
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*

class SsaoEditor(component: SsaoComponent) : ComponentEditor<SsaoComponent>(component) {

    override fun UiScope.compose() = componentPanel(title = "Screen-space Ambient Occlusion", ::removeComponent) {
        component.ssaoState.use().let { ssao ->
            Column(width = Grow.Std) {
                modifier
                    .padding(horizontal = sizes.gap)
                    .margin(bottom = sizes.smallGap)

                labeledDoubleTextField(
                    label = "Radius:",
                    value = ssao.radius.toDouble(),
                    precision = 3,
                    minValue = 0.0,
                    maxValue = if (ssao.isRelativeRadius) 1.0 else Double.POSITIVE_INFINITY,
                    dragChangeSpeed = if (ssao.isRelativeRadius) DragChangeRates.RANGE_0_TO_1 else DragChangeRates.RANGE_0_TO_1 * 10.0,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val oldSettings = component.ssaoState.value.copy(radius = undoValue.toFloat())
                        val newSettings = component.ssaoState.value.copy(radius = applyValue.toFloat())
                        SetSsaoSettingsAction(component, oldSettings, newSettings)
                    }
                )

                labeledCheckbox("Distance-based radius:", ssao.isRelativeRadius) {
                    val oldSettings = component.ssaoState.value
                    val allowedMax = if (it) 1f else Float.POSITIVE_INFINITY
                    val clampedRadius = oldSettings.radius.clamp(0f, allowedMax)
                    val newSettings = component.ssaoState.value.copy(isRelativeRadius = it, radius = clampedRadius)
                    SetSsaoSettingsAction(component, oldSettings, newSettings).apply()
                }

                labeledDoubleTextField(
                    label = "Strength:",
                    value = ssao.strength.toDouble(),
                    precision = 3,
                    minValue = 0.0,
                    maxValue = 5.0,
                    dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 5.0,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val oldSettings = component.ssaoState.value.copy(strength = undoValue.toFloat())
                        val newSettings = component.ssaoState.value.copy(strength = applyValue.toFloat())
                        SetSsaoSettingsAction(component, oldSettings, newSettings)
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
                        val oldSettings = component.ssaoState.value.copy(power = undoValue.toFloat())
                        val newSettings = component.ssaoState.value.copy(power = applyValue.toFloat())
                        SetSsaoSettingsAction(component, oldSettings, newSettings)
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
                        val oldSettings = component.ssaoState.value.copy(mapSize = undoValue.toFloat())
                        val newSettings = component.ssaoState.value.copy(mapSize = applyValue.toFloat())
                        SetSsaoSettingsAction(component, oldSettings, newSettings)
                    }
                )

                labeledIntTextField(
                    label = "Number of samples:",
                    value = ssao.samples,
                    minValue = 2,
                    maxValue = 64,
                    dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 64.0,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val oldSettings = component.ssaoState.value.copy(samples = undoValue)
                        val newSettings = component.ssaoState.value.copy(samples = applyValue)
                        SetSsaoSettingsAction(component, oldSettings, newSettings)
                    }
                )

                Button("Restore defaults") {
                    defaultButtonStyle()
                    modifier
                        .width(sizes.baseSize * 5)
                        .margin(vertical = sizes.smallGap)
                        .alignX(AlignmentX.Center)
                        .onClick {
                            val oldSettings = component.ssaoState.value
                            SetSsaoSettingsAction(component, oldSettings, SsaoSettings()).apply()
                        }
                }
            }
        }
    }
}