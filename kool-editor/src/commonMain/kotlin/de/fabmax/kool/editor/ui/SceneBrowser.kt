package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*

class SceneBrowser(ui: EditorUi) : EditorPanel("Scene Browser", ui) {

    private val sceneObjectTree = SceneObjectTree(this)

    override val windowSurface: UiSurface = WindowSurface(
        windowBounds,
        colors = EditorUi.EDITOR_THEME_COLORS
    ) {
        modifier.backgroundColor(colors.background.withAlpha(0.8f))

        Column(Grow.Std, Grow.Std) {
            TitleBar(windowBounds)
            sceneObjectTree()
        }
    }

    fun refreshSceneTree() {
        sceneObjectTree.refreshSceneTree()
    }

    //override fun UiScope.windowContent() = sceneObjectTree()
}