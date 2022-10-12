package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ui2.*
import kotlin.math.min

class GameOfLifeWindow(val uiDemo: UiDemo) {

    private val windowState = WindowState().apply { setWindowLocation(Dp(400f), Dp(400f)) }
    private val scrollState = ScrollState()

    private val world = GameWorld()
    private val worldRenderer = GameWorldRenderer()

    private val isPaused = mutableStateOf(false)
    private val updateSpeeds = intArrayOf(60, 30, 20, 15, 10, 7, 5, 3, 2, 1)
    private val updateSpeed = mutableStateOf(5)
    private var updateCount = updateSpeeds[updateSpeed.value]

    private val worldPanel = WorldPanel()
    private val renderPanel = RenderPanel()

    init {
        world.loadAsciiState(GameWorld.gliderGun)
    }

    val window = Window(windowState, name = "Conway`s Game of Life") {
        surface.sizes = uiDemo.selectedUiSize.use()
        surface.colors = uiDemo.selectedColors.use()

        TitleBar()

        worldPanel()
        renderPanel()

        surface.onEachFrame {
            if (!isPaused.value && --updateCount <= 0) {
                updateCount = updateSpeeds[updateSpeed.value]
                world.step()
            }
        }

        // use() game state to get updated whenever it is stepped
        world.gameState.use()

        ScrollArea(
            scrollState,
            containerModifier = {
                it
                    .margin(sizes.gap)
                    .size(Grow(1f, max = FitContent), Grow(1f, max = FitContent))
            }
        ) {
            worldRenderer()
        }
    }

    private abstract class CollapsablePanel(val title: String) : ComposableComponent {
        val isCollapsed = mutableStateOf(false)
        val isHovered = mutableStateOf(false)

        override fun UiScope.compose() = Column(Grow.Std) {
            modifier.backgroundColor(colors.backgroundVariant)
            Row(Grow.Std) {
                modifier
                    .backgroundColor(colors.secondaryVariant.withAlpha(if (isHovered.use()) 0.75f else 0.5f))
                    .onClick { isCollapsed.toggle() }
                    .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                    .onEnter { isHovered.set(true) }
                    .onExit { isHovered.set(false) }

                Arrow (if (isCollapsed.use()) 0f else 90f) {
                    modifier
                        .size(sizes.gap * 1.5f, sizes.gap * 1.5f)
                        .margin(horizontal = sizes.gap)
                        .alignY(AlignmentY.Center)
                }
                Text(title) { }
            }
            if (!isCollapsed.value) {
                content()
            } else {
                divider(colors.secondaryVariant.withAlpha(0.75f), horizontalMargin = 0.dp, thickness = 1.dp)
            }
        }

        abstract fun UiScope.content()
    }

