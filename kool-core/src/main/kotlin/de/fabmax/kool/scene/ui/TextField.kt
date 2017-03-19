package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.platform.RenderContext

/**
 * @author fabmax
 */

class TextField(name: String, root: UiRoot) : Label(name, root) {

    init {
        onRender += { ctx ->
            if (!ctx.inputMgr.typedChars.isEmpty()) {
                for (c in ctx.inputMgr.typedChars) {
                    text += c
                }
            }
            if (!ctx.inputMgr.keyEvents.isEmpty() && !text.isEmpty()) {
                for (e in ctx.inputMgr.keyEvents) {
                    if (e.keyCode == InputManager.KEY_BACKSPACE && e.isPressed) {
                        text = text.substring(0, text.length - 1)
                    }
                }
            }
        }
    }

    override fun createThemeUi(ctx: RenderContext): ComponentUi {
        return root.theme.textFieldUi(this)
    }
}

open class TextFieldUi(textField: TextField, baseUi: ComponentUi) : LabelUi(textField, baseUi) {

    override fun renderText(ctx: RenderContext) {
        meshBuilder.color = label.root.theme.accentColor
        val x1 = label.padding.left.toUnits(label.width, label.dpi)
        val x2 = label.width - label.padding.right.toUnits(label.width, label.dpi)
        val y = textBaseline - label.font.apply().fontProps.sizePts * 0.2f

        meshBuilder.withTransform {
            translate(0f, 0f, label.dp(4f))
            meshBuilder.line(x1, y, x2, y, label.dp(1.5f))
        }

        super.renderText(ctx)
    }
}
