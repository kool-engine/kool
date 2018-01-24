package de.fabmax.kool.shading

import de.fabmax.kool.KoolException
import de.fabmax.kool.RenderContext
import de.fabmax.kool.gl.*
import de.fabmax.kool.scene.Mesh

/**
 * Base class for custom shader implementations.
 *
 * @author fabmax
 */
abstract class Shader : GlObject<ProgramResource>() {

    var source = Source("", "", "")
        protected set

    data class Source(val vertexSrc: String, val geometrySrc: String, val fragmentSrc: String) {
        constructor(vertexSrc: String, fragmentSrc: String) : this(vertexSrc, "", fragmentSrc)
    }

    protected data class LocatedAttribute(val descr: Attribute, val location: Int)

    protected val attributes = mutableListOf<LocatedAttribute>()

    abstract fun generateSource(ctx: RenderContext)

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
        generateSource(ctx)
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
            return glGetAttribLocation(ref, attribName)
        } else {
            return -1
        }
    }

    /**
     * Enables the specified attribute for this shader. This method is called by concrete Shader
     * implementations to set the vertex attributes used by the implementation. If no attribute with
     * the specified name is found, the attribute is disabled.
     *
     * @param attribute     the attribute to enable, attribute.name must correspond to the attribute
     *                      name in shader source
     * @return whether the attribute was enabled (i.e. attribName was found)
     */
    open fun enableAttribute(attribute: Attribute, ctx: RenderContext): Boolean {
        val location = findAttributeLocation(attribute.name, ctx)
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
        if (location >= 0) {
            attributes.add(LocatedAttribute(attribute, location))
        }
    }

    /**
     * Sets the location of the given uniform. Returns true if location was set or false if the uniform was
     * not found
     *
     * @param uniform    The uniform to set
     * @return true if location was successfully set
     */
    open fun setUniformLocation(uniform: Uniform<*>, ctx: RenderContext): Boolean {
        uniform.location = findUniformLocation(uniform.name, ctx)
        return uniform.location != null
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
            return glGetUniformLocation(ref, uniformName)
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
        for (i in attributes.indices) {
            val attrib = attributes[i]
            val binder = mesh.meshData.attributeBinders[attrib.descr] ?:
                    throw KoolException("Mesh must supply an attribute binder for attribute ${attrib.descr.name}")
            glEnableVertexAttribArray(attrib.location)
            binder.bindAttribute(attrib.location, ctx)
        }
    }

    /**
     * Disables all vertex attribute arrays that where bound with the last Mesh.
     *
     * @param ctx    the graphics engine context
     */
    open fun unbindMesh(ctx: RenderContext) {
        for (i in attributes.indices) {
            glDisableVertexAttribArray(attributes[i].location)
        }
    }

    /**
     * Deletes the shader program in GPU memory.
     *
     * @param ctx    the graphics engine context
     */
    override fun dispose(ctx: RenderContext) {
        // do not call super, as this will immediately delete the shader program on the GPU. However, Shader program is
        // a shared resource and might be used by other shaders. Therefore deletion on the GPU is handled by the
        // ShaderManager:
        if (isValid) {
            ctx.shaderMgr.deleteShader(this, ctx)
            res = null
        }
    }
}
