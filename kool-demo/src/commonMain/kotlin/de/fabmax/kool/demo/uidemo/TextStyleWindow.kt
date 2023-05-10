package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont

class TextStyleWindow(uiDemo: UiDemo) : DemoWindow("MSDF Text Style", uiDemo) {

    init {
        windowDockable.setFloatingBounds(width = Dp(1200f), height = Dp(800f))
    }

    override fun UiScope.windowContent() = Column(Grow.Std, Grow.Std) {
        var displayText by remember("Hello kool UI!")
        val fontSize = remember(150f)
        val rotation = remember(0f)
        val glow = remember(0f)
        val fontWeight = remember(0f)
        val fontCutoff = remember(0.5f)
        val fontItalic = remember(0f)

        modifier.padding(sizes.gap)

        Row {
            modifier.height(sizes.largeGap * 1.8f)
            Text("Display text") {
                modifier
                    .alignY(AlignmentY.Center)
                    .width(sizes.largeGap * 8f)
            }
            TextField(displayText) {
                modifier
                    .alignY(AlignmentY.Center)
                    .onChange { displayText = it }
                    .width(sizes.largeGap * 12f)
            }
        }

        labeledSlider("Font size", fontSize, 8f, 500f, 0)
        labeledSlider("Weight", fontWeight, -0.2f, 0.3f, 2)
        labeledSlider("Italic", fontItalic, -0.5f, 0.5f, 2)
        labeledSlider("Cutoff / outline", fontCutoff, 0f, 0.5f, 2)
        labeledSlider("Glow", glow, 0f, 1f, 2)
        labeledSlider("Rotation", rotation, 0f, 360f, 0)

        ScrollArea(
            containerModifier = {
                it
                    .margin(top = sizes.gap)
                    .backgroundColor(MdColor.GREY tone 900)
            }
        ) {
            modifier
                .width(Grow(1f, min = FitContent))
                .height(Grow(1f, min = FitContent))

            val font = MsdfFont(
                sizePts = fontSize.value,
                italic = fontItalic.value,
                weight = fontWeight.value,
                cutoff = fontCutoff.value,
                glowColor = colors.secondary.withAlpha(glow.value)
            )
            Text(displayText) {
                modifier
                    .size(Grow.Std, Grow.Std)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
                    .textColor(colors.primary)
                    .font(font)
                    .textRotation(rotation.value)
            }
        }
    }

    fun UiScope.labeledSlider(label: String, state: MutableStateValue<Float>, min: Float, max: Float, precision: Int) = Row {
        modifier.height(sizes.largeGap * 1.8f)
        Text(label) {
            modifier
                .alignY(AlignmentY.Center)
                .width(sizes.largeGap * 8f)
        }
        Slider(state.use(), min, max) {
            modifier
                .alignY(AlignmentY.Center)
                .onChange { state.set(it) }
                .width(sizes.largeGap * 10f)
        }
        Text(state.value.toString(precision)) {
            modifier
                .alignY(AlignmentY.Center)
                .width(sizes.largeGap * 2f)
                .textAlignX(AlignmentX.End)
        }
    }
}