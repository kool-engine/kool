package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.TextMetrics
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MutableColor

interface TextScope : UiScope {
    override val modifier: TextModifier
}

open class TextModifier : UiModifier() {
    var text: String by property("")
    var font: Font by property(Font.DEFAULT_FONT)
    var foreground: Color by property(MdColor.GREY tone 200)
    var textAlignX: AlignmentX by property(AlignmentX.Start)
    var textAlignY: AlignmentY by property(AlignmentY.Top)
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
    private val textCache = CachedTextGeometry()

    override fun measureContentSize(ctx: KoolContext) {
        modifier.font.textDimensions(modifier.text, ctx, textMetrics)

        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) {
            modWidth.px
        } else {
            textMetrics.width + paddingStart + paddingEnd
        }
        val measuredHeight = if (modHeight is Dp) {
            modHeight.px
        } else {
            textMetrics.height + paddingTop + paddingBottom
        }

        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        textProps.apply {
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
            origin.set(minX + oriX, minY + oriY, 0f)
        }
        textCache.addTextGeometry(surface.getTextBuilder(modifier.font, ctx).geometry, textProps)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> TextNode = { parent, surface -> TextNode(parent, surface) }
    }

    private inner class CachedTextGeometry {
        private val cacheData = IndexedVertexList(Ui2Shader.UI_MESH_ATTRIBS)
        private val cacheBuilder = MeshBuilder(cacheData).apply { invertFaceOrientation = true }
        private var cachedText: String? = null
        private var cachedFont: Font? = null
        private val cachedTextPos = MutableVec3f()
        private val cachedClip = MutableVec4f()
        private val cachedColor = MutableColor()

        private val posOffset = cacheData.attributeByteOffsets[Attribute.POSITIONS]!! / 4
        private val colorOffset = cacheData.attributeByteOffsets[Attribute.COLORS]!! / 4
        private val clipOffset = cacheData.attributeByteOffsets[Ui2Shader.ATTRIB_CLIP]!! / 4

        fun addTextGeometry(target: IndexedVertexList, textProps: TextProps) {
            if (cachedText != textProps.text || cachedFont !== textProps.font) {
                buildCache(textProps)
            }
            if (cachedTextPos.x != minX || cachedTextPos.y != minY ||
                cachedClip.x != clippedMinX || cachedClip.y != clippedMinY ||
                cachedClip.z != clippedMaxX || cachedClip.w != clippedMaxX ||
                cachedColor != modifier.foreground) {
                updateCachedPositions(textProps)
            }

            val i0 = target.numVertices
            target.checkIndexSize(cacheData.numIndices)
            for (i in 0 until cacheData.numIndices) {
                target.indices.put(i0 + cacheData.indices[i])
            }

            target.checkBufferSizes(cacheData.numVertices)
            target.dataF.put(cacheData.dataF)
            target.numVertices += cacheData.numVertices
        }

        private fun buildCache(textProps: TextProps) {
            cacheBuilder.configured(modifier.foreground) {
                clear()
                text(textProps)
                cachedText = textProps.text
                cachedFont = textProps.font
                cachedTextPos.set(textProps.origin)
                cachedClip.set(Vec4f.ZERO)
            }
            cacheData.dataF.flip()
        }

        private fun updateCachedPositions(textProps: TextProps) {
            val posOffX = textProps.origin.x - cachedTextPos.x
            val posOffY = textProps.origin.y - cachedTextPos.y
            cachedTextPos.set(textProps.origin)
            cachedClip.set(clippedMinX, clippedMinY, clippedMaxX, clippedMaxY)
            cachedColor.set(modifier.foreground)
            for (i in 0 until cacheData.numVertices) {
                val j = i * cacheData.vertexSizeF
                cacheData.dataF[j + posOffset] += posOffX
                cacheData.dataF[j + posOffset + 1] += posOffY
                cacheData.dataF[j + clipOffset] = clippedMinX
                cacheData.dataF[j + clipOffset + 1] = clippedMinY
                cacheData.dataF[j + clipOffset + 2] = clippedMaxX
                cacheData.dataF[j + clipOffset + 3] = clippedMaxY
                cacheData.dataF[j + colorOffset + 0] = cachedColor.r
                cacheData.dataF[j + colorOffset + 1] = cachedColor.g
                cacheData.dataF[j + colorOffset + 2] = cachedColor.b
                cacheData.dataF[j + colorOffset + 3] = cachedColor.a
            }
        }
    }
}
