package de.fabmax.kool.gl

import de.fabmax.kool.util.*
import org.lwjgl.opengl.*


/**
 * @author fabmax
 */

actual fun glActiveTexture(texture: Int) = GL13.glActiveTexture(texture)

actual fun glAttachShader(program: ProgramResource, shader: ShaderResource) =
        GL20.glAttachShader((program.glRef as Int?) ?: 0, (shader.glRef as Int?) ?: 0)

actual fun glBindBuffer(target: Int, buffer: BufferResource?) =
        GL15.glBindBuffer(target, (buffer?.glRef as Int?) ?: 0)

actual fun glBindFramebuffer(target: Int, framebuffer: FramebufferResource?) =
        GL30.glBindFramebuffer(target, (framebuffer?.glRef as Int?) ?: 0)

actual fun glBindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) =
        GL30.glBindRenderbuffer(target, (renderbuffer?.glRef as Int?) ?: 0)

actual fun glBindTexture(target: Int, texture: TextureResource?) =
        GL11.glBindTexture(target, (texture?.glRef as Int?) ?: 0)

actual fun glBlendFunc(sfactor: Int, dfactor: Int) = GL11.glBlendFunc(sfactor, dfactor)

actual fun glBufferData(target: Int, data: Uint8Buffer, usage: Int) =
        GL15.glBufferData(target, (data as Uint8BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Uint16Buffer, usage: Int) =
        GL15.glBufferData(target, (data as Uint16BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Uint32Buffer, usage: Int) =
        GL15.glBufferData(target, (data as Uint32BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Float32Buffer, usage: Int) =
        GL15.glBufferData(target, (data as Float32BufferImpl).buffer, usage)

actual fun glCheckFramebufferStatus(target: Int) =
        GL30.glCheckFramebufferStatus(target)

actual fun glClear(mask: Int) = GL11.glClear(mask)

actual fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) =
        GL11.glClearColor(red, green, blue, alpha)

actual fun glCompileShader(shader: ShaderResource) = GL20.glCompileShader(shader.glRef as Int)

actual fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) =
        GL11.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border)

actual fun glCreateBuffer(): Any = GL15.glGenBuffers()

actual fun glCreateFramebuffer(): Any = GL30.glGenFramebuffers()

actual fun glCreateProgram(): Any = GL20.glCreateProgram()

actual fun glCreateRenderbuffer(): Any = GL30.glGenRenderbuffers()

actual fun glCreateShader(type: Int): Any = GL20.glCreateShader(type)

actual fun glCreateTexture(): Any = GL11.glGenTextures()

actual fun glCullFace(mode: Int) = GL11.glCullFace(mode)

actual fun glDeleteBuffer(buffer: BufferResource) = GL15.glDeleteBuffers(buffer.glRef as Int)

actual fun glDeleteFramebuffer(framebuffer: FramebufferResource) = GL30.glDeleteFramebuffers(framebuffer.glRef as Int)

actual fun glDeleteProgram(program: ProgramResource) = GL20.glDeleteProgram(program.glRef as Int)

actual fun glDeleteRenderbuffer(renderbuffer: RenderbufferResource) =
        GL30.glDeleteRenderbuffers(renderbuffer.glRef as Int)

actual fun glDeleteShader(shader: ShaderResource) = GL20.glDeleteShader(shader.glRef as Int)

actual fun glDeleteTexture(texture: TextureResource) = GL11.glDeleteTextures(texture.glRef as Int)

actual fun glDepthFunc(func: Int) = GL11.glDepthFunc(func)

actual fun glDepthMask(enabled: Boolean) = GL11.glDepthMask(enabled)

actual fun glDisable(cap: Int) = GL11.glDisable(cap)

actual fun glDisableVertexAttribArray(index: Int) = GL20.glDisableVertexAttribArray(index)

actual fun glDrawBuffer(buf: Int) = GL11.glDrawBuffer(buf)

actual fun glDrawElements(mode: Int, count: Int, type: Int, offset: Int) =
        GL11.glDrawElements(mode, count, type, offset.toLong())

