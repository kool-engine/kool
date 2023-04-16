package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont

class EditorMenu(editor: KoolEditor) : Scene("EditorMenu") {

    private val dockingHost = DockingHost()

    val sceneBrowser = SceneBrowser(editor)
    val objectProperties = ObjectProperties(editor)

    init {
        setupUiScene()
        addNode(dockingHost)

        // add scene browser panel and dock it to the left side of the screen
        dockingHost.addNode(sceneBrowser.windowSurface)
        dockingHost.dockWindow(sceneBrowser.windowScope, listOf(DockingHost.DockPosition.Start to Dp(300f)))

        // add object properties panel and dock it to the right side of the screen
        dockingHost.addNode(objectProperties.windowSurface)
        dockingHost.dockWindow(objectProperties.windowScope, listOf(
            DockingHost.DockPosition.End to Grow.Std,
            DockingHost.DockPosition.End to Dp(300f)
        ))
    }

    companion object {
        val EDITOR_THEME_COLORS = Colors.singleColorDark(MdColor.LIGHT_BLUE)
    }
}

val Sizes.lineHeight: Dimension get() = largeGap * 1.3f

val Sizes.boldText: MsdfFont get() = (normalText as MsdfFont).copy(weight = 0.075f)
