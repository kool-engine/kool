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
    override fun toString(): String {
        return "Dp($value)"
    }

    companion object {
        val ZERO = Dp(0f)
    }
}
