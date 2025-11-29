package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.CursorShape
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Easing
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A demo scene visualizing the various [Easing] functions available in kool.
 * It displays a grid of interactive graphs and a detailed comparison popup to visualize
 * how different easings affect animation properties like opacity, scale, and color.
 * @author dan_bat :>
 */
class EasingDemo : DemoScene("Easings") {

    private val colorBackground = Color("121212")
    private val colorCardBg = Color("252525")
    private val colorCardHover = Color("323232")

    private val colorCurveStart = Color("888888")
    private val colorCurveEnd = Color("44ffdd")

    private val colorAxes = Color("ffffff40")
    private val colorText = Color("cccccc")
    private val colorBorderDefault = Color("ffffff15")

    private val colorDemoStart = MdColor.AMBER
    private val colorDemoEnd = MdColor.DEEP_PURPLE

    private val curveGradient = ColorGradient(
        0f to colorCurveStart,
        1f to colorCurveEnd
    )

    private data class EasingItem(val name: String, val function: Easing.Easing) {
        val isHovered = mutableStateOf(false)
        var animProgress = 0f
        var hoverFade = 0f
    }

    private class EasingGroup(
        val name: String,
        val inItem: EasingItem,
        val outItem: EasingItem,
        val inOutItem: EasingItem
    )

    private val easingGroups = buildList<EasingGroup> {
        fun grp(name: String, inFunc: Easing.Easing, outFunc: Easing.Easing, inOutFunc: Easing.Easing) {
            add(EasingGroup(
                name,
                EasingItem("easeIn$name", inFunc),
                EasingItem("easeOut$name", outFunc),
                EasingItem("easeInOut$name", inOutFunc)
            ))
        }
        grp("Sine", Easing.easeInSine, Easing.easeOutSine, Easing.easeInOutSine)
        grp("Quad", Easing.easeInQuad, Easing.easeOutQuad, Easing.easeInOutQuad)
        grp("Cubic", Easing.easeInCubic, Easing.easeOutCubic, Easing.easeInOutCubic)
        grp("Quart", Easing.easeInQuart, Easing.easeOutQuart, Easing.easeInOutQuart)
        grp("Quint", Easing.easeInQuint, Easing.easeOutQuint, Easing.easeInOutQuint)
        grp("Expo", Easing.easeInExpo, Easing.easeOutExpo, Easing.easeInOutExpo)
        grp("Circ", Easing.easeInCirc, Easing.easeOutCirc, Easing.easeInOutCirc)
        grp("Back", Easing.easeInBack, Easing.easeOutBack, Easing.easeInOutBack)
        grp("Elastic", Easing.easeInElastic, Easing.easeOutElastic, Easing.easeInOutElastic)
        grp("Bounce", Easing.easeInBounce, Easing.easeOutBounce, Easing.easeInOutBounce)
    }

    private val selectedItem = mutableStateOf<EasingItem?>(null)

    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupUiScene()

