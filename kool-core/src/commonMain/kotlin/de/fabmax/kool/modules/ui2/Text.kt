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
}

fun <T: TextModifier> T.text(text: String): T { this.text = text; return this }
fun <T: TextModifier> T.font(font: Font): T { this.font = font; return this }
fun <T: TextModifier> T.foreground(color: Color): T { foreground = color; return this }
fun <T: TextModifier> T.textAlignX(alignment: AlignmentX): T { textAlignX = alignment; return this }
fun <T: TextModifier> T.textAlignY(alignment: AlignmentY): T { textAlignY = alignment; return this }

inline fun UiScope.Text(text: String = "", block: TextScope.() -> Unit) {
    val childText = TextNode(uiNode, uiCtx)
    uiNode.children += childText
    childText.modifier.text(text)
    childText.block()
}

class TextNode(parent: UiNode?, uiCtx: UiContext) : UiNode(parent, uiCtx), TextScope {
    override val modifier = TextModifier()

    private val textMetrics = TextMetrics()
    private val textProps = TextProps(modifier.font)

    override fun measureContentSize(ctx: KoolContext) {
        modifier.font.textDimensions(modifier.text, ctx, textMetrics)
        setContentSize(
            textMetrics.width + paddingStart + paddingEnd,
            textMetrics.height + paddingTop + paddingBottom
        )
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        uiCtx.getTextBuilder(modifier.font, ctx).configured(modifier.foreground) {
            text(textProps.apply {
                font = modifier.font
                isYAxisUp = false
                text = modifier.text
                val oriX = when (modifier.textAlignX) {
                    AlignmentX.Start -> paddingStart
                    AlignmentX.Center -> (width - contentWidth) / 2f
                    AlignmentX.End -> width - contentWidth - paddingEnd
                }
                val oriY = when (modifier.textAlignY) {
                    AlignmentY.Top -> textMetrics.yBaseline + paddingTop
                    AlignmentY.Center -> (height - contentHeight) / 2f + textMetrics.yBaseline
                    AlignmentY.Bottom -> height - contentHeight + textMetrics.yBaseline - paddingBottom
                }
                origin.set(oriX, oriY, 0f)
            })
        }
    }
}
