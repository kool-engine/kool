package de.fabmax.kool.platform.lwjgl3

import de.fabmax.kool.*
import de.fabmax.kool.gl.*
import de.fabmax.kool.platform.*
import de.fabmax.kool.platform.GL
import org.lwjgl.opengl.*


/**
 * @author fabmax
 */
class Lwjgl3Impl private constructor() : GL.Impl {

    companion object {
        val instance = Lwjgl3Impl()
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun activeTexture(texture: Int) {
        GL13.glActiveTexture(texture)
    }

    override fun attachShader(program: ProgramResource, shader: ShaderResource) {
        GL20.glAttachShader((program.glRef as Int?) ?: 0, (shader.glRef as Int?) ?: 0)
    }

    override fun bindBuffer(target: Int, buffer: BufferResource?) {
        GL15.glBindBuffer(target, (buffer?.glRef as Int?) ?: 0)
    }

    override fun bindFramebuffer(target: Int, framebuffer: FramebufferResource?) {
        GL30.glBindFramebuffer(target, (framebuffer?.glRef as Int?) ?: 0)
    }

    override fun bindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) {
        GL30.glBindRenderbuffer(target, (renderbuffer?.glRef as Int?) ?: 0)
    }

    override fun bindTexture(target: Int, texture: TextureResource?) {
        GL11.glBindTexture(target, (texture?.glRef as Int?) ?: 0)
    }

    override fun blendFunc(sfactor: Int, dfactor: Int) {
        GL11.glBlendFunc(sfactor, dfactor)
    }

    override fun bufferData(target: Int, data: Uint8Buffer, usage: Int) {
        GL15.glBufferData(target, (data as Uint8BufferImpl).buffer, usage)
    }

    override fun bufferData(target: Int, data: Uint16Buffer, usage: Int) {
        GL15.glBufferData(target, (data as Uint16BufferImpl).buffer, usage)
    }

    override fun bufferData(target: Int, data: Uint32Buffer, usage: Int) {
        GL15.glBufferData(target, (data as Uint32BufferImpl).buffer, usage)
    }

    override fun bufferData(target: Int, data: Float32Buffer, usage: Int) {
        GL15.glBufferData(target, (data as Float32BufferImpl).buffer, usage)
    }

    override fun clear(mask: Int) {
        GL11.glClear(mask)
    }

    override fun clearColor(red: Float, green: Float, blue: Float, alpha: Float) {
        GL11.glClearColor(red, green, blue, alpha)
    }

    override fun compileShader(shader: ShaderResource) {
        GL20.glCompileShader(shader.glRef as Int)
    }

