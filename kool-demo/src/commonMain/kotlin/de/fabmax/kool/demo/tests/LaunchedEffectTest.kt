package de.fabmax.kool.demo.tests

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene

import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.UniqueId

// A class to store the state of a single animated square
class MovingSquare(val id: String = UniqueId.nextId("square")) {
    val progress = AnimatableFloat(0f)
    val color = MdColor.PALETTE.random()
}

class LaunchedEffectTest : DemoScene("LaunchedEffectTest") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupUiScene()
        addPanelSurface {
            val movingSquares = remember { mutableStateListOf<MovingSquare>() }

            movingSquares.use().forEach { square ->
                LaunchedEffect(square.id) {
                    square.progress.animateTo(1f, duration = 2f)
                    movingSquares.remove(square)
                }

                Box {
                    val start = Vec2f(100f, 100f)
                    val end = Vec2f(uiNode.surface.viewportWidth.use() - 100f, uiNode.surface.viewportHeight.use() - 100f)
                    val p = square.progress.use()

                    val x = start.x + (end.x - start.x) * p
                    val y = start.y + (end.y - start.y) * p

                    modifier
                        .size(50.dp, 50.dp)
                        .margin(start = Dp.fromPx(x), top = Dp.fromPx(y))
                        .backgroundColor(square.color)
                }
            }

            Box(Grow.Std, Grow.Std) {
                Column {
                    modifier.align(AlignmentX.Center, AlignmentY.Center)

                    Button("Spawn Square") {
                        modifier
                            .margin(bottom = sizes.gap)
                            .onClick {
                                movingSquares.add(MovingSquare())
                            }

                        val isHovered = remember { mutableStateOf(false) }
                        modifier
                            .onEnter { isHovered.set(true) }
                            .onExit { isHovered.set(false) }

                        val buttonColor = rememberColorAnimatable(colors.secondaryVariant)
                        val textColor = rememberColorAnimatable(colors.onSecondary)

                        LaunchedEffect(isHovered.use()) {
                            if (isHovered.value) {
                                buttonColor.animateTo(colors.secondary)
                                textColor.animateTo(colors.onSecondary)
                            } else {
                                buttonColor.animateTo(colors.secondaryVariant)
                                textColor.animateTo(colors.onSecondary)
                            }
                        }

                        modifier.colors(
                            buttonColor = buttonColor.use(),
                            textColor = textColor.use(),
                            buttonHoverColor = buttonColor.use(),
                            textHoverColor = textColor.use()
                        )
                    }

                    Button("Not Animated Button") {
                    }
                }
            }
        }
    }
}