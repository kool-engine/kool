package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureData1d
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureData3d
import de.fabmax.kool.platform.*
import de.fabmax.kool.platform.webgl.TextureLoader.arrayBufferView
import de.fabmax.kool.util.*
import org.khronos.webgl.*

object GlImpl : GlApi {
    internal lateinit var gl: WebGL2RenderingContext
        internal set

    override val ARRAY_BUFFER = WebGLRenderingContext.ARRAY_BUFFER
    override val BACK = WebGLRenderingContext.BACK
    override val BLEND = WebGLRenderingContext.BLEND
    override val CLAMP_TO_EDGE = WebGLRenderingContext.CLAMP_TO_EDGE
    override val COLOR = WebGL2RenderingContext.COLOR
    override val COLOR_ATTACHMENT0 = WebGLRenderingContext.COLOR_ATTACHMENT0
    override val COLOR_BUFFER_BIT = WebGLRenderingContext.COLOR_BUFFER_BIT
    override val COMPARE_REF_TO_TEXTURE = WebGL2RenderingContext.COMPARE_REF_TO_TEXTURE
    override val COMPILE_STATUS = WebGLRenderingContext.COMPILE_STATUS
    override val CULL_FACE = WebGLRenderingContext.CULL_FACE
    override val DEPTH_ATTACHMENT = WebGLRenderingContext.DEPTH_ATTACHMENT
    override val DEPTH_BUFFER_BIT = WebGLRenderingContext.DEPTH_BUFFER_BIT
    override val DEPTH_COMPONENT24 = WebGL2RenderingContext.DEPTH_COMPONENT24
    override val DEPTH_COMPONENT32F = WebGL2RenderingContext.DEPTH_COMPONENT32F
    override val DEPTH_TEST = WebGLRenderingContext.DEPTH_TEST
    override val DYNAMIC_DRAW = WebGLRenderingContext.DYNAMIC_DRAW
    override val ELEMENT_ARRAY_BUFFER = WebGLRenderingContext.ELEMENT_ARRAY_BUFFER
    override val FRAGMENT_SHADER = WebGLRenderingContext.FRAGMENT_SHADER
    override val FRAMEBUFFER = WebGLRenderingContext.FRAMEBUFFER
    override val FRAMEBUFFER_COMPLETE = WebGLRenderingContext.FRAMEBUFFER_COMPLETE
    override val FRONT = WebGLRenderingContext.FRONT
    override val INVALID_INDEX = WebGL2RenderingContext.INVALID_INDEX
    override val LINEAR = WebGLRenderingContext.LINEAR
    override val LINEAR_MIPMAP_LINEAR = WebGLRenderingContext.LINEAR_MIPMAP_LINEAR
    override val LINES = WebGLRenderingContext.LINES
    override val LINK_STATUS = WebGLRenderingContext.LINK_STATUS
    override val MIRRORED_REPEAT = WebGLRenderingContext.MIRRORED_REPEAT
    override val NEAREST = WebGLRenderingContext.NEAREST
    override val NEAREST_MIPMAP_NEAREST = WebGLRenderingContext.NEAREST_MIPMAP_NEAREST
    override val NONE = WebGLRenderingContext.NONE
    override val ONE = WebGLRenderingContext.ONE
    override val ONE_MINUS_SRC_ALPHA = WebGLRenderingContext.ONE_MINUS_SRC_ALPHA
    override val POINTS = WebGLRenderingContext.POINTS
    override val RENDERBUFFER = WebGLRenderingContext.RENDERBUFFER
    override val REPEAT = WebGLRenderingContext.REPEAT
    override val SRC_ALPHA = WebGLRenderingContext.SRC_ALPHA
    override val STATIC_DRAW = WebGLRenderingContext.STATIC_DRAW
    override val TEXTURE_2D = WebGLRenderingContext.TEXTURE_2D
    override val TEXTURE_3D = WebGL2RenderingContext.TEXTURE_3D
    override val TEXTURE_COMPARE_MODE = WebGL2RenderingContext.TEXTURE_COMPARE_MODE
    override val TEXTURE_COMPARE_FUNC = WebGL2RenderingContext.TEXTURE_COMPARE_FUNC
    override val TEXTURE_CUBE_MAP = WebGLRenderingContext.TEXTURE_CUBE_MAP
    override val TEXTURE_CUBE_MAP_POSITIVE_X = WebGLRenderingContext.TEXTURE_CUBE_MAP_POSITIVE_X
    override val TEXTURE_CUBE_MAP_NEGATIVE_X = WebGLRenderingContext.TEXTURE_CUBE_MAP_NEGATIVE_X
    override val TEXTURE_CUBE_MAP_POSITIVE_Y = WebGLRenderingContext.TEXTURE_CUBE_MAP_POSITIVE_Y
    override val TEXTURE_CUBE_MAP_NEGATIVE_Y = WebGLRenderingContext.TEXTURE_CUBE_MAP_NEGATIVE_Y
    override val TEXTURE_CUBE_MAP_POSITIVE_Z = WebGLRenderingContext.TEXTURE_CUBE_MAP_POSITIVE_Z
    override val TEXTURE_CUBE_MAP_NEGATIVE_Z = WebGLRenderingContext.TEXTURE_CUBE_MAP_NEGATIVE_Z
    override val TEXTURE_MAG_FILTER = WebGLRenderingContext.TEXTURE_MAG_FILTER
    override val TEXTURE_MIN_FILTER = WebGLRenderingContext.TEXTURE_MIN_FILTER
    override val TEXTURE_WRAP_R = WebGL2RenderingContext.TEXTURE_WRAP_R
    override val TEXTURE_WRAP_S = WebGLRenderingContext.TEXTURE_WRAP_S
    override val TEXTURE_WRAP_T = WebGLRenderingContext.TEXTURE_WRAP_T
    override val TEXTURE0 = WebGLRenderingContext.TEXTURE0
    override val TRIANGLES = WebGLRenderingContext.TRIANGLES
    override val TRUE = true
    override val UNIFORM_BLOCK_DATA_SIZE = WebGL2RenderingContext.UNIFORM_BLOCK_DATA_SIZE
    override val UNIFORM_BUFFER = WebGL2RenderingContext.UNIFORM_BUFFER
    override val UNIFORM_OFFSET = WebGL2RenderingContext.UNIFORM_OFFSET
    override val VERTEX_SHADER = WebGLRenderingContext.VERTEX_SHADER

