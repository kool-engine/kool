package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.*

fun UiScope.editorTitleBar(windowDockable: UiDockable) {
    Row(Grow.Std, height = sizes.lineHeightTitle) {
        val cornerR = if (windowDockable.isDocked.use()) 0f else sizes.gap.px
        modifier
            .margin(sizes.borderWidth)
            .padding(horizontal = sizes.gap - sizes.borderWidth)
            .background(TitleBarBackground(UiColors.titleBg, cornerR, false))

        with(windowDockable) {
            registerDragCallbacks()
        }

        Text(windowDockable.name) {
            modifier
                .width(Grow.Std)
                .textColor(UiColors.titleText)
                .font(sizes.boldText)
                .alignY(AlignmentY.Center)
        }
    }
}

fun UiScope.doubleTextField(
    value: Double,
    precision: Int = precisionForValue(value),
    width: Dimension = FitContent,
    onSet: (Double) -> Unit
) = TextField {
    var text by remember(value.toString(precision))
    if (!isFocused.use()) {
        text = value.toString(precision)
    }
    defaultTextfieldStyle()
    modifier
        .text(text)
        .width(width)
        .margin(start = sizes.smallGap)
        .alignY(AlignmentY.Center)
        .textAlignX(AlignmentX.End)
        .onChange { text = it }
        .onEnterPressed { txt ->
            val d = txt.toDoubleOrNull()
            if (d != null && d.isFinite()) {
                onSet(d)
                // unfocus text field
                surface.requestFocus(null)
            }
        }
}

fun UiScope.intTextField(
    value: Int,
    width: Dimension = FitContent,
    onSet: (Int) -> Unit
) = TextField {
    var text by remember(value.toString())
    if (!isFocused.use()) {
        text = value.toString()
    }
    defaultTextfieldStyle()
    modifier
        .text(text)
        .width(width)
        .margin(start = sizes.smallGap)
        .alignY(AlignmentY.Center)
        .textAlignX(AlignmentX.End)
        .onChange { text = it }
        .onEnterPressed { txt ->
            txt.toIntOrNull()?.let {
                onSet(it)
                // unfocus text field
                surface.requestFocus(null)
            }
        }
}

fun UiScope.xyRow(
    label: String,
    x: Double,
    y: Double,
    xPrecision: Int = precisionForValue(x),
    yPrecision: Int = precisionForValue(y),
    onSet: (Double, Double) -> Unit
) = Column(width = Grow.Std) {
    menuRow {
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    menuRow(Dp.ZERO) {
        Text("X") {
            modifier
                .width(sizes.largeGap * 0.75f)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.RED tone 300)
        }
        doubleTextField(x, xPrecision, width = Grow.Std) { onSet(it, y) }

        Text("Y") {
            modifier
                .width(sizes.largeGap)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.GREEN tone 300)
        }
        doubleTextField(y, yPrecision, width = Grow.Std) { onSet(x, it) }
    }
}

fun UiScope.xyzRow(
    label: String,
    x: Double,
    y: Double,
    z: Double,
    xPrecision: Int = precisionForValue(x),
    yPrecision: Int = precisionForValue(y),
    zPrecision: Int = precisionForValue(z),
    onSet: (Double, Double, Double) -> Unit
) = Column(width = Grow.Std) {
    menuRow {
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    menuRow(Dp.ZERO) {
        Text("X") {
            modifier
                .width(sizes.largeGap * 0.75f)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.RED tone 300)
        }
        doubleTextField(x, xPrecision, width = Grow.Std) { onSet(it, y, z) }

        Text("Y") {
            modifier
                .width(sizes.largeGap)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.GREEN tone 300)
        }
        doubleTextField(y, yPrecision, width = Grow.Std) { onSet(x, it, z) }

        Text("Z") {
            modifier
                .width(sizes.largeGap)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.BLUE tone 300)
        }
        doubleTextField(z, zPrecision, width = Grow.Std) { onSet(x, y, it) }
    }
}

