package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

interface ComboBoxScope : UiScope {
    override val modifier: ComboBoxModifier
    val isHovered: Boolean
}

open class ComboBoxModifier(surface: UiSurface) : UiModifier(surface) {
    var font: Font by property { it.sizes.normalText }
    var items: List<Any> by property(emptyList())
    var selectedIndex: Int by property(0)

    var textColor: Color by property { it.colors.onBackground }
    var textBackgroundColor: Color by property { it.colors.secondaryVariantAlpha(0.5f) }
    var textBackgroundHoverColor: Color by property { it.colors.secondaryAlpha(0.5f) }
    var expanderColor: Color by property { it.colors.secondaryVariant }
    var expanderHoverColor: Color by property { it.colors.secondary }
    var expanderArrowColor: Color by property { it.colors.onSecondary }

    var onItemSelected: ((Int) -> Unit)? by property(null)
}

fun <T: ComboBoxModifier> T.font(font: Font): T { this.font = font; return this }
fun <T: ComboBoxModifier> T.textColor(color: Color): T { textColor = color; return this }
fun <T: ComboBoxModifier> T.items(items: List<Any>): T { this.items = items; return this }
fun <T: ComboBoxModifier> T.selectedIndex(index: Int): T { this.selectedIndex = index; return this }
fun <T: ComboBoxModifier> T.onItemSelected(block: ((Int) -> Unit)?): T { this.onItemSelected = block; return this }
fun <T: ComboBoxModifier> T.colors(
    textColor: Color = this.textColor,
    textBackgroundColor: Color = this.textBackgroundColor,
    textBackgroundHoverColor: Color = this.textBackgroundHoverColor,
    expanderColor: Color = this.expanderColor,
    expanderHoverColor: Color = this.expanderHoverColor,
    expanderArrowColor: Color = this.expanderArrowColor
): T {
    this.textColor = textColor
    this.textBackgroundColor = textBackgroundColor
    this.textBackgroundHoverColor = textBackgroundHoverColor
    this.expanderColor = expanderColor
    this.expanderHoverColor = expanderHoverColor
    this.expanderArrowColor = expanderArrowColor
    return this
}

inline fun UiScope.ComboBox(
    scopeName: String? = null,
    block: ComboBoxScope.() -> Unit
): ComboBoxScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val comboBox = uiNode.createChild(scopeName, ComboBoxNode::class, ComboBoxNode.factory)
    comboBox.modifier
        .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
        .hoverListener(comboBox)
        .onClick(comboBox)
        .onWheelY {
            if (it.pointer.deltaScrollY > 0 && comboBox.modifier.selectedIndex > 0) {
                comboBox.modifier.onItemSelected?.invoke(comboBox.modifier.selectedIndex - 1)
            } else if (it.pointer.deltaScrollY < 0 && comboBox.modifier.selectedIndex < comboBox.modifier.items.lastIndex) {
                comboBox.modifier.onItemSelected?.invoke(comboBox.modifier.selectedIndex + 1)
            }
        }
    comboBox.block()

    if (comboBox.isExpanded.use()) {
        Popup(comboBox.leftPx, comboBox.bottomPx) {
            modifier
                .zLayer(comboBox.modifier.zLayer + UiSurface.LAYER_POPUP)
                .border(RectBorder(colors.primaryVariantAlpha(0.5f), sizes.borderWidth))

            comboBox.modifier.items.forEachIndexed { i, item ->
                val hovered = comboBox.hoveredItem.use()
                Text(item.toString()) {
                    modifier
                        .width(Grow.Std)
                        .padding(horizontal = sizes.gap, vertical = sizes.smallGap * 0.5f)
                        .onEnter {
                            comboBox.hoveredItem.set(i)
                        }
                        .onExit {
                            comboBox.hoveredItem.set(-1)
                        }
                        .onClick {
                            comboBox.modifier.onItemSelected?.invoke(i)
                            comboBox.isExpanded.set(false)
                            comboBox.hoveredItem.set(-1)
                        }
                    if (i == hovered) {
                        modifier
                            .backgroundColor(comboBox.modifier.expanderHoverColor)
                            .textColor(comboBox.modifier.expanderArrowColor)
                    }
                }
            }
        }
    }

    return comboBox
}

open class ComboBoxNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ComboBoxScope, Clickable, Hoverable {
    override val modifier = ComboBoxModifier(surface)
    override val isHovered: Boolean get() = isHoveredState.value

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = CachedTextGeometry(this)

    private var isHoveredState = mutableStateOf(false)

    var isExpanded = mutableStateOf(false)
    var hoveredItem = mutableStateOf(-1)

    private val selectedText: String
        get() {
            return if (modifier.items.isEmpty()) {
                ""
            } else {
                modifier.items[modifier.selectedIndex.clamp(0, modifier.items.lastIndex)].toString()
            }
        }

    override fun measureContentSize(ctx: KoolContext) {
        surface.applyFontScale(modifier.font, ctx)
        val textMetrics = textCache.getTextMetrics(selectedText, modifier.font)
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) {
            modWidth.px
        } else {
            textMetrics.width + paddingStartPx + paddingEndPx + sizes.largeGap.px * 1.5f
        }
        val measuredHeight = if (modHeight is Dp) modHeight.px else textMetrics.height + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        val customClip = MutableVec4f()
        val expanderWidth = sizes.largeGap.px * 1.5f
        val expanderBorder = round(rightPx - expanderWidth)

        var textBgColor = modifier.textBackgroundColor
        var arrowBgColor = modifier.expanderColor
        if (isHoveredState.use()) {
            textBgColor = modifier.textBackgroundHoverColor
            arrowBgColor = modifier.expanderHoverColor
        }

        if (modifier.background == null) {
            // only set default button background if no custom one was configured
            modifier.background(UiRenderer {
                val cornerRadius = sizes.smallGap.px
                val draw = getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                draw.localRoundRect(0f, 0f, widthPx, heightPx, cornerRadius, textBgColor)

                customClip.set(clipBoundsPx)
                customClip.x = max(clipLeftPx, expanderBorder)
                draw.roundRect(
                    rightPx - expanderWidth - cornerRadius, topPx,
                    expanderWidth + cornerRadius, heightPx,
                    cornerRadius, customClip, arrowBgColor
                )
            })
        }

        super.render(ctx)

        textProps.apply {
            font = modifier.font
            text = selectedText
            isYAxisUp = false
            val textMetrics = textCache.textMetrics
            val oriX = paddingStartPx
            val oriY = (heightPx - textMetrics.height) / 2f + textMetrics.yBaseline
            origin.set(oriX, oriY, 0f)
        }
        customClip.set(clipBoundsPx)
        customClip.z = min(clipRightPx, expanderBorder)
        textCache.addTextGeometry(getTextBuilder(modifier.font).geometry, textProps, modifier.textColor, textClip = customClip)

        getPlainBuilder().configured(modifier.expanderArrowColor) {
            val cx = widthPx - expanderWidth * 0.5f
            val cy = heightPx * 0.5f
            val d = sizes.smallGap.px * 2.5f
            arrow(cx, cy, d, 90f)
        }
    }

    override fun onEnter(ev: PointerEvent) {
        isHoveredState.set(true)
    }

    override fun onExit(ev: PointerEvent) {
        isHoveredState.set(false)
    }

    override fun onClick(ev: PointerEvent) {
        isExpanded.set(!isExpanded.value)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ComboBoxNode = { parent, surface -> ComboBoxNode(parent, surface) }
    }
}