    override fun copyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) {
        GL11.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border)
    }

    override fun createBuffer(): Any {
        return GL15.glGenBuffers()
    }

    override fun createFramebuffer(): Any {
        return GL30.glGenFramebuffers()
    }

    override fun createProgram(): Any {
        return GL20.glCreateProgram()
    }

    override fun createRenderbuffer(): Any {
        return GL30.glGenRenderbuffers()
    }

    override fun createShader(type: Int): Any {
        return GL20.glCreateShader(type)
    }

    override fun createTexture(): Any {
        return GL11.glGenTextures()
    }

    override fun deleteBuffer(buffer: BufferResource) {
        GL15.glDeleteBuffers(buffer.glRef as Int)
    }

    override fun deleteFramebuffer(framebuffer: FramebufferResource) {
        GL30.glDeleteFramebuffers(framebuffer.glRef as Int)
    }

    override fun deleteProgram(program: ProgramResource) {
        GL20.glDeleteProgram(program.glRef as Int)
    }

    override fun deleteRenderbuffer(renderbuffer: RenderbufferResource) {
        GL30.glDeleteRenderbuffers(renderbuffer.glRef as Int)
    }

    override fun deleteShader(shader: ShaderResource) {
        GL20.glDeleteShader(shader.glRef as Int)
    }

    override fun deleteTexture(texture: TextureResource) {
        GL11.glDeleteTextures(texture.glRef as Int)
    }

    override fun depthFunc(func: Int) {
        GL11.glDepthFunc(func)
    }

    override fun depthMask(enabled: Boolean) {
        GL11.glDepthMask(enabled)
    }

    override fun disable(cap: Int) {
        GL11.glDisable(cap)
    }

    override fun disableVertexAttribArray(index: Int) {
        GL20.glDisableVertexAttribArray(index)
    }

    override fun drawElements(mode: Int, count: Int, type: Int, offset: Int) {
        GL11.glDrawElements(mode, count, type, offset.toLong())
    }

    override fun drawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) {
        GL31.glDrawElementsInstanced(mode, count, type, indicesOffset.toLong(), instanceCount)
    }

    override fun enable(cap: Int) {
        GL11.glEnable(cap)
    }

    override fun enableVertexAttribArray(index: Int) {
        GL20.glEnableVertexAttribArray(index)
    }

    override fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) {
        GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.glRef as Int)
    }

    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) {
        GL30.glFramebufferTexture2D(target, attachment, textarget, texture.glRef as Int, level)
    }

    override fun generateMipmap(target: Int) {
        GL30.glGenerateMipmap(target)
    }

    override fun getAttribLocation(program: ProgramResource, name: String): Int {
        return GL20.glGetAttribLocation(program.glRef as Int, name)
    }

    override fun getError(): Int {
        return GL11.glGetError()
    }

    override fun getProgrami(program: ProgramResource, pname: Int): Int {
        return GL20.glGetProgrami(program.glRef as Int, pname)
    }

    override fun getShaderi(shader: ShaderResource, pname: Int): Int {
        return GL20.glGetShaderi(shader.glRef as Int, pname)
    }

    override fun getProgramInfoLog(program: ProgramResource): String {
        return GL20.glGetProgramInfoLog(program.glRef as Int, 10000)
    }

    override fun getShaderInfoLog(shader: ShaderResource): String {
        return GL20.glGetShaderInfoLog(shader.glRef as Int, 10000)
    }

    override fun getUniformLocation(program: ProgramResource, name: String): Int {
        return GL20.glGetUniformLocation(program.glRef as Int, name)
    }

    override fun lineWidth(width: Float) {
        GL11.glLineWidth(width)
    }

    override fun linkProgram(program: ProgramResource) {
        GL20.glLinkProgram(program.glRef as Int)
    }

    override fun pointSize(size: Float) {
        GL11.glPointSize(size)
    }

    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) {
        GL30.glRenderbufferStorage(target, internalformat, width, height)
    }

    override fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) {
        GL30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height)
    }

    override fun shaderSource(shader: ShaderResource, source: String) {
        GL20.glShaderSource(shader.glRef as Int, source)
    }

    override fun texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) {
        GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (pixels as Uint8BufferImpl?)?.buffer)
    }

    override fun texParameteri(target: Int, pname: Int, param: Int) {
        GL11.glTexParameteri(target, pname, param)
    }

    override fun uniform1f(location: Any?, x: Float) {
        GL20.glUniform1f(location as Int, x)
    }

    override fun uniform1i(location: Any?, x: Int) {
        GL20.glUniform1i(location as Int, x)
    }

    override fun uniform2f(location: Any?, x: Float, y: Float) {
        GL20.glUniform2f(location as Int, x, y)
    }

    override fun uniform3f(location: Any?, x: Float, y: Float, z: Float) {
        GL20.glUniform3f(location as Int, x, y, z)
    }

    override fun uniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) {
        GL20.glUniform4f(location as Int, x, y, z, w)
    }

    override fun uniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) {
        GL20.glUniformMatrix4fv(location as Int, transpose, (value as Float32BufferImpl).buffer)
    }

    override fun useProgram(program: ProgramResource?) {
        GL20.glUseProgram((program?.glRef as Int?) ?: 0)
    }

    override fun vertexAttribDivisor(index: Int, divisor: Int) {
        GL33.glVertexAttribDivisor(index, divisor)
    }

    override fun vertexAttribPointer(indx: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        GL20.glVertexAttribPointer(indx, size, type, normalized, stride, offset.toLong())
    }

    override fun viewport(x: Int, y: Int, width: Int, height: Int) {
        GL11.glViewport(x, y, width, height)
    }
}
