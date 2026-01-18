package de.fabmax.kool.modules.compose.modifiers

import androidx.compose.runtime.Stable
import de.fabmax.kool.modules.ui2.*
import me.dvyy.compose.mini.modifier.Modifier


@Stable
fun Modifier.size(size: Dimension) = size(size, size)

@Stable
fun Modifier.size(x: Dimension, y: Dimension) = edit<UiModifier> { it.size(x, y) }

fun Modifier.fillMaxWidth(weight: Float = 1f) = width(Grow(weight))
fun Modifier.fillMaxHeight(weight: Float = 1f) = height(Grow(weight))
fun Modifier.fillMaxSize(weight: Float = 1f) = size(Grow(weight))

@Stable
fun Modifier.width(width: Dimension) = edit<UiModifier> { it.width(width) }

@Stable
fun Modifier.height(height: Dimension) = edit<UiModifier> { it.height(height) }

@Stable
fun Modifier.layout(layout: Layout) = edit<UiModifier> { it.layout(layout) }

private fun Dp.orElse(value: Dp) = if (this == Dp.UNBOUNDED) value else this

@Stable
fun Modifier.padding(
    start: Dp = Dp.UNBOUNDED,
    end: Dp = Dp.UNBOUNDED,
    top: Dp = Dp.UNBOUNDED,
    bottom: Dp = Dp.UNBOUNDED,
) = edit<UiModifier> {
    it.padding(
        start = start.orElse(it.paddingStart),
        end = end.orElse(it.paddingEnd),
        top = top.orElse(it.paddingTop),
        bottom = bottom.orElse(it.paddingBottom)
    )
}

@Stable
fun Modifier.padding(horizontal: Dp = Dp.UNBOUNDED, vertical: Dp = Dp.UNBOUNDED) =
    padding(horizontal, horizontal, vertical, vertical)

@Stable
fun Modifier.padding(all: Dp) = padding(all, all, all, all)

@Stable
fun Modifier.margin(all: Dp) = margin(all, all, all, all)

@Stable
fun Modifier.margin(horizontal: Dp = Dp.UNBOUNDED, vertical: Dp = Dp.UNBOUNDED) =
    margin(start = horizontal, end = horizontal, top = vertical, bottom = vertical)

@Stable
fun Modifier.margin(
    start: Dp = Dp.UNBOUNDED,
    end: Dp = Dp.UNBOUNDED,
    top: Dp = Dp.UNBOUNDED,
    bottom: Dp = Dp.UNBOUNDED,
) = edit<UiModifier> {
    it.margin(
        start = start.orElse(it.marginStart),
        end = end.orElse(it.marginEnd),
        top = top.orElse(it.marginTop),
        bottom = bottom.orElse(it.marginBottom)
    )
}

@Stable
fun Modifier.align(alignmentX: AlignmentX, alignmentY: AlignmentY) = edit<UiModifier> {
    it.align(alignmentX, alignmentY)
}

@Stable
fun Modifier.alignX(alignmentX: AlignmentX) = edit<UiModifier> {
    it.alignX(alignmentX)
}

@Stable
fun Modifier.alignY(alignmentY: AlignmentY) = edit<UiModifier> {
    it.alignY(alignmentY)
}

