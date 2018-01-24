package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.gl.GL_FLOAT
import de.fabmax.kool.gl.GL_INT
import kotlin.math.round

/**
 * @author fabmax
 */

enum class AttributeType(val size: Int, val isInt: Boolean, val glType: Int) {
    FLOAT(1, false, GL_FLOAT),
    VEC_2F(2, false, GL_FLOAT),
    VEC_3F(3, false, GL_FLOAT),
    VEC_4F(4, false, GL_FLOAT),

    COLOR_4F(4, false, GL_FLOAT),

    INT(1, true, GL_INT),
    VEC_2I(2, true, GL_INT),
    VEC_3I(3, true, GL_INT),
    VEC_4I(4, true, GL_INT)
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

    val vertexSizeF: Int
    val strideBytesF: Int
    val vertexSizeI: Int
    val strideBytesI: Int

    var size = 0
        private set
    val lastIndex
        get() = size - 1

    var dataF: Float32Buffer
        private set
    var dataI: Uint32Buffer
        private set
    var indices = createUint32Buffer(INITIAL_SIZE)
        private set

    val attributeOffsets: Map<Attribute, Int>

    private val tmpVertex: Vertex

    init {
        var cntF = 0
        var cntI = 0

        val offsets = mutableMapOf<Attribute, Int>()
        for (attrib in vertexAttributes) {
            if (attrib.type.isInt) {
                offsets[attrib] = cntI
                cntI += attrib.type.size
            } else {
                offsets[attrib] = cntF
                cntF += attrib.type.size
            }
        }
        attributeOffsets = offsets

        vertexSizeF = cntF
        strideBytesF = vertexSizeF * 4
        vertexSizeI = cntI
        strideBytesI = vertexSizeI * 4

        dataF = createFloat32Buffer(cntF * INITIAL_SIZE)
        dataI = createUint32Buffer(cntI * INITIAL_SIZE)
        tmpVertex = Vertex(0)
    }

    private fun increaseDataSizeF() {
        val newData = createFloat32Buffer(round(dataF.capacity * GROW_FACTOR).toInt())
        for (i in 0 until dataF.capacity) {
            newData[i] = dataF[i]
        }
        newData.position = dataF.position
        dataF = newData
    }

    private fun increaseDataSizeI() {
        val newData = createUint32Buffer(round(dataI.capacity * GROW_FACTOR).toInt())
        for (i in 0 until dataI.capacity) {
            newData[i] = dataI[i]
        }
        newData.position = dataI.position
        dataI = newData
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
        if (dataF.remaining < vertexSizeF) {
            increaseDataSizeF()
        }
        if (dataI.remaining < vertexSizeI) {
            increaseDataSizeI()
        }

        // initialize all vertex values with 0
        for (i in 1..vertexSizeF) {
            dataF += 0f
        }
        for (i in 1..vertexSizeI) {
            dataI += 0
        }

        tmpVertex.index = size++
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
        size = 0

        dataF.position = 0
        dataF.limit = dataF.capacity

        dataI.position = 0
        dataI.limit = dataI.capacity

        indices.position = 0
        indices.limit = indices.capacity
    }

    operator fun get(i: Int): Vertex {
        if (i < 0 || i >= dataF.capacity / vertexSizeF) {
            throw KoolException("Vertex index out of bounds: $i")
        }
        return Vertex(i)
    }

