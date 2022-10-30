package de.fabmax.kool.util

import de.fabmax.kool.math.clamp

object TextCaretNavigation {

    fun startOfWord(text: String, caretPos: Int): Int {
        var i = caretPos.clamp(0, text.lastIndex)
        while (i > 0 && !text[i].isWhitespace()) i--
        if (text[i].isWhitespace()) i++
        return i
    }

    fun endOfWord(text: String, caretPos: Int): Int {
        var i = caretPos.clamp(0, text.lastIndex)
        while (i < text.length && !text[i].isWhitespace()) i++
        return i
    }

    fun moveWordLeft(text: String, caretPos: Int): Int {
        var i = (caretPos - 1).clamp(0, text.lastIndex)
        return when {
            i == 0 -> 0
            !text[i].isWhitespace() -> startOfWord(text, i)
            else -> {
                while (i > 0 && text[i].isWhitespace()) i--
                startOfWord(text, i)
            }
        }
    }

    fun moveWordRight(text: String, caretPos: Int): Int {
        var i = (caretPos + 1).clamp(0, text.length)
        return when {
            i == text.length -> text.length
            !text[i].isWhitespace() -> endOfWord(text, i)
            else -> {
                while (i < text.length && text[i].isWhitespace()) i++
                endOfWord(text, i)
            }
        }
    }

}