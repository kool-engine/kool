package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.RenderPass
import de.fabmax.kool.gl.*
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.shading.*
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
    return mesh(attributes, name, block)
}

inline fun mesh(vararg attributes: Attribute, name: String? = null, block: Mesh.() -> Unit): Mesh {
    return mesh(attributes.toHashSet(), name, block)
}

inline fun mesh(attributes: Set<Attribute>, name: String? = null, block: Mesh.() -> Unit): Mesh {
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

fun textureMesh(name: String? = null, isNormalMapped: Boolean = false, generate: Mesh.() -> Unit): Mesh {
    val attributes = mutableSetOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
    if (isNormalMapped) {
        attributes += Attribute.TANGENTS
    }
    val mesh = mesh(attributes, name, generate)
    if (isNormalMapped) {
        mesh.meshData.generateTangents()
    }
    return mesh
}

enum class CullMethod {
    DEFAULT,
    CULL_BACK_FACES,
    CULL_FRONT_FACES,
    NO_CULLING
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

    var shader: Shader? = null
    var primitiveType = GL_TRIANGLES
    var cullMethod = CullMethod.DEFAULT

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
    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        meshData.dispose(ctx)
        shader?.dispose(ctx)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        if (!isRendered || (!ctx.isDepthTest && ctx.renderPass == RenderPass.SHADOW)) {
            // mesh is not visible (either hidden or outside frustum)
            return
        }

        // create or update data buffers for this mesh
        meshData.checkBuffers(ctx)

        // bind shader for this mesh
        ctx.shaderMgr.bindShader(shader, ctx)

        // setup shader for mesh rendering, the active shader is not necessarily mMeshShader
        ctx.shaderMgr.boundShader?.also { boundShader ->
            // bind this mesh as input to the used shader
            boundShader.bindMesh(this, ctx)

            if (cullMethod != CullMethod.DEFAULT) {
                ctx.pushAttributes()
                when (cullMethod) {
                    CullMethod.CULL_BACK_FACES -> {
                        ctx.isCullFace = true
                        ctx.cullFace = GL_BACK
                    }
                    CullMethod.CULL_FRONT_FACES -> {
                        ctx.isCullFace = true
                        ctx.cullFace = GL_FRONT
                    }
                    else -> ctx.isCullFace = false
                }
                ctx.applyAttributes()
            }

            // draw mesh
            meshData.indexBuffer?.bind(ctx)
            glDrawElements(primitiveType, meshData.numIndices, meshData.indexType, 0)
            boundShader.unbindMesh(ctx)

            if (cullMethod != CullMethod.DEFAULT) {
                ctx.popAttributes()
            }
        }
    }

    override fun rayTest(test: RayTest) {
        // todo: for now only bounds are tested, optional test on actual geometry would be nice
        val distSqr = bounds.hitDistanceSqr(test.ray)
        if (distSqr < Float.MAX_VALUE && distSqr <= test.hitDistanceSqr) {
            test.hitDistanceSqr = distSqr
            test.hitNode = this
            test.hitPositionLocal.set(test.ray.direction)
                    .scale(sqrt(distSqr.toDouble()).toFloat())
                    .add(test.ray.origin)
        }
    }
}

class MeshData(val vertexAttributes: Set<Attribute>) : Disposable {
    val vertexList = IndexedVertexList(vertexAttributes)
    val bounds = BoundingBox()

    var generator: (MeshBuilder.() -> Unit)? = null

    private var referenceCount = 0

    var usage = GL_STATIC_DRAW
    var indexType = GL_UNSIGNED_INT

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
            synchronized(vertexList) {
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

    fun generateTangents() {
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

            tan.x = f * (dv2 * e1.x - dv1 * e2.x)
            tan.y = f * (dv2 * e1.y - dv1 * e2.y)
            tan.z = f * (dv2 * e1.z - dv1 * e2.z)

            v0.tangent += tan
            v1.tangent += tan
            v2.tangent += tan
        }

        for (i in 0 until numVertices) {
            v0.index = i
            v0.tangent.norm()
        }
    }

    inline fun batchUpdate(block: MeshData.() -> Unit) {
        synchronized(vertexList) {
            val wasBatchUpdate = isBatchUpdate
            isBatchUpdate = true
            block()
            isSyncRequired = true
            isBatchUpdate = wasBatchUpdate
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

    fun addIndices(indices: List<Int>) {
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

    /**
     * Rebuilds the bounding box for this mesh data. Rebuilding requires to iterate over all vertices, which can be
     * very slow for large meshes. However, rebuilding mesh bounds is only required if positions of existing vertices
     * were changed, or vertices were removed.
     * If [isRebuildBoundsOnSync] is true, this function is called automatically whenever mesh data buffers are
     * synchronized.
     */
    fun rebuildBounds() {
        synchronized(vertexList) {
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
        }
    }

    fun checkBuffers(ctx: KoolContext) {
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
                    if (isRebuildBoundsOnSync) {
                        rebuildBounds()
                    }
                    if (!ctx.glCapabilities.uint32Indices) {
                        // convert index buffer to uint16
                        val uint16Buffer = createUint16Buffer(numIndices)
                        for (i in 0..(vertexList.indices.position - 1)) {
                            uint16Buffer.put(vertexList.indices[i].toShort())
                        }
                        indexType = GL_UNSIGNED_SHORT
                        indexBuffer?.setData(uint16Buffer, usage, ctx)
                    } else {
                        indexType = GL_UNSIGNED_INT
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
