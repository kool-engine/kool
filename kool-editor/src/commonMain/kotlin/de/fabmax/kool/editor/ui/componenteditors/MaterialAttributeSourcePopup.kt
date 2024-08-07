package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.Color

class MaterialAttributeSourcePopup(
    var editMatAttr: MaterialAttribute,
    val isColor: Boolean,
    val defaultColor: Color = Color.BLACK,
    val minValue: Float = 0f,
    val maxValue: Float = 1f,
    val defaultValue: Float = 0.5f,
    val channelOptions: List<String> = emptyList(),
    var editHandler: ActionValueEditHandler<MaterialAttribute>
) : AutoPopup() {

    private var editStartAttr = editMatAttr

    private var lastColorAttr = defaultColorAttrib()
    private var lastValueAttr = defaultValueAttrib()
    private var lastTextureAttr = defaultTextureAttrib()
    private var lastVertexAttr = defaultVertexAttrib()

    private val availableTextures: List<AssetItem>
        get() = KoolEditor.instance.availableAssets.textureAssets

    init {
        popupContent = Composable {
            Column {
                defaultPopupStyle()
                content()
            }
        }

        onShow = {
            editStartAttr = editMatAttr
            editHandler.onEditStart(editStartAttr)

            lastColorAttr = editMatAttr as? ConstColorAttribute ?: defaultColorAttrib()
            lastValueAttr = editMatAttr as? ConstValueAttribute ?: defaultValueAttrib()
            lastTextureAttr = editMatAttr as? MapAttribute ?: defaultTextureAttrib()
            lastVertexAttr = editMatAttr as? VertexAttribute ?: defaultVertexAttrib()
        }
        onHide = {
            editHandler.onEditEnd(editStartAttr, editMatAttr)
        }
    }

    private fun UiScope.setEditAttrib(attr: MaterialAttribute) {
        editHandler.onEdit(attr)
        surface.triggerUpdate()
    }

    private fun ColumnScope.content() {
        val colorMode = when (editMatAttr) {
            is ConstColorAttribute -> ColorMode.COLOR
            is ConstValueAttribute -> ColorMode.VALUE
            is MapAttribute -> ColorMode.TEXTURE
            is VertexAttribute -> ColorMode.VERTEX
        }

        Row(width = sizes.baseSize * 6, height = sizes.lineHeight) {
            if (isColor) {
                modeButton("Color", ColorMode.COLOR, colorMode, lastColorAttr)
            } else {
                modeButton("Value", ColorMode.VALUE, colorMode, lastValueAttr)
            }
            divider(verticalMargin = Dp.ZERO)
            modeButton("Texture", ColorMode.TEXTURE, colorMode, lastTextureAttr)
            divider(verticalMargin = Dp.ZERO)
            modeButton("Vertex", ColorMode.VERTEX, colorMode, lastVertexAttr)
        }
        Box {
            modifier
                .size(Grow.Std, sizes.borderWidth)
                .backgroundColor(colors.secondaryVariant)
                .margin(bottom = sizes.gap)
        }

        when (colorMode) {
            ColorMode.VALUE -> valueSelector()
            ColorMode.COLOR -> colorSelector()
            ColorMode.TEXTURE -> textureSelector()
            ColorMode.VERTEX -> vertexAttrSelector()
        }
        okButton { hide() }
    }

    private fun UiScope.modeButton(
        name: String,
        mode: ColorMode,
        activeMode: ColorMode,
        defaultSelectionAttrib: MaterialAttribute
    ) = Text(name) {
        var isHovered by remember(false)
        if (isHovered) {
            modifier.backgroundColor(colors.componentBgHovered)
        } else if (activeMode == mode) {
            modifier.backgroundColor(colors.componentBg)
        }

        modifier
            .size(Grow.Std, Grow.Std)
            .textAlignX(AlignmentX.Center)
            .onEnter { isHovered = true }
            .onExit { isHovered = false }

        if (activeMode != mode) {
            modifier.onClick {
                setEditAttrib(defaultSelectionAttrib)
            }
        }
    }

    private fun UiScope.valueSelector() {
        val valueAttr = editMatAttr as ConstValueAttribute

        labeledSlider(
            label = "Value:",
            value = valueAttr.value.toDouble(),
            min = minValue.toDouble(),
            max = maxValue.toDouble(),
            labelWidth = Grow.Std,
            valueWidth = sizes.baseSize * 2
        ) {
            lastValueAttr = ConstValueAttribute(it.toFloat())
            setEditAttrib(lastValueAttr)
        }
    }

    private fun UiScope.colorSelector() {
        val color = when (val attr = editMatAttr) {
            is ConstColorAttribute -> {
                attr.color.toColorLinear().toSrgb()
            }
            is ConstValueAttribute -> {
                val f = attr.value
                Color(f, f, f).toSrgb()
            }
            else -> throw IllegalStateException("material attribute is neither color nor value")
        }

        val hsv = color.toHsv()
        val hue = remember(hsv.h)
        val sat = remember(hsv.s)
        val bri = remember(hsv.v)
        val alpha = remember(color.a)
        val hexString = remember(color.toHexString())

        ColorChooserV(hue, sat, bri, alpha, hexString) { editColor ->
            lastColorAttr = ConstColorAttribute(ColorData(editColor.toLinear()))
            setEditAttrib(lastColorAttr)
        }
    }

    private fun UiScope.textureSelector() {
        val texAttr = editMatAttr as MapAttribute
        textureSelector(
            selectedTexPath = texAttr.mapPath,
            withNoneOption = false,
            channelOptions = channelOptions,
            selectedChannelOption = texAttr.channels
        ) { asset, channels ->
            lastTextureAttr = MapAttribute(asset?.path ?: "", channels)
            setEditAttrib(lastTextureAttr)
        }
    }

    private fun ColumnScope.vertexAttrSelector() {
        val vertAttr = editMatAttr as VertexAttribute

        menuRow {
            Text("Attribute name:") {
                modifier.alignY(AlignmentY.Center)
            }
            // todo: attribute name is not yet editable because that would require re-building the shader, which is
            //  difficult to trigger from here
            TextField(vertAttr.attribName) {
                defaultTextfieldStyle()
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.gap)
            }
        }
        Text("Not yet editable") {
            modifier
                .alignX(AlignmentX.End)
                .margin(top = sizes.smallGap)
                .textColor(colors.primary)
                .font(sizes.italicText)
        }
    }

    private fun defaultColorAttrib() = ConstColorAttribute(ColorData(defaultColor))
    private fun defaultValueAttrib() = ConstValueAttribute(defaultValue)
    private fun defaultTextureAttrib() = MapAttribute(availableTextures.getOrNull(0)?.path ?: "")
    private fun defaultVertexAttrib() = VertexAttribute(Attribute.COLORS.name)

    enum class ColorMode {
        VALUE,
        COLOR,
        TEXTURE,
        VERTEX
    }
}