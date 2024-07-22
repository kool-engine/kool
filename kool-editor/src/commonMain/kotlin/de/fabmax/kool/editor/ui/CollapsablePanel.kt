package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*

fun UiScope.collapsablePanel(
    title: String,
    imageIcon: IconProvider? = null,
    headerContent: (RowScope.() -> Unit)? = null,
    titleWidth: Dimension = Grow.Std,
    startExpanded: Boolean = true,
    onCollapseChanged: ((Boolean) -> Unit)? = null,
    block: ColumnScope.() -> Any?
) = Column(
    Grow.Std,
    scopeName = title
) {
    var isExpanded by remember(startExpanded)
    var isHovered by remember(false)

    Row(width = Grow.Std, height = sizes.lineHeightLarger) {
        modifier
            .backgroundColor(if (isHovered) colors.hoverBg else null)
            .onEnter { isHovered = true }
            .onExit { isHovered = false }
            .onClick {
                isExpanded = !isExpanded
                onCollapseChanged?.invoke(isExpanded)
            }

        Arrow (if (isExpanded) 90f else 0f) {
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
    if (isExpanded) {
        Column(width = Grow.Std) {
            modifier
                .padding(/*start = sizes.largeGap,*/ bottom = sizes.smallGap)
                .backgroundColor(colors.backgroundVariant)
            block()
        }
    }
}

fun UiScope.collapsablePanelLvl2(
    title: String,
    headerContent: (RowScope.() -> Unit)? = null,
    titleWidth: Dimension = Grow.Std,
    startExpanded: Boolean = true,
    onCollapseChanged: ((Boolean) -> Unit)? = null,
    block: ColumnScope.() -> Any?
) = Column(
    Grow.Std,
    scopeName = title
) {
    var isExpanded by remember(startExpanded)
    var isHovered by remember(false)

    Row(width = Grow.Std, height = sizes.lineHeightLarger) {
        modifier
            .backgroundColor(if (isHovered) colors.hoverBg else null)
            .onEnter { isHovered = true }
            .onExit { isHovered = false }
            .onClick {
                isExpanded = !isExpanded
                onCollapseChanged?.invoke(isExpanded)
            }

        Arrow (if (isExpanded) 90f else 0f) {
            modifier
                .colors(colors.secondaryVariant, colors.secondary)
                .margin(start = sizes.gap, end = sizes.smallGap)
                .alignY(AlignmentY.Center)
        }
        Text(title) {
            modifier
                .width(titleWidth)
                .alignY(AlignmentY.Center)
        }
        headerContent?.invoke(this)
    }
    if (isExpanded) {
        Column(width = Grow.Std) {
            modifier
                .padding(start = sizes.largeGap, bottom = sizes.smallGap)
                .backgroundColor(colors.backgroundVariant)
            block()
        }
    }
}