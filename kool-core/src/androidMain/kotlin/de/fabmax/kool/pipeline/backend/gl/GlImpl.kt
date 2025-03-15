package de.fabmax.kool.pipeline.backend.gl

import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLES32.*
import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

object GlImpl: GlApi {
    override val ARRAY_BUFFER = GL_ARRAY_BUFFER
    override val BACK = GL_BACK
    override val BLEND = GL_BLEND
    override val CLAMP_TO_EDGE = GL_CLAMP_TO_EDGE
    override val COLOR = GL_COLOR
    override val COLOR_ATTACHMENT0 = GL_COLOR_ATTACHMENT0
    override val COLOR_BUFFER_BIT = GL_COLOR_BUFFER_BIT
    override val COMPARE_REF_TO_TEXTURE = GL_COMPARE_REF_TO_TEXTURE
    override val COMPILE_STATUS = GL_COMPILE_STATUS
    override val COMPUTE_SHADER = GL_COMPUTE_SHADER
    override val CULL_FACE = GL_CULL_FACE
    override val DEPTH_ATTACHMENT = GL_DEPTH_ATTACHMENT
    override val DEPTH_BUFFER_BIT = GL_DEPTH_BUFFER_BIT
    override val DEPTH_COMPONENT24 = GL_DEPTH_COMPONENT24
    override val DEPTH_COMPONENT32F = GL_DEPTH_COMPONENT32F
    override val DEPTH_COMPONENT = GL_DEPTH_COMPONENT
    override val DEPTH_TEST = GL_DEPTH_TEST
    override val DRAW_FRAMEBUFFER = GL_DRAW_FRAMEBUFFER
    override val DYNAMIC_DRAW = GL_DYNAMIC_DRAW
    override val ELEMENT_ARRAY_BUFFER = GL_ELEMENT_ARRAY_BUFFER
    override val FRAGMENT_SHADER = GL_FRAGMENT_SHADER
    override val FRAMEBUFFER = GL_FRAMEBUFFER
    override val FRAMEBUFFER_COMPLETE = GL_FRAMEBUFFER_COMPLETE
    override val FRONT = GL_FRONT
    override val INVALID_INDEX = GL_INVALID_INDEX
    override val LINEAR = GL_LINEAR
    override val LINEAR_MIPMAP_LINEAR = GL_LINEAR_MIPMAP_LINEAR
    override val LINES = GL_LINES
    override val LINK_STATUS = GL_LINK_STATUS
    override val LOWER_LEFT = GlesExtensions.LOWER_LEFT_EXT
    override val MIRRORED_REPEAT = GL_MIRRORED_REPEAT
    override val NEAREST = GL_NEAREST
    override val NEAREST_MIPMAP_NEAREST = GL_NEAREST_MIPMAP_NEAREST
    override val NEGATIVE_ONE_TO_ONE = GlesExtensions.NEGATIVE_ONE_TO_ONE_EXT
    override val NONE = GL_NONE
    override val ONE = GL_ONE
    override val ONE_MINUS_SRC_ALPHA = GL_ONE_MINUS_SRC_ALPHA
    override val POINTS = GL_POINTS
    override val QUERY_RESULT = GL_QUERY_RESULT
    override val QUERY_RESULT_AVAILABLE = GL_QUERY_RESULT_AVAILABLE
    override val READ_FRAMEBUFFER = GL_READ_FRAMEBUFFER
    override val READ_ONLY = GL_READ_ONLY
    override val READ_WRITE = GL_READ_WRITE
    override val RENDERBUFFER = GL_RENDERBUFFER
    override val REPEAT = GL_REPEAT
    override val SAMPLES = GL_SAMPLES
    override val SCISSOR_TEST = GL_SCISSOR_TEST
    override val SHADER_STORAGE_BUFFER = GL_SHADER_STORAGE_BUFFER
    override val SRC_ALPHA = GL_SRC_ALPHA
    override val STATIC_DRAW = GL_STATIC_DRAW
    override val TEXTURE_1D = 0
    override val TEXTURE_2D = GL_TEXTURE_2D
    override val TEXTURE_2D_ARRAY = GL_TEXTURE_2D_ARRAY
    override val TEXTURE_3D = GL_TEXTURE_3D
    override val TEXTURE_BASE_LEVEL = GL_TEXTURE_BASE_LEVEL
    override val TEXTURE_COMPARE_MODE = GL_TEXTURE_COMPARE_MODE
    override val TEXTURE_COMPARE_FUNC = GL_TEXTURE_COMPARE_FUNC
    override val TEXTURE_CUBE_MAP = GL_TEXTURE_CUBE_MAP
    override val TEXTURE_CUBE_MAP_ARRAY = GL_TEXTURE_CUBE_MAP_ARRAY
    override val TEXTURE_CUBE_MAP_POSITIVE_X = GL_TEXTURE_CUBE_MAP_POSITIVE_X
    override val TEXTURE_CUBE_MAP_NEGATIVE_X = GL_TEXTURE_CUBE_MAP_NEGATIVE_X
    override val TEXTURE_CUBE_MAP_POSITIVE_Y = GL_TEXTURE_CUBE_MAP_POSITIVE_Y
    override val TEXTURE_CUBE_MAP_NEGATIVE_Y = GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
    override val TEXTURE_CUBE_MAP_POSITIVE_Z = GL_TEXTURE_CUBE_MAP_POSITIVE_Z
    override val TEXTURE_CUBE_MAP_NEGATIVE_Z = GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    override val TEXTURE_MAG_FILTER = GL_TEXTURE_MAG_FILTER
    override val TEXTURE_MAX_LEVEL = GL_TEXTURE_MAX_LEVEL
    override val TEXTURE_MAX_LOD = GL_TEXTURE_MAX_LOD
    override val TEXTURE_MIN_FILTER = GL_TEXTURE_MIN_FILTER
    override val TEXTURE_MIN_LOD = GL_TEXTURE_MIN_LOD
    override val TEXTURE_WRAP_R = GL_TEXTURE_WRAP_R
    override val TEXTURE_WRAP_S = GL_TEXTURE_WRAP_S
    override val TEXTURE_WRAP_T = GL_TEXTURE_WRAP_T
    override val TEXTURE0 = GL_TEXTURE0
    override val TIME_ELAPSED = GlesExtensions.TIME_ELAPSED_EXT
    override val TIMESTAMP = GlesExtensions.TIMESTAMP_EXT
    override val TRIANGLES = GL_TRIANGLES
    override val TRIANGLE_STRIP = GL_TRIANGLE_STRIP
    override val TRUE = GL_TRUE
    override val UNIFORM_BLOCK_DATA_SIZE = GL_UNIFORM_BLOCK_DATA_SIZE
    override val UNIFORM_BUFFER = GL_UNIFORM_BUFFER
    override val UNIFORM_OFFSET = GL_UNIFORM_OFFSET
    override val UPPER_LEFT = GlesExtensions.UPPER_LEFT_EXT
    override val VERTEX_SHADER = GL_VERTEX_SHADER
    override val WRITE_ONLY = GL_WRITE_ONLY
    override val ZERO_TO_ONE = GlesExtensions.ZERO_TO_ONE_EXT

