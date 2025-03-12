package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import kotlin.jvm.JvmInline

@JvmInline
value class GlBuffer(val handle: Int)
@JvmInline
value class GlFramebuffer(val handle: Int)
@JvmInline
value class GlProgram(val handle: Int)
@JvmInline
value class GlQuery(val handle: Int)
@JvmInline
value class GlRenderbuffer(val handle: Int)
@JvmInline
value class GlShader(val handle: Int)
@JvmInline
value class GlTexture(val handle: Int)
@JvmInline
value class GlVertexArrayObject(val handle: Int)

interface GlApi {
    val ARRAY_BUFFER: Int
    val BACK: Int
    val BLEND: Int
    val CLAMP_TO_EDGE: Int
    val COLOR: Int
    val COLOR_ATTACHMENT0: Int
    val COLOR_BUFFER_BIT: Int
    val COMPARE_REF_TO_TEXTURE: Int
    val COMPILE_STATUS: Int
    val COMPUTE_SHADER: Int
    val CULL_FACE: Int
    val DEPTH_ATTACHMENT: Int
    val DEPTH_BUFFER_BIT: Int
    val DEPTH_COMPONENT24: Int
    val DEPTH_COMPONENT32F: Int
    val DEPTH_COMPONENT: Int
    val DEPTH_TEST: Int
    val DRAW_FRAMEBUFFER: Int
    val DYNAMIC_DRAW: Int
    val ELEMENT_ARRAY_BUFFER: Int
    val FRAGMENT_SHADER: Int
    val FRAMEBUFFER: Int
    val FRAMEBUFFER_COMPLETE: Int
    val FRONT: Int
    val INVALID_INDEX: Int
    val LINEAR: Int
    val LINEAR_MIPMAP_LINEAR: Int
    val LINES: Int
    val LINK_STATUS: Int
    val LOWER_LEFT: Int
    val MIRRORED_REPEAT: Int
    val NEAREST: Int
    val NEAREST_MIPMAP_NEAREST: Int
    val NEGATIVE_ONE_TO_ONE: Int
    val NONE: Int
    val ONE: Int
    val ONE_MINUS_SRC_ALPHA: Int
    val POINTS: Int
    val QUERY_RESULT: Int
    val QUERY_RESULT_AVAILABLE: Int
    val READ_FRAMEBUFFER: Int
    val READ_ONLY: Int
    val READ_WRITE: Int
    val RENDERBUFFER: Int
    val REPEAT: Int
    val SAMPLES: Int
    val SCISSOR_TEST: Int
    val SHADER_STORAGE_BUFFER: Int
    val SRC_ALPHA: Int
    val STATIC_DRAW: Int
    val TEXTURE_1D: Int
    val TEXTURE_2D: Int
    val TEXTURE_2D_ARRAY: Int
    val TEXTURE_3D: Int
    val TEXTURE_BASE_LEVEL: Int
    val TEXTURE_COMPARE_MODE: Int
    val TEXTURE_COMPARE_FUNC: Int
    val TEXTURE_CUBE_MAP: Int
    val TEXTURE_CUBE_MAP_ARRAY: Int
    val TEXTURE_CUBE_MAP_POSITIVE_X: Int
    val TEXTURE_CUBE_MAP_NEGATIVE_X: Int
    val TEXTURE_CUBE_MAP_POSITIVE_Y: Int
    val TEXTURE_CUBE_MAP_NEGATIVE_Y: Int
    val TEXTURE_CUBE_MAP_POSITIVE_Z: Int
    val TEXTURE_CUBE_MAP_NEGATIVE_Z: Int
    val TEXTURE_MAG_FILTER: Int
    val TEXTURE_MAX_LEVEL: Int
    val TEXTURE_MAX_LOD: Int
    val TEXTURE_MIN_FILTER: Int
    val TEXTURE_MIN_LOD: Int
    val TEXTURE_WRAP_R: Int
    val TEXTURE_WRAP_S: Int
    val TEXTURE_WRAP_T: Int
    val TEXTURE0: Int
    val TIME_ELAPSED: Int
    val TIMESTAMP: Int
    val TRIANGLES: Int
    val TRIANGLE_STRIP: Int
    val TRUE: Any
    val UNIFORM_BLOCK_DATA_SIZE: Int
    val UNIFORM_BUFFER: Int
    val UNIFORM_OFFSET: Int
    val UPPER_LEFT: Int
    val VERTEX_SHADER: Int
    val WRITE_ONLY: Int
    val ZERO_TO_ONE: Int

