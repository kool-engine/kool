package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.TextMetrics
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

interface TextScope : UiScope {
    override val modifier: TextModifier
}

open class TextModifier : UiModifier() {
    var text = ""
    var font = Font.DEFAULT_FONT
    var foreground: Color = MdColor.GREY tone 200
    var textAlignX = AlignmentX.Start
    var textAlignY = AlignmentY.Top

    override fun resetDefaults() {
        super.resetDefaults()
        text = ""
        font = Font.DEFAULT_FONT
        foreground = MdColor.GREY tone 200
        textAlignX = AlignmentX.Start
        textAlignY = AlignmentY.Top
    }
}

fun <T: TextModifier> T.text(text: String): T { this.text = text; return this }
fun <T: TextModifier> T.font(font: Font): T { this.font = font; return this }
fun <T: TextModifier> T.foreground(color: Color): T { foreground = color; return this }
fun <T: TextModifier> T.textAlignX(alignment: AlignmentX): T { textAlignX = alignment; return this }
fun <T: TextModifier> T.textAlignY(alignment: AlignmentY): T { textAlignY = alignment; return this }

inline fun UiScope.Text(text: String = "", block: TextScope.() -> Unit): TextScope {
    val textNd = uiNode.createChild(TextNode::class, TextNode.factory)
    textNd.modifier.text(text)
    textNd.block()
    return textNd
}

class TextNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), TextScope {
    override val modifier = TextModifier()

    private val textMetrics = TextMetrics()
    private val textProps = TextProps(modifier.font)

    override fun measureContentSize(ctx: KoolContext) {
        modifier.font.textDimensions(modifier.text, ctx, textMetrics)

        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) {
            modWidth.value * surface.measuredScale
        } else {
            textMetrics.width + paddingStart + paddingEnd
        }
        val measuredHeight = if (modHeight is Dp) {
            modHeight.value * surface.measuredScale
        } else {
            textMetrics.height + paddingTop + paddingBottom
        }

        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        surface.getTextBuilder(modifier.font, ctx).configured(modifier.foreground) {
            text(textProps.apply {
                font = modifier.font
                isYAxisUp = false
                text = modifier.text
                val oriX = when (modifier.textAlignX) {
                    AlignmentX.Start -> paddingStart
                    AlignmentX.Center -> (width - textMetrics.width) / 2f
                    AlignmentX.End -> width - textMetrics.width - paddingEnd
                }
                val oriY = when (modifier.textAlignY) {
                    AlignmentY.Top -> textMetrics.yBaseline + paddingTop
                    AlignmentY.Center -> (height - textMetrics.height) / 2f + textMetrics.yBaseline
                    AlignmentY.Bottom -> height - textMetrics.height + textMetrics.yBaseline - paddingBottom
                }
                origin.set(oriX, oriY, 0f)
            })
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> TextNode = { parent, surface -> TextNode(parent, surface) }
    }
}
