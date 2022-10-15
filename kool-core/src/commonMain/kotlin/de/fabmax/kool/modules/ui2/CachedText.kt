package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.TextMetrics

class CachedText(node: UiNode) : CachedGeometry(node, IndexedVertexList(MsdfUiShader.MSDF_UI_MESH_ATTRIBS)) {
    val textMetrics = TextMetrics()

    private var cachedFont: Font? = null
    private var cachedText: String? = null
    private var cachedScale = 0f
    private var cachedRotation = TextRotation.Rotation0
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

    fun addTextGeometry(target: IndexedVertexList, textProps: TextProps, textColor: Color, textRotation: TextRotation = TextRotation.Rotation0, textClip: Vec4f = node.clipBoundsPx) {
        if (hasContentChanged(textRotation, textColor)) {
            rebuildTextGeometry(textProps, textColor, textRotation)
        }
        updateTextCache(textProps, textClip)
        appendTo(target)
    }

    private fun hasContentChanged(rotation: TextRotation, color: Color): Boolean {
        return hasSizeChanged()
                || !geometryValid
                || rotation != cachedRotation
                || color != cachedColor
    }

    private fun rebuildTextGeometry(textProps: TextProps, textColor: Color, textRotation: TextRotation) = rebuildCache(
        node.leftPx + textProps.origin.x, node.topPx + textProps.origin.y, textColor
    ) {
        geometryValid = true
        cachedRotation = textRotation
        cachedColor.set(color)

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
    }

    private fun updateTextCache(textProps: TextProps, textClip: Vec4f) {
        updateCache(node.leftPx + textProps.origin.x, node.topPx + textProps.origin.y, textClip)
    }
}