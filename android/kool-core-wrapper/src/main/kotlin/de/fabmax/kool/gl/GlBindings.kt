package de.fabmax.kool.gl

import de.fabmax.kool.KoolException
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Uint16Buffer
import de.fabmax.kool.util.Uint32Buffer
import de.fabmax.kool.util.Uint8Buffer


/**
 * @author fabmax
 */

internal var glImpl: GlImpl = NoGlImpl()

actual fun glActiveTexture(texture: Int) = glImpl.glActiveTexture(texture)
actual fun glAttachShader(program: ProgramResource, shader: ShaderResource) = glImpl.glAttachShader(program, shader)
actual fun glBindBuffer(target: Int, buffer: BufferResource?) = glImpl.glBindBuffer(target, buffer)
actual fun glBindFramebuffer(target: Int, framebuffer: FramebufferResource?) = glImpl.glBindFramebuffer(target, framebuffer)
actual fun glBindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) = glImpl.glBindRenderbuffer(target, renderbuffer)
actual fun glBindTexture(target: Int, texture: TextureResource?) = glImpl.glBindTexture(target, texture)
actual fun glBlendFunc(sfactor: Int, dfactor: Int) = glImpl.glBlendFunc(sfactor, dfactor)
actual fun glBufferData(target: Int, data: Uint8Buffer, usage: Int) = glImpl.glBufferData(target, data, usage)
actual fun glBufferData(target: Int, data: Uint16Buffer, usage: Int) = glImpl.glBufferData(target, data, usage)
actual fun glBufferData(target: Int, data: Uint32Buffer, usage: Int) = glImpl.glBufferData(target, data, usage)
actual fun glBufferData(target: Int, data: Float32Buffer, usage: Int) = glImpl.glBufferData(target, data, usage)
actual fun glCheckFramebufferStatus(target: Int): Int = glImpl.glCheckFramebufferStatus(target)
actual fun glClear(mask: Int) = glImpl.glClear(mask)
actual fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) = glImpl.glClearColor(red, green, blue, alpha)
actual fun glCompileShader(shader: ShaderResource) = glImpl.glCompileShader(shader)
actual fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) = glImpl.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border)
actual fun glCreateBuffer(): Any = glImpl.glCreateBuffer()
actual fun glCreateFramebuffer(): Any = glImpl.glCreateFramebuffer()
actual fun glCreateProgram(): Any = glImpl.glCreateProgram()
actual fun glCreateRenderbuffer(): Any = glImpl.glCreateRenderbuffer()
actual fun glCreateShader(type: Int): Any = glImpl.glCreateShader(type)
actual fun glCreateTexture(): Any = glImpl.glCreateTexture()
actual fun glCullFace(mode: Int) = glImpl.glCullFace(mode)
actual fun glDeleteBuffer(buffer: BufferResource) = glImpl.glDeleteBuffer(buffer)
actual fun glDeleteFramebuffer(framebuffer: FramebufferResource) = glImpl.glDeleteFramebuffer(framebuffer)
actual fun glDeleteProgram(program: ProgramResource) = glImpl.glDeleteProgram(program)
actual fun glDeleteRenderbuffer(renderbuffer: RenderbufferResource) = glImpl.glDeleteRenderbuffer(renderbuffer)
actual fun glDeleteShader(shader: ShaderResource) = glImpl.glDeleteShader(shader)
actual fun glDeleteTexture(texture: TextureResource) = glImpl.glDeleteTexture(texture)
actual fun glDepthFunc(func: Int) = glImpl.glDepthFunc(func)
actual fun glDepthMask(enabled: Boolean) = glImpl.glDepthMask(enabled)
actual fun glDisable(cap: Int) = glImpl.glDisable(cap)
actual fun glDisableVertexAttribArray(index: Int) = glImpl.glDisableVertexAttribArray(index)
actual fun glDrawBuffer(buf: Int) = glImpl.glDrawBuffer(buf)
actual fun glDrawElements(mode: Int, count: Int, type: Int, offset: Int) = glImpl.glDrawElements(mode, count, type, offset)
actual fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) = glImpl.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount)
actual fun glEnable(cap: Int) = glImpl.glEnable(cap)
actual fun glEnableVertexAttribArray(index: Int) = glImpl.glEnableVertexAttribArray(index)
actual fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) = glImpl.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
actual fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) = glImpl.glFramebufferTexture2D(target, attachment, textarget, texture, level)
actual fun glGenerateMipmap(target: Int) = glImpl.glGenerateMipmap(target)
actual fun glGetAttribLocation(program: ProgramResource, name: String): Int = glImpl.glGetAttribLocation(program, name)
actual fun glGetError(): Int = glImpl.glGetError()
actual fun glGetProgrami(program: ProgramResource, pname: Int): Int = glImpl.glGetProgrami(program, pname)
actual fun glGetShaderi(shader: ShaderResource, pname: Int): Int = glImpl.glGetShaderi(shader, pname)
actual fun glGetProgramInfoLog(program: ProgramResource): String = glImpl.glGetProgramInfoLog(program)
actual fun glGetShaderInfoLog(shader: ShaderResource): String = glImpl.glGetShaderInfoLog(shader)
actual fun glGetUniformLocation(program: ProgramResource, name: String): Any? = glImpl.glGetUniformLocation(program, name)
actual fun glLineWidth(width: Float) = glImpl.glLineWidth(width)
actual fun glLinkProgram(program: ProgramResource) = glImpl.glLinkProgram(program)
actual fun glPointSize(size: Float) = glImpl.glPointSize(size)
actual fun glReadBuffer(src: Int) = glImpl.glReadBuffer(src)
actual fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) = glImpl.glRenderbufferStorage(target, internalformat, width, height)
actual fun glRenderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) = glImpl.glRenderbufferStorageMultisample(target, samples, internalformat, width, height)
actual fun glShaderSource(shader: ShaderResource, source: String) = glImpl.glShaderSource(shader, source)
actual fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) = glImpl.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels)
actual fun glTexParameteri(target: Int, pname: Int, param: Int) = glImpl.glTexParameteri(target, pname, param)
actual fun glUniform1f(location: Any?, x: Float) = glImpl.glUniform1f(location, x)
actual fun glUniform1fv(location: Any?, x: FloatArray) = glImpl.glUniform1fv(location, x)
actual fun glUniform1i(location: Any?, x: Int) = glImpl.glUniform1i(location, x)
actual fun glUniform1iv(location: Any?, x: IntArray) = glImpl.glUniform1iv(location, x)
actual fun glUniform2f(location: Any?, x: Float, y: Float) = glImpl.glUniform2f(location, x, y)
actual fun glUniform3f(location: Any?, x: Float, y: Float, z: Float) = glImpl.glUniform3f(location, x, y, z)
actual fun glUniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) = glImpl.glUniform4f(location, x, y, z, w)
actual fun glUniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) = glImpl.glUniformMatrix4fv(location, transpose, value)
actual fun glUseProgram(program: ProgramResource?) = glImpl.glUseProgram(program)
actual fun glVertexAttribDivisor(index: Int, divisor: Int) = glImpl.glVertexAttribDivisor(index, divisor)
actual fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) = glImpl.glVertexAttribPointer(index, size, type, normalized, stride, offset)
actual fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) = glImpl.glVertexAttribIPointer(index, size, type, stride, offset)
actual fun glViewport(x: Int, y: Int, width: Int, height: Int) = glImpl.glViewport(x, y, width, height)
actual fun isValidUniformLocation(location: Any?): Boolean = (location as? Int ?: -1) >= 0

