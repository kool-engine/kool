package de.fabmax.kool.util

/**
 * @author fabmax
 */

open class Color(r: Float, g: Float, b: Float, a: Float = 1f) : Vec4f(r, g, b, a) {

    open var r: Float
        get() = x
        protected set(value) { x = value }

    open var g: Float
        get() = y
        protected set(value) { y = value }

    open var b: Float
        get() = z
        protected set(value) { z = value }

    open var a: Float
        get() = w
        protected set(value) { w = value }

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

        fun fromHsv(h: Float, s: Float, v: Float, a: Float): Color {
            val color = MutableColor()
            return color.setHsv(h, s, v, a)
        }
    }
}

open class MutableColor(r: Float, g: Float, b: Float, a: Float) : Color(r, g, b, a) {
    override var r: Float
        get() = super.r
        public set(value) { super.r = value }

    override var g: Float
        get() = super.g
        public set(value) { super.g = value }

    override var b: Float
        get() = super.b
        public set(value) { super.b = value }

    override var a: Float
        get() = super.a
        public set(value) { super.a = value }

    constructor() : this(0f, 0f, 0f, 1f)

    constructor(color: Color) : this(color.r, color.g, color.b, color.a)

    fun add(other: Color, weight: Float) {
        r += other.r * weight
        g += other.g * weight
        b += other.b * weight
        a += other.a * weight
    }

    fun clear() {
        set(0f, 0f, 0f, 0f)
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
