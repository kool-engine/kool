package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.UiScope

class BehaviorBrowser(ui: EditorUi) : BrowserPanel("Behavior Browser", IconMap.medium.CODE, ui) {

    override fun UiScope.collectBrowserDirs(traversedPaths: MutableSet<String>) {
        val scriptDir = browserItems.getOrPut("/behaviors") {
            BrowserDir(0, "Behaviors", "/behaviors")
        } as BrowserDir
        expandedDirTree += scriptDir
        traversedPaths += "/behaviors"

        scriptDir.children.clear()
        EditorState.loadedApp.value?.behaviorClasses?.values?.forEach {
            val scriptItem = browserItems.getOrPut("/behaviors/${it.qualifiedName}") {
                BrowserBehaviorItem(1, it)
            }
            scriptDir.children += scriptItem
            traversedPaths += scriptItem.path
        }
    }

    override fun makeItemPopupMenu(item: BrowserItem, isTreeItem: Boolean): SubMenuItem<BrowserItem>? {
        return if (item is BrowserBehaviorItem) {
            SubMenuItem {
                item("Edit") { KoolEditor.instance.editBehaviorSource(item.behavior) }
            }
        } else {
            null
        }
    }

    override fun onItemDoubleClick(item: BrowserItem) {
        if (item is BrowserBehaviorItem) {
            KoolEditor.instance.editBehaviorSource(item.behavior)
        }
    }
}