package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont

class TextStyleWindow(val uiDemo: UiDemo) : UiDemo.DemoWindow {

    private val windowState = WindowState().apply { setWindowSize(Dp(1200f), Dp(800f)) }

    override val windowSurface: UiSurface = Window(windowState, name = "MSDF Text Style") {
        surface.sizes = uiDemo.selectedUiSize.use()
        surface.colors = uiDemo.selectedColors.use()

        TitleBar(onCloseAction = { uiDemo.closeWindow(this@TextStyleWindow, it.ctx) })
        WindowContent()
    }
    override val windowScope: WindowScope = windowSurface.windowScope!!

    fun UiScope.WindowContent() = Column(Grow.Std, Grow.Std) {
        val displayText = weakRememberState("Hello kool UI!")
        val fontSize = weakRememberState(150f)
        val rotation = weakRememberState(0f)
        val glow = weakRememberState(0f)
        val fontWeight = weakRememberState(0f)
        val fontCutoff = weakRememberState(0.5f)
        val fontItalic = weakRememberState(0f)

        modifier.padding(sizes.gap)

        Row {
            modifier.height(sizes.largeGap * 1.8f)
            Text("Display text") {
                modifier
                    .alignY(AlignmentY.Center)
                    .width(sizes.largeGap * 8f)
            }
            TextField(displayText.use()) {
                modifier
                    .alignY(AlignmentY.Center)
                    .onChange { displayText.set(it) }
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
            Text(displayText.use()) {
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