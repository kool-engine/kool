package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.PbrShaderData
import de.fabmax.kool.editor.ui.ActionValueEditHandler
import de.fabmax.kool.editor.ui.DragChangeRates
import de.fabmax.kool.editor.ui.labeledDoubleTextField
import de.fabmax.kool.editor.ui.menuDivider
import de.fabmax.kool.modules.ui2.ColumnScope
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class PbrMaterialEditor(
    material: MaterialComponent,
    materialData: PbrShaderData,
    editor: ComponentEditor<*>
) : MaterialDataEditor<PbrShaderData>(material, materialData, editor) {

    override fun ColumnScope.materialEditor() {
        colorSetting("Base color:", materialData.baseColor, MdColor.GREY.toLinear()) {
            materialData.copy(baseColor = it)
        }
        textureSetting("Normal map:", materialData.normalMap, null) {
            materialData.copy(normalMap = it)
        }
        textureSetting("AO:", materialData.aoMap, "r", texSingleChannels) {
            materialData.copy(aoMap = it)
        }
        floatSetting("Roughness:", materialData.roughness, 0f, 1f, 0.5f) {
            materialData.copy(roughness = it)
        }
        floatSetting("Metallic:", materialData.metallic, 0f, 1f, 0f) {
            materialData.copy(metallic = it)
        }
        textureSetting("Displacement:", materialData.displacementMap, "r", texSingleChannels) {
            materialData.copy(displacementMap = it)
        }
        colorSetting("Emission color:", materialData.emission, Color.BLACK) {
            materialData.copy(emission = it)
        }

        if (materialData.displacementMap != null) {
            labeledDoubleTextField(
                label = "Strength:",
                value = materialData.parallaxStrength.toDouble(),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val undoData = material.data.copy(shaderData = materialData.copy(parallaxStrength = undo.toFloat()))
                    val applyData = material.data.copy(shaderData = materialData.copy(parallaxStrength = apply.toFloat()))
                    SetComponentDataAction(material, undoData, applyData)
                }
            )
            labeledDoubleTextField(
                label = "Offset:",
                value = materialData.parallaxOffset.toDouble(),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val undoData = material.data.copy(shaderData = materialData.copy(parallaxOffset = undo.toFloat()))
                    val applyData = material.data.copy(shaderData = materialData.copy(parallaxOffset = apply.toFloat()))
                    SetComponentDataAction(material, undoData, applyData)
                }
            )
            labeledDoubleTextField(
                label = "Steps:",
                value = materialData.parallaxSteps.toDouble(),
                minValue = 2.0,
                maxValue = 64.0,
                precision = 0,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val undoData = material.data.copy(shaderData = materialData.copy(parallaxSteps = undo.toInt()))
                    val applyData = material.data.copy(shaderData = materialData.copy(parallaxSteps = apply.toInt()))
                    SetComponentDataAction(material, undoData, applyData)
                }
            )
        }
        menuDivider()
        genericSettings()
    }
}