fun UiScope.xyzwRow(
    label: String,
    x: Double,
    y: Double,
    z: Double,
    w: Double,
    xPrecision: Int = precisionForValue(x),
    yPrecision: Int = precisionForValue(y),
    zPrecision: Int = precisionForValue(z),
    wPrecision: Int = precisionForValue(w),
    onSet: (Double, Double, Double, Double) -> Unit
) = Column(width = Grow.Std) {
    menuRow {
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    menuRow(Dp.ZERO) {
        Text("X") {
            modifier
                .width(sizes.largeGap * 0.75f)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.RED tone 300)
        }
        doubleTextField(x, xPrecision, width = Grow.Std) { onSet(it, y, z, w) }

        Text("Y") {
            modifier
                .width(sizes.largeGap)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.GREEN tone 300)
        }
        doubleTextField(y, yPrecision, width = Grow.Std) { onSet(x, it, z, w) }

        Text("Z") {
            modifier
                .width(sizes.largeGap)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.BLUE tone 300)
        }
        doubleTextField(z, zPrecision, width = Grow.Std) { onSet(x, y, it, w) }

        Text("W") {
            modifier
                .width(sizes.largeGap)
                .textAlignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.AMBER tone 300)
        }
        doubleTextField(w, wPrecision, width = Grow.Std) { onSet(x, y, z, it) }
    }
}

fun <T: Any> UiScope.labeledCombobox(
    label: String,
    items: List<T>,
    selectedIndex: MutableStateValue<Int>,
    onItemSelected: (T) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(sizes.baseSize * 3f)
            .alignY(AlignmentY.Center)
    }

    ComboBox {
        defaultComboBoxStyle()
        modifier
            .size(Grow.Std, sizes.lineHeight)
            .items(items)
            .selectedIndex(selectedIndex.use())
            .onItemSelected {
                selectedIndex.set(it)
                onItemSelected(items[it])
            }
    }
}

fun precisionForValue(value: Double): Int {
    if (value == 0.0) {
        return 4
    }
    val log = 3.5 - log10(abs(value))
    val digits = if (log.isFinite()) log.roundToInt() else 4
    return min(4, max(1, digits))
}

fun precisionForValue(value: Float): Int {
    if (value == 0f) {
        return 4
    }
    val log = 3.5f - log10(abs(value))
    val digits = if (log.isFinite()) log.roundToInt() else 4
    return min(4, max(1, digits))
}

fun UiScope.labeledColorPicker(
    label: String,
    pickerColor: MutableStateValue<Color>,
    isWithAlpha: Boolean = false,
    boxWidth: Dimension = sizes.baseSize * 2,
    onShow: ((Color) -> Unit)? = null,
    onHide: ((Color) -> Unit)? = null,
    onPreview: (Color) -> Unit
) = menuRow {

    val colorPickerPopup = remember {
        val popup = AutoPopup()
        popup.popupContent = Composable {
            modifier
                .background(RoundRectBackground(colors.background, sizes.smallGap))
                .border(RoundRectBorder(colors.secondaryVariant, sizes.smallGap, sizes.borderWidth))
                .padding(sizes.smallGap)

            val previewColor = pickerColor.value
            val hsv = previewColor.toHsv()
            val hue = remember(hsv.x)
            val sat = remember(hsv.y)
            val bri = remember(hsv.z)
            val alpha = if (isWithAlpha) remember(previewColor.a) else null
            val hexString = remember(previewColor.toHexString(isWithAlpha))

            ColorChooserV(hue, sat, bri, alpha, hexString) { color ->
                pickerColor.set(color)
                onPreview(color)
            }
        }
        popup.onShow = { onShow?.invoke(pickerColor.value) }
        popup.onHide = { onHide?.invoke(pickerColor.value) }
        popup
    }
    colorPickerPopup()

    Text(label) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
    }

    Box(width = boxWidth, height = Grow.Std) {
        var isHovered by remember(false)
        val borderColor = if (isHovered) colors.elevatedComponentBgHovered else colors.elevatedComponentBg
        modifier
            .backgroundColor(pickerColor.use())
            .border(RectBorder(borderColor, sizes.borderWidth))
            .onEnter { isHovered = true }
            .onExit { isHovered = false }
            .onClick { colorPickerPopup.show(it) }
    }
}

