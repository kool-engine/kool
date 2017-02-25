package de.fabmax.kool

import de.fabmax.kool.platform.*

/**
 * @author fabmax
 */
abstract class GlResource constructor(glRef: Any, val type: Type) {
    enum class Type {
        BUFFER,
        FRAMEBUFFER,
        PROGRAM,
        RENDERBUFFER,
        SHADER,
        TEXTURE
    }

    var glRef: Any? = glRef
        protected set

    val isValid: Boolean
        get() = glRef != null

    open fun delete(ctx: RenderContext) {
        ctx.memoryMgr.deleted(this)
        glRef = null
    }
}

class BufferResource private constructor(glRef: Any, val target: Int, ctx: RenderContext) :
        GlResource(glRef, Type.BUFFER) {

    companion object {
        fun create(target: Int, ctx: RenderContext): BufferResource {
            return BufferResource(GL.createBuffer(), target, ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteBuffer(this)
        super.delete(ctx)
    }

    fun bind(ctx: RenderContext) {
        if (ctx.boundBuffers[target] != this) {
            GL.bindBuffer(target, this)
            ctx.boundBuffers[target] = this
        }
    }

    fun setData(data: Float32Buffer, usage: Int, ctx: RenderContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        GL.bufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint8Buffer, usage: Int, ctx: RenderContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        GL.bufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint16Buffer, usage: Int, ctx: RenderContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        GL.bufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 2)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint32Buffer, usage: Int, ctx: RenderContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        GL.bufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun unbind(ctx: RenderContext) {
        GL.bindBuffer(target, null)
        ctx.boundBuffers[target] = null
    }
}

class FramebufferResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.FRAMEBUFFER) {
    companion object {
        fun create(ctx: RenderContext): FramebufferResource {
            return FramebufferResource(GL.createFramebuffer(), ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteFramebuffer(this)
        super.delete(ctx)
    }
}

class ProgramResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.PROGRAM) {
    companion object {
        fun create(ctx: RenderContext): ProgramResource {
            return ProgramResource(GL.createProgram(), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteProgram(this)
        super.delete(ctx)
    }

    fun attachShader(shader: ShaderResource, ctx: RenderContext) {
        GL.attachShader(this, shader)
    }

    fun link(ctx: RenderContext): Boolean {
        GL.linkProgram(this)
        return GL.getProgrami(this, GL.LINK_STATUS) == GL.TRUE
    }

    fun getInfoLog(ctx: RenderContext): String {
        return GL.getProgramInfoLog(this)
    }
}

class RenderbufferResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.RENDERBUFFER) {
    companion object {
        fun create(ctx: RenderContext): RenderbufferResource {
            return RenderbufferResource(GL.createRenderbuffer(), ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteRenderbuffer(this)
        super.delete(ctx)
    }
}

class ShaderResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.SHADER) {
    companion object {
        fun createFragmentShader(ctx: RenderContext): ShaderResource {
            return ShaderResource(GL.createShader(GL.FRAGMENT_SHADER), ctx)
        }

        fun createVertexShader(ctx: RenderContext): ShaderResource {
            return ShaderResource(GL.createShader(GL.VERTEX_SHADER), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteShader(this)
        super.delete(ctx)
    }

    fun shaderSource(source: String, ctx: RenderContext) {
        GL.shaderSource(this, source)
    }

    fun compile(ctx: RenderContext): Boolean {
        GL.compileShader(this)
        return GL.getShaderi(this, GL.COMPILE_STATUS) == GL.TRUE
    }

    fun getInfoLog(ctx: RenderContext): String {
        return GL.getShaderInfoLog(this)
    }
}

class TextureResource private constructor(glRef: Any, val target: Int, val props: TextureProps, ctx: RenderContext) :
        GlResource(glRef, Type.TEXTURE) {

    companion object {
        fun create(target: Int, props: TextureProps, ctx: RenderContext): TextureResource {
            return TextureResource(GL.createTexture(), target, props, ctx)
        }
    }

    var isLoaded = false

    var texUnit = -1
        internal set

    init {
        GL.bindTexture(GL.TEXTURE_2D, this)
        GL.texParameteri(target, GL.TEXTURE_MIN_FILTER, props.minFilter)
        GL.texParameteri(target, GL.TEXTURE_MAG_FILTER, props.magFilter)
        GL.texParameteri(target, GL.TEXTURE_WRAP_S, props.xWrapping)
        GL.texParameteri(target, GL.TEXTURE_WRAP_T, props.yWrapping)
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteTexture(this)
        super.delete(ctx)
    }
}
