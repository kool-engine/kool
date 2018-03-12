package de.fabmax.kool.gl

import android.opengl.GLES30
import de.fabmax.kool.util.*

/**
 * Kool OpenGL ES bindings for Android
 */

private val tmpIntBuf = IntArray(1)

actual fun glActiveTexture(texture: Int) = 
        GLES30.glActiveTexture(texture)

actual fun glAttachShader(program: ProgramResource, shader: ShaderResource) =
        GLES30.glAttachShader((program.glRef as Int?) ?: 0, (shader.glRef as Int?) ?: 0)

actual fun glBindBuffer(target: Int, buffer: BufferResource?) =
        GLES30.glBindBuffer(target, (buffer?.glRef as Int?) ?: 0)

actual fun glBindFramebuffer(target: Int, framebuffer: FramebufferResource?) =
        GLES30.glBindFramebuffer(target, (framebuffer?.glRef as Int?) ?: 0)

actual fun glBindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) =
        GLES30.glBindRenderbuffer(target, (renderbuffer?.glRef as Int?) ?: 0)

actual fun glBindTexture(target: Int, texture: TextureResource?) =
        GLES30.glBindTexture(target, (texture?.glRef as Int?) ?: 0)

actual fun glBlendFunc(sfactor: Int, dfactor: Int) =
        GLES30.glBlendFunc(sfactor, dfactor)

actual fun glBufferData(target: Int, data: Uint8Buffer, usage: Int) =
        GLES30.glBufferData(target, data.remaining, (data as Uint8BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Uint16Buffer, usage: Int) =
        GLES30.glBufferData(target, data.remaining*2, (data as Uint16BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Uint32Buffer, usage: Int) =
        GLES30.glBufferData(target, data.remaining*4, (data as Uint32BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Float32Buffer, usage: Int) =
        GLES30.glBufferData(target, data.remaining * 4, (data as Float32BufferImpl).buffer, usage)

actual fun glCheckFramebufferStatus(target: Int) =
        GLES30.glCheckFramebufferStatus(target)

actual fun glClear(mask: Int) =
        GLES30.glClear(mask)

actual fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) =
        GLES30.glClearColor(red, green, blue, alpha)

actual fun glCompileShader(shader: ShaderResource) =
        GLES30.glCompileShader(shader.glRef as Int)

actual fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) =
        GLES30.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border)

actual fun glCreateBuffer(): Any {
    GLES30.glGenBuffers(1, tmpIntBuf, 0)
    return tmpIntBuf[0]
}

actual fun glCreateFramebuffer(): Any {
    GLES30.glGenFramebuffers(1, tmpIntBuf, 0)
    return tmpIntBuf[0]
}

actual fun glCreateProgram(): Any =
        GLES30.glCreateProgram()

actual fun glCreateRenderbuffer(): Any {
    GLES30.glGenRenderbuffers(1, tmpIntBuf, 0)
    return tmpIntBuf[0]
}

actual fun glCreateShader(type: Int): Any =
        GLES30.glCreateShader(type)

actual fun glCreateTexture(): Any {
    GLES30.glGenTextures(1, tmpIntBuf, 0)
    return tmpIntBuf[0]
}

actual fun glCullFace(mode: Int) =
        GLES30.glCullFace(mode)

actual fun glDeleteBuffer(buffer: BufferResource) {
    tmpIntBuf[0] = buffer.glRef as Int
    GLES30.glDeleteBuffers(1, tmpIntBuf, 0)
}

actual fun glDeleteFramebuffer(framebuffer: FramebufferResource) {
    tmpIntBuf[0] = framebuffer.glRef as Int
    GLES30.glDeleteFramebuffers(1, tmpIntBuf, 0)
}

actual fun glDeleteProgram(program: ProgramResource) =
        GLES30.glDeleteProgram(program.glRef as Int)

actual fun glDeleteRenderbuffer(renderbuffer: RenderbufferResource) {
    tmpIntBuf[0] = renderbuffer.glRef as Int
    GLES30.glDeleteRenderbuffers(1, tmpIntBuf, 0)
}

actual fun glDeleteShader(shader: ShaderResource) =
        GLES30.glDeleteShader(shader.glRef as Int)

actual fun glDeleteTexture(texture: TextureResource) {
    tmpIntBuf[0] = texture.glRef as Int
    GLES30.glDeleteTextures(1, tmpIntBuf, 0)
}

actual fun glDepthFunc(func: Int) =
        GLES30.glDepthFunc(func)

actual fun glDepthMask(enabled: Boolean) =
        GLES30.glDepthMask(enabled)

actual fun glDisable(cap: Int) =
        GLES30.glDisable(cap)

