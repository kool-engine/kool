package de.fabmax.kool.demo.menu

import de.fabmax.kool.demo.Demos
import de.fabmax.kool.demo.Settings
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont

class DemoListContent(val menu: DemoMenu) : Composable {
    private val nonHiddenDemoItems = mutableListOf<DemoItem>()
    private val allDemoItems = mutableListOf<DemoItem>()

    init {
        Demos.categories.forEach { cat ->
            val titleItem = DemoItem(cat.title, cat.getCategoryColor(0f), true, cat, null)
            allDemoItems += titleItem
            if (!cat.isHidden) {
                nonHiddenDemoItems += titleItem
            }

            cat.entries.forEach { entry ->
                val demoItem = DemoItem(
                    entry.title,
                    entry.color,
                    false,
                    cat,
                    entry
                )
                allDemoItems += demoItem
                if (!cat.isHidden) {
                    nonHiddenDemoItems += demoItem
                }
            }
        }
    }

    override fun UiScope.compose() = Column {
        modifier
            .height(Grow.Std)
            .width(Grow.Std)

        LazyList(
            containerModifier = { it.background(null) },
            vScrollbarModifier = { it.colors(color = colors.onBackgroundAlpha(0.2f), hoverColor = colors.onBackgroundAlpha(0.4f)) }
        ) {
            var hoveredIndex by remember(-1)
            val demoItems = (if (Settings.showHiddenDemos.use()) allDemoItems else nonHiddenDemoItems)
                .filter { it.demo?.platformFilter?.applies() != false }
                .toMutableList()
            itemsIndexed(demoItems) { i, item ->
                Text(item.text) {
                    modifier
                        .width(Grow.Std)
                        .height(UiSizes.baseSize)
                        .padding(horizontal = sizes.gap * 1.25f)
                        .textAlignY(AlignmentY.Center)
                        .onEnter { hoveredIndex = i }
                        .onExit { hoveredIndex = -1 }
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

    private fun TextScope.demoEntryStyle(item: DemoItem, isHovered: Boolean) {
        if (isHovered) {
            modifier
                .backgroundColor(item.color)
                .textColor(Color.WHITE)
        } else if (item.demo?.id == Settings.selectedDemo.value) {
            modifier
                .backgroundColor(item.color.withAlpha(0.2f))
                .textColor(Color.WHITE)
        } else {
            modifier
                .textColor(item.color.mix(Color.WHITE, 0.5f))
        }
    }

    private fun TextScope.categoryTitleStyle(item: DemoItem) {
        modifier
            .background(TitleBgRenderer(DemoMenu.titleBgMesh, item.category.fromColor, item.category.toColor))
            .textColor(Color.WHITE)
            .font((sizes.largeText as MsdfFont).copy(glowColor = DemoMenu.titleTextGlowColor))
    }

    private class DemoItem(val text: String, val color: Color, val isTitle: Boolean, val category: Demos.Category, val demo: Demos.Entry?)
}