package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @author fabmax
 */

class TextField(name: String, root: UiRoot) : Label(name, root) {

    val editText = EditableText()

    init {
        onPreRender += { ctx ->
            if (!ctx.inputMgr.keyEvents.isEmpty()) {
                for (e in ctx.inputMgr.keyEvents) {
                    if (e.isCharTyped) {
                        editText.charTyped(e.typedChar)

                    } else if (e.isPressed) {
                        when (e.keyCode) {
                            InputManager.KEY_BACKSPACE -> editText.backspace()
                            InputManager.KEY_DEL -> editText.deleteSelection()
                            InputManager.KEY_CURSOR_LEFT -> {
                                if (e.isCtrlDown) {
                                    editText.moveCaret(EditableText.MOVE_WORD_LEFT, e.isShiftDown)
                                } else {
                                    editText.moveCaret(EditableText.MOVE_LEFT, e.isShiftDown)
                                }
                            }
                            InputManager.KEY_CURSOR_RIGHT -> {
                                if (e.isCtrlDown) {
                                    editText.moveCaret(EditableText.MOVE_WORD_RIGHT, e.isShiftDown)
                                } else {
                                    editText.moveCaret(EditableText.MOVE_RIGHT, e.isShiftDown)
                                }
                            }
                            InputManager.KEY_HOME -> editText.moveCaret(EditableText.MOVE_START, e.isShiftDown)
                            InputManager.KEY_END -> editText.moveCaret(EditableText.MOVE_END, e.isShiftDown)
                        }
                    }
                }
                text = editText.toString()
            }
        }
    }

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.newTextFieldUi(this)
    }
}

open class TextFieldUi(val textField: TextField, baseUi: ComponentUi) : LabelUi(textField, baseUi) {

    private val caretAlphaAnimator = CosAnimator(InterpolatedFloat(0f, 1f))
    private val caretColor = MutableColor()

    private val caretDrawPos = InterpolatedFloat(0f, 0f)
    private val caretPosAnimator = LinearAnimator(caretDrawPos)

    init {
        caretAlphaAnimator.duration = 0.5f
        caretAlphaAnimator.repeating = Animator.REPEAT_TOGGLE_DIR

        caretPosAnimator.duration = 0.1f
    }

    override fun onRender(ctx: KoolContext) {
        textField.requestUiUpdate()
        super.onRender(ctx)
    }

    override fun renderText(ctx: KoolContext) {
        val x1 = label.padding.left.toUnits(label.width, label.dpi)
        val x2 = label.width - label.padding.right.toUnits(label.width, label.dpi)
        val y = textBaseline - (font?.fontProps?.sizeUnits ?: 0f) * 0.2f

        var caretX = textStartX
        var selectionX = textStartX
        if (textField.editText.caretPosition > 0 || textField.editText.selectionStart > 0) {
            for (i in 0..(max(textField.editText.caretPosition, textField.editText.selectionStart) - 1)) {
                val w = font?.charWidth(textField.editText[i]) ?: 0f
                if (i < textField.editText.caretPosition) {
                    caretX += w
                }
                if (i < textField.editText.selectionStart) {
                    selectionX += w
                }
            }
        }
        if (caretX != caretDrawPos.to) {
            caretDrawPos.from = caretDrawPos.value
            caretDrawPos.to = caretX
            caretPosAnimator.progress = 0f
            caretPosAnimator.speed = 1f
        }
        caretX = caretPosAnimator.tick(ctx)

        meshBuilder.withTransform {
            // elevate z
            translate(0f, 0f, label.dp(4f))

            // draw underline
            meshBuilder.color = label.root.theme.accentColor
            meshBuilder.line(x1, y, x2, y, label.dp(1.5f))

            // draw selection
            if (textField.editText.selectionStart != textField.editText.caretPosition) {
                caretColor.set(label.root.theme.accentColor)
                caretColor.a = 0.4f
                meshBuilder.color = caretColor
                meshBuilder.rect {
                    origin.set(caretX, y, 0f)
                    size.set(selectionX - caretX, (font?.fontProps?.sizeUnits ?: 0f) * 1.2f)
                }
            }

            // draw caret
            caretColor.set(label.root.theme.accentColor)
            caretColor.a = caretAlphaAnimator.tick(ctx)
            meshBuilder.color = caretColor
            meshBuilder.line(caretX, y, caretX, textBaseline + (font?.fontProps?.sizeUnits ?: 0f), label.dp(1.5f))
        }

        super.renderText(ctx)
    }
}

class EditableText(txt: String = "") {

    var text: String = txt
        set(value) {
            if (caretPosition > value.length) {
                caretPosition = value.length
            }
            if (selectionStart > value.length) {
                selectionStart = value.length
            }
            field = value
        }

    var caretPosition = 0
        set(value) {
            field = value.clamp(0, text.length)
        }

    var selectionStart = 0
        set(value) {
            field = value.clamp(0, text.length)
        }

    fun charTyped(c: Char) {
        replaceSelection("$c")
    }

    fun moveCaret(mode: Int, selection: Boolean) {
        when (mode) {
            MOVE_LEFT -> caretPosition--
            MOVE_RIGHT -> caretPosition++
            MOVE_START -> caretPosition = 0
            MOVE_END -> caretPosition = text.length
            MOVE_WORD_LEFT -> moveWordLeft()
            MOVE_WORD_RIGHT -> moveWordRight()
        }
        if (!selection) {
            selectionStart = caretPosition
        }
    }

    private fun moveWordLeft() {
        if (caretPosition > 0) {
            val idx = text.substring(0, caretPosition).lastIndexOf(' ')
            if (idx < 0) {
                caretPosition = 0
            } else {
                caretPosition = idx
            }
        }
    }

    private fun moveWordRight() {
        if (caretPosition < text.length) {
            val idx = text.indexOf(' ', caretPosition)
            if (idx < 0) {
                caretPosition = text.length
            } else {
                caretPosition = idx + 1
            }
        }
    }

    fun backspace() {
        if (selectionStart != caretPosition) {
            replaceSelection("")
        } else if (caretPosition > 0) {
            selectionStart = --caretPosition
            text = text.substring(0, caretPosition) + text.substring(caretPosition + 1)
        }
    }

    fun deleteSelection() {
        if (selectionStart != caretPosition) {
            replaceSelection("")
        } else if (caretPosition < text.length) {
            text = text.substring(0, caretPosition) + text.substring(caretPosition + 1)
        }
    }

    fun replaceSelection(string: String) {
        val start = min(selectionStart, caretPosition)
        val end = max(selectionStart, caretPosition)

        text = text.substring(0, start) + string + text.substring(end)

        caretPosition = min(selectionStart, caretPosition) + string.length
        selectionStart = caretPosition
    }

    operator fun get(index: Int): Char {
        return text[index]
    }

    override fun toString(): String {
        return text
    }

    companion object {
        const val MOVE_LEFT = 1
        const val MOVE_RIGHT = 2
        const val MOVE_WORD_LEFT = 3
        const val MOVE_WORD_RIGHT = 4
        const val MOVE_START = 5
        const val MOVE_END = 6
    }
}
