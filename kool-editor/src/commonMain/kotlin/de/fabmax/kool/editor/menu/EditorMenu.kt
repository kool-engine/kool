package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.Colors
import de.fabmax.kool.modules.ui2.DockingHost
import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.setupUiScene
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.MdColor

class EditorMenu(val editor: KoolEditor) : Scene("EditorMenu") {

    private val dockingHost = DockingHost()
    private val sceneBrowser = SceneBrowser(editor)

    init {
        setupUiScene()
        addNode(dockingHost)

        // add scene browser and dock it to the left side of the screen
        dockingHost.addNode(sceneBrowser.windowSurface)
        dockingHost.dockWindow(sceneBrowser.windowScope, listOf(DockingHost.DockPosition.Start to Dp(300f)))
    }

    companion object {
        val EDITOR_THEME_COLORS = Colors.singleColorDark(MdColor.LIME)
    }
}