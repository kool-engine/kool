package de.fabmax.kool.util

/**
 * Classic 2014 Material Design color palette
 * https://material.io/guidelines/style/color.html#color-color-palette
 */
class MdColor(private val shades: ColorGradient) : Color(shades.getColor(500f, 50f, 900f)) {

    infix fun tone(tone: Int) = shades.getColor(tone.toFloat(), 50f, 900f)

    infix fun toneLin(tone: Int) = tone(tone).toLinear()

    companion object {
        val RED = MdColor(ColorGradient(
            50f to fromHex("FFEBEE"),
            100f to fromHex("FFCDD2"),
            200f to fromHex("EF9A9A"),
            300f to fromHex("E57373"),
            400f to fromHex("EF5350"),
            500f to fromHex("F44336"),
            600f to fromHex("E53935"),
            700f to fromHex("D32F2F"),
            800f to fromHex("C62828"),
            900f to fromHex("B71C1C")
        ))

        val PINK = MdColor(ColorGradient(
            50f to fromHex("FCE4EC"),
            100f to fromHex("F8BBD0"),
            200f to fromHex("F48FB1"),
            300f to fromHex("F06292"),
            400f to fromHex("EC407A"),
            500f to fromHex("E91E63"),
            600f to fromHex("D81B60"),
            700f to fromHex("C2185B"),
            800f to fromHex("AD1457"),
            900f to fromHex("880E4F")
        ))

        val PURPLE = MdColor(ColorGradient(
            50f to fromHex("F3E5F5"),
            100f to fromHex("E1BEE7"),
            200f to fromHex("CE93D8"),
            300f to fromHex("BA68C8"),
            400f to fromHex("AB47BC"),
            500f to fromHex("9C27B0"),
            600f to fromHex("8E24AA"),
            700f to fromHex("7B1FA2"),
            800f to fromHex("6A1B9A"),
            900f to fromHex("4A148C")
        ))

        val DEEP_PURPLE = MdColor(ColorGradient(
            50f to fromHex("EDE7F6"),
            100f to fromHex("D1C4E9"),
            200f to fromHex("B39DDB"),
            300f to fromHex("9575CD"),
            400f to fromHex("7E57C2"),
            500f to fromHex("673AB7"),
            600f to fromHex("5E35B1"),
            700f to fromHex("512DA8"),
            800f to fromHex("4527A0"),
            900f to fromHex("311B92")
        ))

        val INDIGO = MdColor(ColorGradient(
            50f to fromHex("E8EAF6"),
            100f to fromHex("C5CAE9"),
            200f to fromHex("9FA8DA"),
            300f to fromHex("7986CB"),
            400f to fromHex("5C6BC0"),
            500f to fromHex("3F51B5"),
            600f to fromHex("3949AB"),
            700f to fromHex("303F9F"),
            800f to fromHex("283593"),
            900f to fromHex("1A237E"),
        ))

        val BLUE = MdColor(ColorGradient(
            50f to fromHex("E3F2FD"),
            100f to fromHex("BBDEFB"),
            200f to fromHex("90CAF9"),
            300f to fromHex("64B5F6"),
            400f to fromHex("42A5F5"),
            500f to fromHex("2196F3"),
            600f to fromHex("1E88E5"),
            700f to fromHex("1976D2"),
            800f to fromHex("1565C0"),
            900f to fromHex("0D47A1"),
        ))

        val LIGHT_BLUE = MdColor(ColorGradient(
            50f to fromHex("E1F5FE"),
            100f to fromHex("B3E5FC"),
            200f to fromHex("81D4FA"),
            300f to fromHex("4FC3F7"),
            400f to fromHex("29B6F6"),
            500f to fromHex("03A9F4"),
            600f to fromHex("039BE5"),
            700f to fromHex("0288D1"),
            800f to fromHex("0277BD"),
            900f to fromHex("01579B"),
        ))

        val CYAN = MdColor(ColorGradient(
            50f to fromHex("E0F7FA"),
            100f to fromHex("B2EBF2"),
            200f to fromHex("80DEEA"),
            300f to fromHex("4DD0E1"),
            400f to fromHex("26C6DA"),
            500f to fromHex("00BCD4"),
            600f to fromHex("00ACC1"),
            700f to fromHex("0097A7"),
            800f to fromHex("00838F"),
            900f to fromHex("006064"),
        ))

        val TEAL = MdColor(ColorGradient(
            50f to fromHex("E0F2F1"),
            100f to fromHex("B2DFDB"),
            200f to fromHex("80CBC4"),
            300f to fromHex("4DB6AC"),
            400f to fromHex("26A69A"),
            500f to fromHex("009688"),
            600f to fromHex("00897B"),
            700f to fromHex("00796B"),
            800f to fromHex("00695C"),
            900f to fromHex("004D40"),
        ))

        val GREEN = MdColor(ColorGradient(
            50f to fromHex("E8F5E9"),
            100f to fromHex("C8E6C9"),
            200f to fromHex("A5D6A7"),
            300f to fromHex("81C784"),
            400f to fromHex("66BB6A"),
            500f to fromHex("4CAF50"),
            600f to fromHex("43A047"),
            700f to fromHex("388E3C"),
            800f to fromHex("2E7D32"),
            900f to fromHex("1B5E20"),
        ))

        val LIGHT_GREEN = MdColor(ColorGradient(
            50f to fromHex("F1F8E9"),
            100f to fromHex("DCEDC8"),
            200f to fromHex("C5E1A5"),
            300f to fromHex("AED581"),
            400f to fromHex("9CCC65"),
            500f to fromHex("8BC34A"),
            600f to fromHex("7CB342"),
            700f to fromHex("689F38"),
            800f to fromHex("558B2F"),
            900f to fromHex("33691E"),
        ))

        val LIME = MdColor(ColorGradient(
            50f to fromHex("F9FBE7"),
            100f to fromHex("F0F4C3"),
            200f to fromHex("E6EE9C"),
            300f to fromHex("DCE775"),
            400f to fromHex("D4E157"),
            500f to fromHex("CDDC39"),
            600f to fromHex("C0CA33"),
            700f to fromHex("AFB42B"),
            800f to fromHex("9E9D24"),
            900f to fromHex("827717"),
        ))

        val YELLOW = MdColor(ColorGradient(
            50f to fromHex("FFFDE7"),
            100f to fromHex("FFF9C4"),
            200f to fromHex("FFF59D"),
            300f to fromHex("FFF176"),
            400f to fromHex("FFEE58"),
            500f to fromHex("FFEB3B"),
            600f to fromHex("FDD835"),
            700f to fromHex("FBC02D"),
            800f to fromHex("F9A825"),
            900f to fromHex("F57F17"),
        ))

        val AMBER = MdColor(ColorGradient(
            50f to fromHex("FFF8E1"),
            100f to fromHex("FFECB3"),
            200f to fromHex("FFE082"),
            300f to fromHex("FFD54F"),
            400f to fromHex("FFCA28"),
            500f to fromHex("FFC107"),
            600f to fromHex("FFB300"),
            700f to fromHex("FFA000"),
            800f to fromHex("FF8F00"),
            900f to fromHex("FF6F00"),
        ))

        val ORANGE = MdColor(ColorGradient(
            50f to fromHex("FFF3E0"),
            100f to fromHex("FFE0B2"),
            200f to fromHex("FFCC80"),
            300f to fromHex("FFB74D"),
            400f to fromHex("FFA726"),
            500f to fromHex("FF9800"),
            600f to fromHex("FB8C00"),
            700f to fromHex("F57C00"),
            800f to fromHex("EF6C00"),
            900f to fromHex("E65100"),
        ))

        val DEEP_ORANGE = MdColor(ColorGradient(
            50f to fromHex("FBE9E7"),
            100f to fromHex("FFCCBC"),
            200f to fromHex("FFAB91"),
            300f to fromHex("FF8A65"),
            400f to fromHex("FF7043"),
            500f to fromHex("FF5722"),
            600f to fromHex("F4511E"),
            700f to fromHex("E64A19"),
            800f to fromHex("D84315"),
            900f to fromHex("BF360C"),
        ))

        val BROWN = MdColor(ColorGradient(
            50f to fromHex("EFEBE9"),
            100f to fromHex("D7CCC8"),
            200f to fromHex("BCAAA4"),
            300f to fromHex("A1887F"),
            400f to fromHex("8D6E63"),
            500f to fromHex("795548"),
            600f to fromHex("6D4C41"),
            700f to fromHex("5D4037"),
            800f to fromHex("4E342E"),
            900f to fromHex("3E2723"),
        ))

        val GREY = MdColor(ColorGradient(
            50f to fromHex("FAFAFA"),
            100f to fromHex("F5F5F5"),
            200f to fromHex("EEEEEE"),
            300f to fromHex("E0E0E0"),
            400f to fromHex("BDBDBD"),
            500f to fromHex("9E9E9E"),
            600f to fromHex("757575"),
            700f to fromHex("616161"),
            800f to fromHex("424242"),
            900f to fromHex("212121"),
        ))

        val BLUE_GREY = MdColor(ColorGradient(
            50f to fromHex("ECEFF1"),
            100f to fromHex("CFD8DC"),
            200f to fromHex("B0BEC5"),
            300f to fromHex("90A4AE"),
            400f to fromHex("78909C"),
            500f to fromHex("607D8B"),
            600f to fromHex("546E7A"),
            700f to fromHex("455A64"),
            800f to fromHex("37474F"),
            900f to fromHex("263238"),
        ))

        val PALETTE = listOf(RED, PINK, PURPLE, DEEP_PURPLE, INDIGO, BLUE, LIGHT_BLUE, CYAN,
            TEAL, GREEN, LIGHT_GREEN, LIME, YELLOW, AMBER, ORANGE, DEEP_ORANGE, BROWN,
            GREY, BLUE_GREY)
    }

}