        addPanelSurface(
            colors = Colors.darkColors(
                background = colorBackground,
                backgroundVariant = colorCardBg,
                primary = colorCurveEnd,
                onBackground = colorText,
                secondary = colorCurveStart,
                secondaryVariant = colorBorderDefault
            ),
            sizes = Settings.uiSize.value.sizes,
            layout = CellLayout
        ) {
            surface.sizes = Settings.uiSize.use().sizes

            Column(Grow.Std, Grow.Std) {
                modifier.padding(sizes.gap)

                Text("Easing Functions") {
                    modifier
                        .alignX(AlignmentX.Center)
                        .font(sizes.largeText)
                        .textColor(Color.WHITE)
                        .margin(bottom = sizes.largeGap)
                }

                ScrollArea(
                    containerModifier = { it.background(null) }
                ) {
                    modifier.width(Grow.Std)

                    val col1 = easingGroups.filterIndexed { index, _ -> index % 2 == 0 }
                    val col2 = easingGroups.filterIndexed { index, _ -> index % 2 != 0 }

                    Row(FitContent) {
                        modifier.alignX(AlignmentX.Center)

                        Column(FitContent) {
                            for (group in col1) {
                                EasingGroupRow(group)
                            }
                        }

                        Box(width = sizes.largeGap * 2f, height = 1.dp) { }

                        Column(FitContent) {
                            for (group in col2) {
                                EasingGroupRow(group)
                            }
                        }
                    }
                }

                FooterLink()
            }

            val selected = selectedItem.use()
            if (selected != null) {
                ComparisonPopup(selected)
            }
        }
    }

    private fun UiScope.ComparisonPopup(item: EasingItem) {
        val animator = remember { AnimatableFloat(0f) }
        var isClosing by remember { mutableStateOf(false) }
        var activeItem by remember { mutableStateOf<EasingItem?>(null) }

        fun close() {
            if (!isClosing) {
                isClosing = true
                coroutineScope.launch {
                    animator.animateTo(0f, 0.2f, Easing.easeInQuad)
                    selectedItem.set(null)
                    activeItem = null
                }
            }
        }

        if (activeItem != item) {
            activeItem = item
            isClosing = false
            animator.set(0f)
            coroutineScope.launch {
                animator.animateTo(1f, 0.4f, Easing.easeOutBack)
            }
        }

        val animValue by animator
        val alpha = animValue.clamp(0f, 1f)

        val baseSize = sizes.largeGap * 2.5f
        val slideOffset = baseSize * (1f - animValue)
        val dialogWidth = baseSize * 10f

        Box(Grow.Std, Grow.Std) {
            modifier
                .backgroundColor(Color.BLACK.withAlpha(0.8f * alpha))
                .zLayer(UiSurface.LAYER_POPUP)
                .onClick { close() }
                .align(AlignmentX.Center, AlignmentY.Center)
                .onHover { PointerInput.cursorShape = CursorShape.DEFAULT }

            Box {
                modifier
                    .width(dialogWidth)
                    .height(FitContent)
                    .margin(top = slideOffset)
                    .background(RoundRectBackground(colorBackground.withAlpha(alpha), sizes.largeGap))
                    .border(RoundRectBorder(colorCurveEnd.withAlpha(0.5f * alpha), sizes.largeGap, 2.dp))
                    .padding(sizes.largeGap)
                    .align(AlignmentX.Center, AlignmentY.Center)
                    .onClick { it.isConsumed = true }
                    .onHover { PointerInput.cursorShape = CursorShape.DEFAULT }

                val contentAlpha = alpha.clamp(0f, 1f)

                Column(Grow.Std) {
                    Text("Visualize: ${item.name}") {
                        modifier
                            .font(sizes.largeText)
                            .textColor(colorCurveEnd.withAlpha(contentAlpha))
                            .alignX(AlignmentX.Center)
                            .margin(bottom = sizes.largeGap)
                    }

                    var animationTarget by remember(0f)
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(500)
                            animationTarget = 1f
                            delay(1500)
                            animationTarget = 0f
                            delay(1000)
                        }
                    }

                    val progressLinear by animateFloatAsState(
                        targetValue = animationTarget,
                        animationSpec = tween(duration = 1f, easing = Easing.linear)
                    )

                    val progressEased by animateFloatAsState(
                        targetValue = animationTarget,
                        animationSpec = tween(duration = 1f, easing = item.function)
                    )

                    Box(Grow.Std, baseSize * 3f) {
                        modifier
                            .background(RoundRectBackground(colorCardBg.withAlpha(contentAlpha), sizes.gap))
                            .margin(bottom = sizes.gap)

                        modifier.background(UiRenderer { node ->
                            renderGraphComparison(node, progressLinear, progressEased, contentAlpha)
                        })
                    }

                    val tileSize = baseSize * 2f
                    Row(Grow.Std) {
                        modifier.margin(bottom = sizes.largeGap)

                        Column(Grow.Std) {
                            modifier.margin(end = sizes.gap)

                            DemoTile("Opacity", contentAlpha) {
                                Box(tileSize, tileSize) {
                                    modifier
                                        .align(AlignmentX.Center, AlignmentY.Center)
                                        .background(RoundRectBackground(
                                            colorCurveEnd.withAlpha(progressEased * contentAlpha),
                                            sizes.gap
                                        ))
                                }
                            }

                            DemoTile("Scale", contentAlpha) {
                                Box(tileSize, tileSize) {
                                    modifier.align(AlignmentX.Center, AlignmentY.Center)

                                    val maxScale = 1.3f
                                    val targetSize = baseSize * maxScale

                                    Box(targetSize, targetSize) {
                                        modifier
                                            .align(AlignmentX.Center, AlignmentY.Center)
                                            .border(CircularBorder(colors.onBackground.withAlpha(0.2f * contentAlpha), 1.dp))
                                    }

                                    Box {
                                        val scaleValue = 0.5f + (progressEased * 0.8f)
                                        modifier
                                            .align(AlignmentX.Center, AlignmentY.Center)
                                            .background(CircularBackground(colorCurveEnd.withAlpha(contentAlpha)))
                                            .size(baseSize * scaleValue, baseSize * scaleValue)
                                    }
                                }
                            }
                        }

                        Column(Grow.Std) {
                            DemoTile("Color", contentAlpha) {
                                Box(tileSize, tileSize) {
                                    val mixColor = colorDemoStart.mix(colorDemoEnd, progressEased).withAlpha(contentAlpha)
                                    modifier
                                        .align(AlignmentX.Center, AlignmentY.Center)
                                        .background(RoundRectBackground(mixColor, sizes.gap))
                                }
                            }

                            DemoTile("Morph (Shape)", contentAlpha) {
                                Box(tileSize, tileSize) {
                                    modifier.align(AlignmentX.Center, AlignmentY.Center)
                                    val size = baseSize * 1.2f
                                    val maxRadius = size.value / 2f
                                    val minRadius = 12f
                                    val radius = minRadius + (maxRadius - minRadius) * progressEased

                                    Box(size, size) {
                                        modifier
                                            .align(AlignmentX.Center, AlignmentY.Center)
                                            .background(RoundRectBackground(
                                                colorCurveEnd.withAlpha(contentAlpha),
                                                radius.dp
                                            ))
                                    }
                                }
                            }
                        }
                    }

                    Button("Close") {
                        modifier
                            .alignX(AlignmentX.Center)
                            .width(baseSize * 3f)
                            .margin(bottom = sizes.gap)
                            .onClick { close() }
                            .colors(
                                buttonColor = colorCardBg.withAlpha(contentAlpha),
                                textColor = colorCurveEnd.withAlpha(contentAlpha),
                                buttonHoverColor = colorCardHover.withAlpha(contentAlpha),
                                textHoverColor = Color.WHITE.withAlpha(contentAlpha)
                            )
                            .border(RoundRectBorder(
                                colorCurveEnd.withAlpha(0.5f * contentAlpha),
                                sizes.smallGap,
                                1.dp
                            ))
                    }
                }
            }
        }
    }

    private fun UiScope.DemoTile(title: String, alpha: Float, block: UiScope.() -> Unit) {
        Column(Grow.Std) {
            modifier.margin(bottom = sizes.gap)
            Text(title) {
                modifier
                    .textColor(colors.onBackground.withAlpha(0.6f * alpha))
                    .margin(bottom = sizes.smallGap)
            }
            Box(Grow.Std, FitContent) {
                modifier
                    .background(RoundRectBackground(colorCardBg.withAlpha(alpha), sizes.gap))
                    .padding(sizes.smallGap)
                block()
            }
        }
    }

    private fun renderGraphComparison(node: UiNode, linearPos: Float, easedPos: Float, alpha: Float = 1f) {
        node.apply {
            val builder = getPlainBuilder(UiSurface.LAYER_BACKGROUND)
            val fontBuilder = getTextBuilder(Font.DEFAULT_FONT, UiSurface.LAYER_FLOATING)
            val draw = getUiPrimitives(UiSurface.LAYER_DEFAULT)

            val w = widthPx
            val h = heightPx
            val graphBaseSize = sizes.largeGap.px
            val trackPadding = graphBaseSize * 2
            val trackWidth = w - trackPadding * 2
            val centerY = h / 2f
            val trackSpacing = graphBaseSize * 3

            val yLinear = centerY - trackSpacing / 2
            val yEased = centerY + trackSpacing / 2

            val axesCol = MdColor.GREY.withAlpha(0.5f * alpha)
            val textCol = colors.onBackground.withAlpha(colors.onBackground.a * alpha)
            val objLinearCol = MdColor.GREY.withAlpha(alpha)
            val objEasedCol = colorCurveEnd.withAlpha(alpha)

            builder.configured(axesCol) {
                line(trackPadding, yLinear, trackPadding + trackWidth, yLinear, 2.dp.px)
                line(trackPadding, yEased, trackPadding + trackWidth, yEased, 2.dp.px)
            }

            fontBuilder.configured(textCol) {
                withTransform {
                    translate(trackPadding, yLinear - 20.dp.px, 0f)
                    scale(1f, -1f, 1f)
                    text(sizes.normalText) { text = "Linear"; origin.set(0f, 0f, 0f) }
                }
                withTransform {
                    translate(trackPadding, yEased - 20.dp.px, 0f)
                    scale(1f, -1f, 1f)
                    text(sizes.normalText) { text = "Eased"; origin.set(0f, 0f, 0f) }
                }
            }

            // Draw indicators
            val xLinear = trackPadding + linearPos * trackWidth
            val xEased = trackPadding + easedPos * trackWidth

            draw.localRoundRect(
                xLinear - graphBaseSize / 2, yLinear - graphBaseSize / 2,
                graphBaseSize, graphBaseSize, 4.dp.px,
                objLinearCol
            )

            draw.localRoundRect(
                xEased - graphBaseSize / 2, yEased - graphBaseSize / 2,
                graphBaseSize, graphBaseSize, 4.dp.px,
                objEasedCol
            )
        }
    }

    private fun UiScope.EasingGroupRow(group: EasingGroup) {
        Row(FitContent) {
            modifier.margin(bottom = sizes.largeGap)
            EasingCard(group.inItem)
            Box(width = sizes.smallGap, height = 1.dp) { }
            EasingCard(group.outItem)
            Box(width = sizes.smallGap, height = 1.dp) { }
            EasingCard(group.inOutItem)
        }
    }

    private fun UiScope.EasingCard(item: EasingItem) {
        val cardSize = sizes.largeGap * 6.5f

        Column {
            modifier
                .width(cardSize)
                .onHover {
                    if (selectedItem.value == null) {
                        PointerInput.cursorShape = CursorShape.HAND
                    }
                }
                .onEnter {
                    if (selectedItem.value == null) {
                        item.isHovered.set(true)
                    }
                }
                .onExit {
                    item.isHovered.set(false)
                    PointerInput.cursorShape = CursorShape.DEFAULT
                }
                .onClick {
                    item.isHovered.set(false)
                    selectedItem.set(item)
                }

            Box {
                val isHovered = item.isHovered.use()
                val bgColor = if (isHovered) colorCardHover else colorCardBg
                val borderColor = if (isHovered) colorCurveEnd else colorBorderDefault

                modifier
                    .size(cardSize, cardSize)
                    .background(RoundRectBackground(bgColor, sizes.smallGap))
                    .border(RoundRectBorder(borderColor, sizes.smallGap, if (isHovered) 2.dp else 1.dp))

                modifier.background(UiRenderer { node ->
                    renderGraphList(node, item)
                })
            }

            Text(item.name) {
                modifier
                    .alignX(AlignmentX.Center)
                    .margin(top = sizes.smallGap)
                    .textColor(if (item.isHovered.use()) Color.WHITE else colors.onBackground)
            }
        }
    }

    private fun UiScope.FooterLink() {
        val isPopupActive = selectedItem.use() != null

        Box {
            var isHovered by remember(false)
            if (isPopupActive) isHovered = false

            val linkColor = if (isHovered) MdColor.LIGHT_BLUE else Color.WHITE.withAlpha(0.5f)

            modifier
                .align(AlignmentX.Center, AlignmentY.Bottom)
                .margin(sizes.gap)
                .apply {
                    if (!isPopupActive) {
                        onHover { PointerInput.cursorShape = CursorShape.HAND }
                        onEnter {
                            isHovered = true
                        }
                        onExit {
                            isHovered = false
                            PointerInput.cursorShape = CursorShape.DEFAULT
                        }
                        onClick {
                            KoolSystem.requireContext().openUrl("https://easings.net/")
                        }
                    }
                }

            Column {
                Text("Source: easings.net") {
                    modifier.textColor(linkColor).font(sizes.smallText)
                }
                Box(width = Grow.Std, height = 1.dp) {
                    modifier.backgroundColor(linkColor)
                }
            }
        }
    }

    private fun renderGraphList(node: UiNode, item: EasingItem) {
        val isHovered = item.isHovered.use(node.surface)
        val dt = Time.deltaT

        if (isHovered) {
            item.hoverFade = (item.hoverFade + dt * 5f).clamp()
            item.animProgress += dt * 0.7f
            if (item.animProgress > 1.5f) {
                item.animProgress = -0.3f
            }
            node.surface.triggerUpdate()
        } else {
            item.hoverFade = (item.hoverFade - dt * 5f).clamp()
            item.animProgress = 0f
            if (item.hoverFade > 0f) {
                node.surface.triggerUpdate()
            }
        }

        node.apply {
            val builder = getPlainBuilder(UiSurface.LAYER_DEFAULT)
            val prims = getUiPrimitives(UiSurface.LAYER_DEFAULT)
            val fontBuilder = getTextBuilder(Font.DEFAULT_FONT, UiSurface.LAYER_FLOATING)

            val w = widthPx
            val h = heightPx
            val pad = w * 0.20f

            val graphW = w - pad * 2
            val graphH = h - pad * 2
            val y0 = h - pad

            val steps = 128
            for (i in 0 until steps) {
                val t1 = i / steps.toFloat()
                val t2 = (i + 1) / steps.toFloat()

                val v1 = item.function.eased(t1)
                val v2 = item.function.eased(t2)

                val px1 = pad + t1 * graphW
                val py1 = y0 - v1 * graphH
                val px2 = pad + t2 * graphW
                val py2 = y0 - v2 * graphH

                if (py1 in 0f..h || py2 in 0f..h) {
                    val c = curveGradient.getColor(t1)
                    builder.configured(c) {
                        line(px1, py1, px2, py2, 2.dp.px)
                    }
                }
            }

            if (item.hoverFade > 0f) {
                val alpha = item.hoverFade
                val axisCol = colorAxes.withAlpha(0.5f * alpha)
                val textCol = colors.onBackground.withAlpha(0.8f * alpha)
                val accentCol = colors.primary.withAlpha(alpha)

                builder.configured(axisCol) {
                    line(pad, y0 + 2, pad + graphW, y0 + 2, 1.5f)
                    line(pad, y0 + 2, pad, y0 - graphH, 1.5f)
                    line(pad, y0 - graphH, pad + graphW, y0 - graphH, 1f)
                }

                fontBuilder.configured(textCol) {
                    withTransform {
                        translate(pad + graphW + 4.dp.px, y0 - 4.dp.px, 0f)
                        scale(1f, -1f, 1f)
                        text(sizes.smallText) { text = "t"; origin.set(0f, 0f, 0f) }
                    }
                    withTransform {
                        translate(pad - 8.dp.px, y0 - graphH - 8.dp.px, 0f)
                        scale(1f, -1f, 1f)
                        text(sizes.smallText) { text = "x"; origin.set(0f, 0f, 0f) }
                    }
                }

                val rawT = item.animProgress
                if (rawT >= 0f) {
                    val t = rawT.clamp(0f, 1f)
                    val v = item.function.eased(t)

                    val dotX = pad + t * graphW
                    val dotY = y0 - v * graphH

                    val sliderX = w - pad * 0.4f
                    val railTop = y0 - graphH

                    builder.configured(colorAxes.withAlpha(0.3f * alpha)) {
                        line(sliderX, y0, sliderX, railTop, 2.dp.px)
                    }

                    builder.configured(accentCol) {
                        val sz = 3.dp.px
                        val p1 = Vec3f(sliderX - sz, dotY, 0f)
                        val p2 = Vec3f(sliderX + sz, dotY - sz, 0f)
                        val p3 = Vec3f(sliderX + sz, dotY + sz, 0f)

                        val i1 = vertex(p1, Vec3f.Z_AXIS)
                        val i2 = vertex(p2, Vec3f.Z_AXIS)
                        val i3 = vertex(p3, Vec3f.Z_AXIS)
                        geometry.addTriIndices(i1, i2, i3)
                    }

                    prims.localCircle(dotX, dotY, 4.dp.px, accentCol)
                    prims.localCircle(dotX, y0 + 2, 2.5f.dp.px, colorAxes.withAlpha(0.5f * alpha))
                }
            }
        }
    }
}