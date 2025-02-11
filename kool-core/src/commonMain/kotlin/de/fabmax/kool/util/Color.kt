package de.fabmax.kool.util

import de.fabmax.kool.math.*
import de.fabmax.kool.util.Color.Companion.GAMMA_LINEAR_TO_sRGB
import de.fabmax.kool.util.Color.Companion.GAMMA_sRGB_TO_LINEAR
import kotlin.math.*

/**
 * @author fabmax
 */

fun Vec3f.toColor(alpha: Float = 1f): Color = Color(x, y, z, alpha)
fun Vec4f.toColor(): Color = Color(x, y, z, w)

open class Color(open val r: Float, open val g: Float, open val b: Float, open val a: Float = 1f) {

    val rgb: Vec3f
        get() = Vec3f(r, g, b)

    /**
     * This color's perceived brightness (assuming sRGB color space): r * 0.299f + g * 0.587f + b * 0.114f.
     */
    val brightness: Float get() = r * 0.299f + g * 0.587f + b * 0.114f

    /**
     * This color's perceived brightness (assuming linear color space): r * 0.2126f + g * 0.7152f + b * 0.0722f.
     */
    val luminanceLinear: Float get() = r * 0.2126f + g * 0.7152f + b * 0.0722f

    constructor(other: Color) : this(other.r, other.g, other.b, other.a)
    constructor(rgb: Vec3f, a: Float) : this(rgb.x, rgb.y, rgb.z, a)

    /**
     * Linearly interpolates this and another color and returns the result as an (optionally provided)
     * [MutableColor]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: Color, weight: Float, result: MutableColor = MutableColor()): MutableColor {
        result.r = that.r * weight + r * (1f - weight)
        result.g = that.g * weight + g * (1f - weight)
        result.b = that.b * weight + b * (1f - weight)
        result.a = that.a * weight + a * (1f - weight)
        return result
    }

    /**
     * Returns a new color with this color's rgb channels scaled by the given factor and an unchanged alpha value.
     */
    fun mulRgb(factor: Float, result: MutableColor = MutableColor()): MutableColor {
        return result.set(this).mulRgb(factor)
    }

    /**
     * Returns a new color with this color's rgb channels and the given alpha value.
     */
    fun withAlpha(alpha: Float, result: MutableColor = MutableColor()): MutableColor {
        return result.set(r, g, b, alpha)
    }

    /**
     * Converts this color to linear space, assuming that it is in sRGB space. Same as calling [applyGamma] with
     * [GAMMA_sRGB_TO_LINEAR].
     */
    fun toLinear(result: MutableColor = MutableColor()): MutableColor = applyGamma(GAMMA_sRGB_TO_LINEAR, result)

    /**
     * Converts this color to sRGB space, assuming that it is in linear space. Same as calling [applyGamma] with
     * [GAMMA_LINEAR_TO_sRGB].
     */
    fun toSrgb(result: MutableColor = MutableColor()): MutableColor = applyGamma(GAMMA_LINEAR_TO_sRGB, result)

    /**
     * Applies the given gamma value to this color and returns the result as an (optionally provided) [MutableColor].
     * @see toLinear
     * @see toSrgb
     */
    fun applyGamma(gamma: Float, result: MutableColor = MutableColor()): MutableColor {
        return result.set(r.pow(gamma), g.pow(gamma), b.pow(gamma), a)
    }

    /**
     * Translates this color from Srgb into HSV color space.
     */
    fun toHsv(): Hsv {
        val min = min(r, min(g, b)).clamp()
        val max = max(r, max(g, b)).clamp()

        // value
        val v = max

        val delta = max - min
        return if (delta < 0.001f) {
            // saturation is 0, hue is undefined (we set it to 0)
            Hsv(0f, 0f, v)
        } else {
            // saturation
            val s = delta / max

            // hue
            var h = 60f * if (r >= max) {
                (g.clamp() - b.clamp()) / delta
            } else if (g >= max) {
                2f + (b.clamp() - r.clamp()) / delta
            } else {
                4f + (r.clamp() - g.clamp()) / delta
            }
            if (h < 0f) {
                h += 360f
            }
            Hsv(h, s, v)
        }
    }

