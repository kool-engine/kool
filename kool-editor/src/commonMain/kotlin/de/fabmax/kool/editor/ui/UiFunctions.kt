package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.Key
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.input.CursorMode
import de.fabmax.kool.input.CursorShape
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.*

fun UiScope.editorTitleBar(
    windowDockable: UiDockable,
    imageIcon: IconProvider? = null,
    title: String = windowDockable.name,
    roundedTop: Boolean = false,
    onClose: ((PointerEvent) -> Unit)? = null,
    titleBlock: (RowScope.() -> Unit)? = null
) {
    Row(Grow.Std, sizes.heightTitleBar) {
        modifier
            .padding(horizontal = sizes.gap - sizes.borderWidth)
            .zLayer(UiSurface.LAYER_FLOATING)
            .backgroundColor(UiColors.titleBg)

        if (roundedTop) {
            modifier.background(remember { TitleBgRenderer(UiColors.titleBg, UiColors.titleBgAccent, topRadius = sizes.gap.px, fade = TitleBgRenderer.FADE_WEAK) })
        } else {
            modifier.background(remember { TitleBgRenderer(UiColors.titleBg, UiColors.titleBgAccent, fade = TitleBgRenderer.FADE_WEAK) })
        }

        if (!windowDockable.isDocked.use()) {
            modifier.margin(sizes.borderWidth)
            imageIcon?.let {
                Image {
                    modifier
                        .margin(end = sizes.gap, top = sizes.heightTitleBar * 0.5f - Icons.medium.iconSize * 0.55f)
                        .iconImage(it, UiColors.titleText)
                }
            }
        }

        with(windowDockable) {
            registerDragCallbacks()
        }

        Text(title) {
            modifier
                .textColor(UiColors.titleText)
                .font(sizes.boldText.copy(glowColor = Color.BLACK.withAlpha(0.75f)))
                .alignY(AlignmentY.Center)
        }

        titleBlock?.let { it() }

        onClose?.let {
            Box(width = Grow.Std) {  }
            closeButton(onClose)
        }
    }
}

fun UiScope.closeButton(action: (PointerEvent) -> Unit) {
    Button {
        modifier
            .size(sizes.largeGap, sizes.largeGap)
            .padding(Dp.ZERO)
            .alignY(AlignmentY.Center)
            .isClickFeedback(false)
            .background(remember { CloseButtonBackground() })
            .onClick(action)
    }
}

class CloseButtonBackground : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) = node.run {
        val r = innerWidthPx * 0.5f
        val fgColor = colors.onBackground
        val bgColor =
        if ((node as ButtonNode).isHovered) {
            MdColor.RED
        } else {
            colors.componentBg
        }
        getUiPrimitives().localCircle(widthPx * 0.5f, heightPx * 0.5f, r, bgColor)

        getPlainBuilder().configured(fgColor) {
            translate(widthPx * 0.5f, heightPx * 0.5f, 0f)
            rotate(45f.deg, Vec3f.Z_AXIS)
            rect {
                size.set(r * 1.3f, 1.dp.px)
            }
            rect {
                size.set(1.dp.px, r * 1.3f)
            }
        }
    }
}

fun UiScope.doubleTextField(
    value: Double,
    precision: Int = precisionForValue(value),
    width: Dimension = FitContent,
    dragChangeSpeed: Double = 0.0,
    minValue: Double = Double.NEGATIVE_INFINITY,
    maxValue: Double = Double.POSITIVE_INFINITY,
    editHandler: ValueEditHandler<Double>,
    textFieldModifier: ((TextFieldModifier) -> Unit)? = null,
    selectTextOnFocusGain: Boolean = true
) = TextField {
    var text by remember(value.formatted(precision))
    var wasFocuesd by remember(false)
    var dragStartValue by remember(value)

    if (!isFocused.use()) {
        if (wasFocuesd) {
            // focus lost, apply edited value
            text.parseDouble(minValue, maxValue)?.let { editHandler.onEditEnd(dragStartValue, it) }
        } else {
            text = value.formatted(precision)
        }
    } else if (!wasFocuesd) {
        // gained focus
        dragStartValue = value
        editHandler.onEditStart(dragStartValue)
        if (selectTextOnFocusGain) {
            modifier.selectionRange(0, text.length)
        }
    }
    wasFocuesd = isFocused.value

    defaultTextfieldStyle()
    modifier
        .text(text)
        .width(width)
        .padding(horizontal = sizes.smallTextFieldPadding)
        .alignY(AlignmentY.Center)
        .textAlignX(AlignmentX.End)
        .onChange { text = it }
        .onEnterPressed {
            // unfocus text field, onSet() is called in focus lost handler
            surface.requestFocus(null)
        }

    if (!wasFocuesd && dragChangeSpeed != 0.0 && value.isFinite()) {
        modifier.clearDragCallbacks()
        modifier.clearHoverCallbacks()
        modifier
            .onHover { PointerInput.cursorShape = CursorShape.RESIZE_EW }
            .onDragStart {
                dragStartValue = value
                editHandler.onEditStart(dragStartValue)
                PointerInput.cursorMode = CursorMode.LOCKED
            }
            .onDrag {
                val dragSpeed = if (KeyboardInput.isShiftDown) 0.1f else 1f
                val dragVal = dragStartValue + dragChangeSpeed * dragSpeed * Dp.fromPx(it.pointer.dragMovement.x).value
                editHandler.onEdit(dragVal.clamp(minValue, maxValue))
            }
            .onDragEnd {
                editHandler.onEditEnd(dragStartValue, value)
                PointerInput.cursorMode = CursorMode.NORMAL
            }
    }
    textFieldModifier?.invoke(modifier)
}