    override val INT = WebGLRenderingContext.INT
    override val FLOAT = WebGLRenderingContext.FLOAT
    override val UNSIGNED_BYTE = WebGLRenderingContext.UNSIGNED_BYTE
    override val UNSIGNED_INT = WebGLRenderingContext.UNSIGNED_INT

    override val RED = WebGL2RenderingContext.RED
    override val RG = WebGL2RenderingContext.RG
    override val RGB = WebGLRenderingContext.RGB
    override val RGBA = WebGLRenderingContext.RGBA

    override val R8 = WebGL2RenderingContext.R8
    override val RG8 = WebGL2RenderingContext.RG8
    override val RGB8 = WebGL2RenderingContext.RGB8
    override val RGBA8 = WebGL2RenderingContext.RGBA8
    override val R16F = WebGL2RenderingContext.R16F
    override val RG16F = WebGL2RenderingContext.RG16F
    override val RGB16F = WebGL2RenderingContext.RGB16F
    override val RGBA16F = WebGL2RenderingContext.RGBA16F
    override val R32F = WebGL2RenderingContext.R32F
    override val RG32F = WebGL2RenderingContext.RG32F
    override val RGB32F = WebGL2RenderingContext.RGB32F
    override val RGBA32F = WebGL2RenderingContext.RGBA32F

    override val ALWAYS = WebGLRenderingContext.ALWAYS
    override val NEVER = WebGLRenderingContext.NEVER
    override val LESS = WebGLRenderingContext.LESS
    override val LEQUAL = WebGLRenderingContext.LEQUAL
    override val GREATER = WebGLRenderingContext.GREATER
    override val GEQUAL = WebGLRenderingContext.GEQUAL
    override val EQUAL = WebGLRenderingContext.EQUAL
    override val NOTEQUAL = WebGLRenderingContext.NOTEQUAL

