package de.fabmax.kool.rendertest

import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont
import kotlin.test.Test
import kotlin.test.assertEquals

class UiRenderTest {

    @Test
    fun buttonClick() = renderTest(480, 360) {
        var buttonNode: UiNode? = null
        var clickCount = 0

        addScene {
            setupUiScene(ClearColorFill(Scene.DEFAULT_CLEAR_COLOR))
            addPanelSurface(colors = Colors.singleColorLight(MdColor.LIGHT_GREEN)) {
                modifier
                    .size(400.dp, 300.dp)
                    .align(AlignmentX.Center, AlignmentY.Center)
                    .background(RoundRectBackground(colors.background, 16.dp))

                var clicks by remember(0)
                buttonNode = Button("Click me!") {
                    modifier
                        .alignX(AlignmentX.Center)
                        .margin(sizes.largeGap * 4f)
                        .padding(horizontal = sizes.largeGap, vertical = sizes.gap)
                        .font(sizes.largeText)
                        .onClick {
                            clicks++
                            clickCount++
                        }
                }.uiNode
                Text("Button clicked $clicks times") {
                    modifier.alignX(AlignmentX.Center)
                }
            }
        }
        // the default font texture is loaded asynchronously
        awaitTexturesLoaded(MsdfFont.DEFAULT_FONT_DATA.map)
        assertFrameMatchesReference("ui/button-initial")

        // click the button via injected input events (pointer coordinates are in framebuffer pixels)
        val button = checkNotNull(buttonNode)
        val x = button.leftPx + button.widthPx * 0.5f
        val y = button.topPx + button.heightPx * 0.5f
        input.mouseMove(x, y)
        renderFrame()
        input.mouseButton(PointerInput.LEFT_BUTTON, true)
        renderFrame()
        input.mouseButton(PointerInput.LEFT_BUTTON, false)
        renderFrame()
        // the click is reported by the pointer in the frame the button is released and handled by the UI in the next
        renderFrame()
        assertEquals(1, clickCount)

        // button is hovered and text shows the updated click count
        assertFrameMatchesReference("ui/button-clicked")
    }
}
