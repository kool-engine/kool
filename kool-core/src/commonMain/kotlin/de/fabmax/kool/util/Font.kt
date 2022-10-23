package de.fabmax.kool.util

import de.fabmax.kool.KoolContext

/**
 * @author fabmax
 */

sealed class Font(val sizePts: Float, val style: Int) {

    abstract val scale: Float
    abstract val lineHeight: Float

    abstract fun setScale(scale: Float, ctx: KoolContext)

    abstract fun textDimensions(text: String, result: TextMetrics = TextMetrics()): TextMetrics
    abstract fun charWidth(char: Char): Float
    abstract fun charHeight(char: Char): Float

    abstract fun derive(sizePts: Float): Font

    companion object {
        const val PLAIN = 0
        const val BOLD = 1
        const val ITALIC = 2

        val DEFAULT_FONT: Font get() = MsdfFont.DEFAULT_FONT
    }
}

class TextMetrics {
    var width = 0f
    var height = 0f
    var yBaseline = 0f
    var numLines = 0

    var ascentPx = 0f
    var descentPx = 0f
}
