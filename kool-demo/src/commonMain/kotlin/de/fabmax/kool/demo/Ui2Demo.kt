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
        //  lazy list for fast update of large scrolling lists
        //  layer based z-coordinate
        //  use mutable state for surface size (auto update if viewport changes)
        //  smart update: only update nodes which actually changed (might not work with shared meshes)
        //  icons + images
        //  instanced meshes for primitive objects (round-/rects, circles)? might be difficult to solve correct depth
        //  keyboard input
        //  focus
        //  clipboard
        //  more ui elements: button, slider, checkbox, switch, radiobutton
        //  input context stack
        //  theming (colors, sizes)
        //  background style (e.g. rect vs. round-rect)
        //  animations
        //  custom drawing / canvas?

        val clickCnt = mutableStateOf(0)
        val scrollState = ScrollState()
        val buttonBgColor = mutableStateOf(MdColor.LIGHT_BLUE)

        val guiCam = OrthographicCamera().also { camera = it }
        onUpdate += {
            guiCam.left = 0f
            guiCam.top = 0f
            guiCam.right = it.renderPass.viewport.width.toFloat()
            guiCam.bottom = -it.renderPass.viewport.height.toFloat()
        }

        +UiSurface {
            modifier
                .width(200.dp)
                .height(500.dp)
                .margin(64.dp)
                .layout(ColumnLayout)
                .alignX(AlignmentX.End)
                .background(MdColor.PINK.withAlpha(0.3f))
                .padding(8.dp)

            Text("Some static text, clicked: ${clickCnt.use()}") {
                modifier
                    .alignX(AlignmentX.Start)
                    .background(buttonBgColor.use())
                    .margin(8.dp)
                    .padding(4.dp)
                    .onClick { clickCnt.value += 1 }
                    .onEnter { buttonBgColor.set(MdColor.PINK) }
                    .onExit { buttonBgColor.set(MdColor.LIGHT_BLUE) }
            }

            ScrollArea(
                scrollState,
                height = Grow(0.7f),
                backgroundColor = MdColor.LIME,
                scrollBarColor = Color.BLACK.withAlpha(0.5f)
            ) {
                Column {
                    Text("Text with two lines:\nThe second line is a little longer than the first one") {
                        modifier
                            .width(300.dp)
                            .margin(2.dp)
                            .background(MdColor.LIGHT_GREEN)
                    }
                    Text("Another text with a lot of height") {
                        modifier
                            .height(300.dp)
                            .margin(2.dp)
                            .background(MdColor.LIGHT_GREEN)
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
                    .margin(8.dp)
                    .background(MdColor.AMBER)
            }
            Text("Yet another text") {
                modifier
                    .width(Grow())
                    .height(32.dp)
                    .textAlignX(AlignmentX.End)
                    .textAlignY(AlignmentY.Bottom)
                    .background(MdColor.PURPLE)
            }
        }
    }
}