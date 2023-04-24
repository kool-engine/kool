package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.modules.ui2.WindowScope

abstract class EditorPanel(val editor: KoolEditor) {

    val menu: EditorUi get() = editor.menu

    abstract val windowSurface: UiSurface
    abstract val windowScope: WindowScope

}