    /**
     * Translates this color from linear rgb into Oklab color space.
     */
    fun toOklab(): Oklab {
        val l = (0.4122215f * r + 0.5363326f * g + 0.0514460f * b).pow(1/3f)
        val m = (0.2119035f * r + 0.6806995f * g + 0.1073970f * b).pow(1/3f)
        val s = (0.0883025f * r + 0.2817189f * g + 0.6299787f * b).pow(1/3f)

        return Oklab(
            l = 0.2104543f * l + 0.7936178f * m - 0.0040720f * s,
            a = 1.9779985f * l - 2.4285922f * m + 0.4505937f * s,
            b = 0.0259040f * l + 0.7827718f * m - 0.8086758f * s
        )
    }

    /**
     * Returns this color encoded in a hex string of the format "rrggbbaa" (if inclAlpha is true - the default) or
     * "rrggbb" (if inclAlpha is false).
     */
    fun toHexString(inclAlpha: Boolean = true): String {
        var hr = (r * 255).roundToInt().clamp(0, 255).toString(16)
        var hg = (g * 255).roundToInt().clamp(0, 255).toString(16)
        var hb = (b * 255).roundToInt().clamp(0, 255).toString(16)
        var ha = (a * 255).roundToInt().clamp(0, 255).toString(16)
        if (hr.length == 1) hr = "0$hr"
        if (hg.length == 1) hg = "0$hg"
        if (hb.length == 1) hb = "0$hb"
        if (ha.length == 1) ha = "0$ha"
        return if (inclAlpha) "$hr$hg$hb$ha" else "$hr$hg$hb"
    }

    fun toVec4f(): Vec4f = Vec4f(r, g, b, a)

    fun toMutableVec4f(result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(r, g, b, a)

    override fun toString(): String = "($r, $g, $b, $a)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Color) return false
        return r == other.r && g == other.g && b == other.b && a == other.a
    }

    override fun hashCode(): Int {
        var result = r.hashCode()
        result = 31 * result + g.hashCode()
        result = 31 * result + b.hashCode()
        result = 31 * result + a.hashCode()
        return result
    }

    /**
     * Appends the components of this [Color] to the given [Float32Buffer].
     */
    fun putTo(target: Float32Buffer) {
        target.put(r)
        target.put(g)
        target.put(b)
        target.put(a)
    }

    /**
     * Appends the components of this [Color] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(r)
        target.putFloat32(g)
        target.putFloat32(b)
        target.putFloat32(a)
    }

    /**
     * Appends the components of this [Color] to the given [Uint8Buffer].
     */
    fun putTo(target: Uint8Buffer) {
        target.put((r * 255).roundToInt().clamp(0, 255).toUByte())
        target.put((g * 255).roundToInt().clamp(0, 255).toUByte())
        target.put((b * 255).roundToInt().clamp(0, 255).toUByte())
        target.put((a * 255).roundToInt().clamp(0, 255).toUByte())
    }

