package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.TextMetrics

interface TextScope : UiScope {
    override val modifier: TextModifier
}

open class TextModifier(surface: UiSurface) : UiModifier(surface) {
    var text: String by property("")
    var font: Font by property { it.sizes.normalText }
    var textColor: Color by property { it.colors.onBackground }
    var textAlignX: AlignmentX by property(AlignmentX.Start)
    var textAlignY: AlignmentY by property(AlignmentY.Top)
    var textRotation: Float by property(0f)
    var baselineBottomMargin: Dp? by property(null)
    var baselineTopMargin: Dp? by property(null)
}

fun <T: TextModifier> T.text(text: String): T { this.text = text; return this }
fun <T: TextModifier> T.font(font: Font): T { this.font = font; return this }
fun <T: TextModifier> T.textColor(color: Color): T { textColor = color; return this }
fun <T: TextModifier> T.textAlignX(alignment: AlignmentX): T { textAlignX = alignment; return this }
fun <T: TextModifier> T.textAlignY(alignment: AlignmentY): T { textAlignY = alignment; return this }

/**
 * Sets the margin between text baseline and bottom / top border of the text box. If a baseline margin is set,
 * the other y text-alignment and padding properties are ignored. Currently, baseline margin only works for
 * non-rotated text.
 */
fun <T: TextModifier> T.baselineMargin(bottom: Dp? = null, top: Dp? = null): T {
    baselineBottomMargin = bottom
    baselineTopMargin = top
    return this
}

fun <T: TextModifier> T.textAlign(alignX: AlignmentX = textAlignX, alignY: AlignmentY = textAlignY): T {
    textAlignX = alignX
    textAlignY = alignY
    return this
}

fun <T: TextModifier> T.textRotation(rotation: Float): T { textRotation = rotation; return this }

inline fun UiScope.Text(text: String = "", block: TextScope.() -> Unit): TextScope {
    val textNd = uiNode.createChild(TextNode::class, TextNode.factory)
    textNd.modifier.text(text)
    textNd.block()
    return textNd
}

open class TextNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), TextScope {
    override val modifier = TextModifier(surface)

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = CachedTextGeometry(this)
    private val textBounds = MutableVec4f()
    private var isOddRotation = false

    override fun measureContentSize(ctx: KoolContext) {
        surface.applyFontScale(modifier.font, ctx)
        val textMetrics = textCache.getTextMetrics(modifier.text, modifier.font)
        transformMetrics(modifier.textRotation, textMetrics)
        val textWidth = textBounds.z
        val textHeight = textBounds.w
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else textWidth + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else textHeight + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        val textMetrics = textCache.textMetrics

        textProps.apply {
            font = modifier.font
            text = modifier.text
            isYAxisUp = false

            val txtX = textBounds.x
            val txtY = textBounds.y
            val textWidth = textBounds.z
            val textHeight = textBounds.w

            val oriX = txtX + when (modifier.textAlignX) {
                AlignmentX.Start -> paddingStartPx
                AlignmentX.Center -> (widthPx - textWidth) / 2f
                AlignmentX.End -> widthPx - textWidth - paddingEndPx
            }

            // a little hacky:
            // if a baseline margin is set, the original align properties are ignored
            // also, this currently won't correctly work for rotated text
            val baseBotMargin = modifier.baselineBottomMargin
            val baseTopMargin = modifier.baselineTopMargin
            val oriY = if (baseBotMargin != null) {
                heightPx - baseBotMargin.px
            } else if (baseTopMargin != null) {
                textMetrics.yBaseline + baseTopMargin.px
            } else {
                txtY + when (modifier.textAlignY) {
                    AlignmentY.Top -> paddingTopPx
                    AlignmentY.Center -> (heightPx - textHeight) / 2f
                    AlignmentY.Bottom -> heightPx - textHeight - paddingBottomPx
                }
            }

            origin.set(oriX, oriY, 0f)
        }

        val builder = getTextBuilder(modifier.font, ctx)
        if (!isOddRotation) {
            textCache.addTextGeometry(builder.geometry, textProps, modifier.textColor, modifier.textRotation)
        } else {
            builder.configured(modifier.textColor) {
                translate(widthPx * 0.5f, heightPx * 0.5f, 0f)
                rotate(modifier.textRotation, Vec3f.Z_AXIS)
                translate(-textMetrics.width * 0.5f, textMetrics.yBaseline - textMetrics.height * 0.5f, 0f)
                textProps.origin.set(Vec3f.ZERO)
                text(textProps)
            }
        }
    }

    private fun transformMetrics(rotation: Float, inMetrics: TextMetrics) {
        isOddRotation = false
        when (rotation) {
            0f -> {
                textBounds.x = inMetrics.paddingStart
                textBounds.y = inMetrics.yBaseline
                textBounds.z = inMetrics.width
                textBounds.w = inMetrics.height
            }
            90f -> {
                textBounds.x = inMetrics.height - inMetrics.yBaseline
                textBounds.y = 0f
                textBounds.z = inMetrics.height
                textBounds.w = inMetrics.width
            }
            180f -> {
                textBounds.x = inMetrics.width
                textBounds.y = inMetrics.height - inMetrics.yBaseline
                textBounds.z = inMetrics.width
                textBounds.w = inMetrics.height
            }
            270f -> {
                textBounds.x = inMetrics.yBaseline
                textBounds.y = inMetrics.width
                textBounds.z = inMetrics.height
                textBounds.w = inMetrics.width
            }
            else -> {
                isOddRotation = true
                val a = MutableVec2f(0f, inMetrics.height).rotate(rotation)
                val b = MutableVec2f(inMetrics.width, 0f).rotate(rotation)
                val c = MutableVec2f(inMetrics.width, inMetrics.height).rotate(rotation)

                val lt = minOf(0f, a.x, b.x, c.x)
                val rt = maxOf(0f, a.x, b.x, c.x)
                val tp = minOf(0f, a.y, b.y, c.y)
                val dn = maxOf(0f, a.y, b.y, c.y)

                textBounds.x = 0f
                textBounds.y = 0f
                textBounds.z = rt - lt
                textBounds.w = dn - tp
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> TextNode = { parent, surface -> TextNode(parent, surface) }
    }
}
