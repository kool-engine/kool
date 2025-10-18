package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.getFloat3
import de.fabmax.kool.util.getFloat4

open class CachedGeometry<Layout: Struct>(
    val node: UiNode,
    private val cacheData: IndexedVertexList<Layout>
) {
    val cacheBuilder = MeshBuilder(cacheData).apply { isInvertFaceOrientation = true }
    val isEmpty: Boolean get() = cacheData.isEmpty()

    val cachedPosition = MutableVec2f()
    val tmpPosition = MutableVec3f()
    val cachedSize = MutableVec2f()
    val cachedClip = MutableVec4f()

    private val posMember = cacheData.layout.getFloat3(UiVertexLayout.position.name)!!
    private val clipMember = cacheData.layout.getFloat4(UiVertexLayout.clip.name)!!

    fun appendTo(target: IndexedVertexList<*>) {
        val i0 = target.numVertices
        target.checkIndexSize(cacheData.numIndices)
        for (i in 0 until cacheData.numIndices) {
            target.indices.put(i0 + cacheData.indices[i])
        }
        target.checkBufferSize(cacheData.numVertices)
        target.vertexData.put(cacheData.vertexData)
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
        block: MeshBuilder<Layout>.() -> Unit
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
            cacheData.vertexData.set(i) {
                get(posMember, tmpPosition)
                tmpPosition.x += posOffX
                tmpPosition.y += posOffY
                set(posMember, tmpPosition)
                set(clipMember, cachedClip)
            }
        }
    }
}