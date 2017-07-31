package de.fabmax.kool.scene

import de.fabmax.kool.gl.BufferResource
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.*

fun mesh(withNormals: Boolean, withColors: Boolean, withTexCoords: Boolean, name: String? = null,
         block: Mesh.() -> Unit): Mesh {
    val mesh = Mesh(MeshData(withNormals, withColors, withTexCoords), name)

    mesh.shader = basicShader {
        if (withNormals) {
            lightModel = LightModel.PHONG_LIGHTING
        } else {
            lightModel = LightModel.NO_LIGHTING
        }

        if (withTexCoords) {
            colorModel = ColorModel.TEXTURE_COLOR
        } else if (withColors) {
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
    open var primitiveType = GL.TRIANGLES

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

        meshData.checkBuffers(ctx)

        if (meshData.positionBinder == null) {
            throw IllegalStateException("Vertex positions attribute binder is null")
        }

        // bind shader for this mesh
        ctx.shaderMgr.bindShader(shader, ctx)

        // setup shader for mesh rendering, the active shader is not necessarily mMeshShader
        val boundShader = ctx.shaderMgr.boundShader
        if (boundShader != null) {
            // bind this mesh as input to the used shader
            boundShader.bindMesh(this, ctx)
            // draw mesh
            meshData.indexBuffer?.bind(ctx)
            GL.drawElements(primitiveType, meshData.indexSize, GL.UNSIGNED_INT, 0)
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
                    .scale(Math.sqrt(distSqr.toDouble()).toFloat())
                    .add(test.ray.origin)
        }
    }
}

class MeshData(val hasNormals: Boolean, val hasColors: Boolean, val hasTexCoords: Boolean) {
    val data = IndexedVertexList(hasNormals, hasColors, hasTexCoords)
    val bounds = BoundingBox()

    var generator: (MeshBuilder.() -> Unit)? = null

    private var referenceCount = 0

    var usage = GL.STATIC_DRAW

    var dataBuffer: BufferResource? = null
        private set
    var indexBuffer: BufferResource? = null
        private set
    var indexSize = 0
        private set

    var isSyncRequired = false
    var isBatchUpdate = false
        set(value) {
            synchronized(data) {
                field = value
            }
        }

    var positionBinder: VboBinder? = null
        private set
    var normalBinder: VboBinder? = null
        private set
    var texCoordBinder: VboBinder? = null
        private set
    var colorBinder: VboBinder? = null
        private set

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

    fun addVertex(block: IndexedVertexList.Item.() -> Unit): Int {
        var idx = 0
        synchronized(data) {
            isSyncRequired = true
            idx = data.addVertex(bounds, block)
        }
        // return must be outside of synchronized block for successful javascript transpiling
        return idx
    }

    fun addVertex(position: Vec3f, normal: Vec3f? = null, color: Color? = null, texCoord: Vec2f? = null): Int {
        var idx = 0
        synchronized(data) {
            isSyncRequired = true
            idx = data.addVertex(position, normal, color, texCoord)
            bounds.add(position)
        }
        // return must be outside of synchronized block for successful javascript transpiling
        return idx
    }

    fun addIndex(idx: Int) {
        synchronized(data) {
            data.addIndex(idx)
            isSyncRequired = true
        }
    }

    fun addTriIndices(i0: Int, i1: Int, i2: Int) {
        synchronized(data) {
            data.addIndex(i0)
            data.addIndex(i1)
            data.addIndex(i2)
            isSyncRequired = true
        }
    }

    fun addIndices(vararg indices: Int) {
        synchronized(data) {
            data.addIndices(indices)
            isSyncRequired = true
        }
    }

    fun clear() {
        synchronized(data) {
            data.clear()
            bounds.clear()
            isSyncRequired = true
        }
    }

    fun incrementReferenceCount() {
        referenceCount++
    }

    /**
     * Deletes all index and data buffer of this mesh.
     */
    fun dispose(ctx: RenderContext) {
        if (--referenceCount == 0) {
            indexBuffer?.delete(ctx)
            dataBuffer?.delete(ctx)
            indexBuffer = null
            dataBuffer = null
        }
    }

    fun checkBuffers(ctx: RenderContext) {
        if (indexBuffer == null) {
            indexBuffer = BufferResource.create(GL.ELEMENT_ARRAY_BUFFER, ctx)
        }
        if (dataBuffer == null) {
            dataBuffer = BufferResource.create(GL.ARRAY_BUFFER, ctx)
            positionBinder = VboBinder(dataBuffer!!, 3, data.strideBytes)
            if (hasNormals) { normalBinder = VboBinder(dataBuffer!!, 3, data.strideBytes, data.normalOffset) }
            if (hasColors) { colorBinder = VboBinder(dataBuffer!!, 4, data.strideBytes, data.colorOffset) }
            if (hasTexCoords) { texCoordBinder = VboBinder(dataBuffer!!, 2, data.strideBytes, data.texCoordOffset) }
        }

        if (isSyncRequired && !isBatchUpdate) {
            synchronized(data) {
                if (!isBatchUpdate) {
                    indexSize = data.indices.position

                    if (!Platform.supportsUint32Indices) {
                        // convert index buffer to uint16
                        val uint16Buffer = Platform.createUint16Buffer(indexSize)
                        for (i in 0..(data.indices.position - 1)) {
                            uint16Buffer.put(data.indices[i].toShort())
                        }
                        indexBuffer?.setData(uint16Buffer, usage, ctx)
                    } else {
                        indexBuffer?.setData(data.indices, usage, ctx)
                    }
                    dataBuffer?.setData(data.data, usage, ctx)
                    isSyncRequired = false
                }
            }
        }
    }
}