    override val NULL_BUFFER: GlBuffer = GlBuffer(-1)
    override val NULL_FRAMEBUFFER: GlFramebuffer = GlFramebuffer(-1)
    override val NULL_TEXTURE: GlTexture = GlTexture(-1)

    private val buffers = WebGlObjList<WebGLBuffer, Unit>(
        factory = { gl.createBuffer() },
        deleter = { gl.deleteBuffer(it) }
    )
    private val framebuffers = WebGlObjList<WebGLFramebuffer, Unit>(
        factory = { gl.createFramebuffer() },
        deleter = { gl.deleteFramebuffer(it) }
    )
    private val programs = WebGlObjList<ProgramInfo, Unit>(
        factory = { ProgramInfo(gl.createProgram()) },
        deleter = { gl.deleteProgram(it?.webGl) }
    )
    private val renderbuffers = WebGlObjList<WebGLRenderbuffer, Unit>(
        factory = { gl.createRenderbuffer() },
        deleter = { gl.deleteRenderbuffer(it) }
    )
    private val shaders = WebGlObjList<WebGLShader, Int>(
        factory = { gl.createShader(it) },
        deleter = { gl.deleteShader(it) }
    )
    private val textures = WebGlObjList<WebGLTexture, Unit>(
        factory = { gl.createTexture() },
        deleter = { gl.deleteTexture(it) }
    )

    private var activeProgram: ProgramInfo? = null

    private val GlBuffer.webGl: WebGLBuffer? get() = buffers[handle]
    private val GlFramebuffer.webGl: WebGLFramebuffer? get() = framebuffers[handle]
    private val GlProgram.webGl: WebGLProgram? get() = programs[handle]?.webGl
    private val GlRenderbuffer.webGl: WebGLRenderbuffer? get() = renderbuffers[handle]
    private val GlShader.webGl: WebGLShader? get() = shaders[handle]
    private val GlTexture.webGl: WebGLTexture? get() = textures[handle]
    private val Int.webGlUniformLoc: WebGLUniformLocation? get() = activeProgram?.uniformLocations?.getOrNull(this)

