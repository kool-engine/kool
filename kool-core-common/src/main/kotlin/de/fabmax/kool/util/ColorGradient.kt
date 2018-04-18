package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.clamp
import kotlin.math.min

/**
 * @author fabmax
 */

class ColorGradient(vararg colors: Pair<Float, Color>, n: Int = DEFAULT_N) {

    private val gradient = Array(n) { MutableColor() }

    constructor(vararg colors: Color, n: Int = ColorGradient.DEFAULT_N) :
            this(*Array(colors.size) { i -> i.toFloat() to colors[i] }, n = n)

    init {
        if (colors.size < 2) {
            throw KoolException("ColorGradient requires at least two colors")
        }

        colors.sortBy { it.first }
        val mi = colors.first().first
        val mx = colors.last().first

        var pi = 0
        var p0 = colors[pi++]
        var p1 = colors[pi++]
        for (i in 0 until n) {
            val p = i / (n-1f) * (mx - mi) + mi
            while (p > p1.first) {
                p0 = p1
                p1 = colors[min(pi++, colors.size)]
            }
            val w0 = 1f - (p - p0.first) / (p1.first - p0.first)
            gradient[i].set(p0.second).scale(w0).add(p1.second, 1f - w0)
        }
    }

    fun getColor(value: Float, min: Float = 0f, max: Float = 1f): Color =
            gradient[((value - min) / (max - min) * gradient.size).toInt().clamp(0, gradient.size - 1)]

    companion object {
        const val DEFAULT_N = 256

        val JET = ColorGradient(Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.MAGENTA)
        val JET_MD = ColorGradient(Color.MD_BLUE, Color.MD_CYAN, Color.MD_GREEN, Color.MD_YELLOW, Color.MD_RED, Color.MD_PURPLE)
        val RED_YELLOW_GREEN = ColorGradient(Color.RED, Color.YELLOW, Color.GREEN)
        val RED_YELLOW_GREEN_MD = ColorGradient(Color.MD_RED, Color.MD_YELLOW, Color.MD_GREEN)
        val RED_WHITE_BLUE = ColorGradient(
                0f to Color(0.35f, 0f, 0f, 1f),
                0.35f to Color(0.81f, 0.39f, 0f, 1f),
                0.5f to Color.WHITE,
                0.75f to Color(0f, 0.5f, 1f, 1f),
                1f to Color(0f, 0.18f, 0.47f, 1f)
        )
        val PLASMA = ColorGradient(
                Color(0.0504f, 0.0298f, 0.5280f, 1f),
                Color(0.1324f, 0.0223f, 0.5633f, 1f),
                Color(0.1934f, 0.0184f, 0.5903f, 1f),
                Color(0.2546f, 0.0139f, 0.6154f, 1f),
                Color(0.3062f, 0.0089f, 0.6337f, 1f),
                Color(0.3564f, 0.0038f, 0.6478f, 1f),
                Color(0.4055f, 0.0007f, 0.6570f, 1f),
                Color(0.4596f, 0.0036f, 0.6603f, 1f),
                Color(0.5065f, 0.0163f, 0.6562f, 1f),
                Color(0.5517f, 0.0431f, 0.6453f, 1f),
                Color(0.5950f, 0.0772f, 0.6279f, 1f),
                Color(0.6360f, 0.1121f, 0.6052f, 1f),
                Color(0.6792f, 0.1518f, 0.5752f, 1f),
                Color(0.7149f, 0.1873f, 0.5463f, 1f),
                Color(0.7483f, 0.2227f, 0.5168f, 1f),
                Color(0.7796f, 0.2581f, 0.4875f, 1f),
                Color(0.8126f, 0.2979f, 0.4553f, 1f),
                Color(0.8402f, 0.3336f, 0.4275f, 1f),
                Color(0.8661f, 0.3697f, 0.4001f, 1f),
                Color(0.8903f, 0.4064f, 0.3731f, 1f),
                Color(0.9155f, 0.4488f, 0.3429f, 1f),
                Color(0.9356f, 0.4877f, 0.3160f, 1f),
                Color(0.9534f, 0.5280f, 0.2889f, 1f),
                Color(0.9685f, 0.5697f, 0.2617f, 1f),
                Color(0.9806f, 0.6130f, 0.2346f, 1f),
                Color(0.9899f, 0.6638f, 0.2049f, 1f),
                Color(0.9941f, 0.7107f, 0.1801f, 1f),
                Color(0.9939f, 0.7593f, 0.1591f, 1f),
                Color(0.9886f, 0.8096f, 0.1454f, 1f),
                Color(0.9763f, 0.8680f, 0.1434f, 1f),
                Color(0.9593f, 0.9214f, 0.1516f, 1f),
                Color(0.9400f, 0.9752f, 0.1313f, 1f)
        )
        val VIRIDIS = ColorGradient(
                Color(0.2670f, 0.0049f, 0.3294f, 1f),
                Color(0.2770f, 0.0503f, 0.3757f, 1f),
                Color(0.2823f, 0.0950f, 0.4173f, 1f),
                Color(0.2826f, 0.1409f, 0.4575f, 1f),
                Color(0.2780f, 0.1804f, 0.4867f, 1f),
                Color(0.2693f, 0.2188f, 0.5096f, 1f),
                Color(0.2573f, 0.2561f, 0.5266f, 1f),
                Color(0.2412f, 0.2965f, 0.5397f, 1f),
                Color(0.2259f, 0.3308f, 0.5473f, 1f),
                Color(0.2105f, 0.3637f, 0.5522f, 1f),
                Color(0.1959f, 0.3954f, 0.5553f, 1f),
                Color(0.1823f, 0.4262f, 0.5571f, 1f),
                Color(0.1681f, 0.4600f, 0.5581f, 1f),
                Color(0.1563f, 0.4896f, 0.5579f, 1f),
                Color(0.1448f, 0.5191f, 0.5566f, 1f),
                Color(0.1337f, 0.5485f, 0.5535f, 1f),
                Color(0.1235f, 0.5817f, 0.5474f, 1f),
                Color(0.1194f, 0.6111f, 0.5390f, 1f),
                Color(0.1248f, 0.6405f, 0.5271f, 1f),
                Color(0.1433f, 0.6695f, 0.5112f, 1f),
                Color(0.1807f, 0.7014f, 0.4882f, 1f),
                Color(0.2264f, 0.7289f, 0.4628f, 1f),
                Color(0.2815f, 0.7552f, 0.4326f, 1f),
                Color(0.3441f, 0.7800f, 0.3974f, 1f),
                Color(0.4129f, 0.8030f, 0.3573f, 1f),
                Color(0.4966f, 0.8264f, 0.3064f, 1f),
                Color(0.5756f, 0.8446f, 0.2564f, 1f),
                Color(0.6576f, 0.8602f, 0.2031f, 1f),
                Color(0.7414f, 0.8734f, 0.1496f, 1f),
                Color(0.8353f, 0.8860f, 0.1026f, 1f),
                Color(0.9162f, 0.8961f, 0.1007f, 1f),
                Color(0.9932f, 0.9062f, 0.1439f, 1f)
        )
    }
}