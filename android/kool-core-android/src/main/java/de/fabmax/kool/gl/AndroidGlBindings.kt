package de.fabmax.kool.gl

import android.opengl.GLES30
import de.fabmax.kool.util.*

/**
 * Kool for Android OpenGL ES bindings
 */

class AndroidGlBindings : GlImpl {
    private val tmpIntBuf = IntArray(1)

    override fun glActiveTexture(texture: Int) =
            GLES30.glActiveTexture(texture)

    override fun glAttachShader(program: ProgramResource, shader: ShaderResource) =
            GLES30.glAttachShader((program.glRef as Int?) ?: 0, (shader.glRef as Int?) ?: 0)

    override fun glBindBuffer(target: Int, buffer: BufferResource?) =
            GLES30.glBindBuffer(target, (buffer?.glRef as Int?) ?: 0)

    override fun glBindFramebuffer(target: Int, framebuffer: FramebufferResource?) =
            GLES30.glBindFramebuffer(target, (framebuffer?.glRef as Int?) ?: 0)

    override fun glBindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) =
            GLES30.glBindRenderbuffer(target, (renderbuffer?.glRef as Int?) ?: 0)

    override fun glBindTexture(target: Int, texture: TextureResource?) =
            GLES30.glBindTexture(target, (texture?.glRef as Int?) ?: 0)

    override fun glBlendFunc(sfactor: Int, dfactor: Int) =
            GLES30.glBlendFunc(sfactor, dfactor)

    override fun glBufferData(target: Int, data: Uint8Buffer, usage: Int) =
            GLES30.glBufferData(target, data.remaining, (data as Uint8BufferImpl).buffer, usage)

    override fun glBufferData(target: Int, data: Uint16Buffer, usage: Int) =
            GLES30.glBufferData(target, data.remaining*2, (data as Uint16BufferImpl).buffer, usage)

    override fun glBufferData(target: Int, data: Uint32Buffer, usage: Int) =
            GLES30.glBufferData(target, data.remaining*4, (data as Uint32BufferImpl).buffer, usage)

    override fun glBufferData(target: Int, data: Float32Buffer, usage: Int) =
            GLES30.glBufferData(target, data.remaining * 4, (data as Float32BufferImpl).buffer, usage)

    override fun glCheckFramebufferStatus(target: Int) =
            GLES30.glCheckFramebufferStatus(target)

    override fun glClear(mask: Int) =
            GLES30.glClear(mask)

    override fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) =
            GLES30.glClearColor(red, green, blue, alpha)

    override fun glCompileShader(shader: ShaderResource) =
            GLES30.glCompileShader(shader.glRef as Int)

    override fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) =
            GLES30.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border)

    override fun glCreateBuffer(): Any {
        GLES30.glGenBuffers(1, tmpIntBuf, 0)
        return tmpIntBuf[0]
    }

    override fun glCreateFramebuffer(): Any {
        GLES30.glGenFramebuffers(1, tmpIntBuf, 0)
        return tmpIntBuf[0]
    }

    override fun glCreateProgram(): Any =
            GLES30.glCreateProgram()

    override fun glCreateRenderbuffer(): Any {
        GLES30.glGenRenderbuffers(1, tmpIntBuf, 0)
        return tmpIntBuf[0]
    }

    override fun glCreateShader(type: Int): Any =
            GLES30.glCreateShader(type)

    override fun glCreateTexture(): Any {
        GLES30.glGenTextures(1, tmpIntBuf, 0)
        return tmpIntBuf[0]
    }

    override fun glCullFace(mode: Int) =
            GLES30.glCullFace(mode)

    override fun glDeleteBuffer(buffer: BufferResource) {
        tmpIntBuf[0] = buffer.glRef as Int
        GLES30.glDeleteBuffers(1, tmpIntBuf, 0)
    }

    override fun glDeleteFramebuffer(framebuffer: FramebufferResource) {
        tmpIntBuf[0] = framebuffer.glRef as Int
        GLES30.glDeleteFramebuffers(1, tmpIntBuf, 0)
    }

    override fun glDeleteProgram(program: ProgramResource) =
            GLES30.glDeleteProgram(program.glRef as Int)

    override fun glDeleteRenderbuffer(renderbuffer: RenderbufferResource) {
        tmpIntBuf[0] = renderbuffer.glRef as Int
        GLES30.glDeleteRenderbuffers(1, tmpIntBuf, 0)
    }

    override fun glDeleteShader(shader: ShaderResource) =
            GLES30.glDeleteShader(shader.glRef as Int)

    override fun glDeleteTexture(texture: TextureResource) {
        tmpIntBuf[0] = texture.glRef as Int
        GLES30.glDeleteTextures(1, tmpIntBuf, 0)
    }

    override fun glDepthFunc(func: Int) =
            GLES30.glDepthFunc(func)

    override fun glDepthMask(enabled: Boolean) =
            GLES30.glDepthMask(enabled)

    override fun glDisable(cap: Int) =
            GLES30.glDisable(cap)

    override fun glDisableVertexAttribArray(index: Int) =
            GLES30.glDisableVertexAttribArray(index)

    override fun glDrawBuffer(buf: Int) {
        tmpIntBuf[0] = buf
        GLES30.glDrawBuffers(1, tmpIntBuf, 0)
    }

    override fun glDrawElements(mode: Int, count: Int, type: Int, offset: Int) =
            GLES30.glDrawElements(mode, count, type, offset)

    override fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) =
            GLES30.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount)

    override fun glEnable(cap: Int) =
            GLES30.glEnable(cap)

    override fun glEnableVertexAttribArray(index: Int) =
            GLES30.glEnableVertexAttribArray(index)

    override fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) =
            GLES30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.glRef as Int)

    override fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) =
            GLES30.glFramebufferTexture2D(target, attachment, textarget, texture.glRef as Int, level)

    override fun glGenerateMipmap(target: Int) =
            GLES30.glGenerateMipmap(target)

    override fun glGetAttribLocation(program: ProgramResource, name: String): Int =
            GLES30.glGetAttribLocation(program.glRef as Int, name)

    override fun glGetError(): Int =
            GLES30.glGetError()

    override fun glGetProgrami(program: ProgramResource, pname: Int): Int {
        GLES30.glGetProgramiv(program.glRef as Int, pname, tmpIntBuf, 0)
        return tmpIntBuf[0]
    }

    override fun glGetShaderi(shader: ShaderResource, pname: Int): Int {
        GLES30.glGetShaderiv(shader.glRef as Int, pname, tmpIntBuf, 0)
        return tmpIntBuf[0]
    }

    override fun glGetProgramInfoLog(program: ProgramResource): String =
            GLES30.glGetProgramInfoLog(program.glRef as Int)

    override fun glGetShaderInfoLog(shader: ShaderResource): String =
            GLES30.glGetShaderInfoLog(shader.glRef as Int)

    override fun glGetUniformLocation(program: ProgramResource, name: String): Any? =
            GLES30.glGetUniformLocation(program.glRef as Int, name)

    override fun glLineWidth(width: Float) =
            GLES30.glLineWidth(width)

    override fun glLinkProgram(program: ProgramResource) =
            GLES30.glLinkProgram(program.glRef as Int)

    override fun glPointSize(size: Float) {
        // not supported by GLES and silently ignored
    }

    override fun glReadBuffer(src: Int)=
            GLES30.glReadBuffer(src)

    override fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) =
            GLES30.glRenderbufferStorage(target, internalformat, width, height)

    override fun glRenderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) =
            GLES30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height)

    override fun glShaderSource(shader: ShaderResource, source: String) =
            GLES30.glShaderSource(shader.glRef as Int, source)

    override fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) =
            GLES30.glTexImage2D(target, level, internalformat, width, height, border, format, type, (pixels as Uint8BufferImpl?)?.buffer)

    override fun glTexParameteri(target: Int, pname: Int, param: Int) =
            GLES30.glTexParameteri(target, pname, param)

    override fun glUniform1f(location: Any?, x: Float) =
            GLES30.glUniform1f(location as Int, x)

    override fun glUniform1fv(location: Any?, x: FloatArray) =
            GLES30.glUniform1fv(location as Int, x.size, x, 0)

    override fun glUniform1i(location: Any?, x: Int) =
            GLES30.glUniform1i(location as Int, x)

    override fun glUniform1iv(location: Any?, x: IntArray) =
            GLES30.glUniform1iv(location as Int, x.size, x, 0)

    override fun glUniform2f(location: Any?, x: Float, y: Float) =
            GLES30.glUniform2f(location as Int, x, y)

    override fun glUniform3f(location: Any?, x: Float, y: Float, z: Float) =
            GLES30.glUniform3f(location as Int, x, y, z)

    override fun glUniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) =
            GLES30.glUniform4f(location as Int, x, y, z, w)

    override fun glUniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) =
            GLES30.glUniformMatrix4fv(location as Int, value.capacity / 16, transpose, (value as Float32BufferImpl).buffer)

    override fun glUseProgram(program: ProgramResource?) =
            GLES30.glUseProgram((program?.glRef as Int?) ?: 0)

    override fun glVertexAttribDivisor(index: Int, divisor: Int) =
            GLES30.glVertexAttribDivisor(index, divisor)

    override fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) =
            GLES30.glVertexAttribPointer(index, size, type, normalized, stride, offset)

    override fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) =
            GLES30.glVertexAttribIPointer(index, size, type, stride, offset)

    override fun glViewport(x: Int, y: Int, width: Int, height: Int) =
            GLES30.glViewport(x, y, width, height)

}
