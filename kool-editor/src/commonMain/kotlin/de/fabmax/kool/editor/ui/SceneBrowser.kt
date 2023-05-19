package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.Column
import de.fabmax.kool.modules.ui2.Grow
import de.fabmax.kool.modules.ui2.UiSurface

class SceneBrowser(ui: EditorUi) : EditorPanel("Scene Browser", ui) {

    private val sceneObjectTree = SceneObjectTree(this)

    override val windowSurface: UiSurface = EditorPanelWindow {
        Column(Grow.Std, Grow.Std) {
            EditorTitleBar(windowDockable)
            sceneObjectTree()
        }
    }

    fun refreshSceneTree() {
        sceneObjectTree.refreshSceneTree()
    }
}