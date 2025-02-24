@file:Suppress("UnusedReceiverParameter")

package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.Key
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.ui.componenteditors.GameEntityEditor
import de.fabmax.kool.input.CursorMode
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Vec2d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec4d
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.Dock
import de.fabmax.kool.modules.ui2.docking.DockLayout
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.*

class EditorUi(val editor: KoolEditor) : Scene("EditorMenu") {

    val uiColors = mutableStateOf(EDITOR_THEME_COLORS)
    val uiSizes = mutableStateOf(Sizes.medium)

    val uiFont = mutableStateOf(MsdfFont.DEFAULT_FONT)
    val consoleFont = mutableStateOf(MsdfFont.DEFAULT_FONT)

    val dock = Dock()
    val titleBar = WindowTitleBar(editor)
    val overlay = UiOverlay(this)

    private val virtualCursorMesh = ColorMesh().apply {
        generate {
            val lon = Dp(24f).px
            val short = Dp(2f).px
            rect {
                size.set(lon, short)
            }
            rect {
                size.set(short, lon)
            }
        }
        shader = KslUnlitShader {
            color { constColor(Color.WHITE) }
            pipeline {
                isWriteDepth = false
                depthTest = DepthCompareOp.ALWAYS
            }
        }
        val trs = TrsTransformF()
        transform = trs
        onUpdate {
            isVisible = PointerInput.cursorMode == CursorMode.LOCKED && AppState.isEditMode
            if (isVisible) {
                val w = it.viewport.width.toFloat()
                val h = it.viewport.height.toFloat()
                val ptr = PointerInput.primaryPointer
                val x = ptr.pos.x
                val y = ptr.pos.y
                trs.translation.set(((x % w) + w) % w, -((y % h) + h) % h, 0f)
                trs.markDirty()
            }
        }
    }

    private val titleBarSurface = PanelSurface {
        surface.colors = uiColors.use()
        surface.sizes = uiSizes.use()
        titleBar()
    }

    val statusBar = PanelSurface {
        surface.colors = uiColors.use()
        surface.sizes = uiSizes.use()

        modifier
            .alignY(AlignmentY.Bottom)
            .size(Grow.Std, sizes.heightTitleBar - sizes.borderWidth)
            .backgroundColor(colors.backgroundMid)

        Column(width = Grow.Std, height = Grow.Std) {
            divider(UiColors.titleBg, horizontalMargin = Dp.ZERO)
            statusBar()
        }
    }

    val sceneView = SceneView(this)
    val sceneBrowser = SceneBrowser(this)
    val objectProperties = GameEntityEditor(this)
    val assetBrowser = AssetBrowser(this)
    val materialBrowser = MaterialBrowser(this)
    val behaviorBrowser = BehaviorBrowser(this)
    val console = ConsolePanel(this)

    val appStateInfo = mutableStateOf("")
    val inputModeState = mutableStateOf("")

    val dndController = DndController(this)

    init {
        InputStack.onInputStackChanged += {
            InputStack.handlerStack.lastOrNull { it is EditorKeyListener } ?.let { handler ->
                inputModeState.set(handler.name)
            }
        }

        launchOnMainThread {
            val uiFont = MsdfFont("assets/fonts/gidole/font-gidole-regular").getOrThrow()
            this@EditorUi.uiFont.set(uiFont)

            val sz = uiSizes.value
            uiSizes.set(sz.copy(
                normalText = uiFont.copy(sizePts = sz.normalText.sizePts),
                largeText = uiFont.copy(sizePts = sz.largeText.sizePts),
            ))

            val consoleFont = MsdfFont("assets/fonts/hack/font-hack-regular").getOrThrow()
            this@EditorUi.consoleFont.set(consoleFont)
        }

        setupUiScene()

        dock.apply {
            borderWidth.set(Dp.fromPx(1f))
            borderColor.set(UiColors.titleBg)
            dockingSurface.colors = EDITOR_THEME_COLORS
            dockingPaneComposable = Composable {
                resizeMargin.set(sizes.scrollbarWidth)
                Column(Grow.Std, Grow.Std) {
                    modifier.margin(top = sizes.heightWindowTitleBar, bottom = sizes.heightTitleBar)
                    root()
                }
            }

            addDockableSurface(sceneView.windowDockable, sceneView.windowSurface)
            addDockableSurface(sceneBrowser.windowDockable, sceneBrowser.windowSurface)
            addDockableSurface(objectProperties.windowDockable, objectProperties.windowSurface)
            addDockableSurface(assetBrowser.windowDockable, assetBrowser.windowSurface)
            addDockableSurface(materialBrowser.windowDockable, materialBrowser.windowSurface)
            addDockableSurface(behaviorBrowser.windowDockable, behaviorBrowser.windowSurface)
            addDockableSurface(console.windowDockable, console.windowSurface)

            val restoredLayout = DockLayout.loadLayout("editor.ui.layout", this) { windowName ->
                when (windowName) {
                    sceneView.name -> sceneView.windowDockable
                    sceneBrowser.name -> sceneBrowser.windowDockable
                    objectProperties.name -> objectProperties.windowDockable
                    assetBrowser.name -> assetBrowser.windowDockable
                    materialBrowser.name -> materialBrowser.windowDockable
                    behaviorBrowser.name -> behaviorBrowser.windowDockable
                    console.name -> console.windowDockable
                    else -> {
                        logW { "Unable to restore layout - window not found: $windowName" }
                        null
                    }
                }
            }

            if (!restoredLayout) {
                logI { "Setting default window layout" }
                createNodeLayout(
                    listOf(
                        "0:row",
                        "0/0:col",
                        "0/0/0:row",
                        "0/0/0/0:leaf",
                        "0/0/0/1:leaf",
                        "0/0/1:leaf",
                        "0/1:leaf",
                    )
                )
                getLeafAtPath("0/0/0/0")?.dock(sceneBrowser.windowDockable)
                getLeafAtPath("0/0/0/1")?.dock(sceneView.windowDockable)
                getLeafAtPath("0/1")?.dock(objectProperties.windowDockable)
                getLeafAtPath("0/0/1")?.dock(assetBrowser.windowDockable)
                getLeafAtPath("0/0/1")?.dock(materialBrowser.windowDockable)
                getLeafAtPath("0/0/1")?.dock(behaviorBrowser.windowDockable)
                getLeafAtPath("0/0/1")?.dock(console.windowDockable)

                getLeafAtPath("0/0/1")?.bringToTop(assetBrowser.windowDockable)
            }
        }

        // add nodes in correct z-order (title bar overlaps dock)
        addNode(statusBar)
        addNode(dock)
        addNode(titleBarSurface)
        addNode(overlay)
        addNode(virtualCursorMesh)
    }

