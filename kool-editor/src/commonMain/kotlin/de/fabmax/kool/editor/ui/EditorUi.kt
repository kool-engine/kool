package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.Colors
import de.fabmax.kool.modules.ui2.Dimension
import de.fabmax.kool.modules.ui2.Sizes
import de.fabmax.kool.modules.ui2.docking.Dock
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.modules.ui2.setupUiScene
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont

class EditorUi(val editor: KoolEditor) : Scene("EditorMenu") {

    val dock = Dock()

    val sceneBrowser = SceneBrowser(this)
    val objectProperties = ObjectProperties(this)
    val assetBrowser = AssetBrowser(this)

    init {
        setupUiScene()

        addNode(dock)
        dock.createNodeLayout(
            listOf(
                "0:row",
                "0/0:leaf",
                "0/1:col",
                "0/2:leaf",
                "0/1/0:leaf",
                "0/1/1:leaf",
            )
        )

        val centerSpacer = UiDockable("EmptyDockable", dock, isHidden = true)
        dock.getLeafAtPath("0/1/0")?.dock(centerSpacer)

        // add scene browser panel and dock it to the left side of the screen
        dock.addDockableSurface(sceneBrowser.windowDockable, sceneBrowser.windowSurface)
        dock.getLeafAtPath("0/0")?.dock(sceneBrowser.windowDockable)

        // add object properties panel and dock it to the right side of the screen
        dock.addDockableSurface(objectProperties.windowDockable, objectProperties.windowSurface)
        dock.getLeafAtPath("0/2")?.dock(objectProperties.windowDockable)

        dock.addDockableSurface(assetBrowser.windowDockable, assetBrowser.windowSurface)
        dock.getLeafAtPath("0/1/1")?.dock(assetBrowser.windowDockable)
    }

    companion object {
        val EDITOR_THEME_COLORS = Colors.darkColors()
    }
}

val Sizes.lineHeight: Dimension get() = largeGap * 1.3f

val Sizes.boldText: MsdfFont get() = (normalText as MsdfFont).copy(weight = 0.075f)

val Colors.hoverBg: Color get() = secondaryVariantAlpha(0.35f)
val Colors.selectionBg: Color get() = secondaryVariantAlpha(0.5f)
