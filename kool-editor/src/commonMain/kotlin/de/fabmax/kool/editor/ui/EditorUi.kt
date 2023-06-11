package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.math.Vec2d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.Dock
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont

class EditorUi(val editor: KoolEditor) : Scene("EditorMenu") {

    val dock = Dock()
    val statusBar = PanelSurface(colors = EDITOR_THEME_COLORS) {
        modifier
            .alignY(AlignmentY.Bottom)
            .size(Grow.Std, sizes.baseSize)
            .backgroundColor(UiColors.bgMid)
        statusBar()
    }

    val sceneBrowser = SceneBrowser(this)
    val objectProperties = ObjectPropertyEditor(this)
    val resourceBrowser = ResourceBrowser(this)

    val appLoaderState = mutableStateOf("")

    val centerSlot = UiDockable("Center slot", dock, isHidden = true)

    init {
        setupUiScene()

        dock.dockingSurface.colors = EDITOR_THEME_COLORS
        dock.dockingPaneComposable = Composable {
            Column(Grow.Std, Grow.Std) {
                modifier.margin(bottom = sizes.baseSize)
                dock.root()
            }
        }

        addNode(statusBar)
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

        dock.getLeafAtPath("0/1/0")?.dock(centerSlot)

        // add scene browser panel and dock it to the left side of the screen
        dock.addDockableSurface(sceneBrowser.windowDockable, sceneBrowser.windowSurface)
        dock.getLeafAtPath("0/0")?.dock(sceneBrowser.windowDockable)

        // add object properties panel and dock it to the right side of the screen
        dock.addDockableSurface(objectProperties.windowDockable, objectProperties.windowSurface)
        dock.getLeafAtPath("0/2")?.dock(objectProperties.windowDockable)

        dock.addDockableSurface(resourceBrowser.windowDockable, resourceBrowser.windowSurface)
        dock.getLeafAtPath("0/1/1")?.dock(resourceBrowser.windowDockable)
    }

    private fun UiScope.statusBar() = Row(width = Grow.Std, height = sizes.lineHeightLarger) {
        Box(width = Grow.Std) {  }

        Box(width = sizes.baseSize * 8f, height = Grow.Std) {
            Text(appLoaderState.use()) {
                modifier.alignY(AlignmentY.Center)
            }
        }
    }

    companion object {
        val EDITOR_THEME_COLORS = Colors.darkColors(
            background = Color("232933ff"),
            backgroundVariant = Color("161a20ff"),
            onBackground = Color("dbe6ffff"),
            secondary = Color("7786a5ff"),
            secondaryVariant = Color("4d566bff"),
            onSecondary = Color.WHITE
        )
    }
}

val Sizes.baseSize: Dp get() = largeGap * 2f
val Sizes.lineHeight: Dp get() = baseSize * (2f/3f)
val Sizes.lineHeightLarger: Dp get() = baseSize * 0.9f
val Sizes.lineHeightTitle: Dp get() = baseSize
val Sizes.smallTextFieldPadding: Dp get() = smallGap * 0.75f

val Sizes.boldText: MsdfFont get() = (normalText as MsdfFont).copy(weight = 0.075f)
val Sizes.italicText: MsdfFont get() = (normalText as MsdfFont).copy(italic = MsdfFont.ITALIC_STD)

// weak hovered background: hovered list items, hovered collapsable panel header
val Colors.hoverBg: Color get() = secondaryVariantAlpha(0.35f)

val Colors.weakComponentBg: Color get() = secondaryAlpha(0.15f)
val Colors.weakComponentBgHovered: Color get() = secondaryAlpha(0.25f)

// text fields, combo-boxes, right side slider track
val Colors.componentBg: Color get() = secondaryAlpha(0.25f)
// focused text field, hovered combo-boxes
val Colors.componentBgHovered: Color get() = secondaryAlpha(0.5f)

// buttons, combo-box expander, left side slider track
val Colors.elevatedComponentBg: Color get() = secondaryVariant
// hovered buttons / cb expander
val Colors.elevatedComponentBgHovered: Color get() = secondary

object UiColors {
    val border = Color("0f1114ff")
    val titleBg = Color("343a49ff")
    val bgMid = EditorUi.EDITOR_THEME_COLORS.background.mix(EditorUi.EDITOR_THEME_COLORS.backgroundVariant, 0.5f)
    val titleText =  Color("dbe6ffff")
}

object DragChangeRates {
    const val RANGE_0_TO_1 = 0.005
    const val POSITION = 0.01
    const val SCALE = 0.01
    const val ROTATION = 0.1
    const val SIZE = 0.01

    val SIZE_VEC2 = Vec2d(SIZE)
    val SIZE_VEC3 = Vec3d(SIZE)
    val POSITION_VEC3 = Vec3d(POSITION)
    val SCALE_VEC3 = Vec3d(SCALE)
    val ROTATION_VEC3 = Vec3d(ROTATION)
}
