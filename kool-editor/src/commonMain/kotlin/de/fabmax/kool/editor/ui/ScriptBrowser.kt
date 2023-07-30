package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.modules.ui2.UiScope

class ScriptBrowser(ui: EditorUi) : BrowserPanel("Script Browser", IconMap.medium.CODE, ui) {

    override fun UiScope.collectBrowserDirs(traversedPaths: MutableSet<String>) {
        val scriptDir = browserItems.getOrPut("/scripts") {
            BrowserDir(0, "Scripts", "/scripts")
        } as BrowserDir
        expandedDirTree += scriptDir
        traversedPaths += "/scripts"

        scriptDir.children.clear()
        EditorState.loadedApp.value?.scriptClasses?.values?.forEach {
            val scriptItem = browserItems.getOrPut("/scripts/${it.qualifiedName}") {
                BrowserScriptItem(1, it)
            }
            scriptDir.children += scriptItem
            traversedPaths += scriptItem.path
        }
    }

    override fun makeItemPopupMenu(item: BrowserItem, isTreeItem: Boolean): SubMenuItem<BrowserItem>? {
        return null
    }
}