package de.fabmax.kool.demo

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.toString

object UiSizes {
    val hGap: Dp get() = Settings.uiSize.value.sizes.gap * 1.25f
    val vGap: Dp get() = Settings.uiSize.value.sizes.gap

    val baseSize: Dp get() = Settings.uiSize.value.sizes.gap * 4f
    val menuWidth: Dp get() = baseSize * 7f
}

fun UiScope.MenuRow(vGap: Dp = UiSizes.vGap, block: UiScope.() -> Unit) {
    Row(width = Grow.Std) {
        modifier.margin(horizontal = UiSizes.hGap, vertical = vGap)
        block()
    }
}

fun UiScope.MenuSlider(
    value: Float,
    min: Float,
    max: Float,
    txtFormat: (Float) -> String = { it.toString(2) },
    txtWidth: Dp = UiSizes.baseSize,
    onChangeEnd: ((Float) -> Unit)? = null,
    onChange: (Float) -> Unit
) {
    Slider(value, min, max) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
            .margin(horizontal = sizes.gap)
            .onChange(onChange)
        modifier.onChangeEnd = onChangeEnd
    }
    if (txtWidth.value > 0f) {
        Text(txtFormat(value)) {
            labelStyle()
            modifier.width(txtWidth).textAlignX(AlignmentX.End)
        }
    }
}

fun UiScope.MenuSlider2(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    txtFormat: (Float) -> String = { it.toString(2) },
    onChange: (Float) -> Unit
) {
    MenuRow {
        Text(label) { labelStyle(Grow.Std) }
        Text(txtFormat(value)) { labelStyle() }
    }
    MenuRow {
        Slider(value, min, max) {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
                .onChange(onChange)
        }
    }
}

fun UiScope.LabeledRadioButton(label: String, toggleState: Boolean, indent: Dp = sizes.gap, onActivate: () -> Unit) {
    MenuRow {
        modifier.padding(start = indent)
        RadioButton(toggleState) {
            modifier
                .alignY(AlignmentY.Center)
                .margin(end = sizes.gap)
                .onToggle {
                    if (it) {
                        onActivate()
                    }
                }
        }
        Text(label) {
            labelStyle(Grow.Std)
            modifier.onClick { onActivate() }
        }
    }
}

fun UiScope.LabeledSwitch(label: String, toggleState: MutableStateValue<Boolean>, onToggle: ((Boolean) -> Unit)? = null) {
    MenuRow {
        Text(label) {
            labelStyle(Grow.Std)
            modifier.onClick {
                toggleState.toggle()
                onToggle?.invoke(toggleState.value)
            }
        }
        Switch(toggleState.use()) {
            modifier
                .alignY(AlignmentY.Center)
                .onToggle {
                    toggleState.set(it)
                    onToggle?.invoke(toggleState.value)
                }
        }
    }
}

fun TextScope.sectionTitleStyle() {
    modifier
        .width(Grow.Std)
        .margin(vertical = UiSizes.hGap)    // hGap is intentional, since we want a little more spacing around titles
        .padding(vertical = sizes.smallGap)
        .textColor(colors.primary)
        .backgroundColor(colors.primaryVariant.withAlpha(0.2f))
        .font(sizes.largeText)
        .textAlignX(AlignmentX.Center)
}

fun TextScope.labelStyle(width: Dimension = FitContent) {
    modifier
        .width(width)
        .align(yAlignment = AlignmentY.Center)
        .padding(vertical = sizes.smallGap * 0.5f)
}