package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Color.Hsv
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MdColor
import kotlin.math.roundToInt

class ThemeEditorWindow(uiDemo: UiDemo) : DemoWindow("Theme Editor", uiDemo) {

    private val presetsDark = listOf(
        "Yellow" to Colors.singleColorDark(MdColor.YELLOW),
        "Amber" to Colors.singleColorDark(MdColor.AMBER),
        "Orange" to Colors.singleColorDark(MdColor.ORANGE),
        "Deep orange" to Colors.singleColorDark(MdColor.DEEP_ORANGE),
        "Red" to Colors.singleColorDark(MdColor.RED),
        "Pink" to Colors.singleColorDark(MdColor.PINK),
        "Purple" to Colors.singleColorDark(MdColor.PURPLE),
        "Deep purple" to Colors.singleColorDark(MdColor.DEEP_PURPLE),
        "Indigo" to Colors.singleColorDark(MdColor.INDIGO),
        "Blue" to Colors.singleColorDark(MdColor.BLUE),
        "Light blue" to Colors.singleColorDark(MdColor.LIGHT_BLUE),
        "Cyan" to Colors.singleColorDark(MdColor.CYAN),
        "Teal" to Colors.singleColorDark(MdColor.TEAL),
        "Green" to Colors.singleColorDark(MdColor.GREEN),
        "Light green" to Colors.singleColorDark(MdColor.LIGHT_GREEN),
        "Lime" to Colors.singleColorDark(MdColor.LIME),
        "Colorful" to Colors.darkColors(),
        "Neon" to Colors.neon
    )

    private val presetsLight = listOf(
        "Yellow" to Colors.singleColorLight(MdColor.YELLOW),
        "Amber" to Colors.singleColorLight(MdColor.AMBER),
        "Orange" to Colors.singleColorLight(MdColor.ORANGE),
        "Deep orange" to Colors.singleColorLight(MdColor.DEEP_ORANGE),
        "Red" to Colors.singleColorLight(MdColor.RED),
        "Pink" to Colors.singleColorLight(MdColor.PINK),
        "Purple" to Colors.singleColorLight(MdColor.PURPLE),
        "Deep purple" to Colors.singleColorLight(MdColor.DEEP_PURPLE),
        "Indigo" to Colors.singleColorLight(MdColor.INDIGO),
        "Blue" to Colors.singleColorLight(MdColor.BLUE),
        "Light blue" to Colors.singleColorLight(MdColor.LIGHT_BLUE),
        "Cyan" to Colors.singleColorLight(MdColor.CYAN),
        "Teal" to Colors.singleColorLight(MdColor.TEAL),
        "Green" to Colors.singleColorLight(MdColor.GREEN),
        "Light green" to Colors.singleColorLight(MdColor.LIGHT_GREEN),
        "Lime" to Colors.singleColorLight(MdColor.LIME)
    )

    private val colorEntries = listOf(
        ColorEntry("Primary", uiDemo.selectedColors.value.primary),
        ColorEntry("Primary variant", uiDemo.selectedColors.value.primaryVariant),
        ColorEntry("Secondary", uiDemo.selectedColors.value.secondary),
        ColorEntry("Secondary variant", uiDemo.selectedColors.value.secondaryVariant),
        ColorEntry("Background", uiDemo.selectedColors.value.background),
        ColorEntry("Background variant", uiDemo.selectedColors.value.backgroundVariant),
        ColorEntry("On primary", uiDemo.selectedColors.value.onPrimary),
        ColorEntry("On secondary", uiDemo.selectedColors.value.onSecondary),
        ColorEntry("On background", uiDemo.selectedColors.value.onBackground)
    )

    private val isDarkPresets = mutableStateOf(true)
    private val selectedPreset = mutableStateOf(16)
    private val selectedColor = mutableStateOf(0)

    init {
        windowDockable.setFloatingBounds(width = Dp(500f), height = Dp(700f))
    }

