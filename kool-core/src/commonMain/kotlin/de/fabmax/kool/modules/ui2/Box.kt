package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface ColumnScope: UiScope {
    fun divider(
        color: Color = colors.secondaryVariant,
        horizontalMargin: Dp = sizes.gap,
        marginTop: Dp = Dp.ZERO,
        marginBottom: Dp = Dp.ZERO,
        thickness: Dp = sizes.borderWidth
    ) {
        Box(Grow.Std, thickness) {
            modifier
                .backgroundColor(color)
                .alignX(AlignmentX.Center)
                .margin(start = horizontalMargin, end = horizontalMargin, top = marginTop, bottom = marginBottom)
        }
    }
}

interface RowScope: UiScope {
    fun divider(
        color: Color = colors.secondaryVariant,
        verticalMargin: Dp = sizes.gap,
        marginStart: Dp = Dp.ZERO,
        marginEnd: Dp = Dp.ZERO,
        thickness: Dp = sizes.borderWidth
    ) {
        Box(thickness, Grow.Std) {
            modifier
                .backgroundColor(color)
                .alignY(AlignmentY.Center)
                .margin(start = marginStart, end = marginEnd, top = verticalMargin, bottom = verticalMargin)
        }
    }
}

inline fun UiScope.Column(
    width: Dimension = FitContent,
    height: Dimension = FitContent,
    scopeName: String? = null,
    block: ColumnScope.() -> Unit
): ColumnScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val column = uiNode.createChild(scopeName, ColumnNode::class, ColumnNode.factory)
    column.modifier.size(width, height).layout(ColumnLayout)
    column.block()
    return column
}

inline fun UiScope.ReverseColumn(
    width: Dimension = FitContent,
    height: Dimension = FitContent,
    scopeName: String? = null,
    block: ColumnScope.() -> Unit
): ColumnScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val column = uiNode.createChild(scopeName, ColumnNode::class, ColumnNode.factory)
    column.modifier.size(width, height).layout(ReverseColumnLayout)
    column.block()
    return column
}

inline fun UiScope.Row(
    width: Dimension = FitContent,
    height: Dimension = FitContent,
    scopeName: String? = null,
    block: RowScope.() -> Unit
): RowScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val row = uiNode.createChild(scopeName, RowNode::class, RowNode.factory)
    row.modifier.size(width, height).layout(RowLayout)
    row.block()
    return row
}

inline fun UiScope.ReverseRow(
    width: Dimension = FitContent,
    height: Dimension = FitContent,
    scopeName: String? = null,
    block: RowScope.() -> Unit
): RowScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val row = uiNode.createChild(scopeName, RowNode::class, RowNode.factory)
    row.modifier.size(width, height).layout(ReverseRowLayout)
    row.block()
    return row
}

inline fun UiScope.Box(
    width: Dimension = FitContent,
    height: Dimension = FitContent,
    scopeName: String? = null,
    block: UiScope.() -> Unit
): UiScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val box = uiNode.createChild(scopeName, BoxNode::class, BoxNode.factory)
    box.modifier.size(width, height)
    box.block()
    return box
}

open class BoxNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), UiScope {
    override val modifier = UiModifier(surface)

    companion object {
        val factory: (UiNode, UiSurface) -> BoxNode = { parent, surface -> BoxNode(parent, surface) }
    }
}

open class ColumnNode(parent: UiNode?, surface: UiSurface) : BoxNode(parent, surface), ColumnScope {
    override val modifier = UiModifier(surface)

    companion object {
        val factory: (UiNode, UiSurface) -> ColumnNode = { parent, surface -> ColumnNode(parent, surface) }
    }
}

open class RowNode(parent: UiNode?, surface: UiSurface) : BoxNode(parent, surface), RowScope {
    override val modifier = UiModifier(surface)

    companion object {
        val factory: (UiNode, UiSurface) -> RowNode = { parent, surface -> RowNode(parent, surface) }
    }
}
