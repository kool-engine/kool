package de.fabmax.kool.scene

import de.fabmax.kool.BufferResource
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.shading.Shader
import de.fabmax.kool.shading.VboBinder
import de.fabmax.kool.util.*

/**
 * Abstract base class for renderable geometry (triangles, lines, points, etc.).
 *
 * @author fabmax
 */
open class Mesh(val hasNormals: Boolean, val hasColors: Boolean, val hasTexCoords: Boolean) : Node() {

    protected val data = IndexedVertexList(hasNormals, hasColors, hasTexCoords)

    protected var dataBuf: BufferResource? = null
    protected var idxBuf: BufferResource? = null
    protected var idxCount = 0
    protected var syncBuffers = false
    var batchUpdate = false
        set(value) {
            synchronized(data) {
                field = value
            }
        }

    var positionBinder: VboBinder? = null
    var normalBinder: VboBinder? = null
    var texCoordBinder: VboBinder? = null
    var colorBinder: VboBinder? = null

    var shader: Shader? = null

    fun addVertex(init: IndexedVertexList.Item.() -> Unit): Int {
        var idx = 0
        synchronized(data) {
            syncBuffers = true
            idx = data.addVertex(init)
        }
        // return must be outside of synchronized block for successful javascript transpiling
        return idx
    }

    fun addVertex(position: Vec3f, normal: Vec3f? = null, color: Color? = null, texCoord: Vec2f? = null): Int {
        var idx = 0
        synchronized(data) {
            syncBuffers = true
            idx = data.addVertex(position, normal, color, texCoord)
        }
        // return must be outside of synchronized block for successful javascript transpiling
        return idx
    }

    fun addIndex(idx: Int) {
        synchronized(data) {
            data.addIndex(idx)
            syncBuffers = true
        }
    }

    fun addTriIndices(i0: Int, i1: Int, i2: Int) {
        synchronized(data) {
            data.addIndex(i0)
            data.addIndex(i1)
            data.addIndex(i2)
            syncBuffers = true
        }
    }

    fun addIndices(vararg indices: Int) {
        synchronized(data) {
            data.addIndices(indices)
            syncBuffers = true
        }
    }

    fun clear() {
        synchronized(data) {
            data.clear()
            syncBuffers = true
        }
    }

    /**
     * Deletes all buffers associated with this mesh.
     */
    override fun delete(ctx: RenderContext) {
        idxBuf?.delete(ctx)
        dataBuf?.delete(ctx)
    }

    override fun render(ctx: RenderContext) {
        checkBuffers(ctx)

        if (positionBinder == null) {
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
            drawElements(ctx)
            boundShader.unbindMesh(ctx)
        }
    }

    protected fun drawElements(ctx: RenderContext) {
        idxBuf?.bind(ctx)
        GL.drawElements(GL.TRIANGLES, idxCount, GL.UNSIGNED_INT, 0)
    }

    private fun checkBuffers(ctx: RenderContext) {
        if (idxBuf == null) {
            idxBuf = BufferResource.create(GL.ELEMENT_ARRAY_BUFFER, ctx)
        }
        if (dataBuf == null) {
            dataBuf = BufferResource.create(GL.ARRAY_BUFFER, ctx)
            positionBinder = VboBinder(dataBuf!!, 3, data.strideBytes)
            if (hasNormals) { normalBinder = VboBinder(dataBuf!!, 3, data.strideBytes, data.normalOffset) }
            if (hasColors) { colorBinder = VboBinder(dataBuf!!, 4, data.strideBytes, data.colorOffset) }
            if (hasTexCoords) { texCoordBinder = VboBinder(dataBuf!!, 2, data.strideBytes, data.texCoordOffset) }
        }

        if (syncBuffers && !batchUpdate) {
            synchronized(data) {
                if (!batchUpdate) {
                    idxCount = data.indices.position
                    idxBuf?.setData(data.indices, GL.STATIC_DRAW, ctx)
                    dataBuf?.setData(data.data, GL.STATIC_DRAW, ctx)
                    syncBuffers = false
                }
            }
        }
    }
}