    override val INT = GL_INT
    override val FLOAT = GL_FLOAT
    override val UNSIGNED_BYTE = GL_UNSIGNED_BYTE
    override val UNSIGNED_INT = GL_UNSIGNED_INT

    override val RED = GL_RED
    override val RG = GL_RG
    override val RGB = GL_RGB
    override val RGBA = GL_RGBA
    override val RED_INTEGER = GL_RED_INTEGER
    override val RG_INTEGER = GL_RG_INTEGER
    override val RGB_INTEGER = GL_RGB_INTEGER
    override val RGBA_INTEGER = GL_RGBA_INTEGER

    override val R8 = GL_R8
    override val RG8 = GL_RG8
    override val RGB8 = GL_RGB8
    override val RGBA8 = GL_RGBA8
    override val R16F = GL_R16F
    override val RG16F = GL_RG16F
    override val RGB16F = GL_RGB16F
    override val RGBA16F = GL_RGBA16F
    override val R32F = GL_R32F
    override val RG32F = GL_RG32F
    override val RGB32F = GL_RGB32F
    override val RGBA32F = GL_RGBA32F
    override val R32I = GL_R32I
    override val RG32I = GL_RG32I
    override val RGB32I = GL_RGB32I
    override val RGBA32I = GL_RGBA32I
    override val R32UI = GL_R32UI
    override val RG32UI = GL_RG32UI
    override val RGB32UI = GL_RGB32UI
    override val RGBA32UI = GL_RGBA32UI
    override val R11F_G11F_B10F = GL_R11F_G11F_B10F

