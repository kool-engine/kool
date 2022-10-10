package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class UiDemo : DemoScene("UI Demo") {

    private val windowState = WindowState()
    private val isMinimizedToTitle = mutableStateOf(false)
    private val clickCnt = mutableStateOf(0)
    private val scrollState = ScrollState()
    private val listState = LazyListState()
    private val hoveredListItem = mutableStateOf<String?>(null)

    private val radioButtonState = mutableStateOf(false)
    private val checkboxState = mutableStateOf(false)
    private val switchState = mutableStateOf(false)
    private val sliderValue = mutableStateOf(1f)
    private val checkboxTooltipState = MutableTooltipState()

    private val text1 = mutableStateOf("")
    private val text2 = mutableStateOf("")

    private val smallUi = Sizes.small()
    private val mediumUi = Sizes.medium()
    private val largeUi = Sizes.large()

    val selectedColors = mutableStateOf(Colors.darkColors())
    val selectedUiSize = mutableStateOf(mediumUi)

    private var imageTex: Texture2d? = null

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        imageTex = loadAndPrepareTexture("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
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

        // not for now
        //  smart update: only update nodes which actually changed (might not work with shared meshes), also not really
        //  needed because update is fast enough

        val listItems = mutableStateListOf<String>()
        var nextItem = 1
        for (i in 1..500) {
            listItems += "Item ${nextItem++}"
        }

        setupUiScene(true)

        windowState.apply {
            x.set(Dp(200f))
            y.set(Dp(200f))
            width.set(Dp(500f))
            height.set(Dp(700f))
        }

        +DockingHost().apply {
            +Window(windowState, name = "Demo Window") {
                surface.sizes = selectedUiSize.use()
                surface.colors = selectedColors.use()

                modifier
                    .isMinimizedToTitle(isMinimizedToTitle.use())
                    .onCloseClicked { println("close clicked") }
                    .minSize(200.dp, 200.dp)
                if (!isMinimizedToTitle.use()) modifier.onMinimizeClicked { isMinimizedToTitle.set(true) }
                if (isMinimizedToTitle.use()) modifier.onMaximizeClicked { isMinimizedToTitle.set(false) }

                TitleBar()
                if (!isMinimizedToTitle.value) {
                    WindowContent(listItems)
                }
            }//.apply { printTiming = true }

            +GameOfLifeWindow(this@UiDemo).window
            +ThemeEditorWindow(this@UiDemo).window

            onUpdate += {
                // set a left margin for the demo menu band
                dockingSurface.rootContainer.dockMarginStart.set(UiSizes.baseSize)
            }
        }
    }

    fun UiScope.WindowContent(listItems: MutableList<String>) = Box(Grow.Std, Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap, vertical = sizes.largeGap)
            .layout(ColumnLayout)

        Row {
            modifier.margin(bottom = sizes.smallGap)

            Button("A regular button... clicked: ${clickCnt.use()}") {
                modifier
                    .onClick { clickCnt.value += 1 }
                    .margin(end = sizes.largeGap)
            }
        }

        Row {
            modifier.margin(bottom = sizes.smallGap)
            Text("UI size:") { modifier.alignY(AlignmentY.Center) }

            fun TextScope.sizeButtonLabel(size: Sizes) {
                modifier
                    .margin(start = sizes.largeGap)
                    .alignY(AlignmentY.Center)
                    .onClick { selectedUiSize.set(size) }
            }

            Text("Small") { sizeButtonLabel(smallUi) }
            RadioButton(surface.sizes == smallUi) { modifier.margin(sizes.smallGap).onToggle { if (it) selectedUiSize.set(smallUi) } }

            Text("Medium") { sizeButtonLabel(mediumUi) }
            RadioButton(surface.sizes == mediumUi) { modifier.margin(sizes.smallGap).onToggle { if (it) selectedUiSize.set(mediumUi) } }

            Text("Large") { sizeButtonLabel(largeUi) }
            RadioButton(surface.sizes == largeUi) { modifier.margin(sizes.smallGap).onToggle { if (it) selectedUiSize.set(largeUi) } }
        }

        Row {
            modifier.margin(bottom = sizes.smallGap)
            Text("Global UI Scale:") { modifier.alignY(AlignmentY.Center) }
            Slider(sliderValue.use(), 0.8f, 2f) {
                modifier
                    .margin(sizes.gap)
                    .orientation(SliderOrientation.Horizontal)
                    .onChange { sliderValue.set(it) }
                    .onChangeEnd { UiScale.uiScale.set(it) }
            }
            Text(sliderValue.use().toString(2)) { modifier.alignY(AlignmentY.Center) }
        }

        Row {
            modifier.margin(bottom = sizes.smallGap)
            Text("Checkbox") { modifier.alignY(AlignmentY.Center) }
            Checkbox(checkboxState.use()) {
                modifier.margin(sizes.gap).onToggle { checkboxState.set(it) }
                Tooltip(checkboxTooltipState, "A simple checkbox")
            }

            Text("Radio Button") { modifier.alignY(AlignmentY.Center).margin(start = sizes.largeGap) }
            RadioButton(radioButtonState.use()) {
                modifier.margin(sizes.gap).onToggle { radioButtonState.set(it) }
            }

            Text("Switch") { modifier.alignY(AlignmentY.Center).margin(start = sizes.largeGap) }
            Switch(switchState.use()) {
                modifier.margin(sizes.gap).onToggle { switchState.set(it) }
            }
        }

        Row {
            modifier.margin(bottom = sizes.smallGap)
            TextField(text1.use()) {
                modifier
                    .width(150.dp)
                    .hint("A text field")
                    .onChange { text1.set(it) }
                    .onEnterPressed { println("typed: $it") }
            }
            TextField(text2.use()) {
                modifier
                    .width(150.dp)
                    .hint("Another text field")
                    .margin(start = sizes.largeGap)
                    .onChange { text2.set(it) }
            }
        }

        ScrollArea(scrollState) {
            Column {
                Text("Text with two lines in a slightly larger font:\nThe second line is a little longer than the first one") {
                    modifier
                        .margin(sizes.smallGap)
                        .font(sizes.largeText)
                }
                Row {
                    for (tint in listOf(Color.WHITE, MdColor.RED, MdColor.GREEN, MdColor.BLUE)) {
                        Image {
                            modifier
                                .padding(sizes.smallGap)
                                .image(imageTex)
                                .tint(tint)
                                .imageScale(0.2f)
                        }
                    }
                }
                Row {
                    for (r in TextRotation.values()) {
                        Text("Another text with rotation: \"$r\"") {
                            modifier
                                .margin(sizes.smallGap)
                                .padding(sizes.largeGap)
                                .textRotation(r)
                                .border(RoundRectBorder(colors.primaryVariant, sizes.gap, 2.dp, 6.dp))
                        }
                    }
                }
            }
        }

        LazyList(
            listState,
            containerModifier = {
                it.margin(top = sizes.largeGap)
            },
            vScrollbarModifier = {
                it.colors(
                    trackColor = colors.primaryVariant.withAlpha(0.1f),
                    trackHoverColor = colors.primaryVariant.withAlpha(0.15f)
                )
            }
        ) {
            itemsIndexed(listItems) { i, item ->
                val isHovered = item == hoveredListItem.use()
                val bgColor = if (isHovered) {
                    colors.primaryVariant
                } else if (i % 2 == 0) {
                    MdColor.GREY.withAlpha(0.05f)
                } else {
                    null
                }
                val textColor = if (isHovered) colors.onPrimary else colors.onBackground
                val isLarge = (i / 10) % 2 != 0
                val txt = if (isLarge) "$item [large]" else item

                Text(txt) {
                    modifier
                        .textColor(textColor)
                        .textAlignY(AlignmentY.Center)
                        .padding(sizes.smallGap)
                        .width(Grow.Std)
                        .height(if (isLarge) 64.dp else FitContent)
                        .backgroundColor(bgColor)
                        .onHover { hoveredListItem.set(item) }
                        .onExit { hoveredListItem.set(null) }
                        .onClick {
                            listItems.remove(item)
                        }
                }
            }
        }
    }
}