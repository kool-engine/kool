package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class Ui2Demo : DemoScene("UI2 Demo") {
    private val themeColors = Colors.darkColors()

    private val clickCnt = mutableStateOf(0)
    private val scrollState = ScrollState()
    private val listState = LazyListState()
    private val buttonBgColor = mutableStateOf(themeColors.primaryVariant)
    private val hoveredListItem = mutableStateOf<String?>(null)

    override fun Scene.setupMainScene(ctx: KoolContext) {
        // new improved ui system
        // - somewhat jetpack compose inspired ui
        // - is purely functional possible without compiler plugin?
        // - top element is surface, below that boxes
        // desired features
        // - [x] traditional ui coord system: top left origin
        // - [x] layout via nested boxes
        // - [x] lazy list for fast update of large scrolling lists
        //   - [ ] pie menu?
        // - [x] clip content to bounds
        // - [x] scrollable content
        // - [ ] docking
        // - [x] size: absolute (dp), grow, wrap content
        // - [x] alignment: start, center, end / top, center, bottom
        // - [x] margin / outside gap
        // - [x] padding / inside gap
        // - [x] arbitrary number of fonts
        // - [ ] arbitrary number of textures / images

        // todo
        //  z-layers (determine draw order)
        //  scale-aware fonts
        //  elastic overscroll
        //  icons + images
        //  keyboard input
        //  focus
        //  clipboard
        //  more ui elements: button, text field, slider, checkbox, switch, radiobutton, combo-box
        //  popup menus, tooltips
        //  input context stack
        //  animations
        //  custom drawing / canvas?

        // most likely not
        //  smart update: only update nodes which actually changed (might not work with shared meshes)

        val listItems = mutableListStateOf<String>()
        var nextItem = 1
        for (i in 1..500) {
            listItems += "Item ${nextItem++}"
        }

        val guiCam = OrthographicCamera().also { camera = it }
        onUpdate += {
            // setup camera to cover viewport size with origin in upper left corner
            // camera clip space uses OpenGL coordinates -> y-axis points downwards, i.e. bottom coordinate has to be
            // set to negative viewport height
            // UI surface internally mirrors y-axis to get a regular UI coordinate system (however, this means tirangle
            // index order or face orientation has to be inverted)
            guiCam.left = 0f
            guiCam.top = 0f
            guiCam.right = it.renderPass.viewport.width.toFloat()
            guiCam.bottom = -it.renderPass.viewport.height.toFloat()
        }

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

//            Column {
//                modifier.width(Grow())
//                Text("Hallo Welt") {
//                    modifier.width(Grow()).background(Color.RED)
//                }
//            }
        }
    }

    fun UiScope.TestContent(listItems: MutableList<String>) {
        Text("Some static text, clicked: ${clickCnt.use()}") {
            modifier
                .background(RectBackground(buttonBgColor.use()))
                .margin(8.dp)
                .padding(4.dp)
                .onClick { clickCnt.value += 1 }
                .onEnter { buttonBgColor.set(colors.primary) }
                .onExit { buttonBgColor.set(colors.primaryVariant) }
                .foreground(colors.onPrimary)
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
                //it.trackColor = colors.secondaryVariant.withAlpha(0.15f)
                it.trackColorHovered = colors.secondaryVariant.withAlpha(0.1f)
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
                        .foreground(textColor)
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

        val accent1500 = Color.fromHex("FF6F00")
        val accent1700 = Color.fromHex("7F4519")
        val accent1900 = Color.fromHex("4C301A")

        val accent2500 = Color.fromHex("197CFF")
        val accent2700 = Color.fromHex("18467A")
        val accent2900 = Color.fromHex("1C324F")

        val neutral500 = Color.fromHex("C7D4E5")
        val neutral700 = Color.fromHex("858B93")
        val neutral900 = Color.fromHex("505459")

        SwitchDummy(neutral500, neutral700, neutral700, neutral900)
        SwitchDummy(accent1500, accent1700, accent1700, accent1900)
        SwitchDummy(accent2500, accent2700, accent2700, accent2900)

        Text("Yet another text") {
            modifier
                .width(Grow())
                .height(32.dp)
                .textAlignX(AlignmentX.End)
                .textAlignY(AlignmentY.Bottom)
                .margin(8.dp)
        }
    }

    fun UiScope.SwitchDummy(knobActive: Color, trackActive: Color, knobInactive: Color, trackInactive: Color) {
        Row {
            modifier.margin(32.dp)
            Box {
                Box {
                    modifier
                        .width(40.dp)
                        .height(20.dp)
                        .alignY(AlignmentY.Center)
                        .background(RoundRectBackground(trackInactive, 10.dp))
                }
                Box {
                    modifier
                        .margin(start = 0.dp)
                        .width(20.dp)
                        .height(20.dp)
                        .alignY(AlignmentY.Center)
                        .background(RoundRectBackground(knobInactive, 10.dp))
                }
            }

            Box {
                modifier.margin(start = 32.dp)
                Box {
                    modifier
                        .width(40.dp)
                        .height(20.dp)
                        .alignY(AlignmentY.Center)
                        .background(RoundRectBackground(trackActive, 10.dp))
                }
                Box {
                    modifier
                        .margin(start = 20.dp)
                        .width(20.dp)
                        .height(20.dp)
                        .alignY(AlignmentY.Center)
                        .background(RoundRectBackground(knobActive, 10.dp))
                }
            }
        }
    }
}