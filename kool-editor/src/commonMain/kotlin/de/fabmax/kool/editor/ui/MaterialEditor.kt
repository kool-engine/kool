package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.RenameMaterialAction
import de.fabmax.kool.editor.actions.SetMaterialAction
import de.fabmax.kool.editor.actions.UpdateMaterialAction
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class MaterialEditor(component: MaterialComponent) : ComponentEditor<MaterialComponent>(component) {

    override fun UiScope.compose() = componentPanel(
        title = "Material",
        imageIcon = IconMap.PALETTE,
        onRemove = ::removeComponent,
        titleWidth = sizes.baseSize * 2.3f,
        headerContent = {
            val (items, idx) = makeMaterialItemsAndIndex()
            var selectedIndex by remember(idx)
            selectedIndex = idx

            ComboBox {
                defaultComboBoxStyle()
                modifier
                    .margin(horizontal = sizes.gap)
                    .size(Grow.Std, sizes.lineHeight)
                    .alignY(AlignmentY.Center)
                    .items(items)
                    .selectedIndex(selectedIndex)
                    .onItemSelected {
                        SetMaterialAction(component, items[it].getMaterialModel()).apply()
                    }
            }
        }
    ) {
        component.materialState.use()?.let { selectedMaterial ->
            Column(width = Grow.Std) {
                modifier
                    .padding(horizontal = sizes.gap)
                    .margin(bottom = sizes.smallGap)

                labeledTextField("Name:", selectedMaterial.name) {
                    RenameMaterialAction(selectedMaterial, it, selectedMaterial.name).apply()
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
            UpdateMaterialAction(material, applyMaterial, undoMaterial).apply()
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

    private fun RowScope.materialSetting(
        label: String,
        labelWidth: Dimension,
        valueColor: Color?,
        material: MaterialData,
        shaderDataSetter: (MapAttribute) -> MaterialShaderData,
        sourcePopup: AutoPopup,
        isOpaqueBox: Boolean,
        block: UiScope.(Pair<Boolean, Boolean>) -> Unit
    ) {
        Text(label) {
            modifier
                .width(labelWidth)
                .alignY(AlignmentY.Center)
        }

        Box(height = sizes.lineHeight) {
            val dndHandler = rememberTextureDndHandler(material, shaderDataSetter, uiNode)
            var isHovered by remember(false)
            val hover = isHovered || dndHandler.isHovered.use()
            val drag = dndHandler.isDrag.use()

            val bgColor =  when {
                valueColor != null -> when {
                    drag && hover -> valueColor.mix(MdColor.GREEN, 0.5f)
                    drag -> valueColor.mix(MdColor.GREEN, 0.3f)
                    else -> valueColor
                }
                drag && hover -> colors.dndAcceptableBgHovered
                drag -> colors.dndAcceptableBg
                hover -> colors.componentBgHovered
                else -> colors.componentBg
            }
            val borderColor = when {
                drag -> MdColor.GREEN
                hover -> colors.elevatedComponentBgHovered
                else -> colors.elevatedComponentBg
            }

            modifier
                .width(Grow.Std)
                .onEnter { isHovered = true }
                .onExit { isHovered = false }
                .onClick {
                    sourcePopup.toggleVisibility(Vec2f(uiNode.leftPx, uiNode.bottomPx))
                }

            if (isOpaqueBox) {
                modifier
                    .background(RoundRectBackground(bgColor, sizes.smallGap))
                    .border(RoundRectBorder(borderColor, sizes.smallGap, sizes.borderWidth))
            }

            block(hover to drag)

            sourcePopup()
        }
    }

    private fun UiScope.colorSetting(
        label: String,
        colorAttr: MaterialAttribute,
        default: Color,
        material: MaterialData,
        shaderDataSetter: (MaterialAttribute) -> MaterialShaderData
    ) = menuRow {

        var valueColor: Color? = null
        var text: String? = null
        var labelWidth = sizes.baseSize * 5
        var textAlign = AlignmentX.Start
        when (colorAttr) {
            is ConstColorAttribute -> {
                valueColor = colorAttr.color.toColorLinear().toSrgb()
            }
            is ConstValueAttribute -> {
                val f = colorAttr.value
                valueColor = Color(f, f, f, 1f).toSrgb()
            }
            is MapAttribute -> {
                text = colorAttr.mapName
                labelWidth = sizes.baseSize * 3
            }
            is VertexAttribute -> {
                text = "Vertex"
                textAlign = AlignmentX.Center
            }
        }

        val sourcePopup = remember {
            MaterialAttributeSourcePopup(colorAttr, true, default) { undoValue, applyValue ->
                val undoMaterial = shaderDataSetter(undoValue)
                val applyMaterial = shaderDataSetter(applyValue)
                UpdateMaterialAction(material, applyMaterial, undoMaterial)
            }
        }
        sourcePopup.editMatAttr = colorAttr

        materialSetting(label, labelWidth, valueColor, material, shaderDataSetter, sourcePopup, true) {
            text?.let {
                Text(text) {
                    modifier
                        .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                        .align(textAlign, AlignmentY.Center)
                }
            }
        }
    }

    private fun UiScope.floatSetting(
        label: String,
        floatAttr: MaterialAttribute,
        min: Float,
        max: Float,
        default: Float,
        material: MaterialData,
        shaderDataSetter: (MaterialAttribute) -> MaterialShaderData
    ) = menuRow {

        val doubleVal: Double
        val text: String
        val isTextField: Boolean
        var labelWidth = sizes.baseSize * 5
        var textAlign = AlignmentX.Start
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
                isTextField = false
                labelWidth = sizes.baseSize * 3
            }
            is VertexAttribute -> {
                text = "Vertex"
                doubleVal = 0.0
                textAlign = AlignmentX.Center
                isTextField = false
            }
        }

        val sourcePopup = remember {
            MaterialAttributeSourcePopup(floatAttr, false, minValue = min, maxValue = max, defaultValue = default) { undoValue, applyValue ->
                val undoMaterial = shaderDataSetter(undoValue)
                val applyMaterial = shaderDataSetter(applyValue)
                UpdateMaterialAction(material, applyMaterial, undoMaterial)
            }
        }
        sourcePopup.editMatAttr = floatAttr

        materialSetting(
            label = label,
            labelWidth = labelWidth,
            valueColor = null,
            material = material,
            shaderDataSetter = shaderDataSetter,
            sourcePopup = sourcePopup,
            isOpaqueBox = !isTextField
        ) { (isHover, isDrag) ->
            if (!isTextField) {
                Text(text) {
                    modifier
                        .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                        .align(textAlign, AlignmentY.Center)
                }
            } else {
                val txtField = doubleTextField(
                    value = doubleVal,
                    precision = precisionForValue((max - min).toDouble()),
                    width = Grow.Std,
                    dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
                    minValue = min.toDouble(),
                    maxValue = max.toDouble(),
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMaterial = shaderDataSetter(ConstValueAttribute(undoValue.toFloat()))
                        val applyMaterial = shaderDataSetter(ConstValueAttribute(applyValue.toFloat()))
                        UpdateMaterialAction(material, applyMaterial, undoMaterial)
                    },
                    textFieldModifier = {
                        if (isDrag) {
                            it
                                .border(RoundRectBorder(MdColor.GREEN, sizes.smallTextFieldPadding, sizes.borderWidth))
                                .backgroundColor(if (isHover) colors.dndAcceptableBgHovered else colors.dndAcceptableBg)
                        }
                    }
                )

                if (!sourcePopup.isVisible.use()) {
                    txtField.modifier.onClick {
                        sourcePopup.toggleVisibility(Vec2f(uiNode.leftPx, uiNode.bottomPx))
                    }
                }
            }
        }
    }

    private fun UiScope.textureSetting(
        label: String,
        texAttr: MapAttribute?,
        material: MaterialData,
        shaderDataSetter: (MapAttribute?) -> MaterialShaderData
    ) = menuRow {
        var editStartTex by remember(texAttr)
        var editTex by remember(texAttr)
        editTex = texAttr
        val editHandler = ActionValueEditHandler<MapAttribute?> { undoValue, applyValue ->
            val undoMaterial = shaderDataSetter(undoValue)
            val applyMaterial = shaderDataSetter(applyValue)
            UpdateMaterialAction(material, applyMaterial, undoMaterial)
        }
        val texPopup = remember {
            AutoPopup().apply {
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

        materialSetting(label, sizes.baseSize * 3, null, material, shaderDataSetter, texPopup, true) {
            Text(texAttr?.mapName ?: "None selected") {
                modifier
                    .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                    .alignY(AlignmentY.Center)
            }
        }
    }

    private fun UiScope.makeMaterialItemsAndIndex(): Pair<List<MaterialItem>, Int> {
        val items = mutableListOf(
            MaterialItem("Default", null),
            MaterialItem("New material", null)
        )
        var index = 0
        EditorState.projectModel.materials.use().forEachIndexed { i, material ->
            if (component.isHoldingMaterial(material)) {
                index = i + 2
            }
            items += MaterialItem(material.name, material)
        }
        return items to index
    }

    private fun UiScope.rememberTextureDndHandler(
        material: MaterialData,
        shaderDataSetter: (MapAttribute) -> MaterialShaderData,
        dropTarget: UiNode
    ): TextureDndHandler {
        val handler = remember { TextureDndHandler(material, shaderDataSetter, dropTarget) }
        handler.dropTarget = uiNode
        KoolEditor.instance.ui.dndController.registerHandler(handler, surface)
        return handler
    }

    private inner class TextureDndHandler(
        val material: MaterialData,
        val shaderDataSetter: (MapAttribute) -> MaterialShaderData,
        dropTarget: UiNode
    ) :
        DndHandler(dropTarget, setOf(DndItemFlavor.ASSET_ITEM_TEXTURE))
    {
        override fun onMatchingReceive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) {
            val dragTextureItem = dragItem.get(DndItemFlavor.ASSET_ITEM_TEXTURE)
            val applyMaterial = shaderDataSetter(MapAttribute(dragTextureItem.path))
            if (applyMaterial != material.shaderData) {
                UpdateMaterialAction(material, applyMaterial, material.shaderData).apply()
            }
        }
    }

    private class MaterialItem(val itemText: String, val material: MaterialData?) {
        override fun toString(): String = itemText

        fun getMaterialModel(): MaterialData? {
            return material ?: if (itemText == "New material") EditorState.projectModel.createNewMaterial() else null
        }
    }
}