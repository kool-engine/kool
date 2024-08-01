package de.fabmax.kool.editor.ui

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*

class ContextPopupMenu<T: Any?>(scopeName: String, hideOnOutsideClick: Boolean = true) :
    AutoPopup(hideOnOutsideClick = hideOnOutsideClick, scopeName = scopeName)
{

    private val menu = mutableStateOf<SubMenuItem<T>?>(null)

    private var contextItem = mutableStateOf<ContextItemHolder<T>?>(null)

    init {
        popupContent = Composable {
            val item = contextItem.use()
            val rootMenu = menu.use()
            if (item != null && rootMenu != null) {
                modifier
                    .layout(CellLayout)
                    .backgroundColor(null)

                menuList(rootMenu.menuItems.use(), item.item, Dp.ZERO, Dp.ZERO, modifier.zLayer)
            }
        }
    }

    fun show(screenPosPx: Vec2f, menu: SubMenuItem<T>, contextItem: T) {
        super.show(screenPosPx)
        this.menu.set(menu)
        this.contextItem.set(ContextItemHolder(contextItem))
    }

    override fun hide() {
        super.hide()
        contextItem.set(null)
    }

    private fun UiScope.menuList(items: List<ContextMenuItem<T>>, contextItem: T, x: Dp, y: Dp, z: Int) {
        var subMenu by remember<SubMenuItem<T>?>(null)
        var subMenuNode by remember<UiNode?>(null)
        val withIcons = items.any { (it is MenuItem && it.icon != null) || (it is SubMenuItem && it.icon != null) }

        menuColumn(x, y, z) {
            items.forEach { item ->
                when (item) {
                    is MenuItem -> {
                        Row(width = Grow.Std) {
                            var isHovered by remember(false)
                            modifier
                                .padding(vertical = sizes.smallGap * 0.5f)
                                .onEnter {
                                    isHovered = true
                                    subMenu = null
                                    subMenuNode = null
                                }
                                .onExit { isHovered = false }
                                .onClick {
                                    item.action.invoke(contextItem)
                                    hide()
                                }

                            if (isHovered) {
                                modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                            }

                            iconBox(withIcons, item.icon)
                            Text(item.label) {
                                modifier
                                    .width(Grow.MinFit)
                                    .alignY(AlignmentY.Center)
                                    .margin(start = if(withIcons) sizes.smallGap else sizes.gap, end = sizes.gap)
                            }
                        }
                    }
                    is SubMenuItem -> {
                        Row(width = Grow.Std) {
                            var isHovered by remember(false)
                            modifier
                                .padding(vertical = sizes.smallGap * 0.5f)
                                .onEnter {
                                    isHovered = true
                                    subMenu = item
                                    subMenuNode = uiNode
                                }
                                .onExit { isHovered = false }

                            if (isHovered || subMenu == item) {
                                modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                            }

                            iconBox(withIcons, item.icon)
                            Text(item.label ?: "Sub menu") {
                                modifier
                                    .width(Grow.MinFit)
                                    .alignY(AlignmentY.Center)
                                    .margin(start = if(withIcons) sizes.smallGap else sizes.gap, end = sizes.gap)
                            }
                            Arrow {
                                modifier
                                    .alignY(AlignmentY.Center)
                                    .margin(end = sizes.smallGap)
                            }
                        }
                    }
                    is Divider -> {
                        menuDivider(
                            marginStart = sizes.gap,
                            marginEnd = sizes.gap,
                            marginTop = sizes.smallGap * 0.5f,
                            marginBottom = sizes.smallGap * 0.5f
                        )
                    }
                }
            }
        }

        val subMenuItems = subMenu?.menuItems?.use()
        val anchorNode = subMenuNode
        if (!subMenuItems.isNullOrEmpty() && anchorNode != null) {
            val subPos = uiNode.toLocal(anchorNode.rightPx, anchorNode.topPx)
            menuList(
                subMenuItems,
                contextItem,
                Dp.fromPx(subPos.x) - sizes.smallGap,
                Dp.fromPx(subPos.y) - sizes.smallGap,
                z + UiSurface.LAYER_POPUP
            )
        }
    }

    private fun UiScope.iconBox(withIcons: Boolean, icon: IconProvider?) {
        if (withIcons) {
            if (icon != null) {
                Image {
                    modifier
                        .margin(start = sizes.smallGap)
                        .alignY(AlignmentY.Center)
                        .iconImage(icon, UiColors.titleText)
                }
            } else {
                val sz = Icons.small.iconSize
                Box(sz, sz) { modifier.margin(start = sizes.smallGap) }
            }
        }
    }

    private inline fun UiScope.menuColumn(x: Dp, y: Dp, z: Int, block: ColumnScope.() -> Unit) = Column {
        modifier
            .margin(start = x, top = y)
            .background(RoundRectBackground(colors.backgroundMid, sizes.smallGap))
            .border(RoundRectBorder(colors.secondaryVariant, sizes.smallGap, sizes.borderWidth))
            .padding(sizes.smallGap)
            .zLayer(z)
        block()
    }

    private class ContextItemHolder<T>(val item: T)
}

sealed class ContextMenuItem<T: Any?>

class Divider<T: Any?> : ContextMenuItem<T>()

class MenuItem<T: Any?>(
    val label: String,
    val icon: IconProvider?,
    val action: ((T) -> Unit),
) : ContextMenuItem<T>()

class SubMenuItem<T: Any?>(val label: String?, val icon: IconProvider?) : ContextMenuItem<T>() {
    val menuItems: MutableStateList<ContextMenuItem<T>> = mutableStateListOf()

    fun item(label: String, icon: IconProvider? = null, action: (T) -> Unit) {
        menuItems += MenuItem(label, icon, action)
    }

    fun subMenu(label: String, icon: IconProvider? = null, block: SubMenuItem<T>.() -> Unit) {
        val subMenu = SubMenuItem<T>(label, icon)
        subMenu.block()
        menuItems += subMenu
    }

    fun divider() {
        menuItems += Divider()
    }
}

fun <T: Any?> SubMenuItem(label: String? = null, icon: IconProvider? = null, block: SubMenuItem<T>.() -> Unit): SubMenuItem<T> {
    val menu = SubMenuItem<T>(label, icon)
    menu.block()
    return menu
}
