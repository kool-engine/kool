package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.math.deg
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.*

class CachedTextGeometry(node: UiNode) : CachedGeometry(node, IndexedVertexList(MsdfUiShader.MSDF_UI_MESH_ATTRIBS)) {
    val textMetrics = TextMetrics()

    private var cachedFont: Font? = null
    private var cachedText: String? = null
    private var cachedScale = 0f
    private var cachedRotation = 0f
    private val cachedColor = MutableColor()

    private var metricsValid = false
    private var geometryValid = false

    fun getTextMetrics(text: String, font: Font): TextMetrics {
        checkCache(text, font)
        if (!metricsValid) {
            metricsValid = true
            font.textDimensions(text, textMetrics)
        }
        return textMetrics
    }

    private fun checkCache(text: String, font: Font) {
        val fontScale = font.scale
        if (text != cachedText || font !== cachedFont || cachedScale != font.scale) {
            cachedFont = font
            cachedScale = fontScale
            cachedText = text
            metricsValid = false
            geometryValid = false
        }
    }

    fun addTextGeometry(target: IndexedVertexList, textProps: TextProps, textColor: Color, textRotation: Float = 0f, textClip: Vec4f = node.clipBoundsPx) {
        if (hasContentChanged(textRotation, textColor)) {
            rebuildTextGeometry(textProps, textColor, textRotation)
        }
        updateTextCache(textProps, textClip)
        appendTo(target)
    }

    private fun hasContentChanged(rotation: Float, color: Color): Boolean {
        return hasSizeChanged()
                || !geometryValid
                || rotation != cachedRotation
                || color != cachedColor
    }

    private fun rebuildTextGeometry(textProps: TextProps, textColor: Color, textRotation: Float) = rebuildCache(
        node.leftPx + textProps.origin.x, node.topPx + textProps.origin.y, textColor
    ) {
        geometryValid = true
        cachedRotation = textRotation
        cachedColor.set(color)

        if (textRotation != 0f) {
            translate(textProps.origin)
            when (textRotation) {
                90f -> rotate(90f.deg, Vec3f.Z_AXIS)
                180f -> rotate(180f.deg, Vec3f.Z_AXIS)
                270f -> rotate(270f.deg, Vec3f.Z_AXIS)
                else -> logW { "CachedText supports only 90 degrees rotation steps" }
            }
            translate(-textProps.origin.x, -textProps.origin.y, -textProps.origin.z)
        }
        text(textProps)
    }

    private fun updateTextCache(textProps: TextProps, textClip: Vec4f) {
        updateCache(node.leftPx + textProps.origin.x, node.topPx + textProps.origin.y, textClip)
    }
}