interface GlImpl {
    fun glActiveTexture(texture: Int)
    fun glAttachShader(program: ProgramResource, shader: ShaderResource)
    fun glBindBuffer(target: Int, buffer: BufferResource?)
    fun glBindFramebuffer(target: Int, framebuffer: FramebufferResource?)
    fun glBindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?)
    fun glBindTexture(target: Int, texture: TextureResource?)
    fun glBlendFunc(sfactor: Int, dfactor: Int)
    fun glBufferData(target: Int, data: Uint8Buffer, usage: Int)
    fun glBufferData(target: Int, data: Uint16Buffer, usage: Int)
    fun glBufferData(target: Int, data: Uint32Buffer, usage: Int)
    fun glBufferData(target: Int, data: Float32Buffer, usage: Int)
    fun glCheckFramebufferStatus(target: Int): Int
    fun glClear(mask: Int)
    fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float)
    fun glCompileShader(shader: ShaderResource)
    fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int)
    fun glCreateBuffer(): Any
    fun glCreateFramebuffer(): Any
    fun glCreateProgram(): Any
    fun glCreateRenderbuffer(): Any
    fun glCreateShader(type: Int): Any
    fun glCreateTexture(): Any
    fun glCullFace(mode: Int)
    fun glDeleteBuffer(buffer: BufferResource)
    fun glDeleteFramebuffer(framebuffer: FramebufferResource)
    fun glDeleteProgram(program: ProgramResource)
    fun glDeleteRenderbuffer(renderbuffer: RenderbufferResource)
    fun glDeleteShader(shader: ShaderResource)
    fun glDeleteTexture(texture: TextureResource)
    fun glDepthFunc(func: Int)
    fun glDepthMask(enabled: Boolean)
    fun glDisable(cap: Int)
    fun glDisableVertexAttribArray(index: Int)
    fun glDrawBuffer(buf: Int)
    fun glDrawElements(mode: Int, count: Int, type: Int, offset: Int)
    fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int)
    fun glEnable(cap: Int)
    fun glEnableVertexAttribArray(index: Int)
    fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource)
    fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int)
    fun glGenerateMipmap(target: Int)
    fun glGetAttribLocation(program: ProgramResource, name: String): Int
    fun glGetError(): Int
    fun glGetProgrami(program: ProgramResource, pname: Int): Int
    fun glGetShaderi(shader: ShaderResource, pname: Int): Int
    fun glGetProgramInfoLog(program: ProgramResource): String
    fun glGetShaderInfoLog(shader: ShaderResource): String
    fun glGetUniformLocation(program: ProgramResource, name: String): Any?
    fun glLineWidth(width: Float)
    fun glLinkProgram(program: ProgramResource)
    fun glPointSize(size: Float)
    fun glReadBuffer(src: Int)
    fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int)
    fun glRenderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int)
    fun glShaderSource(shader: ShaderResource, source: String)
    fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?)
    fun glTexParameteri(target: Int, pname: Int, param: Int)
    fun glUniform1f(location: Any?, x: Float)
    fun glUniform1fv(location: Any?, x: FloatArray)
    fun glUniform1i(location: Any?, x: Int)
    fun glUniform1iv(location: Any?, x: IntArray)
    fun glUniform2f(location: Any?, x: Float, y: Float)
    fun glUniform3f(location: Any?, x: Float, y: Float, z: Float)
    fun glUniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float)
    fun glUniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer)
    fun glUseProgram(program: ProgramResource?)
    fun glVertexAttribDivisor(index: Int, divisor: Int)
    fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int)
    fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int)
    fun glViewport(x: Int, y: Int, width: Int, height: Int)
}

