package de.fabmax.kool.util

import de.fabmax.kool.math.Vec4f
import kotlin.math.pow

/**
 * @author fabmax
 */

@Suppress("unused")
open class Color(r: Float, g: Float, b: Float, a: Float = 1f) : Vec4f(r, g, b, a) {

    open val r get() = this[0]
    open val g get() = this[1]
    open val b get() = this[2]
    open val a get() = this[3]

    val brightness: Float
        get() = 0.299f * r + 0.587f * g + 0.114f * b

    fun mix(other: Color, weight: Float): MutableColor {
        return mix(other, weight, MutableColor())
    }

    fun mix(other: Color, weight: Float, result: MutableColor): MutableColor {
        result.r = other.r * weight + r * (1f - weight)
        result.g = other.g * weight + g * (1f - weight)
        result.b = other.b * weight + b * (1f - weight)
        result.a = other.a * weight + a * (1f - weight)
        return result
    }

    fun withAlpha(alpha: Float): MutableColor {
        return MutableColor(r, g, b, alpha)
    }

    fun toLinear(): MutableColor = gamma(2.2f)

    fun toLinear(result: MutableColor): MutableColor = gamma(2.2f, result)

    fun toSrgb(): MutableColor = gamma(1f / 2.2f)

    fun toSrgb(result: MutableColor): MutableColor = gamma(1f / 2.2f, result)

    fun gamma(gamma: Float): MutableColor {
        return gamma(gamma, MutableColor())
    }

    fun gamma(gamma: Float, result: MutableColor): MutableColor {
        return result.set(r.pow(gamma), g.pow(gamma), b.pow(gamma), a)
    }

