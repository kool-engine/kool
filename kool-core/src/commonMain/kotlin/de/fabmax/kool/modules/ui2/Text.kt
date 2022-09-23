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
    var textRotation: TextRotation by property(TextRotation.Rotation0)
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
        val font = surface.getFont(modifier.font, ctx)
        val textMetrics = textCache.getTextMetrics(modifier.text, font, ctx)
        val modWidth = modifier.width
        val modHeight = modifier.height
        val textWidth = if (modifier.textRotation.isHorizontal) textMetrics.width else textMetrics.height
        val textHeight = if (modifier.textRotation.isHorizontal) textMetrics.height else textMetrics.width
        val measuredWidth = if (modWidth is Dp) modWidth.px else textWidth + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else textHeight + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        textProps.apply {
            font = surface.getFont(modifier.font, ctx)
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
            val oriY = txtY + when (modifier.textAlignY) {
                AlignmentY.Top -> paddingTopPx
                AlignmentY.Center -> (heightPx - textHeight) / 2f
                AlignmentY.Bottom -> heightPx - textHeight - paddingBottomPx
            }
            origin.set(leftPx + oriX, topPx + oriY, 0f)
        }
        textCache.addTextGeometry(getTextBuilder(modifier.font, ctx).geometry, textProps, modifier.textColor, modifier.textRotation)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> TextNode = { parent, surface -> TextNode(parent, surface) }
    }
}
