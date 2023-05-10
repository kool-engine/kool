package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.modules.ui2.docking.UiDockable

abstract class EditorPanel(name: String, val ui: EditorUi, defaultWidth: Dp = Dp(300f), defaultHeight: Dp = Dp(600f)) {

    val windowDockable = UiDockable(name, ui.dock)

    abstract val windowSurface: UiSurface

    init {
        windowDockable.setFloatingBounds(width = defaultWidth, height = defaultHeight)
    }

}