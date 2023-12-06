package de.fabmax.kool.util

import de.fabmax.kool.KoolContext

/**
 * @author fabmax
 */

sealed class Font(val sizePts: Float) {

    abstract val scale: Float
    abstract val lineHeight: Float

    abstract fun setScale(scale: Float, ctx: KoolContext)

    abstract fun textDimensions(
        text: String, result: TextMetrics = TextMetrics(), enforceSameWidthDigits: Boolean = true): TextMetrics
    abstract fun charWidth(char: Char, enforceSameWidthDigits: Boolean = true): Float
    abstract fun charHeight(char: Char): Float

    abstract fun derive(sizePts: Float): Font

    companion object {
        val DEFAULT_FONT: Font by lazy { MsdfFont.DEFAULT_FONT }
    }
}

class TextMetrics {
    var baselineWidth = 0f
    var height = 0f
    var yBaseline = 0f
    var numLines = 0
    var paddingStart = 0f
    var paddingEnd = 0f

    var ascentPx = 0f
    var descentPx = 0f

    val width: Float
        get() = baselineWidth + paddingStart + paddingEnd
}
