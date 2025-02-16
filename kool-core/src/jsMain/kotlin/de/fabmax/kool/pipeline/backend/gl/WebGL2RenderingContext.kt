package de.fabmax.kool.pipeline.backend.gl

import org.khronos.webgl.*
import org.w3c.dom.HTMLImageElement

abstract external class WebGL2RenderingContext : WebGLRenderingContext {
    fun beginQuery(target: Int, query: WebGLQuery?)
    fun bindBufferBase(target: Int, index: Int, buffer: WebGLBuffer?)
    fun bindVertexArray(vertexArray: WebGLVertexArrayObject?)
    fun blitFramebuffer(srcX0: Int, srcY0: Int, srcX1: Int, srcY1: Int, dstX0: Int, dstY0: Int, dstX1: Int, dstY1: Int, mask: Int, filter: Int)
    fun bufferData(target: Int, srcData: ArrayBufferView, usage: Int, srcOffset: Int, length: Int)
    fun clearBufferfv(buffer: Int, drawBuffer: Int, values: Float32Array)
    fun createVertexArray(): WebGLVertexArrayObject
    fun createQuery(): WebGLQuery
    fun deleteQuery(query: WebGLQuery?)
    fun deleteVertexArray(vertexArray: WebGLVertexArrayObject?)
    fun drawBuffers(buffers: IntArray)
    fun drawElementsInstanced(mode: Int, count: Int, type: Int, offset: Int, instanceCount: Int)
    fun endQuery(target: Int)
    fun getActiveUniformBlockParameter(program: WebGLProgram?, uniformBlockIndex: Int, pname: Int): Int
    fun getActiveUniforms(program: WebGLProgram?, uniformIndices: IntArray, pname: Int): IntArray
    fun getQueryParameter(query: WebGLQuery?, param: Int): Any
    fun getUniformBlockIndex(program: WebGLProgram?, uniformBlockName: String): Int
    fun getUniformIndices(program: WebGLProgram?, uniformNames: Array<String>): IntArray
    fun readBuffer(src: Int)
    fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int)
    fun texImage3D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, depth: Int, border: Int, format: Int, type: Int, srcData: ArrayBufferView?)
    fun texImage3D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, depth: Int, border: Int, format: Int, type: Int, source: HTMLImageElement?)
    fun texSubImage3D(target: Int, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, format: Int, type: Int, pixels: Any?)
    fun texStorage2D(target: Int, levels: Int, internalformat: Int, width: Int, height: Int)
    fun texStorage3D(target: Int, levels: Int, internalformat: Int, width: Int, height: Int, depth: Int)
    fun uniformBlockBinding(program: WebGLProgram?, uniformBlockIndex: Int, uniformBlockBinding: Int)
    fun vertexAttribDivisor(index: Int, divisor: Int)
    fun vertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int)

    companion object {
        val COLOR: Int
        val DEPTH: Int
        val STENCIL: Int
        val DEPTH_STENCIL: Int
        val DEPTH_COMPONENT: Int
        val DRAW_FRAMEBUFFER: Int
        val READ_FRAMEBUFFER: Int
        val QUERY_RESULT: Int
        val QUERY_RESULT_AVAILABLE: Int

        val DEPTH_COMPONENT24: Int
        val DEPTH_COMPONENT32F: Int
        val TEXTURE_2D_ARRAY: Int
        val TEXTURE_3D: Int
        val TEXTURE_BASE_LEVEL: Int
        val TEXTURE_MAX_LEVEL: Int
        val TEXTURE_MAX_LOD: Int
        val TEXTURE_MIN_LOD: Int
        val TEXTURE_WRAP_R: Int
        val TEXTURE_COMPARE_MODE: Int
        val COMPARE_REF_TO_TEXTURE: Int
        val TEXTURE_COMPARE_FUNC: Int

        val UNIFORM_BLOCK_DATA_SIZE: Int
        val UNIFORM_BUFFER: Int
        val UNIFORM_OFFSET: Int

        val INVALID_INDEX: Int

        val RED: Int
        val RG: Int
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
    }
}

external interface WebGLQuery

external interface WebGLVertexArrayObject

external interface EXT_clip_control {
    fun clipControlEXT(origin: Int, depth: Int)

    val LOWER_LEFT_EXT: Int
    val UPPER_LEFT_EXT: Int

    val NEGATIVE_ONE_TO_ONE_EXT: Int
    val ZERO_TO_ONE_EXT: Int

    val CLIP_ORIGIN_EXT: Int
    val CLIP_DEPTH_MODE_EXT: Int
}