    private inner class WorldPanel : CollapsablePanel("World") {
        override fun UiScope.content() {
            Row {
                modifier.padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                Text("Content") { modifier.alignY(AlignmentY.Center).width(sizes.largeGap * 7f) }
                Button("Clear world") {
                    modifier
                        .width(sizes.largeGap * 7f)
                        .margin(horizontal = sizes.gap)
                        .onClick {
                            world.clear()
                            surface.triggerUpdate()
                        }
                }
                Button("Load glider gun") {
                    modifier
                        .width(sizes.largeGap * 7f)
                        .margin(horizontal = sizes.gap)
                        .onClick {
                            world.loadAsciiState(GameWorld.gliderGun)
                            surface.triggerUpdate()
                        }
                }
                Button("Randomize") {
                    modifier
                        .width(sizes.largeGap * 7f)
                        .margin(horizontal = sizes.gap)
                        .onClick {
                            world.randomize(0.3f)
                            surface.triggerUpdate()
                        }
                }
            }
            Row {
                modifier.padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                Text("Size") { modifier.alignY(AlignmentY.Center).width(sizes.largeGap * 7f) }
                Text("Width:") { modifier.alignY(AlignmentY.Center).margin(horizontal = sizes.gap) }
                Slider(world.worldSizeX.use().toFloat(), 16f, 70f) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(horizontal = sizes.gap)
                        .width(sizes.largeGap * 5f)
                        .onChange { world.worldSizeX.set(it.toInt()) }
                }
                Text("${world.worldSizeX.value}") { modifier.alignY(AlignmentY.Center).margin(end = sizes.largeGap * 2f) }
                Text("Height:") { modifier.alignY(AlignmentY.Center) }
                Slider(world.worldSizeY.use().toFloat(), 16f, 70f) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(horizontal = sizes.gap)
                        .width(sizes.largeGap * 5f)
                        .onChange { world.worldSizeY.set(it.toInt()) }
                }
                Text("${world.worldSizeY.value}") { modifier.alignY(AlignmentY.Center).margin(end = sizes.largeGap) }
            }
            Row {
                modifier.padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                Text("Connect edges") { modifier.alignY(AlignmentY.Center).width(sizes.largeGap * 7f) }
                Switch(world.connectWorldEdges.use()) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(sizes.gap)
                        .onToggle {
                            world.connectWorldEdges.set(it)
                        }
                }
            }
        }
    }

    private inner class RenderPanel : CollapsablePanel("Visualization") {
        override fun UiScope.content() {
            Row {
                modifier.padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                Text("Cell-renderer") { modifier.alignY(AlignmentY.Center).width(sizes.largeGap * 7f) }
                ComboBox {
                    modifier
                        .alignY(AlignmentY.Center)
                        .width(sizes.largeGap * 7f)
                        .margin(horizontal = sizes.gap)
                        .items(worldRenderer.rendererChoices)
                        .selectedIndex(worldRenderer.selectedRenderer.use())
                        .onItemSelected { worldRenderer.selectedRenderer.set(it) }
                }
            }
            Row {
                modifier.padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                Text("Speed") { modifier.alignY(AlignmentY.Center).width(sizes.largeGap * 7f) }
                Slider(updateSpeed.use().toFloat(), 0f, updateSpeeds.lastIndex.toFloat()) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .width(sizes.largeGap * 5f)
                        .margin(horizontal = sizes.gap)
                        .onChange {
                            updateSpeed.set(it.toInt())
                            updateCount = min(updateCount, updateSpeeds[updateSpeed.value])
                        }
                }
            }
            Row {
                modifier.padding(horizontal = sizes.gap, vertical = sizes.smallGap)
                Text("Pause game") { modifier.alignY(AlignmentY.Center).width(sizes.largeGap * 7f) }
                Switch(isPaused.use()) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(sizes.gap)
                        .onToggle { isPaused.toggle() }
                }
            }
        }
    }

    private inner class GameWorldRenderer : ComposableComponent {
        val rendererChoices = listOf("Radiobutton", "Checkbox", "Box")
        val selectedRenderer = mutableStateOf(0)

        private var isEditDrag = false
        private var editChangeToState = false

        private val radioButtonRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
            RadioButton(world[x, y]) {
                modifier.colors(
                    borderColorOff = colors.secondary.withAlpha(0.5f),
                    backgroundColorOff = colors.secondary.withAlpha(0.15f)
                )
                setupCellRenderer(x, y)
            }
        }

        private val checkboxRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
            Checkbox(world[x, y]) {
                modifier.colors(
                    borderColor = colors.secondary.withAlpha(0.5f),
                    backgroundColor = colors.secondary.withAlpha(0.15f)
                )
                setupCellRenderer(x, y)
            }
        }

        private val boxRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
            Box {
                modifier.size(sizes.checkboxSize, sizes.checkboxSize)
                setupCellRenderer(x, y)
                if (world[x, y]) {
                    modifier.backgroundColor(colors.primary)
                }
            }
        }

        private fun UiScope.setupCellRenderer(x: Int, y: Int) {
            modifier
                .onDragStart {
                    isEditDrag = true
                    editChangeToState = !world[x, y]
                    isPaused.set(true)
                }
                .onDragEnd {
                    isEditDrag = false
                }
                .onPointer {
                    if (isEditDrag) {
                        world[x, y] = editChangeToState
                        surface.triggerUpdate()
                    }
                }
                .onClick {
                    // pause game to enable manual editing
                    isPaused.set(true)
                    world[x, y] = !world[x, y]
                    // trigger update (without stepping the game state) to update button state
                    surface.triggerUpdate()
                }
        }

        override fun UiScope.compose() = Column {
            val szX = world.worldSizeX.use()
            val szY = world.worldSizeY.use()
            val renderer = when(selectedRenderer.use()) {
                0 -> radioButtonRenderer
                1 -> checkboxRenderer
                else -> boxRenderer
            }

            for (y in 0 until szY) {
                Row {
                    for (x in 0 until szX) {
                        renderer(x, y)
                    }
                }
            }
        }
    }

    private class GameWorld {
        val connectWorldEdges = mutableStateOf(false)
        val worldSizeX: MutableStateValue<Int> = mutableStateOf(45).onChange { resize(it, worldSizeY.value) }
        val worldSizeY: MutableStateValue<Int> = mutableStateOf(30).onChange { resize(worldSizeX.value, it) }
        val gameState = mutableStateOf(BooleanArray(worldSizeX.value * worldSizeY.value))

        private val size = MutableVec2i(worldSizeX.value, worldSizeY.value)
        private var nextGameState = BooleanArray(worldSizeX.value * worldSizeY.value)

        private fun resize(newX: Int, newY: Int) {
            val newState = BooleanArray(newX * newY)
            nextGameState = BooleanArray(newX * newY)
            for (y in 0 until min(size.y, newY)) {
                for (x in 0 until min(size.x, newX)) {
                    newState[y * newX + x] = gameState.value[y * size.x + x]
                }
            }
            size.x = newX
            size.y = newY
            gameState.set(newState)
        }

        fun step() {
            val state = gameState.value
            for (y in 0 until size.y) {
                for (x in 0 until size.x) {
                    val popCnt = countPopNeighbors(x, y)
                    nextGameState[y * size.x + x] = if (popCnt == 2 && this[x, y]) true else popCnt == 3
                }
            }
            gameState.set(nextGameState)
            nextGameState = state
        }

        private fun countPopNeighbors(x: Int, y: Int): Int {
            var popCnt = 0
            for (iy in -1..1) {
                for (ix in -1..1) {
                    if (this[x + ix, y + iy] && (ix != 0 || iy != 0)) {
                        popCnt++
                    }
                }
            }
            return popCnt
        }

        operator fun get(x: Int, y: Int): Boolean {
            var xx = x
            var yy = y

            if (xx !in 0 until size.x) {
                if (!connectWorldEdges.value) {
                    return false
                }
                xx %= size.x
                if (xx < 0) {
                    xx += size.x
                }
            }
            if (yy !in 0 until size.y) {
                if (!connectWorldEdges.value) {
                    return false
                }
                yy %= size.y
                if (yy < 0) {
                    yy += size.y
                }
            }
            return gameState.value[yy * size.x + xx]
        }

        operator fun set(x: Int, y: Int, value: Boolean) {
            if (x !in 0 until size.x || y !in 0 until size.y) {
                return
            }
            gameState.value[y * size.x + x] = value
        }

        fun clear() = loadAsciiState("")

        fun randomize(p: Float) {
            for (i in gameState.value.indices) gameState.value[i] = randomF() < p
        }

        fun loadAsciiState(state: String) {
            for (i in gameState.value.indices) gameState.value[i] = false
            state.lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    this[x, y] = c =='0'
                }
            }
        }

        companion object {
            val gliderGun = """
            .
            .........................0
            .......................0.0
            .............00......00............00
            ............0...0....00............00
            .00........0.....0...00
            .00........0...0.00....0.0
            ...........0.....0.......0
            ............0...0
            .............00
        """.trimIndent()
        }
    }
}