    override val ALWAYS = GL_ALWAYS
    override val NEVER = GL_NEVER
    override val LESS = GL_LESS
    override val LEQUAL = GL_LEQUAL
    override val GREATER = GL_GREATER
    override val GEQUAL = GL_GEQUAL
    override val EQUAL = GL_EQUAL
    override val NOTEQUAL = GL_NOTEQUAL

    override val VERTEX_ATTRIB_ARRAY_BARRIER_BIT = GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT
    override val ELEMENT_ARRAY_BARRIER_BIT = GL_ELEMENT_ARRAY_BARRIER_BIT
    override val UNIFORM_BARRIER_BIT = GL_UNIFORM_BARRIER_BIT
    override val TEXTURE_FETCH_BARRIER_BIT = GL_TEXTURE_FETCH_BARRIER_BIT
    override val SHADER_IMAGE_ACCESS_BARRIER_BIT = GL_SHADER_IMAGE_ACCESS_BARRIER_BIT
    override val COMMAND_BARRIER_BIT = GL_COMMAND_BARRIER_BIT
    override val PIXEL_BUFFER_BARRIER_BIT = GL_PIXEL_BUFFER_BARRIER_BIT
    override val TEXTURE_UPDATE_BARRIER_BIT = GL_TEXTURE_UPDATE_BARRIER_BIT
    override val BUFFER_UPDATE_BARRIER_BIT = GL_BUFFER_UPDATE_BARRIER_BIT
    override val CLIENT_MAPPED_BUFFER_BARRIER_BIT = 0
    override val FRAMEBUFFER_BARRIER_BIT = GL_FRAMEBUFFER_BARRIER_BIT
    override val TRANSFORM_FEEDBACK_BARRIER_BIT = GL_TRANSFORM_FEEDBACK_BARRIER_BIT
    override val ATOMIC_COUNTER_BARRIER_BIT = GL_ATOMIC_COUNTER_BARRIER_BIT
    override val SHADER_STORAGE_BARRIER_BIT = GL_SHADER_STORAGE_BARRIER_BIT
    override val QUERY_BUFFER_BARRIER_BIT = 0

    override val DEFAULT_FRAMEBUFFER: GlFramebuffer = GlFramebuffer(GL_NONE)
    override val NULL_BUFFER: GlBuffer = GlBuffer(GL_NONE)
    override val NULL_TEXTURE: GlTexture = GlTexture(0)
    override val NULL_VAO: GlVertexArrayObject = GlVertexArrayObject(0)

    override var TEXTURE_MAX_ANISOTROPY_EXT = 0
        private set

    override lateinit var version: GlApiVersion
        private set
    override lateinit var capabilities: GlCapabilities
        private set

    private lateinit var backend: RenderBackendGlImpl

    private val intParam = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()

