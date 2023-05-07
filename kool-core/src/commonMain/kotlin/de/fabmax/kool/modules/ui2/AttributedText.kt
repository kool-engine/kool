package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.CursorShape
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.Time
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class TextLine(val spans: List<Pair<String, TextAttributes>>) {
    val length: Int = spans.sumOf { it.first.length }
    val text: String
        get() = spans.joinToString("") { (str,_) -> str }


    fun charIndexToPx(charIndex: Int): Float {
        var x = 0f
        var i = charIndex
        for (s in spans.indices) {
            val (txt, attr) = spans[s]
            for (j in 0 until min(txt.length, i)) {
                if (i-- == 0) {
                    return x
                }
                x += attr.font.charWidth(txt[j])
            }
        }
        return x
    }

    fun charIndexFromPx(px: Float): Int {
        var x = 0f
        var i = 0
        for (s in spans.indices) {
            val (txt, attr) = spans[s]
            for (j in txt.indices) {
                val w = attr.font.charWidth(txt[j])
                if (x + w >= px) {
                    return if (abs(x - px) < abs(x + w - px)) i else i + 1
                }
                x += w
                i++
            }
        }
        return i
    }

    override fun toString(): String {
        return spans.joinToString { "\"${it.first}\"" }
    }

    companion object {
        val EMPTY = TextLine(emptyList())
    }
}

data class TextAttributes(
    val font: MsdfFont,
    val color: Color,
    val background: Color? = null
)

interface AttributedTextScope : UiScope {
    override val modifier: AttributedTextModifier

    fun charIndexFromLocalX(localX: Float): Int
    fun charIndexToLocalX(charIndex: Int): Float
}

open class AttributedTextModifier(surface: UiSurface) : UiModifier(surface) {
    var text: TextLine by property(TextLine.EMPTY)
    var textAlignX: AlignmentX by property(AlignmentX.Start)
    var textAlignY: AlignmentY by property(AlignmentY.Center)

    var caretPos: Int by property(0)
    var selectionStart: Int by property(0)
    var caretColor: Color by property { it.colors.onBackground }
    var isCaretVisible: Boolean by property(false)
    var selectionColor: Color by property { it.colors.primaryAlpha(0.5f) }
    var onSelectText: ((Int, Int) -> Unit)? by property(null)
}

fun <T: AttributedTextModifier> T.textAlignX(alignment: AlignmentX): T { textAlignX = alignment; return this }
fun <T: AttributedTextModifier> T.textAlignY(alignment: AlignmentY): T { textAlignY = alignment; return this }
fun <T: AttributedTextModifier> T.isCaretVisible(flag: Boolean): T { isCaretVisible = flag; return this }
fun <T: AttributedTextModifier> T.onSelectText(block: ((Int, Int) -> Unit)?): T { onSelectText = block; return this }
fun <T: AttributedTextModifier> T.cursorPos(cursor: Int): T {
    caretPos = cursor
    selectionStart = cursor
    return this
}
    fun <T: AttributedTextModifier> T.selectionRange(start: Int, cursor: Int): T {
    caretPos = cursor
    selectionStart = start
    return this
}

inline fun UiScope.AttributedText(
    text: TextLine,
    scopeName: String? = null,
    block: AttributedTextScope.() -> Unit = { }
): AttributedTextScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val textNd = uiNode.createChild(scopeName, AttributedTextNode::class, AttributedTextNode.factory)
    textNd.modifier.text = text
    textNd.modifier
        .onClick(textNd)
        .hoverListener(textNd)
        .dragListener(textNd)
    textNd.block()
    return textNd
}

