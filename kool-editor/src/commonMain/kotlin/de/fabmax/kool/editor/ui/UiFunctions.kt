package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.toString
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
    Row(height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(start = sizes.gap)
        Text("X") {
            modifier
                .alignY(AlignmentY.Center)
                .textColor(MdColor.RED tone 300)
        }
        doubleTextField(x, xPrecision, width = Grow.Std) { onSet(it, y) }

        Text("Y") {
            modifier
                .margin(start = sizes.gap)
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
    Row(height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(start = sizes.gap)
        Text("X") {
            modifier
                .alignY(AlignmentY.Center)
                .textColor(MdColor.RED tone 300)
        }
        doubleTextField(x, xPrecision, width = Grow.Std) { onSet(it, y, z) }

        Text("Y") {
            modifier
                .margin(start = sizes.gap)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.GREEN tone 300)
        }
        doubleTextField(y, yPrecision, width = Grow.Std) { onSet(x, it, z) }

        Text("Z") {
            modifier
                .margin(start = sizes.gap)
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
    Row(height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)
        Text(label) {
            modifier.alignY(AlignmentY.Center)
        }
    }
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(start = sizes.gap)
        Text("X") {
            modifier
                .alignY(AlignmentY.Center)
                .textColor(MdColor.RED tone 300)
        }
        doubleTextField(x, xPrecision, width = Grow.Std) { onSet(it, y, z, w) }

        Text("Y") {
            modifier
                .margin(start = sizes.gap)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.GREEN tone 300)
        }
        doubleTextField(y, yPrecision, width = Grow.Std) { onSet(x, it, z, w) }

        Text("Z") {
            modifier
                .margin(start = sizes.gap)
                .alignY(AlignmentY.Center)
                .textColor(MdColor.BLUE tone 300)
        }
        doubleTextField(z, zPrecision, width = Grow.Std) { onSet(x, y, it, w) }

        Text("W") {
            modifier
                .margin(start = sizes.gap)
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

fun UiScope.labeledSlider(
    label: String,
    value: MutableStateValue<Float>,
    min: Float = 0f,
    max: Float = 1f,
    precision: Int = precisionForValue(max - min),
    onChange: (Float) -> Unit
) = Column(Grow.Std, scopeName = label) {
    menuRow {
        Text(label) {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
        }
        doubleTextField(value.use().toDouble(), precision, width = sizes.baseSize * 2) {
            value.set(it.toFloat())
        }
    }
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)
        Slider(value.use(), min, max) {
            modifier
                .width(Grow.Std)
                .onChange {
                    value.set(it)
                    onChange(it)
                }
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

inline fun UiScope.menuRow(block: RowScope.() -> Unit) = Row(width = Grow.Std, height = sizes.lineHeight) {
    modifier.margin(top = sizes.smallGap)
    block()
}

