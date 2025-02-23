package de.fabmax.kool.util

import de.fabmax.kool.math.clamp
import kotlin.math.min

fun ColorGradient(vararg colors: Pair<Float, Color>, n: Int = ColorGradient.DEFAULT_N, toLinear: Boolean = false): ColorGradient {
    return ColorGradient(colors.toList(), n, toLinear)
}

fun ColorGradient(vararg colors: Color, n: Int = ColorGradient.DEFAULT_N, toLinear: Boolean = false): ColorGradient {
    return ColorGradient(
        colors.mapIndexed { i, color -> i.toFloat() to color },
        n,
        toLinear
    )
}

class ColorGradient(colors: List<Pair<Float, Color>>, n: Int = DEFAULT_N, toLinear: Boolean = false) {

    private val gradient = Array(n) { MutableColor() }

    init {
        check(colors.size >= 2) { "ColorGradient requires at least two colors" }

        val sortedColors = colors.sortedBy { it.first }
        val mi = sortedColors.first().first
        val mx = sortedColors.last().first

        var pi = 0
        var p0 = sortedColors[pi++]
        var p1 = sortedColors[pi++]
        for (i in 0 until n) {
            val p = i / (n-1f) * (mx - mi) + mi
            while (p > p1.first) {
                p0 = p1
                p1 = sortedColors[min(pi++, sortedColors.size)]
            }
            val w0 = 1f - (p - p0.first) / (p1.first - p0.first)
            p1.second.mix(p0.second, w0, gradient[i])
            if (toLinear) {
                gradient[i] = gradient[i].toLinear()
            }
        }
    }

    fun getColor(value: Float, min: Float = 0f, max: Float = 1f): Color {
        val f = (value - min) / (max - min) * gradient.size
        val i = f.toInt()
        return gradient[i.clamp(0, gradient.size - 1)]
    }

    fun getColorInterpolated(value: Float, result: MutableColor, min: Float = 0f, max: Float = 1f): MutableColor {
        val fi = ((value - min) / (max - min) * gradient.size).clamp(0f, gradient.size - 1f)
        val iLower = fi.toInt().clamp(0, gradient.size - 1)
        val iUpper = (iLower + 1).clamp(0, gradient.size - 1)
        val wUpper = iUpper - fi
        return gradient[iLower].mix(gradient[iUpper], wUpper, result)
    }

    fun inverted(): ColorGradient {
        val invertedColors = Array<Pair<Float, Color>>(gradient.size) { i -> i.toFloat() to gradient[gradient.lastIndex - i] }
        return ColorGradient(*invertedColors, n = gradient.size)
    }

    fun toLinear(): ColorGradient {
        val invertedColors = Array<Pair<Float, Color>>(gradient.size) { i -> i.toFloat() to gradient[i].toLinear() }
        return ColorGradient(*invertedColors, n = gradient.size)
    }

    companion object {
        const val DEFAULT_N = 256

        private val MD_BLUE = Color.fromHex("2196F3")
        private val MD_CYAN = Color.fromHex("00BCD4")
        private val MD_GREEN = Color.fromHex("4CAF50")
        private val MD_YELLOW = Color.fromHex("FFEB3B")
        private val MD_RED = Color.fromHex("F44336")
        private val MD_PURPLE = Color.fromHex("9C27B0")

        val JET = ColorGradient(Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.MAGENTA)

        val JET_MD = ColorGradient(MD_BLUE, MD_CYAN, MD_GREEN, MD_YELLOW, MD_RED, MD_PURPLE)

        val RED_YELLOW_GREEN = ColorGradient(Color.RED, Color.YELLOW, Color.GREEN)

        val RED_YELLOW_GREEN_MD = ColorGradient(MD_RED, MD_YELLOW, MD_GREEN)

        val RED_WHITE_BLUE = ColorGradient(
                0f to Color(0.35f, 0f, 0f, 1f),
                0.35f to Color(0.81f, 0.39f, 0f, 1f),
                0.5f to Color.WHITE,
                0.75f to Color(0f, 0.5f, 1f, 1f),
                1f to Color(0f, 0.18f, 0.47f, 1f)
        )

        val MAGMA = ColorGradient(
            Color("000003"),
            Color("231251"),
            Color("5F187F"),
            Color("982D80"),
            Color("D3436E"),
            Color("F8765C"),
            Color("FEBA80"),
            Color("FCFDBF"),
        )

        val INFERNO = ColorGradient(
            Color("000003"),
            Color("270B54"),
            Color("65156E"),
            Color("9F2A63"),
            Color("D44842"),
            Color("F57D15"),
            Color("FAC127"),
            Color("FCFDA4"),
        )

        val PLASMA = ColorGradient(
            Color("0D1687"),
            Color("541FA3"),
            Color("8B23A5"),
            Color("B93289"),
            Color("DB5C68"),
            Color("F48848"),
            Color("FEBC2A"),
            Color("F0F920"),
        )

        val VIRIDIS = ColorGradient(
            Color("440D54"),
            Color("47337E"),
            Color("365C8D"),
            Color("277F8E"),
            Color("1FA187"),
            Color("4AC16D"),
            Color("9FDA3A"),
            Color("FDE725"),
        )

        val CIVIDIS = ColorGradient(
            Color("00214D"),
            Color("16396D"),
            Color("4B546C"),
            Color("6C6E72"),
            Color("8E8A79"),
            Color("B3A772"),
            Color("DBC761"),
            Color("FFEA46"),
        )

        val ROCKET = ColorGradient(
            Color("02041A"),
            Color("36193E"),
            Color("701F57"),
            Color("AE1B59"),
            Color("E13342"),
            Color("F37651"),
            Color("F6B48E"),
            Color("FAEBDD"),
        )

        val MAKO = ColorGradient(
            Color("0C0304"),
            Color("2E1D3C"),
            Color("423C7B"),
            Color("37659E"),
            Color("348FA7"),
            Color("3FB7AD"),
            Color("8AD9B1"),
            Color("DEF5E5"),
        )

        val TURBO = ColorGradient(
            Color("31123B"),
            Color("4777EF"),
            Color("1BD0D5"),
            Color("62FA6B"),
            Color("D2E934"),
            Color("FE9B2D"),
            Color("DB3A07"),
            Color("7A0C02"),
        )
    }
}