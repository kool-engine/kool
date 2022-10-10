package de.fabmax.kool.demo.uidemo

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

    init {
        world.loadAsciiState(GameWorld.gliderGun)
    }

    val window = Window(windowState, name = "Conway`s Game of Life") {
        surface.sizes = uiDemo.selectedUiSize.use()
        surface.colors = uiDemo.selectedColors.use()

        TitleBar()

        Row {
            Text("Paused") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
            }
            Switch(isPaused.use()) {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
                    .onToggle { isPaused.toggle() }
            }
            Button("Clear") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
                    .onClick {
                        world.clear()
                        surface.triggerUpdate()
                    }
            }
            Button("Load glider gun") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
                    .onClick {
                        world.loadAsciiState(GameWorld.gliderGun)
                        surface.triggerUpdate()
                    }
            }
            Button("Randomize") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
                    .onClick {
                        world.randomize(0.3f)
                        surface.triggerUpdate()
                    }
            }
            ComboBox {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
                    .items(worldRenderer.rendererChoices)
                    .selectedIndex(worldRenderer.selectedRenderer.use())
                    .onItemSelected { worldRenderer.selectedRenderer.set(it) }
            }
            Text("Speed") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
            }
            Slider(updateSpeed.use().toFloat(), 0f, updateSpeeds.lastIndex.toFloat()) {
                modifier
                    .alignY(AlignmentY.Center)
                    .onChange {
                        updateSpeed.set(it.toInt())
                        updateCount = min(updateCount, updateSpeeds[updateSpeed.value])
                    }
            }
        }

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
        val worldSizeX = mutableStateOf(50)
        val worldSizeY = mutableStateOf(50)
        val gameState = mutableStateOf(BooleanArray(worldSizeX.value * worldSizeY.value))

        private var nextGameState = BooleanArray(worldSizeX.value * worldSizeY.value)

        fun step() {
            val state = gameState.value
            for (y in 0 until worldSizeY.value) {
                for (x in 0 until worldSizeX.value) {
                    val popCnt = countPopNeighbors(x, y)
                    nextGameState[y * worldSizeX.value + x] = if (popCnt == 2 && this[x, y]) true else popCnt == 3
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
            if (x !in 0 until worldSizeX.value || y !in 0 until worldSizeY.value) {
                return false
            }
            return gameState.value[y * worldSizeX.value + x]
        }

        operator fun set(x: Int, y: Int, value: Boolean) {
            if (x !in 0 until worldSizeX.value || y !in 0 until worldSizeY.value) {
                return
            }
            gameState.value[y * worldSizeX.value + x] = value
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