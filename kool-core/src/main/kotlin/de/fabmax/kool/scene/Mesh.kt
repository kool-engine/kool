package de.fabmax.kool.scene

import de.fabmax.kool.RenderContext
import de.fabmax.kool.gl.*
import de.fabmax.kool.shading.*
import de.fabmax.kool.supportsUint32Indices
import de.fabmax.kool.util.*
import kotlin.math.sqrt


inline fun mesh(withNormals: Boolean, withColors: Boolean, withTexCoords: Boolean, name: String? = null,
         block: Mesh.() -> Unit): Mesh {
    val attributes = mutableSetOf(Attribute.POSITIONS)
    if (withNormals) {
        attributes += Attribute.NORMALS
    }
    if (withColors) {
        attributes += Attribute.COLORS
    }
    if (withTexCoords) {
        attributes += Attribute.TEXTURE_COORDS
    }
    return mesh(name, attributes, block)
}

inline fun mesh(name: String? = null, vararg attributes: Attribute, block: Mesh.() -> Unit): Mesh {
    return mesh(name, attributes.toHashSet(), block)
}

inline fun mesh(name: String? = null, attributes: Set<Attribute>, block: Mesh.() -> Unit): Mesh {
    val mesh = Mesh(MeshData(attributes), name)

    mesh.shader = basicShader {
        if (attributes.contains(Attribute.NORMALS)) {
            lightModel = LightModel.PHONG_LIGHTING
        } else {
            lightModel = LightModel.NO_LIGHTING
        }

        if (attributes.contains(Attribute.TEXTURE_COORDS)) {
            colorModel = ColorModel.TEXTURE_COLOR
        } else if (attributes.contains(Attribute.COLORS)) {
            colorModel = ColorModel.VERTEX_COLOR
        } else {
            colorModel = ColorModel.STATIC_COLOR
        }
    }

    mesh.block()

    // todo: Optionally generate geometry lazily
    mesh.generateGeometry()

    return mesh

}

fun colorMesh(name: String? = null, generate: Mesh.() -> Unit): Mesh {
    return mesh(true, true, false, name, generate)
}

fun textMesh(font: Font, name: String? = null, generate: Mesh.() -> Unit): Mesh {
    val text = mesh(true, true, true, name, generate)
    text.shader = fontShader(font) { lightModel = LightModel.NO_LIGHTING }
    return text
}

fun textureMesh(name: String? = null, generate: Mesh.() -> Unit): Mesh {
    return mesh(true, false, true, name, generate)
}

/**
 * Abstract base class for renderable geometry (triangles, lines, points, etc.).
 *
 * @author fabmax
 */
open class Mesh(var meshData: MeshData, name: String? = null) : Node(name) {

    open var generator: (MeshBuilder.() -> Unit)?
        get() = meshData.generator
        set(value) { meshData.generator = value }

    open var shader: Shader? = null
    open var primitiveType = GL_TRIANGLES

    override val bounds: BoundingBox
        get() = meshData.bounds

    init {
        meshData.incrementReferenceCount()
    }

    open fun generateGeometry() {
        meshData.generateGeometry()
    }

    /**
     * Deletes all buffers associated with this mesh.
     */
    override fun dispose(ctx: RenderContext) {
        meshData.dispose(ctx)
        shader?.dispose(ctx)
    }

    override fun render(ctx: RenderContext) {
        super.render(ctx)
        if (!isRendered) {
            // mesh is not visible (either hidden or outside frustum)
            return
        }

        // create or update data buffers for this mesh
        meshData.checkBuffers(ctx)

        // bind shader for this mesh
        ctx.shaderMgr.bindShader(shader, ctx)

        // setup shader for mesh rendering, the active shader is not necessarily mMeshShader
        val boundShader = ctx.shaderMgr.boundShader
        if (boundShader != null) {
            // bind this mesh as input to the used shader
            boundShader.bindMesh(this, ctx)
            // draw mesh
            meshData.indexBuffer?.bind(ctx)
            glDrawElements(primitiveType, meshData.numIndices, GL_UNSIGNED_INT, 0)
            boundShader.unbindMesh(ctx)
        }
    }

    override fun rayTest(test: RayTest) {
        // todo: for now only bounds are tested, optional test on actual geometry would be nice
        val distSqr = bounds.hitDistanceSqr(test.ray)
        if (distSqr < Float.POSITIVE_INFINITY && distSqr <= test.hitDistanceSqr) {
            test.hitDistanceSqr = distSqr
            test.hitNode = this
            test.hitPositionLocal.set(test.ray.direction)
                    .scale(sqrt(distSqr.toDouble()).toFloat())
                    .add(test.ray.origin)
        }
    }
}

class MeshData(val vertexAttributes: Set<Attribute>) {
    val vertexList = IndexedVertexList(vertexAttributes)
    val bounds = BoundingBox()

    var generator: (MeshBuilder.() -> Unit)? = null

    private var referenceCount = 0

