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

    override fun UiScope.compose() = componentPanel(title = "Light", ::removeComponent) {
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
                        else -> LightTypeData.Point(color)
                    }
                    SetDiscreteLightAction(component, newLight).apply()
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
        labeledColorPicker("Color:", currentLight.color.toColorSrgb()) {
            val chgLight = when (val light = currentLight) {
                is LightTypeData.Directional -> light.copy(color = ColorData(it.toLinear()))
                is LightTypeData.Point -> light.copy(color = ColorData(it.toLinear()))
                is LightTypeData.Spot -> light.copy(color = ColorData(it.toLinear()))
            }
            SetDiscreteLightAction(component, chgLight).apply()
        }

        val light = currentLight
        val dragChangeSpeed = if (light is LightTypeData.Directional) 0.05 else 10.0
        labeledDoubleTextField("Strength:", light.intensity.toDouble(), minValue = 0.0, dragChangeSpeed = dragChangeSpeed) {
            val chgLight = when (light) {
                is LightTypeData.Directional -> light.copy(intensity = it.toFloat())
                is LightTypeData.Point -> light.copy(intensity = it.toFloat())
                is LightTypeData.Spot -> light.copy(intensity = it.toFloat())
            }
            SetDiscreteLightAction(component, chgLight).apply()
        }
    }

    private fun UiScope.spotSettings(spot: LightTypeData.Spot) {
        labeledDoubleTextField("Angle:", spot.spotAngle.toDouble(), minValue = 0.0, maxValue = 120.0, dragChangeSpeed = 0.5) {
            SetDiscreteLightAction(component, spot.copy(spotAngle = it.toFloat())).apply()
        }
        labeledDoubleTextField("Hardness:", spot.coreRatio.toDouble(), minValue = 0.0, maxValue = 1.0, dragChangeSpeed = 0.01) {
            SetDiscreteLightAction(component, spot.copy(coreRatio = it.toFloat())).apply()
        }
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