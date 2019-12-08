package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.*
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.AttributeType
import kotlin.math.max
import kotlin.math.round

class IndexedVertexList(vertexAttributes: Set<Attribute>) {

    companion object {
        internal val INITIAL_SIZE = 1000
        internal val GROW_FACTOR = 2.0f
    }

    /**
     * Number of floats per vertex. E.g. a vertex containing only a position consists of 3 floats.
     */
    val vertexSizeF: Int

    /**
     * Number of float bytes per vertex. E.g. a vertex containing only a position consists of 3 * 4 = 12 bytes.
     */
    val strideBytesF: Int

    /**
     * Number of ints per vertex. E.g. a vertex with 4 bone indices consists of 4 ints.
     */
    val vertexSizeI: Int

    /**
     * Number of int byte per vertex. E.g. a vertex with 4 bone indices consists of 4 * 4 = 16 bytes.
     */
    val strideBytesI: Int

    /**
     * Number of vertices. Equal to [dataF.position] / [vertexSizeF] and [dataI.position] / [vertexSizeI].
     */
    var size = 0
    val lastIndex
        get() = size - 1

    var dataF: Float32Buffer
    var dataI: Uint32Buffer
    var indices = createUint32Buffer(INITIAL_SIZE)

    /**
     * Vertex attribute offsets in bytes.
     */
    val attributeOffsets: Map<Attribute, Int>

    val vertexIt: Vertex

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

        vertexSizeF = cntF / 4
        strideBytesF = cntF
        vertexSizeI = cntI / 4
        strideBytesI = cntI

