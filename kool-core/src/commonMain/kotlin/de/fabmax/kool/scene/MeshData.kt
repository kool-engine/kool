package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.gl.BufferResource
import de.fabmax.kool.gl.GL_STATIC_DRAW
import de.fabmax.kool.gl.GL_TRIANGLES
import de.fabmax.kool.gl.GL_UNSIGNED_INT
import de.fabmax.kool.lock
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.shading.VboBinder
import de.fabmax.kool.util.*

class MeshData(val vertexAttributes: Set<Attribute>) : Disposable {
    val vertexList = IndexedVertexList(vertexAttributes)
    val bounds = BoundingBox()

    var generator: (MeshBuilder.() -> Unit)? = null

    private var referenceCount = 0

    var usage = GL_STATIC_DRAW
    var indexType = GL_UNSIGNED_INT
    var primitiveType = GL_TRIANGLES

    var dataBufferF: BufferResource? = null
    var dataBufferI: BufferResource? = null
    var indexBuffer: BufferResource? = null

    val numIndices: Int
        get() = vertexList.indices.position
    val numVertices: Int
        get() = vertexList.size

    var isRebuildBoundsOnSync = false
    var isSyncRequired = false
    var isBatchUpdate = false
        set(value) {
            lock(vertexList) {
                field = value
            }
        }
    private val vertexIt = vertexList[0]

    val attributeBinders = mutableMapOf<Attribute, VboBinder>()

    constructor(vararg vertexAttributes: Attribute) : this(vertexAttributes.toHashSet())

    fun hasAttribute(attribute: Attribute): Boolean = vertexAttributes.contains(attribute)

    fun generateGeometry() {
        val gen = generator
        if (gen != null) {
            batchUpdate {
                clear()
                MeshBuilder(this).gen()
            }
        }
    }

