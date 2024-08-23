package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.modules.ksl.KslPbrSplatShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.max

class PbrSplatMaterialEditor(
    material: MaterialComponent,
    materialData: PbrSplatShaderData,
    editor: ComponentEditor<*>
) : MaterialDataEditor<PbrSplatShaderData>(material, materialData, editor) {

    override fun ColumnScope.materialEditor() {
        textureSetting("Weight map:", materialData.splatMap, "r") {
            materialData.copy(splatMap = it)
        }
        genericSettings()

        labeledCombobox("Debug mode:", debugOptions, materialData.debugMode) {
            val undoMaterial = material.data.copy(shaderData = materialData)
            val applyMaterial = material.data.copy(shaderData = materialData.copy(debugMode = it.debugValue))
            SetComponentDataAction(material, undoMaterial, applyMaterial).apply()
        }

        val numMats = max(2, materialData.materialMaps.size)
        for (i in 0 until numMats) {
            val panelKey = "splat-mat-$i"

            collapsablePanelLvl2(
                title = "Material ${i + 1}",
                titleWidth = sizes.editorLabelWidthSmall - sizes.gap * 2f,
                startExpanded = editor.getPanelState(false, panelKey = panelKey),
                indicatorColor = splatIndiColors[i % splatIndiColors.size],
                isAlwaysShowIndicator = true,
                headerContent = { splatMaterialPreview(getSplatMaterial(i).baseColor, i) },
                onCollapseChanged = { editor.setPanelState(it, panelKey = panelKey) }
            ) {
                val mat = getSplatMaterial(i)
                textureSetting("Displacement:", mat.displacementMap, "r", texSingleChannels) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(displacementMap = it?.copy(format = TexFormat.R)))
                }
                colorSetting("Base color:", mat.baseColor, MdColor.GREY.toLinear()) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(baseColor = it))
                }
                textureSetting("Normal map:", mat.normalMap, null) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(normalMap = it))
                }
                textureSetting("AO:", mat.aoMap, "r", texSingleChannels) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(aoMap = it))
                }
                floatSetting("Roughness:", mat.roughness, 0f, 1f, 0.5f) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(roughness = it))
                }
                floatSetting("Metallic:", mat.metallic, 0f, 1f, 0f) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(metallic = it))
                }
                colorSetting("Emission color:", mat.emission, Color.BLACK) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(emission = it))
                }
                menuDivider()
                labeledDoubleTextField(
                    label = "Texture scale:",
                    value = mat.textureScale.toDouble(),
                    dragChangeSpeed = DragChangeRates.SIZE,
                    minValue = 0.0,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMats = setSplatMaterial(i, getSplatMaterial(i).copy(textureScale = undoValue.toFloat()))
                        val applyMats = setSplatMaterial(i, getSplatMaterial(i).copy(textureScale = applyValue.toFloat()))
                        SetComponentDataAction(material, material.data.copy(shaderData = undoMats), material.data.copy(shaderData = applyMats))
                    }
                )
                labeledDoubleTextField(
                    label = "Texture rotation:",
                    value = mat.textureRotation.toDouble(),
                    dragChangeSpeed = DragChangeRates.SIZE,
                    minValue = 0.0,
                    maxValue = 360.0,
                    precision = 1,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMats = setSplatMaterial(i, getSplatMaterial(i).copy(textureRotation = undoValue.toFloat()))
                        val applyMats = setSplatMaterial(i, getSplatMaterial(i).copy(textureRotation = applyValue.toFloat()))
                        SetComponentDataAction(material, material.data.copy(shaderData = undoMats), material.data.copy(shaderData = applyMats))
                    }
                )
                labeledDoubleTextField(
                    label = "Tiling scale:",
                    value = mat.stochasticTileSize.toDouble(),
                    dragChangeSpeed = DragChangeRates.SIZE,
                    minValue = 0.0,
                    maxValue = 1.0,
                    precision = 3,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMats = setSplatMaterial(i, getSplatMaterial(i).copy(stochasticTileSize = undoValue.toFloat()))
                        val applyMats = setSplatMaterial(i, getSplatMaterial(i).copy(stochasticTileSize = applyValue.toFloat()))
                        SetComponentDataAction(material, material.data.copy(shaderData = undoMats), material.data.copy(shaderData = applyMats))
                    }
                )
                labeledDoubleTextField(
                    label = "Tiling rotation:",
                    value = mat.stochasticRotation.toDouble(),
                    dragChangeSpeed = DragChangeRates.SIZE,
                    minValue = 0.0,
                    maxValue = 360.0,
                    precision = 1,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMats = setSplatMaterial(i, getSplatMaterial(i).copy(stochasticRotation = undoValue.toFloat()))
                        val applyMats = setSplatMaterial(i, getSplatMaterial(i).copy(stochasticRotation = applyValue.toFloat()))
                        SetComponentDataAction(material, material.data.copy(shaderData = undoMats), material.data.copy(shaderData = applyMats))
                    }
                )
            }
        }

        if (materialData.materialMaps.size < 5) {
            iconTextButton(
                icon = Icons.small.plus,
                text = "Add Material",
                width = sizes.baseSize * 5,
                margin = sizes.gap
            ) {
                val newMats = materialData.copy(materialMaps = materialData.materialMaps + SplatMapData())
                SetComponentDataAction(material, material.data, material.data.copy(shaderData = newMats)).apply()
            }
        }
    }

    private fun getSplatMaterial(index: Int): SplatMapData {
        return materialData.materialMaps.getOrNull(index) ?: SplatMapData()
    }

    private fun setSplatMaterial(index: Int, mat: SplatMapData): PbrSplatShaderData {
        val mats = materialData.materialMaps
        val newMats = if (index >= mats.size) {
            mats + mat
        } else {
            mats.toMutableList().also { it[index] = mat }
        }
        return materialData.copy(materialMaps = newMats)
    }

    private fun RowScope.splatMaterialPreview(baseColor: MaterialAttribute, matIndex: Int) {
        val mapPath = (baseColor as? MapAttribute)?.mapPath
        if (mapPath != null) {
            val tex = KoolEditor.instance.cachedAppAssets.getTextureMutableState(AssetReference.Texture(mapPath)).use()
            Image(tex) {
                modifier
                    .size(sizes.lineHeight, sizes.lineHeight)
                    .alignY(AlignmentY.Center)
            }
        } else {
            val color = (baseColor as? ConstColorAttribute)?.color?.toColorSrgb() ?: MdColor.GREY
            Box(width = sizes.lineHeight, height = sizes.lineHeight) {
                modifier
                    .alignY(AlignmentY.Center)
                    .backgroundColor(color)
            }
        }

        if (matIndex >= 2 && matIndex == materialData.materialMaps.lastIndex) {
            Box(width = Grow.Std) {}
            Box {
                var isHovered by remember(false)
                val fgColor = colors.onBackground
                val bgColor = if (isHovered) MdColor.RED else colors.componentBg

                modifier
                    .alignY(AlignmentY.Center)
                    .margin(start = sizes.largeGap, end = sizes.gap * 0.75f)
                    .padding(sizes.smallGap * 0.5f)
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }
                    .onClick {
                        val withoutLast = materialData.copy(materialMaps = materialData.materialMaps.take(matIndex))
                        SetComponentDataAction(material, material.data, material.data.copy(shaderData = withoutLast)).apply()
                    }
                    .background(CircularBackground(bgColor))

                Image {
                    modifier.iconImage(Icons.small.trash, fgColor)
                }
            }
        }
    }

    private data class DebugOption(val label: String, val debugValue: Int) {
        override fun toString(): String = label
    }

    companion object {
        private val splatIndiColors = listOf(
            MdColor.GREEN.withAlpha(0.8f),
            MdColor.BLUE.mix(MdColor.INDIGO, 0.5f).withAlpha(0.8f),
            MdColor.PURPLE.withAlpha(0.8f),
            MdColor.RED.withAlpha(0.8f),
            MdColor.AMBER.withAlpha(0.8f),
        )

        private val debugOptions = listOf(
            DebugOption("Off", KslPbrSplatShader.DEBUG_MODE_OFF),
            DebugOption("Weights", KslPbrSplatShader.DEBUG_MODE_WEIGHTS),
            DebugOption("Normals", KslPbrSplatShader.DEBUG_MODE_NORMALS),
            DebugOption("Displacement", KslPbrSplatShader.DEBUG_MODE_DISPLACEMENT),
        )

        private val texSingleChannels = listOf("R", "G", "B", "A")
    }
}