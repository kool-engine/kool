package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetMaterialAction
import de.fabmax.kool.editor.actions.UpdateMaterialAction
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class MaterialEditor(var sceneNodeModel: SceneNodeModel, var materialComponent: MaterialComponent) : Composable {

    private var undoMaterial: MaterialShaderData? = null

    override fun UiScope.compose() = collapsapsablePanel(
        title = "Material",
        scopeName = "material-${materialComponent.materialData?.id}"
    ) {

        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val (items, idx) = makeMaterialItemsAndIndex()
            val selectedIndex = remember(idx)
            selectedIndex.set(idx)
            labeledCombobox("Material:", items, selectedIndex) { item ->
                EditorActions.applyAction(SetMaterialAction(sceneNodeModel, materialComponent, item.getMaterialModel()))
            }

            materialComponent.materialState.use()?.let { selectedMaterial ->
                menuDivider()
                materialEditor(selectedMaterial)
            }
        }
    }

    private fun UiScope.materialEditor(material: MaterialData) {
        when (val shaderData = material.shaderData) {
            is BlinnPhongShaderData -> TODO()
            is PbrShaderData -> pbrMaterialEditor(material, shaderData)
            is UnlitShaderData -> TODO()
        }
    }

    private fun UiScope.pbrMaterialEditor(material: MaterialData, pbrData: PbrShaderData) {
        // shader setting callback functions need to use cast material.shaderData instead of pbrData because otherwise
        // pbrData is captured on first invocation and will never be updated

        (pbrData.baseColor as? ConstColorAttribute)?.let { baseColor ->
            colorSetting("Base color:", baseColor, material) {
                (material.shaderData as PbrShaderData).copy(baseColor = it)
            }
        }
        (pbrData.roughness as? ConstValueAttribute)?.let { roughness ->
            floatSetting("Roughness:", roughness, 0f, 1f, material) {
                (material.shaderData as PbrShaderData).copy(roughness = it)
            }
        }
        (pbrData.metallic as? ConstValueAttribute)?.let { metallic ->
            floatSetting("Metallic:", metallic, 0f, 1f, material) {
                (material.shaderData as PbrShaderData).copy(metallic = it)
            }
        }
        (pbrData.emission as? ConstColorAttribute)?.let { emission ->
            colorSetting("Emission color:", emission, material) {
                (material.shaderData as PbrShaderData).copy(emission = it)
            }
        }
    }

    private fun UiScope.colorSetting(
        label: String,
        colorAttr: ConstColorAttribute,
        material: MaterialData,
        shaderDataSetter: (MaterialAttribute) -> MaterialShaderData
    ) {
        val availableTextures = KoolEditor.instance.availableAssets.textureAssets.use()
                .filter { !it.name.lowercase().endsWith(".rgbe.png") }

        menuRow {
            Text(label) {
                modifier
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
            }

            Box(width = sizes.baseSize * 2f, height = sizes.lineHeight) {
                val colorEditHandler = ActionValueEditHandler<Color> { undoValue, applyValue ->
                    val undoMaterial = shaderDataSetter(ConstColorAttribute(ColorData(undoValue.toLinear())))
                    val applyMaterial = shaderDataSetter(ConstColorAttribute(ColorData(applyValue.toLinear())))
                    UpdateMaterialAction(material, applyMaterial, undoMaterial)
                }
                colorPicker(colorAttr.color.toColor().toSrgb(), editHandler = colorEditHandler).apply {
                    modifier.width(sizes.baseSize * 2f + sizes.smallGap)
                }
//                Text("uv_checker_map.png") {
//                    modifier
//                        .size(sizes.baseSize * 2f + sizes.smallGap, sizes.lineHeight)
//                        .padding(start = sizes.smallGap)
//                        .background(RoundRectBackground(colors.componentBg, sizes.smallGap))
//                        .onClick {
//                            availableTextures.getOrNull(0)?.let { tex ->
//                                val mat = shaderDataSetter(MapAttribute(tex.path))
//                                EditorActions.applyAction(UpdateMaterialAction(material, mat, mat))
//                            }
//                        }
//                }
            }
            Box(width = sizes.largeGap * 1.51f) {
                Box(width = sizes.largeGap * 1.51f + sizes.smallGap, height = sizes.lineHeight) {
                    var isHovered by remember(false)
                    val bgColor = if (isHovered) colors.elevatedComponentBgHovered else colors.elevatedComponentBg
                    modifier
                        .margin(start = sizes.smallGap * -1)
                        .background(RoundRectBackground(bgColor, sizes.smallGap))
                        .onEnter { isHovered = true }
                        .onExit { isHovered = false }
                }
            }
        }
    }

    private inline fun UiScope.floatSetting(
        label: String,
        floatAttr: ConstValueAttribute,
        min: Float,
        max: Float,
        material: MaterialData,
        crossinline shaderDataSetter: (ConstValueAttribute) -> MaterialShaderData
    ) {
        var editValue by remember(floatAttr.value)
        labeledDoubleTextField(
            label = label,
            value = floatAttr.value.toDouble(),
            precision = precisionForValue((max - min).toDouble()),
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
            minValue = min.toDouble(),
            maxValue = max.toDouble(),
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                editValue = applyValue.toFloat()
                val undoMaterial = shaderDataSetter(ConstValueAttribute(undoValue.toFloat()))
                val applyMaterial = shaderDataSetter(ConstValueAttribute(editValue))
                UpdateMaterialAction(material, applyMaterial, undoMaterial)
            }
        )
    }

    private fun makeMaterialItemsAndIndex(): Pair<List<MaterialItem>, Int> {
        val items = mutableListOf(
            MaterialItem("Default", null),
            MaterialItem("New material", null)
        )
        var index = 0
        EditorState.projectModel.materials.values.forEachIndexed { i, material ->
            if (materialComponent.isHoldingMaterial(material)) {
                index = i + 2
            }
            items += MaterialItem(material.name, material)
        }
        return items to index
    }

    private class MaterialItem(val itemText: String, val material: MaterialData?) {
        override fun toString(): String = itemText

        fun getMaterialModel(): MaterialData? {
            return material ?: if (itemText == "New material") EditorState.projectModel.createNewMaterial() else null
        }
    }
}