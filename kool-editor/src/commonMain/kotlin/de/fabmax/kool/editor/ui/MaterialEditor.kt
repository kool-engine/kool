package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.RenameMaterialAction
import de.fabmax.kool.editor.actions.SetMaterialAction
import de.fabmax.kool.editor.actions.UpdateMaterialAction
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class MaterialEditor(var sceneNodeModel: SceneNodeModel, var materialComponent: MaterialComponent) : Composable {

    private var undoMaterial: MaterialShaderData? = null

    override fun UiScope.compose() = collapsapsablePanel(
        title = "Material",
        headerContent = {
            val (items, idx) = makeMaterialItemsAndIndex()
            var selectedIndex by remember(idx)
            selectedIndex = idx

            Box(Grow.Std) { }
            ComboBox {
                defaultComboBoxStyle()
                modifier
                    .margin(end = sizes.gap)
                    .size(sizes.baseSize * 4, sizes.lineHeight)
                    .alignY(AlignmentY.Center)
                    .items(items)
                    .selectedIndex(selectedIndex)
                    .onItemSelected {
                        EditorActions.applyAction(SetMaterialAction(sceneNodeModel, materialComponent, items[it].getMaterialModel()))
                    }
            }
        }
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            materialComponent.materialState.use()?.let { selectedMaterial ->
                labeledTextField("Name:", selectedMaterial.name) {
                    EditorActions.applyAction(RenameMaterialAction(selectedMaterial, it, selectedMaterial.name))
                }

                menuDivider()
                materialEditor(selectedMaterial)
                menuDivider()
                genericSettings(selectedMaterial)
            }
        }
    }

    private fun UiScope.genericSettings(material: MaterialData) {
        var isTwoSided by remember(material.shaderData.genericSettings.isTwoSided)
        labeledSwitch("Is two-sided:", isTwoSided) {
            isTwoSided = it

            val undoMaterial = material.shaderData
            val applyMaterial = material.shaderData.copy(genericSettings = material.shaderData.genericSettings.copy(it))
            EditorActions.applyAction(UpdateMaterialAction(material, applyMaterial, undoMaterial))
        }
    }

    private fun UiScope.materialEditor(material: MaterialData) {
        when (val shaderData = material.shaderDataState.use()) {
            is BlinnPhongShaderData -> TODO()
            is PbrShaderData -> pbrMaterialEditor(material, shaderData)
            is UnlitShaderData -> TODO()
        }
    }

    private fun UiScope.pbrMaterialEditor(material: MaterialData, pbrData: PbrShaderData) {
        // shader setting callback functions need to use cast material.shaderData instead of pbrData because otherwise
        // pbrData is captured on first invocation and will never be updated

        colorSetting("Base color:", pbrData.baseColor, MdColor.GREY.toLinear(), material) {
            (material.shaderData as PbrShaderData).copy(baseColor = it)
        }
        floatSetting("Roughness:", pbrData.roughness, 0f, 1f, 0.5f, material) {
            (material.shaderData as PbrShaderData).copy(roughness = it)
        }
        floatSetting("Metallic:", pbrData.metallic, 0f, 1f, 0f, material) {
            (material.shaderData as PbrShaderData).copy(metallic = it)
        }
        colorSetting("Emission color:", pbrData.emission, Color.BLACK, material) {
            (material.shaderData as PbrShaderData).copy(emission = it)
        }
        textureSetting("Normal map:", pbrData.normalMap, material) {
            (material.shaderData as PbrShaderData).copy(normalMap = it)
        }
    }

    private fun UiScope.colorSetting(
        label: String,
        colorAttr: MaterialAttribute,
        default: Color,
        material: MaterialData,
        shaderDataSetter: (MaterialAttribute) -> MaterialShaderData
    ) = menuRow {

        Text(label) {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
        }

        Box(height = sizes.lineHeight) {
            var isHovered by remember(false)
            val sourcePopup = remember {
                MaterialAttributeSourcePopup(colorAttr, true, default) { undoValue, applyValue ->
                    val undoMaterial = shaderDataSetter(undoValue)
                    val applyMaterial = shaderDataSetter(applyValue)
                    UpdateMaterialAction(material, applyMaterial, undoMaterial)
                }
            }
            sourcePopup.editMatAttr = colorAttr

            var width: Dimension = sizes.baseSize * 2
            var text: String? = null
            var textAlign = AlignmentX.Start
            var bgColor = if (isHovered) colors.componentBgHovered else colors.componentBg
            val borderColor = if (isHovered) colors.elevatedComponentBgHovered else colors.elevatedComponentBg

            when (colorAttr) {
                is ConstColorAttribute -> {
                    bgColor = colorAttr.color.toColor().toSrgb()
                }
                is ConstValueAttribute -> {
                    val f = colorAttr.value
                    bgColor = Color(f, f, f, 1f).toSrgb()
                }
                is MapAttribute -> {
                    text = colorAttr.mapName
                    width = sizes.baseSize * 4
                }
                is VertexAttribute -> {
                    text = "Vertex"
                    textAlign = AlignmentX.Center
                }
            }

            modifier
                .width(width)
                .onEnter { isHovered = true }
                .onExit { isHovered = false }
                .background(RoundRectBackground(bgColor, sizes.smallGap))
                .border(RoundRectBorder(borderColor, sizes.smallGap, sizes.borderWidth))
                .onClick {
                    sourcePopup.toggleVisibility(Vec2f(uiNode.leftPx, uiNode.bottomPx))
                }

            text?.let {
                Text(text) {
                    modifier
                        .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                        .align(textAlign, AlignmentY.Center)
                }
            }
            sourcePopup()
        }
    }

    private inline fun UiScope.floatSetting(
        label: String,
        floatAttr: MaterialAttribute,
        min: Float,
        max: Float,
        default: Float,
        material: MaterialData,
        crossinline shaderDataSetter: (MaterialAttribute) -> MaterialShaderData
    ) = menuRow {

        Text(label) {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
        }

        Box(height = sizes.lineHeight) {
            var isHovered by remember(false)
            val sourcePopup = remember {
                MaterialAttributeSourcePopup(floatAttr, false, minValue = min, maxValue = max, defaultValue = default) { undoValue, applyValue ->
                    val undoMaterial = shaderDataSetter(undoValue)
                    val applyMaterial = shaderDataSetter(applyValue)
                    UpdateMaterialAction(material, applyMaterial, undoMaterial)
                }
            }
            sourcePopup.editMatAttr = floatAttr

            var width: Dimension = sizes.baseSize * 2
            val doubleVal: Double
            val text: String
            val isTextField: Boolean
            var textAlign = AlignmentX.Start
            val bgColor = if (isHovered) colors.componentBgHovered else colors.componentBg
            val borderColor = if (isHovered) colors.elevatedComponentBgHovered else colors.elevatedComponentBg

            when (floatAttr) {
                is ConstColorAttribute -> {
                    doubleVal = floatAttr.color.r.toDouble()
                    text = "${floatAttr.color.r}"
                    isTextField = true
                }
                is ConstValueAttribute -> {
                    doubleVal = floatAttr.value.toDouble()
                    text = "${floatAttr.value}"
                    isTextField = true
                }
                is MapAttribute -> {
                    text = floatAttr.mapName
                    doubleVal = 0.0
                    width = sizes.baseSize * 4
                    isTextField = false
                }
                is VertexAttribute -> {
                    text = "Vertex"
                    doubleVal = 0.0
                    textAlign = AlignmentX.Center
                    isTextField = false
                }
            }

            modifier
                .width(width)
                .onEnter { isHovered = true }
                .onExit { isHovered = false }

            if (!isTextField) {
                modifier
                    .background(RoundRectBackground(bgColor, sizes.smallGap))
                    .border(RoundRectBorder(borderColor, sizes.smallGap, sizes.borderWidth))
                    .onClick {
                        sourcePopup.toggleVisibility(Vec2f(uiNode.leftPx, uiNode.bottomPx))
                    }
                Text(text) {
                    modifier
                        .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                        .align(textAlign, AlignmentY.Center)
                }
            } else {
                val txtField = doubleTextField(
                    value = doubleVal,
                    precision = precisionForValue((max - min).toDouble()),
                    width = sizes.baseSize * 2,
                    dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
                    minValue = min.toDouble(),
                    maxValue = max.toDouble(),
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        //editValue = applyValue.toFloat()
                        val undoMaterial = shaderDataSetter(ConstValueAttribute(undoValue.toFloat()))
                        val applyMaterial = shaderDataSetter(ConstValueAttribute(applyValue.toFloat()))
                        UpdateMaterialAction(material, applyMaterial, undoMaterial)
                    }
                )

                if (!sourcePopup.isVisible.use()) {
                    txtField.modifier.onClick {
                        sourcePopup.toggleVisibility(Vec2f(uiNode.leftPx, uiNode.bottomPx))
                    }
                }
            }

            sourcePopup()
        }
    }

    private fun UiScope.textureSetting(
        label: String,
        texAttr: MapAttribute?,
        material: MaterialData,
        shaderDataSetter: (MapAttribute?) -> MaterialShaderData
    ) = menuRow {

        Text(label) {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
        }

        Box(height = sizes.lineHeight) {
            var isHovered by remember(false)
            var editStartTex by remember(texAttr)
            var editTex by remember(texAttr)
            editTex = texAttr

            val editHandler = ActionValueEditHandler<MapAttribute?> { undoValue, applyValue ->
                val undoMaterial = shaderDataSetter(undoValue)
                val applyMaterial = shaderDataSetter(applyValue)
                UpdateMaterialAction(material, applyMaterial, undoMaterial)
            }
            val texPopup = remember {
                AutoPopup(hideOnOutsideClick = false).apply {
                    popupContent = Composable {
                        defaultPopupStyle()
                        textureSelector(editTex?.mapPath ?: "", true) {
                            editTex = if (it.path.isEmpty()) null else MapAttribute(it.path)
                            editHandler.onEdit(editTex)
                        }
                        okButton { hide() }
                    }
                    onShow = {
                        editStartTex = editTex
                        editHandler.onEditStart(editStartTex)
                    }
                    onHide = {
                        editHandler.onEditEnd(editStartTex, editTex)
                    }
                }
            }

            val bgColor = if (isHovered) colors.componentBgHovered else colors.componentBg
            val borderColor = if (isHovered) colors.elevatedComponentBgHovered else colors.elevatedComponentBg

            modifier
                .width(sizes.baseSize * 4)
                .onEnter { isHovered = true }
                .onExit { isHovered = false }
                .background(RoundRectBackground(bgColor, sizes.smallGap))
                .border(RoundRectBorder(borderColor, sizes.smallGap, sizes.borderWidth))
                .onClick {
                    texPopup.toggleVisibility(Vec2f(uiNode.leftPx, uiNode.bottomPx))
                }

            Text(texAttr?.mapName ?: "None selected") {
                modifier
                    .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                    .alignY(AlignmentY.Center)
            }
            texPopup()
        }
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