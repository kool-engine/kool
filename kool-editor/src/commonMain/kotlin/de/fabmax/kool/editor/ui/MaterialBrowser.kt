package de.fabmax.kool.editor.ui

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.actions.DeleteMaterialAction
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.util.ThumbnailRenderer
import de.fabmax.kool.editor.util.ThumbnailState
import de.fabmax.kool.editor.util.materialThumbnail
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.scene.Lighting
import kotlin.math.roundToInt

class MaterialBrowser(ui: EditorUi) : BrowserPanel("Material Browser", IconMap.medium.palette, ui) {

    private val thumbnailRenderer = ThumbnailRenderer("material-thumbnails")
    private val materialThumbnails = BrowserThumbnails<MaterialComponent>(thumbnailRenderer) { thumbnailRenderer.materialThumbnail(it) }

    private var prevActiveScene: EditorScene? = null

    init {
        KoolSystem.requireContext().backgroundPasses += thumbnailRenderer
        thumbnailRenderer.lighting = Lighting().apply {
            singleDirectionalLight { setup(Vec3f(-1f, -1f, -1f)) }
        }

        ui.editor.projectModel.listenerComponents += MaterialComponent.ListenerComponent { component, _ ->
            materialThumbnails.getThumbnail(component)?.state?.set(ThumbnailState.USABLE_OUTDATED)
        }
    }

    context(UiScope)
    override fun titleBar() {
        super.titleBar()

        thumbnailRenderer.updateTileSize(sizes.browserItemSize.px.roundToInt())
        if (ui.editor.activeScene.use() != prevActiveScene) {
            prevActiveScene = ui.editor.activeScene.value
            materialThumbnails.loadedThumbnails.values.forEach { it.thumbnail?.state?.set(ThumbnailState.USABLE_OUTDATED) }
        }
    }

    override fun UiScope.collectBrowserDirs(traversedPaths: MutableSet<String>) {
        val materialDir = browserItems.getOrPut("/materials") {
            BrowserDir(0, "Materials", "/materials")
        } as BrowserDir
        expandedDirTree += materialDir
        traversedPaths += "/materials"

        materialDir.children.clear()
        editor.projectModel.materials.use()
            .filter { it.gameEntity.isVisible }
            .forEach { material ->
                material.dataState.use()
                val materialItem = browserItems.getOrPut("/materials/${material.name}") {
                    BrowserMaterialItem(1, material).apply {
                        composable = materialThumbnails.getThumbnailComposable(material)
                    }
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