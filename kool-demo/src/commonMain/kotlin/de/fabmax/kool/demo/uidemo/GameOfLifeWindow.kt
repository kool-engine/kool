package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import kotlin.math.min

class GameOfLifeWindow(uiDemo: UiDemo) : DemoWindow("Conway`s Game of Life", uiDemo) {

    private val world = GameWorld()
    private val worldRenderer = GameWorldRenderer()

    private val isPaused = mutableStateOf(false)
    private val isPauseOnEdit = mutableStateOf(false)
    private val updateSpeeds = intArrayOf(60, 30, 20, 15, 10, 7, 5, 3, 2, 1)
    private val updateSpeed = mutableStateOf(5)
    private var updateCount = updateSpeeds[updateSpeed.value]

    private val worldPanel = WorldPanel()
    private val renderPanel = RenderPanel()

    init {
        world.loadAsciiState(GameWorld.gliderGun)
    }

    override fun UiScope.windowContent() = Column(Grow.Std, Grow.Std) {
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
            containerModifier = {
                it
                    .margin(sizes.gap)
                    .size(Grow(1f, max = FitContent), Grow(1f, max = FitContent))
                    .backgroundColor(if (worldRenderer.isClassicColor) colors.backgroundVariant else MdColor.GREY tone 900)
            }
        ) {
            worldRenderer()
        }
    }

    private abstract class CollapsablePanel(val title: String) : Composable {
        val isCollapsed = mutableStateOf(false)
        val isHovered = mutableStateOf(false)

        override fun UiScope.compose() = Column(Grow.Std) {
            modifier.backgroundColor(colors.backgroundVariant)
            Row(Grow.Std) {
                modifier
                    .backgroundColor(colors.secondaryVariantAlpha(if (isHovered.use()) 0.75f else 0.5f))
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
                divider(colors.secondaryVariantAlpha(0.75f), horizontalMargin = 0.dp, thickness = 1.dp)
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
                Slider(world.worldSizeX.use().toFloat(), 10f, 100f) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(horizontal = sizes.gap)
                        .width(sizes.largeGap * 8f)
                        .onChange { world.worldSizeX.set(it.toInt()) }
                }
                Text("${world.worldSizeX.value}") { modifier.alignY(AlignmentY.Center).margin(end = sizes.largeGap * 2f) }
                Text("Height:") { modifier.alignY(AlignmentY.Center) }
                Slider(world.worldSizeY.use().toFloat(), 10f, 100f) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(horizontal = sizes.gap)
                        .width(sizes.largeGap * 8f)
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
                Text("Colors") { modifier.alignY(AlignmentY.Center).width(sizes.largeGap * 7f) }
                ComboBox {
                    modifier
                        .alignY(AlignmentY.Center)
                        .width(sizes.largeGap * 7f)
                        .margin(horizontal = sizes.gap)
                        .items(worldRenderer.colorChoices)
                        .selectedIndex(worldRenderer.selectedColor.use())
                        .onItemSelected { worldRenderer.selectedColor.set(it) }
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
                Text("Pause on edit") { modifier.alignY(AlignmentY.Center).margin(start = sizes.largeGap * 4f, end = sizes.gap) }
                Switch(isPauseOnEdit.use()) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .margin(sizes.gap)
                        .onToggle { isPauseOnEdit.toggle() }
                }
            }
        }
    }

    private inner class GameWorldRenderer : Composable {
        val rendererChoices = listOf("Radiobutton", "Checkbox", "Box")
        val selectedRenderer = mutableStateOf(0)
        val colorChoices = listOf("Binary", "Oceanic", "Viridis", "Plasma")
        val selectedColor = mutableStateOf(0)

        val isClassicColor: Boolean
            get() = selectedColor.value == 0

        private var colorGradient: ColorGradient? = null
        private var isEditDrag = false
        private var editChangeToState = false

        private var classicButtonBorderColor = Color.WHITE
        private var classicButtonBgColor = Color.BLACK

        val cellCallbacks = mutableListOf<CellCallbacks>()

        private val radioButtonRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
            val cell = world[x, y]
            val cellColor = world.cellColor(x, y, colors, colorGradient)
            RadioButton(cell) {
                modifier.colors(
                    borderColorOff = cellColor ?: classicButtonBorderColor,
                    backgroundColorOff = cellColor?.withAlpha(0.5f) ?: classicButtonBgColor,
                    knobColor = cellColor ?: colors.primary
                )
                setupCellRenderer(x, y)
            }
        }

        private val checkboxRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
            val cell = world[x, y]
            val cellColor = world.cellColor(x, y, colors, colorGradient)
            Checkbox(cell) {
                modifier.colors(
                    borderColor = cellColor ?: classicButtonBorderColor,
                    backgroundColor = cellColor?.withAlpha(0.5f) ?: classicButtonBgColor,
                    fillColor = cellColor ?: colors.primary
                )
                setupCellRenderer(x, y)
            }
        }

        private val boxRenderer: UiScope.(Int, Int) -> Unit = { x, y ->
            val cellColor = world.cellColor(x, y, colors, colorGradient)
            Box {
                modifier.size(sizes.checkboxSize, sizes.checkboxSize)
                setupCellRenderer(x, y)
                cellColor?.let { modifier.backgroundColor(it) }
            }
        }

        private fun UiScope.setupCellRenderer(x: Int, y: Int) {
            val cbs = cellCallbacks[y * world.worldSizeX.value + x]
            modifier
                .onDragStart(cbs.onDragStart)
                .onDragEnd(cbs.onDragEnd)
                .onPointer(cbs.onPointer)
                .onClick(cbs.onClick)
        }

        override fun UiScope.compose() = Column {
            val szX = world.worldSizeX.use()
            val szY = world.worldSizeY.use()

            if (szX * szY != cellCallbacks.size) {
                cellCallbacks.clear()
                for (y in 0 until szY) {
                    for (x in 0 until szX) {
                        cellCallbacks += CellCallbacks(x, y)
                    }
                }
            }

            val renderer = when(selectedRenderer.use()) {
                0 -> radioButtonRenderer
                1 -> checkboxRenderer
                else -> boxRenderer
            }

            classicButtonBorderColor = colors.secondaryAlpha(0.5f)
            classicButtonBgColor = colors.secondaryAlpha(0.15f)
            colorGradient = when(selectedColor.use()) {
                1 -> ColorGradient.RED_WHITE_BLUE.inverted()
                2 -> ColorGradient.VIRIDIS
                3 -> ColorGradient.PLASMA
                else -> null
            }

            for (y in 0 until szY) {
                Row {
                    for (x in 0 until szX) {
                        renderer(x, y)
                    }
                }
            }
        }

        private inner class CellCallbacks(val x: Int, val y: Int) {
            val onDragStart: (PointerEvent) -> Unit = {
                isEditDrag = true
                editChangeToState = !world[x, y]
                if (isPauseOnEdit.value) {
                    isPaused.set(true)
                }
            }

            val onDragEnd: (PointerEvent) -> Unit = {
                isEditDrag = false
            }

            val onPointer: (PointerEvent) -> Unit = {
                if (isEditDrag) {
                    world[x, y] = editChangeToState
                    windowSurface.triggerUpdate()
                }
            }

            val onClick: (PointerEvent) -> Unit = {
                // pause game to enable manual editing
                if (isPauseOnEdit.value) {
                    isPaused.set(true)
                }
                world[x, y] = !world[x, y]
                // trigger update (without stepping the game state) to update button state
                windowSurface.triggerUpdate()
            }
        }
    }

    private class GameWorld {
        val connectWorldEdges = mutableStateOf(false)
        val worldSizeX: MutableStateValue<Int> = mutableStateOf(45).onChange { _, new -> resize(new, worldSizeY.value) }
        val worldSizeY: MutableStateValue<Int> = mutableStateOf(30).onChange { _, new -> resize(worldSizeX.value, new) }

        val gameState = mutableStateOf(BooleanArray(worldSizeX.value * worldSizeY.value))

        private val size = MutableVec2i(worldSizeX.value, worldSizeY.value)
        private var nextGameState = BooleanArray(worldSizeX.value * worldSizeY.value)
        private var aliveness = FloatArray(worldSizeX.value * worldSizeY.value)

        private fun resize(newX: Int, newY: Int) {
            val newState = BooleanArray(newX * newY)
            nextGameState = BooleanArray(newX * newY)
            val newAliveness = FloatArray(newX * newY)
            for (y in 0 until min(size.y, newY)) {
                for (x in 0 until min(size.x, newX)) {
                    newState[y * newX + x] = gameState.value[y * size.x + x]
                    newAliveness[y * newX + x] = aliveness[y * size.x + x]
                }
            }
            size.x = newX
            size.y = newY
            gameState.set(newState)
            aliveness = newAliveness
        }

        fun step() {
            val state = gameState.value
            for (y in 0 until size.y) {
                for (x in 0 until size.x) {
                    val popCnt = countPopNeighbors(x, y)
                    val cell = this[x, y]
                    if (nextGameState[y * size.x + x] != cell) {
                        aliveness[y * size.x + x] += 1f
                    }
                    nextGameState[y * size.x + x] = if (popCnt == 2 && cell) true else popCnt == 3
                    aliveness[y * size.x + x] *= 0.99f
                }
            }
            gameState.set(nextGameState)
            nextGameState = state
        }

        private fun countPopNeighbors(x: Int, y: Int): Int {
            var popCnt = 0
            for (iy in -1..1) {
                for (ix in -1..1) {
                    if ((ix != 0 || iy != 0) && this[x + ix, y + iy]) {
                        popCnt++
                    }
                }
            }
            return popCnt
        }

        fun cellColor(x: Int, y: Int, colors: Colors, gradient: ColorGradient?): Color? {
            return if (this[x, y]) {
                colors.primary
            } else {
                gradient?.getColor(aliveness[y * size.x + x], 0f, 10f)
            }
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

        fun clear() {
            for (i in gameState.value.indices) {
                gameState.value[i] = false
                nextGameState[i] = false
                aliveness[i] = 0f
            }
        }

        fun randomize(p: Float) {
            clear()
            for (i in gameState.value.indices) gameState.value[i] = randomF() < p
        }

        fun loadAsciiState(state: String) {
            clear()
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

    private class GameCell(val isBorder: Boolean = false) {
        private var state = false

        var aliveness = 0f

        var isAlive: Boolean
            get() = state
            set(value) {
                if (!isBorder) {
                    aliveness *= 0.99f
                    if (state && !value) {
                        aliveness += 1f
                    }
                    state = value
                }
            }

        fun clear() {
            state = false
            aliveness = 0f
        }

        fun getColor(colors: Colors, gradient: ColorGradient?): Color? {
            return if (isAlive) {
                colors.primary
            } else {
                gradient?.getColor(aliveness, 0f, 10f)
            }
        }

        companion object {
            val BORDER = GameCell(true)
        }
    }
}