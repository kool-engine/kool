package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.SetMaterialAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.components.MaterialReferenceComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.launchOnMainThread
import kotlin.math.max
import kotlin.reflect.KClass

class MaterialEditor : ComponentEditor<MaterialReferenceComponent>() {

    private val material: MaterialComponent get() = components[0].material!!

    private val pbrData: PbrShaderData get() = material.shaderData as PbrShaderData
    private val pbrSplatData: PbrSplatShaderData get() = material.shaderData as PbrSplatShaderData

    override fun UiScope.compose() {
        val allTheSameMaterial = components.all {
            it.material?.dataState?.use()
            it.dataState.use().materialId == components[0].data.materialId
        }
        componentPanel(
            title = "Material",
            imageIcon = IconMap.small.palette,
            onRemove = ::removeComponent,
            titleWidth = sizes.baseSize * 2.3f,
            headerContent = {
                val (items, idx) = makeMaterialItemsAndIndex(allTheSameMaterial)
                ComboBox {
                    defaultComboBoxStyle()
                    modifier
                        .margin(end = sizes.gap)
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                        .items(items)
                        .selectedIndex(idx)
                        .onItemSelected { index ->
                            if (allTheSameMaterial || index > 0) {
                                launchOnMainThread {
                                    val setMaterial = items[index].getMaterial()
                                    components
                                        .map { SetMaterialAction(it, setMaterial) }
                                        .fused().apply()
                                }
                            }
                        }
                }
            }
        ) {
            if (allTheSameMaterial) {
                val checkedMaterial = components[0].material ?: return@componentPanel Unit
                labeledTextField("Name:", checkedMaterial.name, labelWidth = sizes.editorLabelWidthSmall) {
                    val oldData = checkedMaterial.data
                    val newData = oldData.copy(name = it)
                    SetComponentDataAction(checkedMaterial, oldData, newData).apply()
                }

                labeledCombobox("Type:", materialTypes, materialTypes.indexOfFirst { it.matches(checkedMaterial.shaderData) }) {
                    if (!it.matches(checkedMaterial.shaderData)) {
                        val oldData = checkedMaterial.data
                        val newData = oldData.copy(shaderData = it.factory())
                        SetComponentDataAction(checkedMaterial, oldData, newData).apply()
                    }
                }

                menuDivider()
                materialEditor()
                menuDivider()
                genericSettings()
            }
        }
    }

    private fun ColumnScope.genericSettings() {
        var isTwoSided by remember(material.shaderData.genericSettings.isTwoSided)
        labeledCheckbox("Is two-sided:", isTwoSided) {
            isTwoSided = it

            val undoMaterial = material.data
            val applyMaterial = material.data.copy(
                shaderData = material.data.shaderData.copy(genericSettings = material.shaderData.genericSettings.copy(isTwoSided = it))
            )
            SetComponentDataAction(material, undoMaterial, applyMaterial).apply()
        }
        var isCastingShadow by remember(material.shaderData.genericSettings.isCastingShadow)
        labeledCheckbox("Is casting shadow:", isCastingShadow) {
            isCastingShadow = it

            val undoMaterial = material.data
            val applyMaterial = material.data.copy(
                shaderData = material.data.shaderData.copy(genericSettings = material.shaderData.genericSettings.copy(isCastingShadow = it))
            )
            SetComponentDataAction(material, undoMaterial, applyMaterial).apply()
        }
    }

    private fun ColumnScope.materialEditor() {
        when (material.shaderData) {
            is BlinnPhongShaderData -> TODO()
            is PbrShaderData -> pbrMaterialEditor()
            is UnlitShaderData -> TODO()
            is PbrSplatShaderData -> pbrSplatMaterialEditor()
        }
    }

