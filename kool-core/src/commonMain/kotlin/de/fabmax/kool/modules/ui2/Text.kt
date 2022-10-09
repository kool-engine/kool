package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font

interface TextScope : UiScope {
    override val modifier: TextModifier
}

open class TextModifier(surface: UiSurface) : UiModifier(surface) {
    var text: String by property("")
    var font: Font by property { it.sizes.normalText }
    var textColor: Color by property { it.colors.onBackground }
    var textAlignX: AlignmentX by property(AlignmentX.Start)
    var textAlignY: AlignmentY by property(AlignmentY.Top)
    var textRotation: TextRotation by property(TextRotation.Rotation0)
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

fun <T: TextModifier> T.textRotation(rotation: TextRotation): T { textRotation = rotation; return this }

enum class TextRotation(val isHorizontal: Boolean) {
    Rotation0(true),
    Rotation90(false),
    Rotation180(true),
    Rotation270(false)
}

inline fun UiScope.Text(text: String = "", block: TextScope.() -> Unit): TextScope {
    val textNd = uiNode.createChild(TextNode::class, TextNode.factory)
    textNd.modifier.text(text)
    textNd.block()
    return textNd
}

open class TextNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), TextScope {
    override val modifier = TextModifier(surface)

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = CachedText(this)

    override fun measureContentSize(ctx: KoolContext) {
        val font = surface.loadFont(modifier.font, ctx)
        val textMetrics = textCache.getTextMetrics(modifier.text, font)
        val textWidth = if (modifier.textRotation.isHorizontal) textMetrics.width else textMetrics.height
        val textHeight = if (modifier.textRotation.isHorizontal) textMetrics.height else textMetrics.width
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else textWidth + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else textHeight + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        textProps.apply {
            font = modifier.font
            text = modifier.text
            isYAxisUp = false

            val textMetrics = textCache.textMetrics
            val textWidth = if (modifier.textRotation.isHorizontal) textMetrics.width else textMetrics.height
            val textHeight = if (modifier.textRotation.isHorizontal) textMetrics.height else textMetrics.width

            val txtX: Float
            val txtY: Float
            when (modifier.textRotation) {
                TextRotation.Rotation0 -> {
                    txtX = 0f
                    txtY = textMetrics.yBaseline
                }
                TextRotation.Rotation90 -> {
                    txtX = textWidth - textMetrics.yBaseline
                    txtY = 0f
                }
                TextRotation.Rotation180 -> {
                    txtX = textWidth
                    txtY = textHeight - textMetrics.yBaseline
                }
                TextRotation.Rotation270 -> {
                    txtX = textMetrics.yBaseline
                    txtY = textHeight
                }
            }

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
        textCache.addTextGeometry(getTextBuilder(modifier.font, ctx).geometry, textProps, modifier.textColor, modifier.textRotation)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> TextNode = { parent, surface -> TextNode(parent, surface) }
    }
}
