package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.ImageAtlasTextureData
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.*
import org.khronos.webgl.*

object GlImpl : GlApi {
    lateinit var gl: WebGL2RenderingContext
        private set

    private var clipControlExt: EXT_clip_control? = null

    override val ARRAY_BUFFER = WebGLRenderingContext.ARRAY_BUFFER
    override val BACK = WebGLRenderingContext.BACK
    override val BLEND = WebGLRenderingContext.BLEND
    override val CLAMP_TO_EDGE = WebGLRenderingContext.CLAMP_TO_EDGE
    override val COLOR = WebGL2RenderingContext.COLOR
    override val COLOR_ATTACHMENT0 = WebGLRenderingContext.COLOR_ATTACHMENT0
    override val COLOR_BUFFER_BIT = WebGLRenderingContext.COLOR_BUFFER_BIT
    override val COMPARE_REF_TO_TEXTURE = WebGL2RenderingContext.COMPARE_REF_TO_TEXTURE
    override val COMPILE_STATUS = WebGLRenderingContext.COMPILE_STATUS
    override val COMPUTE_SHADER: Int get() = notSupported("COMPUTE_SHADER")
    override val CULL_FACE = WebGLRenderingContext.CULL_FACE
    override val DEPTH_ATTACHMENT = WebGLRenderingContext.DEPTH_ATTACHMENT
    override val DEPTH_BUFFER_BIT = WebGLRenderingContext.DEPTH_BUFFER_BIT
    override val DEPTH_COMPONENT24 = WebGL2RenderingContext.DEPTH_COMPONENT24
    override val DEPTH_COMPONENT32F = WebGL2RenderingContext.DEPTH_COMPONENT32F
    override val DEPTH_COMPONENT = WebGL2RenderingContext.DEPTH_COMPONENT
    override val DEPTH_TEST = WebGLRenderingContext.DEPTH_TEST
    override val DRAW_FRAMEBUFFER = WebGL2RenderingContext.DRAW_FRAMEBUFFER
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
    override val QUERY_RESULT: Int = WebGL2RenderingContext.QUERY_RESULT
    override val QUERY_RESULT_AVAILABLE: Int = WebGL2RenderingContext.QUERY_RESULT_AVAILABLE
    override val READ_FRAMEBUFFER = WebGL2RenderingContext.READ_FRAMEBUFFER
    override val READ_ONLY: Int get() = notSupported("READ_ONLY")
    override val READ_WRITE: Int get() = notSupported("READ_WRITE")
    override val RENDERBUFFER = WebGLRenderingContext.RENDERBUFFER
    override val REPEAT = WebGLRenderingContext.REPEAT
    override val SAMPLES = WebGLRenderingContext.SAMPLES
    override val SCISSOR_TEST = WebGLRenderingContext.SCISSOR_TEST
    override val SHADER_STORAGE_BUFFER: Int get() = notSupported("SHADER_STORAGE_BUFFER")
    override val SRC_ALPHA = WebGLRenderingContext.SRC_ALPHA
    override val STATIC_DRAW = WebGLRenderingContext.STATIC_DRAW
    override val TEXTURE_1D: Int get() = notSupported("TEXTURE_1D")
    override val TEXTURE_2D = WebGLRenderingContext.TEXTURE_2D
    override val TEXTURE_2D_ARRAY: Int = WebGL2RenderingContext.TEXTURE_2D_ARRAY
    override val TEXTURE_3D = WebGL2RenderingContext.TEXTURE_3D
    override val TEXTURE_BASE_LEVEL = WebGL2RenderingContext.TEXTURE_BASE_LEVEL
    override val TEXTURE_COMPARE_MODE = WebGL2RenderingContext.TEXTURE_COMPARE_MODE
    override val TEXTURE_COMPARE_FUNC = WebGL2RenderingContext.TEXTURE_COMPARE_FUNC
    override val TEXTURE_CUBE_MAP = WebGLRenderingContext.TEXTURE_CUBE_MAP
    override val TEXTURE_CUBE_MAP_ARRAY: Int get() = notSupported("TEXTURE_CUBE_MAP_ARRAY")
    override val TEXTURE_CUBE_MAP_POSITIVE_X = WebGLRenderingContext.TEXTURE_CUBE_MAP_POSITIVE_X
    override val TEXTURE_CUBE_MAP_NEGATIVE_X = WebGLRenderingContext.TEXTURE_CUBE_MAP_NEGATIVE_X
    override val TEXTURE_CUBE_MAP_POSITIVE_Y = WebGLRenderingContext.TEXTURE_CUBE_MAP_POSITIVE_Y
    override val TEXTURE_CUBE_MAP_NEGATIVE_Y = WebGLRenderingContext.TEXTURE_CUBE_MAP_NEGATIVE_Y
    override val TEXTURE_CUBE_MAP_POSITIVE_Z = WebGLRenderingContext.TEXTURE_CUBE_MAP_POSITIVE_Z
    override val TEXTURE_CUBE_MAP_NEGATIVE_Z = WebGLRenderingContext.TEXTURE_CUBE_MAP_NEGATIVE_Z
    override val TEXTURE_MAG_FILTER = WebGLRenderingContext.TEXTURE_MAG_FILTER
    override val TEXTURE_MAX_LOD = WebGL2RenderingContext.TEXTURE_MAX_LOD
    override val TEXTURE_MAX_LEVEL = WebGL2RenderingContext.TEXTURE_MAX_LEVEL
    override val TEXTURE_MIN_FILTER = WebGLRenderingContext.TEXTURE_MIN_FILTER
    override val TEXTURE_MIN_LOD = WebGL2RenderingContext.TEXTURE_MIN_LOD
    override val TEXTURE_WRAP_R = WebGL2RenderingContext.TEXTURE_WRAP_R
    override val TEXTURE_WRAP_S = WebGLRenderingContext.TEXTURE_WRAP_S
    override val TEXTURE_WRAP_T = WebGLRenderingContext.TEXTURE_WRAP_T
    override val TEXTURE0 = WebGLRenderingContext.TEXTURE0
    override val TIME_ELAPSED: Int get() = notSupported("TIME_ELAPSED")
    override val TIMESTAMP: Int get() = notSupported("TIMESTAMP")
    override val TRIANGLES = WebGLRenderingContext.TRIANGLES
    override val TRIANGLE_STRIP = WebGLRenderingContext.TRIANGLE_STRIP
    override val TRUE = true
    override val UNIFORM_BLOCK_DATA_SIZE = WebGL2RenderingContext.UNIFORM_BLOCK_DATA_SIZE
    override val UNIFORM_BUFFER = WebGL2RenderingContext.UNIFORM_BUFFER
    override val UNIFORM_OFFSET = WebGL2RenderingContext.UNIFORM_OFFSET
    override val VERTEX_SHADER = WebGLRenderingContext.VERTEX_SHADER
    override val WRITE_ONLY: Int get() = notSupported("WRITE_ONLY")

