package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*

fun UiScope.collapsapsablePanel(title: String, scopeName: String? = null, block: ColumnScope.() -> Any) = Column(
    Grow.Std,
    scopeName = scopeName
) {
    var isCollapsed by remember(false)
    var isHovered by remember(false)

    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier
            .backgroundColor(colors.secondaryVariantAlpha(if (isHovered) 0.5f else 0.25f))
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
    }
    if (!isCollapsed) {
        block()
    } else {
        divider(colors.secondaryVariantAlpha(0.75f), horizontalMargin = 0.dp, thickness = 1.dp)
    }
}