    override fun UiScope.windowContent() = Column(Grow.Std, Grow.Std) {
        uiDemo.selectedColors.set(makeColors())
        val entry = colorEntries[selectedColor.use()]

        Column(Grow.Std, Grow.Std) {
            Text("${entry.name} color") {
                modifier
                    .margin(sizes.gap)
                    .font(sizes.largeText)
            }
            ColorChooserH(entry.hue, entry.sat, entry.value, entry.alpha, entry.hexString)

            Row {
                modifier.margin(start = sizes.gap, end = sizes.gap, top = sizes.largeGap, bottom = sizes.gap)
                Text("Preset:") { modifier.width(sizes.largeGap * 3f).alignY(AlignmentY.Center) }
                ComboBox {
                    modifier
                        .width(150.dp)
                        .alignY(AlignmentY.Center)
                        .margin(horizontal = sizes.largeGap)
                        .items((if (isDarkPresets.use()) presetsDark else presetsLight).map { it.first })
                        .selectedIndex(selectedPreset.use())
                        .onItemSelected {
                            selectedPreset.set(it)
                            applyPreset()
                        }
                }
                RadioButton(isDarkPresets.use()) {
                    modifier
                        .margin(sizes.gap)
                        .alignY(AlignmentY.Center)
                        .onClick {
                            isDarkPresets.set(true)
                            applyPreset()
                        }
                }
                Text("Dark") {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(end = sizes.largeGap)
                        .onClick {
                            isDarkPresets.set(true)
                            applyPreset()
                        }
                }
                RadioButton(!isDarkPresets.use()) {
                    modifier
                        .margin(sizes.gap)
                        .alignY(AlignmentY.Center)
                        .onClick {
                            isDarkPresets.set(false)
                            applyPreset()
                        }
                }
                Text("Light") {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(end = sizes.largeGap)
                        .onClick {
                            isDarkPresets.set(false)
                            applyPreset()
                        }
                }
            }

            Row {
                modifier.margin(start = sizes.gap, end = sizes.gap, top = sizes.gap, bottom = sizes.largeGap)
                Text("Size:") { modifier.width(sizes.largeGap * 3f).alignY(AlignmentY.Center) }

                RadioButton(uiDemo.selectedUiSize.use() == Sizes.small) {
                    modifier
                        .margin(start = sizes.largeGap, end = sizes.gap)
                        .alignY(AlignmentY.Center)
                        .onClick {uiDemo.selectedUiSize.set(Sizes.small) }
                }
                Text("Small") {
                    modifier
                        .alignY(AlignmentY.Center)
                        .onClick { uiDemo.selectedUiSize.set(Sizes.small) }
                }

                RadioButton(uiDemo.selectedUiSize.use() == Sizes.medium) {
                    modifier
                        .margin(start = sizes.largeGap, end = sizes.gap)
                        .alignY(AlignmentY.Center)
                        .onClick {uiDemo.selectedUiSize.set(Sizes.medium) }
                }
                Text("Medium") {
                    modifier
                        .alignY(AlignmentY.Center)
                        .onClick {uiDemo.selectedUiSize.set(Sizes.medium) }
                }

                RadioButton(uiDemo.selectedUiSize.use() == Sizes.large) {
                    modifier
                        .margin(start = sizes.largeGap, end = sizes.gap)
                        .alignY(AlignmentY.Center)
                        .onClick {uiDemo.selectedUiSize.set(Sizes.large) }
                }
                Text("Large") {
                    modifier
                        .alignY(AlignmentY.Center)
                        .onClick {uiDemo.selectedUiSize.set(Sizes.large) }
                }
            }

            LazyList(
                containerModifier = {
                    it
                        .margin(sizes.gap)
                        .size(Grow.Std, Grow.Std)
                }
            ) {
                var hoveredItem by remember(-1)
                itemsIndexed(colorEntries) { i, it ->
                    it.apply {
                        itemRow(i, i == hoveredItem).apply {
                            modifier.onEnter { hoveredItem = i }
                            modifier.onExit { hoveredItem = -1 }
                            modifier.onClick { selectedColor.set(i) }
                        }
                    }
                }
            }
        }
    }

    init {
        applyPreset()
    }