    override val INT = WebGLRenderingContext.INT
    override val FLOAT = WebGLRenderingContext.FLOAT
    override val UNSIGNED_BYTE = WebGLRenderingContext.UNSIGNED_BYTE
    override val UNSIGNED_INT = WebGLRenderingContext.UNSIGNED_INT

    override val RED = WebGL2RenderingContext.RED
    override val RG = WebGL2RenderingContext.RG
    override val RGB = WebGLRenderingContext.RGB
    override val RGBA = WebGLRenderingContext.RGBA
    override val RED_INTEGER = WebGL2RenderingContext.RED_INTEGER
    override val RG_INTEGER = WebGL2RenderingContext.RG_INTEGER
    override val RGB_INTEGER = WebGL2RenderingContext.RGB_INTEGER
    override val RGBA_INTEGER = WebGL2RenderingContext.RGBA_INTEGER

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
    override val R32I = WebGL2RenderingContext.R32I
    override val RG32I = WebGL2RenderingContext.RG32I
    override val RGB32I = WebGL2RenderingContext.RGB32I
    override val RGBA32I = WebGL2RenderingContext.RGBA32I
    override val R32UI = WebGL2RenderingContext.R32UI
    override val RG32UI = WebGL2RenderingContext.RG32UI
    override val RGB32UI = WebGL2RenderingContext.RGB32UI
    override val RGBA32UI = WebGL2RenderingContext.RGBA32UI
    override val R11F_G11F_B10F = WebGL2RenderingContext.R11F_G11F_B10F