fun String.parseDouble(min: Double = Double.NEGATIVE_INFINITY, max: Double = Double.POSITIVE_INFINITY): Double? {
    var d = replace(',', '.').toDoubleOrNull()
    if (d != null && d.isFinite()) {
        d = d.clamp(min, max)
    }
    return d
}

fun Double.formatted(precision: Int): String {
    val isScientific = precision >= 9 || abs(this) > 1e9
    return when {
        !isFinite() -> ""
        isScientific -> toString()
        else -> toString(precision)
    }
}

fun UiScope.intTextField(
    value: Int,
    width: Dimension = FitContent,
    dragChangeSpeed: Double = 0.0,
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE,
    editHandler: ValueEditHandler<Int>,
    textFieldModifier: ((TextFieldModifier) -> Unit)? = null
) = TextField {
    var text by remember(value.toString())
    var wasFocuesd by remember(false)
    var dragStartValue by remember(value)

    if (!isFocused.use()) {
        if (wasFocuesd) {
            // focus lost, apply edited value
            text.parseInt(minValue, maxValue)?.let { editHandler.onEditEnd(dragStartValue, it) }
        } else {
            text = value.toString()
        }
    } else if (!wasFocuesd) {
        // gained focus
        dragStartValue = value
        editHandler.onEditStart(dragStartValue)
    }
    wasFocuesd = isFocused.value

    defaultTextfieldStyle()
    modifier
        .text(text)
        .width(width)
        .padding(horizontal = sizes.smallTextFieldPadding)
        .alignY(AlignmentY.Center)
        .textAlignX(AlignmentX.End)
        .onChange { text = it }
        .onEnterPressed {
            // unfocus text field, onSet() is called in focus lost handler
            surface.requestFocus(null)
        }

    if (!wasFocuesd && dragChangeSpeed != 0.0) {
        modifier.clearDragCallbacks()
        modifier.clearHoverCallbacks()
        modifier
            .onHover { PointerInput.cursorShape = CursorShape.RESIZE_EW }
            .onDragStart {
                dragStartValue = value
                editHandler.onEditStart(dragStartValue)
            }
            .onDrag {
                val dragVal = (dragStartValue + dragChangeSpeed * Dp.fromPx(it.pointer.dragMovement.x).value).roundToInt()
                editHandler.onEdit(dragVal.clamp(minValue, maxValue))
            }
            .onDragEnd {
                editHandler.onEditEnd(dragStartValue, value)
            }
    }
    textFieldModifier?.invoke(modifier)
}

