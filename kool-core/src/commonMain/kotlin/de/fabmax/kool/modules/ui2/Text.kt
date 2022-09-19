package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps
import de.fabmax.kool.util.Color

interface TextScope : UiScope {
    override val modifier: TextModifier
}

open class TextModifier(surface: UiSurface) : UiModifier(surface) {
    var text: String by property("")
    var font: FontProps by property { it.sizes.normalText }
    var textColor: Color by property { it.colors.onSurface }
    var textAlignX: AlignmentX by property(AlignmentX.Start)
    var textAlignY: AlignmentY by property(AlignmentY.Top)
}

fun <T: TextModifier> T.text(text: String): T { this.text = text; return this }
fun <T: TextModifier> T.font(font: FontProps): T { this.font = font; return this }
fun <T: TextModifier> T.textColor(color: Color): T { textColor = color; return this }
fun <T: TextModifier> T.textAlignX(alignment: AlignmentX): T { textAlignX = alignment; return this }
fun <T: TextModifier> T.textAlignY(alignment: AlignmentY): T { textAlignY = alignment; return this }

fun <T: TextModifier> T.textAlign(alignX: AlignmentX, alignY: AlignmentY): T {
    textAlignX = alignX
    textAlignY = alignY
    return this
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
        val font = surface.getFont(modifier.font, ctx)
        val textMetrics = textCache.getTextMetrics(modifier.text, font, ctx)
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else textMetrics.width + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else textMetrics.height + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        textProps.apply {
            font = surface.getFont(modifier.font, ctx)
            text = modifier.text
            isYAxisUp = false
            val textMetrics = textCache.textMetrics
            val oriX = when (modifier.textAlignX) {
                AlignmentX.Start -> paddingStartPx
                AlignmentX.Center -> (widthPx - textMetrics.width) / 2f
                AlignmentX.End -> widthPx - textMetrics.width - paddingEndPx
            }
            val oriY = when (modifier.textAlignY) {
                AlignmentY.Top -> textMetrics.yBaseline + paddingTopPx
                AlignmentY.Center -> (heightPx - textMetrics.height) / 2f + textMetrics.yBaseline
                AlignmentY.Bottom -> heightPx - textMetrics.height + textMetrics.yBaseline - paddingBottomPx
            }
            origin.set(leftPx + oriX, topPx + oriY, 0f)
        }
        textCache.addTextGeometry(surface.getTextBuilder(modifier.font, ctx).geometry, textProps, modifier.textColor)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> TextNode = { parent, surface -> TextNode(parent, surface) }
    }
}
