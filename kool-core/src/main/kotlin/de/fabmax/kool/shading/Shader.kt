package de.fabmax.kool.shading

import de.fabmax.kool.GlObject
import de.fabmax.kool.ProgramResource
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh

/**
 * Base class for custom shader implementations.
 *
 * @author fabmax
 */
abstract class Shader(source: Source) : GlObject<ProgramResource>() {

    var source = source
        protected set

    data class Source(val vertexSrc: String, val fragmentSrc: String)

    enum class Attribute {
        POSITIONS,
        NORMALS,
        TEXTURE_COORDS,
        COLORS
    }

    // Shader attribute pointers
    protected var attributePositions = -1
    protected var attributeNormals = -1
    protected var attributeTexCoords = -1
    protected var attributeColors = -1

    /**
     * Checks if this Shader is currently bound.
     *
     * @return true if this Shader is bound, false otherwise.
     */
    fun isBound(ctx: RenderContext): Boolean {
        return ctx.shaderMgr.boundShader === this
    }

    /**
     * Loads / compiles the shader program. Is called automatically when the shader is bound for
     * the first time and it is not already loaded.
     *
     * @param ctx    the graphics engine context
     */
    open fun onLoad(ctx: RenderContext) {
        res = ctx.shaderMgr.createShader(source, ctx)
    }

    /**
     * Is called when the shader is bound. Implementations should update all their shader uniforms
     * here.
     *
     * @param ctx    the graphics engine context
     */
    abstract fun onBind(ctx: RenderContext)

    /**
     * Is called on the currently bound shader when the MVP matrix has changed. Implementations should update their
     * transform matrix uniforms here.
     *
     * @param ctx    the graphics engine context
     */
    abstract fun onMatrixUpdate(ctx: RenderContext)

    /**
     * Looks for the specified attribute and returns its location or -1 if the attribute was not found.
     *
     * @param attribName    The attribute name to look for
     * @return the attribute location or -1 if the attribute was not found.
     */
    open fun findAttributeLocation(attribName: String, ctx: RenderContext): Int {
        val ref: ProgramResource? = res
        if (ref != null) {
            return GL.getAttribLocation(ref, attribName)
        } else {
            return -1
        }
    }

    /**
     * Enables the specified attribute for this shader. This method is called by concrete Shader
     * implementations to set the vertex attributes used by the implementation. If no attribute with
     * the specified name is found, the attribute is disabled.
     *
     * @param attribute     the attribute to enable
     * @param attribName    name of the attribute in shader code
     * @return whether the attribute was enabled (i.e. attribName was found)
     */
    open fun enableAttribute(attribute: Attribute, attribName: String, ctx: RenderContext): Boolean {
        val location = findAttributeLocation(attribName, ctx)
        enableAttribute(attribute, location, ctx)
        return location >= 0
    }

    /**
     * Enables the specified attribute for this shader. This method is called by concrete Shader
     * implementations to set the vertex attributes used by the implementation.
     *
     * @param attribute    the attribute to enable
     * @param location     attribute location in shader code, if specified with layout (location=...)
     */
    open fun enableAttribute(attribute: Attribute, location: Int, ctx: RenderContext) {
        when (attribute) {
            Attribute.POSITIONS -> attributePositions = location
            Attribute.NORMALS -> attributeNormals = location
            Attribute.TEXTURE_COORDS -> attributeTexCoords = location
            Attribute.COLORS -> attributeColors = location
        }
    }

    /**
     * Looks for the specified uniform and returns its location or -1 if the uniform was not found.
     *
     * @param uniformName    The uniform name to look for
     * @return the uniform location or -1 if the attribute was not found.
     */
    open fun findUniformLocation(uniformName: String, ctx: RenderContext): Any? {
        val ref: ProgramResource? = res
        if (ref != null) {
            return GL.getUniformLocation(ref, uniformName)
        } else {
            return null
        }
    }

    /**
     * Binds the specified Mesh as input to this shader. The mesh's ShaderAttributBinders will
     * be bound to the Shader attributes.

     * @param mesh    Mesh to use as input for shader execution
     * @param ctx    the graphics engine context
     */
    open fun bindMesh(mesh: Mesh, ctx: RenderContext) {
        if (attributePositions != -1) {
            val binder = mesh.meshData.positionBinder ?:
                    throw NullPointerException("Mesh must supply an attribute binder for vertex positions")
            GL.enableVertexAttribArray(attributePositions)
            binder.bindAttribute(attributePositions, ctx)
        }
        if (attributeNormals != -1) {
            val binder = mesh.meshData.normalBinder ?:
                    throw NullPointerException("Mesh must supply an attribute binder for vertex normals")
            GL.enableVertexAttribArray(attributeNormals)
            binder.bindAttribute(attributeNormals, ctx)
        }
        if (attributeTexCoords != -1) {
            val binder = mesh.meshData.texCoordBinder ?:
                    throw NullPointerException("Mesh must supply an attribute binder for vertex texture coordinates")
            GL.enableVertexAttribArray(attributeTexCoords)
            binder.bindAttribute(attributeTexCoords, ctx)
        }
        if (attributeColors != -1) {
            val binder = mesh.meshData.colorBinder ?:
                    throw NullPointerException("Mesh must supply an attribute binder for vertex colors")
            GL.enableVertexAttribArray(attributeColors)
            binder.bindAttribute(attributeColors, ctx)
        }
    }

    /**
     * Disables all vertex attribute arrays that where bound with the last Mesh.
     *
     * @param ctx    the graphics engine context
     */
    open fun unbindMesh(ctx: RenderContext) {
        if (attributePositions != -1) { GL.disableVertexAttribArray(attributePositions) }
        if (attributeNormals != -1) { GL.disableVertexAttribArray(attributeNormals) }
        if (attributeTexCoords != -1) { GL.disableVertexAttribArray(attributeTexCoords) }
        if (attributeColors != -1) { GL.disableVertexAttribArray(attributeColors) }
    }

    /**
     * Deletes the shader program in GPU memory.
     *
     * @param ctx    the graphics engine context
     */
    override fun delete(ctx: RenderContext) {
        // do not call super, as this will immediately delete the shader program on the GPU. However, Shader program is
        // a shared resource and might be used by other shaders. Therefore deletion on the GPU is handled by the
        // ShaderManager:
        if (isValid) {
            ctx.shaderMgr.deleteShader(this, ctx)
            res = null
        }
    }
}
