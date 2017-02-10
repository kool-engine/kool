package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.platform.Float32Buffer
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.Uint32Buffer

/**
 * @author fabmax
 */
class IndexedVertexList(val hasNormals: Boolean, val hasColors: Boolean, val hasTexCoords: Boolean) {

    companion object {
        internal val INITIAL_SIZE = 1000
        internal val GROW_FACTOR = 2.0f
    }

    val vertexSize: Int
    val strideBytes: Int
    val positionOffset = 0
    val normalOffset: Int
    val colorOffset: Int
    val texCoordOffset: Int

    var elements = 0
        private set
    var size = 0
        private set
    var data: Float32Buffer
        private set
    var indices = Platform.createUint32Buffer(INITIAL_SIZE)
        private set

    private val addItem: Item

    init {
        var cnt = 3

        if (hasNormals) {
            normalOffset = cnt
            cnt += 3
        } else {
            normalOffset = -1
        }

        if (hasColors) {
            colorOffset = cnt
            cnt += 4
        } else {
            colorOffset = -1
        }

        if (hasTexCoords) {
            texCoordOffset = cnt
            cnt += 2
        } else {
            texCoordOffset = -1
        }

        vertexSize = cnt
        strideBytes = vertexSize * 4

        data = Platform.createFloat32Buffer(cnt * INITIAL_SIZE)
        addItem = Item(0)
    }

    private fun increaseDataSize() {
        val newData = Platform.createFloat32Buffer(Math.round(data.capacity * GROW_FACTOR))
        for (i in 0..data.capacity-1) {
            newData[i] = data[i]
        }
        newData.position = data.position
        data = newData
    }

    private fun increaseIndicesSize() {
        val newIdxs = Platform.createUint32Buffer(Math.round(indices.capacity * IndexedVertexList.GROW_FACTOR))
        for (i in 0..indices.capacity-1) {
            newIdxs[i] = indices[i]
        }
        newIdxs.position = indices.position
        indices = newIdxs
    }

    fun addVertex(init: Item.() -> Unit): Int {
        if (data.remaining < vertexSize) {
            increaseDataSize()
        }
        for (i in 1..vertexSize) {
            data += 0f
        }
        addItem.index = elements++
        size += vertexSize
        addItem.init()
        return elements - 1
    }

    fun addVertex(position: Vec3f, normal: Vec3f? = null, color: Color? = null, texCoord: Vec2f? = null): Int {
        return addVertex {
            this.position.set(position)
            if (normal != null) {
                this.normal.set(normal)
            }
            if (color != null) {
                this.color.set(color)
            }
            if (texCoord!= null) {
                this.texCoord.set(texCoord)
            }
        }
    }

    fun addIndex(idx: Int) {
        if (indices.remaining == 0) {
            increaseIndicesSize()
        }
        indices += idx
    }

    fun addIndices(indices: IntArray) {
        for (idx in indices.indices) {
            addIndex(indices[idx])
        }
    }

    fun clear() {
        size = 0
        elements = 0
        data.position = 0
        data.limit = data.capacity
        indices.position = 0
        indices.limit = indices.capacity
    }

    operator fun get(i: Int): Item {
        if (i < 0 || i >= data.capacity) {
            throw KoolException("Vertex index out of bounds: $i")
        }
        return Item(i)
    }

    inner class Item(index: Int) {
        private var offset = index * vertexSize
        val position = Vec3fView(positionOffset)
        val normal = Vec3fView(normalOffset)
        val color = ColorView(colorOffset)
        val texCoord = Vec2fView(texCoordOffset)

        var index = index
            set(value) {
                field = value
                offset = value * vertexSize
            }

        inner class Vec2fView(private val componentOffset: Int) : MutableVec2f() {
            override var x: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset] = value }
                }
            override var y: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset + 1] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset + 1] = value }
                }
        }

        inner class Vec3fView(val componentOffset: Int) : MutableVec3f() {
            override var x: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset] = value }
                }
            override var y: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset + 1] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset + 1] = value }
                }
            override var z: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset + 2] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset + 2] = value }
                }
        }

        inner class ColorView(val componentOffset: Int) : MutableColor() {
            override var x: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset] = value }
                }
            override var y: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset + 1] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset + 1] = value }
                }
            override var z: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset + 2] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset + 2] = value }
                }
            override var w: Float
                get() {
                    if (componentOffset < 0) { return 0f }
                    else { return data[offset + componentOffset + 3] }
                }
                set(value) {
                    if (componentOffset >= 0) { data[offset + componentOffset + 3] = value }
                }
        }
    }
}