    override fun activeTexture(texture: Int) = glActiveTexture(texture)
    override fun attachShader(program: GlProgram, shader: GlShader) = glAttachShader(program.handle, shader.handle)
    override fun beginQuery(target: Int, query: GlQuery) = glBeginQuery(target, query.handle)
    override fun bindBuffer(target: Int, buffer: GlBuffer) = glBindBuffer(target, buffer.handle)
    override fun bindBufferBase(target: Int, index: Int, buffer: GlBuffer) = glBindBufferBase(target, index, buffer.handle)
    override fun bindFramebuffer(target: Int, framebuffer: GlFramebuffer) = glBindFramebuffer(target, framebuffer.handle)
    override fun bindImageTexture(unit: Int, texture: GlTexture, level: Int, layered: Boolean, layer: Int, access: Int, format: Int) = glBindImageTexture(unit, texture.handle, level, layered, layer, access, format)
    override fun bindRenderbuffer(target: Int, renderbuffer: GlRenderbuffer) = glBindRenderbuffer(target, renderbuffer.handle)
    override fun bindTexture(target: Int, texture: GlTexture) = glBindTexture(target, texture.handle)
    override fun bindVertexArray(vao: GlVertexArrayObject) = glBindVertexArray(vao.handle)
    override fun blendFunc(sFactor: Int, dFactor: Int) = glBlendFunc(sFactor, dFactor)
    override fun blitFramebuffer(srcX0: Int, srcY0: Int, srcX1: Int, srcY1: Int, dstX0: Int, dstY0: Int, dstX1: Int, dstY1: Int, mask: Int, filter: Int) = glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter)
    override fun bufferData(target: Int, size: Int, usage: Int) = glBufferData(target, size, null, usage)
    override fun bufferData(target: Int, buffer: Uint8Buffer, usage: Int) = buffer.useRaw { glBufferData(target, it.limit(), it, usage) }
    override fun bufferData(target: Int, buffer: Uint16Buffer, usage: Int) = buffer.useRaw { glBufferData(target, it.limit() * 2, it, usage) }
    override fun bufferData(target: Int, buffer: Int32Buffer, usage: Int) = buffer.useRaw { glBufferData(target, it.limit() * 4, it, usage) }
    override fun bufferData(target: Int, buffer: Float32Buffer, usage: Int) = buffer.useRaw { glBufferData(target, it.limit() * 4, it, usage) }
    override fun bufferData(target: Int, buffer: MixedBuffer, usage: Int) = buffer.useRaw { glBufferData(target, it.limit(), it, usage) }
    override fun checkFramebufferStatus(target: Int): Int = glCheckFramebufferStatus(target)
    override fun clear(mask: Int) = glClear(mask)
    override fun clearBufferfv(buffer: Int, drawBuffer: Int, values: Float32Buffer) = values.useRaw { glClearBufferfv(buffer, drawBuffer, it) }
    override fun clearColor(r: Float, g: Float, b: Float, a: Float) = glClearColor(r, g, b, a)
    override fun clearDepth(depth: Float) = glClearDepthf(depth)
    override fun clipControl(origin: Int, depth: Int) = GlesExtensions.clipControl(origin, depth)
    override fun createBuffer(): GlBuffer = GlBuffer(receiveInt { glGenBuffers(1, it) })
    override fun createFramebuffer(): GlFramebuffer = GlFramebuffer(receiveInt { glGenFramebuffers(1, it) })
    override fun createProgram(): GlProgram = GlProgram(glCreateProgram())
    override fun createQuery(): GlQuery = GlQuery(receiveInt { glGenQueries(1, it) })
    override fun createRenderbuffer(): GlRenderbuffer = GlRenderbuffer(receiveInt { glGenRenderbuffers(1, it) })
    override fun createShader(type: Int): GlShader = GlShader(glCreateShader(type))
    override fun createTexture(): GlTexture = GlTexture(receiveInt { glGenTextures(1, it) })
    override fun createVertexArray(): GlVertexArrayObject = GlVertexArrayObject(receiveInt { glGenVertexArrays(1, it) })
    override fun compileShader(shader: GlShader) = glCompileShader(shader.handle)
    override fun copyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int) = glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height)
    override fun cullFace(mode: Int) = glCullFace(mode)
    override fun deleteBuffer(buffer: GlBuffer) = glDeleteBuffers(1, sendInt(buffer.handle))
    override fun deleteFramebuffer(framebuffer: GlFramebuffer) = glDeleteFramebuffers(1, sendInt(framebuffer.handle))
    override fun deleteProgram(program: GlProgram) = glDeleteProgram(program.handle)
    override fun deleteQuery(query: GlQuery) = glDeleteQueries(1, sendInt(query.handle))
    override fun deleteRenderbuffer(renderbuffer: GlRenderbuffer) = glDeleteRenderbuffers(1, sendInt(renderbuffer.handle))
    override fun deleteShader(shader: GlShader) = glDeleteShader(shader.handle)
    override fun deleteTexture(texture: GlTexture) = glDeleteTextures(1, sendInt(texture.handle))
    override fun deleteVertexArray(vao: GlVertexArrayObject) = GLES30.glDeleteVertexArrays(1, sendInt(vao.handle))
    override fun depthFunc(func: Int) = glDepthFunc(func)
    override fun depthMask(flag: Boolean) = glDepthMask(flag)
    override fun disable(cap: Int) = glDisable(cap)
    override fun disableVertexAttribArray(index: Int) = glDisableVertexAttribArray(index)
    override fun dispatchCompute(numGroupsX: Int, numGroupsY: Int, numGroupsZ: Int) = glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ)
    override fun drawBuffers(buffers: IntArray) = glDrawBuffers(buffers.size, buffers, 0)
    override fun drawElements(mode: Int, count: Int, type: Int) = glDrawElements(mode, count, type, 0)
    override fun drawElementsInstanced(mode: Int, count: Int, type: Int, instanceCount: Int) = glDrawElementsInstanced(mode, count, type, 0, instanceCount)
    override fun enable(cap: Int) = glEnable(cap)
    override fun enableVertexAttribArray(index: Int) = glEnableVertexAttribArray(index)
    override fun endQuery(target: Int) = glEndQuery(target)
    override fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: GlRenderbuffer) = glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.handle)
    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: GlTexture, level: Int) = glFramebufferTexture2D(target, attachment, textarget, texture.handle, level)
    override fun generateMipmap(target: Int) = glGenerateMipmap(target)
    override fun getActiveUniformBlockParameter(program: GlProgram, uniformBlockIndex: Int, pName: Int): Int = receiveInt { glGetActiveUniformBlockiv(program.handle, uniformBlockIndex, pName, it) }
    override fun getActiveUniforms(program: GlProgram, uniformIndices: IntArray, pName: Int): IntArray = getActiveUniformsImpl(program, uniformIndices, pName)
    override fun getError(): Int = glGetError()
    override fun getInteger(pName: Int): Int = receiveInt { glGetIntegerv(pName, it) }
    override fun getProgramInfoLog(program: GlProgram): String = glGetProgramInfoLog(program.handle)
    override fun getProgramParameter(program: GlProgram, param: Int): Int = receiveInt { glGetProgramiv(program.handle, param, it) }
    override fun getQueryParameter(query: GlQuery, param: Int): Any = receiveInt { glGetQueryObjectuiv(query.handle, param, it) }
    override fun getQueryParameterU64(query: GlQuery, param: Int): Long = GlesExtensions.getQueryObjectui64(query.handle, param)
    override fun getShaderInfoLog(shader: GlShader): String = glGetShaderInfoLog(shader.handle)
    override fun getShaderParameter(shader: GlShader, param: Int): Int = receiveInt { glGetShaderiv(shader.handle, param, it) }
    override fun getUniformBlockIndex(program: GlProgram, uniformBlockName: String): Int = glGetUniformBlockIndex(program.handle, uniformBlockName)
    override fun getUniformIndices(program: GlProgram, names: Array<String>): IntArray = getUniformIndicesImpl(program, names)
    override fun getUniformLocation(program: GlProgram, uniformName: String): Int = glGetUniformLocation(program.handle, uniformName)
    override fun lineWidth(width: Float) = glLineWidth(width)
    override fun linkProgram(program: GlProgram) = glLinkProgram(program.handle)
    override fun memoryBarrier(barriers: Int) = glMemoryBarrier(barriers)
    override fun queryCounter(query: GlQuery, target: Int) = GlesExtensions.queryCounter(query.handle, target)
    override fun readBuffer(src: Int) = glReadBuffer(src)
    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) = glRenderbufferStorage(target, internalformat, width, height)
    override fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) = glRenderbufferStorageMultisample(target, samples, internalformat, width, height)
    override fun scissor(x: Int, y: Int, width: Int, height: Int) = glScissor(x, y, width, height)
    override fun shaderSource(shader: GlShader, source: String) = glShaderSource(shader.handle, source)
    override fun texImage1d(data: ImageData1d) = texImage2dImpl(GL_TEXTURE_2D, data)
    override fun texImage2d(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Buffer?) = texImage2dImpl(target, level, internalformat, width, height, border, format, type, pixels)
    override fun texImage2d(target: Int, data: ImageData2d) = texImage2dImpl(target, data)
    override fun texImage3d(target: Int, data: ImageData3d) = texImage3dImpl(target, data)
    override fun texSubImage3d(target: Int, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, format: Int, type: Int, pixels: ImageData) = texSubImage3dImpl(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels)
    override fun texParameteri(target: Int, pName: Int, param: Int) = glTexParameteri(target, pName, param)
    override fun texStorage2d(target: Int, levels: Int, internalformat: Int, width: Int, height: Int) = glTexStorage2D(target, levels, internalformat, width, height)
    override fun texStorage3d(target: Int, levels: Int, internalformat: Int, width: Int, height: Int, depth: Int) = glTexStorage3D(target, levels, internalformat, width, height, depth)
    override fun uniformBlockBinding(program: GlProgram, uniformBlockIndex: Int, uniformBlockBinding: Int) = glUniformBlockBinding(program.handle, uniformBlockIndex, uniformBlockBinding)
    override fun useProgram(program: GlProgram) = glUseProgram(program.handle)
    override fun uniform1f(location: Int, x: Float) = glUniform1f(location, x)
    override fun uniform2f(location: Int, x: Float, y: Float) = glUniform2f(location, x, y)
    override fun uniform3f(location: Int, x: Float, y: Float, z: Float) = glUniform3f(location, x, y, z)
    override fun uniform4f(location: Int, x: Float, y: Float, z: Float, w: Float) = glUniform4f(location, x, y, z, w)
    override fun uniform1fv(location: Int, values: Float32Buffer) = values.useRaw { glUniform1fv(location, it.limit(), it) }
    override fun uniform2fv(location: Int, values: Float32Buffer) = values.useRaw { glUniform2fv(location, it.limit(), it) }
    override fun uniform3fv(location: Int, values: Float32Buffer) = values.useRaw { glUniform3fv(location, it.limit(), it) }
    override fun uniform4fv(location: Int, values: Float32Buffer) = values.useRaw { glUniform4fv(location, it.limit(), it) }
    override fun uniform1i(location: Int, x: Int) = glUniform1i(location, x)
    override fun uniform2i(location: Int, x: Int, y: Int) = glUniform2i(location, x, y)
    override fun uniform3i(location: Int, x: Int, y: Int, z: Int) = glUniform3i(location, x, y, z)
    override fun uniform4i(location: Int, x: Int, y: Int, z: Int, w: Int) = glUniform4i(location, x, y, z, w)
    override fun uniform1iv(location: Int, values: Int32Buffer) = values.useRaw { glUniform1iv(location, it.limit(), it) }
    override fun uniform2iv(location: Int, values: Int32Buffer) = values.useRaw { glUniform2iv(location, it.limit(), it) }
    override fun uniform3iv(location: Int, values: Int32Buffer) = values.useRaw { glUniform3iv(location, it.limit(), it) }
    override fun uniform4iv(location: Int, values: Int32Buffer) = values.useRaw { glUniform4iv(location, it.limit(), it) }
    override fun uniformMatrix2fv(location: Int, values: Float32Buffer) = values.useRaw { glUniformMatrix2fv(location, it.limit(), false, it) }
    override fun uniformMatrix3fv(location: Int, values: Float32Buffer) = values.useRaw { glUniformMatrix3fv(location, it.limit(), false, it) }
    override fun uniformMatrix4fv(location: Int, values: Float32Buffer) = values.useRaw { glUniformMatrix4fv(location, it.limit(), false, it) }
    override fun vertexAttribDivisor(index: Int, divisor: Int) = glVertexAttribDivisor(index, divisor)
    override fun vertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) = glVertexAttribIPointer(index, size, type, stride, offset)
    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) = glVertexAttribPointer(index, size, type, normalized, stride, offset)
    override fun viewport(x: Int, y: Int, width: Int, height: Int) = glViewport(x, y, width, height)

    fun initOpenGl(backend: RenderBackendGlImpl) {
        this.backend = backend
        this.version = checkApiVersion()

        // check for anisotropic texture filtering support
        val extensions = glGetString(GL_EXTENSIONS)?.split(" ")?.toSet() ?: emptySet()
        //extensions.filter { it.isNotBlank() }.sorted().forEach { logD { "ext: $it" } }

        var maxAnisotropy = 1
        if ("GL_EXT_texture_filter_anisotropic" in extensions) {
            val f = FloatArray(1)
            glGetFloatv(GLES11Ext.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, f, 0)
            maxAnisotropy = f[0].toInt()
            TEXTURE_MAX_ANISOTROPY_EXT = GLES11Ext.GL_TEXTURE_MAX_ANISOTROPY_EXT
            logD { "Anisotropic filtering available, max anisotropy: ${f[0]}" }
        }

        var hasTimestampQuery = false
        if ("GL_EXT_disjoint_timer_query" in extensions) {
            hasTimestampQuery = GlesExtensions.enableEXTdisjointTimerQuery()
            logD { "Enabled extension GL_EXT_disjoint_timer_query: $hasTimestampQuery" }
        }

        var hasClipControl = false
        if ("GL_EXT_clip_control" in extensions) {
            hasClipControl = GlesExtensions.enableEXTclipControl()
            logD { "Enabled extension GL_EXT_clip_control: $hasClipControl" }
        }

        val maxTexUnits = getInteger(GL_MAX_TEXTURE_IMAGE_UNITS)
        val canFastCopyTextures = false

        val hasComputeShaders = version.isHigherOrEqualThan(3, 1)
        val workGroupCount = MutableVec3i()
        val workGroupSize = MutableVec3i()
        var maxInvocations = 0
        if (hasComputeShaders) {
            val r = intParam
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 0, r).also { workGroupCount.x = r[0] }
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 1, r).also { workGroupCount.y = r[0] }
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 2, r).also { workGroupCount.z = r[0] }

            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 0, r).also { workGroupSize.x = r[0] }
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 1, r).also { workGroupSize.y = r[0] }
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 2, r).also { workGroupSize.z = r[0] }
            maxInvocations = getInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS)

            logD { "Compute shader support available. Max workgroups: $workGroupCount, max workgroup size: $workGroupSize, max invocations per workgroup: $maxInvocations" }
        }

        capabilities = GlCapabilities(
            maxTexUnits = maxTexUnits,
            maxAnisotropy = maxAnisotropy,
            canFastCopyTextures = canFastCopyTextures,
            hasClipControl = hasClipControl,
            hasTimestampQuery = hasTimestampQuery,
            hasComputeShaders = hasComputeShaders,
            maxWorkGroupCount = workGroupCount,
            maxWorkGroupSize = workGroupSize,
            maxWorkGroupInvocations = maxInvocations
        )
    }

    private fun checkApiVersion(): GlApiVersion {
        val versionStr = glGetString(GL_VERSION) ?: ""
        val match = Regex("\\D*([0-9])\\.([0-9]).*").matchEntire(versionStr)
        if (match == null) {
            logE { "Failed parsing OpenGLES version string: \"$versionStr\" - assuming version 3.0" }
        }

        val major = match?.groups?.get(1)?.value?.toInt() ?: 3
        val minor = match?.groups?.get(2)?.value?.toInt() ?: 0

        val deviceName = glGetString(GL_RENDERER) ?: "<unknown>"
        logI { "Detected OpenGLES version $major.$minor, device: $deviceName" }
        return GlApiVersion(major, minor, GlFlavor.OpenGLES, deviceName)
    }

    override fun readBuffer(gpuBuffer: GpuBufferGl, dstBuffer: Buffer): Boolean {
        gpuBuffer.bind()
        val bytes = glMapBufferRange(gpuBuffer.target, 0, dstBuffer.capacity * 4, GL_READ_ONLY) as ByteBuffer
        when (dstBuffer) {
            is Uint8BufferImpl -> dstBuffer.useRaw { it.put(bytes) }
            is Uint16BufferImpl -> dstBuffer.useRaw { it.put(bytes.asShortBuffer()) }
            is Int32BufferImpl -> dstBuffer.useRaw { it.put(bytes.asIntBuffer()) }
            is Float32BufferImpl -> dstBuffer.useRaw { it.put(bytes.asFloatBuffer()) }
            is MixedBufferImpl -> dstBuffer.useRaw { it.put(bytes) }
            else -> logE { "Unexpected buffer type: ${dstBuffer::class.simpleName}" }
        }
        glUnmapBuffer(gpuBuffer.target)
        return true
    }

    override fun readTexturePixels(src: LoadedTextureGl, dst: BufferedImageData): Boolean {
        TODO("Not yet implemented")
    }

    private inline fun receiveInt(block: (IntBuffer) -> Unit): Int {
        intParam.position(0)
        block(intParam)
        return intParam.get(0)
    }

    private fun sendInt(value: Int): IntBuffer {
        intParam.position(0)
        intParam.put(0, value)
        return intParam
    }

    private fun getActiveUniformsImpl(program: GlProgram, uniformIndices: IntArray, pName: Int): IntArray {
        val offsets = IntArray(uniformIndices.size)
        glGetActiveUniformsiv(program.handle, uniformIndices.size, uniformIndices, 0, pName, offsets, 0)
        return offsets
    }

    private fun getUniformIndicesImpl(program: GlProgram, names: Array<String>): IntArray {
        val indices = IntArray(names.size)
        glGetUniformIndices(program.handle, names, indices, 0)
        return indices
    }

    private fun texImage2dImpl(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Buffer?) {
        when (pixels) {
            is Uint8BufferImpl -> pixels.useRaw { glTexImage2D(target, level, internalformat, width, height, border, format, type, it) }
            is Uint16BufferImpl -> pixels.useRaw { glTexImage2D(target, level, internalformat, width, height, border, format, type, it) }
            is Int32BufferImpl -> pixels.useRaw { glTexImage2D(target, level, internalformat, width, height, border, format, type, it) }
            is Float32BufferImpl -> pixels.useRaw { glTexImage2D(target, level, internalformat, width, height, border, format, type, it) }
            else -> glTexImage2D(target, level, internalformat, width, height, border, format, type, null as ByteBuffer?)
        }
    }

    private fun texImage2dImpl(target: Int, data: ImageData) {
        when (data) {
            is BufferedImageData1d -> when (val buf = data.data) {
                is Uint8BufferImpl -> buf.useRaw {
                    glTexImage2D(target, 0, data.format.glInternalFormat(this), data.width, 1, 0, data.format.glFormat(this), data.format.glType(this), it)
                }
                is Uint16BufferImpl -> buf.useRaw {
                    glTexImage2D(target, 0, data.format.glInternalFormat(this), data.width, 1, 0, data.format.glFormat(this), data.format.glType(this), it)
                }
                is Int32BufferImpl -> buf.useRaw {
                    glTexImage2D(target, 0, data.format.glInternalFormat(this), data.width, 1, 0, data.format.glFormat(this), data.format.glType(this), it)
                }
                is Float32BufferImpl -> buf.useRaw {
                    glTexImage2D(target, 0, data.format.glInternalFormat(this), data.width, 1, 0, data.format.glFormat(this), data.format.glType(this), it)
                }
                else -> error("ImageData buffer must be any of Uint8Buffer, Uint16Buffer, Int32Buffer, Float32Buffer")
            }
            is BufferedImageData2d -> when (val buf = data.data) {
                is Uint8BufferImpl -> buf.useRaw {
                    glTexImage2D(target, 0, data.format.glInternalFormat(this), data.width, data.height, 0, data.format.glFormat(this), data.format.glType(this), it)
                }
                is Uint16BufferImpl -> buf.useRaw {
                    glTexImage2D(target, 0, data.format.glInternalFormat(this), data.width, data.height, 0, data.format.glFormat(this), data.format.glType(this), it)
                }
                is Int32BufferImpl -> buf.useRaw {
                    glTexImage2D(target, 0, data.format.glInternalFormat(this), data.width, data.height, 0, data.format.glFormat(this), data.format.glType(this), it)
                }
                is Float32BufferImpl -> buf.useRaw {
                    glTexImage2D(target, 0, data.format.glInternalFormat(this), data.width, data.height, 0, data.format.glFormat(this), data.format.glType(this), it)
                }
                else -> error("ImageData buffer must be any of Uint8Buffer, Uint16Buffer, Int32Buffer, Float32Buffer")
            }
            else -> error("Invalid ImageData type for texImage2d: $data")
        }
    }

    private fun texImage3dImpl(target: Int, img: ImageData3d) {
        check(img is BufferedImageData3d) { "Invalid ImageData type for texImage3d: $img" }
        when (val buf = img.data) {
            is Uint8BufferImpl -> buf.useRaw {
                glTexImage3D(target, 0, img.format.glInternalFormat(this), img.width, img.height, img.depth, 0, img.format.glFormat(this), img.format.glType(this), it)
            }
            is Uint16BufferImpl -> buf.useRaw {
                glTexImage3D(target, 0, img.format.glInternalFormat(this), img.width, img.height, img.depth, 0, img.format.glFormat(this), img.format.glType(this), it)
            }
            is Int32BufferImpl -> buf.useRaw {
                glTexImage3D(target, 0, img.format.glInternalFormat(this), img.width, img.height, img.depth, 0, img.format.glFormat(this), img.format.glType(this), it)
            }
            is Float32BufferImpl -> buf.useRaw {
                glTexImage3D(target, 0, img.format.glInternalFormat(this), img.width, img.height, img.depth, 0, img.format.glFormat(this), img.format.glType(this), it)
            }
            else -> error("ImageData buffer must be either any of Uint8Buffer, Uint16Buffer, Int32Buffer, Float32Buffer")
        }
    }

    private fun texSubImage3dImpl(target: Int, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, format: Int, type: Int, pixels: ImageData) {
        when (val data = (pixels as BufferedImageData).data) {
            is Uint8BufferImpl -> data.useRaw { glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, it) }
            is Uint16BufferImpl -> data.useRaw { glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, it) }
            is Int32BufferImpl -> data.useRaw { glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, it) }
            is Float32BufferImpl -> data.useRaw { glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, it) }
            else -> error("Unsupported buffer type: $pixels")
        }
    }
}