actual fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) =
        GL31.glDrawElementsInstanced(mode, count, type, indicesOffset.toLong(), instanceCount)

actual fun glEnable(cap: Int) = GL11.glEnable(cap)

actual fun glEnableVertexAttribArray(index: Int) = GL20.glEnableVertexAttribArray(index)

actual fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) =
        GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.glRef as Int)

actual fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) =
        GL30.glFramebufferTexture2D(target, attachment, textarget, texture.glRef as Int, level)

actual fun glGenerateMipmap(target: Int) = GL30.glGenerateMipmap(target)

actual fun glGetAttribLocation(program: ProgramResource, name: String): Int =
        GL20.glGetAttribLocation(program.glRef as Int, name)

actual fun glGetError(): Int = GL11.glGetError()

actual fun glGetProgrami(program: ProgramResource, pname: Int): Int = GL20.glGetProgrami(program.glRef as Int, pname)

actual fun glGetShaderi(shader: ShaderResource, pname: Int): Int = GL20.glGetShaderi(shader.glRef as Int, pname)

actual fun glGetProgramInfoLog(program: ProgramResource): String =
        GL20.glGetProgramInfoLog(program.glRef as Int, 10000)

actual fun glGetShaderInfoLog(shader: ShaderResource): String =
        GL20.glGetShaderInfoLog(shader.glRef as Int, 10000)

actual fun glGetUniformLocation(program: ProgramResource, name: String): Any? =
        GL20.glGetUniformLocation(program.glRef as Int, name)

actual fun glLineWidth(width: Float) = GL11.glLineWidth(width)

actual fun glLinkProgram(program: ProgramResource) = GL20.glLinkProgram(program.glRef as Int)

actual fun glPointSize(size: Float) = GL11.glPointSize(size)

actual fun glReadBuffer(src: Int) = GL11.glReadBuffer(src)

actual fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) =
        GL30.glRenderbufferStorage(target, internalformat, width, height)

actual fun glRenderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) =
        GL30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height)

actual fun glShaderSource(shader: ShaderResource, source: String) = GL20.glShaderSource(shader.glRef as Int, source)

actual fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) =
        GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (pixels as Uint8BufferImpl?)?.buffer)

actual fun glTexParameteri(target: Int, pname: Int, param: Int) = GL11.glTexParameteri(target, pname, param)

actual fun glUniform1f(location: Any?, x: Float) = GL20.glUniform1f(location as Int, x)

actual fun glUniform1fv(location: Any?, x: FloatArray) = GL20.glUniform1fv(location as Int, x)

actual fun glUniform1i(location: Any?, x: Int) = GL20.glUniform1i(location as Int, x)

actual fun glUniform1iv(location: Any?, x: IntArray) = GL20.glUniform1iv(location as Int, x)

actual fun glUniform2f(location: Any?, x: Float, y: Float) = GL20.glUniform2f(location as Int, x, y)

actual fun glUniform3f(location: Any?, x: Float, y: Float, z: Float) = GL20.glUniform3f(location as Int, x, y, z)

actual fun glUniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) =
        GL20.glUniform4f(location as Int, x, y, z, w)

actual fun glUniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) =
        GL20.glUniformMatrix4fv(location as Int, transpose, (value as Float32BufferImpl).buffer)

actual fun glUseProgram(program: ProgramResource?) = GL20.glUseProgram((program?.glRef as Int?) ?: 0)

actual fun glVertexAttribDivisor(index: Int, divisor: Int) = GL33.glVertexAttribDivisor(index, divisor)

actual fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) =
        GL20.glVertexAttribPointer(index, size, type, normalized, stride, offset.toLong())

actual fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) =
        GL30.glVertexAttribIPointer(index, size, type, stride, offset.toLong())

actual fun glViewport(x: Int, y: Int, width: Int, height: Int) = GL11.glViewport(x, y, width, height)

actual fun isValidUniformLocation(location: Any?): Boolean = (location as? Int ?: -1) >= 0