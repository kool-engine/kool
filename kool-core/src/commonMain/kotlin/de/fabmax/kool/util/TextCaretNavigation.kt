package de.fabmax.kool.util

import de.fabmax.kool.math.clamp

object TextCaretNavigation {
    private val LIMITING_CHARS = charArrayOf(' ', '(', '{', '[', '<', ')', '}', ']', '>', ',', '.')

    private fun Char.isLimitingChar() = this in LIMITING_CHARS

    fun startOfWord(text: String, caretPos: Int): Int {
        if (text.isEmpty()) return 0
        var i = caretPos.clamp(0, text.lastIndex)
        while (i > 0 && !text[i].isLimitingChar()) i--
        if (text[i].isLimitingChar()) i++
        return i
    }

    fun endOfWord(text: String, caretPos: Int): Int {
        if (text.isEmpty()) return 0
        var i = caretPos.clamp(0, text.lastIndex)
        while (i < text.length && !text[i].isLimitingChar()) i++
        return i
    }

    fun moveWordLeft(text: String, caretPos: Int): Int {
        var i = (caretPos - 1).clamp(0, text.lastIndex)
        return when {
            i == 0 -> 0
            !text[i].isLimitingChar() -> startOfWord(text, i)
            else -> {
                while (i > 0 && text[i].isLimitingChar()) i--
                startOfWord(text, i)
            }
        }
    }

    fun moveWordRight(text: String, caretPos: Int): Int {
        var i = (caretPos + 1).clamp(0, text.length)
        return when {
            i == text.length -> text.length
            !text[i].isLimitingChar() -> endOfWord(text, i)
            else -> {
                while (i < text.length && text[i].isLimitingChar()) i++
                endOfWord(text, i)
            }
        }
    }

}