package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class Ui2Demo : DemoScene("UI2 Demo") {
    private val themeColors = Colors.darkColors()

    private val clickCnt = mutableStateOf(0)
    private val scrollState = ScrollState()
    private val listState = LazyListState()
    private val hoveredListItem = mutableStateOf<String?>(null)

    private val radioButtonState = mutableStateOf(false)
    private val checkboxState = mutableStateOf(false)
    private val switchState = mutableStateOf(false)
    private val sliderValue = mutableStateOf(1f)

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
        //  more ui elements: text field, combo-box
        //  elastic overscroll
        //  icons + images
        //  keyboard input
        //  focus
        //  clipboard
        //  popup menus, tooltips
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

        +UiSurface(themeColors) {
            modifier
                .width(500.dp)
                .height(WrapContent)
                .margin(top = 100.dp, bottom = 100.dp)
                .padding(8.dp)
                .layout(ColumnLayout)
                .alignX(AlignmentX.Center)
                .alignY(AlignmentY.Center)

            TestContent(listItems)

        }.apply { printTiming = true }
    }

    fun UiScope.TestContent(listItems: MutableList<String>) {
        Button("A regular button... clicked: ${clickCnt.use()}") {
            modifier
                .onClick { clickCnt.value += 1 }
        }

        ScrollArea(scrollState, height = 200.dp) {
            Column {
                modifier.margin(0.dp)

                Text("Text with two lines:\nThe second line is a little longer than the first one") {
                    modifier
                        .width(300.dp)
                        .margin(2.dp)
                }
                Row {
                    for (i in 1..5) {
                        Text("Another text no. $i with a lot of height") {
                            modifier
                                .height(300.dp)
                                .margin(2.dp)
                                .padding(20.dp)
                                .border(RoundRectBorder(colors.primary, 14.dp, 2.dp, 6.dp))
                        }
                    }
                }
            }
        }

        LazyList(
            listState,
            height = 400.dp,
            containerModifier = {
                it.margin(top = 16.dp)
            },
            vScrollbarModifier = {
                it.colors(
                    trackColor = colors.secondaryVariant.withAlpha(0.1f),
                    trackHoverColor = colors.secondaryVariant.withAlpha(0.15f)
                )
            }
        ) {
            itemsIndexed(listItems) { i, item ->
                val isHovered = item == hoveredListItem.use()
                val itemColor = when (item) {
                    "Item 17" -> MdColor.GREEN tone 200
                    else -> MdColor.RED tone 200
                }
                val bgColor = if (isHovered) {
                    itemColor
                } else if (i % 2 == 0) {
                    MdColor.GREY.withAlpha(0.05f)
                } else {
                    null
                }
                val textColor = if (isHovered) Color.BLACK else itemColor

                Text(item) {
                    modifier
                        .textColor(textColor)
                        .padding(8.dp)
                        .width(Grow())
                        .background(bgColor)
                        .onHover { hoveredListItem.set(item) }
                        .onExit { hoveredListItem.set(null) }
                        .onClick {
                            if (item == "Item 17") {
                                listItems += "Item ${listItems.size}"
                            } else {
                                listItems.remove(item)
                            }
                        }
                }
            }
        }

        Row {
            Text("Checkbox") { modifier.alignY(AlignmentY.Center) }
            Checkbox(checkboxState.use()) {
                modifier.onToggle { checkboxState.set(it) }
            }

            Text("Radio Button") { modifier.alignY(AlignmentY.Center).margin(start = 16.dp) }
            RadioButton(radioButtonState.use()) {
                modifier.onToggle { radioButtonState.set(it) }
            }

            Text("Switch") { modifier.alignY(AlignmentY.Center).margin(start = 16.dp) }
            Switch(switchState.use()) {
                modifier.onToggle { switchState.set(it) }
            }
        }
        Row {
            Text("Slider") { modifier.alignY(AlignmentY.Center) }
            Slider(sliderValue.use(), 0.8f, 3f) {
                modifier
                    .orientation(SliderOrientation.Horizontal)
                    .onChange { sliderValue.set(it) }
                    .onChangeEnd { UiScale.uiScale.set(it) }
            }
            Text("UI Scale: ${sliderValue.use()}") { modifier.alignY(AlignmentY.Center) }
        }

        Text("Yet another text") {
            modifier
                .width(Grow())
                .height(32.dp)
                .textAlignX(AlignmentX.End)
                .textAlignY(AlignmentY.Bottom)
                .margin(8.dp)
        }
    }
}