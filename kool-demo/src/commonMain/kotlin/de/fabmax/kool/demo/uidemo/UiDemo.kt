package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Scene

class UiDemo : DemoScene("UI Demo") {

    val selectedColors = mutableStateOf(Colors.darkColors())
    val selectedUiSize = mutableStateOf(Sizes.medium)

    private val dockingHost = DockingHost()
    private val windows = mutableListOf<UiSurface>()

    var imageTex: Texture2d? = null

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        imageTex = loadAndPrepareTexture("${DemoLoader.materialPath}/uv_checker_map.jpg")
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        // new improved ui system
        // desired features
        // - [x] somewhat jetpack compose inspired api
        // - [x] traditional ui coord system: top left origin
        // - [x] layout via nested boxes
        // - [x] lazy list for fast update of large scrolling lists
        // - [x] clip content to bounds
        // - [x] scrollable content
        // - [x] docking
        // - [x] size: absolute (dp), grow, wrap content
        // - [x] alignment: start, center, end / top, center, bottom
        // - [x] margin / outside gap
        // - [x] padding / inside gap

        // todo
        //  icons

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
        // The ThemeEditorWindow is docked at End (right) position below the root node. Splitting it with a weight of
        // 0.25, i.e. the ThemeEditorWindow will take 25% of the screen space.
        // GameOfLifeWindow is docked left to the theme editor, initially taking up the remaining 75% of screen space.
        // Finally, the BasicUiWindow is spawned left of the GameOfLifeWindow (path: root -> start/left -> start/left),
        // reducing the size of the GameOfLifeWindow by one third (from former 75% screen space to 50% screen space).

        spawnWindow(ThemeEditorWindow(this@UiDemo).window, listOf(DockingHost.DockPosition.End to 0.25f))
        spawnWindow(GameOfLifeWindow(this@UiDemo).window, listOf(DockingHost.DockPosition.Start to 0.75f))
        spawnWindow(BasicUiWindow(this@UiDemo).window, listOf(DockingHost.DockPosition.Start to 0.75f, DockingHost.DockPosition.Start to 0.333f))
    }

    fun spawnWindow(windowSurface: UiSurface, dockPath: List<Pair<DockingHost.DockPosition, Float>>? = null) {
        windows += windowSurface
        dockingHost.apply {
            +windowSurface
            if (dockPath != null) {
                windowSurface.windowScope?.let { window ->
                    dockWindow(window, dockPath)
                }
            }
        }
    }

}