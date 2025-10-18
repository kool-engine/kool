package de.fabmax.kool.scene.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.triArea
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.asAttribute
import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.util.*
import kotlin.math.abs

@Suppress("DEPRECATION")
@Deprecated("Use a layout struct instead of specifying vertex attributes individually")
fun IndexedVertexList(
    vararg vertexAttributes: Attribute,
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    usage: Usage = Usage.STATIC
): IndexedVertexList<DynamicStruct> {
    return IndexedVertexList(vertexAttributes.toList(), primitiveType, usage)
}

@Deprecated("Use a layout struct instead of specifying vertex attributes individually")
fun IndexedVertexList(
    vertexAttributes: List<Attribute>,
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    usage: Usage = Usage.STATIC
): IndexedVertexList<DynamicStruct> {
    val layout = DynamicStruct("vertexLayout", MemoryLayout.TightlyPacked) {
        vertexAttributes.forEach { attr ->
            when (attr.type) {
                GpuType.Float1 -> float1(attr.name)
                GpuType.Float2 -> float2(attr.name)
                GpuType.Float3 -> float3(attr.name)
                GpuType.Float4 -> float4(attr.name)
                GpuType.Int1 -> int1(attr.name)
                GpuType.Int2 -> int2(attr.name)
                GpuType.Int3 -> int3(attr.name)
                GpuType.Int4 -> int4(attr.name)
                else -> error("Unsupported vertex attribute type: ${attr.type}")
            }
        }
    }
    return IndexedVertexList(
        layout,
        primitiveType = primitiveType,
        usage = usage
    )
}

