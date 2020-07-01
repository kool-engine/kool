package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import kotlin.math.max
import kotlin.math.round

class IndexedVertexList(val vertexAttributes: List<Attribute>) {

    /**
     * Hash of present vertexAttributes, can be used to check for same attributes (incl. order) of two IndexedVertexLists
     */
    val attributeHash: Long = vertexAttributes.fold(0L) { h, a -> h * 31 + a.hashCode() }

    /**
     * Number of floats per vertex. E.g. a vertex containing only a position consists of 3 floats.
     */
    val vertexSizeF: Int

    /**
     * Number of float bytes per vertex. E.g. a vertex containing only a position consists of 3 * 4 = 12 bytes.
     */
    val byteStrideF: Int

    /**
     * Number of ints per vertex. E.g. a vertex with 4 joint indices consists of 4 ints.
     */
    val vertexSizeI: Int

    /**
     * Number of int bytes per vertex. E.g. a vertex with 4 joint indices consists of 4 * 4 = 16 bytes.
     */
    val byteStrideI: Int

    /**
     * Vertex attribute offsets in bytes.
     */
    val attributeByteOffsets: Map<Attribute, Int>

    /**
     * Primitive type of geometry in this vertex list
     */
    var primitiveType = PrimitiveType.TRIANGLES

    /**
     * Expected usage of geometry in this vertex list: STATIC if geometry is expected to change very infrequently /
     * never, DYNAMIC if it will be updated often.
     */
    var usage = Usage.STATIC

    /**
     * Number of vertices.
     */
    var numVertices = 0

    val numIndices: Int
        get() = indices.position

    val numPrimitives: Int
        get() = numIndices / primitiveType.nVertices

    val lastIndex
        get() = numVertices - 1

    var dataF: Float32Buffer
    var dataI: Uint32Buffer
    var indices = createUint32Buffer(INITIAL_SIZE)

    val bounds = BoundingBox()

    val vertexIt: VertexView

    var isRebuildBoundsOnSync = false
    var hasChanged = true
    var isBatchUpdate = false

    constructor(vararg vertexAttributes: Attribute) : this(vertexAttributes.toList())

    init {
        var strideF = 0
        var strideI = 0

        val byteOffsets = mutableMapOf<Attribute, Int>()
        for (attrib in vertexAttributes) {
            if (attrib.type == GlslType.MAT_2F || attrib.type == GlslType.MAT_3F || attrib.type == GlslType.MAT_4F) {
                throw IllegalArgumentException("Matrix types are not supported as vertex attributes")
            }
            if (attrib.type.isInt) {
                byteOffsets[attrib] = strideI
                strideI += attrib.type.byteSize
            } else {
                byteOffsets[attrib] = strideF
                strideF += attrib.type.byteSize
            }
        }
        attributeByteOffsets = byteOffsets

        vertexSizeF = strideF / 4
        byteStrideF = strideF
        vertexSizeI = strideI / 4
        byteStrideI = strideI

        dataF = createFloat32Buffer(strideF * INITIAL_SIZE)
        dataI = createUint32Buffer(strideI * INITIAL_SIZE)
        vertexIt = VertexView(this, 0)
    }