    companion object {
        val BLACK = Color(0.00f, 0.00f, 0.00f, 1.00f)
        val DARK_GRAY = Color(0.25f, 0.25f, 0.25f, 1.00f)
        val GRAY = Color(0.50f, 0.50f, 0.50f, 1.00f)
        val LIGHT_GRAY = Color(0.75f, 0.75f, 0.75f, 1.00f)
        val WHITE = Color(1.00f, 1.00f, 1.00f, 1.00f)

        val RED = Color(1.0f, 0.0f, 0.0f, 1.0f)
        val GREEN = Color(0.0f, 1.0f, 0.0f, 1.0f)
        val BLUE = Color(0.0f, 0.0f, 1.0f, 1.0f)
        val YELLOW = Color(1.0f, 1.0f, 0.0f, 1.0f)
        val CYAN = Color(0.0f, 1.0f, 1.0f, 1.0f)
        val MAGENTA = Color(1.0f, 0.0f, 1.0f, 1.0f)
        val ORANGE = Color(1.0f, 0.5f, 0.0f, 1.0f)
        val LIME = Color(0.7f, 1.0f, 0.0f, 1.0f)

        val LIGHT_RED = Color(1.0f, 0.5f, 0.5f, 1.0f)
        val LIGHT_GREEN = Color(0.5f, 1.0f, 0.5f, 1.0f)
        val LIGHT_BLUE = Color(0.5f, 0.5f, 1.0f, 1.0f)
        val LIGHT_YELLOW = Color(1.0f, 1.0f, 0.5f, 1.0f)
        val LIGHT_CYAN = Color(0.5f, 1.0f, 1.0f, 1.0f)
        val LIGHT_MAGENTA = Color(1.0f, 0.5f, 1.0f, 1.0f)
        val LIGHT_ORANGE = Color(1.0f, 0.75f, 0.5f, 1.0f)

        val DARK_RED = Color(0.5f, 0.0f, 0.0f, 1.0f)
        val DARK_GREEN = Color(0.0f, 0.5f, 0.0f, 1.0f)
        val DARK_BLUE = Color(0.0f, 0.0f, 0.5f, 1.0f)
        val DARK_YELLOW = Color(0.5f, 0.5f, 0.0f, 1.0f)
        val DARK_CYAN = Color(0.0f, 0.5f, 0.5f, 1.0f)
        val DARK_MAGENTA = Color(0.5f, 0.0f, 0.5f, 1.0f)
        val DARK_ORANGE = Color(0.5f, 0.25f, 0.0f, 1.0f)

        //
        // Material Design Colors
        // https://material.io/guidelines/style/color.html#color-color-palette
        //
        val MD_RED_50 = fromHex("FFEBEE")
        val MD_RED_100 = fromHex("FFCDD2")
        val MD_RED_200 = fromHex("EF9A9A")
        val MD_RED_300 = fromHex("E57373")
        val MD_RED_400 = fromHex("EF5350")
        val MD_RED_500 = fromHex("F44336")
        val MD_RED_600 = fromHex("E53935")
        val MD_RED_700 = fromHex("D32F2F")
        val MD_RED_800 = fromHex("C62828")
        val MD_RED_900 = fromHex("B71C1C")
        val MD_RED_A100 = fromHex("FF8A80")
        val MD_RED_A200 = fromHex("FF5252")
        val MD_RED_A400 = fromHex("FF1744")
        val MD_RED_A700 = fromHex("D50000")
        val MD_RED = MD_RED_500

        val MD_PINK_50 = fromHex("FCE4EC")
        val MD_PINK_100 = fromHex("F8BBD0")
        val MD_PINK_200 = fromHex("F48FB1")
        val MD_PINK_300 = fromHex("F06292")
        val MD_PINK_400 = fromHex("EC407A")
        val MD_PINK_500 = fromHex("E91E63")
        val MD_PINK_600 = fromHex("D81B60")
        val MD_PINK_700 = fromHex("C2185B")
        val MD_PINK_800 = fromHex("AD1457")
        val MD_PINK_900 = fromHex("880E4F")
        val MD_PINK_A100 = fromHex("FF80AB")
        val MD_PINK_A200 = fromHex("FF4081")
        val MD_PINK_A400 = fromHex("F50057")
        val MD_PINK_A700 = fromHex("C51162")
        val MD_PINK = MD_PINK_500

        val MD_PURPLE_50 = fromHex("F3E5F5")
        val MD_PURPLE_100 = fromHex("E1BEE7")
        val MD_PURPLE_200 = fromHex("CE93D8")
        val MD_PURPLE_300 = fromHex("BA68C8")
        val MD_PURPLE_400 = fromHex("AB47BC")
        val MD_PURPLE_500 = fromHex("9C27B0")
        val MD_PURPLE_600 = fromHex("8E24AA")
        val MD_PURPLE_700 = fromHex("7B1FA2")
        val MD_PURPLE_800 = fromHex("6A1B9A")
        val MD_PURPLE_900 = fromHex("4A148C")
        val MD_PURPLE_A100 = fromHex("EA80FC")
        val MD_PURPLE_A200 = fromHex("E040FB")
        val MD_PURPLE_A400 = fromHex("D500F9")
        val MD_PURPLE_A700 = fromHex("AA00FF")
        val MD_PURPLE = MD_PURPLE_500

        val MD_DEEP_PURPLE_50 = fromHex("EDE7F6")
        val MD_DEEP_PURPLE_100 = fromHex("D1C4E9")
        val MD_DEEP_PURPLE_200 = fromHex("B39DDB")
        val MD_DEEP_PURPLE_300 = fromHex("9575CD")
        val MD_DEEP_PURPLE_400 = fromHex("7E57C2")
        val MD_DEEP_PURPLE_500 = fromHex("673AB7")
        val MD_DEEP_PURPLE_600 = fromHex("5E35B1")
        val MD_DEEP_PURPLE_700 = fromHex("512DA8")
        val MD_DEEP_PURPLE_800 = fromHex("4527A0")
        val MD_DEEP_PURPLE_900 = fromHex("311B92")
        val MD_DEEP_PURPLE_A100 = fromHex("B388FF")
        val MD_DEEP_PURPLE_A200 = fromHex("7C4DFF")
        val MD_DEEP_PURPLE_A400 = fromHex("651FFF")
        val MD_DEEP_PURPLE_A700 = fromHex("6200EA")
        val MD_DEEP_PURPLE = MD_DEEP_PURPLE_500

        val MD_INDIGO_50 = fromHex("E8EAF6")
        val MD_INDIGO_100 = fromHex("C5CAE9")
        val MD_INDIGO_200 = fromHex("9FA8DA")
        val MD_INDIGO_300 = fromHex("7986CB")
        val MD_INDIGO_400 = fromHex("5C6BC0")
        val MD_INDIGO_500 = fromHex("3F51B5")
        val MD_INDIGO_600 = fromHex("3949AB")
        val MD_INDIGO_700 = fromHex("303F9F")
        val MD_INDIGO_800 = fromHex("283593")
        val MD_INDIGO_900 = fromHex("1A237E")
        val MD_INDIGO_A100 = fromHex("8C9EFF")
        val MD_INDIGO_A200 = fromHex("536DFE")
        val MD_INDIGO_A400 = fromHex("3D5AFE")
        val MD_INDIGO_A700 = fromHex("304FFE")
        val MD_INDIGO = MD_INDIGO_500

        val MD_BLUE_50 = fromHex("E3F2FD")
        val MD_BLUE_100 = fromHex("BBDEFB")
        val MD_BLUE_200 = fromHex("90CAF9")
        val MD_BLUE_300 = fromHex("64B5F6")
        val MD_BLUE_400 = fromHex("42A5F5")
        val MD_BLUE_500 = fromHex("2196F3")
        val MD_BLUE_600 = fromHex("1E88E5")
        val MD_BLUE_700 = fromHex("1976D2")
        val MD_BLUE_800 = fromHex("1565C0")
        val MD_BLUE_900 = fromHex("0D47A1")
        val MD_BLUE_A100 = fromHex("82B1FF")
        val MD_BLUE_A200 = fromHex("448AFF")
        val MD_BLUE_A400 = fromHex("2979FF")
        val MD_BLUE_A700 = fromHex("2962FF")
        val MD_BLUE = MD_BLUE_500

        val MD_LIGHT_BLUE_50 = fromHex("E1F5FE")
        val MD_LIGHT_BLUE_100 = fromHex("B3E5FC")
        val MD_LIGHT_BLUE_200 = fromHex("81D4FA")
        val MD_LIGHT_BLUE_300 = fromHex("4FC3F7")
        val MD_LIGHT_BLUE_400 = fromHex("29B6F6")
        val MD_LIGHT_BLUE_500 = fromHex("03A9F4")
        val MD_LIGHT_BLUE_600 = fromHex("039BE5")
        val MD_LIGHT_BLUE_700 = fromHex("0288D1")
        val MD_LIGHT_BLUE_800 = fromHex("0277BD")
        val MD_LIGHT_BLUE_900 = fromHex("01579B")
        val MD_LIGHT_BLUE_A100 = fromHex("80D8FF")
        val MD_LIGHT_BLUE_A200 = fromHex("40C4FF")
        val MD_LIGHT_BLUE_A400 = fromHex("00B0FF")
        val MD_LIGHT_BLUE_A700 = fromHex("0091EA")
        val MD_LIGHT_BLUE = MD_LIGHT_BLUE_500

        val MD_CYAN_50 = fromHex("E0F7FA")
        val MD_CYAN_100 = fromHex("B2EBF2")
        val MD_CYAN_200 = fromHex("80DEEA")
        val MD_CYAN_300 = fromHex("4DD0E1")
        val MD_CYAN_400 = fromHex("26C6DA")
        val MD_CYAN_500 = fromHex("00BCD4")
        val MD_CYAN_600 = fromHex("00ACC1")
        val MD_CYAN_700 = fromHex("0097A7")
        val MD_CYAN_800 = fromHex("00838F")
        val MD_CYAN_900 = fromHex("006064")
        val MD_CYAN_A100 = fromHex("84FFFF")
        val MD_CYAN_A200 = fromHex("18FFFF")
        val MD_CYAN_A400 = fromHex("00E5FF")
        val MD_CYAN_A700 = fromHex("00B8D4")
        val MD_CYAN = MD_CYAN_500

        val MD_TEAL_50 = fromHex("E0F2F1")
        val MD_TEAL_100 = fromHex("B2DFDB")
        val MD_TEAL_200 = fromHex("80CBC4")
        val MD_TEAL_300 = fromHex("4DB6AC")
        val MD_TEAL_400 = fromHex("26A69A")
        val MD_TEAL_500 = fromHex("009688")
        val MD_TEAL_600 = fromHex("00897B")
        val MD_TEAL_700 = fromHex("00796B")
        val MD_TEAL_800 = fromHex("00695C")
        val MD_TEAL_900 = fromHex("004D40")
        val MD_TEAL_A100 = fromHex("A7FFEB")
        val MD_TEAL_A200 = fromHex("64FFDA")
        val MD_TEAL_A400 = fromHex("1DE9B6")
        val MD_TEAL_A700 = fromHex("00BFA5")
        val MD_TEAL = MD_TEAL_500

        val MD_GREEN_50 = fromHex("E8F5E9")
        val MD_GREEN_100 = fromHex("C8E6C9")
        val MD_GREEN_200 = fromHex("A5D6A7")
        val MD_GREEN_300 = fromHex("81C784")
        val MD_GREEN_400 = fromHex("66BB6A")
        val MD_GREEN_500 = fromHex("4CAF50")
        val MD_GREEN_600 = fromHex("43A047")
        val MD_GREEN_700 = fromHex("388E3C")
        val MD_GREEN_800 = fromHex("2E7D32")
        val MD_GREEN_900 = fromHex("1B5E20")
        val MD_GREEN_A100 = fromHex("B9F6CA")
        val MD_GREEN_A200 = fromHex("69F0AE")
        val MD_GREEN_A400 = fromHex("00E676")
        val MD_GREEN_A700 = fromHex("00C853")
        val MD_GREEN = MD_GREEN_500

        val MD_LIGHT_GREEN_50 = fromHex("F1F8E9")
        val MD_LIGHT_GREEN_100 = fromHex("DCEDC8")
        val MD_LIGHT_GREEN_200 = fromHex("C5E1A5")
        val MD_LIGHT_GREEN_300 = fromHex("AED581")
        val MD_LIGHT_GREEN_400 = fromHex("9CCC65")
        val MD_LIGHT_GREEN_500 = fromHex("8BC34A")
        val MD_LIGHT_GREEN_600 = fromHex("7CB342")
        val MD_LIGHT_GREEN_700 = fromHex("689F38")
        val MD_LIGHT_GREEN_800 = fromHex("558B2F")
        val MD_LIGHT_GREEN_900 = fromHex("33691E")
        val MD_LIGHT_GREEN_A100 = fromHex("CCFF90")
        val MD_LIGHT_GREEN_A200 = fromHex("B2FF59")
        val MD_LIGHT_GREEN_A400 = fromHex("76FF03")
        val MD_LIGHT_GREEN_A700 = fromHex("64DD17")
        val MD_LIGHT_GREEN = MD_LIGHT_GREEN_500

        val MD_LIME_50 = fromHex("F9FBE7")
        val MD_LIME_100 = fromHex("F0F4C3")
        val MD_LIME_200 = fromHex("E6EE9C")
        val MD_LIME_300 = fromHex("DCE775")
        val MD_LIME_400 = fromHex("D4E157")
        val MD_LIME_500 = fromHex("CDDC39")
        val MD_LIME_600 = fromHex("C0CA33")
        val MD_LIME_700 = fromHex("AFB42B")
        val MD_LIME_800 = fromHex("9E9D24")
        val MD_LIME_900 = fromHex("827717")
        val MD_LIME_A100 = fromHex("F4FF81")
        val MD_LIME_A200 = fromHex("EEFF41")
        val MD_LIME_A400 = fromHex("C6FF00")
        val MD_LIME_A700 = fromHex("AEEA00")
        val MD_LIME = MD_LIME_500

        val MD_YELLOW_50 = fromHex("FFFDE7")
        val MD_YELLOW_100 = fromHex("FFF9C4")
        val MD_YELLOW_200 = fromHex("FFF59D")
        val MD_YELLOW_300 = fromHex("FFF176")
        val MD_YELLOW_400 = fromHex("FFEE58")
        val MD_YELLOW_500 = fromHex("FFEB3B")
        val MD_YELLOW_600 = fromHex("FDD835")
        val MD_YELLOW_700 = fromHex("FBC02D")
        val MD_YELLOW_800 = fromHex("F9A825")
        val MD_YELLOW_900 = fromHex("F57F17")
        val MD_YELLOW_A100 = fromHex("FFFF8D")
        val MD_YELLOW_A200 = fromHex("FFFF00")
        val MD_YELLOW_A400 = fromHex("FFEA00")
        val MD_YELLOW_A700 = fromHex("FFD600")
        val MD_YELLOW = MD_YELLOW_500

        val MD_AMBER_50 = fromHex("FFF8E1")
        val MD_AMBER_100 = fromHex("FFECB3")
        val MD_AMBER_200 = fromHex("FFE082")
        val MD_AMBER_300 = fromHex("FFD54F")
        val MD_AMBER_400 = fromHex("FFCA28")
        val MD_AMBER_500 = fromHex("FFC107")
        val MD_AMBER_600 = fromHex("FFB300")
        val MD_AMBER_700 = fromHex("FFA000")
        val MD_AMBER_800 = fromHex("FF8F00")
        val MD_AMBER_900 = fromHex("FF6F00")
        val MD_AMBER_A100 = fromHex("FFE57F")
        val MD_AMBER_A200 = fromHex("FFD740")
        val MD_AMBER_A400 = fromHex("FFC400")
        val MD_AMBER_A700 = fromHex("FFAB00")
        val MD_AMBER = MD_AMBER_500

        val MD_ORANGE_50 = fromHex("FFF3E0")
        val MD_ORANGE_100 = fromHex("FFE0B2")
        val MD_ORANGE_200 = fromHex("FFCC80")
        val MD_ORANGE_300 = fromHex("FFB74D")
        val MD_ORANGE_400 = fromHex("FFA726")
        val MD_ORANGE_500 = fromHex("FF9800")
        val MD_ORANGE_600 = fromHex("FB8C00")
        val MD_ORANGE_700 = fromHex("F57C00")
        val MD_ORANGE_800 = fromHex("EF6C00")
        val MD_ORANGE_900 = fromHex("E65100")
        val MD_ORANGE_A100 = fromHex("FFD180")
        val MD_ORANGE_A200 = fromHex("FFAB40")
        val MD_ORANGE_A400 = fromHex("FF9100")
        val MD_ORANGE_A700 = fromHex("FF6D00")
        val MD_ORANGE = MD_ORANGE_500

        val MD_DEEP_ORANGE_50 = fromHex("FBE9E7")
        val MD_DEEP_ORANGE_100 = fromHex("FFCCBC")
        val MD_DEEP_ORANGE_200 = fromHex("FFAB91")
        val MD_DEEP_ORANGE_300 = fromHex("FF8A65")
        val MD_DEEP_ORANGE_400 = fromHex("FF7043")
        val MD_DEEP_ORANGE_500 = fromHex("FF5722")
        val MD_DEEP_ORANGE_600 = fromHex("F4511E")
        val MD_DEEP_ORANGE_700 = fromHex("E64A19")
        val MD_DEEP_ORANGE_800 = fromHex("D84315")
        val MD_DEEP_ORANGE_900 = fromHex("BF360C")
        val MD_DEEP_ORANGE_A100 = fromHex("FF9E80")
        val MD_DEEP_ORANGE_A200 = fromHex("FF6E40")
        val MD_DEEP_ORANGE_A400 = fromHex("FF3D00")
        val MD_DEEP_ORANGE_A700 = fromHex("DD2C00")
        val MD_DEEP_ORANGE = MD_DEEP_ORANGE_500

        val MD_BROWN_50 = fromHex("EFEBE9")
        val MD_BROWN_100 = fromHex("D7CCC8")
        val MD_BROWN_200 = fromHex("BCAAA4")
        val MD_BROWN_300 = fromHex("A1887F")
        val MD_BROWN_400 = fromHex("8D6E63")
        val MD_BROWN_500 = fromHex("795548")
        val MD_BROWN_600 = fromHex("6D4C41")
        val MD_BROWN_700 = fromHex("5D4037")
        val MD_BROWN_800 = fromHex("4E342E")
        val MD_BROWN_900 = fromHex("3E2723")
        val MD_BROWN = MD_BROWN_500

        val MD_GREY_50 = fromHex("FAFAFA")
        val MD_GREY_100 = fromHex("F5F5F5")
        val MD_GREY_200 = fromHex("EEEEEE")
        val MD_GREY_300 = fromHex("E0E0E0")
        val MD_GREY_400 = fromHex("BDBDBD")
        val MD_GREY_500 = fromHex("9E9E9E")
        val MD_GREY_600 = fromHex("757575")
        val MD_GREY_700 = fromHex("616161")
        val MD_GREY_800 = fromHex("424242")
        val MD_GREY_900 = fromHex("212121")
        val MD_GREY = MD_GREY_500

        val MD_BLUE_GREY_50 = fromHex("ECEFF1")
        val MD_BLUE_GREY_100 = fromHex("CFD8DC")
        val MD_BLUE_GREY_200 = fromHex("B0BEC5")
        val MD_BLUE_GREY_300 = fromHex("90A4AE")
        val MD_BLUE_GREY_400 = fromHex("78909C")
        val MD_BLUE_GREY_500 = fromHex("607D8B")
        val MD_BLUE_GREY_600 = fromHex("546E7A")
        val MD_BLUE_GREY_700 = fromHex("455A64")
        val MD_BLUE_GREY_800 = fromHex("37474F")
        val MD_BLUE_GREY_900 = fromHex("263238")
        val MD_BLUE_GREY = MD_BLUE_GREY_500

        val MD_COLORS = listOf(MD_RED, MD_PINK, MD_PURPLE, MD_DEEP_PURPLE, MD_INDIGO, MD_BLUE, MD_LIGHT_BLUE, MD_CYAN,
                MD_TEAL, MD_GREEN, MD_LIGHT_GREEN, MD_LIME, MD_YELLOW, MD_AMBER, MD_ORANGE, MD_DEEP_ORANGE, MD_BROWN,
                MD_GREY, MD_BLUE_GREY)

        fun fromHsv(h: Float, s: Float, v: Float, a: Float): Color {
            val color = MutableColor()
            return color.setHsv(h, s, v, a)
        }

        fun fromHex(hex: String): Color {
            if (hex.isEmpty()) {
                return BLACK
            }

            var str = hex
            if (str[0] == '#') {
                str = str.substring(1)
            }

            var r = 0f
            var g = 0f
            var b = 0f
            var a = 1f
            try {
                when {
                    str.length == 3 -> {
                        val r4 = str.substring(0, 1).toInt(16)
                        val g4 = str.substring(1, 2).toInt(16)
                        val b4 = str.substring(2, 3).toInt(16)
                        r = (r4 or (r4 shl 4)) / 255f
                        g = (g4 or (g4 shl 4)) / 255f
                        b = (b4 or (b4 shl 4)) / 255f

                    }
                    str.length == 4 -> {
                        val r4 = str.substring(0, 1).toInt(16)
                        val g4 = str.substring(1, 2).toInt(16)
                        val b4 = str.substring(2, 3).toInt(16)
                        val a4 = str.substring(2, 3).toInt(16)
                        r = (r4 or (r4 shl 4)) / 255f
                        g = (g4 or (g4 shl 4)) / 255f
                        b = (b4 or (b4 shl 4)) / 255f
                        a = (a4 or (a4 shl 4)) / 255f

                    }
                    str.length == 6 -> {
                        // parse rgb
                        r = str.substring(0, 2).toInt(16) / 255f
                        g = str.substring(2, 4).toInt(16) / 255f
                        b = str.substring(4, 6).toInt(16) / 255f
                    }
                    str.length == 8 -> {
                        // parse rgba
                        r = str.substring(0, 2).toInt(16) / 255f
                        g = str.substring(2, 4).toInt(16) / 255f
                        b = str.substring(4, 6).toInt(16) / 255f
                        a = str.substring(6, 8).toInt(16) / 255f
                    }
                }
            } catch (e: NumberFormatException) {
                logE { "invalid color code: $hex, $e" }
            }
            return Color(r, g, b, a)
        }
    }
}