    override val ALWAYS = WebGLRenderingContext.ALWAYS
    override val NEVER = WebGLRenderingContext.NEVER
    override val LESS = WebGLRenderingContext.LESS
    override val LEQUAL = WebGLRenderingContext.LEQUAL
    override val GREATER = WebGLRenderingContext.GREATER
    override val GEQUAL = WebGLRenderingContext.GEQUAL
    override val EQUAL = WebGLRenderingContext.EQUAL
    override val NOTEQUAL = WebGLRenderingContext.NOTEQUAL

    override val VERTEX_ATTRIB_ARRAY_BARRIER_BIT: Int get() = notSupported("VERTEX_ATTRIB_ARRAY_BARRIER_BIT")
    override val ELEMENT_ARRAY_BARRIER_BIT: Int get() = notSupported("ELEMENT_ARRAY_BARRIER_BIT")
    override val UNIFORM_BARRIER_BIT: Int get() = notSupported("UNIFORM_BARRIER_BIT")
    override val TEXTURE_FETCH_BARRIER_BIT: Int get() = notSupported("TEXTURE_FETCH_BARRIER_BIT")
    override val SHADER_IMAGE_ACCESS_BARRIER_BIT: Int get() = notSupported("SHADER_IMAGE_ACCESS_BARRIER_BIT")
    override val COMMAND_BARRIER_BIT: Int get() = notSupported("COMMAND_BARRIER_BIT")
    override val PIXEL_BUFFER_BARRIER_BIT: Int get() = notSupported("PIXEL_BUFFER_BARRIER_BIT")
    override val TEXTURE_UPDATE_BARRIER_BIT: Int get() = notSupported("TEXTURE_UPDATE_BARRIER_BIT")
    override val BUFFER_UPDATE_BARRIER_BIT: Int get() = notSupported("BUFFER_UPDATE_BARRIER_BIT")
    override val CLIENT_MAPPED_BUFFER_BARRIER_BIT: Int get() = notSupported("CLIENT_MAPPED_BUFFER_BARRIER_BIT")
    override val FRAMEBUFFER_BARRIER_BIT: Int get() = notSupported("FRAMEBUFFER_BARRIER_BIT")
    override val TRANSFORM_FEEDBACK_BARRIER_BIT: Int get() = notSupported("TRANSFORM_FEEDBACK_BARRIER_BIT")
    override val ATOMIC_COUNTER_BARRIER_BIT: Int get() = notSupported("ATOMIC_COUNTER_BARRIER_BIT")
    override val SHADER_STORAGE_BARRIER_BIT: Int get() = notSupported("SHADER_STORAGE_BARRIER_BIT")
    override val QUERY_BUFFER_BARRIER_BIT: Int get() = notSupported("QUERY_BUFFER_BARRIER_BIT")

    override val DEFAULT_FRAMEBUFFER: GlFramebuffer = GlFramebuffer(-1)
    override val NULL_BUFFER: GlBuffer = GlBuffer(-1)
    override val NULL_TEXTURE: GlTexture = GlTexture(-1)
    override val NULL_VAO: GlVertexArrayObject = GlVertexArrayObject(-1)

    override var TEXTURE_MAX_ANISOTROPY_EXT = 0
        private set

