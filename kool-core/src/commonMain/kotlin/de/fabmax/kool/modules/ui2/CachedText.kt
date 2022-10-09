package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.FontMap
import de.fabmax.kool.util.TextMetrics

class CachedText(node: UiNode) : CachedGeometry(node) {
    val textMetrics = TextMetrics()

    private var cachedFont: FontMap? = null
    private var cachedText: String? = null
    private var cachedScale = 0f
    private var cachedRotation = TextRotation.Rotation0

    private var metricsValid = false
    private var geometryValid = false

    fun getTextMetrics(text: String, fontMap: FontMap): TextMetrics {
        checkCache(text, fontMap)
        if (!metricsValid) {
            metricsValid = true
            fontMap.textDimensions(text, textMetrics)
        }
        return textMetrics
    }

    private fun checkCache(text: String, fontMap: FontMap) {
        if (text != cachedText || fontMap !== cachedFont || cachedScale != fontMap.scale) {
            cachedFont = fontMap
            cachedScale = fontMap.scale
            cachedText = text
            metricsValid = false
            geometryValid = false
        }
    }

    fun addTextGeometry(target: IndexedVertexList, textProps: TextProps, textColor: Color, textRotation: TextRotation = TextRotation.Rotation0, textClip: Vec4f = node.clipBoundsPx) {
        if (hasContentChanged(textRotation)) {
            rebuildTextGeometry(textProps, textColor, textRotation)
        }
        updateTextCache(textProps, textColor, textClip)
        appendTo(target)
    }

    fun hasContentChanged(textRotation: TextRotation): Boolean {
        return hasSizeChanged() || !geometryValid || textRotation != cachedRotation
    }

    fun rebuildTextGeometry(textProps: TextProps, textColor: Color, textRotation: TextRotation) = rebuildCache(
        node.leftPx + textProps.origin.x, node.topPx + textProps.origin.y
    ) {
        geometryValid = true
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
    }

    fun updateTextCache(textProps: TextProps, textColor: Color, textClip: Vec4f) {
        updateCache(node.leftPx + textProps.origin.x, node.topPx + textProps.origin.y, textColor, textClip)
    }
}