fun String.parseInt(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int? {
    var i = toIntOrNull()
    if (i != null) {
        i = i.clamp(min, max)
    }
    return i
}

fun ColumnScope.labeledXyRow(
    label: String,
    xy: Vec2d,
    precision: Vec2i = Vec2i(precisionForValue(xy.x), precisionForValue(xy.y)),
    minValues: Vec2d? = null,
    maxValues: Vec2d? = null,
    dragChangeSpeed: Vec2d = Vec2d.ZERO,
    editHandler: ValueEditHandler<Vec2d>
) {
    menuRow {
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    xyRow(xy, precision, minValues, maxValues, dragChangeSpeed, editHandler)
}

fun ColumnScope.xyRow(
    xy: Vec2d,
    precision: Vec2i = Vec2i(precisionForValue(xy.x), precisionForValue(xy.y)),
    minValues: Vec2d? = null,
    maxValues: Vec2d? = null,
    dragChangeSpeed: Vec2d = Vec2d.ZERO,
    editHandler: ValueEditHandler<Vec2d>
) = menuRow(Dp.ZERO) {
    val xEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec2d(startValue, xy.y))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec2d(value, xy.y))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec2d(startValue, xy.y), Vec2d(endValue, xy.y))
    }
    val yEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec2d(xy.x, startValue))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec2d(xy.x, value))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec2d(xy.x, startValue), Vec2d(xy.x, endValue))
    }

    Text("X") {
        modifier
            .width(sizes.largeGap * 0.75f)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.RED tone 300)
    }
    val minX = minValues?.x ?: Double.NEGATIVE_INFINITY
    val maxX = maxValues?.x ?: Double.POSITIVE_INFINITY
    doubleTextField(xy.x, precision.x, Grow.Std, dragChangeSpeed.x, minX, maxX, editHandler = xEditHandler)

    Text("Y") {
        modifier
            .width(sizes.largeGap)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.GREEN tone 300)
    }
    val minY = minValues?.y ?: Double.NEGATIVE_INFINITY
    val maxY = maxValues?.y ?: Double.POSITIVE_INFINITY
    doubleTextField(xy.y, precision.y, Grow.Std, dragChangeSpeed.y, minY, maxY, editHandler = yEditHandler)
}

fun ColumnScope.labeledXyzRow(
    label: String,
    xyz: Vec3d,
    precision: Vec3i = Vec3i(precisionForValue(xyz.x), precisionForValue(xyz.y), precisionForValue(xyz.z)),
    minValues: Vec3d? = null,
    maxValues: Vec3d? = null,
    dragChangeSpeed: Vec3d = Vec3d.ZERO,
    editHandler: ValueEditHandler<Vec3d>
) {
    menuRow {
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    xyzRow(xyz, precision, minValues, maxValues, dragChangeSpeed, editHandler)
}


fun ColumnScope.xyzRow(
    xyz: Vec3d,
    precision: Vec3i = Vec3i(precisionForValue(xyz.x), precisionForValue(xyz.y), precisionForValue(xyz.z)),
    minValues: Vec3d? = null,
    maxValues: Vec3d? = null,
    dragChangeSpeed: Vec3d = Vec3d.ZERO,
    editHandler: ValueEditHandler<Vec3d>
) = menuRow(Dp.ZERO) {
    val xEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec3d(startValue, xyz.y, xyz.z))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec3d(value, xyz.y, xyz.z))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec3d(startValue, xyz.y, xyz.z), Vec3d(endValue, xyz.y, xyz.z))
    }
    val yEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec3d(xyz.x, startValue, xyz.z))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec3d(xyz.x, value, xyz.z))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec3d(xyz.x, startValue, xyz.z), Vec3d(xyz.x, endValue, xyz.z))
    }
    val zEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec3d(xyz.x, xyz.y, startValue))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec3d(xyz.x, xyz.y, value))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec3d(xyz.x, xyz.y, startValue), Vec3d(xyz.x, xyz.y, endValue))
    }

    Text("X") {
        modifier
            .width(sizes.largeGap * 0.75f)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.RED tone 300)
    }
    val minX = minValues?.x ?: Double.NEGATIVE_INFINITY
    val maxX = maxValues?.x ?: Double.POSITIVE_INFINITY
    doubleTextField(xyz.x, precision.x, Grow.Std, dragChangeSpeed.x, minX, maxX, editHandler = xEditHandler)

    Text("Y") {
        modifier
            .width(sizes.largeGap)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.GREEN tone 300)
    }
    val minY = minValues?.y ?: Double.NEGATIVE_INFINITY
    val maxY = maxValues?.y ?: Double.POSITIVE_INFINITY
    doubleTextField(xyz.y, precision.y, Grow.Std, dragChangeSpeed.y, minY, maxY, editHandler = yEditHandler)

    Text("Z") {
        modifier
            .width(sizes.largeGap)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.BLUE tone 300)
    }
    val minZ = minValues?.z ?: Double.NEGATIVE_INFINITY
    val maxZ = maxValues?.z ?: Double.POSITIVE_INFINITY
    doubleTextField(xyz.z, precision.z, Grow.Std, dragChangeSpeed.z, minZ, maxZ, editHandler = zEditHandler)
}

