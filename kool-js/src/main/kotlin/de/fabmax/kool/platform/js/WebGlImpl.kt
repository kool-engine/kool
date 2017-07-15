package de.fabmax.kool.platform.js

import de.fabmax.kool.gl.*
import de.fabmax.kool.platform.*
import org.khronos.webgl.*
import org.w3c.dom.HTMLImageElement

/**
 * @author fabmax
 */
class WebGlImpl private constructor() : GL.Api {
    companion object {
        val instance = WebGlImpl()
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun glslFragHeader(): String {
        return "#version 100\n"
    }

    override fun glslVertHeader(): String {
        return "#version 100\n"
    }

    override fun activeTexture(texture: Int) {
        PlatformImpl.gl.activeTexture(texture)
    }

    override fun attachShader(program: ProgramResource, shader: ShaderResource) {
        PlatformImpl.gl.attachShader(program.glRef as WebGLProgram, shader.glRef as WebGLShader)
    }

    override fun bindBuffer(target: Int, buffer: BufferResource?) {
        PlatformImpl.gl.bindBuffer(target, buffer?.glRef as WebGLBuffer?)
    }

    override fun bindFramebuffer(target: Int, framebuffer: FramebufferResource?) {
        PlatformImpl.gl.bindFramebuffer(target, framebuffer?.glRef as WebGLFramebuffer?)
    }

    override fun bindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) {
        PlatformImpl.gl.bindRenderbuffer(target, renderbuffer?.glRef as WebGLRenderbuffer?)
    }

    override fun bindTexture(target: Int, texture: TextureResource?) {
        PlatformImpl.gl.bindTexture(target, texture?.glRef as WebGLTexture?)
    }

    override fun blendFunc(sfactor: Int, dfactor: Int) {
        PlatformImpl.gl.blendFunc(sfactor, dfactor)
    }

    override fun bufferData(target: Int, data: Uint8Buffer, usage: Int) {
        PlatformImpl.gl.bufferData(target, (data as Uint8BufferImpl).buffer, usage)
    }

    override fun bufferData(target: Int, data: Uint16Buffer, usage: Int) {
        PlatformImpl.gl.bufferData(target, (data as Uint16BufferImpl).buffer, usage)
    }

    override fun bufferData(target: Int, data: Uint32Buffer, usage: Int) {
        PlatformImpl.gl.bufferData(target, (data as Uint32BufferImpl).buffer, usage)
    }

    override fun bufferData(target: Int, data: Float32Buffer, usage: Int) {
        PlatformImpl.gl.bufferData(target, (data as Float32BufferImpl).buffer, usage)
    }

    override fun clear(mask: Int) {
        PlatformImpl.gl.clear(mask)
    }

    override fun clearColor(red: Float, green: Float, blue: Float, alpha: Float) {
        PlatformImpl.gl.clearColor(red, green, blue, alpha)
    }

    override fun compileShader(shader: ShaderResource) {
        PlatformImpl.gl.compileShader(shader.glRef as WebGLShader)
    }