open class AttributedTextNode(parent: UiNode?, surface: UiSurface)
    : UiNode(parent, surface), AttributedTextScope, Clickable, Hoverable, Draggable
{
    override val modifier = AttributedTextModifier(surface)

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = mutableListOf<CachedTextGeometry>()

    private var totalTextWidth = 0f
    private var totalTextHeight = 0f
    private var baselineY = 0f

    private val textOrigin = MutableVec2f()
    private var dragStartFrame = 0
    private var caretBlink = 0f
    private val isCaretBlink = mutableStateOf(false)
    private val caretWidth = Dp(1f)

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
        val measuredWidth = if (modWidth is Dp) modWidth.px else totalTextWidth + caretWidth.px + paddingStartPx + paddingEndPx
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

        // selection background
        if (modifier.caretPos != modifier.selectionStart) {
            val x1 = charIndexToLocalX(modifier.caretPos)
            val x2 = charIndexToLocalX(modifier.selectionStart)
            val from = min(x1, x2)
            val to = max(x1, x2)
            getUiPrimitives().localRect(from, paddingTopPx, to - from, innerHeightPx, modifier.selectionColor)
        }

        textOrigin.x = when (modifier.textAlignX) {
            AlignmentX.Start -> paddingStartPx
            AlignmentX.Center -> (widthPx - totalTextWidth) / 2f
            AlignmentX.End -> widthPx - totalTextWidth - caretWidth.px - paddingEndPx
        }
        textOrigin.y = baselineY + when (modifier.textAlignY) {
            AlignmentY.Top -> paddingTopPx
            AlignmentY.Center -> (heightPx - totalTextHeight) / 2f
            AlignmentY.Bottom -> heightPx - totalTextHeight - paddingBottomPx
        }
        var textX = textOrigin.x

        // text spans
        modifier.text.spans.forEachIndexed { i, (txt, attr) ->
            val cache = textCache[i]
            val metrics = cache.textMetrics

            if (i == 0) textX += metrics.paddingStart

            // text background
            attr.background?.let { bg ->
                getUiPrimitives(UiSurface.LAYER_BACKGROUND).localRect(
                    textX - metrics.paddingStart,
                    paddingTopPx,
                    metrics.width,
                    innerHeightPx,
                    bg
                )
            }

            // text geometry
            textProps.apply {
                font = attr.font
                text = txt
                isYAxisUp = false

                origin.set(textX, textOrigin.y, 0f)
                textX += metrics.baselineWidth
            }
            val builder = getTextBuilder(attr.font)
            cache.addTextGeometry(builder.geometry, textProps, attr.color)
        }

        // caret
        if (modifier.isCaretVisible && surface.isFocused.use()) {
            surface.onEachFrame(::updateCaretBlinkState)
            val caretX = charIndexToLocalX(modifier.caretPos)
            if (isCaretBlink.use()) {
                getUiPrimitives().localRect(caretX, paddingTopPx, caretWidth.px, innerHeightPx, modifier.caretColor)
            }
        }
    }

    override fun charIndexToLocalX(charIndex: Int): Float {
        return textOrigin.x + modifier.text.charIndexToPx(charIndex)
    }

    override fun charIndexFromLocalX(localX: Float): Int {
        return modifier.text.charIndexFromPx(localX - textOrigin.x)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun updateCaretBlinkState(ctx: KoolContext) {
        if (modifier.isCaretVisible) {
            caretBlink -= Time.deltaT
            if (caretBlink < 0f) {
                isCaretBlink.set(!isCaretBlink.value)
                caretBlink += 0.5f
                if (caretBlink < 0f) {
                    caretBlink = 0.5f
                }
            }
        } else {
            caretBlink = 0f
            isCaretBlink.set(false)
        }
    }


    fun resetCaretBlinkState() {
        caretBlink = 0.5f
        isCaretBlink.set(true)
    }

    override fun onHover(ev: PointerEvent) {
        PointerInput.cursorShape = CursorShape.TEXT
    }

    override fun onClick(ev: PointerEvent) {
        val txtI = charIndexFromLocalX(ev.position.x)
        modifier.onSelectText?.invoke(txtI, txtI)
        dragStartFrame = Time.frameCount
    }

    override fun onDragStart(ev: PointerEvent) = onClick(ev)

    override fun onDrag(ev: PointerEvent) {
        PointerInput.cursorShape = CursorShape.TEXT
        if (Time.frameCount > dragStartFrame) {
            val txtI = charIndexFromLocalX(ev.position.x)
            if (txtI != modifier.caretPos) {
                modifier.onSelectText?.invoke(txtI, modifier.selectionStart)
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> AttributedTextNode = { parent, surface -> AttributedTextNode(parent, surface) }
    }
}