package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.Settings
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont

class UiDemo : DemoScene("UI Demo") {

    val selectedColors = mutableStateOf(Colors.darkColors())
    val selectedUiSize = mutableStateOf(Sizes.medium)

    val demoWindows = mutableListOf<DemoWindow>()

    private val windowSpawnLocation = MutableVec2f(320f, 64f)
    private val dockingHost = DockingHost()

    var exampleImage: Texture2d? = null
    val dndContext = DragAndDropContext<DragAndDropWindow.DndItem>()

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        exampleImage = loadAndPrepareTexture("${DemoLoader.materialPath}/uv_checker_map.jpg")
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupUiScene(true)

        +dockingHost.apply {
            onUpdate += {
                // set a left margin for the demo menu band
                dockingSurface.rootContainer.dockMarginStart.set(UiSizes.baseSize)
            }
        }

        // Spawn a few windows, docked right, left and in center
        //
        // Specifying the docking path is a bit cumbersome right now:
        // Docking nodes are organized as a bin-tree, only leaf nodes can contain docked windows.
        // The docking path specifies the path in the tree where to dock the given window, starting at the root node.
        // The numbers define the split weights in case tree nodes have to be spawned to complete the path.
        // If the internal tree structure conflicts with the given path, the window is docked in the next best slot.
        //
        // The LauncherWindow is docked at Start (left) position below the root node. Splitting it with an absolute
        // width of 250 dp, remaining screen space remain will be empty.
        // Then, the ThemeEditorWindow is spawned on the right side of the empty side (path: root -> end/right -> end/right),
        // taking 30 % of the empty space.

        spawnWindow(LauncherWindow(this@UiDemo), listOf(DockingHost.DockPosition.Start to Dp(250f)))
        spawnWindow(ThemeEditorWindow(this@UiDemo), listOf(DockingHost.DockPosition.End to Grow.Std, DockingHost.DockPosition.End to Grow(0.3f)))

        // TextStyleWindow is spawned as a floating window
        spawnWindow(TextStyleWindow(this@UiDemo))

        // add a sidebar for the demo menu
        +Panel {
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
    }

    fun spawnWindow(window: DemoWindow, dockPath: List<Pair<DockingHost.DockPosition, Dimension>>? = null) {
        demoWindows += window
        dockingHost.apply {
            +window.windowSurface
            if (dockPath != null) {
                dockWindow(window.windowScope, dockPath)
            } else {
                window.windowScope.windowState.setWindowLocation(Dp(windowSpawnLocation.x), Dp(windowSpawnLocation.y))
                windowSpawnLocation.x += 32f
                windowSpawnLocation.y += 32f

                if (windowSpawnLocation.y > 480f) {
                    windowSpawnLocation.y -= 416
                    windowSpawnLocation.x -= 384

                    if (windowSpawnLocation.x > 480f) {
                        windowSpawnLocation.x = 320f
                    }
                }
            }
        }
        window.windowSurface.bringToTop()
    }

    fun closeWindow(window: DemoWindow, ctx: KoolContext) {
        dockingHost.undockWindow(window.windowScope)
        dockingHost -= window.windowSurface
        demoWindows -= window
        window.onClose()
        window.windowSurface.dispose(ctx)
    }

    interface DemoWindow {
        val windowSurface: UiSurface
        val windowScope: WindowScope

        fun onClose() { }
    }
}