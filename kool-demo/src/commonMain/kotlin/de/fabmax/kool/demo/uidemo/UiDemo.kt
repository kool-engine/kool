package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.Settings
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.Dock
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Scene.Companion.DEFAULT_CLEAR_COLOR
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.launchDelayed

class UiDemo : DemoScene("UI Demo") {

    val selectedColors = mutableStateOf(Colors.darkColors()).onChange { _, new -> dock.dockingSurface.colors = new }
    val selectedUiSize = mutableStateOf(Sizes.medium)

    val dock = Dock()
    val demoWindows = mutableListOf<DemoWindow>()

    private val windowSpawnLocation = MutableVec2f(320f, 64f)

    val exampleImage by texture2d("${DemoLoader.materialPath}/uv_checker_map.jpg")
    val dndContext = DragAndDropContext<DragAndDropWindow.DndItem>()

    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupUiScene(DEFAULT_CLEAR_COLOR)

        dock.dockingSurface.colors = selectedColors.value
        dock.dockingPaneComposable = Composable {
            Box(Grow.Std, Grow.Std) {
                modifier.margin(start = UiSizes.baseSize)
                dock.root()
            }
        }

        addNode(dock)

        // setup initial dock layout: one row with three leafs as children (left, center, right)
        dock.createNodeLayout(
            listOf(
                "0:row",
                "0:row/0:leaf",
                "0:row/1:leaf",
                "0:row/2:leaf"
            )
        )

        // add a hidden empty dockable to the center node to avoid, center node being removed on undock
        val centerSpacer = UiDockable("EmptyDockable", dock, isHidden = true)
        dock.getLeafAtPath("0:row/1:leaf")?.dock(centerSpacer)

        // Spawn launcher and theme editor windows docked to the left and right dock nodes
        spawnWindow(LauncherWindow(this@UiDemo), "0:row/0:leaf")
        spawnWindow(ThemeEditorWindow(this@UiDemo), "0:row/2:leaf")

        // TextStyleWindow is spawned as a floating window
        spawnWindow(TextStyleWindow(this@UiDemo))

        // add a sidebar for the demo menu
        sideBar()
    }

    private fun Scene.sideBar() = addPanelSurface {
        surface.colors = selectedColors.use()
        surface.sizes = Settings.uiSize.use().sizes

        val fgColor: Color
        val bgColor: Color
        if (colors.isLight) {
            fgColor = colors.primary
            bgColor = colors.secondaryVariant.mix(Color.BLACK, 0.3f)
        } else {
            fgColor = colors.secondary
            bgColor = colors.backgroundVariant
        }

        modifier
            .width(UiSizes.baseSize)
            .height(Grow.Std)
            .backgroundColor(bgColor)
            .layout(CellLayout)
            .onClick { demoLoader?.menu?.isExpanded = true }

        Text("UI Demo") {
            val font = MsdfFont(sizePts = sizes.largeText.sizePts * 1.25f, weight = MsdfFont.WEIGHT_BOLD)
            modifier
                .textColor(fgColor)
                .textRotation(270f)
                .font(font)
                .margin(top = UiSizes.baseSize)
                .align(AlignmentX.Center, AlignmentY.Top)
        }
    }

    fun spawnWindow(window: DemoWindow, dockPath: String? = null) {
        demoWindows += window

        dock.addDockableSurface(window.windowDockable, window.windowSurface)
        dockPath?.let {
            dock.getLeafAtPath(it)?.dock(window.windowDockable)
        }

        window.windowDockable.setFloatingBounds(Dp(windowSpawnLocation.x), Dp(windowSpawnLocation.y))
        windowSpawnLocation.x += 32f
        windowSpawnLocation.y += 32f
        if (windowSpawnLocation.y > 480f) {
            windowSpawnLocation.y -= 416
            windowSpawnLocation.x -= 384

            if (windowSpawnLocation.x > 480f) {
                windowSpawnLocation.x = 320f
            }
        }

        launchDelayed(1) {
            window.windowSurface.isFocused.set(true)
        }
    }

    fun closeWindow(window: DemoWindow) {
        dock.removeDockableSurface(window.windowSurface)
        demoWindows -= window
        window.onClose()
        window.windowSurface.release()
    }
}