    val INT: Int
    val FLOAT: Int
    val UNSIGNED_BYTE: Int
    val UNSIGNED_INT: Int

    val RED: Int
    val RG: Int
    val RGB: Int
    val RGBA: Int
    val RED_INTEGER: Int
    val RG_INTEGER: Int
    val RGB_INTEGER: Int
    val RGBA_INTEGER: Int

    val R8: Int
    val RG8: Int
    val RGB8: Int
    val RGBA8: Int
    val R16F: Int
    val RG16F: Int
    val RGB16F: Int
    val RGBA16F: Int
    val R32F: Int
    val RG32F: Int
    val RGB32F: Int
    val RGBA32F: Int
    val R32I: Int
    val RG32I: Int
    val RGB32I: Int
    val RGBA32I: Int
    val R32UI: Int
    val RG32UI: Int
    val RGB32UI: Int
    val RGBA32UI: Int
    val R11F_G11F_B10F: Int

    val ALWAYS: Int
    val NEVER: Int
    val LESS: Int
    val LEQUAL: Int
    val GREATER: Int
    val GEQUAL: Int
    val EQUAL: Int
    val NOTEQUAL: Int

    val VERTEX_ATTRIB_ARRAY_BARRIER_BIT: Int
    val ELEMENT_ARRAY_BARRIER_BIT: Int
    val UNIFORM_BARRIER_BIT: Int
    val TEXTURE_FETCH_BARRIER_BIT: Int
    val SHADER_IMAGE_ACCESS_BARRIER_BIT: Int
    val COMMAND_BARRIER_BIT: Int
    val PIXEL_BUFFER_BARRIER_BIT: Int
    val TEXTURE_UPDATE_BARRIER_BIT: Int
    val BUFFER_UPDATE_BARRIER_BIT: Int
    val CLIENT_MAPPED_BUFFER_BARRIER_BIT: Int
    val FRAMEBUFFER_BARRIER_BIT: Int
    val TRANSFORM_FEEDBACK_BARRIER_BIT: Int
    val ATOMIC_COUNTER_BARRIER_BIT: Int
    val SHADER_STORAGE_BARRIER_BIT: Int
    val QUERY_BUFFER_BARRIER_BIT: Int

    val DEFAULT_FRAMEBUFFER: GlFramebuffer
    val NULL_BUFFER: GlBuffer
    val NULL_TEXTURE: GlTexture
    val NULL_VAO: GlVertexArrayObject

    val TEXTURE_MAX_ANISOTROPY_EXT: Int

    val version: GlApiVersion
    val capabilities: GlCapabilities