private class NoGlImpl : GlImpl {
    override fun glActiveTexture(texture: Int) { throw KoolException("No GL implementation set") }
    override fun glAttachShader(program: ProgramResource, shader: ShaderResource) { throw KoolException("No GL implementation set") }
    override fun glBindBuffer(target: Int, buffer: BufferResource?) { throw KoolException("No GL implementation set") }
    override fun glBindFramebuffer(target: Int, framebuffer: FramebufferResource?) { throw KoolException("No GL implementation set") }
    override fun glBindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) { throw KoolException("No GL implementation set") }
    override fun glBindTexture(target: Int, texture: TextureResource?) { throw KoolException("No GL implementation set") }
    override fun glBlendFunc(sfactor: Int, dfactor: Int) { throw KoolException("No GL implementation set") }
    override fun glBufferData(target: Int, data: Uint8Buffer, usage: Int) { throw KoolException("No GL implementation set") }
    override fun glBufferData(target: Int, data: Uint16Buffer, usage: Int) { throw KoolException("No GL implementation set") }
    override fun glBufferData(target: Int, data: Uint32Buffer, usage: Int) { throw KoolException("No GL implementation set") }
    override fun glBufferData(target: Int, data: Float32Buffer, usage: Int) { throw KoolException("No GL implementation set") }
    override fun glCheckFramebufferStatus(target: Int): Int { throw KoolException("No GL implementation set") }
    override fun glClear(mask: Int) { throw KoolException("No GL implementation set") }
    override fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) { throw KoolException("No GL implementation set") }
    override fun glCompileShader(shader: ShaderResource) { throw KoolException("No GL implementation set") }
    override fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) { throw KoolException("No GL implementation set") }
    override fun glCreateBuffer(): Any { throw KoolException("No GL implementation set") }
    override fun glCreateFramebuffer(): Any { throw KoolException("No GL implementation set") }
    override fun glCreateProgram(): Any { throw KoolException("No GL implementation set") }
    override fun glCreateRenderbuffer(): Any { throw KoolException("No GL implementation set") }
    override fun glCreateShader(type: Int): Any { throw KoolException("No GL implementation set") }
    override fun glCreateTexture(): Any { throw KoolException("No GL implementation set") }
    override fun glCullFace(mode: Int) { throw KoolException("No GL implementation set") }
    override fun glDeleteBuffer(buffer: BufferResource) { throw KoolException("No GL implementation set") }
    override fun glDeleteFramebuffer(framebuffer: FramebufferResource) { throw KoolException("No GL implementation set") }
    override fun glDeleteProgram(program: ProgramResource) { throw KoolException("No GL implementation set") }
    override fun glDeleteRenderbuffer(renderbuffer: RenderbufferResource) { throw KoolException("No GL implementation set") }
    override fun glDeleteShader(shader: ShaderResource) { throw KoolException("No GL implementation set") }
    override fun glDeleteTexture(texture: TextureResource) { throw KoolException("No GL implementation set") }
    override fun glDepthFunc(func: Int) { throw KoolException("No GL implementation set") }
    override fun glDepthMask(enabled: Boolean) { throw KoolException("No GL implementation set") }
    override fun glDisable(cap: Int) { throw KoolException("No GL implementation set") }
    override fun glDisableVertexAttribArray(index: Int) { throw KoolException("No GL implementation set") }
    override fun glDrawBuffer(buf: Int) { throw KoolException("No GL implementation set") }
    override fun glDrawElements(mode: Int, count: Int, type: Int, offset: Int) { throw KoolException("No GL implementation set") }
    override fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) { throw KoolException("No GL implementation set") }
    override fun glEnable(cap: Int) { throw KoolException("No GL implementation set") }
    override fun glEnableVertexAttribArray(index: Int) { throw KoolException("No GL implementation set") }
    override fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) { throw KoolException("No GL implementation set") }
    override fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) { throw KoolException("No GL implementation set") }
    override fun glGenerateMipmap(target: Int) { throw KoolException("No GL implementation set") }
    override fun glGetAttribLocation(program: ProgramResource, name: String): Int { throw KoolException("No GL implementation set") }
    override fun glGetError(): Int { throw KoolException("No GL implementation set") }
    override fun glGetProgrami(program: ProgramResource, pname: Int): Int { throw KoolException("No GL implementation set") }
    override fun glGetShaderi(shader: ShaderResource, pname: Int): Int { throw KoolException("No GL implementation set") }
    override fun glGetProgramInfoLog(program: ProgramResource): String { throw KoolException("No GL implementation set") }
    override fun glGetShaderInfoLog(shader: ShaderResource): String { throw KoolException("No GL implementation set") }
    override fun glGetUniformLocation(program: ProgramResource, name: String): Any? { throw KoolException("No GL implementation set") }
    override fun glLineWidth(width: Float) { throw KoolException("No GL implementation set") }
    override fun glLinkProgram(program: ProgramResource) { throw KoolException("No GL implementation set") }
    override fun glPointSize(size: Float) { throw KoolException("No GL implementation set") }
    override fun glReadBuffer(src: Int) { throw KoolException("No GL implementation set") }
    override fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) { throw KoolException("No GL implementation set") }
    override fun glRenderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) { throw KoolException("No GL implementation set") }
    override fun glShaderSource(shader: ShaderResource, source: String) { throw KoolException("No GL implementation set") }
    override fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) { throw KoolException("No GL implementation set") }
    override fun glTexParameteri(target: Int, pname: Int, param: Int) { throw KoolException("No GL implementation set") }
    override fun glUniform1f(location: Any?, x: Float) { throw KoolException("No GL implementation set") }
    override fun glUniform1fv(location: Any?, x: FloatArray) { throw KoolException("No GL implementation set") }
    override fun glUniform1i(location: Any?, x: Int) { throw KoolException("No GL implementation set") }
    override fun glUniform1iv(location: Any?, x: IntArray) { throw KoolException("No GL implementation set") }
    override fun glUniform2f(location: Any?, x: Float, y: Float) { throw KoolException("No GL implementation set") }
    override fun glUniform3f(location: Any?, x: Float, y: Float, z: Float) { throw KoolException("No GL implementation set") }
    override fun glUniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) { throw KoolException("No GL implementation set") }
    override fun glUniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) { throw KoolException("No GL implementation set") }
    override fun glUseProgram(program: ProgramResource?) { throw KoolException("No GL implementation set") }
    override fun glVertexAttribDivisor(index: Int, divisor: Int) { throw KoolException("No GL implementation set") }
    override fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) { throw KoolException("No GL implementation set") }
    override fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) { throw KoolException("No GL implementation set") }
    override fun glViewport(x: Int, y: Int, width: Int, height: Int) { throw KoolException("No GL implementation set") }
}
