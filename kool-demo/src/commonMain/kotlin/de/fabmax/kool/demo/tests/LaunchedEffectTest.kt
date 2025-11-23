package de.fabmax.kool.demo.tests

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.input.CursorShape
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Easing
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class LaunchedEffectTest : DemoScene("Animation API Test") {

    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupUiScene(clearColor = ClearColorFill(Color("222222")))

        addPanelSurface(
            colors = Colors.darkColors(),
            sizes = Sizes.medium
        ) {
            modifier
                .width(400.dp)
                .height(FitContent)
                .align(AlignmentX.Center, AlignmentY.Center)
                .background(RoundRectBackground(colors.background, sizes.gap))
                .border(RoundRectBorder(colors.primaryVariant, sizes.gap, sizes.borderWidth))
                .padding(sizes.largeGap)

            Column(Grow.Std, FitContent) {

                SectionTitle("1. Hover Animation")
                AnimatedButton("Hover Me!")

                Divider()

                SectionTitle("2. Shape Morphing")
                MorphingBox()

                Divider()

                SectionTitle("3. Movement Tween")
                ToggleSwitch()
            }
        }
    }


    private fun UiScope.SectionTitle(text: String) {
        Text(text) {
            modifier
                .font(sizes.largeText)
                .textColor(colors.primary)
                .alignX(AlignmentX.Center)
                .margin(bottom = sizes.gap)
        }
    }

    private fun UiScope.Divider() {
        Box(Grow.Std, 1.dp) {
            modifier
                .backgroundColor(colors.secondaryVariant.withAlpha(0.5f))
                .margin(vertical = sizes.largeGap)
        }
    }



    private fun UiScope.AnimatedButton(text: String) {
        val isHovered = remember(false)

        val targetBg = if (isHovered.value) colors.primary else colors.secondaryVariant
        val targetText = if (isHovered.value) colors.onPrimary else colors.onSecondary
        val targetScale = if (isHovered.value) 1.1f else 1.0f

        val animBg by animateColorAsState(targetBg, tween(0.2f))
        val animTextColor by animateColorAsState(targetText, tween(0.2f))
        val animScale by animateFloatAsState(targetScale, tween(0.2f, Easing.sqrRev))

        Box {
            modifier
                .alignX(AlignmentX.Center)
                .onEnter {
                    isHovered.set(true)
                    PointerInput.cursorShape = CursorShape.HAND
                }
                .onExit {
                    isHovered.set(false)
                    PointerInput.cursorShape = CursorShape.DEFAULT
                }
                .onClick { }

            Box {
                modifier
                    .background(RoundRectBackground(animBg, sizes.gap))
                    .padding(
                        horizontal = sizes.largeGap * animScale,
                        vertical = sizes.gap * animScale
                    )
                    .align(AlignmentX.Center, AlignmentY.Center)

                Text(text) {
                    modifier
                        .textColor(animTextColor)
                }
            }
        }
    }

    private fun UiScope.MorphingBox() {
        var isExpanded by remember(false)

        val targetSize = if (isExpanded) 150f else 80f
        val targetRadius = if (isExpanded) 75f else 16f
        val targetColor = if (isExpanded) MdColor.AMBER else MdColor.CYAN

        val animSize by animateFloatAsState(targetSize, tween(0.5f, Easing.quadRev))
        val animRadius by animateFloatAsState(targetRadius, tween(0.5f, Easing.quadRev))
        val animColor by animateColorAsState(targetColor, tween(0.5f))

        Box {
            modifier.alignX(AlignmentX.Center)

            Box {
                modifier
                    .size(animSize.dp, animSize.dp)
                    .layout(ColumnLayout)
                    .background(RoundRectBackground(animColor, Dp(animRadius)))
                    .border(RoundRectBorder(colors.onBackground, Dp(animRadius), 2.dp))
                    .onClick { isExpanded = !isExpanded }
                    .onEnter { PointerInput.cursorShape = CursorShape.HAND }
                    .onExit { PointerInput.cursorShape = CursorShape.DEFAULT }

                Text(if (isExpanded) "Circle" else "Box") {
                    modifier
                        .align(AlignmentX.Center, AlignmentY.Center)
                        .textColor(Color.BLACK.withAlpha(0.7f))
                        .font(sizes.largeText)
                }
            }
        }
    }

    private fun UiScope.ToggleSwitch() {
        var isToggled by remember(false)

        val trackWidth = 100f
        val knobSize = 32f
        val padding = 4f
        val trackHeight = knobSize + padding * 2

        val startX = padding
        val endX = trackWidth - knobSize - padding

        val targetX = if (isToggled) endX else startX
        val targetColor = if (isToggled) MdColor.GREEN else MdColor.RED

        val animX by animateFloatAsState(targetX, tween(0.25f, Easing.smooth))
        val animColor by animateColorAsState(targetColor, tween(0.25f))

        Box {
            modifier
                .size(trackWidth.dp, trackHeight.dp)
                .alignX(AlignmentX.Center)
                .background(RoundRectBackground(colors.onBackground.withAlpha(0.2f), 100.dp))
                .onClick { isToggled = !isToggled }
                .onEnter { PointerInput.cursorShape = CursorShape.HAND }
                .onExit { PointerInput.cursorShape = CursorShape.DEFAULT }

            Box {
                modifier
                    .size(knobSize.dp, knobSize.dp)
                    .alignY(AlignmentY.Center)
                    .margin(start = Dp.fromPx(animX))
                    .background(CircularBackground(animColor))
                    .border(CircularBorder(Color.WHITE, 2.dp))
            }
        }
    }
}