    companion object {
        const val GAMMA_sRGB_TO_LINEAR = 2.2f
        const val GAMMA_LINEAR_TO_sRGB = 1f / 2.2f

        val BLACK = Color(0.0f, 0.0f, 0.0f, 1.0f)
        val DARK_GRAY = Color(0.25f, 0.25f, 0.25f, 1.0f)
        val GRAY = Color(0.5f, 0.5f, 0.5f, 1.0f)
        val LIGHT_GRAY = Color(0.75f, 0.75f, 0.75f, 1.0f)
        val WHITE = Color(1.0f, 1.0f, 1.0f, 1.0f)

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

        /**
         * Parses the given hex string to a [Color]. Valid hex strings may have an optional leading '#' followed by up
         * to 8 hex characters, containing the r, g, b, a values.
         */
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
    /**
     * Color in HSV (hue, saturation, value) color space. Hue is in degrees (range: 0..360), saturation and value
     * are normalized (range: 0..1).
     */
    data class Hsv(val h: Float, val s: Float, val v: Float) {

        /**
         * Returns a new Hsv color with the hue shifted by the given amount.
         * This is equivalent to Hsv(h + hueShift, s, v).
         */
        fun shiftHue(hueShift: Float): Hsv {
            val shifted = (h + hueShift) % 360f
            return Hsv(if (shifted < 0f) shifted + 360f else shifted, s, v)
        }

        /**
         * Returns a new Hsv color with the saturation shifted by the given amount.
         * This is equivalent to Hsv(h, s + satShift, v).
         */
        fun shiftSaturation(satShift: Float): Hsv {
            return Hsv(h, (s + satShift).clamp(), v)
        }

        /**
         * Returns a new Hsv color with the value shifted by the given amount.
         * This is equivalent to Hsv(h, s, v + valShift).
         */
        fun shiftValue(valShift: Float): Hsv {
            return Hsv(h, s, (v + valShift).clamp())
        }

        fun toSrgb(result: MutableColor = MutableColor(), a: Float = 1f): MutableColor {
            var hue = h % 360f
            if (hue < 0) {
                hue += 360f
            }
            val hi = (hue / 60.0f).toInt()
            val f = hue / 60.0f - hi
            val p = v * (1 - s)
            val q = v * (1 - s * f)
            val t = v * (1 - s * (1 - f))

            return when (hi) {
                1 -> result.set(q, v, p, a)
                2 -> result.set(p, v, t, a)
                3 -> result.set(p, q, v, a)
                4 -> result.set(t, p, v, a)
                5 -> result.set(v, p, q, a)
                else -> result.set(v, t, p, a)
            }
        }

        fun toLinearRgb(result: MutableColor = MutableColor(), a: Float = 1f): MutableColor {
            return toSrgb(result, a).toLinear(result)
        }
    }

    /**
     * Color in Oklab colorspace: https://bottosson.github.io/posts/oklab/
     * - l: Lightness
     * - a: green / red
     * - b: blue / yellow
     */
    data class Oklab(val l: Float, val a: Float, val b: Float) {
        val chroma: Float
            get() = sqrt(a * a + b * b)
        val hue: Float
            get() = atan2(b, a).toDeg()

        /**
         * Returns a new Oklab color with the hue shifted by the given amount.
         */
        fun shiftHue(hueShift: Float): Oklab {
            return fromHueChroma(l, hue + hueShift, chroma)
        }

        /**
         * Returns a new Oklab color with the chroma shifted by the given amount.
         */
        fun shiftChroma(chromaShift: Float): Oklab {
            return fromHueChroma(l, hue, chroma + chromaShift)
        }

        /**
         * Returns a new Oklab color with the lightness shifted by the given amount.
         */
        fun shiftLightness(lightnessShift: Float): Oklab {
            return Oklab(l + lightnessShift, a, b)
        }

        fun toLinearRgb(result: MutableColor = MutableColor(), a: Float = 1f): MutableColor {
            val l = this.l + 0.39633778f * this.a + 0.2158038f * b
            val m = this.l - 0.10556135f * this.a - 0.0638542f * b
            val s = this.l - 0.08948418f * this.a - 1.2914855f * b

            val lt = l * l * l
            val mt = m * m * m
            val st = s * s * s

            return result.set(
                r = (+4.076742f * lt - 3.3077116f * mt + 0.2309700f * st).clamp(),
                g = (-1.268438f * lt + 2.6097574f * mt - 0.3413194f * st).clamp(),
                b = (-0.004196f * lt - 0.7034186f * mt + 1.7076147f * st).clamp(),
                a = a
            )
        }

        fun toSrgb(result: MutableColor = MutableColor(), a: Float = 1f): MutableColor {
            return toLinearRgb(result, a).toSrgb(result)
        }

        companion object {
            fun fromHueChroma(l: Float, hue: Float, chroma: Float): Oklab {
                val rad = hue.toRad()
                val a = chroma * cos(rad)
                val b = chroma * sin(rad)
                return Oklab(l, a, b)
            }
        }
    }
}

fun Color(hex: String): Color = Color.fromHex(hex)

open class MutableColor(override var r: Float, override var g: Float, override var b: Float, override var a: Float) : Color(r, g, b, a) {

    constructor() : this(0f, 0f, 0f, 1f)
    constructor(color: Color) : this(color.r, color.g, color.b, color.a)
    constructor(rgb: Vec3f, a: Float) : this(rgb.x, rgb.y, rgb.z, a)

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

    fun set(rgb: Vec3f, a: Float): MutableColor {
        r = rgb.x
        g = rgb.y
        b = rgb.z
        this.a = a
        return this
    }

    fun set(that: Vec4f): MutableColor {
        r = that.x
        g = that.y
        b = that.z
        a = that.w
        return this
    }

    /**
     * Inplace operation: Multiplies this color's rgb channels by the given factor leaving the alpha channel unchanged.
     */
    fun mulRgb(factor: Float): MutableColor {
        r *= factor
        g *= factor
        b *= factor
        return this
    }
}