fun ColumnScope.labeledXyzwRow(
    label: String,
    xyzw: Vec4d,
    precision: Vec4i = Vec4i(precisionForValue(xyzw.x), precisionForValue(xyzw.y), precisionForValue(xyzw.z), precisionForValue(xyzw.w)),
    minValues: Vec4d? = null,
    maxValues: Vec4d? = null,
    dragChangeSpeed: Vec4d = Vec4d.ZERO,
    editHandler: ValueEditHandler<Vec4d>
) {
    menuRow {
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    xyzwRow(xyzw, precision, minValues, maxValues, dragChangeSpeed, editHandler)
}

fun ColumnScope.xyzwRow(
    xyzw: Vec4d,
    precision: Vec4i = Vec4i(precisionForValue(xyzw.x), precisionForValue(xyzw.y), precisionForValue(xyzw.z), precisionForValue(xyzw.w)),
    minValues: Vec4d? = null,
    maxValues: Vec4d? = null,
    dragChangeSpeed: Vec4d = Vec4d.ZERO,
    editHandler: ValueEditHandler<Vec4d>
) = menuRow(Dp.ZERO) {
    val xEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec4d(startValue, xyzw.y, xyzw.z, xyzw.w))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec4d(value, xyzw.y, xyzw.z, xyzw.w))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec4d(startValue, xyzw.y, xyzw.z, xyzw.w), Vec4d(endValue, xyzw.y, xyzw.z, xyzw.w))
    }
    val yEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec4d(xyzw.x, startValue, xyzw.z, xyzw.w))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec4d(xyzw.x, value, xyzw.z, xyzw.w))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec4d(xyzw.x, startValue, xyzw.z, xyzw.w), Vec4d(xyzw.x, endValue, xyzw.z, xyzw.w))
    }
    val zEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec4d(xyzw.x, xyzw.y, startValue, xyzw.w))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec4d(xyzw.x, xyzw.y, value, xyzw.w))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec4d(xyzw.x, xyzw.y, startValue, xyzw.w), Vec4d(xyzw.x, xyzw.y, endValue, xyzw.w))
    }
    val wEditHandler = object : ValueEditHandler<Double> {
        override fun onEditStart(startValue: Double) = editHandler.onEditStart(Vec4d(xyzw.x, xyzw.y, xyzw.z, startValue))
        override fun onEdit(value: Double) = editHandler.onEdit(Vec4d(xyzw.x, xyzw.y, xyzw.z, value))
        override fun onEditEnd(startValue: Double, endValue: Double) = editHandler.onEditEnd(Vec4d(xyzw.x, xyzw.y, xyzw.z, startValue), Vec4d(xyzw.x, xyzw.y, xyzw.z, endValue))
    }

    Text("X") {
        modifier
            .width(sizes.largeGap * 0.75f)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.RED tone 300)
    }
    val minX = minValues?.x ?: Double.NEGATIVE_INFINITY
    val maxX = maxValues?.x ?: Double.POSITIVE_INFINITY
    doubleTextField(xyzw.x, precision.x, Grow.Std, dragChangeSpeed.x, minX, maxX, editHandler = xEditHandler)

    Text("Y") {
        modifier
            .width(sizes.largeGap)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.GREEN tone 300)
    }
    val minY = minValues?.y ?: Double.NEGATIVE_INFINITY
    val maxY = maxValues?.y ?: Double.POSITIVE_INFINITY
    doubleTextField(xyzw.y, precision.y, Grow.Std, dragChangeSpeed.y, minY, maxY, editHandler = yEditHandler)

    Text("Z") {
        modifier
            .width(sizes.largeGap)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.BLUE tone 300)
    }
    val minZ = minValues?.z ?: Double.NEGATIVE_INFINITY
    val maxZ = maxValues?.z ?: Double.POSITIVE_INFINITY
    doubleTextField(xyzw.z, precision.z, Grow.Std, dragChangeSpeed.z, minZ, maxZ, editHandler = zEditHandler)

    Text("W") {
        modifier
            .width(sizes.largeGap)
            .margin(end = sizes.smallGap)
            .textAlignX(AlignmentX.End)
            .alignY(AlignmentY.Center)
            .textColor(MdColor.AMBER tone 300)
    }
    val minW = minValues?.w ?: Double.NEGATIVE_INFINITY
    val maxW = maxValues?.w ?: Double.POSITIVE_INFINITY
    doubleTextField(xyzw.w, precision.w, Grow.Std, dragChangeSpeed.w, minW, maxW, editHandler = wEditHandler)
}

fun <T: Any> ColumnScope.labeledCombobox(
    label: String,
    items: List<T>,
    selectedIndex: Int,
    labelWidth: Dimension = sizes.editorLabelWidthSmall,
    valueWidth: Dimension = Grow.Std,
    onItemSelected: (T) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(labelWidth)
            .alignY(AlignmentY.Center)
    }

    ComboBox {
        defaultComboBoxStyle()
        modifier
            .width(valueWidth)
            .items(items)
            .selectedIndex(selectedIndex)
            .onItemSelected {
                onItemSelected(items[it])
            }
    }
}

