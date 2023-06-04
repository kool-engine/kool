package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*

fun UiScope.collapsapsablePanel(
    title: String,
    headerContent: (RowScope.() -> Unit)? = null,
    block: ColumnScope.() -> Any
) = Column(
    Grow.Std,
    scopeName = title
) {
    var isCollapsed by remember(false)
    var isHovered by remember(false)

    Row(width = Grow.Std, height = sizes.lineHeightLarger) {
        modifier
            .backgroundColor(if (isHovered) colors.hoverBg else null)
            .onClick { isCollapsed = !isCollapsed }
            .onEnter { isHovered = true }
            .onExit { isHovered = false }

        Arrow (if (isCollapsed) 0f else 90f) {
            modifier
                .margin(horizontal = sizes.gap)
                .alignY(AlignmentY.Center)
        }
        Text(title) {
            modifier.alignY(AlignmentY.Center)
        }
        headerContent?.invoke(this)
    }
    if (!isCollapsed) {
        Column(width = Grow.Std) {
            modifier
                .padding(start = sizes.largeGap, bottom = sizes.smallGap)
                .backgroundColor(colors.backgroundVariant)
            block()
        }
    }
}