package de.fabmax.kool.demo.menu

import de.fabmax.kool.demo.Demos
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class DemoListContent(val menu: DemoMenu) : ComposableComponent {

    private val nonHiddenDemoItems = mutableListOf<DemoItem>()
    private val allDemoItems = mutableListOf<DemoItem>()

    private val hoveredDemoItem = mutableStateOf(-1)
    private val demoSelectionScrollState = LazyListState()

    init {
        Demos.categories.forEach { cat ->
            val titleItem = DemoItem(cat.title, cat.colorSet.getColor(0f), true, null)
            allDemoItems += titleItem
            if (!cat.isHidden) {
                nonHiddenDemoItems += titleItem
            }

            cat.entries.forEachIndexed { i, entry ->
                val demoItem = DemoItem(
                    entry.title,
                    cat.colorSet.getColor((i + 1f) / cat.entries.size),
                    false,
                    entry
                )
                allDemoItems += demoItem
                if (!cat.isHidden) {
                    nonHiddenDemoItems += demoItem
                }
            }
        }
    }

    override fun UiScope.compose() {
        Column {
            modifier
                .height(Grow.Std)
                .width(Grow.Std)

            LazyList(
                demoSelectionScrollState,
                containerModifier = { it.background(null) },
                vScrollbarModifier = { it.colors(color = Color.WHITE.withAlpha(0.2f), hoverColor = Color.WHITE.withAlpha(0.4f)) }
            ) {
                val hoveredIndex = hoveredDemoItem.use()
                val demoItems = if (menu.showHiddenDemos.use()) allDemoItems else nonHiddenDemoItems
                itemsIndexed(demoItems) { i, item ->
                    Text(item.text) {
                        modifier
                            .width(Grow.Std)
                            .height(DemoMenu.itemSize.dp)
                            .padding(horizontal = sizes.gap)
                            .textAlignY(AlignmentY.Center)
                            .onEnter { hoveredDemoItem.set(i) }
                            .onExit { hoveredDemoItem.set(-1) }
                            .onClick {
                                if (!item.isTitle) {
                                    item.demo?.let { menu.demoLoader.loadDemo(it) }
                                    menu.isExpanded = false
                                }
                            }

                        if (item.isTitle) {
                            categoryTitleStyle(item)
                        } else {
                            demoEntryStyle(item, hoveredIndex == i)
                        }
                    }
                }
            }
        }
    }

    private fun TextScope.demoEntryStyle(item: DemoItem, isHovered: Boolean) {
        if (isHovered) {
            modifier
                .backgroundColor(item.color.withAlpha(0.7f))
                .textColor(Color.WHITE)
        } else {
            modifier
                .textColor(item.color)
        }
    }

    private fun TextScope.categoryTitleStyle(item: DemoItem) {
        modifier
            .backgroundColor(item.color)
            .textColor(Color.WHITE)
            .font(sizes.largeText)
    }

    private class DemoItem(val text: String, val color: Color, val isTitle: Boolean, val demo: Demos.Entry?)
}