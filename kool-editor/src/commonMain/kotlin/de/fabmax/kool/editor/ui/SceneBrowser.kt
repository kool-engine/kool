package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*

class SceneBrowser(ui: EditorUi) : EditorPanel("Scene Browser", ui) {

    private val sceneObjectTree = SceneObjectTree(this)

    override val windowSurface: UiSurface = WindowSurface(
        windowDockable,
        colors = EditorUi.EDITOR_THEME_COLORS
    ) {
        modifier.backgroundColor(colors.backgroundAlpha(0.8f))

        Column(Grow.Std, Grow.Std) {
            TitleBar(windowDockable)
            sceneObjectTree()
        }
    }

    fun refreshSceneTree() {
        sceneObjectTree.refreshSceneTree()
    }
}