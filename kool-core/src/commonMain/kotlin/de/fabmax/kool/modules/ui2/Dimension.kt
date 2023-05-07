package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.clamp
import kotlin.jvm.JvmInline

sealed interface Dimension

object FitContent : Dimension {
    override fun toString(): String {
        return "FitContent"
    }
}

data class Grow(val weight: Float, val min: Dimension = Dp.ZERO, val max: Dimension = Dp.UNBOUNDED) : Dimension {
    fun clampPx(px: Float, contentSize: Float): Float {
        val minPx = if (min is Dp) min.px else contentSize
        val maxPx = if (max is Dp) max.px else contentSize
        return px.clamp(minPx, maxPx)
    }

    companion object {
        val Std = Grow(1f)
        val MinFit = Grow(1f, FitContent)
    }
}

@JvmInline
value class Dp(val value: Float): Dimension, Comparable<Dp> {

    val px: Float get() = value * UiScale.measuredScale

    operator fun plus(other: Dp): Dp = Dp(value + other.value)
    operator fun minus(other: Dp): Dp = Dp(value - other.value)
    operator fun times(factor: Float): Dp = Dp(value * factor)
    operator fun times(factor: Int): Dp = Dp(value * factor)

    override fun compareTo(other: Dp): Int = value.compareTo(other.value)

    override fun toString(): String {
        return "Dp($value)"
    }

    companion object {
        val ZERO = Dp(0f)
        val UNBOUNDED = Dp(1e9f)

        fun fromPx(px: Float) = Dp(px / UiScale.measuredScale)
    }
}