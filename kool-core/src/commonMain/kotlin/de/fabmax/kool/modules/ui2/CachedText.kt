package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.TextMetrics
import kotlin.math.round

class CachedText(val node: UiNode) {
    private val cacheData = IndexedVertexList(Ui2Shader.UI_MESH_ATTRIBS)
    private val cacheBuilder = MeshBuilder(cacheData).apply { isInvertFaceOrientation = true }

    private val posOffset = cacheData.attributeByteOffsets[Attribute.POSITIONS]!! / 4
    private val colorOffset = cacheData.attributeByteOffsets[Attribute.COLORS]!! / 4
    private val clipOffset = cacheData.attributeByteOffsets[Ui2Shader.ATTRIB_CLIP]!! / 4

    val textMetrics = TextMetrics()

    private var cachedFont: Font? = null
    private var cachedText: String? = null
    private var cachedScale = 0f
    private var cachedRotation = TextRotation.Rotation0
    private val cachedTextPos = MutableVec3f()
    private val cachedClip = MutableVec4f()
    private val cachedColor = MutableColor()

    private var metricsValid = false
    private var geometryValid = false

    fun getTextMetrics(text: String, font: Font, ctx: KoolContext): TextMetrics {
        checkCache(text, font)
        if (!metricsValid) {
            metricsValid = true
            font.textDimensions(text, ctx, textMetrics)
        }
        return textMetrics
    }

    fun addTextGeometry(target: IndexedVertexList, textProps: TextProps, textColor: Color, rotation: TextRotation = TextRotation.Rotation0, clip: Vec4f = node.clipBoundsPx) {
        if (rotation != cachedRotation) {
            geometryValid = false
        }
        if (!geometryValid) {
            geometryValid = true
            buildGeometry(textProps, textColor, rotation)
        }
        if (cachedTextPos.x != node.leftPx || cachedTextPos.y != node.topPx ||
            cachedClip != clip || cachedColor != textColor) {
            updateVertexAttribs(textProps, textColor)
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

    private fun checkCache(text: String, font: Font) {
        val fontScale = font.charMap?.scale
        if (text != cachedText || font !== cachedFont || cachedScale != fontScale) {
            cachedFont = font
            cachedText = text
            metricsValid = false
            geometryValid = false
        }
    }

    private fun buildGeometry(textProps: TextProps, textColor: Color, textRotation: TextRotation) {
        cachedScale = textProps.font.charMap!!.scale
        cacheBuilder.apply {
            clear()
            vertexModFun = node.setBoundsVertexMod
            color = textColor

            if (textRotation != TextRotation.Rotation0) {
                translate(textProps.origin)
                when (textRotation) {
                    TextRotation.Rotation90 -> rotate(90f, Vec3f.Z_AXIS)
                    TextRotation.Rotation180 -> rotate(180f, Vec3f.Z_AXIS)
                    TextRotation.Rotation270 -> rotate(270f, Vec3f.Z_AXIS)
                    TextRotation.Rotation0 -> { }
                }
                translate(-textProps.origin.x, -textProps.origin.y, -textProps.origin.z)
            }

            text(textProps)
            cachedTextPos.set(textProps.origin)
            cachedClip.set(Vec4f.ZERO)
        }
        cacheData.dataF.flip()
    }

    private fun updateVertexAttribs(textProps: TextProps, textColor: Color) {
        val posOffX = textProps.origin.x - cachedTextPos.x
        val posOffY = textProps.origin.y - cachedTextPos.y
        cachedTextPos.set(textProps.origin)
        cachedClip.set(node.clipBoundsPx)
        cachedColor.set(textColor)
        for (i in 0 until cacheData.numVertices) {
            val j = i * cacheData.vertexSizeF
            cacheData.dataF[j + posOffset] = round(cacheData.dataF[j + posOffset] + posOffX)
            cacheData.dataF[j + posOffset + 1] = round(cacheData.dataF[j + posOffset + 1] + posOffY)
            cacheData.dataF[j + clipOffset] = node.clipLeftPx
            cacheData.dataF[j + clipOffset + 1] = node.clipTopPx
            cacheData.dataF[j + clipOffset + 2] = node.clipRightPx
            cacheData.dataF[j + clipOffset + 3] = node.clipBottomPx
            cacheData.dataF[j + colorOffset + 0] = cachedColor.r
            cacheData.dataF[j + colorOffset + 1] = cachedColor.g
            cacheData.dataF[j + colorOffset + 2] = cachedColor.b
            cacheData.dataF[j + colorOffset + 3] = cachedColor.a
        }
    }
}