fun precisionForValue(value: Double): Int {
    if (value == 0.0) {
        return 3
    } else if (!value.isFinite()) {
        return 2
    }
    val log = 3.5 - log10(abs(value))
    val digits = if (log.isFinite()) log.roundToInt() else 3
    return min(3, max(1, digits))
}

fun ColumnScope.labeledColorPicker(
    label: String,
    pickerColor: Color,
    isWithAlpha: Boolean = false,
    boxWidth: Dimension = sizes.baseSize * 2,
    editHandler: ValueEditHandler<Color>
) = menuRow {
    Text(label) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
    }
    colorPicker(pickerColor, isWithAlpha, boxWidth, editHandler)
}

fun UiScope.colorPicker(
    pickerColor: Color,
    isWithAlpha: Boolean = false,
    boxWidth: Dimension = sizes.baseSize * 2,
    editHandler: ValueEditHandler<Color>
): UiScope {
    val currentColor = remember(pickerColor)
    var editColor by remember(pickerColor)
    var editStartColor by remember(pickerColor)

    // proxy current picker color in currentColor so that following remember block always uses the up-to-date value
    // instead of the initial value, captured when remember was first called
    currentColor.set(pickerColor)

    val colorPickerPopup = remember {
        val popup = AutoPopup()
        popup.popupContent = Composable {
            modifier
                .background(RoundRectBackground(colors.background, sizes.smallGap))
                .border(RoundRectBorder(colors.secondaryVariant, sizes.smallGap, sizes.borderWidth))
                .padding(sizes.smallGap)

            val hsv = currentColor.use().toHsv()
            val hue = remember(hsv.h)
            val sat = remember(hsv.s)
            val bri = remember(hsv.v)
            val alpha = if (isWithAlpha) remember(currentColor.value.a) else null
            val hexString = remember(currentColor.value.toHexString(isWithAlpha))

            ColorChooserV(hue, sat, bri, alpha, hexString) { color ->
                editColor = color
                editHandler.onEdit(color)
            }
        }
        popup.onShow = {
            editHandler.onEditStart(currentColor.value)
            editStartColor = currentColor.value
        }
        popup.onHide = { editHandler.onEditEnd(editStartColor, editColor) }
        popup
    }
    colorPickerPopup()

    return Box(width = boxWidth, height = Grow.Std) {
        var isHovered by remember(false)
        val borderColor = if (isHovered) colors.elevatedComponentBgHovered else colors.elevatedComponentBg
        modifier
            .background(RoundRectBackground(pickerColor, sizes.smallGap))
            .border(RoundRectBorder(borderColor, sizes.smallGap, sizes.borderWidth))
            .onEnter { isHovered = true }
            .onExit { isHovered = false }
            .onClick { colorPickerPopup.show(it) }
    }
}

fun ColumnScope.labeledDoubleTextField(
    label: String,
    value: Double,
    precision: Int = precisionForValue(value),
    labelWidth: Dimension = sizes.editorLabelWidthLarge,
    valueWidth: Dimension = Grow.Std,
    dragChangeSpeed: Double = 0.0,
    minValue: Double = Double.NEGATIVE_INFINITY,
    maxValue: Double = Double.POSITIVE_INFINITY,
    textFieldModifier: ((TextFieldModifier) -> Unit)? = null,
    editHandler: ValueEditHandler<Double>
) = menuRow {
    Text(label) {
        modifier
            .width(labelWidth)
            .alignY(AlignmentY.Center)
    }
    doubleTextField(value, precision, valueWidth, dragChangeSpeed, minValue, maxValue, editHandler, textFieldModifier)
}

fun ColumnScope.labeledIntTextField(
    label: String,
    value: Int,
    labelWidth: Dimension = sizes.editorLabelWidthLarge,
    valueWidth: Dimension = Grow.Std,
    dragChangeSpeed: Double = 0.0,
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE,
    textFieldModifier: ((TextFieldModifier) -> Unit)? = null,
    editHandler: ValueEditHandler<Int>
) = menuRow {
    Text(label) {
        modifier
            .width(labelWidth)
            .alignY(AlignmentY.Center)
    }
    intTextField(value, valueWidth, dragChangeSpeed, minValue, maxValue, editHandler, textFieldModifier)
}

