package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

open class UiModifier {
    var width: Dimension = WrapContent
    var height: Dimension = WrapContent
    var layout: Layout = CellLayout
    var background: Color? = null

    var paddingStart: Dp = Dp.ZERO
    var paddingEnd: Dp = Dp.ZERO
    var paddingTop: Dp = Dp.ZERO
    var paddingBottom: Dp = Dp.ZERO

    var marginStart: Dp = Dp.ZERO
    var marginEnd: Dp = Dp.ZERO
    var marginTop: Dp = Dp.ZERO
    var marginBottom: Dp = Dp.ZERO

    var alignX = AlignmentX.Start
    var alignY = AlignmentY.Top

    val pointerCallbacks = PointerCallbacks()

    open fun resetDefaults() {
        width = WrapContent
        height = WrapContent
        layout = CellLayout
        background = null

        paddingStart = Dp.ZERO
        paddingEnd = Dp.ZERO
        paddingTop = Dp.ZERO
        paddingBottom = Dp.ZERO

        marginStart = Dp.ZERO
        marginEnd = Dp.ZERO
        marginTop = Dp.ZERO
        marginBottom = Dp.ZERO

        alignX = AlignmentX.Start
        alignY = AlignmentY.Top

        pointerCallbacks.resetDefaults()
    }
}

fun <T: UiModifier> T.width(width: Dimension): T { this.width = width; return this }
fun <T: UiModifier> T.height(height: Dimension): T { this.height = height; return this }
fun <T: UiModifier> T.layout(layout: Layout): T { this.layout = layout; return this }
fun <T: UiModifier> T.background(color: Color?): T { background = color; return this }

fun <T: UiModifier> T.alignX(alignment: AlignmentX): T { alignX = alignment; return this }
fun <T: UiModifier> T.alignY(alignment: AlignmentY): T { alignY = alignment; return this }

fun <T: UiModifier> T.padding(all: Dp): T {
    paddingStart = all
    paddingEnd = all
    paddingTop = all
    paddingBottom = all
    return this
}

fun <T: UiModifier> T.padding(
    start: Dp = paddingStart,
    end: Dp = paddingEnd,
    top: Dp = paddingTop,
    bottom: Dp = paddingBottom
): T {
    paddingStart = start
    paddingEnd = end
    paddingTop = top
    paddingBottom = bottom
    return this
}

fun <T: UiModifier> T.margin(all: Dp): T {
    marginStart = all
    marginEnd = all
    marginTop = all
    marginBottom = all
    return this
}

fun <T: UiModifier> T.margin(
    start: Dp = marginStart,
    end: Dp = marginEnd,
    top: Dp = marginTop,
    bottom: Dp = marginBottom
): T {
    marginStart = start
    marginEnd = end
    marginTop = top
    marginBottom = bottom
    return this
}

enum class AlignmentX {
    Start,
    Center,
    End
}

enum class AlignmentY {
    Top,
    Center,
    Bottom
}

