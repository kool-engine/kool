package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*

fun UiScope.collapsapsablePanel(
    title: String,
    imageIcon: ImageIconMap.IconImageProvider? = null,
    headerContent: (RowScope.() -> Unit)? = null,
    titleWidth: Dimension = Grow.Std,
    block: ColumnScope.() -> Any?
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
                .margin(start = sizes.gap, end = if (imageIcon == null) sizes.gap else sizes.smallGap)
                .alignY(AlignmentY.Center)
        }
        imageIcon?.let {
            Image {
                modifier
                    .margin(end = sizes.smallGap)
                    .alignY(AlignmentY.Center)
                    .iconImage(it, UiColors.titleText)
            }
        }
        Text(title) {
            modifier
                .width(titleWidth)
                .alignY(AlignmentY.Center)
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