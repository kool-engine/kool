package de.fabmax.kool.modules.ui2

import kotlin.jvm.JvmInline

sealed interface Dimension

object WrapContent : Dimension

@JvmInline
value class Grow(val weight: Float = 1f) : Dimension

@JvmInline
value class Dp(val value: Float): Dimension {
    companion object {
        val ZERO = Dp(0f)
    }
}
