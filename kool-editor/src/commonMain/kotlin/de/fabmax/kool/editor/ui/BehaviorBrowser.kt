package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.PointerEvent
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.isLeftDoubleClick
import de.fabmax.kool.modules.ui2.isRightClick

class BehaviorBrowser(ui: EditorUi) : BrowserPanel("Behavior Browser", Icons.medium.code, ui) {

    override fun UiScope.collectBrowserDirs(traversedPaths: MutableSet<String>) {
        val scriptDir = browserItems.getOrPut("/behaviors") {
            BrowserDir(0, "Behaviors", "/behaviors")
        } as BrowserDir
        expandedDirTree += scriptDir
        traversedPaths += "/behaviors"

        scriptDir.children.clear()
        editor.loadedApp.value?.behaviorClasses?.values?.forEach {
            val scriptItem = browserItems.getOrPut("/behaviors/${it.qualifiedName}") {
                BrowserBehaviorItem(1, it)
            }
            scriptDir.children += scriptItem
            traversedPaths += scriptItem.path
        }
    }

    override fun onItemClick(item: BrowserItem, ev: PointerEvent): SubMenuItem<BrowserItem>? {
        val behaviorItem = item as? BrowserBehaviorItem
        return when {
            behaviorItem != null && ev.isLeftDoubleClick -> {
                KoolEditor.instance.editBehaviorSource(item.behavior)
                null
            }
            behaviorItem != null && ev.isRightClick -> SubMenuItem {
                item("Edit") { KoolEditor.instance.editBehaviorSource(item.behavior) }
            }
            else -> super.onItemClick(item, ev)
        }
    }
}