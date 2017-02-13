package de.fabmax.kool.util

import de.fabmax.kool.Texture2d
import de.fabmax.kool.platform.Platform

/**
 * @author fabmax
 */

class Font(val family: String, val size: Float, val style: Int = Font.PLAIN) {

    companion object {
        val PLAIN = 0
        val BOLD = 1
        val ITALIC = 2

        // todo: For now char maps are created with a hardcoded set of characters (ASCII + a few german ones)
        // todo: theoretically arbitrary unicode characters are supported
        private val STD_CHARS: String
        init {
            var str = ""
            for (i in 32..126) {
                str += i.toChar()
            }
            str += "äÄöÖüÜß"
            STD_CHARS = str
        }
    }

    val charMap: CharMap = Platform.createCharMap(this, STD_CHARS)

}

class CharMetrics {
    var width = 0f
    var height = 0f
    var xOffset = 0f
    var yBaseline = 0f
    var advance = 0f

    val uvMin = MutableVec2f()
    val uvMax = MutableVec2f()
}

class CharMap(val fontTexture: Texture2d, private val map: Map<Char, CharMetrics>) : Map<Char, CharMetrics> by map
