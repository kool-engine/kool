package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.modules.ui2.docking.DockableBounds

abstract class EditorPanel(name: String, val ui: EditorUi, defaultWidth: Dp = Dp(300f), defaultHeight: Dp = Dp(600f)) {

    val windowBounds = DockableBounds(name, ui.dock)

    abstract val windowSurface: UiSurface



//    val windowSurface = WindowSurface(
//        windowBounds,
//        colors = EditorUi.EDITOR_THEME_COLORS,
//        backgroundColor = EditorUi.EDITOR_THEME_COLORS.backgroundAlpha(0.8f)
//    ) {
//        Column(Grow.Std, Grow.Std) {
//            TitleBar(windowBounds)
//            windowContent()
//        }
//    }

    init {
        windowBounds.setFloatingBounds(width = defaultWidth, height = defaultHeight)
    }

//    protected abstract fun UiScope.windowContent(): Any

}