    private fun ColumnScope.pbrSplatMaterialEditor() {
        textureSetting("Weight map:", pbrSplatData.splatMap) {
            pbrSplatData.copy(splatMap = it)
        }

        labeledCheckbox("Debug mode:", pbrSplatData.debugMode != 0) {
            val undoMaterial = material.data.copy(shaderData = pbrSplatData)
            val applyMaterial = material.data.copy(shaderData = pbrSplatData.copy(debugMode = if (it) 1 else 0))
            SetComponentDataAction(material, undoMaterial, applyMaterial).apply()
        }

        val numMats = max(2, pbrSplatData.materialMaps.size)
        for (i in 0 until numMats) {
            val panelKey = "splat-mat-$i"

            collapsablePanelLvl2(
                title = "Material ${i + 1}",
                titleWidth = sizes.editorLabelWidthSmall - sizes.gap * 4f,
                startExpanded = getPanelState(false, panelKey = panelKey),
                indicatorColor = splatIndiColors[i % splatIndiColors.size],
                isAlwaysShowIndicator = false,
                headerContent = { splatMaterialPreview(getSplatMaterial(i).baseColor, i) },
                onCollapseChanged = { setPanelState(it, panelKey = panelKey) }
            ) {
                val mat = getSplatMaterial(i)
                textureSetting("Displacement:", mat.displacementMap, texSingleChannels) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(displacementMap = it))
                }
                colorSetting("Base color:", mat.baseColor, MdColor.GREY.toLinear()) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(baseColor = it))
                }
                textureSetting("Normal map:", mat.normalMap) {
                    setSplatMaterial(i, getSplatMaterial(i).copy(normalMap = it))
                }
                textureSetting("AO:", mat.aoMap, texSingleChannels) {
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
                    precision = 0,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMats = setSplatMaterial(i, getSplatMaterial(i).copy(stochasticRotation = undoValue.toFloat()))
                        val applyMats = setSplatMaterial(i, getSplatMaterial(i).copy(stochasticRotation = applyValue.toFloat()))
                        SetComponentDataAction(material, material.data.copy(shaderData = undoMats), material.data.copy(shaderData = applyMats))
                    }
                )
            }
        }

        if (pbrSplatData.materialMaps.size < 5) {
            Button("Add Splat Material") {
                defaultButtonStyle()
                modifier
                    .width(sizes.baseSize * 5)
                    .height(sizes.editItemHeight)
                    .margin(vertical = sizes.smallGap)
                    .alignX(AlignmentX.Center)
                    .onClick {
                        val newMats = pbrSplatData.copy(materialMaps = pbrSplatData.materialMaps + SplatMapData())
                        SetComponentDataAction(material, material.data, material.data.copy(shaderData = newMats)).apply()
                    }
            }
        }
    }

    private fun getSplatMaterial(index: Int): SplatMapData {
        return pbrSplatData.materialMaps.getOrNull(index) ?: SplatMapData()
    }

    private fun setSplatMaterial(index: Int, mat: SplatMapData): PbrSplatShaderData {
        val mats = pbrSplatData.materialMaps
        val newMats = if (index >= mats.size) {
            mats + mat
        } else {
            mats.toMutableList().also { it[index] = mat }
        }
        return pbrSplatData.copy(materialMaps = newMats)
    }

    private fun RowScope.splatMaterialPreview(baseColor: MaterialAttribute, matIndex: Int) {
        val mapPath = (baseColor as? MapAttribute)?.mapPath
        if (mapPath != null) {
            val tex = KoolEditor.instance.cachedAppAssets.getTextureMutableState(AssetReference.Texture(mapPath)).use()
            Image(tex) {
                modifier
                    .size(sizes.lineHeight, sizes.lineHeight)
                    .margin(start = sizes.largeGap)
                    .alignY(AlignmentY.Center)
            }
        } else {
            val color = (baseColor as? ConstColorAttribute)?.color?.toColorSrgb() ?: MdColor.GREY
            Box(width = sizes.lineHeight, height = sizes.lineHeight) {
                modifier
                    .margin(start = sizes.largeGap)
                    .alignY(AlignmentY.Center)
                    .backgroundColor(color)
            }
        }

        if (matIndex >= 2 && matIndex == pbrSplatData.materialMaps.lastIndex) {
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
                        val withoutLast = pbrSplatData.copy(materialMaps = pbrSplatData.materialMaps.take(matIndex))
                        SetComponentDataAction(material, material.data, material.data.copy(shaderData = withoutLast)).apply()
                    }
                    .background(CircularBackground(bgColor))

                Image {
                    modifier.iconImage(IconMap.small.trash, fgColor)
                }
            }
        }
    }

    private fun ColumnScope.pbrMaterialEditor() {
        // shader setting callback functions need to use cast material.shaderData instead of pbrData because otherwise
        // pbrData is captured on first invocation and will never be updated

        colorSetting("Base color:", pbrData.baseColor, MdColor.GREY.toLinear()) {
            pbrData.copy(baseColor = it)
        }
        textureSetting("Normal map:", pbrData.normalMap) {
            pbrData.copy(normalMap = it)
        }
        textureSetting("AO:", pbrData.aoMap, texSingleChannels) {
            pbrData.copy(aoMap = it)
        }
        floatSetting("Roughness:", pbrData.roughness, 0f, 1f, 0.5f) {
            pbrData.copy(roughness = it)
        }
        floatSetting("Metallic:", pbrData.metallic, 0f, 1f, 0f) {
            pbrData.copy(metallic = it)
        }
        textureSetting("Displacement:", pbrData.displacementMap, texSingleChannels) {
            pbrData.copy(displacementMap = it)
        }
        colorSetting("Emission color:", pbrData.emission, Color.BLACK) {
            pbrData.copy(emission = it)
        }

        if (pbrData.displacementMap != null) {
            labeledDoubleTextField(
                label = "Strength:",
                value = pbrData.parallaxStrength.toDouble(),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val undoData = material.data.copy(shaderData = pbrData.copy(parallaxStrength = undo.toFloat()))
                    val applyData = material.data.copy(shaderData = pbrData.copy(parallaxStrength = apply.toFloat()))
                    SetComponentDataAction(material, undoData, applyData)
                }
            )
            labeledDoubleTextField(
                label = "Offset:",
                value = pbrData.parallaxOffset.toDouble(),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val undoData = material.data.copy(shaderData = pbrData.copy(parallaxOffset = undo.toFloat()))
                    val applyData = material.data.copy(shaderData = pbrData.copy(parallaxOffset = apply.toFloat()))
                    SetComponentDataAction(material, undoData, applyData)
                }
            )
            labeledDoubleTextField(
                label = "Steps:",
                value = pbrData.parallaxSteps.toDouble(),
                minValue = 2.0,
                maxValue = 64.0,
                precision = 0,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val undoData = material.data.copy(shaderData = pbrData.copy(parallaxSteps = undo.toInt()))
                    val applyData = material.data.copy(shaderData = pbrData.copy(parallaxSteps = apply.toInt()))
                    SetComponentDataAction(material, undoData, applyData)
                }
            )
        }
    }

    private fun RowScope.materialSetting(
        label: String,
        labelWidth: Dimension,
        valueColor: Color?,
        material: MaterialComponent,
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

            val bgColor = when {
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

    private fun ColumnScope.colorSetting(
        label: String,
        colorAttr: MaterialAttribute,
        default: Color,
        shaderDataSetter: (MaterialAttribute) -> MaterialShaderData
    ) = menuRow {

        var valueColor: Color? = null
        var text: String? = null
        var labelWidth = sizes.editorLabelWidthLarge
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
                labelWidth = sizes.editorLabelWidthSmall
            }
            is VertexAttribute -> {
                text = "Vertex"
                textAlign = AlignmentX.Center
            }
        }

        val sourcePopup = remember {
            MaterialAttributeSourcePopup(colorAttr, true, default) { undoValue, applyValue ->
                val undoMaterial = material.data.copy(shaderData = shaderDataSetter(undoValue))
                val applyMaterial = material.data.copy(shaderData = shaderDataSetter(applyValue))
                SetComponentDataAction(material, undoMaterial, applyMaterial)
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

    private fun ColumnScope.floatSetting(
        label: String,
        floatAttr: MaterialAttribute,
        min: Float,
        max: Float,
        default: Float,
        shaderDataSetter: (MaterialAttribute) -> MaterialShaderData
    ) = menuRow {
        val doubleVal: Double
        val text: String
        val isTextField: Boolean
        var labelWidth = sizes.editorLabelWidthLarge
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
                labelWidth = sizes.editorLabelWidthSmall
            }
            is VertexAttribute -> {
                text = "Vertex"
                doubleVal = 0.0
                textAlign = AlignmentX.Center
                isTextField = false
            }
        }

        val sourcePopup = remember {
            MaterialAttributeSourcePopup(
                editMatAttr = floatAttr,
                isColor = false,
                minValue = min,
                maxValue = max,
                defaultValue = default,
                channelOptions = texSingleChannels
            ) { undoValue, applyValue ->
                val undoMaterial = material.data.copy(shaderData = shaderDataSetter(undoValue))
                val applyMaterial = material.data.copy(shaderData = shaderDataSetter(applyValue))
                SetComponentDataAction(material, undoMaterial, applyMaterial)
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
                texChannels(floatAttr as? MapAttribute)
            } else {
                val txtField = doubleTextField(
                    value = doubleVal,
                    precision = precisionForValue((max - min).toDouble()),
                    width = Grow.Std,
                    dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
                    minValue = min.toDouble(),
                    maxValue = max.toDouble(),
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMaterial = material.data.copy(shaderData = shaderDataSetter(ConstValueAttribute(undoValue.toFloat())))
                        val applyMaterial = material.data.copy(shaderData = shaderDataSetter(ConstValueAttribute(applyValue.toFloat())))
                        SetComponentDataAction(material, undoMaterial, applyMaterial)
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

    private fun ColumnScope.textureSetting(
        label: String,
        texAttr: MapAttribute?,
        channelOptions: List<String> = emptyList(),
        shaderDataSetter: (MapAttribute?) -> MaterialShaderData
    ) = menuRow {
        var editStartTex by remember(texAttr)
        var editTex by remember(texAttr)
        editTex = texAttr
        val editHandler = ActionValueEditHandler<MapAttribute?> { undoValue, applyValue ->
            val undoMaterial = material.data.copy(shaderData = shaderDataSetter(undoValue))
            val applyMaterial = material.data.copy(shaderData = shaderDataSetter(applyValue))
            SetComponentDataAction(material, undoMaterial, applyMaterial)
        }
        val texPopup = remember {
            AutoPopup().apply {
                popupContent = Composable {
                    Column {
                        defaultPopupStyle()
                        textureSelector(
                            selectedTexPath = editTex?.mapPath ?: "",
                            withNoneOption = true,
                            channelOptions = channelOptions,
                            selectedChannelOption = editTex?.channels
                        ) { asset, channels ->
                            editTex = asset?.let { MapAttribute(it.path, channels) }
                            editHandler.onEdit(editTex)
                        }
                        okButton { hide() }
                    }
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

        materialSetting(label, sizes.editorLabelWidthSmall, null, material, shaderDataSetter, texPopup, true) {
            Text(texAttr?.mapName ?: "None selected") {
                modifier
                    .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                    .alignY(AlignmentY.Center)
            }
            texChannels(texAttr)
        }
    }

    private fun UiScope.texChannels(texAttr: MapAttribute?) {
        texAttr?.channels?.let { ch ->
            Row {
                modifier
                    .zLayer(UiSurface.LAYER_FLOATING * 2)
                    .alignX(AlignmentX.End)
                    .alignY(AlignmentY.Center)
                    .background(RoundRectBackground(colors.backgroundVariant, sizes.smallGap))
                    .padding(top = 2.dp, bottom = 2.dp, start = sizes.smallGap, end = sizes.smallGap * 1.5f)
                    .margin(sizes.smallGap)

                ch.uppercase().forEach { c ->
                    val color = when (c) {
                        'R' -> MdColor.RED
                        'G' -> MdColor.GREEN
                        'B' -> MdColor.BLUE
                        else -> colors.onBackground
                    }
                    Text("$c") {
                        modifier
                            .font(KoolEditor.instance.ui.consoleFont.use().copy(weight = MsdfFont.WEIGHT_BOLD))
                            .textColor(color)
                    }
                }
            }
        }
    }

    private fun UiScope.makeMaterialItemsAndIndex(allTheSameMaterial: Boolean): Pair<List<MaterialItem>, Int> {
        val items = mutableListOf(
            MaterialItem("Default", null),
            MaterialItem("New material", null)
        )

        if (!allTheSameMaterial) {
            items.add(0, MaterialItem("", null))
        }

        var index = 0
        KoolEditor.instance.projectModel.materials.use()
            .filter { it.gameEntity.isVisible }
            .forEachIndexed { i, material ->
                if (allTheSameMaterial && components[0].isHoldingMaterial(material)) {
                    index = i + 2
                }
                items += MaterialItem(material.name, material)
            }
        return items to index
    }

    private fun UiScope.rememberTextureDndHandler(
        material: MaterialComponent,
        shaderDataSetter: (MapAttribute) -> MaterialShaderData,
        dropTarget: UiNode
    ): TextureDndHandler {
        val handler = remember { TextureDndHandler(material, shaderDataSetter, dropTarget) }
        KoolEditor.instance.ui.dndController.registerHandler(handler, surface)
        return handler
    }

    private inner class TextureDndHandler(
        val material: MaterialComponent,
        val shaderDataSetter: (MapAttribute) -> MaterialShaderData,
        dropTarget: UiNode
    ) :
        DndHandler(dropTarget, setOf(DndItemFlavor.DndItemTexture))
    {
        override fun onMatchingReceive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) {
            val dragTextureItem = dragItem.get(DndItemFlavor.DndItemTexture)
            val applyMaterial = material.data.copy(shaderData = shaderDataSetter(MapAttribute(dragTextureItem.path)))
            if (applyMaterial.shaderData != material.shaderData) {
                SetComponentDataAction(material, material.data, applyMaterial).apply()
            }
        }
    }

    private class MaterialItem(val itemText: String, val material: MaterialComponent?) {
        override fun toString(): String = itemText

        suspend fun getMaterial(): MaterialComponent? {
            return material ?: if (itemText == "New material") KoolEditor.instance.projectModel.createNewMaterial() else null
        }
    }

    private class MaterialTypeOption<T: MaterialShaderData>(val label: String, val dataType: KClass<T>, val factory: () -> T) {
        fun matches(data: MaterialShaderData): Boolean = dataType.isInstance(data)
        override fun toString() = label
    }

    companion object {
        private val materialTypes = listOf(
            MaterialTypeOption("PBR", PbrShaderData::class) { PbrShaderData() },
            MaterialTypeOption("Splatted PBR", PbrSplatShaderData::class) { PbrSplatShaderData() },
        )

        private val splatIndiColors = listOf(
            MdColor.GREEN,
            MdColor.BLUE.mix(MdColor.INDIGO, 0.5f),
            MdColor.PURPLE,
            MdColor.RED,
            MdColor.AMBER,
        )

        private val texSingleChannels = listOf("R", "G", "B", "A")
    }
}