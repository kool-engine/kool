package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.math.clamp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * @author fabmax
 */

@Suppress("unused")
open class Color(r: Float, g: Float, b: Float, a: Float = 1f) : Vec4f(r, g, b, a) {

    constructor(other: Color) : this(other.r, other.g, other.b, other.a)

    open val r get() = this[0]
    open val g get() = this[1]
    open val b get() = this[2]
    open val a get() = this[3]

    val brightness: Float
        get() = 0.299f * r + 0.587f * g + 0.114f * b

    fun mix(other: Color, weight: Float, result: MutableColor = MutableColor()): MutableColor {
        result.r = other.r * weight + r * (1f - weight)
        result.g = other.g * weight + g * (1f - weight)
        result.b = other.b * weight + b * (1f - weight)
        result.a = other.a * weight + a * (1f - weight)
        return result
    }

    fun scaleRgb(factor : Float, result: MutableColor = MutableColor()): MutableColor {
        return result.set(this).scaleRgb(factor)
    }

    fun withAlpha(alpha: Float): MutableColor {
        return MutableColor(r, g, b, alpha)
    }

    fun toLinear(): MutableColor = gamma(GAMMA_sRGB_TO_LINEAR)

    fun toLinear(result: MutableColor): MutableColor = gamma(GAMMA_sRGB_TO_LINEAR, result)

    fun toSrgb(): MutableColor = gamma(GAMMA_LINEAR_TO_sRGB)

    fun toSrgb(result: MutableColor): MutableColor = gamma(GAMMA_LINEAR_TO_sRGB, result)

    fun gamma(gamma: Float, result: MutableColor = MutableColor()): MutableColor {
        return result.set(r.pow(gamma), g.pow(gamma), b.pow(gamma), a)
    }

    fun toGray(): Float {
        return r * 0.299f + g * 0.587f + b * 0.114f
    }

    /**
     * Returns this color is HSV color space:
     * [result].x = hue [0..360]
     * [result].y = saturation [0..1]
     * [result].z = value [0..1]
     */
    fun toHsv(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        val min = min(r, min(g, b)).clamp()
        val max = max(r, max(g, b)).clamp()

        // value
        result.z = max

        val delta = max - min
        if (delta < 0.001f) {
            // saturation is 0, hue is undefined (we set it to 0)
            result.set(0f, 0f, max)
        } else {
            // saturation
            result.y = delta / max

            // hue
            result.x = 60f * if (r >= max) {
                (g.clamp() - b.clamp()) / delta
            } else if (g >= max) {
                2f + (b.clamp() - r.clamp()) / delta
            } else {
                4f + (r.clamp() - g.clamp()) / delta
            }
            if (result.x < 0f) {
                result.x += 360f
            }
        }
        return result
    }

    fun toHexString(inclAlpha: Boolean = true): String {
        var hr = (r * 255).roundToInt().clamp(0, 255).toString(16)
        var hg = (g * 255).roundToInt().clamp(0, 255).toString(16)
        var hb = (b * 255).roundToInt().clamp(0, 255).toString(16)
        var ha = (a * 255).roundToInt().clamp(0, 255).toString(16)
        if (hr.length == 1) hr = "0$hr"
        if (hg.length == 1) hg = "0$hg"
        if (hb.length == 1) hb = "0$hb"
        if (ha.length == 1) ha = "0$ha"
        if (inclAlpha) {
            return "$hr$hg$hb$ha"
        } else {
            return "$hr$hg$hb"
        }
    }

    companion object {
        const val GAMMA_sRGB_TO_LINEAR = 2.2f
        const val GAMMA_LINEAR_TO_sRGB = 1f / 2.2f

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

        fun fromHsv(h: Float, s: Float, v: Float, a: Float): Color {
            return MutableColor().setHsv(h, s, v, a)
        }

        fun fromHex(hex: String): Color {
            if (hex.isEmpty()) {
                return BLACK
            }

            var str = hex
            if (str[0] == '#') {
                str = str.substring(1)
            }
            if (str.length > 8) {
                str = str.substring(0, 8)
            }

            val r: Float
            var g = 0f
            var b = 0f
            var a = 1f
            try {
                when (str.length) {
                    1 -> {
                        val r4 = str.toInt(16)
                        r = (r4 or (r4 shl 4)) / 255f
                    }
                    2 -> {
                        val r4 = str.substring(0, 1).toInt(16)
                        val g4 = str.substring(1, 2).toInt(16)
                        r = (r4 or (r4 shl 4)) / 255f
                        g = (g4 or (g4 shl 4)) / 255f
                    }
                    3 -> {
                        val r4 = str.substring(0, 1).toInt(16)
                        val g4 = str.substring(1, 2).toInt(16)
                        val b4 = str.substring(2, 3).toInt(16)
                        r = (r4 or (r4 shl 4)) / 255f
                        g = (g4 or (g4 shl 4)) / 255f
                        b = (b4 or (b4 shl 4)) / 255f

                    }
                    4 -> {
                        val r4 = str.substring(0, 1).toInt(16)
                        val g4 = str.substring(1, 2).toInt(16)
                        val b4 = str.substring(2, 3).toInt(16)
                        val a4 = str.substring(2, 3).toInt(16)
                        r = (r4 or (r4 shl 4)) / 255f
                        g = (g4 or (g4 shl 4)) / 255f
                        b = (b4 or (b4 shl 4)) / 255f
                        a = (a4 or (a4 shl 4)) / 255f

                    }
                    5 -> {
                        r = str.substring(0, 2).toInt(16) / 255f
                        g = str.substring(2, 4).toInt(16) / 255f
                        b = str.substring(4, 5).toInt(16) / 255f
                    }
                    6 -> {
                        // parse rgb
                        r = str.substring(0, 2).toInt(16) / 255f
                        g = str.substring(2, 4).toInt(16) / 255f
                        b = str.substring(4, 6).toInt(16) / 255f
                    }
                    else -> {
                        // parse rgba
                        r = str.substring(0, 2).toInt(16) / 255f
                        g = str.substring(2, 4).toInt(16) / 255f
                        b = str.substring(4, 6).toInt(16) / 255f
                        a = str.substring(6).toInt(16) / 255f
                    }
                }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid color code: $hex", e)
            }
            return Color(r, g, b, a)
        }

        fun fromHexOrNull(hex: String): Color? {
            return try {
                fromHex(hex)
            } catch (e: Exception) {
                null
            }
        }
    }
}

fun Color(hex: String): Color = Color.fromHex(hex)

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
