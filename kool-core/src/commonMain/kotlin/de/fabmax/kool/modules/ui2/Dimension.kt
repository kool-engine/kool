package de.fabmax.kool.modules.ui2

import kotlin.jvm.JvmInline

sealed interface Dimension

object WrapContent : Dimension {
    override fun toString(): String {
        return "WrapContent"
    }
}

class Grow(val weight: Float, val min: Dimension = Dp.ZERO, val max: Dimension = Dp.UNBOUNDED) : Dimension {
    override fun toString(): String {
        return "Grow(weight=$weight)"
    }

    companion object {
        val Std = Grow(1f)
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
        val UNBOUNDED = Dp(1e9f)
    }
}