open class MutableColor(r: Float, g: Float, b: Float, a: Float) : Color(r, g, b, a) {

    override var r
        get() = this[0]
        set(value) { this[0] = value }
    override var g
        get() = this[1]
        set(value) { this[1] = value }
    override var b
        get() = this[2]
        set(value) { this[2] = value }
    override var a
        get() = this[3]
        set(value) { this[3] = value }

    val array: FloatArray
        get() = fields

    constructor() : this(0f, 0f, 0f, 1f)
    constructor(color: Color) : this(color.r, color.g, color.b, color.a)

    fun add(other: Vec4f): MutableColor {
        r += other.x
        g += other.y
        b += other.z
        a += other.w
        return this
    }

    fun add(other: Vec4f, weight: Float): MutableColor {
        r += other.x * weight
        g += other.y * weight
        b += other.z * weight
        a += other.w * weight
        return this
    }

    fun subtract(other: Vec4f): MutableColor {
        r -= other.x
        g -= other.y
        b -= other.z
        a -= other.w
        return this
    }

    fun scale(factor : Float): MutableColor {
        r *= factor
        g *= factor
        b *= factor
        a *= factor
        return this
    }

    fun scaleRgb(factor : Float): MutableColor {
        r *= factor
        g *= factor
        b *= factor
        return this
    }

    fun clear(): MutableColor {
        set(0f, 0f, 0f, 0f)
        return this
    }

    fun set(r: Float, g: Float, b: Float, a: Float): MutableColor {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
        return this
    }

    fun set(other: Vec4f): MutableColor {
        r = other.x
        g = other.y
        b = other.z
        a = other.w
        return this
    }

    open operator fun set(i: Int, v: Float) { fields[i] = v }

    fun setHsv(h: Float, s: Float, v: Float, a: Float): MutableColor {
        var hue = h % 360f
        if (hue < 0) {
            hue += 360f
        }
        val hi = (hue / 60.0f).toInt()
        val f = hue / 60.0f - hi
        val p = v * (1 - s)
        val q = v * (1 - s * f)
        val t = v * (1 - s * (1 - f))

        when (hi) {
            1 -> set(q, v, p, a)
            2 -> set(p, v, t, a)
            3 -> set(p, q, v, a)
            4 -> set(t, p, v, a)
            5 -> set(v, p, q, a)
            else -> set(v, t, p, a)
        }
        return this
    }
}
