package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.FusedAction
import de.fabmax.kool.editor.actions.SetDiscreteLightAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.math.toVec4d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class LightEditor : ComponentEditor<DiscreteLightComponent>() {

    private var editLightsStart = listOf<LightTypeData>()

    override fun UiScope.compose() = componentPanel("Light", IconMap.small.light, ::removeComponent) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val lightData = components.map { it.lightState.use() }
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
                        val color = component.lightState.value.color
                        val newLight = when (lightType) {
                            TypeOption.Directional -> LightTypeData.Directional(color)
                            TypeOption.Spot -> LightTypeData.Spot(color)
                            TypeOption.Point -> LightTypeData.Point(color)
                        }
                        SetDiscreteLightAction(component.nodeModel.nodeId, newLight, component.lightState.value)
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
    }

    private fun UiScope.colorSettings() {
        val colors = condenseVec4(components.map { it.lightState.value.color.toColorSrgb().toVec4f().toVec4d() })
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
                    editLightsStart = components.map { it.lightState.value }
                }

                override fun makeEditAction(undoValue: Color, applyValue: Color): EditorAction {
                    return components.mapIndexed { i, component ->
                        val applyLight: LightTypeData
                        val undoLight: LightTypeData = editLightsStart[i]
                        when (val light = component.lightState.value) {
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
                        SetDiscreteLightAction(component.nodeModel.nodeId, applyLight, undoLight)
                    }.fused()
                }

            }
        )

        val isAnyDirectional = components.any { it.lightState.value is LightTypeData.Directional }
        val dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * if (isAnyDirectional) 5.0 else 1000.0
        labeledDoubleTextField(
            label = "Strength:",
            value = condenseDouble(components.map { it.lightState.value.intensity.toDouble() }),
            minValue = 0.0,
            dragChangeSpeed = dragChangeSpeed,
            editHandler = ActionValueEditHandler { undo, apply ->
                components.map { component ->
                    val mergedUndo = mergeDouble(undo, component.lightState.value.intensity.toDouble()).toFloat()
                    val mergedApply = mergeDouble(apply, component.lightState.value.intensity.toDouble()).toFloat()
                    val applyLight: LightTypeData
                    val undoLight: LightTypeData
                    when (val light = component.lightState.value) {
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
                    SetDiscreteLightAction(component.nodeModel.nodeId, applyLight, undoLight)
                }.fused()
            }
        )
    }

    private fun UiScope.spotSettings() {
        val spots = components.map { it.lightState.value as LightTypeData.Spot }
        labeledDoubleTextField(
            label = "Angle:",
            value = condenseDouble(spots.map { it.spotAngle.toDouble() }),
            minValue = 0.0,
            maxValue = 120.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 90,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.lightState.value as LightTypeData.Spot }
                components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, props[i].spotAngle.toDouble())
                    val mergedApply = mergeDouble(apply, props[i].spotAngle.toDouble())
                    val undoProps = props[i].copy(spotAngle = mergedUndo.toFloat())
                    val applyProps = props[i].copy(spotAngle = mergedApply.toFloat())
                    SetDiscreteLightAction(component.nodeModel.nodeId, applyProps, undoProps)
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
                val props = components.map { it.lightState.value as LightTypeData.Spot }
                components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, props[i].coreRatio.toDouble())
                    val mergedApply = mergeDouble(apply, props[i].coreRatio.toDouble())
                    val undoProps = props[i].copy(coreRatio = mergedUndo.toFloat())
                    val applyProps = props[i].copy(coreRatio = mergedApply.toFloat())
                    SetDiscreteLightAction(component.nodeModel.nodeId, applyProps, undoProps)
                }.fused()
            }
        )
    }

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