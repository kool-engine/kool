package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class Ui2Demo : DemoScene("UI2 Demo") {
    private val selectedColors = mutableStateOf(0)
    private val themeColors = listOf(
        "Neon" to Colors.darkColors(Color("b2ff00"), Color("7cb200")),
        "Lime" to Colors.darkColors(MdColor.LIME, MdColor.LIME tone 800),
        "Green" to Colors.darkColors(MdColor.LIGHT_GREEN, MdColor.LIGHT_GREEN tone 800),
        "Cyan" to Colors.darkColors(MdColor.CYAN, MdColor.CYAN tone 800),
        "Blue" to Colors.darkColors(MdColor.LIGHT_BLUE, MdColor.LIGHT_BLUE tone 800),
        "Purple" to Colors.darkColors(MdColor.PURPLE, MdColor.PURPLE tone 800, onAccent = Color.WHITE),
        "Pink" to Colors.darkColors(MdColor.PINK, MdColor.PINK tone 800),
        "Red" to Colors.darkColors(MdColor.RED, MdColor.RED tone 800),
    )

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
    private var selectedUiSize = mutableStateOf(mediumUi)

    private var imageTex: Texture2d? = null

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        imageTex = loadAndPrepareTexture("${Demo.assetStorageBase}/uv_checker_map.png")
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
        // - [ ] docking
        // - [x] size: absolute (dp), grow, wrap content
        // - [x] alignment: start, center, end / top, center, bottom
        // - [x] margin / outside gap
        // - [x] padding / inside gap

        // todo
        //  icons
        //  input context stack

        // not for now
        //  smart update: only update nodes which actually changed (might not work with shared meshes), also not really
        //  needed because update is fast enough

        val listItems = mutableListStateOf<String>()
        var nextItem = 1
        for (i in 1..500) {
            listItems += "Item ${nextItem++}"
        }

        setupUiScene(true)

        +UiSurface {
            surface.sizes = selectedUiSize.use()
            surface.colors = themeColors[selectedColors.use()].second

            modifier
                .width(500.dp)
                .height(WrapContent)
                .margin(top = 100.dp, bottom = 100.dp)
                .padding(horizontal = sizes.gap, vertical = sizes.largeGap)
                .layout(ColumnLayout)
                .alignX(AlignmentX.Center)
                .alignY(AlignmentY.Top)

            TestContent(listItems)

        }.apply { printTiming = true }
    }

    fun UiScope.TestContent(listItems: MutableList<String>) {
        Row {
            modifier.margin(bottom = sizes.gap)

            Button("A regular button... clicked: ${clickCnt.use()}") {
                modifier
                    .onClick { clickCnt.value += 1 }
                    .margin(end = sizes.largeGap)
            }
        }

        Row {
            modifier.margin(bottom = sizes.gap)
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
            modifier.margin(bottom = sizes.gap)
            Text("Accent color:") { modifier.alignY(AlignmentY.Center).margin(end = sizes.largeGap) }

            ComboBox {
                modifier
                    .margin(sizes.smallGap)
                    .width(sizes.gap * 10f)
                    .items(themeColors.map { it.first })
                    .selectedIndex(selectedColors.use())
                    .onItemSelected { selectedColors.set(it) }
            }
        }

        ScrollArea(scrollState, height = 200.dp) {
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
                                .imageScale(0.05f)
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
                                .border(RoundRectBorder(colors.accentVariant, sizes.gap, 2.dp, 6.dp))
                        }
                    }
                }
            }
        }

        LazyList(
            listState,
            height = 350.dp,
            containerModifier = {
                it.margin(top = sizes.largeGap)
            },
            vScrollbarModifier = {
                it.colors(
                    trackColor = colors.accentVariant.withAlpha(0.1f),
                    trackHoverColor = colors.accentVariant.withAlpha(0.15f)
                )
            }
        ) {
            itemsIndexed(listItems) { i, item ->
                val isHovered = item == hoveredListItem.use()
                val bgColor = if (isHovered) {
                    colors.accentVariant
                } else if (i % 2 == 0) {
                    MdColor.GREY.withAlpha(0.05f)
                } else {
                    null
                }
                val textColor = if (isHovered) colors.onAccent else colors.onBackground
                val isLarge = (i / 10) % 2 != 0
                val txt = if (isLarge) "$item [large]" else item

                Text(txt) {
                    modifier
                        .textColor(textColor)
                        .textAlignY(AlignmentY.Center)
                        .padding(sizes.smallGap)
                        .width(Grow())
                        .height(if (isLarge) 64.dp else WrapContent)
                        .background(bgColor)
                        .onHover { hoveredListItem.set(item) }
                        .onExit { hoveredListItem.set(null) }
                        .onClick {
                            listItems.remove(item)
                        }
                }
            }
        }

        Row {
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
            Text("Slider") { modifier.alignY(AlignmentY.Center) }
            Slider(sliderValue.use(), 0.8f, 3f) {
                modifier
                    .margin(sizes.gap)
                    .orientation(SliderOrientation.Horizontal)
                    .onChange { sliderValue.set(it) }
                    .onChangeEnd { UiScale.uiScale.set(it) }
            }
            Text("UI Scale: ${sliderValue.use()}") { modifier.alignY(AlignmentY.Center) }
        }

        Row {
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
    }
}