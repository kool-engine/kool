package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.UiDockable

abstract class EditorPanel(
    name: String,
    val ui: EditorUi,
    defaultWidth: Dp = ui.dock.dockingSurface.sizes.baseSize * 8,
    defaultHeight: Dp = ui.dock.dockingSurface.sizes.baseSize * 8) {

    val editor: KoolEditor get() = ui.editor

    val windowDockable = UiDockable(name, ui.dock)

    abstract val windowSurface: UiSurface

    init {
        windowDockable.setFloatingBounds(width = defaultWidth, height = defaultHeight)
    }

    protected fun EditorPanelWindow(block: UiScope.() -> Unit) = WindowSurface(
        windowDockable,
        colors = EditorUi.EDITOR_THEME_COLORS,
        borderColor = { UiColors.border }
    ) {
        modifier.backgroundColor(colors.background)
        block()
    }
}