fun ColumnScope.labeledTextField(
    label: String,
    text: String,
    labelWidth: Dimension = sizes.editorLabelWidthLarge,
    valueWidth: Dimension = Grow.Std,
    textFieldModifier: ((TextFieldModifier) -> Unit)? = null,
    onEdited: (String) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(labelWidth)
            .alignY(AlignmentY.Center)
    }

    var editText by remember(text)
    TextField(editText) {
        if (!isFocused.use()) {
            editText = text
        }

        defaultTextfieldStyle()
        modifier
            .width(valueWidth)
            .onChange { editText = it }
            .onEnterPressed {
                onEdited(editText)
                surface.unfocus(this)
            }
        textFieldModifier?.invoke(modifier)
    }
}

fun UiScope.labeledSlider(
    label: String,
    value: Double,
    min: Double = 0.0,
    max: Double = 1.0,
    precision: Int = precisionForValue(max - min),
    labelWidth: Dimension = sizes.editorLabelWidthLarge,
    valueWidth: Dimension = Grow.Std,
    editHandler: ValueEditHandler<Double>,
) = Column(Grow.Std, scopeName = label) {
    menuRow {
        Text(label) {
            modifier
                .width(labelWidth)
                .alignY(AlignmentY.Center)
        }
        doubleTextField(value, precision, valueWidth, 0.0, min, max, editHandler)
    }
    menuRow {
        var dragStartValue by remember(value)
        Slider(value.toFloat(), min.toFloat(), max.toFloat()) {
            defaultSliderStyle()
            modifier
                .width(Grow.Std)
                .onChange { editHandler.onEdit(it.toDouble()) }
                .onDragEnd { editHandler.onEditEnd(dragStartValue, value) }
                .onDragStart {
                    editHandler.onEditStart(value)
                    dragStartValue = value
                }
        }
    }
}

fun ColumnScope.labeledCheckbox(
    label: String,
    state: Boolean,
    onToggle: (Boolean) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
            .onClick {
                onToggle(!state)
            }
    }

    Checkbox(state) {
        modifier
            .size(FitContent, sizes.editItemHeight)
            .onToggle {
                onToggle(it)
            }
    }
}

fun ColumnScope.labeledSwitch(
    label: String,
    state: Boolean,
    onToggle: (Boolean) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
            .onClick {
                onToggle(!state)
            }
    }

    Switch(state) {
        modifier
            .size(FitContent, sizes.editItemHeight)
            .onToggle {
                onToggle(it)
            }
    }
}

fun ColumnScope.okButton(xAlign: AlignmentX = AlignmentX.End, onClick: (PointerEvent) -> Unit) = menuRow {
    if (xAlign != AlignmentX.Start) {
        Box(width = Grow.Std) { }
    }
    Button("OK") {
        modifier
            .size(sizes.baseSize * 2, sizes.lineHeight)
            .alignY(AlignmentY.Center)
            .onClick(onClick)
    }
    if (xAlign == AlignmentX.Center) {
        Box(width = Grow.Std) { }
    }
}

inline fun ColumnScope.menuRow(
    marginTop: Dp = sizes.smallGap,
    marginBottom: Dp = sizes.smallGap,
    marginStart: Dp = sizes.editorPanelMarginStart,
    marginEnd: Dp = sizes.editorPanelMarginEnd,
    block: RowScope.() -> Unit
) = Row(width = Grow.Std, height = sizes.lineHeight) {
    modifier.margin(start = marginStart, end = marginEnd, top = marginTop, bottom = marginBottom)
    block()
}

fun UiScope.textureSelector(
    selectedTexPath: String,
    withNoneOption: Boolean,
    selectedChannelOption: String? = null,
    channelOptions: List<String> = emptyList(),
    onSelect: (AssetItem?, String?) -> Unit
) = Column {
    val textures = mutableListOf<AssetOption>()
    if (withNoneOption) {
        textures += AssetOption("None", null)
    }
    textures += KoolEditor.instance.availableAssets.textureAssets.map { AssetOption(it.name, it) }
    val selectedTexIndex = textures.indexOfFirst { selectedTexPath == it.assetItem?.path }

    ComboBox {
        defaultComboBoxStyle()
        modifier
            .width(sizes.baseSize * 6)
            .items(textures)
            .selectedIndex(selectedTexIndex)
            .onItemSelected {
                onSelect(textures[it].assetItem, selectedChannelOption ?: channelOptions.getOrNull(0))
            }
    }

    if (selectedTexPath.isNotEmpty()) {
        val texRef = AssetReference.Texture(selectedTexPath)
        val tex = KoolEditor.instance.cachedAppAssets.getTextureMutableState(texRef).use()
        Image(tex) {
            modifier
                .margin(top = sizes.gap)
                .size(sizes.baseSize * 6, sizes.baseSize * 6)
                .imageZ(UiSurface.LAYER_BACKGROUND)
        }
    }
    if (channelOptions.isNotEmpty()) {
        Row(width = Grow.Std) {
            modifier.margin(vertical = sizes.gap)
            Text("Channel:") {
                modifier.alignY(AlignmentY.Center)
            }
            ComboBox {
                defaultComboBoxStyle()
                modifier
                    .margin(start = sizes.gap)
                    .alignY(AlignmentY.Center)
                    .width(Grow.Std)
                    .items(channelOptions)
                    .selectedIndex(channelOptions.indexOf(selectedChannelOption))
                    .onItemSelected {
                        onSelect(textures.getOrNull(selectedTexIndex)?.assetItem, channelOptions[it])
                    }
            }
        }
    }
}

