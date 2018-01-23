package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.shading.Shader
import kotlin.math.round

/**
 * @author fabmax
 */

enum class AttributeType(val size: Int) {
    INT(1),
    VEC_2I(2),
    VEC_3I(3),
    VEC_4I(4),

    FLOAT(1),
    VEC_2F(2),
    VEC_3F(3),
    VEC_4F(4),

    COLOR_4F(4)
}

data class Attribute(val name: String, val type: AttributeType) {
    companion object {
        val POSITIONS = Attribute("positions", AttributeType.VEC_3F)
        val NORMALS = Attribute("normals", AttributeType.VEC_3F)
        val TEXTURE_COORDS = Attribute("texture_coordinates", AttributeType.VEC_2F)
        val COLORS = Attribute("colors", AttributeType.COLOR_4F)
    }
}

class IndexedVertexList(vertexAttributes: Set<Attribute>) {

    companion object {
        internal val INITIAL_SIZE = 1000
        internal val GROW_FACTOR = 2.0f
    }

    val vertexSize: Int
    val strideBytes: Int

    var size = 0
        private set
    val lastIndex
        get() = size - 1
    var data: Float32Buffer
        private set
    var indices = createUint32Buffer(INITIAL_SIZE)
        private set

    val attributeOffsets: Map<Attribute, Int>

    private var dataSize = 0

    private val tmpVertex: Vertex

    init {
        var cnt = 0

        val offsets = mutableMapOf<Attribute, Int>()
        for (attrib in vertexAttributes) {
            offsets[attrib] = cnt
            cnt += attrib.type.size
        }
        attributeOffsets = offsets

        vertexSize = cnt
        strideBytes = vertexSize * 4

        data = createFloat32Buffer(cnt * INITIAL_SIZE)
        tmpVertex = Vertex(0)
    }

    private fun increaseDataSize() {
        val newData = createFloat32Buffer(round(data.capacity * GROW_FACTOR).toInt())
        for (i in 0 until data.capacity) {
            newData[i] = data[i]
        }
        newData.position = data.position
        data = newData
    }

    private fun increaseIndicesSize() {
        val newIdxs = createUint32Buffer(round(indices.capacity * IndexedVertexList.GROW_FACTOR).toInt())
        for (i in 0 until indices.capacity) {
            newIdxs[i] = indices[i]
        }
        newIdxs.position = indices.position
        indices = newIdxs
    }

    fun addVertex(updateBounds: BoundingBox? = null, block: Vertex.() -> Unit): Int {
        if (data.remaining < vertexSize) {
            increaseDataSize()
        }
        for (i in 1..vertexSize) {
            data += 0f
        }
        tmpVertex.index = size++
        dataSize += vertexSize
        tmpVertex.block()

        updateBounds?.add(tmpVertex.position)

        return size - 1
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
        dataSize = 0
        size = 0
        data.position = 0
        data.limit = data.capacity
        indices.position = 0
        indices.limit = indices.capacity
    }

    operator fun get(i: Int): Vertex {
        if (i < 0 || i >= data.capacity / vertexSize) {
            throw KoolException("Vertex index out of bounds: $i")
        }
        return Vertex(i)
    }

    inner class Vertex(index: Int) {
        private var offset = index * vertexSize

        // standard attributes for easy access
        val position: Vec3fView
        val normal: Vec3fView
        val color: ColorView
        val texCoord: Vec2fView

        val vec2fAttributes: Map<Attribute, Vec2fView>
        val vec3fAttributes: Map<Attribute, Vec3fView>
        val vec4fAttributes: Map<Attribute, Vec4fView>
        val colorAttributes: Map<Attribute, ColorView>

        init {
            val vec2fAttribs = mutableMapOf<Attribute, Vec2fView>()
            val vec3fAttribs = mutableMapOf<Attribute, Vec3fView>()
            val vec4fAttribs = mutableMapOf<Attribute, Vec4fView>()
            val colorAttribs = mutableMapOf<Attribute, ColorView>()

            for (offset in attributeOffsets) {
                when (offset.key.type) {
                    AttributeType.VEC_2F -> vec2fAttribs[offset.key] = Vec2fView(offset.value)
                    AttributeType.VEC_3F -> vec3fAttribs[offset.key] = Vec3fView(offset.value)
                    AttributeType.VEC_4F -> vec4fAttribs[offset.key] = Vec4fView(offset.value)
                    AttributeType.COLOR_4F -> colorAttribs[offset.key] = ColorView(offset.value)
                    else -> println("Unmapped attribute type: ${offset.key.type}")
                }
            }

            position = vec3fAttribs[Attribute.POSITIONS] ?: Vec3fView(-1)
            normal = vec3fAttribs[Attribute.NORMALS] ?: Vec3fView(-1)
            texCoord = vec2fAttribs[Attribute.TEXTURE_COORDS] ?: Vec2fView(-1)
            color = colorAttribs[Attribute.COLORS] ?: ColorView(-1)

            vec2fAttributes = vec2fAttribs
            vec3fAttributes = vec3fAttribs
            vec4fAttributes = vec4fAttribs
            colorAttributes = colorAttribs
        }

        var index = index
            set(value) {
                field = value
                offset = value * vertexSize
            }

        inner class Vec2fView(private val attribOffset: Int) : MutableVec2f() {
            override var x: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset] = value }
                }
            override var y: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 1] = value }
                }
        }

        inner class Vec3fView(val attribOffset: Int) : MutableVec3f() {
            override var x: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset] = value }
                }
            override var y: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 1] = value }
                }
            override var z: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 2] = value }
                }
        }

        inner class Vec4fView(val attribOffset: Int) : MutableVec4f() {
            override var x: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset] = value }
                }
            override var y: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 1] = value }
                }
            override var z: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 2] = value }
                }
            override var w: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 3] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 3] = value }
                }
        }

        inner class ColorView(val attribOffset: Int) : MutableColor() {
            override var r: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset] = value }
                }
            override var x: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset] = value }
                }

            override var g: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 1] = value }
                }
            override var y: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 1] = value }
                }

            override var b: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 2] = value }
                }
            override var z: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 2] = value }
                }

            override var a: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 3] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 3] = value }
                }
            override var w: Float
                get() = if (attribOffset < 0) { 0f } else { data[offset + attribOffset + 3] }
                set(value) {
                    if (attribOffset >= 0) { data[offset + attribOffset + 3] = value }
                }
        }
    }
}