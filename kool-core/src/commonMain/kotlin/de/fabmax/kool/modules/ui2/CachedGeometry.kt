package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor

open class CachedGeometry(val node: UiNode) {
    val cacheData = IndexedVertexList(Ui2Shader.UI_MESH_ATTRIBS)
    val cacheBuilder = MeshBuilder(cacheData).apply { isInvertFaceOrientation = true }
    val isEmpty: Boolean get() = cacheData.isEmpty()

    val cachedPosition = MutableVec2f()
    val cachedSize = MutableVec2f()
    val cachedClip = MutableVec4f()
    val cachedColor = MutableColor()

    private val posOffset = cacheData.attributeByteOffsets[Attribute.POSITIONS]!! / 4
    private val colorOffset = cacheData.attributeByteOffsets[Attribute.COLORS]!! / 4
    private val clipOffset = cacheData.attributeByteOffsets[Ui2Shader.ATTRIB_CLIP]!! / 4

    fun appendTo(target: IndexedVertexList) {
        val i0 = target.numVertices
        target.checkIndexSize(cacheData.numIndices)
        for (i in 0 until cacheData.numIndices) {
            target.indices.put(i0 + cacheData.indices[i])
        }

        target.checkBufferSizes(cacheData.numVertices)
        target.dataF.put(cacheData.dataF)
        target.numVertices += cacheData.numVertices
    }

    fun hasSizeChanged(): Boolean {
        return cachedSize.x != node.widthPx || cachedSize.y != node.heightPx
    }

    fun hasMoved(posX: Float, posY: Float, clip: Vec4f = node.clipBoundsPx): Boolean {
        return cachedClip != clip || cachedPosition.x != posX || cachedPosition.y != posY
    }

    inline fun rebuildCache(
        posX: Float = node.leftPx, posY: Float = node.topPx,
        color: Color? = null,
        block: MeshBuilder.() -> Unit
    ) {
        node.apply {
            cacheBuilder.clear()
            cacheBuilder.configured(block = block)
            cacheData.dataF.flip()
        }
        cachedPosition.set(posX, posY)
        cachedClip.set(node.clipBoundsPx)
        cachedSize.set(node.widthPx, node.heightPx)
        color?.let { cachedColor.set(it) }
    }

    open fun updateCache(posX: Float = node.leftPx, posY: Float = node.topPx, color: Color? = null, clip: Vec4f = node.clipBoundsPx) {
        if (hasMoved(posX, posY) || (color != null && color != cachedColor)) {
            updateVertices(posX, posY, color, clip)
        }
    }

    fun updateVertices(posX: Float, posY: Float, color: Color?, clip: Vec4f) {
        val posOffX = posX - cachedPosition.x
        val posOffY = posY - cachedPosition.y
        cachedPosition.set(posX, posY)
        cachedClip.set(clip)
        color?.let { cachedColor.set(it) }

        for (i in 0 until cacheData.numVertices) {
            val j = i * cacheData.vertexSizeF
            cacheData.dataF[j + posOffset] = cacheData.dataF[j + posOffset] + posOffX
            cacheData.dataF[j + posOffset + 1] = cacheData.dataF[j + posOffset + 1] + posOffY
            cacheData.dataF[j + clipOffset] = cachedClip.x
            cacheData.dataF[j + clipOffset + 1] = cachedClip.y
            cacheData.dataF[j + clipOffset + 2] = cachedClip.z
            cacheData.dataF[j + clipOffset + 3] = cachedClip.w
            if (color != null) {
                cacheData.dataF[j + colorOffset + 0] = cachedColor.r
                cacheData.dataF[j + colorOffset + 1] = cachedColor.g
                cacheData.dataF[j + colorOffset + 2] = cachedColor.b
                cacheData.dataF[j + colorOffset + 3] = cachedColor.a
            }
        }
    }
}