    override fun copyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) {
        PlatformImpl.gl.copyTexImage2D(target, level, internalformat, x, y, width, height, border)
    }

    override fun createBuffer(): Any {
        return PlatformImpl.gl.createBuffer()!!
    }

    override fun createFramebuffer(): Any {
        return PlatformImpl.gl.createFramebuffer()!!
    }

    override fun createRenderbuffer(): Any {
        return PlatformImpl.gl.createRenderbuffer()!!
    }

    override fun createProgram(): Any {
        return PlatformImpl.gl.createProgram()!!
    }

    override fun createShader(type: Int): Any {
        return PlatformImpl.gl.createShader(type)!!
    }

    override fun createTexture(): Any {
        return PlatformImpl.gl.createTexture()!!
    }

    override fun deleteBuffer(buffer: BufferResource) {
        PlatformImpl.gl.deleteBuffer(buffer.glRef as WebGLBuffer)
    }

    override fun deleteFramebuffer(framebuffer: FramebufferResource) {
        PlatformImpl.gl.deleteFramebuffer(framebuffer.glRef as WebGLFramebuffer)
    }

    override fun deleteProgram(program: ProgramResource) {
        PlatformImpl.gl.deleteProgram(program.glRef as WebGLProgram)
    }

    override fun deleteRenderbuffer(renderbuffer: RenderbufferResource) {
        PlatformImpl.gl.deleteRenderbuffer(renderbuffer.glRef as WebGLRenderbuffer)
    }

    override fun deleteShader(shader: ShaderResource) {
        PlatformImpl.gl.deleteShader(shader.glRef as WebGLShader)
    }

    override fun deleteTexture(texture: TextureResource) {
        PlatformImpl.gl.deleteTexture(texture.glRef as WebGLTexture)
    }

    override fun depthFunc(func: Int) {
        PlatformImpl.gl.depthFunc(func)
    }

    override fun depthMask(enabled: Boolean) {
        PlatformImpl.gl.depthMask(enabled)
    }

    override fun disable(cap: Int) {
        PlatformImpl.gl.disable(cap)
    }

    override fun disableVertexAttribArray(index: Int) {
        PlatformImpl.gl.disableVertexAttribArray(index)
    }

    override fun drawElements(mode: Int, count: Int, type: Int, offset: Int) {
        PlatformImpl.gl.drawElements(mode, count, type, offset)
    }

    override fun drawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) {
        throw UnsupportedOperationException("not available on WebGL")
    }

    override fun enable(cap: Int) {
        PlatformImpl.gl.enable(cap)
    }

    override fun enableVertexAttribArray(index: Int) {
        PlatformImpl.gl.enableVertexAttribArray(index)
    }

    override fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) {
        PlatformImpl.gl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.glRef as WebGLRenderbuffer)
    }

    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) {
        PlatformImpl.gl.framebufferTexture2D(target, attachment, textarget, texture.glRef as WebGLTexture, level)
    }

    override fun generateMipmap(target: Int) {
        PlatformImpl.gl.generateMipmap(target)
    }

    override fun getAttribLocation(program: ProgramResource, name: String): Int {
        return PlatformImpl.gl.getAttribLocation(program.glRef as WebGLProgram, name)
    }

    override fun getError(): Int {
        return PlatformImpl.gl.getError()
    }

    override fun getProgrami(program: ProgramResource, pname: Int): Int {
        val res = PlatformImpl.gl.getProgramParameter(program.glRef as WebGLProgram, pname)
        if (pname == GL.LINK_STATUS) {
            if (res as Boolean) {
                return GL.TRUE
            } else {
                return GL.FALSE
            }
        }
        return 0
    }

    override fun getShaderi(shader: ShaderResource, pname: Int): Int {
        val res = PlatformImpl.gl.getShaderParameter(shader.glRef as WebGLShader, pname)
        if (pname == GL.COMPILE_STATUS) {
            if (res as Boolean) {
                return GL.TRUE
            } else {
                return GL.FALSE
            }
        }
        return 0
    }

    override fun getProgramInfoLog(program: ProgramResource): String {
        return PlatformImpl.gl.getProgramInfoLog(program.glRef as WebGLProgram) ?: ""
    }

    override fun getShaderInfoLog(shader: ShaderResource): String {
        return PlatformImpl.gl.getShaderInfoLog(shader.glRef as WebGLShader) ?: ""
    }

    override fun getUniformLocation(program: ProgramResource, name: String): Any? {
        return PlatformImpl.gl.getUniformLocation(program.glRef as WebGLProgram, name)
    }

    override fun lineWidth(width: Float) {
        PlatformImpl.gl.lineWidth(width)
    }

    override fun linkProgram(program: ProgramResource) {
        PlatformImpl.gl.linkProgram(program.glRef as WebGLProgram)
    }

    override fun pointSize(size: Float) {
        // not supported by WebGL and silently ignored
    }

    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) {
        PlatformImpl.gl.renderbufferStorage(target, internalformat, width, height)
    }

    override fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) {
        throw UnsupportedOperationException("not available on WebGL")
    }

    override fun shaderSource(shader: ShaderResource, source: String) {
        PlatformImpl.gl.shaderSource(shader.glRef as WebGLShader, source)
    }

    override fun texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) {
        PlatformImpl.gl.texImage2D(target, level, internalformat, width, height, border, format, type, (pixels as Uint8BufferImpl?)?.buffer)
    }

    fun texImage2D(target: Int, level: Int, internalformat: Int, format: Int, type: Int, pixels: Any?) {
        // pre-multiply alpha for image object
        PlatformImpl.gl.pixelStorei(WebGLRenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, GL.TRUE)
        PlatformImpl.gl.texImage2D(target, level, internalformat, format, type, pixels as HTMLImageElement?)
        PlatformImpl.gl.pixelStorei(WebGLRenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, GL.FALSE)
    }

    override fun texParameteri(target: Int, pname: Int, param: Int) {
        PlatformImpl.gl.texParameteri(target, pname, param)
    }

    override fun uniform1f(location: Any?, x: Float) {
        PlatformImpl.gl.uniform1f((location as WebGLUniformLocation?), x)
    }

    override fun uniform1i(location: Any?, x: Int) {
        PlatformImpl.gl.uniform1i((location as WebGLUniformLocation?), x)
    }

    override fun uniform2f(location: Any?, x: Float, y: Float) {
        PlatformImpl.gl.uniform2f((location as WebGLUniformLocation?), x, y)
    }

    override fun uniform3f(location: Any?, x: Float, y: Float, z: Float) {
        PlatformImpl.gl.uniform3f((location as WebGLUniformLocation?), x, y, z)
    }

    override fun uniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) {
        PlatformImpl.gl.uniform4f((location as WebGLUniformLocation?), x, y, z, w)
    }

    override fun uniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) {
        PlatformImpl.gl.uniformMatrix4fv((location as WebGLUniformLocation?), transpose, (value as Float32BufferImpl).buffer)
    }

    override fun useProgram(program: ProgramResource?) {
        PlatformImpl.gl.useProgram(program?.glRef as WebGLProgram?)
    }

    override fun vertexAttribDivisor(index: Int, divisor: Int) {
        throw UnsupportedOperationException("not available on WebGL")
    }

    override fun vertexAttribPointer(indx: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        PlatformImpl.gl.vertexAttribPointer(indx, size, type, normalized, stride, offset)
    }

    override fun viewport(x: Int, y: Int, width: Int, height: Int) {
        PlatformImpl.gl.viewport(x, y, width, height)
    }
}
