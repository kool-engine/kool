package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color

open class CachedGeometry(
    val node: UiNode,
    val cacheData: IndexedVertexList = IndexedVertexList(Ui2Shader.UI_MESH_ATTRIBS)
) {
    val cacheBuilder = MeshBuilder(cacheData).apply { isInvertFaceOrientation = true }
    val isEmpty: Boolean get() = cacheData.isEmpty()

    val cachedPosition = MutableVec2f()
    val cachedSize = MutableVec2f()
    val cachedClip = MutableVec4f()

    private val posOffset = cacheData.attributeByteOffsets[Attribute.POSITIONS]!! / 4
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
            cacheBuilder.configured(color, block = block)
        }
        cachedPosition.set(posX, posY)
        cachedClip.set(node.clipBoundsPx)
        cachedSize.set(node.widthPx, node.heightPx)
    }

    open fun updateCache(posX: Float = node.leftPx, posY: Float = node.topPx, clip: Vec4f = node.clipBoundsPx) {
        if (hasMoved(posX, posY) || clip != cachedClip) {
            updateVertices(posX, posY, clip)
        }
    }

    fun updateVertices(posX: Float, posY: Float, clip: Vec4f) {
        val posOffX = posX - cachedPosition.x
        val posOffY = posY - cachedPosition.y
        cachedPosition.set(posX, posY)
        cachedClip.set(clip)

        for (i in 0 until cacheData.numVertices) {
            val j = i * cacheData.vertexSizeF
            cacheData.dataF[j + posOffset] = cacheData.dataF[j + posOffset] + posOffX
            cacheData.dataF[j + posOffset + 1] = cacheData.dataF[j + posOffset + 1] + posOffY
            cacheData.dataF[j + clipOffset] = cachedClip.x
            cacheData.dataF[j + clipOffset + 1] = cachedClip.y
            cacheData.dataF[j + clipOffset + 2] = cachedClip.z
            cacheData.dataF[j + clipOffset + 3] = cachedClip.w
        }
    }
}