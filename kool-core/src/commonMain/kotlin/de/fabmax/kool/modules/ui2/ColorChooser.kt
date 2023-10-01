package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Color.Hsv
import kotlin.math.roundToInt

fun UiScope.ColorChooserH(
    hue: MutableStateValue<Float>,
    saturation: MutableStateValue<Float>,
    value: MutableStateValue<Float>,
    alpha: MutableStateValue<Float>? = null,
    hexString: MutableStateValue<String>? = null,
    scopeName: String? = null,
    onChange: ((Color) -> Unit)? = null
) {
    Row(Grow(1f, min = FitContent), scopeName = scopeName) {
        modifier.margin(sizes.gap)
        ColorWheel(hue.use(), saturation.use(), value.use()) {
            modifier
                .alignY(AlignmentY.Center)
                .margin(end = sizes.largeGap)
                .onChange { h, s, v ->
                    hue.set(h)
                    saturation.set(s)
                    value.set(v)
                    onChange?.invoke(Hsv(h, s, v).toSrgb(a = alpha?.value ?: 1f))
                }
        }
        ColorSliderPanel(hue, saturation, value, alpha, hexString, onChange)
    }
}

fun UiScope.ColorChooserV(
    hue: MutableStateValue<Float>,
    saturation: MutableStateValue<Float>,
    value: MutableStateValue<Float>,
    alpha: MutableStateValue<Float>? = null,
    hexString: MutableStateValue<String>? = null,
    scopeName: String? = null,
    onChange: ((Color) -> Unit)? = null
) {
    Column(Grow(1f, min = FitContent), scopeName = scopeName) {
        modifier.margin(sizes.gap)
        ColorWheel(hue.use(), saturation.use(), value.use()) {
            modifier
                .alignX(AlignmentX.Center)
                .margin(bottom = sizes.largeGap)
                .onChange { h, s, v ->
                    hue.set(h)
                    saturation.set(s)
                    value.set(v)
                    onChange?.invoke(Hsv(h, s, v).toSrgb(a = alpha?.value ?: 1f))
                }
        }
        ColorSliderPanel(hue, saturation, value, alpha, hexString, onChange)
    }
}

fun UiScope.ColorSliderPanel(
    hue: MutableStateValue<Float>,
    saturation: MutableStateValue<Float>,
    value: MutableStateValue<Float>,
    alpha: MutableStateValue<Float>?,
    hexString: MutableStateValue<String>?,
    onChange: ((Color) -> Unit)?
) {
    Column(Grow.Std, Grow.Std) {
        val color = Hsv(hue.use(), saturation.use(), value.use()).toSrgb(a = alpha?.use() ?: 1f)
        Box(Grow.Std, Grow(1f, min = 32.dp)) {
            modifier
                .backgroundColor(color)
                .margin(bottom = sizes.gap)
        }
        ColorSlider(hue, 0f, 360f, 1f, "H:") {
            onChange?.invoke(Hsv(it, saturation.value, value.value).toSrgb(a = alpha?.value ?: 1f))
        }
        ColorSlider(saturation, 0f, 1f, 100f, "S:") {
            onChange?.invoke(Hsv(hue.value, it, value.value).toSrgb(a = alpha?.value ?: 1f))
        }
        ColorSlider(value, 0f, 1f, 100f, "V:") {
            onChange?.invoke(Hsv(hue.value, saturation.value, it).toSrgb(a = alpha?.value ?: 1f))
        }
        alpha?.let {
            ColorSlider(it, 0f, 1f, 100f, "A:") { a ->
                onChange?.invoke(Hsv(hue.value, saturation.value, value.value).toSrgb(a = a))
            }
        }
        Row(width = Grow.Std) {
            Text("Hex:") {
                modifier
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
            }
            TextField(color.toHexString(alpha != null)) {
                if (isFocused.value && hexString != null) {
                    modifier.text(hexString.use())
                } else {
                    hexString?.set(color.toHexString())
                }
                modifier
                    .hint("hex code")
                    .width(sizes.largeGap * 4f)
                    .margin(start = sizes.gap, top = sizes.smallGap, bottom = sizes.smallGap)
                    .alignY(AlignmentY.Center)
                    .textAlignX(AlignmentX.End)
                    .maxLength(9)
                    .onChange { txt ->
                        hexString?.set(txt)
                        Color.fromHexOrNull(txt)?.let {
                            val hsv = it.toHsv()
                            hue.set(hsv.h)
                            saturation.set(hsv.s)
                            value.set(hsv.v)
                            alpha?.set(it.a)
                            onChange?.invoke(it)
                        }
                    }
                    .onEnterPressed { txt ->
                        val parsedColor = Color.fromHexOrNull(txt)
                        parsedColor?.let {
                            val hsv = it.toHsv()
                            hue.set(hsv.h)
                            saturation.set(hsv.s)
                            value.set(hsv.v)
                            alpha?.set(it.a)
                            hexString?.set(it.toHexString())
                            onChange?.invoke(it)
                        }
                    }
            }
        }
    }
}

private fun UiScope.ColorSlider(
    state: MutableStateValue<Float>,
    min: Float,
    max: Float,
    scale: Float,
    label: String,
    onChange: (Float) -> Unit
) = Row(width = Grow.Std) {
    Text(label) {
        modifier
            .width(sizes.gap * 2f)
            .margin(vertical = sizes.smallGap)
            .alignY(AlignmentY.Center)
    }
    Slider(state.value, min, max) {
        modifier
            .width(Grow.Std)
            .alignY(AlignmentY.Center)
            .onChange {
                state.set(it)
                onChange(it)
            }
    }
    TextField("${(state.value * scale).roundToInt()}") {
        modifier
            .width(sizes.gap * 3f)
            .margin(start = sizes.gap)
            .alignY(AlignmentY.Center)
            .textAlignX(AlignmentX.End)
            .onChange { txt ->
                txt.toIntOrNull()?.let {
                    state.set((it / scale).clamp(min, max))
                    onChange(state.value)
                }
            }
    }
}
