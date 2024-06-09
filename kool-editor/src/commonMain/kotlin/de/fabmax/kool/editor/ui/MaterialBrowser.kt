package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.DeleteMaterialAction
import de.fabmax.kool.modules.ui2.UiScope

class MaterialBrowser(ui: EditorUi) : BrowserPanel("Material Browser", IconMap.medium.palette, ui) {

    override fun UiScope.collectBrowserDirs(traversedPaths: MutableSet<String>) {
        val materialDir = browserItems.getOrPut("/materials") {
            BrowserDir(0, "Materials", "/materials")
        } as BrowserDir
        expandedDirTree += materialDir
        traversedPaths += "/materials"

        materialDir.children.clear()
        editor.projectModel.materials.use().forEach {
            it.dataState.use()
            val materialItem = browserItems.getOrPut("/materials/${it.name}") {
                BrowserMaterialItem(1, it)
            }
            materialDir.children += materialItem
            traversedPaths += materialItem.path
        }
    }

    override fun makeItemPopupMenu(item: BrowserItem, isTreeItem: Boolean): SubMenuItem<BrowserItem>? {
        return if (item is BrowserMaterialItem) {
            SubMenuItem {
                item("Delete material") {
                    OkCancelTextDialog("Delete Material", "Delete material \"${item.name}\"?") {
                        DeleteMaterialAction(item.material).apply()
                    }
                }
            }
        } else {
            null
        }
    }
}