    fun activeTexture(texture: Int)
    fun attachShader(program: GlProgram, shader: GlShader)
    fun beginQuery(target: Int, query: GlQuery)
    fun bindBuffer(target: Int, buffer: GlBuffer)
    fun bindBufferBase(target: Int, index: Int, buffer: GlBuffer)
    fun bindFramebuffer(target: Int, framebuffer: GlFramebuffer)
    fun bindImageTexture(unit: Int, texture: GlTexture, level: Int, layered: Boolean, layer: Int, access: Int, format: Int)
    fun bindRenderbuffer(target: Int, renderbuffer: GlRenderbuffer)
    fun bindTexture(target: Int, texture: GlTexture)
    fun bindVertexArray(vao: GlVertexArrayObject)
    fun blendFunc(sFactor: Int, dFactor: Int)
    fun blitFramebuffer(srcX0: Int, srcY0: Int, srcX1: Int, srcY1: Int, dstX0: Int, dstY0: Int, dstX1: Int, dstY1: Int, mask: Int, filter: Int)
    fun bufferData(target: Int, size: Int, usage: Int)
    fun bufferData(target: Int, buffer: Uint8Buffer, usage: Int)
    fun bufferData(target: Int, buffer: Uint16Buffer, usage: Int)
    fun bufferData(target: Int, buffer: Int32Buffer, usage: Int)
    fun bufferData(target: Int, buffer: Float32Buffer, usage: Int)
    fun bufferData(target: Int, buffer: MixedBuffer, usage: Int)
    fun checkFramebufferStatus(target: Int): Int
    fun clear(mask: Int)
    fun clearBufferfv(buffer: Int, drawBuffer: Int, values: Float32Buffer)
    fun clearColor(r: Float, g: Float, b: Float, a: Float)
    fun clearDepth(depth: Float)
    fun clipControl(origin: Int, depth: Int)
    fun createBuffer(): GlBuffer
    fun createFramebuffer(): GlFramebuffer
    fun createProgram(): GlProgram
    fun createQuery(): GlQuery
    fun createRenderbuffer(): GlRenderbuffer
    fun createShader(type: Int): GlShader
    fun createTexture(): GlTexture
    fun createVertexArray(): GlVertexArrayObject
    fun compileShader(shader: GlShader)
    fun copyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int)
    fun cullFace(mode: Int)
    fun deleteBuffer(buffer: GlBuffer)
    fun deleteFramebuffer(framebuffer: GlFramebuffer)
    fun deleteProgram(program: GlProgram)
    fun deleteQuery(query: GlQuery)
    fun deleteRenderbuffer(renderbuffer: GlRenderbuffer)
    fun deleteShader(shader: GlShader)
    fun deleteTexture(texture: GlTexture)
    fun deleteVertexArray(vao: GlVertexArrayObject)
    fun depthFunc(func: Int)
    fun depthMask(flag: Boolean)
    fun disable(cap: Int)
    fun disableVertexAttribArray(index: Int)
    fun dispatchCompute(numGroupsX: Int, numGroupsY: Int, numGroupsZ: Int)
    fun drawBuffers(buffers: IntArray)
    fun drawElements(mode: Int, count: Int, type: Int)
    fun drawElementsInstanced(mode: Int, count: Int, type: Int, instanceCount: Int)
    fun enable(cap: Int)
    fun enableVertexAttribArray(index: Int)
    fun endQuery(target: Int)
    fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: GlRenderbuffer)
    fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: GlTexture, level: Int)
    fun generateMipmap(target: Int)
    fun getActiveUniformBlockParameter(program: GlProgram, uniformBlockIndex: Int, pName: Int): Int
    fun getActiveUniforms(program: GlProgram, uniformIndices: IntArray, pName: Int): IntArray
    fun getError(): Int
    fun getInteger(pName: Int): Int
    fun getProgramInfoLog(program: GlProgram): String
    fun getProgramParameter(program: GlProgram, param: Int): Any
    fun getQueryParameter(query: GlQuery, param: Int): Any
    fun getQueryParameterU64(query: GlQuery, param: Int): Long
    fun getShaderInfoLog(shader: GlShader): String
    fun getShaderParameter(shader: GlShader, param: Int): Any
    fun getUniformBlockIndex(program: GlProgram, uniformBlockName: String): Int
    fun getUniformIndices(program: GlProgram, names: Array<String>): IntArray
    fun getUniformLocation(program: GlProgram, uniformName: String): Int
    fun lineWidth(width: Float)
    fun linkProgram(program: GlProgram)
    fun memoryBarrier(barriers: Int)
    fun queryCounter(query: GlQuery, target: Int)
    fun readBuffer(src: Int)
    fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int)
    fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int)
    fun scissor(x: Int, y: Int, width: Int, height: Int)
    fun shaderSource(shader: GlShader, source: String)
    fun texImage1d(data: ImageData1d)
    fun texImage2d(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Buffer?)
    fun texImage2d(target: Int, data: ImageData2d)
    fun texImage3d(target: Int, data: ImageData3d)
    fun texSubImage3d(target: Int, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, format: Int, type: Int, pixels: ImageData)
    fun texParameteri(target: Int, pName: Int, param: Int)
    fun texStorage2d(target: Int, levels: Int, internalformat: Int, width: Int, height: Int)
    fun texStorage3d(target: Int, levels: Int, internalformat: Int, width: Int, height: Int, depth: Int)
    fun uniformBlockBinding(program: GlProgram, uniformBlockIndex: Int, uniformBlockBinding: Int)
    fun useProgram(program: GlProgram)
    fun uniform1f(location: Int, x: Float)
    fun uniform2f(location: Int, x: Float, y: Float)
    fun uniform3f(location: Int, x: Float, y: Float, z: Float)
    fun uniform4f(location: Int, x: Float, y: Float, z: Float, w: Float)
    fun uniform1fv(location: Int, values: Float32Buffer)
    fun uniform2fv(location: Int, values: Float32Buffer)
    fun uniform3fv(location: Int, values: Float32Buffer)
    fun uniform4fv(location: Int, values: Float32Buffer)
    fun uniform1i(location: Int, x: Int)
    fun uniform2i(location: Int, x: Int, y: Int)
    fun uniform3i(location: Int, x: Int, y: Int, z: Int)
    fun uniform4i(location: Int, x: Int, y: Int, z: Int, w: Int)
    fun uniform1iv(location: Int, values: Int32Buffer)
    fun uniform2iv(location: Int, values: Int32Buffer)
    fun uniform3iv(location: Int, values: Int32Buffer)
    fun uniform4iv(location: Int, values: Int32Buffer)
    fun uniformMatrix2fv(location: Int, values: Float32Buffer)
    fun uniformMatrix3fv(location: Int, values: Float32Buffer)
    fun uniformMatrix4fv(location: Int, values: Float32Buffer)
    fun vertexAttribDivisor(index: Int, divisor: Int)
    fun vertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int)
    fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int)
    fun viewport(x: Int, y: Int, width: Int, height: Int)

    fun readBuffer(gpuBuffer: GpuBufferGl, dstBuffer: Buffer): Boolean
    fun readTexturePixels(src: LoadedTextureGl, dst: BufferedImageData): Boolean

    fun checkNoError() = check(getError() == 0)
}

data class GlApiVersion(
    val major: Int,
    val minor: Int,
    val flavor: GlFlavor,
    val deviceInfo: String
) {
    val versionName: String = "${flavor.flavorName} $major.$minor"

    fun isHigherOrEqualThan(major: Int, minor: Int): Boolean {
        if (this.major < major) {
            return false
        }
        return this.major > major || this.minor >= minor
    }
}

enum class GlFlavor(val flavorName: String) {
    OpenGL("OpenGL"),
    OpenGLES("OpenGL ES"),
    WebGL("WebGL")
}

data class GlCapabilities(
    val maxTexUnits: Int,
    val maxAnisotropy: Int,
    val canFastCopyTextures: Boolean,
    val hasClipControl: Boolean,
    val hasTimestampQuery: Boolean,

    val hasComputeShaders: Boolean = false,
    val maxWorkGroupCount: Vec3i = Vec3i.ZERO,
    val maxWorkGroupSize: Vec3i = Vec3i.ZERO,
    val maxWorkGroupInvocations: Int = 0
)
