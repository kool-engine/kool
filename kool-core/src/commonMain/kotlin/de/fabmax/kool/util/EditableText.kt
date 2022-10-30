package de.fabmax.kool.util

import de.fabmax.kool.math.clamp
import kotlin.math.max
import kotlin.math.min

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

    var maxLength = 100

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
        caretPosition = TextCaretNavigation.moveWordLeft(text, caretPosition)
    }

    private fun moveWordRight() {
        caretPosition = TextCaretNavigation.moveWordRight(text, caretPosition)
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

    fun cutSelection(): String? {
        return if (selectionStart != caretPosition) {
            val txt = copySelection()
            deleteSelection()
            txt
        } else {
            null
        }
    }

    fun copySelection(): String? {
        return if (selectionStart != caretPosition) {
            text.substring(min(selectionStart, caretPosition), max(selectionStart, caretPosition))
        } else {
            null
        }
    }

    fun replaceSelection(string: String) {
        val start = min(selectionStart, caretPosition)
        val end = max(selectionStart, caretPosition)

        val newText = text.substring(0, start) + string + text.substring(end)
        if (maxLength <= 0 || newText.length < text.length || newText.length <= maxLength) {
            text = newText
            caretPosition = min(selectionStart, caretPosition) + string.length
            selectionStart = caretPosition
        }
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