    fun isEmpty(): Boolean = numVertices == 0 || numIndices == 0

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
            increaseDataSizeF(max(round(dataF.capacity * GROW_FACTOR).toInt(), (numVertices + reqSpace) * vertexSizeF))
        }
        if (dataI.remaining < vertexSizeI * reqSpace) {
            increaseDataSizeI(max(round(dataI.capacity * GROW_FACTOR).toInt(), (numVertices + reqSpace) * vertexSizeI))
        }
    }

    fun checkIndexSize(reqSpace: Int = 1) {
        if (indices.remaining < reqSpace) {
            increaseIndicesSize(max(round(indices.capacity * GROW_FACTOR).toInt(), numVertices + reqSpace))
        }
    }

    fun hasAttribute(attribute: Attribute): Boolean = vertexAttributes.contains(attribute)

    inline fun batchUpdate(rebuildBounds: Boolean = false, block: IndexedVertexList.() -> Unit) {
        val wasBatchUpdate = isBatchUpdate
        isBatchUpdate = true
        block.invoke(this)
        hasChanged = true
        isBatchUpdate = wasBatchUpdate
        if (rebuildBounds) {
            rebuildBounds()
        }
    }

    inline fun addVertex(block: VertexView.() -> Unit): Int {
        checkBufferSizes()

        // initialize all vertex values with 0
        for (i in 1..vertexSizeF) {
            dataF += 0f
        }
        for (i in 1..vertexSizeI) {
            dataI += 0
        }

        vertexIt.index = numVertices++
        vertexIt.block()
        bounds.add(vertexIt.position)
        hasChanged = true
        return numVertices - 1
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

    fun addGeometry(geometry: IndexedVertexList) {
        val baseIdx = numVertices

        checkBufferSizes(geometry.numVertices)
        for (i in 0 until geometry.numVertices) {
            addVertex {
                geometry.vertexIt.index = i
                set(geometry.vertexIt)
            }
        }

        checkIndexSize(geometry.indices.position)
        for (i in 0 until geometry.indices.position) {
            addIndex(baseIdx + geometry.indices[i])
        }
    }

    fun addIndex(idx: Int) {
        if (indices.remaining == 0) {
            checkIndexSize()
        }
        indices += idx
    }

    fun addIndices(vararg indices: Int) {
        for (idx in indices.indices) {
            addIndex(indices[idx])
        }
        hasChanged = true
    }

    fun addIndices(indices: List<Int>) {
        for (idx in indices.indices) {
            addIndex(indices[idx])
        }
    }

    fun addTriIndices(i0: Int, i1: Int, i2: Int) {
        addIndex(i0)
        addIndex(i1)
        addIndex(i2)
    }

    fun rebuildBounds() {
        bounds.clear()
        for (i in 0 until numVertices) {
            vertexIt.index = i
            bounds.add(vertexIt.position)
        }
    }

    fun clear() {
        numVertices = 0

        dataF.position = 0
        dataF.limit = dataF.capacity

        dataI.position = 0
        dataI.limit = dataI.capacity

        indices.position = 0
        indices.limit = indices.capacity

        hasChanged = true
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
        if (newSize > numVertices) {
            throw KoolException("new size must be less (or equal) than old size")
        }

        numVertices = newSize

        dataF.position = newSize * vertexSizeF
        dataF.limit = dataF.capacity

        dataI.position = newSize * vertexSizeI
        dataI.limit = dataI.capacity
    }

    operator fun get(i: Int): VertexView {
        if (i < 0 || i >= dataF.capacity / vertexSizeF) {
            throw KoolException("Vertex index out of bounds: $i")
        }
        return VertexView(this, i)
    }

    inline fun forEach(block: (VertexView) -> Unit) {
        for (i in 0 until numVertices) {
            vertexIt.index = i
            block(vertexIt)
        }
    }

    fun generateNormals() {
        if (!vertexAttributes.contains(Attribute.NORMALS)) {
            return
        }
        if (primitiveType != PrimitiveType.TRIANGLES) {
            throw KoolException("Normal generation is only supported for triangle meshes")
        }

        val v0 = this[0]
        val v1 = this[1]
        val v2 = this[2]
        val e1 = MutableVec3f()
        val e2 = MutableVec3f()
        val nrm = MutableVec3f()

        for (i in 0 until numVertices) {
            v0.index = i
            v0.normal.set(Vec3f.ZERO)
        }

        for (i in 0 until numIndices step 3) {
            v0.index = indices[i]
            v1.index = indices[i+1]
            v2.index = indices[i+2]

            if (v0.index > numVertices || v1.index > numVertices || v2.index > numVertices) {
                logE { "index to large ${v0.index}, ${v1.index}, ${v2.index}, sz: $numVertices" }
            }

            v1.position.subtract(v0.position, e1).norm()
            v2.position.subtract(v0.position, e2).norm()
            val a = triArea(v0.position, v1.position, v2.position)

            e1.cross(e2, nrm).norm().scale(a)
            if (nrm.x.isNaN() || nrm.y.isNaN() || nrm.z.isNaN()) {
                logW { "degenerated triangle" }
            } else {
                v0.normal += nrm
                v1.normal += nrm
                v2.normal += nrm
            }
        }

        for (i in 0 until numVertices) {
            v0.index = i
            v0.normal.norm()
        }
    }

    fun generateTangents(tangentSign: Float = 1f) {
        if (!vertexAttributes.contains(Attribute.TANGENTS)) {
            return
        }
        if (primitiveType != PrimitiveType.TRIANGLES) {
            throw KoolException("Normal generation is only supported for triangle meshes")
        }

        val v0 = this[0]
        val v1 = this[1]
        val v2 = this[2]
        val e1 = MutableVec3f()
        val e2 = MutableVec3f()
        val tan = MutableVec3f()

        for (i in 0 until numVertices) {
            v0.index = i
            v0.tangent.set(Vec3f.ZERO)
        }

        for (i in 0 until numIndices step 3) {
            v0.index = indices[i]
            v1.index = indices[i+1]
            v2.index = indices[i+2]

            v1.position.subtract(v0.position, e1)
            v2.position.subtract(v0.position, e2)

            val du1 = v1.texCoord.x - v0.texCoord.x
            val dv1 = v1.texCoord.y - v0.texCoord.y
            val du2 = v2.texCoord.x - v0.texCoord.x
            val dv2 = v2.texCoord.y - v0.texCoord.y
            val f = 1f / (du1 * dv2 - du2 * dv1)
            if (f.isNaN()) {
                logW { "degenerated triangle" }
            } else {
                tan.x = f * (dv2 * e1.x - dv1 * e2.x)
                tan.y = f * (dv2 * e1.y - dv1 * e2.y)
                tan.z = f * (dv2 * e1.z - dv1 * e2.z)

                v0.tangent += Vec4f(tan, 0f)
                v1.tangent += Vec4f(tan, 0f)
                v2.tangent += Vec4f(tan, 0f)
            }
        }

        for (i in 0 until numVertices) {
            v0.index = i

            if (v0.normal.sqrLength() == 0f) {
                logW { "singular normal" }
                v0.normal.set(Vec3f.Y_AXIS)
            }

            if (v0.tangent.sqrLength() != 0f) {
                v0.tangent.norm()
                v0.tangent.w = tangentSign
            } else {
                logW { "singular tangent" }
                v0.normal.set(Vec3f.X_AXIS)
            }
        }
    }

    companion object {
        private const val INITIAL_SIZE = 1000
        private const val GROW_FACTOR = 2.0f
    }

}

enum class PrimitiveType(val nVertices: Int) {
    LINES(2),
    POINTS(1),
    TRIANGLES(3)
}

enum class Usage {
    DYNAMIC,
    STATIC
}