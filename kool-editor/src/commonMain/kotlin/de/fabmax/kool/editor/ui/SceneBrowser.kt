package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.Column
import de.fabmax.kool.modules.ui2.Grow
import de.fabmax.kool.modules.ui2.UiSurface

class SceneBrowser(ui: EditorUi) : EditorPanel("Scene Browser", Icons.medium.listTree, ui) {

    private val sceneObjectTree = SceneObjectTree(this)

    override val windowSurface: UiSurface = editorPanelWithPanelBar {
        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, icon)
            sceneObjectTree()
        }
    }

    fun refreshSceneTree() {
        sceneObjectTree.refreshSceneTree()
    }
}