    private fun applyPreset() {
        val presets = if (isDarkPresets.value) {
            presetsDark
        } else {
            presetsLight
        }
        if (selectedPreset.value !in presets.indices) {
            selectedPreset.set(selectedPreset.value.clamp(0, presets.lastIndex))
        }
        val preset = presets[selectedPreset.value].second

        colorEntries[0].setColor(preset.primary)
        colorEntries[1].setColor(preset.primaryVariant)
        colorEntries[2].setColor(preset.secondary)
        colorEntries[3].setColor(preset.secondaryVariant)
        colorEntries[4].setColor(preset.background)
        colorEntries[5].setColor(preset.backgroundVariant)
        colorEntries[6].setColor(preset.onPrimary)
        colorEntries[7].setColor(preset.onSecondary)
        colorEntries[8].setColor(preset.onBackground)
    }

    private fun makeColors() = Colors(
        primary = colorEntries[0].color,
        primaryVariant = colorEntries[1].color,
        secondary = colorEntries[2].color,
        secondaryVariant = colorEntries[3].color,
        background = colorEntries[4].color,
        backgroundVariant = colorEntries[5].color,
        onPrimary = colorEntries[6].color,
        onSecondary = colorEntries[7].color,
        onBackground = colorEntries[8].color,
        isLight = colorEntries[4].color.brightness > 0.5f
    )

    private inner class ColorEntry(val name: String, initColor: Color) {
        val hue = mutableStateOf(0f)
        val sat = mutableStateOf(1f)
        val value = mutableStateOf(1f)
        val alpha = mutableStateOf(1f)
        val hexString = mutableStateOf("")

        val color: Color get() = Hsv(hue.value, sat.value, value.value).toSrgb(a = alpha.value)

        init {
            setColor(initColor)
        }

        fun setColor(color: Color) {
            val hsv = color.toHsv()
            hue.set(hsv.h)
            sat.set(hsv.s)
            value.set(hsv.v)
            alpha.set(color.a)
        }

        fun UiScope.itemRow(index: Int, isHovered: Boolean) = Row(Grow.Std) {
            if (isHovered) {
                modifier.backgroundColor(colors.secondaryAlpha(0.5f))
            } else if (index == selectedColor.use()) {
                modifier.backgroundColor(colors.secondaryAlpha(0.3f))
            } else if (index % 2 == 0) {
                val bg = if (colors.isLight) Color.BLACK.withAlpha(0.05f) else colors.secondaryAlpha(0.05f)
                modifier.backgroundColor(bg.withAlpha(0.05f))
            }

            val color = Hsv(hue.use(), sat.use(), value.use()).toSrgb(a = alpha.use())
            Box(80.dp, 64.dp) {
                modifier
                    .backgroundColor(color)
                    .border(RectBorder(if (colors.isLight) Color.BLACK else Color.WHITE, 1.dp))
                    .margin(sizes.smallGap)
            }
            Column {
                modifier
                    .margin(start = sizes.largeGap)
                    .alignY(AlignmentY.Center)
                Text(name) {  }
                Row {
                    Text("#${color.toHexString()}") {
                        modifier.width(80.dp).font(Font.DEFAULT_FONT)
                    }
                    Text("HSVA:") {
                        modifier.width(34.dp).font(Font.DEFAULT_FONT)
                    }
                    Text("${hue.value.roundToInt()}") {
                        modifier.width(28.dp).font(Font.DEFAULT_FONT).textAlignX(AlignmentX.End)
                    }
                    Text("${(sat.value * 100f).roundToInt()}") {
                        modifier.width(28.dp).font(Font.DEFAULT_FONT).textAlignX(AlignmentX.End)
                    }
                    Text("${(value.value * 100f).roundToInt()}") {
                        modifier.width(28.dp).font(Font.DEFAULT_FONT).textAlignX(AlignmentX.End)
                    }
                    Text("${(alpha.value * 100f).roundToInt()}") {
                        modifier.width(28.dp).font(Font.DEFAULT_FONT).textAlignX(AlignmentX.End)
                    }
                }
            }
        }
    }
}