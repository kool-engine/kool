package de.fabmax.kool.scene.geometry

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.math.spatial.InRadiusTraverser
import de.fabmax.kool.math.spatial.pointKdTree
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.pipeline.isInt
import de.fabmax.kool.util.*
import kotlin.math.abs
import kotlin.math.max

fun IndexedVertexList(vararg vertexAttributes: Attribute, primitiveType: PrimitiveType = PrimitiveType.TRIANGLES): IndexedVertexList {
    return IndexedVertexList(vertexAttributes.toList(), primitiveType)
}

class IndexedVertexList(
    val vertexAttributes: List<Attribute>,
    val primitiveType: PrimitiveType = PrimitiveType.TRIANGLES
) : BaseReleasable() {

    var name: String = "geometry"

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
        get() = primitiveType.getNumberOfPrimitives(numIndices)

    val lastIndex
        get() = numVertices - 1

    var dataF: Float32Buffer
        private set
    var dataI: Int32Buffer
        private set
    var indices = Uint32Buffer(INITIAL_SIZE, true)
        private set

    val bounds = BoundingBoxF()

    val vertexIt: VertexView

    var isRebuildBoundsOnSync = false
    var hasChanged = true
    var isBatchUpdate = false

    var gpuGeometry: GpuGeometry? = null

    init {
        var strideF = 0
        var strideI = 0

        val byteOffsets = mutableMapOf<Attribute, Int>()
        for (attrib in vertexAttributes) {
            if (attrib.type == GpuType.Mat2 || attrib.type == GpuType.Mat3 || attrib.type == GpuType.Mat4) {
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

        dataF = Float32Buffer(strideF * INITIAL_SIZE, true)
        dataI = Int32Buffer(strideI * INITIAL_SIZE, true)
        vertexIt = VertexView(this, 0)
    }

    fun hasAttributes(requiredAttributes: Set<Attribute>): Boolean {
        return vertexAttributes.containsAll(requiredAttributes)
    }

    fun getMorphAttributes(): List<Attribute> {
        val morphAttribs = mutableListOf<Attribute>()
        vertexAttributes.forEach { a ->
            if (a.name.startsWith(Attribute.NORMALS.name + "_") ||
                    a.name.startsWith(Attribute.POSITIONS.name + "_") ||
                    a.name.startsWith(Attribute.TANGENTS.name + "_")) {
                morphAttribs += a
            }
        }
        return morphAttribs
    }

    fun isEmpty(): Boolean = numVertices == 0 || numIndices == 0

    private fun increaseDataSizeF(newSize: Int) {
        val newData = Float32Buffer(newSize, true)
        newData.put(dataF)
        dataF = newData
    }

    private fun increaseDataSizeI(newSize: Int) {
        val newData = Int32Buffer(newSize, true)
        newData.put(dataI)
        dataI = newData
    }

    private fun increaseIndicesSize(newSize: Int) {
        val newIdxs = Uint32Buffer(newSize, true)
        newIdxs.put(indices)
        indices = newIdxs
    }

    fun checkBufferSizes(reqSpace: Int = 1) {
        if (dataF.remaining < vertexSizeF * reqSpace) {
            increaseDataSizeF(increaseSize(dataF.capacity, reqSpace * vertexSizeF))
        }
        if (dataI.remaining < vertexSizeI * reqSpace) {
            increaseDataSizeI(increaseSize(dataI.capacity, reqSpace * vertexSizeI))
        }
    }

    fun checkIndexSize(reqSpace: Int = 1) {
        if (indices.remaining < reqSpace) {
            increaseIndicesSize(increaseSize(indices.capacity, reqSpace))
        }
    }

    private fun increaseSize(currentSize: Int, requiredInc: Int, elemSize: Int = 4): Int {
        val increased = (max(currentSize + requiredInc.toLong(), (currentSize * GROW_FACTOR).toLong()))
            .coerceAtMost(Int.MAX_VALUE.toLong() / elemSize).toInt()
        check(increased - currentSize >= requiredInc) {
            "Unable to increase buffer to requested size of ${currentSize + requiredInc} elements. Underlying buffer " +
                    "size is limited to ${Int.MAX_VALUE.toLong() / elemSize} elements / ${Int.MAX_VALUE} bytes."
        }
        return increased
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

    fun addGeometry(geometry: IndexedVertexList) = addGeometry(geometry) { }

    inline fun addGeometry(geometry: IndexedVertexList, vertexMod: (VertexView.() -> Unit)) {
        val baseIdx = numVertices

        checkBufferSizes(geometry.numVertices)
        for (i in 0 until geometry.numVertices) {
            addVertex {
                geometry.vertexIt.index = i
                set(geometry.vertexIt)
                vertexMod.invoke(this)
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

        dataF.clear()
        dataI.clear()
        indices.clear()

        bounds.clear()
        hasChanged = true
    }

    fun clearIndices() {
        indices.clear()
    }

    fun shrinkIndices(newSize: Int) {
        check(newSize <= indices.position) { "new size must be less (or equal) than old size" }
        indices.position = newSize
    }

    fun shrinkVertices(newSize: Int) {
        check(newSize <= numVertices) { "new size must be less (or equal) than old size" }

        numVertices = newSize
        dataF.position = newSize * vertexSizeF
        dataI.position = newSize * vertexSizeI
    }

    operator fun get(i: Int): VertexView {
        check(i >= 0 && i < dataF.capacity / vertexSizeF) { "Vertex index out of bounds: $i" }
        return VertexView(this, i)
    }

    inline fun forEach(block: (VertexView) -> Unit) {
        for (i in 0 until numVertices) {
            vertexIt.index = i
            block(vertexIt)
        }
    }

    fun removeDegeneratedTriangles() {
        val v0 = this[0]
        val v1 = this[1]
        val v2 = this[2]

        val e1 = MutableVec3f()
        val e2 = MutableVec3f()

        val fixedIndices = IntArray(numIndices)
        var iFixed = 0
        for (i in 0 until numIndices step 3) {
            v0.index = indices[i]
            v1.index = indices[i + 1]
            v2.index = indices[i + 2]

            v1.position.subtract(v0.position, e1).norm()
            v2.position.subtract(v0.position, e2).norm()
            val a = triArea(v0.position, v1.position, v2.position)

            if (e1 != Vec3f.ZERO && e2 != Vec3f.ZERO && abs(e1.dot(e2)) != 1f && !a.isNaN() && a > 0f) {
                fixedIndices[iFixed++] = indices[i]
                fixedIndices[iFixed++] = indices[i + 1]
                fixedIndices[iFixed++] = indices[i + 2]
            }
        }
        if (iFixed != numIndices) {
            indices.clear()
            indices.put(fixedIndices, 0, iFixed)
        }
    }

    fun mergeCloseVertices(epsilon: Float = 0.001f) {
        val positions = mutableListOf<PointAndIndex>()
        forEach {
            positions += PointAndIndex(it, it.index)
        }

        val mergeMap = mutableMapOf<Int, Int>()

        val tree = pointKdTree(positions)
        val trav = InRadiusTraverser<PointAndIndex>()
        positions.forEach { pt ->
            trav.setup(pt, epsilon).traverse(tree)
            trav.result.removeAll { it.index in mergeMap.keys }
            trav.result.forEach { mergeMap[it.index] = pt.index }
        }

        val mergeDataF = Float32Buffer(dataF.capacity)
        val mergeDataI = Int32Buffer(dataI.capacity)
        val indexMap = mutableMapOf<Int, Int>()
        var j = 0
        for (i in 0 until numVertices) {
            val mergedI = mergeMap[i] ?: i
            if (mergedI == i) {
                indexMap[mergedI] = j
                for (fi in 0 until vertexSizeF) {
                    mergeDataF.put(dataF[i * vertexSizeF + fi])
                }
                for (ii in 0 until vertexSizeI) {
                    mergeDataI.put(dataI[i * vertexSizeI + ii])
                }
                j++
            }
        }
        logD { "Removed ${numVertices - j} vertices" }
        numVertices = j
        dataF = mergeDataF
        dataI = mergeDataI

        val mergeIndices = Uint32Buffer(indices.capacity)
        for (i in 0 until numIndices) {
            val ind = indices[i]
            mergeIndices.put(indexMap[mergeMap[ind]!!]!!)
        }
        indices = mergeIndices
    }

    fun splitVertices() {
        val splitDataF = Float32Buffer(numIndices * vertexSizeF)
        val splitDataI = Int32Buffer(numIndices * vertexSizeI)
        for (i in 0 until numIndices) {
            val ind = indices[i]
            for (fi in 0 until vertexSizeF) {
                splitDataF.put(dataF[ind * vertexSizeF + fi])
            }
            for (ii in 0 until vertexSizeI) {
                splitDataI.put(dataI[ind * vertexSizeI + ii])
            }
        }
        dataF = splitDataF
        dataI = splitDataI

        val n = numIndices
        indices.clear()
        for (i in 0 until n) {
            indices.put(i)
        }
        numVertices = numIndices
    }

    fun generateNormals() {
        if (!vertexAttributes.contains(Attribute.NORMALS)) {
            return
        }
        check(primitiveType == PrimitiveType.TRIANGLES) { "Normal generation is only supported for triangle meshes" }

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
            v1.index = indices[i + 1]
            v2.index = indices[i + 2]

            if (v0.index > numVertices || v1.index > numVertices || v2.index > numVertices) {
                logE { "index to large ${v0.index}, ${v1.index}, ${v2.index}, sz: $numVertices" }
            }

            v1.position.subtract(v0.position, e1).norm()
            v2.position.subtract(v0.position, e2).norm()
            val a = triArea(v0.position, v1.position, v2.position)

            e1.cross(e2, nrm).norm().mul(a)
            if (nrm == Vec3f.ZERO || nrm.x.isNaN() || nrm.y.isNaN() || nrm.z.isNaN()) {
                //logW { "generate normals: degenerated triangle, a = $a, e1 = $e1, e2 = $e2" }
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
        check(primitiveType == PrimitiveType.TRIANGLES) { "Normal generation is only supported for triangle meshes" }

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
                //logW { "generate tangents: degenerated triangle, e1 = $e1, e2 = $e2" }
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
                v0.normal.set(Vec3f.Y_AXIS)
            }

            if (v0.tangent.sqrLength() != 0f) {
                v0.tangent.norm()
                v0.tangent.w = tangentSign
            } else {
                v0.tangent.set(Vec3f.X_AXIS)
            }
        }
    }

    override fun release() {
        gpuGeometry?.release()
        gpuGeometry = null
        super.release()
    }

    companion object {
        private const val INITIAL_SIZE = 1000
        private const val GROW_FACTOR = 2.0
    }

    private class PointAndIndex(pos: Vec3f, val index: Int) : Vec3f(pos)
}

enum class PrimitiveType {
    LINES {
        override fun getNumberOfPrimitives(numIndices: Int): Int = numIndices / 2
    },
    POINTS {
        override fun getNumberOfPrimitives(numIndices: Int): Int = numIndices
    },
    TRIANGLES {
        override fun getNumberOfPrimitives(numIndices: Int): Int = numIndices / 3
    },
    TRIANGLE_STRIP {
        override fun getNumberOfPrimitives(numIndices: Int): Int = numIndices - 2
    };

    abstract fun getNumberOfPrimitives(numIndices: Int): Int
}

enum class Usage {
    DYNAMIC,
    STATIC
}