actual fun glDisableVertexAttribArray(index: Int) =
        GLES30.glDisableVertexAttribArray(index)

actual fun glDrawBuffer(buf: Int) {
    tmpIntBuf[0] = buf
    GLES30.glDrawBuffers(1, tmpIntBuf, 0)
}

actual fun glDrawElements(mode: Int, count: Int, type: Int, offset: Int) =
        GLES30.glDrawElements(mode, count, type, offset)

actual fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) =
        GLES30.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount)

actual fun glEnable(cap: Int) =
        GLES30.glEnable(cap)

actual fun glEnableVertexAttribArray(index: Int) =
        GLES30.glEnableVertexAttribArray(index)

actual fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) =
        GLES30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.glRef as Int)

actual fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) =
        GLES30.glFramebufferTexture2D(target, attachment, textarget, texture.glRef as Int, level)

actual fun glGenerateMipmap(target: Int) =
        GLES30.glGenerateMipmap(target)

actual fun glGetAttribLocation(program: ProgramResource, name: String): Int =
        GLES30.glGetAttribLocation(program.glRef as Int, name)

actual fun glGetError(): Int =
        GLES30.glGetError()

actual fun glGetProgrami(program: ProgramResource, pname: Int): Int {
    GLES30.glGetProgramiv(program.glRef as Int, pname, tmpIntBuf, 0)
    return tmpIntBuf[0]
}

actual fun glGetShaderi(shader: ShaderResource, pname: Int): Int {
    GLES30.glGetShaderiv(shader.glRef as Int, pname, tmpIntBuf, 0)
    return tmpIntBuf[0]
}

actual fun glGetProgramInfoLog(program: ProgramResource): String =
        GLES30.glGetProgramInfoLog(program.glRef as Int)

actual fun glGetShaderInfoLog(shader: ShaderResource): String =
        GLES30.glGetShaderInfoLog(shader.glRef as Int)

actual fun glGetUniformLocation(program: ProgramResource, name: String): Any? =
        GLES30.glGetUniformLocation(program.glRef as Int, name)

actual fun glLineWidth(width: Float) =
        GLES30.glLineWidth(width)

actual fun glLinkProgram(program: ProgramResource) =
        GLES30.glLinkProgram(program.glRef as Int)

actual fun glPointSize(size: Float) {
    // not supported by GLES and silently ignored
}

actual fun glReadBuffer(src: Int)=
        GLES30.glReadBuffer(src)

actual fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) =
        GLES30.glRenderbufferStorage(target, internalformat, width, height)

actual fun glRenderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) =
        GLES30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height)

actual fun glShaderSource(shader: ShaderResource, source: String) =
        GLES30.glShaderSource(shader.glRef as Int, source)

actual fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) =
        GLES30.glTexImage2D(target, level, internalformat, width, height, border, format, type, (pixels as Uint8BufferImpl?)?.buffer)

actual fun glTexParameteri(target: Int, pname: Int, param: Int) =
        GLES30.glTexParameteri(target, pname, param)

actual fun glUniform1f(location: Any?, x: Float) =
        GLES30.glUniform1f(location as Int, x)

actual fun glUniform1fv(location: Any?, x: FloatArray) =
        GLES30.glUniform1fv(location as Int, x.size, x, 0)

actual fun glUniform1i(location: Any?, x: Int) =
        GLES30.glUniform1i(location as Int, x)

actual fun glUniform1iv(location: Any?, x: IntArray) =
        GLES30.glUniform1iv(location as Int, x.size, x, 0)

actual fun glUniform2f(location: Any?, x: Float, y: Float) =
        GLES30.glUniform2f(location as Int, x, y)

actual fun glUniform3f(location: Any?, x: Float, y: Float, z: Float) =
        GLES30.glUniform3f(location as Int, x, y, z)

actual fun glUniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) =
        GLES30.glUniform4f(location as Int, x, y, z, w)

actual fun glUniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) =
        GLES30.glUniformMatrix4fv(location as Int, value.capacity / 16, transpose, (value as Float32BufferImpl).buffer)

actual fun glUseProgram(program: ProgramResource?) =
        GLES30.glUseProgram((program?.glRef as Int?) ?: 0)

actual fun glVertexAttribDivisor(index: Int, divisor: Int) =
        GLES30.glVertexAttribDivisor(index, divisor)

actual fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) =
        GLES30.glVertexAttribPointer(index, size, type, normalized, stride, offset)

actual fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) =
        GLES30.glVertexAttribIPointer(index, size, type, stride, offset)

actual fun glViewport(x: Int, y: Int, width: Int, height: Int) =
        GLES30.glViewport(x, y, width, height)

actual fun isValidUniformLocation(location: Any?): Boolean = (location as? Int ?: -1) >= 0
