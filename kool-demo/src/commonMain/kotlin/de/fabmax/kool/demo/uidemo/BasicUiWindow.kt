package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class BasicUiWindow(uiDemo: UiDemo) : DemoWindow("Demo Window", uiDemo) {

    init {
        windowDockable.setFloatingBounds(width = Dp(500f), height = Dp(700f))
    }

    override fun UiScope.windowContent() = Column(Grow.Std, Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap, vertical = sizes.largeGap)

        Row {
            modifier.margin(bottom = sizes.smallGap)
            var clickCnt by remember(0)
            Button("A regular button: clicked $clickCnt times") {
                modifier
                    .onClick { clickCnt++ }
            }
        }

        Row {
            modifier.margin(bottom = sizes.smallGap)

            var checkboxState by remember(false)
            var radioButtonState by remember(false)
            var switchState by remember(false)

            Text("Checkbox") { modifier.alignY(AlignmentY.Center) }
            Checkbox(checkboxState) {
                modifier.margin(sizes.gap).onToggle { checkboxState = it }
            }

            Text("Radio Button") { modifier.alignY(AlignmentY.Center).margin(start = sizes.largeGap) }
            RadioButton(radioButtonState) {
                modifier.margin(sizes.gap).onToggle { radioButtonState = it }
            }

            Text("Switch") { modifier.alignY(AlignmentY.Center).margin(start = sizes.largeGap) }
            Switch(switchState) {
                modifier.margin(sizes.gap).onToggle { switchState = it }
            }
        }

        Row {
            modifier.margin(bottom = sizes.smallGap)
            Text("Text fields") { modifier.alignY(AlignmentY.Center).margin(end = sizes.gap) }
            var text1 by remember("")
            var text2 by remember("")
            TextField(text1) {
                modifier
                    .width(150.dp)
                    .hint("Ctrl+C to copy")
                    .onChange { text1 = it }
            }
            TextField(text2) {
                modifier
                    .width(150.dp)
                    .hint("Ctrl+V to paste")
                    .margin(start = sizes.largeGap)
                    .onChange { text2 = it }
            }
        }

        Row {
            modifier.margin(bottom = sizes.smallGap)
            Text("Slider") { modifier.alignY(AlignmentY.Center) }
            var sliderValue by remember(50f)
            Slider(sliderValue, 0f, 100f) {
                modifier
                    .margin(sizes.gap)
                    .width(sizes.largeGap * 6f)
                    .onChange { sliderValue = it }
            }
            Text("${sliderValue.toInt()}") { modifier.alignY(AlignmentY.Center).margin(start = sizes.gap) }
        }

        Row {
            Text("Combo-box") { modifier.alignY(AlignmentY.Center) }

            val items = remember { List(20) { "Item ${it + 1}" } }
            var selectedIndex by remember(0)
            ComboBox {
                modifier
                    .margin(start = sizes.gap)
                    .width(sizes.largeGap * 6f)
                    .items(items)
                    .selectedIndex(selectedIndex)
                    .onItemSelected { selectedIndex = it }
                Tooltip("You can also change this by using the scroll wheel while hovering")
            }
        }

        divider(colors.secondaryVariantAlpha(0.5f), marginTop = sizes.largeGap, marginBottom = sizes.gap)
        Text("A scroll area:") {
            modifier.margin(bottom = sizes.gap)
        }
        ScrollArea(
            vScrollbarModifier = {
                it.colors(
                    trackColor = colors.secondaryVariantAlpha(0.1f),
                    trackHoverColor = colors.secondaryVariantAlpha(0.2f)
                )
            },
            hScrollbarModifier = {
                it.colors(
                    trackColor = colors.secondaryVariantAlpha(0.1f),
                    trackHoverColor = colors.secondaryVariantAlpha(0.2f)
                )
            }
        ) {
            Column {
                Text("Text with two lines in a slightly larger font:\nThe second line is a tiny bit longer than the first one.") {
                    modifier
                        .margin(sizes.smallGap)
                        .font(sizes.largeText)
                }
                Row {
                    fun ImageScope.imageLabel(text: String) {
                        Text(text) {
                            modifier
                                .textColor(Color.WHITE)
                                .align(AlignmentX.Start, AlignmentY.Bottom)
                                .zLayer(UiSurface.LAYER_FLOATING)
                                .backgroundColor(Color.BLACK.withAlpha(0.5f))
                        }
                    }
                    Image {
                        modifier
                            .margin(sizes.smallGap)
                            .image(uiDemo.exampleImage)
                            .imageZ(UiSurface.LAYER_BACKGROUND)
                            .imageSize(ImageSize.FixedScale(0.3f))
                            .border(RectBorder(Color.RED, 1.dp))
                            .size(FitContent, FitContent)
                        imageLabel("sz: fit, img: fixedScale(0.3)")
                    }
                    Image {
                        modifier
                            .margin(sizes.smallGap)
                            .image(uiDemo.exampleImage)
                            .imageZ(UiSurface.LAYER_BACKGROUND)
                            .size(400.dp, 300.dp)
                            .imageSize(ImageSize.FitContent)
                            .border(RectBorder(Color.RED, 1.dp))
                        imageLabel("sz: 400x300, img: fit")
                    }
                    Image {
                        modifier
                            .margin(sizes.smallGap)
                            .image(uiDemo.exampleImage)
                            .imageZ(UiSurface.LAYER_BACKGROUND)
                            .size(400.dp, 300.dp)
                            .imageSize(ImageSize.ZoomContent)
                            .border(RectBorder(Color.RED, 1.dp))
                        imageLabel("sz: 400x300, img: zoom")
                    }
                    Image {
                        modifier
                            .margin(sizes.smallGap)
                            .image(uiDemo.exampleImage)
                            .imageZ(UiSurface.LAYER_BACKGROUND)
                            .size(400.dp, 300.dp)
                            .imageSize(ImageSize.Stretch)
                            .border(RectBorder(Color.RED, 1.dp))
                        imageLabel("sz: 400x300, img: stretch")
                    }
                }
                Row {
                    for (r in 0..270 step 90) {
                        Text("Another text with rotation: $r") {
                            modifier
                                .margin(sizes.smallGap)
                                .padding(sizes.largeGap)
                                .textRotation(r.toFloat())
                                .border(RoundRectBorder(colors.primaryVariant, sizes.gap, 2.dp, 6.dp))
                        }
                    }
                }
            }
        }

        divider(colors.secondaryVariantAlpha(0.5f), marginTop = sizes.largeGap, marginBottom = sizes.gap)
        Text("A longer list, click items to delete them:") {
            modifier.margin(bottom = sizes.gap)
        }
        val listItems by remember {
            mutableStateListOf<String>().apply {
                for (i in 1..500) {
                    add("Item $i")
                }
            }
        }
        var hoveredItemIndex by remember(-1)
        LazyList(
            vScrollbarModifier = {
                it.colors(
                    trackColor = colors.secondaryVariantAlpha(0.1f),
                    trackHoverColor = colors.secondaryVariantAlpha(0.2f)
                )
            }
        ) {
            itemsIndexed(listItems) { i, item ->
                val isHovered = i == hoveredItemIndex
                val bgColor = if (isHovered) {
                    colors.secondaryAlpha(0.5f)
                } else if (i % 2 == 0) {
                    if (colors.isLight) Color.BLACK.withAlpha(0.05f) else colors.secondaryAlpha(0.05f)
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
                        .onHover { hoveredItemIndex = i }
                        .onExit { hoveredItemIndex = -1 }
                        .onClick {
                            listItems.remove(item)
                        }
                }
            }
        }
    }
}