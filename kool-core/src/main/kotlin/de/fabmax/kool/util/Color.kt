package de.fabmax.kool.util

import de.fabmax.kool.math.Vec4f

/**
 * @author fabmax
 */

@Suppress("unused")
open class Color(r: Float, g: Float, b: Float, a: Float = 1f) : Vec4f(r, g, b, a) {

    open val r get() = this[0]
    open val g get() = this[1]
    open val b get() = this[2]
    open val a get() = this[3]

    fun withAlpha(alpha: Float): MutableColor {
        return MutableColor(r, g, b, alpha)
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
        val MD_RED_50 = color("FFEBEE")
        val MD_RED_100 = color("FFCDD2")
        val MD_RED_200 = color("EF9A9A")
        val MD_RED_300 = color("E57373")
        val MD_RED_400 = color("EF5350")
        val MD_RED_500 = color("F44336")
        val MD_RED_600 = color("E53935")
        val MD_RED_700 = color("D32F2F")
        val MD_RED_800 = color("C62828")
        val MD_RED_900 = color("B71C1C")
        val MD_RED_A100 = color("FF8A80")
        val MD_RED_A200 = color("FF5252")
        val MD_RED_A400 = color("FF1744")
        val MD_RED_A700 = color("D50000")
        val MD_RED = MD_RED_500

        val MD_PINK_50 = color("FCE4EC")
        val MD_PINK_100 = color("F8BBD0")
        val MD_PINK_200 = color("F48FB1")
        val MD_PINK_300 = color("F06292")
        val MD_PINK_400 = color("EC407A")
        val MD_PINK_500 = color("E91E63")
        val MD_PINK_600 = color("D81B60")
        val MD_PINK_700 = color("C2185B")
        val MD_PINK_800 = color("AD1457")
        val MD_PINK_900 = color("880E4F")
        val MD_PINK_A100 = color("FF80AB")
        val MD_PINK_A200 = color("FF4081")
        val MD_PINK_A400 = color("F50057")
        val MD_PINK_A700 = color("C51162")
        val MD_PINK = MD_PINK_500

        val MD_PURPLE_50 = color("F3E5F5")
        val MD_PURPLE_100 = color("E1BEE7")
        val MD_PURPLE_200 = color("CE93D8")
        val MD_PURPLE_300 = color("BA68C8")
        val MD_PURPLE_400 = color("AB47BC")
        val MD_PURPLE_500 = color("9C27B0")
        val MD_PURPLE_600 = color("8E24AA")
        val MD_PURPLE_700 = color("7B1FA2")
        val MD_PURPLE_800 = color("6A1B9A")
        val MD_PURPLE_900 = color("4A148C")
        val MD_PURPLE_A100 = color("EA80FC")
        val MD_PURPLE_A200 = color("E040FB")
        val MD_PURPLE_A400 = color("D500F9")
        val MD_PURPLE_A700 = color("AA00FF")
        val MD_PURPLE = MD_PURPLE_500

        val MD_DEEP_PURPLE_50 = color("EDE7F6")
        val MD_DEEP_PURPLE_100 = color("D1C4E9")
        val MD_DEEP_PURPLE_200 = color("B39DDB")
        val MD_DEEP_PURPLE_300 = color("9575CD")
        val MD_DEEP_PURPLE_400 = color("7E57C2")
        val MD_DEEP_PURPLE_500 = color("673AB7")
        val MD_DEEP_PURPLE_600 = color("5E35B1")
        val MD_DEEP_PURPLE_700 = color("512DA8")
        val MD_DEEP_PURPLE_800 = color("4527A0")
        val MD_DEEP_PURPLE_900 = color("311B92")
        val MD_DEEP_PURPLE_A100 = color("B388FF")
        val MD_DEEP_PURPLE_A200 = color("7C4DFF")
        val MD_DEEP_PURPLE_A400 = color("651FFF")
        val MD_DEEP_PURPLE_A700 = color("6200EA")
        val MD_DEEP_PURPLE = MD_DEEP_PURPLE_500

        val MD_INDIGO_50 = color("E8EAF6")
        val MD_INDIGO_100 = color("C5CAE9")
        val MD_INDIGO_200 = color("9FA8DA")
        val MD_INDIGO_300 = color("7986CB")
        val MD_INDIGO_400 = color("5C6BC0")
        val MD_INDIGO_500 = color("3F51B5")
        val MD_INDIGO_600 = color("3949AB")
        val MD_INDIGO_700 = color("303F9F")
        val MD_INDIGO_800 = color("283593")
        val MD_INDIGO_900 = color("1A237E")
        val MD_INDIGO_A100 = color("8C9EFF")
        val MD_INDIGO_A200 = color("536DFE")
        val MD_INDIGO_A400 = color("3D5AFE")
        val MD_INDIGO_A700 = color("304FFE")
        val MD_INDIGO = MD_INDIGO_500

        val MD_BLUE_50 = color("E3F2FD")
        val MD_BLUE_100 = color("BBDEFB")
        val MD_BLUE_200 = color("90CAF9")
        val MD_BLUE_300 = color("64B5F6")
        val MD_BLUE_400 = color("42A5F5")
        val MD_BLUE_500 = color("2196F3")
        val MD_BLUE_600 = color("1E88E5")
        val MD_BLUE_700 = color("1976D2")
        val MD_BLUE_800 = color("1565C0")
        val MD_BLUE_900 = color("0D47A1")
        val MD_BLUE_A100 = color("82B1FF")
        val MD_BLUE_A200 = color("448AFF")
        val MD_BLUE_A400 = color("2979FF")
        val MD_BLUE_A700 = color("2962FF")
        val MD_BLUE = MD_BLUE_500

        val MD_LIGHT_BLUE_50 = color("E1F5FE")
        val MD_LIGHT_BLUE_100 = color("B3E5FC")
        val MD_LIGHT_BLUE_200 = color("81D4FA")
        val MD_LIGHT_BLUE_300 = color("4FC3F7")
        val MD_LIGHT_BLUE_400 = color("29B6F6")
        val MD_LIGHT_BLUE_500 = color("03A9F4")
        val MD_LIGHT_BLUE_600 = color("039BE5")
        val MD_LIGHT_BLUE_700 = color("0288D1")
        val MD_LIGHT_BLUE_800 = color("0277BD")
        val MD_LIGHT_BLUE_900 = color("01579B")
        val MD_LIGHT_BLUE_A100 = color("80D8FF")
        val MD_LIGHT_BLUE_A200 = color("40C4FF")
        val MD_LIGHT_BLUE_A400 = color("00B0FF")
        val MD_LIGHT_BLUE_A700 = color("0091EA")
        val MD_LIGHT_BLUE = MD_LIGHT_BLUE_500

        val MD_CYAN_50 = color("E0F7FA")
        val MD_CYAN_100 = color("B2EBF2")
        val MD_CYAN_200 = color("80DEEA")
        val MD_CYAN_300 = color("4DD0E1")
        val MD_CYAN_400 = color("26C6DA")
        val MD_CYAN_500 = color("00BCD4")
        val MD_CYAN_600 = color("00ACC1")
        val MD_CYAN_700 = color("0097A7")
        val MD_CYAN_800 = color("00838F")
        val MD_CYAN_900 = color("006064")
        val MD_CYAN_A100 = color("84FFFF")
        val MD_CYAN_A200 = color("18FFFF")
        val MD_CYAN_A400 = color("00E5FF")
        val MD_CYAN_A700 = color("00B8D4")
        val MD_CYAN = MD_CYAN_500

        val MD_TEAL_50 = color("E0F2F1")
        val MD_TEAL_100 = color("B2DFDB")
        val MD_TEAL_200 = color("80CBC4")
        val MD_TEAL_300 = color("4DB6AC")
        val MD_TEAL_400 = color("26A69A")
        val MD_TEAL_500 = color("009688")
        val MD_TEAL_600 = color("00897B")
        val MD_TEAL_700 = color("00796B")
        val MD_TEAL_800 = color("00695C")
        val MD_TEAL_900 = color("004D40")
        val MD_TEAL_A100 = color("A7FFEB")
        val MD_TEAL_A200 = color("64FFDA")
        val MD_TEAL_A400 = color("1DE9B6")
        val MD_TEAL_A700 = color("00BFA5")
        val MD_TEAL = MD_TEAL_500

        val MD_GREEN_50 = color("E8F5E9")
        val MD_GREEN_100 = color("C8E6C9")
        val MD_GREEN_200 = color("A5D6A7")
        val MD_GREEN_300 = color("81C784")
        val MD_GREEN_400 = color("66BB6A")
        val MD_GREEN_500 = color("4CAF50")
        val MD_GREEN_600 = color("43A047")
        val MD_GREEN_700 = color("388E3C")
        val MD_GREEN_800 = color("2E7D32")
        val MD_GREEN_900 = color("1B5E20")
        val MD_GREEN_A100 = color("B9F6CA")
        val MD_GREEN_A200 = color("69F0AE")
        val MD_GREEN_A400 = color("00E676")
        val MD_GREEN_A700 = color("00C853")
        val MD_GREEN = MD_GREEN_500

        val MD_LIGHT_GREEN_50 = color("F1F8E9")
        val MD_LIGHT_GREEN_100 = color("DCEDC8")
        val MD_LIGHT_GREEN_200 = color("C5E1A5")
        val MD_LIGHT_GREEN_300 = color("AED581")
        val MD_LIGHT_GREEN_400 = color("9CCC65")
        val MD_LIGHT_GREEN_500 = color("8BC34A")
        val MD_LIGHT_GREEN_600 = color("7CB342")
        val MD_LIGHT_GREEN_700 = color("689F38")
        val MD_LIGHT_GREEN_800 = color("558B2F")
        val MD_LIGHT_GREEN_900 = color("33691E")
        val MD_LIGHT_GREEN_A100 = color("CCFF90")
        val MD_LIGHT_GREEN_A200 = color("B2FF59")
        val MD_LIGHT_GREEN_A400 = color("76FF03")
        val MD_LIGHT_GREEN_A700 = color("64DD17")
        val MD_LIGHT_GREEN = MD_LIGHT_GREEN_500

        val MD_LIME_50 = color("F9FBE7")
        val MD_LIME_100 = color("F0F4C3")
        val MD_LIME_200 = color("E6EE9C")
        val MD_LIME_300 = color("DCE775")
        val MD_LIME_400 = color("D4E157")
        val MD_LIME_500 = color("CDDC39")
        val MD_LIME_600 = color("C0CA33")
        val MD_LIME_700 = color("AFB42B")
        val MD_LIME_800 = color("9E9D24")
        val MD_LIME_900 = color("827717")
        val MD_LIME_A100 = color("F4FF81")
        val MD_LIME_A200 = color("EEFF41")
        val MD_LIME_A400 = color("C6FF00")
        val MD_LIME_A700 = color("AEEA00")
        val MD_LIME = MD_LIME_500

        val MD_YELLOW_50 = color("FFFDE7")
        val MD_YELLOW_100 = color("FFF9C4")
        val MD_YELLOW_200 = color("FFF59D")
        val MD_YELLOW_300 = color("FFF176")
        val MD_YELLOW_400 = color("FFEE58")
        val MD_YELLOW_500 = color("FFEB3B")
        val MD_YELLOW_600 = color("FDD835")
        val MD_YELLOW_700 = color("FBC02D")
        val MD_YELLOW_800 = color("F9A825")
        val MD_YELLOW_900 = color("F57F17")
        val MD_YELLOW_A100 = color("FFFF8D")
        val MD_YELLOW_A200 = color("FFFF00")
        val MD_YELLOW_A400 = color("FFEA00")
        val MD_YELLOW_A700 = color("FFD600")
        val MD_YELLOW = MD_YELLOW_500

        val MD_AMBER_50 = color("FFF8E1")
        val MD_AMBER_100 = color("FFECB3")
        val MD_AMBER_200 = color("FFE082")
        val MD_AMBER_300 = color("FFD54F")
        val MD_AMBER_400 = color("FFCA28")
        val MD_AMBER_500 = color("FFC107")
        val MD_AMBER_600 = color("FFB300")
        val MD_AMBER_700 = color("FFA000")
        val MD_AMBER_800 = color("FF8F00")
        val MD_AMBER_900 = color("FF6F00")
        val MD_AMBER_A100 = color("FFE57F")
        val MD_AMBER_A200 = color("FFD740")
        val MD_AMBER_A400 = color("FFC400")
        val MD_AMBER_A700 = color("FFAB00")
        val MD_AMBER = MD_AMBER_500

        val MD_ORANGE_50 = color("FFF3E0")
        val MD_ORANGE_100 = color("FFE0B2")
        val MD_ORANGE_200 = color("FFCC80")
        val MD_ORANGE_300 = color("FFB74D")
        val MD_ORANGE_400 = color("FFA726")
        val MD_ORANGE_500 = color("FF9800")
        val MD_ORANGE_600 = color("FB8C00")
        val MD_ORANGE_700 = color("F57C00")
        val MD_ORANGE_800 = color("EF6C00")
        val MD_ORANGE_900 = color("E65100")
        val MD_ORANGE_A100 = color("FFD180")
        val MD_ORANGE_A200 = color("FFAB40")
        val MD_ORANGE_A400 = color("FF9100")
        val MD_ORANGE_A700 = color("FF6D00")
        val MD_ORANGE = MD_ORANGE_500

        val MD_DEEP_ORANGE_50 = color("FBE9E7")
        val MD_DEEP_ORANGE_100 = color("FFCCBC")
        val MD_DEEP_ORANGE_200 = color("FFAB91")
        val MD_DEEP_ORANGE_300 = color("FF8A65")
        val MD_DEEP_ORANGE_400 = color("FF7043")
        val MD_DEEP_ORANGE_500 = color("FF5722")
        val MD_DEEP_ORANGE_600 = color("F4511E")
        val MD_DEEP_ORANGE_700 = color("E64A19")
        val MD_DEEP_ORANGE_800 = color("D84315")
        val MD_DEEP_ORANGE_900 = color("BF360C")
        val MD_DEEP_ORANGE_A100 = color("FF9E80")
        val MD_DEEP_ORANGE_A200 = color("FF6E40")
        val MD_DEEP_ORANGE_A400 = color("FF3D00")
        val MD_DEEP_ORANGE_A700 = color("DD2C00")
        val MD_DEEP_ORANGE = MD_DEEP_ORANGE_500

        val MD_BROWN_50 = color("EFEBE9")
        val MD_BROWN_100 = color("D7CCC8")
        val MD_BROWN_200 = color("BCAAA4")
        val MD_BROWN_300 = color("A1887F")
        val MD_BROWN_400 = color("8D6E63")
        val MD_BROWN_500 = color("795548")
        val MD_BROWN_600 = color("6D4C41")
        val MD_BROWN_700 = color("5D4037")
        val MD_BROWN_800 = color("4E342E")
        val MD_BROWN_900 = color("3E2723")
        val MD_BROWN = MD_BROWN_500

        val MD_GREY_50 = color("FAFAFA")
        val MD_GREY_100 = color("F5F5F5")
        val MD_GREY_200 = color("EEEEEE")
        val MD_GREY_300 = color("E0E0E0")
        val MD_GREY_400 = color("BDBDBD")
        val MD_GREY_500 = color("9E9E9E")
        val MD_GREY_600 = color("757575")
        val MD_GREY_700 = color("616161")
        val MD_GREY_800 = color("424242")
        val MD_GREY_900 = color("212121")
        val MD_GREY = MD_GREY_500

        val MD_BLUE_GREY_50 = color("ECEFF1")
        val MD_BLUE_GREY_100 = color("CFD8DC")
        val MD_BLUE_GREY_200 = color("B0BEC5")
        val MD_BLUE_GREY_300 = color("90A4AE")
        val MD_BLUE_GREY_400 = color("78909C")
        val MD_BLUE_GREY_500 = color("607D8B")
        val MD_BLUE_GREY_600 = color("546E7A")
        val MD_BLUE_GREY_700 = color("455A64")
        val MD_BLUE_GREY_800 = color("37474F")
        val MD_BLUE_GREY_900 = color("263238")
        val MD_BLUE_GREY = MD_BLUE_GREY_500

        fun fromHsv(h: Float, s: Float, v: Float, a: Float): Color {
            val color = MutableColor()
            return color.setHsv(h, s, v, a)
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

    constructor() : this(0f, 0f, 0f, 1f)
    constructor(color: Color) : this(color.r, color.g, color.b, color.a)

    fun add(other: Color): MutableColor {
        r += other.r
        g += other.g
        b += other.b
        a += other.a
        return this
    }

    fun add(other: Color, weight: Float): MutableColor {
        r += other.r * weight
        g += other.g * weight
        b += other.b * weight
        a += other.a * weight
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

    fun set(other: Color): MutableColor {
        r = other.r
        g = other.g
        b = other.b
        a = other.a
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

fun color(hex: String): Color {
    if (hex.isEmpty()) {
        return Color.BLACK
    }

    var str = hex
    if (str[0] == '#') {
        str = str.substring(1)
    }

    var r = 0f
    var g = 0f
    var b = 0f
    var a = 1f
    if (str.length == 3) {
        val r4 = str.substring(0, 1).toInt(16)
        val g4 = str.substring(1, 2).toInt(16)
        val b4 = str.substring(2, 3).toInt(16)
        r = (r4 or (r4 shl 4)) / 255f
        g = (g4 or (g4 shl 4)) / 255f
        b = (b4 or (b4 shl 4)) / 255f

    } else if (str.length == 4) {
        val r4 = str.substring(0, 1).toInt(16)
        val g4 = str.substring(1, 2).toInt(16)
        val b4 = str.substring(2, 3).toInt(16)
        val a4 = str.substring(2, 3).toInt(16)
        r = (r4 or (r4 shl 4)) / 255f
        g = (g4 or (g4 shl 4)) / 255f
        b = (b4 or (b4 shl 4)) / 255f
        a = (a4 or (a4 shl 4)) / 255f

    } else if (str.length == 6) {
        // parse rgb
        r = str.substring(0, 2).toInt(16) / 255f
        g = str.substring(2, 4).toInt(16) / 255f
        b = str.substring(4, 6).toInt(16) / 255f
    } else if (str.length == 8) {
        // parse rgba
        r = str.substring(0, 2).toInt(16) / 255f
        g = str.substring(2, 4).toInt(16) / 255f
        b = str.substring(4, 6).toInt(16) / 255f
        a = str.substring(6, 8).toInt(16) / 255f
    }
    return Color(r, g, b, a)
}
