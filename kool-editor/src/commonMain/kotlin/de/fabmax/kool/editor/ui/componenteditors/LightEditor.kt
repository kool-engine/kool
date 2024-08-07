package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.FusedAction
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.math.toVec4d
import de.fabmax.kool.modules.ui2.ColumnScope
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.util.Color

class LightEditor : ComponentEditor<DiscreteLightComponent>() {

    private var editLightsStart = listOf<LightTypeData>()

    override fun UiScope.compose() = componentPanel("Light", Icons.small.light, ::removeComponent) {
        val lightData = components.map { it.dataState.use().light }
        val (typeItems, typeIdx) = typeOptions.getOptionsAndIndex(lightData.map { it.typeOption })
        labeledCombobox(
            label = "Type:",
            items = typeItems,
            selectedIndex = typeIdx
        ) { selected ->
            selected.item?.let { lightType ->
                val actions = components.map { component ->
                    // keep color when switching light type, but not intensity (because directional lights have
                    // drastically different intensity range than point- / spot-lights)
                    val color = component.data.light.color
                    val newLight = when (lightType) {
                        TypeOption.Directional -> LightTypeData.Directional(color)
                        TypeOption.Spot -> LightTypeData.Spot(color)
                        TypeOption.Point -> LightTypeData.Point(color)
                    }
                    setLightDataAction(component, component.data.light, newLight)
                }
                FusedAction(actions).apply()
            }
        }

        menuDivider()
        colorSettings()

        if (lightData.all { it is LightTypeData.Spot }) {
            spotSettings()
        }
    }

    private fun ColumnScope.colorSettings() {
        val colors = condenseVec4(components.map { it.data.light.color.toColorSrgb().toVec4f().toVec4d() })
        val color = if (colors.x.isFinite() && colors.y.isFinite() && colors.z.isFinite() && colors.w.isFinite()) {
            Color(colors.x.toFloat(), colors.y.toFloat(), colors.z.toFloat(), colors.w.toFloat())
        } else {
            Color.WHITE
        }

        labeledColorPicker(
            label = "Color:",
            pickerColor = color,
            editHandler = object : ActionValueEditHandler<Color> {
                override fun onEditStart(startValue: Color) {
                    editLightsStart = components.map { it.data.light }
                }

                override fun makeEditAction(undoValue: Color, applyValue: Color): EditorAction {
                    return components.mapIndexed { i, component ->
                        val applyLight: LightTypeData
                        val undoLight: LightTypeData = editLightsStart[i]
                        when (val light = component.data.light) {
                            is LightTypeData.Directional -> {
                                applyLight = light.copy(color = ColorData(applyValue.toLinear()))
                            }
                            is LightTypeData.Point -> {
                                applyLight = light.copy(color = ColorData(applyValue.toLinear()))
                            }
                            is LightTypeData.Spot -> {
                                applyLight = light.copy(color = ColorData(applyValue.toLinear()))
                            }
                        }
                        setLightDataAction(component, undoLight, applyLight)
                    }.fused()
                }

            }
        )

        val isAnyDirectional = components.any { it.data.light is LightTypeData.Directional }
        val dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * if (isAnyDirectional) 5.0 else 1000.0
        labeledDoubleTextField(
            label = "Strength:",
            value = condenseDouble(components.map { it.data.light.intensity.toDouble() }),
            minValue = 0.0,
            dragChangeSpeed = dragChangeSpeed,
            editHandler = ActionValueEditHandler { undo, apply ->
                components.map { component ->
                    val mergedUndo = mergeDouble(undo, component.data.light.intensity.toDouble()).toFloat()
                    val mergedApply = mergeDouble(apply, component.data.light.intensity.toDouble()).toFloat()
                    val applyLight: LightTypeData
                    val undoLight: LightTypeData
                    when (val light = component.data.light) {
                        is LightTypeData.Directional -> {
                            applyLight = light.copy(intensity = mergedApply)
                            undoLight = light.copy(intensity = mergedUndo)
                        }
                        is LightTypeData.Point -> {
                            applyLight = light.copy(intensity = mergedApply)
                            undoLight = light.copy(intensity = mergedUndo)
                        }
                        is LightTypeData.Spot -> {
                            applyLight = light.copy(intensity = mergedApply)
                            undoLight = light.copy(intensity = mergedUndo)
                        }
                    }
                    setLightDataAction(component, undoLight, applyLight)
                }.fused()
            }
        )
    }

    private fun ColumnScope.spotSettings() {
        val spots = components.map { it.data.light as LightTypeData.Spot }
        labeledDoubleTextField(
            label = "Angle:",
            value = condenseDouble(spots.map { it.spotAngle.toDouble() }),
            minValue = 0.0,
            maxValue = 120.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 90,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.data.light as LightTypeData.Spot }
                components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, props[i].spotAngle.toDouble())
                    val mergedApply = mergeDouble(apply, props[i].spotAngle.toDouble())
                    val undoProps = props[i].copy(spotAngle = mergedUndo.toFloat())
                    val applyProps = props[i].copy(spotAngle = mergedApply.toFloat())
                    setLightDataAction(component, undoProps, applyProps)
                }.fused()
            }
        )
        labeledDoubleTextField(
            label = "Hardness:",
            value = condenseDouble(spots.map { it.coreRatio.toDouble() }),
            minValue = 0.0,
            maxValue = 1.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.data.light as LightTypeData.Spot }
                components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, props[i].coreRatio.toDouble())
                    val mergedApply = mergeDouble(apply, props[i].coreRatio.toDouble())
                    val undoProps = props[i].copy(coreRatio = mergedUndo.toFloat())
                    val applyProps = props[i].copy(coreRatio = mergedApply.toFloat())
                    setLightDataAction(component, undoProps, applyProps)
                }.fused()
            }
        )
    }

    private fun setLightDataAction(component: DiscreteLightComponent, oldLightData: LightTypeData, newLightData: LightTypeData) =
        SetComponentDataAction(component, component.data.copy(light = oldLightData), component.data.copy(light = newLightData))

    private val LightTypeData.typeOption: TypeOption get() = TypeOption.entries.first { it.matches(this) }

    private enum class TypeOption(val label: String, val matches: (LightTypeData?) -> Boolean) {
        Directional("Directional", { it is LightTypeData.Directional }),
        Spot("Spot", { it is LightTypeData.Spot }),
        Point("Point", { it is LightTypeData.Point }),
    }

    companion object {
        private val typeOptions = ComboBoxItems(TypeOption.entries) { it.label }
    }
}