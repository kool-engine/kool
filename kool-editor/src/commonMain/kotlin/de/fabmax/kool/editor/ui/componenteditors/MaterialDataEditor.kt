package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont

abstract class MaterialDataEditor<T: MaterialShaderData>(
    val material: MaterialComponent,
    var materialData: T,
    val editor: ComponentEditor<*>
) {
    abstract fun ColumnScope.materialEditor()

    protected fun ColumnScope.genericSettings() {
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

    protected fun ColumnScope.colorSetting(
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

        materialSetting(label, labelWidth, valueColor, material, null, shaderDataSetter, sourcePopup, true) {
            text?.let {
                Text(text) {
                    modifier
                        .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                        .align(textAlign, AlignmentY.Center)
                }
            }
        }
    }

    protected fun ColumnScope.floatSetting(
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
            defaultChannels = "r",
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

    protected fun ColumnScope.textureSetting(
        label: String,
        texAttr: MapAttribute?,
        defaultChannels: String?,
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

        materialSetting(label, sizes.editorLabelWidthSmall, null, material, defaultChannels, shaderDataSetter, texPopup, true) {
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

    private fun RowScope.materialSetting(
        label: String,
        labelWidth: Dimension,
        valueColor: Color?,
        material: MaterialComponent,
        defaultChannels: String?,
        shaderDataSetter: (MapAttribute) -> MaterialShaderData,
        sourcePopup: AutoPopup,
        isOpaqueBox: Boolean,
        block: UiScope.(DndHoverState) -> Unit
    ) {
        Text(label) {
            modifier
                .width(labelWidth)
                .alignY(AlignmentY.Center)
        }

        Box(height = sizes.lineHeight) {
            val dndHandler = rememberTextureDndHandler(material, defaultChannels, shaderDataSetter, uiNode)
            val dndState = getDndHoverState(dndHandler, valueColor)
            modifier
                .width(Grow.Std)
                .onClick {
                    sourcePopup.toggleVisibility(Vec2f(uiNode.leftPx, uiNode.bottomPx))
                }
            if (isOpaqueBox) {
                modifier
                    .background(RoundRectBackground(dndState.bgColor, sizes.smallGap))
                    .border(RoundRectBorder(dndState.borderColor, sizes.smallGap, sizes.borderWidth))
            }
            block(dndState)
            sourcePopup()
        }
    }

    private fun UiScope.rememberTextureDndHandler(
        material: MaterialComponent,
        defaultChannels: String?,
        shaderDataSetter: (MapAttribute) -> MaterialShaderData,
        dropTarget: UiNode
    ): TextureDndHandler {
        val handler = remember { TextureDndHandler(material, defaultChannels, shaderDataSetter, dropTarget) }
        KoolEditor.instance.ui.dndController.registerHandler(handler, surface)
        return handler
    }

    private class TextureDndHandler(
        val material: MaterialComponent,
        val defaultChannels: String?,
        val shaderDataSetter: (MapAttribute) -> MaterialShaderData,
        dropTarget: UiNode
    ) : DndHandler(dropTarget, setOf(DndItemFlavor.DndItemTexture)) {
        override fun onMatchingReceive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) {
            val dragTextureItem = dragItem.get(DndItemFlavor.DndItemTexture)
            val applyMaterial = material.data.copy(shaderData = shaderDataSetter(MapAttribute(dragTextureItem.path, channels = defaultChannels)))
            if (applyMaterial.shaderData != material.shaderData) {
                SetComponentDataAction(material, material.data, applyMaterial).apply()
            }
        }
    }

    companion object {
        val texSingleChannels = listOf("R", "G", "B", "A")
    }
}

