package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*

class SceneBrowser(editor: KoolEditor) {

    private val windowState = WindowState().apply { setWindowSize(Dp(300f), Dp(600f)) }
    private val sceneObjectTree = SceneObjectTree(editor)

    val windowSurface: UiSurface = Window(
        windowState,
        colors = EditorMenu.EDITOR_THEME_COLORS,
        name = "Scene Browser"
    ) {
        modifier.backgroundColor(colors.background.withAlpha(0.8f))

        TitleBar()

        sceneObjectTree()
    }

    val windowScope: WindowScope = windowSurface.windowScope!!
}