        dataF = createFloat32Buffer(cntF * INITIAL_SIZE)
        dataI = createUint32Buffer(cntI * INITIAL_SIZE)
        vertexIt = Vertex(0)
    }

    private fun increaseDataSizeF(newSize: Int) {
        val newData = createFloat32Buffer(newSize)
        dataF.flip()
        newData.put(dataF)
        dataF = newData
    }

    private fun increaseDataSizeI(newSize: Int) {
        val newData = createUint32Buffer(newSize)
        dataI.flip()
        newData.put(dataI)
        dataI = newData
    }

    private fun increaseIndicesSize(newSize: Int) {
        val newIdxs = createUint32Buffer(newSize)
        indices.flip()
        newIdxs.put(indices)
        indices = newIdxs
    }

    fun checkBufferSizes(reqSpace: Int = 1) {
        if (dataF.remaining < vertexSizeF * reqSpace) {
            increaseDataSizeF(max(round(dataF.capacity * GROW_FACTOR).toInt(), (size + reqSpace) * vertexSizeF))
        }
        if (dataI.remaining < vertexSizeI * reqSpace) {
            increaseDataSizeI(max(round(dataI.capacity * GROW_FACTOR).toInt(), (size + reqSpace) * vertexSizeI))
        }
    }

    fun checkIndexSize(reqSpace: Int = 1) {
        if (indices.remaining < reqSpace) {
            increaseIndicesSize(max(round(indices.capacity * GROW_FACTOR).toInt(), size + reqSpace))
        }
    }

    inline fun addVertex(updateBounds: BoundingBox? = null, block: Vertex.() -> Unit): Int {
        checkBufferSizes()

        // initialize all vertex values with 0
        for (i in 1..vertexSizeF) {
            dataF += 0f
        }
        for (i in 1..vertexSizeI) {
            dataI += 0
        }

        vertexIt.index = size++
        vertexIt.block()

        updateBounds?.add(vertexIt.position)

        return size - 1
    }

    fun addFrom(other: IndexedVertexList) {
        val baseIdx = size

        checkBufferSizes(other.size)
        for (i in 0 until other.size) {
            addVertex {
                other.vertexIt.index = i
                set(other.vertexIt)
            }
        }

        checkIndexSize(other.indices.position)
        for (i in 0 until other.indices.position) {
            addIndex(baseIdx + other.indices[i])
        }
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
            checkIndexSize()
        }
        indices += idx
    }

    fun addIndices(indices: IntArray) {
        for (idx in indices.indices) {
            addIndex(indices[idx])
        }
    }

    fun addIndices(indices: List<Int>) {
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

    fun clearIndices() {
        indices.position = 0
        indices.limit = indices.capacity
    }

    fun shrinkIndices(newSize: Int) {
        if (newSize > indices.position) {
            throw KoolException("new size must be less (or equal) than old size")
        }

        indices.position = newSize
        indices.limit = indices.capacity
    }

    fun shrinkVertices(newSize: Int) {
        if (newSize > size) {
            throw KoolException("new size must be less (or equal) than old size")
        }

        size = newSize

        dataF.position = newSize * vertexSizeF
        dataF.limit = dataF.capacity

        dataI.position = newSize * vertexSizeI
        dataI.limit = dataI.capacity
    }

    operator fun get(i: Int): Vertex {
        if (i < 0 || i >= dataF.capacity / vertexSizeF) {
            throw KoolException("Vertex index out of bounds: $i")
        }
        return Vertex(i)
    }

    inline fun forEach(block: (Vertex) -> Unit) {
        for (i in 0 until size) {
            vertexIt.index = i
            block(vertexIt)
        }
    }

    inner class Vertex(index: Int) : MutableVec3f() {
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
        val position: MutableVec3f
        val normal: MutableVec3f
        val tangent: MutableVec3f
        val color: MutableColor
        val texCoord: MutableVec2f

        private val attributeViews: Map<Attribute, Any>


        override var x: Float
            get() = position.x
            set(value) { position.x = value}
        override var y: Float
            get() = position.y
            set(value) { position.y = value }
        override var z: Float
            get() = position.z
            set(value) { position.z = value}

        init {
            val attribViews = mutableMapOf<Attribute, Any>()
            attributeViews = attribViews

            for (offset in attributeOffsets) {
                when (offset.key.type) {
                    AttributeType.FLOAT -> attribViews[offset.key] = FloatView(offset.value / 4)
                    AttributeType.VEC_2F -> attribViews[offset.key] = Vec2fView(offset.value / 4)
                    AttributeType.VEC_3F -> attribViews[offset.key] = Vec3fView(offset.value / 4)
                    AttributeType.VEC_4F -> attribViews[offset.key] = Vec4fView(offset.value / 4)
                    AttributeType.COLOR_4F -> attribViews[offset.key] = ColorView(offset.value / 4)

                    AttributeType.INT -> attribViews[offset.key] = IntView(offset.value / 4)
                    AttributeType.VEC_2I -> attribViews[offset.key] = Vec2iView(offset.value / 4)
                    AttributeType.VEC_3I -> attribViews[offset.key] = Vec3iView(offset.value / 4)
                    AttributeType.VEC_4I -> attribViews[offset.key] = Vec4iView(offset.value / 4)
                }
            }

            position = getVec3fAttribute(Attribute.POSITIONS) ?: Vec3fView(-1)
            normal = getVec3fAttribute(Attribute.NORMALS) ?: Vec3fView(-1)
            tangent = getVec3fAttribute(Attribute.TANGENTS) ?: Vec3fView(-1)
            texCoord = getVec2fAttribute(Attribute.TEXTURE_COORDS) ?: Vec2fView(-1)
            color = getColorAttribute(Attribute.COLORS) ?: ColorView(-1)
        }

        fun set(other: Vertex) {
            for (attrib in attributeViews.keys) {
                val view = other.attributeViews[attrib]
                if (view != null) {
                    when (view) {
                        is FloatView -> (attributeViews[attrib] as FloatView).f = view.f
                        is Vec2fView -> (attributeViews[attrib] as Vec2fView).set(view)
                        is Vec3fView -> (attributeViews[attrib] as Vec3fView).set(view)
                        is Vec4fView -> (attributeViews[attrib] as Vec4fView).set(view)
                        is ColorView -> (attributeViews[attrib] as ColorView).set(view)
                        is IntView   -> (attributeViews[attrib] as IntView).i = view.i
                        is Vec2iView -> (attributeViews[attrib] as Vec2iView).set(view)
                        is Vec3iView -> (attributeViews[attrib] as Vec3iView).set(view)
                        is Vec4iView -> (attributeViews[attrib] as Vec4iView).set(view)
                    }
                }
            }
        }

        fun getFloatAttribute(attribute: Attribute): FloatView? = attributeViews[attribute] as FloatView?
        fun getVec2fAttribute(attribute: Attribute): MutableVec2f? = attributeViews[attribute] as MutableVec2f?
        fun getVec3fAttribute(attribute: Attribute): MutableVec3f? = attributeViews[attribute] as MutableVec3f?
        fun getVec4fAttribute(attribute: Attribute): MutableVec4f? = attributeViews[attribute] as MutableVec4f?
        fun getColorAttribute(attribute: Attribute): MutableColor? = attributeViews[attribute] as MutableColor?
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

        private inner class Vec2fView(private val attribOffset: Int) : MutableVec2f() {
            override operator fun get(i: Int): Float {
                return if (attribOffset >= 0 && i in 0..1) {
                    dataF[offsetF + attribOffset + i]
                } else {
                    0f
                }
            }
            override operator fun set(i: Int, v: Float) {
                if (attribOffset >= 0 && i in 0..1) {
                    dataF[offsetF + attribOffset + i] = v
                }
            }
        }

        private inner class Vec3fView(val attribOffset: Int) : MutableVec3f() {
            override operator fun get(i: Int): Float {
                return if (attribOffset >= 0 && i in 0..2) {
                    dataF[offsetF + attribOffset + i]
                } else {
                    0f
                }
            }
            override operator fun set(i: Int, v: Float) {
                if (attribOffset >= 0 && i in 0..2) {
                    dataF[offsetF + attribOffset + i] = v
                }
            }
        }

        private inner class Vec4fView(val attribOffset: Int) : MutableVec4f() {
            override operator fun get(i: Int): Float {
                return if (attribOffset >= 0 && i in 0..3) {
                    dataF[offsetF + attribOffset + i]
                } else {
                    0f
                }
            }
            override operator fun set(i: Int, v: Float) {
                if (attribOffset >= 0 && i in 0..3) {
                    dataF[offsetF + attribOffset + i] = v
                }
            }
        }

        private inner class ColorView(val attribOffset: Int) : MutableColor() {
            override operator fun get(i: Int): Float {
                return if (attribOffset >= 0 && i in 0..3) {
                    dataF[offsetF + attribOffset + i]
                } else {
                    0f
                }
            }
            override operator fun set(i: Int, v: Float) {
                if (attribOffset >= 0 && i in 0..3) {
                    dataF[offsetF + attribOffset + i] = v
                }
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

            fun set(x: Int, y: Int) {
                this.x = x
                this.y = y
            }

            fun set(other: Vec2iView) {
                x = other.x
                y = other.y
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

            fun set(other: Vec3iView) {
                x = other.x
                y = other.y
                z = other.z
            }

            fun set(x: Int, y: Int, z: Int) {
                this.x = x
                this.y = y
                this.z = z
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

            fun set(other: Vec4iView) {
                x = other.x
                y = other.y
                z = other.z
                w = other.w
            }

            fun set(x: Int, y: Int, z: Int, w: Int) {
                this.x = x
                this.y = y
                this.z = z
                this.w = w
            }
        }
    }
}