    override fun activeTexture(texture: Int) = gl.activeTexture(texture)
    override fun attachShader(program: GlProgram, shader: GlShader) = gl.attachShader(program.webGl, shader.webGl)
    override fun bindBuffer(target: Int, buffer: GlBuffer) = gl.bindBuffer(target, buffer.webGl)
    override fun bindBufferBase(target: Int, index: Int, buffer: GlBuffer) = gl.bindBufferBase(target, index, buffer.webGl)
    override fun bindFramebuffer(target: Int, framebuffer: GlFramebuffer) = gl.bindFramebuffer(target, framebuffer.webGl)
    override fun bindRenderbuffer(target: Int, renderbuffer: GlRenderbuffer) = gl.bindRenderbuffer(target, renderbuffer.webGl)
    override fun bindTexture(target: Int, texture: GlTexture) = gl.bindTexture(target, texture.webGl)
    override fun blendFunc(sFactor: Int, dFactor: Int) = gl.blendFunc(sFactor, dFactor)
    override fun bufferData(target: Int, buffer: Uint8Buffer, usage: Int) = gl.bufferData(target, (buffer as Uint8BufferImpl).buffer, usage, 0, buffer.len)
    override fun bufferData(target: Int, buffer: Uint16Buffer, usage: Int) = gl.bufferData(target, (buffer as Uint16BufferImpl).buffer, usage, 0, buffer.len)
    override fun bufferData(target: Int, buffer: Int32Buffer, usage: Int) = gl.bufferData(target, (buffer as Int32BufferImpl).buffer, usage, 0, buffer.len)
    override fun bufferData(target: Int, buffer: Float32Buffer, usage: Int) = gl.bufferData(target, (buffer as Float32BufferImpl).buffer, usage, 0, buffer.len)
    override fun bufferData(target: Int, buffer: MixedBuffer, usage: Int) = gl.bufferData(target, (buffer as MixedBufferImpl).buffer, usage, 0, buffer.len)
    override fun checkFramebufferStatus(target: Int): Int = gl.checkFramebufferStatus(target)
    override fun clear(mask: Int) = gl.clear(mask)
    override fun clearBufferfv(buffer: Int, drawBuffer: Int, values: Float32Buffer) = gl.clearBufferfv(buffer, drawBuffer, (values as Float32BufferImpl).buffer)
    override fun clearColor(r: Float, g: Float, b: Float, a: Float) = gl.clearColor(r, g, b, a)
    override fun compileShader(shader: GlShader) = gl.compileShader(shader.webGl)
    override fun copyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int) = gl.copyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height)
    override fun createBuffer(): GlBuffer = GlBuffer(buffers.create(Unit))
    override fun createFramebuffer(): GlFramebuffer = GlFramebuffer(framebuffers.create(Unit))
    override fun createProgram(): GlProgram = GlProgram(programs.create(Unit))
    override fun createRenderbuffer(): GlRenderbuffer = GlRenderbuffer(renderbuffers.create(Unit))
    override fun createShader(type: Int): GlShader = GlShader(shaders.create(type))
    override fun createTexture(): GlTexture = GlTexture(textures.create(Unit))
    override fun cullFace(mode: Int) = gl.cullFace(mode)
    override fun deleteBuffer(buffer: GlBuffer) = buffers.delete(buffer.handle)
    override fun deleteFramebuffer(framebuffer: GlFramebuffer) = framebuffers.delete(framebuffer.handle)
    override fun deleteProgram(program: GlProgram) = programs.delete(program.handle)
    override fun deleteRenderbuffer(renderbuffer: GlRenderbuffer) = renderbuffers.delete(renderbuffer.handle)
    override fun deleteShader(shader: GlShader) = shaders.delete(shader.handle)
    override fun deleteTexture(texture: GlTexture) = textures.delete(texture.handle)
    override fun depthFunc(func: Int) = gl.depthFunc(func)
    override fun depthMask(flag: Boolean) = gl.depthMask(flag)
    override fun disable(cap: Int) = gl.disable(cap)
    override fun disableVertexAttribArray(index: Int) = gl.disableVertexAttribArray(index)
    override fun drawBuffers(buffers: IntArray) = gl.drawBuffers(buffers)
    override fun drawElements(mode: Int, count: Int, type: Int) = gl.drawElements(mode, count, type, 0)
    override fun drawElementsInstanced(mode: Int, count: Int, type: Int, instanceCount: Int) = gl.drawElementsInstanced(mode, count, type, 0, instanceCount)
    override fun enable(cap: Int) = gl.enable(cap)
    override fun enableVertexAttribArray(index: Int) = gl.enableVertexAttribArray(index)
    override fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: GlRenderbuffer) = gl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.webGl)
    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: GlTexture, level: Int) = gl.framebufferTexture2D(target, attachment, textarget, texture.webGl, level)
    override fun generateMipmap(target: Int) = gl.generateMipmap(target)
    override fun getActiveUniformBlockParameter(program: GlProgram, uniformBlockIndex: Int, pName: Int): Int = gl.getActiveUniformBlockParameter(program.webGl, uniformBlockIndex, pName)
    override fun getActiveUniforms(program: GlProgram, uniformIndices: IntArray, pName: Int): IntArray = gl.getActiveUniforms(program.webGl, uniformIndices, pName)
    override fun getProgramInfoLog(program: GlProgram): String = gl.getProgramInfoLog(program.webGl) ?: ""
    override fun getProgramParameter(program: GlProgram, param: Int): Any = gl.getProgramParameter(program.webGl, param) ?: 0
    override fun getShaderInfoLog(shader: GlShader): String = gl.getShaderInfoLog(shader.webGl) ?: ""
    override fun getShaderParameter(shader: GlShader, param: Int): Any = gl.getShaderParameter(shader.webGl, param) ?: 0
    override fun getUniformBlockIndex(program: GlProgram, uniformBlockName: String): Int = gl.getUniformBlockIndex(program.webGl, uniformBlockName)
    override fun getUniformIndices(program: GlProgram, names: Array<String>): IntArray = gl.getUniformIndices(program.webGl, names)
    override fun getUniformLocation(program: GlProgram, uniformName: String): Int = programs[program.handle]?.getUniformLocation(uniformName) ?: -1
    override fun lineWidth(width: Float) = gl.lineWidth(width)
    override fun linkProgram(program: GlProgram) = gl.linkProgram(program.webGl)
    override fun readBuffer(src: Int) = gl.readBuffer(src)
    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) = gl.renderbufferStorage(target, internalformat, width, height)
    override fun shaderSource(shader: GlShader, source: String) = gl.shaderSource(shader.webGl, source)
    override fun texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Buffer?) = texImage2dImpl(target, level, internalformat, width, height, border, format, type, pixels)
    override fun texImage2d(target: Int, data: TextureData) = texImage2dImpl(target, data)
    override fun texImage3d(target: Int, data: TextureData) = textImage3dImpl(target, data)
    override fun texParameteri(target: Int, pName: Int, param: Int) = gl.texParameteri(target, pName, param)
    override fun texStorage2D(target: Int, levels: Int, internalformat: Int, width: Int, height: Int) = gl.texStorage2D(target, levels, internalformat, width, height)
    override fun uniformBlockBinding(program: GlProgram, uniformBlockIndex: Int, uniformBlockBinding: Int) = gl.uniformBlockBinding(program.webGl, uniformBlockIndex, uniformBlockBinding)
    override fun useProgram(program: GlProgram) = programs[program.handle].let { gl.useProgram(it?.webGl); activeProgram = it }
    override fun uniform1f(location: Int, x: Float) = gl.uniform1f(location.webGlUniformLoc, x)
    override fun uniform2f(location: Int, x: Float, y: Float) = gl.uniform2f(location.webGlUniformLoc, x, y)
    override fun uniform3f(location: Int, x: Float, y: Float, z: Float) = gl.uniform3f(location.webGlUniformLoc, x, y, z)
    override fun uniform4f(location: Int, x: Float, y: Float, z: Float, w: Float) = gl.uniform4f(location.webGlUniformLoc, x, y, z, w)
    override fun uniform1fv(location: Int, values: Float32Buffer) = gl.uniform1fv(location.webGlUniformLoc, (values as Float32BufferImpl).buffer)
    override fun uniform2fv(location: Int, values: Float32Buffer) = gl.uniform2fv(location.webGlUniformLoc, (values as Float32BufferImpl).buffer)
    override fun uniform3fv(location: Int, values: Float32Buffer) = gl.uniform3fv(location.webGlUniformLoc, (values as Float32BufferImpl).buffer)
    override fun uniform4fv(location: Int, values: Float32Buffer) = gl.uniform4fv(location.webGlUniformLoc, (values as Float32BufferImpl).buffer)
    override fun uniform1i(location: Int, x: Int) = gl.uniform1i(location.webGlUniformLoc, x)
    override fun uniform2i(location: Int, x: Int, y: Int) = gl.uniform2i(location.webGlUniformLoc, x, y)
    override fun uniform3i(location: Int, x: Int, y: Int, z: Int) = gl.uniform3i(location.webGlUniformLoc, x, y, z)
    override fun uniform4i(location: Int, x: Int, y: Int, z: Int, w: Int) = gl.uniform4i(location.webGlUniformLoc, x, y, z, w)
    override fun uniform1iv(location: Int, values: Int32Buffer) = gl.uniform1iv(location.webGlUniformLoc, (values as Int32BufferImpl).buffer)
    override fun uniform2iv(location: Int, values: Int32Buffer) = gl.uniform2iv(location.webGlUniformLoc, (values as Int32BufferImpl).buffer)
    override fun uniform3iv(location: Int, values: Int32Buffer) = gl.uniform3iv(location.webGlUniformLoc, (values as Int32BufferImpl).buffer)
    override fun uniform4iv(location: Int, values: Int32Buffer) = gl.uniform4iv(location.webGlUniformLoc, (values as Int32BufferImpl).buffer)
    override fun uniformMatrix3fv(location: Int, values: Float32Buffer) = gl.uniformMatrix3fv(location.webGlUniformLoc, false, (values as Float32BufferImpl).buffer)
    override fun uniformMatrix4fv(location: Int, values: Float32Buffer) = gl.uniformMatrix4fv(location.webGlUniformLoc, false, (values as Float32BufferImpl).buffer)
    override fun vertexAttribDivisor(index: Int, divisor: Int) = gl.vertexAttribDivisor(index, divisor)
    override fun vertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) = gl.vertexAttribIPointer(index, size, type, stride, offset)
    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) = gl.vertexAttribPointer(index, size, type, normalized, stride, offset)
    override fun viewport(x: Int, y: Int, width: Int, height: Int) = gl.viewport(x, y, width, height)

    private class WebGlObjList<T, P>(
        val factory: (P) -> T?,
        val deleter: (T?) -> Unit
    ) {
        val objs = mutableListOf<T?>()

        operator fun get(index: Int) = objs.getOrNull(index)

        fun create(p: P): Int {
            val insertIdx = objs.indexOfFirst { it == null }
            return if (insertIdx >= 0) {
                objs[insertIdx] = factory(p)
                insertIdx
            } else {
                objs += factory(p)
                objs.lastIndex
            }
        }

        fun delete(index: Int) {
            deleter(objs[index])
            objs[index] = null
        }
    }

    private class ProgramInfo(val webGl: WebGLProgram?) {
        val uniformLocations = mutableListOf<WebGLUniformLocation?>()
        val uniformLocationIds = mutableMapOf<String, Int>()

        fun getUniformLocation(uniformName: String): Int {
            val locId = uniformLocationIds[uniformName]
            return if (locId != null) {
                locId
            } else {
                uniformLocations += gl.getUniformLocation(webGl, uniformName)
                uniformLocationIds[uniformName] = uniformLocations.lastIndex
                uniformLocations.lastIndex
            }
        }
    }

    private fun texImage2dImpl(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Buffer?) {
        when (pixels) {
            is Uint8BufferImpl -> gl.texImage2D(target, level, internalformat, width, height, border, format, type, pixels.buffer)
            is Uint16BufferImpl -> gl.texImage2D(target, level, internalformat, width, height, border, format, type, pixels.buffer)
            is Int32BufferImpl -> gl.texImage2D(target, level, internalformat, width, height, border, format, type, pixels.buffer)
            is Float32BufferImpl -> gl.texImage2D(target, level, internalformat, width, height, border, format, type, pixels.buffer)
            else -> {
                val nullPixels: ArrayBufferView? = null
                gl.texImage2D(target, level, internalformat, width, height, border, format, type, nullPixels)
            }
        }
    }

    private fun texImage2dImpl(target: Int, data: TextureData) {
        gl.pixelStorei(WebGLRenderingContext.UNPACK_COLORSPACE_CONVERSION_WEBGL, WebGLRenderingContext.NONE)
        when (data) {
            is TextureData1d -> {
                gl.texImage2D(target, 0, data.format.glInternalFormat, data.width, 1, 0, data.format.glFormat, data.format.glType, data.arrayBufferView)
            }
            is TextureData2d -> {
                gl.texImage2D(target, 0, data.format.glInternalFormat, data.width, data.height, 0, data.format.glFormat, data.format.glType, data.arrayBufferView)
            }
            is ImageTextureData -> {
                gl.texImage2D(target, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, data.data)
            }
            else -> throw IllegalArgumentException("Invalid TextureData type for texImage2d: $data")
        }
    }

    private fun textImage3dImpl(target: Int, img: TextureData) {
        when (img) {
            is TextureData3d -> {
                gl.texImage3D(target, 0, img.format.glInternalFormat, img.width, img.height, img.depth, 0, img.format.glFormat, img.format.glType, img.arrayBufferView)
            }
            is ImageAtlasTextureData -> {
                gl.texStorage3D(target, 1, img.format.glInternalFormat, img.width, img.height, img.depth)
                for (z in 0 until img.depth) {
                    gl.texSubImage3D(target, 0, 0, 0, z, img.width, img.height, 1, img.format.glFormat, img.format.glType, img.data[z])
                }
            }
            else -> {
                throw IllegalStateException("TextureData buffer must be either TextureData3d or ImageAtlasTextureData")
            }
        }
    }
}