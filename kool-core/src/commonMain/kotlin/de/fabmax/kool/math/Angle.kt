package de.fabmax.kool.math

import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

val Float.deg: AngleF get() = AngleF(this / 180f * PI_F)
val Float.rad: AngleF get() = AngleF(this)
fun AngleF.toAngleD() = AngleD(rad.toDouble())

val Double.deg: AngleD get() = AngleD(this / 180.0 * PI)
val Double.rad: AngleD get() = AngleD(this)
fun AngleD.toAngleF() = AngleF(rad.toFloat())

// <template> Changes made within the template section will also affect the other type variants of this class

@JvmInline
value class AngleF(val rad: Float) {
    val deg: Float get() = rad / PI_F * 180f

    val sin: Float get() = sin(rad)
    val cos: Float get() = cos(rad)
    val tan: Float get() = tan(rad)

    operator fun plus(that: AngleF): AngleF = AngleF(rad + that.rad)
    operator fun minus(that: AngleF): AngleF = AngleF(rad - that.rad)
    operator fun unaryMinus(): AngleF = AngleF(-rad)
    operator fun times(that: Float): AngleF = AngleF(rad * that)
    operator fun times(that: Int): AngleF = AngleF(rad * that)
    operator fun div(that: Float): AngleF = AngleF(rad / that)
    operator fun div(that: Int): AngleF = AngleF(rad / that)
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


@JvmInline
value class AngleD(val rad: Double) {
    val deg: Double get() = rad / PI * 180.0

    val sin: Double get() = sin(rad)
    val cos: Double get() = cos(rad)
    val tan: Double get() = tan(rad)

    operator fun plus(that: AngleD): AngleD = AngleD(rad + that.rad)
    operator fun minus(that: AngleD): AngleD = AngleD(rad - that.rad)
    operator fun unaryMinus(): AngleD = AngleD(-rad)
    operator fun times(that: Double): AngleD = AngleD(rad * that)
    operator fun times(that: Int): AngleD = AngleD(rad * that)
    operator fun div(that: Double): AngleD = AngleD(rad / that)
    operator fun div(that: Int): AngleD = AngleD(rad / that)
}