fun ColumnScope.heightmapSelector(
    selectedHeightmapPath: String?,
    withNoneOption: Boolean,
    onSelect: (AssetItem?) -> Unit
) {
    val heightmaps = mutableListOf<AssetOption>()
    if (withNoneOption) {
        heightmaps += AssetOption("None", null)
    }
    heightmaps += KoolEditor.instance.availableAssets.heightmapAssets.map { AssetOption(it.name, it) }
    val selectedIndex = heightmaps.indexOfFirst { selectedHeightmapPath == it.assetItem?.path }

    labeledCombobox("Heightmap:", heightmaps, selectedIndex) {
        onSelect(it.assetItem)
    }
}

private class AssetOption(val name: String, val assetItem: AssetItem?) {
    override fun toString(): String = name
}

fun UiScope.iconButton(
    icon: IconProvider,
    tooltip: String? = null,
    toggleState: Boolean = false,
    tint: Color = colors.onBackground,
    margin: Dp = sizes.smallGap,
    width: Dimension = FitContent,
    height: Dimension = FitContent,
    padding: Dp = sizes.smallGap * 0.5f,
    boxBlock: (UiScope.() -> Unit)? = null,
    onClick: (PointerEvent) -> Unit
) = Box(width, height) {
    var isHovered by remember(false)
    var isClickFeedback by remember(false)

    val bgColor = when {
        isClickFeedback -> colors.elevatedComponentBgHovered
        toggleState || isHovered -> colors.componentBgHovered
        else -> null
    }

    bgColor?.let {
        modifier.background(RoundRectBackground(it, sizes.smallGap))
    }

    modifier
        .align(AlignmentX.Center, AlignmentY.Center)
        .margin(margin)
        .padding(padding)
        .onPointer { isClickFeedback = it.pointer.isLeftButtonDown }
        .onEnter { isHovered = true }
        .onExit {
            isHovered = false
            isClickFeedback = false
        }
        .onClick(onClick)

    Image {
        modifier
            .align(AlignmentX.Center, AlignmentY.Center)
            .iconImage(icon, tint)
    }

    tooltip?.let {
        Tooltip(it, borderColor = colors.secondaryVariant)
    }

    boxBlock?.invoke(this)
}

fun UiScope.iconTextButton(
    icon: IconProvider,
    text: String,
    tooltip: String? = null,
    toggleState: Boolean = false,
    tint: Color = Color.WHITE,
    width: Dimension = FitContent,
    margin: Dp = sizes.smallGap,
    bgColor: Color = colors.elevatedComponentBg,
    bgColorHovered: Color = colors.elevatedComponentBgHovered,
    bgColorClicked: Color = colors.elevatedComponentClickFeedback,
    boxBlock: (UiScope.() -> Unit)? = null,
    onClick: (PointerEvent) -> Unit
) = Box {
    var isHovered by remember(false)
    var isClickFeedback by remember(false)

    val color = when {
        isClickFeedback -> bgColorClicked
        toggleState || isHovered -> bgColorHovered
        else -> bgColor
    }

    modifier
        .background(RoundRectBackground(color, sizes.smallGap))
        .align(AlignmentX.Center, AlignmentY.Center)
        .margin(margin)
        .width(width)
        .height(sizes.editItemHeight)
        .onPointer { isClickFeedback = it.pointer.isLeftButtonDown }
        .onEnter { isHovered = true }
        .onExit {
            isHovered = false
            isClickFeedback = false
        }
        .onClick(onClick)

    Row {
        modifier
            .align(AlignmentX.Center, AlignmentY.Center)

        Image {
            modifier
                .alignY(AlignmentY.Center)
                .iconImage(icon, tint)
                .margin(end = sizes.gap)
        }
        Text(text) {
            modifier
                .alignY(AlignmentY.Center)
                .textColor(tint)
                .margin(horizontal = sizes.gap)
        }
    }

    tooltip?.let {
        Tooltip(it, borderColor = colors.secondaryVariant)
    }

    boxBlock?.invoke(this)
}

