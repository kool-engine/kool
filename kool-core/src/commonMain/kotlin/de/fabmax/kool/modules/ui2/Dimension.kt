package de.fabmax.kool.modules.ui2

import kotlin.jvm.JvmInline

sealed interface Dimension

object WrapContent : Dimension {
    override fun toString(): String {
        return "WrapContent"
    }
}

@JvmInline
value class Grow(val weight: Float = 1f) : Dimension {
    override fun toString(): String {
        return "Grow(weight=$weight)"
    }
}

@JvmInline
value class Dp(val value: Float): Dimension {
    operator fun plus(other: Dp): Dp = Dp(value + other.value)
    operator fun minus(other: Dp): Dp = Dp(value - other.value)
    operator fun times(factor: Float): Dp = Dp(value * factor)

    override fun toString(): String {
        return "Dp($value)"
    }

    companion object {
        val ZERO = Dp(0f)
    }
}