    var usage = GL_STATIC_DRAW

    var dataBufferF: BufferResource? = null
    var dataBufferI: BufferResource? = null
    var indexBuffer: BufferResource? = null

    val numIndices: Int
        get() = vertexList.indices.position
    val numVertices: Int
        get() = vertexList.size

    var isSyncRequired = false
    var isBatchUpdate = false
        set(value) {
            synchronized(vertexList) {
                field = value
            }
        }

    val attributeBinders = mutableMapOf<Attribute, VboBinder>()

    constructor(vararg vertexAttributes: Attribute) : this(vertexAttributes.toHashSet())

    fun hasAttribute(attribute: Attribute): Boolean = vertexAttributes.contains(attribute)

    fun generateGeometry() {
        val gen = generator
        if (gen != null) {
            isBatchUpdate = true
            clear()
            val builder = MeshBuilder(this)
            builder.gen()
            isBatchUpdate = false
        }
    }

    fun addVertex(block: IndexedVertexList.Vertex.() -> Unit): Int {
        var idx = 0
        synchronized(vertexList) {
            isSyncRequired = true
            idx = vertexList.addVertex(bounds, block)
        }
        // return must be outside of synchronized block for successful javascript transpiling
        return idx
    }

    fun addVertex(position: Vec3f, normal: Vec3f? = null, color: Color? = null, texCoord: Vec2f? = null): Int {
        var idx = 0
        synchronized(vertexList) {
            isSyncRequired = true
            idx = vertexList.addVertex(position, normal, color, texCoord)
            bounds.add(position)
        }
        // return must be outside of synchronized block for successful javascript transpiling
        return idx
    }

    fun addIndex(idx: Int) {
        synchronized(vertexList) {
            vertexList.addIndex(idx)
            isSyncRequired = true
        }
    }

    fun addTriIndices(i0: Int, i1: Int, i2: Int) {
        synchronized(vertexList) {
            vertexList.addIndex(i0)
            vertexList.addIndex(i1)
            vertexList.addIndex(i2)
            isSyncRequired = true
        }
    }

    fun addIndices(vararg indices: Int) {
        synchronized(vertexList) {
            vertexList.addIndices(indices)
            isSyncRequired = true
        }
    }

    fun clear() {
        synchronized(vertexList) {
            vertexList.clear()
            bounds.clear()
            isSyncRequired = true
        }
    }

    operator fun get(i: Int): IndexedVertexList.Vertex = vertexList[i]

    fun incrementReferenceCount() {
        referenceCount++
    }

    /**
     * Deletes all index and data buffer of this mesh.
     */
    fun dispose(ctx: RenderContext) {
        if (--referenceCount == 0) {
            indexBuffer?.delete(ctx)
            dataBufferF?.delete(ctx)
            dataBufferI?.delete(ctx)
            indexBuffer = null
            dataBufferF = null
            dataBufferI = null
        }
    }

    fun checkBuffers(ctx: RenderContext) {
        if (indexBuffer == null) {
            indexBuffer = BufferResource.create(GL_ELEMENT_ARRAY_BUFFER, ctx)
        }
        var hasIntData = false
        if (dataBufferF == null) {
            dataBufferF = BufferResource.create(GL_ARRAY_BUFFER, ctx)
            for (vertexAttrib in vertexAttributes) {
                if (vertexAttrib.type.isInt) {
                    hasIntData = true
                } else {
                    attributeBinders[vertexAttrib] = VboBinder(dataBufferF!!, vertexAttrib.type.size,
                            vertexList.strideBytesF, vertexList.attributeOffsets[vertexAttrib]!!, vertexAttrib.type.glType)
                }
            }
        }
        if (hasIntData && dataBufferI == null) {
            dataBufferI = BufferResource.create(GL_ARRAY_BUFFER, ctx)
            for (vertexAttrib in vertexAttributes) {
                if (vertexAttrib.type.isInt) {
                    attributeBinders[vertexAttrib] = VboBinder(dataBufferI!!, vertexAttrib.type.size,
                            vertexList.strideBytesI, vertexList.attributeOffsets[vertexAttrib]!!, vertexAttrib.type.glType)
                }
            }
        }

        if (isSyncRequired && !isBatchUpdate) {
            synchronized(vertexList) {
                if (!isBatchUpdate) {
                    if (!supportsUint32Indices) {
                        // convert index buffer to uint16
                        val uint16Buffer = createUint16Buffer(numIndices)
                        for (i in 0..(vertexList.indices.position - 1)) {
                            uint16Buffer.put(vertexList.indices[i].toShort())
                        }
                        indexBuffer?.setData(uint16Buffer, usage, ctx)
                    } else {
                        indexBuffer?.setData(vertexList.indices, usage, ctx)
                    }
                    dataBufferF?.setData(vertexList.dataF, usage, ctx)
                    dataBufferI?.setData(vertexList.dataI, usage, ctx)
                    isSyncRequired = false
                }
            }
        }
    }
}