    private fun UiScope.statusBar() = Row(width = Grow.Std, height = Grow.Std) {
        Row {
            modifier
                .width(sizes.baseSize * 8)
                .alignY(AlignmentY.Center)
                .onClick { editor.ui.sceneView.isShowKeyInfo.toggle() }

            if (AppState.appModeState.use() == AppMode.EDIT) {
                Text("${inputModeState.use()}:") {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(horizontal = sizes.gap)
                }
                keyLabel(Key.Help)
                Text("for key info") {
                    modifier
                        .margin(start = sizes.gap)
                        .alignY(AlignmentY.Center)
                }
            }
        }

        divider(colors.strongDividerColor, marginStart = sizes.largeGap, marginEnd = sizes.largeGap)

        Box(width = Grow.Std) {  }

        divider(colors.strongDividerColor, marginStart = sizes.largeGap, marginEnd = sizes.largeGap)

        Text(appStateInfo.use()) {
            modifier
                .width(sizes.baseSize * 6f)
                .alignY(AlignmentY.Center)
        }
    }

    companion object {
        private val EDITOR_THEME_COLORS = Colors.darkColors(
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
val Sizes.treeIndentation: Dp get() = gap * 1.5f
val Sizes.editItemHeight: Dp get() = largeGap * 1.25f
val Sizes.lineHeight: Dp get() = baseSize * (2f/3f)
val Sizes.lineHeightMedium: Dp get() = baseSize * 0.8f
val Sizes.lineHeightLarge: Dp get() = baseSize * 0.9f
val Sizes.heightTitleBar: Dp get() = lineHeightLarge
val Sizes.heightWindowTitleBar: Dp get() = heightTitleBar * 1.1f
val Sizes.panelBarWidth: Dp get() = baseSize - borderWidth
val Sizes.smallTextFieldPadding: Dp get() = smallGap * 0.75f
val Sizes.scrollbarWidth: Dp get() = gap * 0.33f
val Sizes.editorLabelWidthSmall: Dp get() = baseSize * 3.125f
val Sizes.editorLabelWidthMedium: Dp get() = baseSize * 4
val Sizes.editorLabelWidthLarge: Dp get() = baseSize * 5
val Sizes.browserItemSize: Dp get() = baseSize * 2.5f

val Sizes.editorPanelMarginStart: Dp get() = gap * 1.5f
val Sizes.editorPanelMarginEnd: Dp get() = gap

val Sizes.smallText: MsdfFont get() = (normalText as MsdfFont).copy(sizePts = normalText.sizePts * 0.8f)
val Sizes.boldText: MsdfFont get() = (normalText as MsdfFont).copy(weight = 0.075f)
val Sizes.italicText: MsdfFont get() = (normalText as MsdfFont).copy(italic = MsdfFont.ITALIC_STD)

// hovered background: hovered list items, hovered collapsable panel header
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
val Colors.elevatedComponentClickFeedback: Color get() = UiColors.secondaryBright

val Colors.dndAcceptableBg: Color get() = MdColor.GREEN.withAlpha(0.3f)
val Colors.dndAcceptableBgHovered: Color get() = MdColor.GREEN.withAlpha(0.5f)

val Colors.backgroundMid: Color get() = background.mix(backgroundVariant, 0.5f)

val Colors.weakDividerColor: Color get() = secondaryVariantAlpha(0.75f)
val Colors.strongDividerColor: Color get() = secondaryAlpha(0.75f)

val Colors.windowTitleBg: Color get() = backgroundMid


object UiColors {
    val border = Color("0f1114ff")
    val titleBg = Color("343a49ff")
    val windowTitleBgAccent = MdColor.DEEP_PURPLE
    val titleBgAccent = MdColor.DEEP_PURPLE
    val titleText = Color("dbe6ffff")
    val secondaryBright = Color("a0b3d8ff")
    val selectionChild = Color("ff7b0080")
}

object DragChangeRates {
    const val RANGE_0_TO_1 = 0.005
    const val POSITION = 0.01
    const val SCALE = 0.01
    const val ROTATION = 0.1
    const val SIZE = 0.01

    val SIZE_VEC2 = Vec2d(SIZE)
    val SIZE_VEC3 = Vec3d(SIZE)
    val SIZE_VEC4 = Vec4d(SIZE)
    val POSITION_VEC3 = Vec3d(POSITION)
    val SCALE_VEC3 = Vec3d(SCALE)
    val ROTATION_VEC3 = Vec3d(ROTATION)
}
