package de.fabmax.kool.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
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

    protected data class AttributeLocation(val descr: Attribute, val location: Int)
    protected val attributeLocations = mutableListOf<AttributeLocation>()

    val attributes = mutableSetOf<Attribute>()
    val uniforms = mutableMapOf<String, Uniform<*>>()

    abstract fun generate(ctx: KoolContext)

    /**
     * Checks if this Shader is currently bound.
     *
     * @return true if this Shader is bound, false otherwise.
     */
    fun isBound(ctx: KoolContext): Boolean {
        return ctx.shaderMgr.boundShader === this
    }

    /**
     * Loads / compiles the shader program. Is called automatically when the shader is bound for
     * the first time and it is not already loaded.
     *
     * @param ctx    the graphics engine context
     */
    open fun onLoad(ctx: KoolContext) {
        generate(ctx)
        res = ctx.shaderMgr.createShader(source, ctx)

        attributeLocations.clear()
        for (attrib in attributes) {
            enableAttribute(attrib, ctx)
        }

        for (uniform in uniforms.values) {
            uniform.location = findUniformLocation(uniform.name, ctx)
        }
    }

    /**
     * Is called when the shader is bound. Implementations should update all their shader uniforms
     * here.
     *
     * @param ctx    the graphics engine context
     */
    abstract fun onBind(ctx: KoolContext)

    /**
     * Is called on the currently bound shader when the MVP matrix has changed. Implementations should update their
     * transform matrix uniforms here.
     *
     * @param ctx    the graphics engine context
     */
    abstract fun onMatrixUpdate(ctx: KoolContext)

    /**
     * Looks for the specified attribute and returns its location or -1 if the attribute was not found.
     *
     * @param attribName    The attribute name to look for
     * @return the attribute location or -1 if the attribute was not found.
     */
    protected open fun findAttributeLocation(attribName: String, ctx: KoolContext): Int {
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
    private fun enableAttribute(attribute: Attribute, ctx: KoolContext): Boolean {
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
    private fun enableAttribute(attribute: Attribute, location: Int, ctx: KoolContext) {
        if (location >= 0) {
            attributeLocations.add(AttributeLocation(attribute, location))
        }
    }

    /**
     * Adds the given uniform to this shader. The uniform location is not set until [Shader.onLoad] is called (when the
     * shader is loaded for the first time).
     *
     * @param uniform    The uniform to add
     */
    fun <T, U: Uniform<T>> addUniform(uniform: U): U {
        uniforms[uniform.name] = uniform
        return uniform
    }

    fun <T, U: Uniform<T>> getUniform(name: String): U? = uniforms[name] as? U

    /**
     * Looks for the specified uniform and returns its location or null if the uniform was not found. The actual type
     * of the uniform location is platform dependent.
     *
     * @param uniformName    The uniform name to look for
     * @return the uniform location or null if the attribute was not found.
     */
    protected open fun findUniformLocation(uniformName: String, ctx: KoolContext): Any? {
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
    open fun bindMesh(mesh: Mesh, ctx: KoolContext) {
        for (i in attributeLocations.indices) {
            val attrib = attributeLocations[i]
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
    open fun unbindMesh(ctx: KoolContext) {
        for (i in attributeLocations.indices) {
            glDisableVertexAttribArray(attributeLocations[i].location)
        }
    }

    /**
     * Deletes the shader program in GPU memory.
     *
     * @param ctx    the graphics engine context
     */
    override fun dispose(ctx: KoolContext) {
        // do not call super, as this will immediately delete the shader program on the GPU. However, Shader program is
        // a shared resource and might be used by other shaders. Therefore deletion on the GPU is handled by the
        // ShaderManager:
        if (isValid) {
            ctx.shaderMgr.deleteShader(this, ctx)
            res = null
        }
    }
}
