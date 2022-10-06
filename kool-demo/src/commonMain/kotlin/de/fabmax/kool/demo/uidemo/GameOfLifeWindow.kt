package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ui2.*

class GameOfLifeWindow(val uiDemo: UiDemo) {

    private val windowState = WindowState().apply { setWindowBounds(Dp(400f), Dp(400f), Dp(800f), Dp(800f)) }
    private val scrollState = ScrollState()

    private val gridSizeX = mutableStateOf(50)
    private val gridSizeY = mutableStateOf(50)

    private val gameState = mutableStateOf(BooleanArray(gridSizeX.value * gridSizeY.value))
    private var nextGameState = BooleanArray(gridSizeX.value * gridSizeY.value)

    private val isPaused = mutableStateOf(false)
    private var updateRate = mutableStateOf(5)
    private var updateCount = updateRate.value

    private var dragChangeToState = false
    private var windowNode: WindowNode? = null

    private val radioButtonRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
        RadioButton(gameState.value[x, y]) {
            modifier.colors(
                borderColor = colors.accentVariant.withAlpha(0.65f),
                backgroundColor = colors.accentVariant.withAlpha(0.15f)
            )
            setupCellRenderer(x, y)
        }
    }

    private val checkboxRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
        Checkbox(gameState.value[x, y]) {
            modifier.colors(
                borderColor = colors.accentVariant.withAlpha(0.65f),
                backgroundColor = colors.accentVariant.withAlpha(0.15f)
            )
            setupCellRenderer(x, y)
        }
    }

    private val boxRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
        Box {
            modifier.size(sizes.checkboxSize, sizes.checkboxSize)
            setupCellRenderer(x, y)
            if (gameState.value[x, y]) {
                modifier.backgroundColor(colors.accent)
            }
        }
    }

    private val rendererChoices = listOf("Radiobutton", "Checkbox", "Box")
    private val selectedRenderer = mutableStateOf(0)

    init {
        gameState.value.loadAsciiState(gliderGun)
    }

    val window = Window(windowState) {
        surface.sizes = uiDemo.selectedUiSize.use()
        surface.colors = uiDemo.selectedColors.use()

        windowNode = uiNode as WindowNode

        TitleBar("Game of Life")

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
            Button("Load glider gun") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
                    .onClick {
                        gameState.value.loadAsciiState(gliderGun)
                        surface.triggerUpdate()
                    }
            }
            Button("Randomize") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
                    .onClick {
                        gameState.value.randomize(0.3f)
                        surface.triggerUpdate()
                    }
            }
            ComboBox {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
                    .items(rendererChoices)
                    .selectedIndex(selectedRenderer.use())
                    .onItemSelected { selectedRenderer.set(it) }
            }
            Text("Speed") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(sizes.gap)
            }
            Slider(10f - updateRate.use(), 0f, 10f) {
                modifier
                    .alignY(AlignmentY.Center)
                    .onChange { updateRate.set(10 - it.toInt()) }
            }
        }

        surface.onEachFrame {
            if (!isPaused.value && --updateCount <= 0) {
                updateCount = updateRate.value
                step()
            }
        }

        // use() game state to get updated whenever it is stepped
        gameState.use()

        ScrollArea(
            scrollState,
            containerModifier = {
                it
                    .margin(sizes.gap)
                    .size(Grow(1f, max = FitContent), Grow(1f, max = FitContent))
            }
        ) {
            Column {
                val szX = gridSizeX.use()
                val szY = gridSizeY.use()
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
    }

    fun step() {
        val state = gameState.value
        for (y in 0 until gridSizeY.value) {
            for (x in 0 until gridSizeX.value) {
                val popCnt = state.countPopNeighbors(x, y)
                nextGameState[x, y] = if (popCnt == 2 && state[x, y]) true else popCnt == 3
            }
        }
        gameState.set(nextGameState)
        nextGameState = state
    }

    private fun UiScope.setupCellRenderer(x: Int, y: Int) {
        modifier
            .onDragStart {
                val flags = windowNode?.let { wnd -> wnd.getBorderFlags(wnd.toLocal(it.screenPosition)) }
                if (flags != 0) {
                    it.reject()
                } else {
                    dragChangeToState = !gameState.value[x, y]
                    isPaused.set(true)
                }
            }
            .onPointer {
                if (it.pointer.isDrag) {
                    gameState.value[x, y] = dragChangeToState
                    surface.triggerUpdate()
                }
            }
            .onClick {
                // pause game to enable manual editing
                isPaused.set(true)
                gameState.value[x, y] = !gameState.value[x, y]
                // trigger update (without stepping the game state) to update button state
                surface.triggerUpdate()
            }
    }

    private fun BooleanArray.countPopNeighbors(x: Int, y: Int): Int {
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

    private operator fun BooleanArray.get(x: Int, y: Int): Boolean {
        if (x !in 0 until gridSizeX.value || y !in 0 until gridSizeY.value) {
            return false
        }
        return this[y * gridSizeX.value + x]
    }

    private operator fun BooleanArray.set(x: Int, y: Int, value: Boolean) {
        if (x !in 0 until gridSizeX.value || y !in 0 until gridSizeY.value) {
            return
        }
        this[y * gridSizeX.value + x] = value
    }

    private fun BooleanArray.randomize(p: Float) {
        for (i in indices) this[i] = randomF() < p
    }

    private fun BooleanArray.loadAsciiState(state: String) {
        for (i in indices) this[i] = false
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