    fun generateNormals() {
        if (!vertexAttributes.contains(Attribute.NORMALS)) {
            return
        }
        if (primitiveType != GL_TRIANGLES) {
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
            v0.index = vertexList.indices[i]
            v1.index = vertexList.indices[i+1]
            v2.index = vertexList.indices[i+2]

            if (v0.index > vertexList.size || v1.index > vertexList.size || v2.index > vertexList.size) {
                logE { "index to large ${v0.index}, ${v1.index}, ${v2.index}, sz: ${vertexList.size}" }
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

    fun generateTangents() {
        if (!vertexAttributes.contains(Attribute.TANGENTS)) {
            return
        }
        if (primitiveType != GL_TRIANGLES) {
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
            v0.index = vertexList.indices[i]
            v1.index = vertexList.indices[i+1]
            v2.index = vertexList.indices[i+2]

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

                v0.tangent += tan
                v1.tangent += tan
                v2.tangent += tan
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
            } else {
                logW { "singular tangent" }
                v0.normal.set(Vec3f.X_AXIS)
            }
        }
    }

    fun joinCloseVertices(eps: Float = FUZZY_EQ_F) {
        batchUpdate {
            val verts = mutableListOf<IndexedVertexList.Vertex>()
            for (i in 0 until numVertices) {
                verts += get(i)
            }
            val tree = pointKdTree(verts)
            val trav = InRadiusTraverser<IndexedVertexList.Vertex>()
            val removeVerts = mutableListOf<IndexedVertexList.Vertex>()
            val replaceIndcs = mutableMapOf<Int, Int>()
            var requiresRebuildNormals = false

            for (v in verts) {
                if (v !in removeVerts) {
                    trav.setup(v, eps).traverse(tree)
                    trav.result.removeAll { it.index == v.index || it.index in replaceIndcs.keys }

                    if (trav.result.isNotEmpty()) {
                        for (j in trav.result) {
                            v.position += j.position
                            v.normal += j.normal

                            removeVerts += j
                            replaceIndcs[j.index] = v.index
                        }
                        v.position.scale(1f / (1f + trav.result.size))

                        if (hasAttribute(Attribute.NORMALS)) {
                            v.normal.scale(1f / (1f + trav.result.size))
                            requiresRebuildNormals = requiresRebuildNormals || v.normal.length().isFuzzyZero()
                            if (!requiresRebuildNormals) {
                                v.normal.norm()
                            }
                        }
                    }
                }
            }

            logD { "Found ${removeVerts.size} duplicate positions (of $numVertices vertices)" }

            for (r in removeVerts.sortedBy { -it.index }) {
                // remove int attributes of deleted vertex
                for (i in r.index * vertexList.vertexSizeI until vertexList.dataI.position - vertexList.vertexSizeI) {
                    vertexList.dataI[i] = vertexList.dataI[i + vertexList.vertexSizeI]
                }
                vertexList.dataI.position -= vertexList.vertexSizeI
                vertexList.dataI.limit -= vertexList.vertexSizeI

                // remove float attributes of deleted vertex
                for (i in r.index * vertexList.vertexSizeF until vertexList.dataF.position - vertexList.vertexSizeF) {
                    vertexList.dataF[i] = vertexList.dataF[i + vertexList.vertexSizeF]
                }
                vertexList.dataF.position -= vertexList.vertexSizeF
                vertexList.dataF.limit -= vertexList.vertexSizeF

                for (i in 0 until vertexList.indices.position) {
                    if (vertexList.indices[i] == r.index) {
                        // this index was replaced by a different one
                        vertexList.indices[i] = replaceIndcs[r.index]!!
                    } else if (vertexList.indices[i] > r.index) {
                        vertexList.indices[i]--
                    }
                }
            }
            vertexList.size -= removeVerts.size

            if (requiresRebuildNormals) {
                logD { "Normal reconstruction required" }
                generateNormals()
            }

            logD { "Removed ${removeVerts.size} duplicate vertices" }
        }
    }

    inline fun batchUpdate(rebuildBounds: Boolean = false, block: MeshData.() -> Unit) {
        lock(vertexList) {
            val wasBatchUpdate = isBatchUpdate
            isBatchUpdate = true
            block()
            isSyncRequired = true
            isBatchUpdate = wasBatchUpdate
        }
        if (rebuildBounds) {
            rebuildBounds()
        }
    }

    inline fun addVertex(block: IndexedVertexList.Vertex.() -> Unit): Int {
        return lock(vertexList) {
            isSyncRequired = true
            vertexList.addVertex(bounds, block)
        }
    }

    fun addVertex(position: Vec3f, normal: Vec3f? = null, color: Color? = null, texCoord: Vec2f? = null): Int {
        return lock(vertexList) {
            isSyncRequired = true
            bounds.add(position)
            vertexList.addVertex(position, normal, color, texCoord)
        }
    }

    fun addMeshData(other: MeshData) {
        lock(vertexList) {
            vertexList.addFrom(other.vertexList)
            bounds.add(other.bounds)
            isSyncRequired = true
        }
    }

    fun addIndex(idx: Int) {
        lock(vertexList) {
            vertexList.addIndex(idx)
            isSyncRequired = true
        }
    }

    fun addTriIndices(i0: Int, i1: Int, i2: Int) {
        lock(vertexList) {
            vertexList.addIndex(i0)
            vertexList.addIndex(i1)
            vertexList.addIndex(i2)
            isSyncRequired = true
        }
    }

    fun addIndices(vararg indices: Int) {
        lock(vertexList) {
            vertexList.addIndices(indices)
            isSyncRequired = true
        }
    }

    fun addIndices(indices: List<Int>) {
        lock(vertexList) {
            vertexList.addIndices(indices)
            isSyncRequired = true
        }
    }

    fun clear() {
        lock(vertexList) {
            vertexList.clear()
            bounds.clear()
            isSyncRequired = true
        }
    }

    /**
     * Rebuilds the bounding box for this mesh data. Rebuilding requires to iterate over all vertices, which can be
     * very slow for large meshes. However, rebuilding mesh bounds is only required if positions of existing vertices
     * were changed, or vertices were removed.
     * If [isRebuildBoundsOnSync] is true, this function is called automatically whenever mesh data buffers are
     * synchronized.
     */
    fun rebuildBounds() {
        lock(vertexList) {
            bounds.clear()
            for (i in 0 until numVertices) {
                vertexIt.index = i
                bounds.add(vertexIt.position)
            }
        }
    }

    operator fun get(i: Int): IndexedVertexList.Vertex = vertexList[i]

    fun incrementReferenceCount() {
        referenceCount++
    }

    /**
     * Deletes all index and data buffer of this mesh.
     */
    override fun dispose(ctx: KoolContext) {
        if (--referenceCount == 0) {
            indexBuffer?.delete(ctx)
            dataBufferF?.delete(ctx)
            dataBufferI?.delete(ctx)
            indexBuffer = null
            dataBufferF = null
            dataBufferI = null
            attributeBinders.clear()
        }
    }

//    fun checkBuffers(ctx: KoolContext): Boolean {
//        if (indexBuffer == null) {
//            indexBuffer = BufferResource.create(GL_ELEMENT_ARRAY_BUFFER, ctx)
//        }
//        var hasIntData = false
//        if (dataBufferF == null) {
//            dataBufferF = BufferResource.create(GL_ARRAY_BUFFER, ctx)
//            for (vertexAttrib in vertexAttributes) {
//                if (vertexAttrib.type.isInt) {
//                    hasIntData = true
//                } else {
//                    attributeBinders[vertexAttrib] = VboBinder(dataBufferF!!, vertexAttrib.type.size,
//                            vertexList.strideBytesF, vertexList.attributeOffsets[vertexAttrib]!!, vertexAttrib.type.glType)
//                }
//            }
//        }
//        if (hasIntData && dataBufferI == null) {
//            dataBufferI = BufferResource.create(GL_ARRAY_BUFFER, ctx)
//            for (vertexAttrib in vertexAttributes) {
//                if (vertexAttrib.type.isInt) {
//                    attributeBinders[vertexAttrib] = VboBinder(dataBufferI!!, vertexAttrib.type.size,
//                            vertexList.strideBytesI, vertexList.attributeOffsets[vertexAttrib]!!, vertexAttrib.type.glType)
//                }
//            }
//        }
//
//        return if (isSyncRequired && !isBatchUpdate) {
//            lock(vertexList) {
//                if (!isBatchUpdate) {
//                    if (isRebuildBoundsOnSync) {
//                        rebuildBounds()
//                    }
//                    if (!ctx.glCapabilities.uint32Indices) {
//                        // convert index buffer to uint16
//                        val uint16Buffer = createUint16Buffer(numIndices)
//                        for (i in 0..(vertexList.indices.position - 1)) {
//                            uint16Buffer.put(vertexList.indices[i].toShort())
//                        }
//                        indexType = GL_UNSIGNED_SHORT
//                        indexBuffer?.setData(uint16Buffer, usage, ctx)
//                    } else {
//                        indexType = GL_UNSIGNED_INT
//                        indexBuffer?.setData(vertexList.indices, usage, ctx)
//                    }
//                    dataBufferF?.setData(vertexList.dataF, usage, ctx)
//                    dataBufferI?.setData(vertexList.dataI, usage, ctx)
//                    isSyncRequired = false
//                }
//            }
//            true
//        } else {
//            false
//        }
//    }
}