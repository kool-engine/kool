package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MsdfFont
import kotlin.math.max

data class TextLine(val spans: List<Pair<String, TextAttributes>>) {
    companion object {
        val EMPTY = TextLine(emptyList())
    }
}

data class TextAttributes(
    val font: MsdfFont,
    val color: Color,
    val background: Color? = null
) {
    companion object {
        val DEFAULT_ATTRIBS = TextAttributes(MsdfFont.DEFAULT_FONT, Color.GRAY)
    }
}

interface AttributedTextScope : UiScope {
    override val modifier: AttributedTextModifier
}

open class AttributedTextModifier(surface: UiSurface) : UiModifier(surface) {
    var text: TextLine by property(TextLine.EMPTY)
    var textAlignX: AlignmentX by property(AlignmentX.Start)
    var textAlignY: AlignmentY by property(AlignmentY.Top)
}

fun <T: AttributedTextModifier> T.textAlignX(alignment: AlignmentX): T { textAlignX = alignment; return this }
fun <T: AttributedTextModifier> T.textAlignY(alignment: AlignmentY): T { textAlignY = alignment; return this }

inline fun UiScope.AttributedText(text: TextLine, block: AttributedTextScope.() -> Unit = { }): AttributedTextScope {
    val textNd = uiNode.createChild(AttributedTextNode::class, AttributedTextNode.factory)
    textNd.modifier.text = text
    textNd.modifier
        .onClick(textNd)
        .hoverListener(textNd)
        .dragListener(textNd)
    textNd.block()
    return textNd
}

open class AttributedTextNode(parent: UiNode?, surface: UiSurface)
    : UiNode(parent, surface), AttributedTextScope, Clickable, Hoverable, Draggable, Focusable
{
    override val modifier = AttributedTextModifier(surface)
    override val isFocused: Boolean get() = isFocusedState.value

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = mutableListOf<CachedTextGeometry>()

    private var totalTextWidth = 0f
    private var totalTextHeight = 0f
    private var baselineY = 0f

    private var prevClickTime = 0.0
    private var isFocusedState = mutableStateOf(false)
    private var cursorBlink = 0f
    private var cursorShow = mutableStateOf(false)

    override fun measureContentSize(ctx: KoolContext) {
        updateCacheSize()

        totalTextWidth = 0f
        totalTextHeight = 0f
        baselineY = 0f
        modifier.text.spans.forEachIndexed { i, (txt, attr) ->
            surface.applyFontScale(attr.font, ctx)
            val textMetrics = textCache[i].getTextMetrics(txt, attr.font)
            totalTextWidth += textMetrics.baselineWidth
            totalTextHeight = max(totalTextHeight, textMetrics.height)
            baselineY = max(baselineY, textMetrics.yBaseline)
            if (i == 0) {
                totalTextWidth += textMetrics.paddingStart
            } else if (i == modifier.text.spans.lastIndex) {
                totalTextWidth += textMetrics.paddingEnd
            }
        }

        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else totalTextWidth + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else totalTextHeight + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    private fun updateCacheSize() {
        while (textCache.size > modifier.text.spans.size) {
            textCache.removeAt(textCache.lastIndex)
        }
        while (textCache.size < modifier.text.spans.size) {
            textCache += CachedTextGeometry(this)
        }
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        var textX = when (modifier.textAlignX) {
            AlignmentX.Start -> paddingStartPx
            AlignmentX.Center -> (widthPx - totalTextWidth) / 2f
            AlignmentX.End -> widthPx - totalTextWidth - paddingEndPx
        }

        modifier.text.spans.forEachIndexed { i, (txt, attr) ->
            val cache = textCache[i]
            val metrics = cache.textMetrics

            if (i == 0) {
                textX += metrics.paddingStart
            }

            attr.background?.let { bg ->
                getUiPrimitives(UiSurface.LAYER_BACKGROUND).localRect(textX - metrics.paddingStart, 0f, metrics.width, heightPx, bg)
            }

            textProps.apply {
                font = attr.font
                text = txt
                isYAxisUp = false

                val oriY = baselineY + when (modifier.textAlignY) {
                    AlignmentY.Top -> paddingTopPx
                    AlignmentY.Center -> (heightPx - totalTextHeight) / 2f
                    AlignmentY.Bottom -> heightPx - totalTextHeight - paddingBottomPx
                }
                origin.set(textX, oriY, 0f)
                textX += metrics.baselineWidth
            }

            val builder = getTextBuilder(attr.font, ctx)
            cache.addTextGeometry(builder.geometry, textProps, attr.color)
        }
    }

    override fun onFocusGain() {
        isFocusedState.set(true)
    }

    override fun onFocusLost() {
        isFocusedState.set(false)
    }

    override fun onEnter(ev: PointerEvent) {
        ev.ctx.inputMgr.cursorShape = InputManager.CursorShape.TEXT
    }

    override fun onExit(ev: PointerEvent) {
        ev.ctx.inputMgr.cursorShape = InputManager.CursorShape.DEFAULT
    }

    override fun onDragStart(ev: PointerEvent) = onClick(ev)

    override fun onDrag(ev: PointerEvent) {
//        val selPos = textIndex(modifier.font, ev.position.x)
//        if (selPos != editText.caretPosition) {
//            editText.caretPosition = selPos
//            surface.triggerUpdate()
//        }
    }

    override fun onKeyEvent(keyEvent: InputManager.KeyEvent) {

    }

    companion object {
        val factory: (UiNode, UiSurface) -> AttributedTextNode = { parent, surface -> AttributedTextNode(parent, surface) }
    }
}