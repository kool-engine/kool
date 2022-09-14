package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class Ui2Demo : DemoScene("UI2 Demo") {
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
        //  theming (colors, fonts, sizes)
        //  z-layers (determine draw order)
        //  smooth scroll, elastic overscroll
        //  icons + images
        //  keyboard input
        //  focus
        //  clipboard
        //  more ui elements: button, slider, checkbox, switch, radiobutton, combo-box
        //  popup menus, tooltips
        //  input context stack
        //  animations
        //  custom drawing / canvas?

        // most likely not
        //  smart update: only update nodes which actually changed (might not work with shared meshes)

        val clickCnt = mutableStateOf(0)
        val scrollState = ScrollState()
        val listState = LazyListState()
        val buttonBgColor = mutableStateOf(MdColor.LIGHT_BLUE)
        val hoveredListItem = mutableStateOf<String?>(null)

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

        +UiSurface {
            modifier
                .width(200.dp)
                .height(Grow())
                .margin(start = 300.dp)
                .padding(8.dp)
                .layout(ColumnLayout)
                .alignX(AlignmentX.Start)
                .background(RectBackground(MdColor.PINK.withAlpha(0.3f)))

            Text("Some static text, clicked: ${clickCnt.use()}") {
                modifier
                    .background(RectBackground(buttonBgColor.use()))
                    .margin(8.dp)
                    .padding(4.dp)
                    .onClick { clickCnt.value += 1 }
                    .onEnter { buttonBgColor.set(MdColor.PINK) }
                    .onExit { buttonBgColor.set(MdColor.LIGHT_BLUE) }
            }

            ScrollArea(
                scrollState,
                height = 200.dp,
                background = RectBackground(MdColor.LIME),
                scrollbarColor = Color.BLACK.withAlpha(0.5f)
            ) {
                Column {
                    modifier.margin(0.dp)

                    Text("Text with two lines:\nThe second line is a little longer than the first one") {
                        modifier
                            .width(300.dp)
                            .margin(2.dp)
                            .background(RectBackground(MdColor.LIGHT_GREEN))
                    }
                    Text("Another text with a lot of height") {
                        modifier
                            .height(300.dp)
                            .margin(2.dp)
                            .padding(20.dp)
                            .border(RoundRectBorder(Color.RED, 6.dp, 14.dp, 2.dp))
                            .background(RoundRectBackground(MdColor.LIGHT_GREEN, 20.dp))
                    }
                }
            }

            LazyList(
                listState,
                height = 400.dp,
                background = RectBackground(MdColor.YELLOW),
                scrollbarColor = Color.BLACK.withAlpha(0.5f),
                containerModifier = { it.margin(8.dp) }
            ) {
//                var loopCnt = 0
                items(listItems) { item ->
                    val isHovered = item == hoveredListItem.use()
                    val bgColor = when {
                        isHovered && item == "Item 17" -> MdColor.GREEN
                        isHovered -> MdColor.RED
                        !isHovered && item == "Item 17" -> MdColor.GREEN tone 200
                        else -> MdColor.RED tone 200
                    }
                    Row {
                        modifier.margin(8.dp)
                        Text(item) {
                            modifier
                                .background(RectBackground(bgColor))
                                .margin(end =  8.dp)
                                .onEnter { hoveredListItem.set(item) }
                                .onExit { hoveredListItem.set(null) }
                                .onClick {
                                    if (item == "Item 17") {
                                        listItems += "Item ${nextItem++}"
                                    } else {
                                        listItems.remove(item)
                                    }
                                }
                        }
//                        Text("Loop Item: ${loopCnt++}") { }
                    }
                }
            }

            Text("Lorem ipsum") {
                modifier
                    .alignX(AlignmentX.End)
                    .textAlignX(AlignmentX.Start)
                    .textAlignY(AlignmentY.Center)
                    .width(100.dp)
                    .height(Grow(0.3f))
                    .background(RectBackground(MdColor.AMBER))
                    .margin(8.dp)
            }
            Text("Yet another text") {
                modifier
                    .width(Grow())
                    .height(32.dp)
                    .textAlignX(AlignmentX.End)
                    .textAlignY(AlignmentY.Bottom)
                    .background(RectBackground(MdColor.PURPLE))
                    .margin(8.dp)
            }
        }
    }
}