    inner class Vertex(index: Int) {
        private var offsetF = index * vertexSizeF
        private var offsetI = index * vertexSizeI

        /**
         * Vertex index in the underlying vertex list. Can be set to navigate within the vertex list
         */
        var index = index
            set(value) {
                field = value
                offsetF = value * vertexSizeF
                offsetI = value * vertexSizeI
            }

        // standard attributes for easy access
        val position: Vec3fView
        val normal: Vec3fView
        val color: ColorView
        val texCoord: Vec2fView

        private val attributeViews: Map<Attribute, Any>

        init {
            val attribViews = mutableMapOf<Attribute, Any>()
            attributeViews = attribViews

            for (offset in attributeOffsets) {
                when (offset.key.type) {
                    AttributeType.FLOAT -> attribViews[offset.key] = FloatView(offset.value)
                    AttributeType.VEC_2F -> attribViews[offset.key] = Vec2fView(offset.value)
                    AttributeType.VEC_3F -> attribViews[offset.key] = Vec3fView(offset.value)
                    AttributeType.VEC_4F -> attribViews[offset.key] = Vec4fView(offset.value)
                    AttributeType.COLOR_4F -> attribViews[offset.key] = ColorView(offset.value)

                    AttributeType.INT -> attribViews[offset.key] = IntView(offset.value)
                    AttributeType.VEC_2I -> attribViews[offset.key] = Vec2iView(offset.value)
                    AttributeType.VEC_3I -> attribViews[offset.key] = Vec3iView(offset.value)
                    AttributeType.VEC_4I -> attribViews[offset.key] = Vec4iView(offset.value)
                }
            }

            position = getVec3fAttribute(Attribute.POSITIONS) ?: Vec3fView(-1)
            normal = getVec3fAttribute(Attribute.NORMALS) ?: Vec3fView(-1)
            texCoord = getVec2fAttribute(Attribute.TEXTURE_COORDS) ?: Vec2fView(-1)
            color = getColorAttribute(Attribute.COLORS) ?: ColorView(-1)
        }

        fun getFloatAttribute(attribute: Attribute): FloatView? = attributeViews[attribute] as FloatView?
        fun getVec2fAttribute(attribute: Attribute): Vec2fView? = attributeViews[attribute] as Vec2fView?
        fun getVec3fAttribute(attribute: Attribute): Vec3fView? = attributeViews[attribute] as Vec3fView?
        fun getVec4fAttribute(attribute: Attribute): Vec4fView? = attributeViews[attribute] as Vec4fView?
        fun getColorAttribute(attribute: Attribute): ColorView? = attributeViews[attribute] as ColorView?
        fun getIntAttribute(attribute: Attribute): IntView? = attributeViews[attribute] as IntView?
        fun getVec2iAttribute(attribute: Attribute): Vec2iView? = attributeViews[attribute] as Vec2iView?
        fun getVec3iAttribute(attribute: Attribute): Vec3iView? = attributeViews[attribute] as Vec3iView?
        fun getVec4iAttribute(attribute: Attribute): Vec4iView? = attributeViews[attribute] as Vec4iView?

        inner class FloatView(private val attribOffset: Int) {
            var f: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset] = value }
                }
        }

        inner class Vec2fView(private val attribOffset: Int) : MutableVec2f() {
            override var x: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset] = value }
                }
            override var y: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 1] = value }
                }
        }

        inner class Vec3fView(val attribOffset: Int) : MutableVec3f() {
            override var x: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset] = value }
                }
            override var y: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 1] = value }
                }
            override var z: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 2] = value }
                }
        }

        inner class Vec4fView(val attribOffset: Int) : MutableVec4f() {
            override var x: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset] = value }
                }
            override var y: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 1] = value }
                }
            override var z: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 2] = value }
                }
            override var w: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 3] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 3] = value }
                }
        }

        inner class ColorView(val attribOffset: Int) : MutableColor() {
            override var r: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset] = value }
                }
            override var x: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset] = value }
                }

            override var g: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 1] = value }
                }
            override var y: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 1] = value }
                }

            override var b: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 2] = value }
                }
            override var z: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 2] = value }
                }

            override var a: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 3] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 3] = value }
                }
            override var w: Float
                get() = if (attribOffset < 0) { 0f } else { dataF[offsetF + attribOffset + 3] }
                set(value) {
                    if (attribOffset >= 0) { dataF[offsetF + attribOffset + 3] = value }
                }
        }

        inner class IntView(private val attribOffset: Int) {
            var i: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset] = value }
                }
        }

        inner class Vec2iView(private val attribOffset: Int) {
            var x: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset] = value }
                }
            var y: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset + 1] = value }
                }
        }

        inner class Vec3iView(private val attribOffset: Int) {
            var x: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset] = value }
                }
            var y: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset + 1] = value }
                }
            var z: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset + 2] = value }
                }
        }

        inner class Vec4iView(private val attribOffset: Int) {
            var x: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset] = value }
                }
            var y: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset + 1] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset + 1] = value }
                }
            var z: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset + 2] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset + 2] = value }
                }
            var w: Int
                get() = if (attribOffset < 0) { 0 } else { dataI[offsetI + attribOffset + 3] }
                set(value) {
                    if (attribOffset >= 0) { dataI[offsetI + attribOffset + 3] = value }
                }
        }
    }
}