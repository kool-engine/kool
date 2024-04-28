package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetDiscreteLightAction
import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

class LightEditor(component: DiscreteLightComponent) : ComponentEditor<DiscreteLightComponent>(component) {

    private val currentLight: LightTypeData get() = component.lightState.value
    private val lightTypeIndex: Int
        get() = lightTypes.indexOfFirst { it.lightType.isInstance(currentLight) }

    override fun UiScope.compose() = componentPanel("Light", IconMap.small.light, ::removeComponent) {
        component.lightState.use().let { light ->
            Column(width = Grow.Std) {
                modifier
                    .padding(horizontal = sizes.gap)
                    .margin(bottom = sizes.smallGap)

                labeledCombobox("Type:", lightTypes, lightTypeIndex) {
                    // keep color when switching light type, but not intensity (because directional lights have
                    // drastically different intensity range than point- / spot-lights)
                    val color = currentLight.color
                    val newLight = when (it.lightType) {
                        LightTypeData.Directional::class -> LightTypeData.Directional(color)
                        LightTypeData.Spot::class -> LightTypeData.Spot(color)
                        LightTypeData.Point::class -> LightTypeData.Point(color)
                        else -> throw IllegalStateException("Unsupported light type: ${it.lightType}")
                    }
                    SetDiscreteLightAction(component, newLight, currentLight).apply()
                }

                menuDivider()
                colorSettings()

                if (light is LightTypeData.Spot) {
                    spotSettings(light)
                }
            }
        }
    }

    private fun UiScope.colorSettings() {
        labeledColorPicker(
            label = "Color:",
            pickerColor = currentLight.color.toColorSrgb(),
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val applyLight: LightTypeData
                val undoLight: LightTypeData
                when (val light = currentLight) {
                    is LightTypeData.Directional -> {
                        applyLight = light.copy(color = ColorData(applyValue.toLinear()))
                        undoLight = light.copy(color = ColorData(undoValue.toLinear()))
                    }
                    is LightTypeData.Point -> {
                        applyLight = light.copy(color = ColorData(applyValue.toLinear()))
                        undoLight = light.copy(color = ColorData(undoValue.toLinear()))
                    }
                    is LightTypeData.Spot -> {
                        applyLight = light.copy(color = ColorData(applyValue.toLinear()))
                        undoLight = light.copy(color = ColorData(undoValue.toLinear()))
                    }
                }
                SetDiscreteLightAction(component, applyLight, undoLight)
            }
        )

        val dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * if (currentLight is LightTypeData.Directional) 5.0 else 1000.0
        labeledDoubleTextField(
            label = "Strength:",
            value = currentLight.intensity.toDouble(),
            minValue = 0.0,
            dragChangeSpeed = dragChangeSpeed,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val applyLight: LightTypeData
                val undoLight: LightTypeData
                when (val light = currentLight) {
                    is LightTypeData.Directional -> {
                        applyLight = light.copy(intensity = applyValue.toFloat())
                        undoLight = light.copy(intensity = undoValue.toFloat())
                    }
                    is LightTypeData.Point -> {
                        applyLight = light.copy(intensity = applyValue.toFloat())
                        undoLight = light.copy(intensity = undoValue.toFloat())
                    }
                    is LightTypeData.Spot -> {
                        applyLight = light.copy(intensity = applyValue.toFloat())
                        undoLight = light.copy(intensity = undoValue.toFloat())
                    }
                }
                SetDiscreteLightAction(component, applyLight, undoLight)
            }
        )
    }

    private fun UiScope.spotSettings(spot: LightTypeData.Spot) {
        labeledDoubleTextField(
            label = "Angle:",
            value = spot.spotAngle.toDouble(),
            minValue = 0.0,
            maxValue = 120.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 90,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val applyLight = spot.copy(spotAngle = applyValue.toFloat())
                val undoLight = spot.copy(spotAngle = undoValue.toFloat())
                SetDiscreteLightAction(component, applyLight, undoLight)
            }
        )
        labeledDoubleTextField(
            label = "Hardness:",
            value = spot.coreRatio.toDouble(),
            minValue = 0.0,
            maxValue = 1.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val applyLight = spot.copy(coreRatio = applyValue.toFloat())
                val undoLight = spot.copy(coreRatio = undoValue.toFloat())
                SetDiscreteLightAction(component, applyLight, undoLight)
            }
        )
    }

    private class LightTypeOption<T: LightTypeData>(val name: String, val lightType: KClass<T>) {
        override fun toString(): String = name
    }

    companion object {
        private val lightTypes = listOf(
            LightTypeOption("Directional", LightTypeData.Directional::class),
            LightTypeOption("Spot", LightTypeData.Spot::class),
            LightTypeOption("Point", LightTypeData.Point::class)
        )
    }
}