fun ColumnScope.menuDivider(
    marginStart: Dp = sizes.editorPanelMarginStart,
    marginEnd: Dp = sizes.editorPanelMarginEnd,
    marginTop: Dp = sizes.smallGap,
    marginBottom: Dp = Dp.ZERO,
    color: Color = colors.weakDividerColor
) {
    Box(Grow.Std, sizes.borderWidth) {
        modifier
            .backgroundColor(color)
            .margin(start = marginStart, end = marginEnd, top = marginTop, bottom = marginBottom)
    }
}

fun ColumnScope.toolbarDivider(marginTop: Dp = sizes.smallGap, marginBottom: Dp = Dp.ZERO, color: Color = colors.weakDividerColor) {
    divider(color, marginTop = marginTop, marginBottom = marginBottom)
}

fun UiScope.keyLabel(key: Key) = Row {
    modifier.alignY(AlignmentY.Center)

    val binding = key.binding
    if (binding.keyMod.isCtrl) {
        singleKey("Ctrl")
        Box(width = sizes.smallGap) { }
    }
    if (binding.keyMod.isAlt) {
        singleKey("Alt")
        Box(width = sizes.smallGap) { }
    }
    if (binding.keyMod.isShift) {
        singleKey("Shift")
        Box(width = sizes.smallGap) { }
    }
    singleKey(binding.keyCode.name)
}

fun UiScope.singleKey(text: String) = Text(text) {
    modifier
        .height(sizes.largeGap)
        .padding(sizes.smallGap * 0.9f)
        .border(RoundRectBorder(colors.strongDividerColor, sizes.smallGap, borderWidth = 1.dp))
        .font(sizes.smallText)
        .textAlign(AlignmentX.Center, AlignmentY.Center)
        .textColor(UiColors.secondaryBright)
}

fun ButtonScope.defaultButtonStyle() {
    modifier.colors(buttonColor = colors.elevatedComponentBg, buttonHoverColor = colors.elevatedComponentBgHovered)
}

fun ComboBoxScope.defaultComboBoxStyle() {
    modifier
        .height(sizes.editItemHeight)
        .clearWheelCallbacks()
        .padding(vertical = sizes.smallTextFieldPadding)
        .colors(
            textBackgroundColor = colors.componentBg,
            textBackgroundHoverColor = colors.componentBgHovered,
            expanderColor = colors.elevatedComponentBg,
            expanderHoverColor = colors.elevatedComponentBgHovered
        )
        .popupColors(
            popupBackgroundColor = colors.backgroundMid,
            popupHoverColor = colors.componentBgHovered,
            popupHoverTextColor = colors.onBackground,
            popupBorderColor = colors.secondaryVariant
        )
}

fun SliderScope.defaultSliderStyle() {
    modifier.colors(trackColor = colors.elevatedComponentBg, trackColorActive = colors.elevatedComponentBgHovered)
}

fun TextFieldScope.defaultTextfieldStyle() {
    val bgColor = if (isFocused.use()) colors.componentBgHovered else colors.componentBg
    modifier
        .colors(lineColor = null, lineColorFocused = null)
        .background(RoundRectBackground(bgColor, sizes.smallTextFieldPadding))
        .padding(horizontal = sizes.gap, vertical = sizes.smallTextFieldPadding)
}

fun UiScope.defaultPopupStyle(layout: Layout = ColumnLayout) {
    modifier
        .background(RoundRectBackground(colors.backgroundMid, sizes.smallGap))
        .border(RoundRectBorder(colors.secondaryVariant, sizes.smallGap, sizes.borderWidth))
        .layout(layout)
        .padding(sizes.gap)
}

fun interface ValueEditHandler<T> {
    fun onEditStart(startValue: T) { }
    fun onEdit(value: T)
    fun onEditEnd(startValue: T, endValue: T) = onEdit(endValue)
}

fun interface ActionValueEditHandler<T> : ValueEditHandler<T> {
    override fun onEdit(value: T) = makeEditAction(value, value).doAction()
    override fun onEditEnd(startValue: T, endValue: T) = makeEditAction(startValue, endValue).apply()

    fun makeEditAction(undoValue: T, applyValue: T) : EditorAction
}

fun UiScope.defaultScrollbarModifierV(): ((ScrollbarModifier) -> Unit) = {
    it
        .width(sizes.scrollbarWidth)
        .margin(sizes.scrollbarWidth)
}

fun UiScope.defaultScrollbarModifierH(): ((ScrollbarModifier) -> Unit) = {
    it
        .height(sizes.scrollbarWidth)
        .margin(sizes.scrollbarWidth)
}
