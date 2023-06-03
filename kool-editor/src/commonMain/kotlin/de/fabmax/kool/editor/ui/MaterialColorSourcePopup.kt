package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class MaterialColorSourcePopup(
    var editColorAttr: MaterialAttribute,
    var editHandler: ActionValueEditHandler<MaterialAttribute>
) : AutoPopup(hideOnOutsideClick = false) {

    private var editStartAttr = editColorAttr

    private val availableTextures: List<AssetItem>
        get() = KoolEditor.instance.availableAssets.textureAssets

    init {
        popupContent = Composable {
            content()
        }

        onShow = {
            editStartAttr = editColorAttr
            editHandler.onEditStart(editStartAttr)
        }
        onHide = {
            editHandler.onEditEnd(editStartAttr, editColorAttr)
        }
    }

    private fun UiScope.setEditAttrib(attr: MaterialAttribute) {
        editHandler.onEdit(attr)
        surface.triggerUpdate()
    }

    private fun UiScope.content() {
        defaultPopupStyle()

        val colorMode = when (editColorAttr) {
            is ConstColorAttribute -> ColorMode.COLOR
            is ConstValueAttribute -> ColorMode.COLOR
            is MapAttribute -> ColorMode.TEXTURE
            is VertexAttribute -> ColorMode.VERTEX
        }

        Row(width = sizes.baseSize * 6, height = sizes.lineHeight) {
            Text("Color") {
                var isHovered by remember(false)
                if (isHovered) {
                    modifier.backgroundColor(colors.componentBgHovered)
                } else if (colorMode == ColorMode.COLOR) {
                    modifier.backgroundColor(colors.componentBg)
                }

                modifier
                    .size(Grow.Std, Grow.Std)
                    .textAlignX(AlignmentX.Center)
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }

                if (colorMode != ColorMode.COLOR) {
                    modifier.onClick {
                        setEditAttrib(defaultColorAttrib())
                    }
                }
            }
            divider(verticalMargin = Dp.ZERO)
            Text("Texture") {
                var isHovered by remember(false)
                if (isHovered) {
                    modifier.backgroundColor(colors.componentBgHovered)
                } else if (colorMode == ColorMode.TEXTURE) {
                    modifier.backgroundColor(colors.componentBg)
                }

                modifier
                    .size(Grow.Std, Grow.Std)
                    .textAlignX(AlignmentX.Center)
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }

                if (colorMode != ColorMode.TEXTURE) {
                    modifier.onClick {
                        setEditAttrib(defaultTextureAttrib())
                    }
                }
            }
            divider(verticalMargin = Dp.ZERO)
            Text("Vertex") {
                var isHovered by remember(false)
                if (isHovered) {
                    modifier.backgroundColor(colors.componentBgHovered)
                } else if (colorMode == ColorMode.VERTEX) {
                    modifier.backgroundColor(colors.componentBg)
                }

                modifier
                    .size(Grow.Std, Grow.Std)
                    .textAlignX(AlignmentX.Center)
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }

                if (colorMode != ColorMode.VERTEX) {
                    modifier.onClick {
                        setEditAttrib(defaultVertexAttrib())
                    }
                }
            }
        }
        Box {
            modifier
                .size(Grow.Std, sizes.borderWidth)
                .backgroundColor(colors.secondaryVariant)
                .margin(bottom = sizes.gap)
        }

        when (colorMode) {
            ColorMode.COLOR -> colorSelector()
            ColorMode.TEXTURE -> textureSelector()
            ColorMode.VERTEX -> vertexAttrSelector()
        }
        okButton { hide() }
    }

    private fun UiScope.colorSelector() {
        val color = when (val attr = editColorAttr) {
            is ConstColorAttribute -> {
                attr.color.toColor().toSrgb()
            }
            is ConstValueAttribute -> {
                val f = attr.value
                Color(f, f, f).toSrgb()
            }
            else -> throw IllegalStateException("material attribute is neither color nor value")
        }

        val hsv = color.toHsv()
        val hue = remember(0f).apply { set(hsv.x) }
        val sat = remember(0f).apply { set(hsv.y) }
        val bri = remember(0f).apply { set(hsv.z) }
        val alpha = remember(0f).apply { set(color.a) }
        val hexString = remember(color.toHexString())

        ColorChooserV(hue, sat, bri, alpha, hexString) { editColor ->
            setEditAttrib(ConstColorAttribute(ColorData(editColor.toLinear())))
        }
    }

    private fun UiScope.textureSelector() {
        val texAttr = editColorAttr as MapAttribute
        textureSelector(texAttr.mapPath, false) {
            setEditAttrib(MapAttribute(it.path))
        }
    }

    private fun UiScope.vertexAttrSelector() {
        val vertAttr = editColorAttr as VertexAttribute

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

    private fun defaultColorAttrib() = ConstColorAttribute(ColorData(MdColor.GREY.toLinear()))
    private fun defaultTextureAttrib() = MapAttribute(availableTextures.getOrNull(0)?.path ?: "")
    private fun defaultVertexAttrib() = VertexAttribute(Attribute.COLORS.name)

    enum class ColorMode {
        COLOR,
        TEXTURE,
        VERTEX
    }
}