package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.Dock
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont

class EditorUi(val editor: KoolEditor) : Scene("EditorMenu") {

    val dock = Dock()

    val sceneBrowser = SceneBrowser(this)
    val objectProperties = ObjectProperties(this)

    init {
        setupUiScene()

        addNode(dock)
        dock.createNodeLayout(
            listOf(
                "0:row",
                "0:row/0:leaf",
                "0:row/1:leaf",
                "0:row/2:leaf"
            )
        )

        dock.dockingPaneComposable = Composable {
//            Box(Grow.Std, Grow.Std) {
//                modifier.margin(start =48.dp)
                dock.root()
//            }
        }

        // add scene browser panel and dock it to the left side of the screen
        dock.addDockableSurface(sceneBrowser.windowBounds, sceneBrowser.windowSurface)
        dock.getLeafAtPath("0/0")?.dock(sceneBrowser.windowBounds)

        // add object properties panel and dock it to the right side of the screen
        dock.addDockableSurface(objectProperties.windowBounds, objectProperties.windowSurface)
        dock.getLeafAtPath("0/2")?.dock(objectProperties.windowBounds)
    }

    companion object {
        val EDITOR_THEME_COLORS = Colors.darkColors()
    }
}

val Sizes.lineHeight: Dimension get() = largeGap * 1.3f

val Sizes.boldText: MsdfFont get() = (normalText as MsdfFont).copy(weight = 0.075f)

val Colors.hoverBg: Color get() = secondaryVariantAlpha(0.35f)
val Colors.selectionBg: Color get() = secondaryVariantAlpha(0.5f)
