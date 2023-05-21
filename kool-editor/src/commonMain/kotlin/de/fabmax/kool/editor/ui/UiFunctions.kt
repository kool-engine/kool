package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.toString

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
    precision: Int,
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

fun UiScope.labeledSlider(
    label: String,
    value: MutableStateValue<Float>,
    min: Float = 0f,
    max: Float = 1f,
    precision: Int = 3,
    onChange: (Float) -> Unit
) = Column(Grow.Std, scopeName = label) {
    menuRow {
        Text(label) {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
        }
        Text(value.use().toString(precision)) {
            modifier.alignY(AlignmentY.Center)
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