    override var LOWER_LEFT = 0
        private set
    override var NEGATIVE_ONE_TO_ONE = 0
        private set
    override var UPPER_LEFT = 0
        private set
    override var ZERO_TO_ONE = 0
        private set

    override val version = GlApiVersion(2, 0, GlFlavor.WebGL, "WebGL")

    override lateinit var capabilities: GlCapabilities
        private set

    private val buffers = WebGlObjList<WebGLBuffer, Unit>(
        factory = { gl.createBuffer() },
        deleter = { gl.deleteBuffer(it) }
    )
    private val framebuffers = WebGlObjList<WebGLFramebuffer, Unit>(
        factory = { gl.createFramebuffer() },
        deleter = { gl.deleteFramebuffer(it) }
    )
    private val queries = WebGlObjList<WebGLQuery, Unit>(
        factory = { gl.createQuery() },
        deleter = { gl.deleteQuery(it) }
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
    private val vaos = WebGlObjList<WebGLVertexArrayObject, Unit>(
        factory = { gl.createVertexArray() },
        deleter = { gl.deleteVertexArray(it) }
    )

    private var activeProgram: ProgramInfo? = null

    private val GlBuffer.webGl: WebGLBuffer? get() = buffers[handle]
    private val GlFramebuffer.webGl: WebGLFramebuffer? get() = framebuffers[handle]
    private val GlProgram.webGl: WebGLProgram? get() = programs[handle]?.webGl
    private val GlQuery.webGl: WebGLQuery? get() = queries[handle]
    private val GlRenderbuffer.webGl: WebGLRenderbuffer? get() = renderbuffers[handle]
    private val GlShader.webGl: WebGLShader? get() = shaders[handle]
    private val GlTexture.webGl: WebGLTexture? get() = textures[handle]
    private val GlVertexArrayObject.webGl: WebGLVertexArrayObject? get() = vaos[handle]
    private val Int.webGlUniformLoc: WebGLUniformLocation? get() = activeProgram?.uniformLocations?.getOrNull(this)

    override fun activeTexture(texture: Int) = gl.activeTexture(texture)
    override fun attachShader(program: GlProgram, shader: GlShader) = gl.attachShader(program.webGl, shader.webGl)
    override fun beginQuery(target: Int, query: GlQuery) = gl.beginQuery(target, query.webGl)
    override fun bindBuffer(target: Int, buffer: GlBuffer) = gl.bindBuffer(target, buffer.webGl)
    override fun bindBufferBase(target: Int, index: Int, buffer: GlBuffer) = gl.bindBufferBase(target, index, buffer.webGl)
    override fun bindFramebuffer(target: Int, framebuffer: GlFramebuffer) = gl.bindFramebuffer(target, framebuffer.webGl)
    override fun bindImageTexture(unit: Int, texture: GlTexture, level: Int, layered: Boolean, layer: Int, access: Int, format: Int) = notSupported("bindImageTexture")
    override fun bindRenderbuffer(target: Int, renderbuffer: GlRenderbuffer) = gl.bindRenderbuffer(target, renderbuffer.webGl)
    override fun bindTexture(target: Int, texture: GlTexture) = gl.bindTexture(target, texture.webGl)
    override fun bindVertexArray(vao: GlVertexArrayObject) = gl.bindVertexArray(vao.webGl)
    override fun blendFunc(sFactor: Int, dFactor: Int) = gl.blendFunc(sFactor, dFactor)
    override fun blitFramebuffer(srcX0: Int, srcY0: Int, srcX1: Int, srcY1: Int, dstX0: Int, dstY0: Int, dstX1: Int, dstY1: Int, mask: Int, filter: Int) = gl.blitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter)
    override fun bufferData(target: Int, size: Int, usage: Int) = gl.bufferData(target, size, usage)
    override fun bufferData(target: Int, buffer: Uint8Buffer, usage: Int) = gl.bufferData(target, (buffer as Uint8BufferImpl).buffer, usage, 0, buffer.limit)
    override fun bufferData(target: Int, buffer: Uint16Buffer, usage: Int) = gl.bufferData(target, (buffer as Uint16BufferImpl).buffer, usage, 0, buffer.limit)
    override fun bufferData(target: Int, buffer: Int32Buffer, usage: Int) = gl.bufferData(target, (buffer as Int32BufferImpl).buffer, usage, 0, buffer.limit)
    override fun bufferData(target: Int, buffer: Float32Buffer, usage: Int) = gl.bufferData(target, (buffer as Float32BufferImpl).buffer, usage, 0, buffer.limit)
    override fun bufferData(target: Int, buffer: MixedBuffer, usage: Int) = gl.bufferData(target, (buffer as MixedBufferImpl).buffer, usage, 0, buffer.limit)
    override fun checkFramebufferStatus(target: Int): Int = gl.checkFramebufferStatus(target)
    override fun clear(mask: Int) = gl.clear(mask)
    override fun clearBufferfv(buffer: Int, drawBuffer: Int, values: Float32Buffer) = gl.clearBufferfv(buffer, drawBuffer, (values as Float32BufferImpl).buffer)
    override fun clearColor(r: Float, g: Float, b: Float, a: Float) = gl.clearColor(r, g, b, a)
    override fun clearDepth(depth: Float) = gl.clearDepth(depth)
    override fun compileShader(shader: GlShader) = gl.compileShader(shader.webGl)
    override fun copyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int) = gl.copyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height)
    override fun createBuffer(): GlBuffer = GlBuffer(buffers.create(Unit))
    override fun createFramebuffer(): GlFramebuffer = GlFramebuffer(framebuffers.create(Unit))
    override fun createProgram(): GlProgram = GlProgram(programs.create(Unit))
    override fun createQuery(): GlQuery = GlQuery(queries.create(Unit))
    override fun createRenderbuffer(): GlRenderbuffer = GlRenderbuffer(renderbuffers.create(Unit))
    override fun createShader(type: Int): GlShader = GlShader(shaders.create(type))
    override fun createTexture(): GlTexture = GlTexture(textures.create(Unit))
    override fun createVertexArray(): GlVertexArrayObject = GlVertexArrayObject(vaos.create(Unit))
    override fun cullFace(mode: Int) = gl.cullFace(mode)
    override fun deleteBuffer(buffer: GlBuffer) = buffers.delete(buffer.handle)
    override fun deleteFramebuffer(framebuffer: GlFramebuffer) = framebuffers.delete(framebuffer.handle)
    override fun deleteProgram(program: GlProgram) = programs.delete(program.handle)
    override fun deleteQuery(query: GlQuery) = queries.delete(query.handle)
    override fun deleteRenderbuffer(renderbuffer: GlRenderbuffer) = renderbuffers.delete(renderbuffer.handle)
    override fun deleteShader(shader: GlShader) = shaders.delete(shader.handle)
    override fun deleteTexture(texture: GlTexture) = textures.delete(texture.handle)
    override fun deleteVertexArray(vao: GlVertexArrayObject) = vaos.delete(vao.handle)
    override fun depthFunc(func: Int) = gl.depthFunc(func)
    override fun depthMask(flag: Boolean) = gl.depthMask(flag)
    override fun disable(cap: Int) = gl.disable(cap)
    override fun disableVertexAttribArray(index: Int) = gl.disableVertexAttribArray(index)
    override fun dispatchCompute(numGroupsX: Int, numGroupsY: Int, numGroupsZ: Int) = notSupported("dispatchCompute")
    override fun drawBuffers(buffers: IntArray) = gl.drawBuffers(buffers)
    override fun drawElements(mode: Int, count: Int, type: Int) = gl.drawElements(mode, count, type, 0)
    override fun drawElementsInstanced(mode: Int, count: Int, type: Int, instanceCount: Int) = gl.drawElementsInstanced(mode, count, type, 0, instanceCount)
    override fun enable(cap: Int) = gl.enable(cap)
    override fun enableVertexAttribArray(index: Int) = gl.enableVertexAttribArray(index)
    override fun endQuery(target: Int) = gl.endQuery(target)
    override fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: GlRenderbuffer) = gl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.webGl)
    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: GlTexture, level: Int) = gl.framebufferTexture2D(target, attachment, textarget, texture.webGl, level)
    override fun generateMipmap(target: Int) = gl.generateMipmap(target)
    override fun getActiveUniformBlockParameter(program: GlProgram, uniformBlockIndex: Int, pName: Int): Int = gl.getActiveUniformBlockParameter(program.webGl, uniformBlockIndex, pName)
    override fun getActiveUniforms(program: GlProgram, uniformIndices: IntArray, pName: Int): IntArray = gl.getActiveUniforms(program.webGl, uniformIndices, pName)
    override fun getError(): Int = gl.getError()
    override fun getInteger(pName: Int): Int = gl.getParameter(pName) as Int
    override fun getProgramInfoLog(program: GlProgram): String = gl.getProgramInfoLog(program.webGl) ?: ""
    override fun getProgramParameter(program: GlProgram, param: Int): Any = gl.getProgramParameter(program.webGl, param) ?: 0
    override fun getQueryParameter(query: GlQuery, param: Int): Any = gl.getQueryParameter(query.webGl, param)
    override fun getQueryParameterU64(query: GlQuery, param: Int): Long = 0L
    override fun getShaderInfoLog(shader: GlShader): String = gl.getShaderInfoLog(shader.webGl) ?: ""
    override fun getShaderParameter(shader: GlShader, param: Int): Any = gl.getShaderParameter(shader.webGl, param) ?: 0
    override fun getUniformBlockIndex(program: GlProgram, uniformBlockName: String): Int = gl.getUniformBlockIndex(program.webGl, uniformBlockName)
    override fun getUniformIndices(program: GlProgram, names: Array<String>): IntArray = gl.getUniformIndices(program.webGl, names)
    override fun getUniformLocation(program: GlProgram, uniformName: String): Int = programs[program.handle]?.getUniformLocation(uniformName) ?: -1
    override fun lineWidth(width: Float) = gl.lineWidth(width)
    override fun linkProgram(program: GlProgram) = gl.linkProgram(program.webGl)
    override fun memoryBarrier(barriers: Int) = notSupported("memoryBarrier")
    override fun queryCounter(query: GlQuery, target: Int) = notSupported("queryCounter")
    override fun readBuffer(src: Int) = gl.readBuffer(src)
    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) = gl.renderbufferStorage(target, internalformat, width, height)
    override fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) = gl.renderbufferStorageMultisample(target, samples, internalformat, width, height)
    override fun scissor(x: Int, y: Int, width: Int, height: Int) = gl.scissor(x, y, width, height)
    override fun shaderSource(shader: GlShader, source: String) = gl.shaderSource(shader.webGl, source)
    override fun texImage1d(data: ImageData1d) = texImage2dImpl(TEXTURE_2D, data)
    override fun texImage2d(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Buffer?) = texImage2dImpl(target, level, internalformat, width, height, border, format, type, pixels)
    override fun texImage2d(target: Int, data: ImageData2d) = texImage2dImpl(target, data)
    override fun texImage3d(target: Int, data: ImageData3d) = textImage3dImpl(target, data)
    override fun texSubImage3d(target: Int, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, format: Int, type: Int, pixels: ImageData) = texSubImage3dImpl(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels)
    override fun texParameteri(target: Int, pName: Int, param: Int) = gl.texParameteri(target, pName, param)
    override fun texStorage2d(target: Int, levels: Int, internalformat: Int, width: Int, height: Int) = gl.texStorage2D(target, levels, internalformat, width, height)
    override fun texStorage3d(target: Int, levels: Int, internalformat: Int, width: Int, height: Int, depth: Int) = gl.texStorage3D(target, levels, internalformat, width, height, depth)
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
    override fun uniformMatrix2fv(location: Int, values: Float32Buffer) = gl.uniformMatrix2fv(location.webGlUniformLoc, false, (values as Float32BufferImpl).buffer)
    override fun uniformMatrix3fv(location: Int, values: Float32Buffer) = gl.uniformMatrix3fv(location.webGlUniformLoc, false, (values as Float32BufferImpl).buffer)
    override fun uniformMatrix4fv(location: Int, values: Float32Buffer) = gl.uniformMatrix4fv(location.webGlUniformLoc, false, (values as Float32BufferImpl).buffer)
    override fun vertexAttribDivisor(index: Int, divisor: Int) = gl.vertexAttribDivisor(index, divisor)
    override fun vertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) = gl.vertexAttribIPointer(index, size, type, stride, offset)
    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) = gl.vertexAttribPointer(index, size, type, normalized, stride, offset)
    override fun viewport(x: Int, y: Int, width: Int, height: Int) = gl.viewport(x, y, width, height)

    override fun clipControl(origin: Int, depth: Int) {
        check(clipControlExt != null) {
            "Missing required EXT_clip_control extension"
        }
        clipControlExt!!.clipControlEXT(origin, depth)
    }

    @Suppress("UnsafeCastFromDynamic")
    fun initWebGl(glCtx: WebGL2RenderingContext) {
        gl = glCtx

        logD { "Supported WebGL extensions:\n${gl.getSupportedExtensions()?.joinToString("\n")}" }

        // by getting the extension, it is automatically enabled, i.e. float formats become usable as texture formats
        if (gl.getExtension("EXT_color_buffer_float") == null) {
            js("alert(\"WebGL 2 implementation lacks support for float textures (EXT_color_buffer_float)\")")
            logE { "WebGL 2 implementation lacks support for float textures (EXT_color_buffer_float)" }
        }

        // check for anisotropic filtering support
        var maxAnisotropy = 1
        val extAnisotropic = gl.getExtension("EXT_texture_filter_anisotropic") ?:
                             gl.getExtension("MOZ_EXT_texture_filter_anisotropic") ?:
                             gl.getExtension("WEBKIT_EXT_texture_filter_anisotropic")
        if (extAnisotropic != null) {
            TEXTURE_MAX_ANISOTROPY_EXT = extAnisotropic.TEXTURE_MAX_ANISOTROPY_EXT
            maxAnisotropy = gl.getParameter(extAnisotropic.MAX_TEXTURE_MAX_ANISOTROPY_EXT) as Int
        }

        clipControlExt = gl.getExtension("EXT_clip_control")
        clipControlExt?.let {
            LOWER_LEFT = it.LOWER_LEFT_EXT
            UPPER_LEFT = it.UPPER_LEFT_EXT
            NEGATIVE_ONE_TO_ONE = it.NEGATIVE_ONE_TO_ONE_EXT
            ZERO_TO_ONE = it.ZERO_TO_ONE_EXT
        }

        val maxTexUnits = gl.getParameter(WebGLRenderingContext.MAX_TEXTURE_IMAGE_UNITS) as Int
        val canFastCopyTextures = false
        val hasClipControl = clipControlExt != null

        capabilities = GlCapabilities(
            maxTexUnits = maxTexUnits,
            maxAnisotropy = maxAnisotropy,
            canFastCopyTextures = canFastCopyTextures,
            hasClipControl = hasClipControl,
            hasTimestampQuery = false
        )
    }

    override fun readBuffer(gpuBuffer: GpuBufferGl, dstBuffer: Buffer): Boolean = false

    override fun readTexturePixels(src: LoadedTextureGl, dst: BufferedImageData): Boolean {
        if (src.target != TEXTURE_2D) {
            return false
        }

        val fb = createFramebuffer()
        bindFramebuffer(FRAMEBUFFER, fb)
        framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_2D, src.glTexture, 0)

        if (checkFramebufferStatus(FRAMEBUFFER) == FRAMEBUFFER_COMPLETE) {
            val format = gl.getParameter(WebGLRenderingContext.IMPLEMENTATION_COLOR_READ_FORMAT) as Int
            val type = gl.getParameter(WebGLRenderingContext.IMPLEMENTATION_COLOR_READ_TYPE) as Int
            gl.readPixels(0, 0, src.width, src.height, format, type, dst.arrayBufferView)
        } else {
            logE { "Failed reading pixels from framebuffer" }
        }
        deleteFramebuffer(fb)
        return true
    }

    private class WebGlObjList<T, P>(
        val factory: (P) -> T?,
        val deleter: (T?) -> Unit
    ) {
        val objs = mutableListOf<T?>()
        private val freeSlots = mutableListOf<Int>()

        operator fun get(index: Int) = objs.getOrNull(index)

        fun create(p: P): Int {
            val insertIdx = if (freeSlots.isEmpty()) -1 else freeSlots.removeLast()
            return if (insertIdx >= 0) {
                objs[insertIdx] = factory(p)
                insertIdx
            } else {
                objs += factory(p)
                objs.lastIndex
            }
        }

        fun delete(index: Int) {
            checkNotNull(objs[index])
            deleter(objs[index])
            objs[index] = null
            freeSlots += index
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

    private fun texImage2dImpl(target: Int, image: ImageData) {
        gl.pixelStorei(WebGLRenderingContext.UNPACK_COLORSPACE_CONVERSION_WEBGL, WebGLRenderingContext.NONE)
        when (image) {
            is BufferedImageData1d -> {
                gl.texImage2D(target, 0, image.format.glInternalFormat(this), image.width, 1, 0, image.format.glFormat(this), image.format.glType(this), image.arrayBufferView)
            }
            is BufferedImageData2d -> {
                gl.texImage2D(target, 0, image.format.glInternalFormat(this), image.width, image.height, 0, image.format.glFormat(this), image.format.glType(this), image.arrayBufferView)
            }
            is ImageTextureData -> {
                gl.texImage2D(target, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, image.data)
            }
            else -> error("Invalid ImageData type for texImage2d: $image")
        }
    }

    private fun textImage3dImpl(target: Int, image: ImageData) {
        when (image) {
            is BufferedImageData3d -> {
                gl.texImage3D(target, 0, image.format.glInternalFormat(this), image.width, image.height, image.depth, 0, image.format.glFormat(this), image.format.glType(this), image.arrayBufferView)
            }
            is ImageAtlasTextureData -> {
                gl.texStorage3D(target, 1, image.format.glInternalFormat(this), image.width, image.height, image.depth)
                for (z in 0 until image.depth) {
                    gl.texSubImage3D(target, 0, 0, 0, z, image.width, image.height, 1, image.format.glFormat(this), image.format.glType(this), image.data[z])
                }
            }
            else -> error("ImageData must be either BufferedImageData3d or ImageAtlasTextureData")
        }
    }

    private fun texSubImage3dImpl(target: Int, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, format: Int, type: Int, pixels: ImageData) {
        when (pixels) {
            is BufferedImageData -> {
                val arrayBufferView = (pixels.data as GenericBuffer<*>).buffer
                gl.texSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, arrayBufferView)
            }
            is ImageTextureData -> {
                gl.texSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels.data)
            }
            else -> error("Invalid ImageData type for texSubImage3D: $pixels")
        }
    }

    private fun notSupported(name: String): Nothing = error("$name is not supported in WebGL")

    private val BufferedImageData.arrayBufferView: ArrayBufferView get() = when (val bufData = data) {
        is Uint8BufferImpl -> bufData.buffer
        is Uint16BufferImpl -> bufData.buffer
        is Float32BufferImpl -> bufData.buffer
        else -> throw IllegalArgumentException("Unsupported buffer type")
    }
}