fun UiScope.labeledDoubleTextField(
    label: String,
    value: Double,
    precision: Int = precisionForValue(value),
    valueWidth: Dimension = sizes.baseSize * 2,
    onSet: (Double) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
    }
    doubleTextField(value, precision, valueWidth, onSet)
}

fun UiScope.labeledIntTextField(
    label: String,
    value: Int,
    valueWidth: Dimension = sizes.baseSize * 2,
    onSet: (Int) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
    }
    intTextField(value, valueWidth, onSet)
}

fun UiScope.labeledSlider(
    label: String,
    value: MutableStateValue<Float>,
    min: Float = 0f,
    max: Float = 1f,
    precision: Int = precisionForValue(max - min),
    valueWidth: Dimension = sizes.baseSize * 2,
    onChange: (Float) -> Unit
) = Column(Grow.Std, scopeName = label) {
    menuRow {
        Text(label) {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
        }
        doubleTextField(value.use().toDouble(), precision, valueWidth) {
            value.set(it.toFloat())
            onChange(it.toFloat())
        }
    }
    menuRow {
        Slider(value.use(), min, max) {
            defaultSliderStyle()
            modifier
                .width(Grow.Std)
                .onChange {
                    value.set(it)
                    onChange(it)
                }
        }
    }
}

fun UiScope.labeledCheckbox(
    label: String,
    state: MutableStateValue<Boolean>,
    onToggle: (Boolean) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
            .onClick {
                state.set(!state.value)
                onToggle(state.value)
            }
    }

    Checkbox(state.use()) {
        modifier
            .size(FitContent, sizes.lineHeight)
            .onToggle {
                state.set(it)
                onToggle(it)
            }
    }
}

fun UiScope.labeledSwitch(
    label: String,
    state: MutableStateValue<Boolean>,
    onToggle: (Boolean) -> Unit
) = menuRow {
    Text(label) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
            .onClick {
                state.set(!state.value)
                onToggle(state.value)
            }
    }

    Switch(state.use()) {
        modifier
            .size(FitContent, sizes.lineHeight)
            .onToggle {
                state.set(it)
                onToggle(it)
            }
    }
}

inline fun UiScope.menuRow(marginTop: Dp = sizes.smallGap, block: RowScope.() -> Unit) = Row(width = Grow.Std, height = sizes.lineHeight) {
    modifier.margin(top = marginTop)
    block()
}

fun ColumnScope.menuDivider(marginTop: Dp = sizes.smallGap, marginBottom: Dp = Dp.ZERO) {
    divider(colors.secondaryVariantAlpha(0.75f), marginTop = marginTop, marginBottom = marginBottom)
}

fun ButtonScope.defaultButtonStyle() {
    modifier.colors(buttonColor = colors.elevatedComponentBg, buttonHoverColor = colors.elevatedComponentBgHovered)
}

fun ComboBoxScope.defaultComboBoxStyle() {
    modifier.colors(
        textBackgroundColor = colors.componentBg,
        textBackgroundHoverColor = colors.componentBgHovered,
        expanderColor = colors.elevatedComponentBg,
        expanderHoverColor = colors.elevatedComponentBgHovered
    )
}

fun SliderScope.defaultSliderStyle() {
    modifier.colors(trackColor = colors.elevatedComponentBg, trackColorActive = colors.elevatedComponentBgHovered)
}

fun TextFieldScope.defaultTextfieldStyle() {
    val bgColor = if (isFocused.use()) colors.componentBgHovered else colors.componentBg
    modifier
        .colors(lineColor = null, lineColorFocused = null)
        .background(RoundRectBackground(bgColor, sizes.textFieldPadding))
        .padding(sizes.textFieldPadding)
}