class IndexedVertexList<Layout: Struct>(
    val layout: Layout,
    initialSize: Int = 1024,
    val primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    val usage: Usage = Usage.STATIC,
    val positionAttr: Float3Member<Layout>? = layout.getFloat3(VertexLayouts.Position.name),
    val normalAttr: Float3Member<Layout>? = layout.getFloat3(VertexLayouts.Normal.name),
    val tangentAttr: Float4Member<Layout>? = layout.getFloat4(VertexLayouts.Tangent.name),
    val colorAttr: Float4Member<Layout>? = layout.getFloat4(VertexLayouts.Color.name),
    val emissiveColorAttr: Float3Member<Layout>? = layout.getFloat3(VertexLayouts.EmissiveColor.name),
    val metallicAttr: Float1Member<Layout>? = layout.getFloat1(VertexLayouts.Metallic.name),
    val roughnessAttr: Float1Member<Layout>? = layout.getFloat1(VertexLayouts.Roughness.name),
    val texCoordAttr: Float2Member<Layout>? = layout.getFloat2(VertexLayouts.TexCoord.name),
    val joint: Int4Member<Layout>? = layout.getInt4(VertexLayouts.Joint.name),
    val weight: Float4Member<Layout>? = layout.getFloat4(VertexLayouts.Weight.name),
) : BaseReleasable() {

    var name: String = "geometry"

    var vertexData: StructBuffer<Layout> = StructBuffer(layout, capacity = initialSize)
        internal set

    var indices = Uint32Buffer(initialSize, true)
        internal set

    /**
     * Number of vertices.
     */
    var numVertices: Int
        get() = vertexData.limit
        set(value) {
            vertexData.limit = value
            incrementModCount()
        }

    val numIndices: Int get() = indices.position
    val numPrimitives: Int get() = primitiveType.getNumberOfPrimitives(numIndices)
    val lastIndex get() = numVertices - 1

    val vertexIt: VertexView<Layout> = VertexView(this, 0)

    val modCount = ModCounter()

    var gpuGeometry: GpuGeometry? = null

    fun incrementModCount() = modCount.increment()

    fun getMorphAttributes(): List<Attribute> {
        return layout.members
            .filter { a ->
                a.name.startsWith(Attribute.NORMALS.name + "_") ||
                a.name.startsWith(Attribute.POSITIONS.name + "_") ||
                a.name.startsWith(Attribute.TANGENTS.name + "_")
            }
            .map { it.asAttribute() }
    }

    fun isEmpty(): Boolean = numVertices == 0 || numIndices == 0

    private fun increaseIndicesSize(newSize: Int) {
        val newIdxs = Uint32Buffer(newSize, true)
        newIdxs.put(indices)
        indices = newIdxs
    }

    fun checkBufferSize(reqSpace: Int) {
        if (reqSpace <= 0 || layout.structSize == 0) return
        if (vertexData.remaining < reqSpace) {
            val newSize = increaseBufferSize(vertexData.capacity, reqSpace - vertexData.remaining, layout.structSize)
            val newData = StructBuffer(layout, capacity = newSize)
            newData.put(vertexData)
            vertexData = newData
        }
    }

    fun checkIndexSize(reqSpace: Int) {
        if (reqSpace <= 0) return
        if (indices.remaining < reqSpace) {
            val newSize = increaseBufferSize(indices.capacity, reqSpace, 4)
            increaseIndicesSize(newSize)
        }
    }

    fun hasAttribute(attribute: Attribute): Boolean = layout.getByName(attribute.name, attribute.type) != null

    fun hasAttributes(requiredAttributes: Set<Attribute>): Boolean {
        return requiredAttributes.all { hasAttribute(it) }
    }

    /**
     * Adds a new vertex to the vertex buffer and returns its index. The given block can be used to set the vertex
     * attributes using the corresponding vertex layout struct members.
     *
     * @see addVertexOld for the now-deprecated version using a [VertexView].
     */
    inline fun addVertex(block: MutableStructBufferView<Layout>.(Layout) -> Unit): Int {
        checkBufferSize(1)
        incrementModCount()
        return vertexData.put(block)
    }

    @Deprecated("Use addVertex instead")
    inline fun addVertexOld(block: VertexView<Layout>.() -> Unit): Int {
        checkBufferSize(1)
        val addIndex = numVertices++
        vertexIt.index = addIndex
        vertexIt.block()
        incrementModCount()
        return addIndex
    }

    fun addVertex(position: Vec3f, normal: Vec3f? = null, color: Color? = null, texCoord: Vec2f? = null): Int {
        return addVertex {
            positionAttr?.let { set(it, position) }
            if (normal != null) {
                normalAttr?.let { set(it, normal) }
            }
            if (color != null) {
                colorAttr?.let { set(it, color) }
            }
            if (texCoord!= null) {
                texCoordAttr?.let { set(it, texCoord) }
            }
        }
    }

    fun addGeometry(geometry: IndexedVertexList<*>) {
        if (layout == geometry.layout) {
            val baseIdx = numVertices
            checkBufferSize(geometry.numVertices)
            vertexData.put(geometry.vertexData)
            checkIndexSize(geometry.indices.position)
            for (i in 0 until geometry.indices.position) {
                addIndex(baseIdx + geometry.indices[i])
            }
        } else {
            @Suppress("DEPRECATION")
            addGeometry(geometry) { }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Use addGeometry instead")
    inline fun addGeometry(geometry: IndexedVertexList<*>, vertexMod: (VertexView<Layout>.() -> Unit)) {
        val baseIdx = numVertices
        checkBufferSize(geometry.numVertices)
        for (i in 0 until geometry.numVertices) {
            addVertexOld {
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
            checkIndexSize(1)
        }
        indices += idx
        modCount.increment()
    }

    fun addIndices(vararg indices: Int) {
        for (idx in indices.indices) {
            addIndex(indices[idx])
        }
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

    fun clear() {
        vertexData.clear()
        indices.clear()
        modCount.increment()
    }

    /**
     * Replaces all content by the content of [source]. The given source buffer must have the same vertex layout.
     * This vertex list's [modCount] is set to the [modCount] value of the source vertex list.
     */
    internal fun set(source: IndexedVertexList<*>) {
        require(source.layout == layout) { "Source vertex layout does not match this vertex layout" }
        clear()
        checkBufferSize(source.numVertices)
        checkIndexSize(source.indices.position)
        @Suppress("UNCHECKED_CAST")
        vertexData.put(source.vertexData as StructBuffer<Layout>)
        indices.put(source.indices)
        modCount.reset(source.modCount)
    }

    fun clearIndices() {
        indices.clear()
        modCount.increment()
    }

    fun shrinkIndices(newSize: Int) {
        if (newSize > indices.position) {
            return
        }
        indices.position = newSize
        modCount.increment()
    }

    fun shrinkVertices(newSize: Int) {
        if (newSize > numVertices) {
            return
        }
        numVertices = newSize
        modCount.increment()
    }

    operator fun get(i: Int): VertexView<Layout> {
        check(i in 0 ..< vertexData.capacity) { "Vertex index $i out of bounds: 0 ..< $numVertices" }
        return VertexView(this, i)
    }

    inline fun forEach(block: (VertexView<Layout>) -> Unit) {
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
            modCount.increment()
        }
    }

    override fun doRelease() {
        gpuGeometry?.releaseDelayed(1)
    }
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

/**
 * Expected vertex data usage.
 */
enum class Usage {
    /**
     * Vertex data will be updated often.
     */
    DYNAMIC,
    /**
     * Vertex data will be changed very infrequently / never.
     */
    STATIC
}