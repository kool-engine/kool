package de.fabmax.kool.gl

import de.fabmax.kool.JsImpl
import de.fabmax.kool.KoolException
import de.fabmax.kool.util.*
import org.khronos.webgl.*
import org.w3c.dom.HTMLImageElement

/**
 * @author fabmax
 */

actual fun glActiveTexture(texture: Int) = JsImpl.gl.activeTexture(texture)

actual fun glAttachShader(program: ProgramResource, shader: ShaderResource) =
        JsImpl.gl.attachShader(program.glRef as WebGLProgram, shader.glRef as WebGLShader)

actual fun glBindBuffer(target: Int, buffer: BufferResource?) =
        JsImpl.gl.bindBuffer(target, buffer?.glRef as WebGLBuffer?)

actual fun glBindFramebuffer(target: Int, framebuffer: FramebufferResource?) =
        JsImpl.gl.bindFramebuffer(target, framebuffer?.glRef as WebGLFramebuffer?)

actual fun glBindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) =
        JsImpl.gl.bindRenderbuffer(target, renderbuffer?.glRef as WebGLRenderbuffer?)

actual fun glBindTexture(target: Int, texture: TextureResource?) =
        JsImpl.gl.bindTexture(target, texture?.glRef as WebGLTexture?)

actual fun glBlendFunc(sfactor: Int, dfactor: Int) = JsImpl.gl.blendFunc(sfactor, dfactor)

actual fun glBufferData(target: Int, data: Uint8Buffer, usage: Int) =
        JsImpl.gl.bufferData(target, (data as Uint8BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Uint16Buffer, usage: Int) =
        JsImpl.gl.bufferData(target, (data as Uint16BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Uint32Buffer, usage: Int) =
        JsImpl.gl.bufferData(target, (data as Uint32BufferImpl).buffer, usage)

actual fun glBufferData(target: Int, data: Float32Buffer, usage: Int) =
        JsImpl.gl.bufferData(target, (data as Float32BufferImpl).buffer, usage)

actual fun glCheckFramebufferStatus(target: Int) =
        JsImpl.gl.checkFramebufferStatus(target)

actual fun glClear(mask: Int) = JsImpl.gl.clear(mask)

actual fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) =
        JsImpl.gl.clearColor(red, green, blue, alpha)

actual fun glCompileShader(shader: ShaderResource) = JsImpl.gl.compileShader(shader.glRef as WebGLShader)

actual fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) =
        JsImpl.gl.copyTexImage2D(target, level, internalformat, x, y, width, height, border)

actual fun glCreateBuffer(): Any = JsImpl.gl.createBuffer()!!

actual fun glCreateFramebuffer(): Any = JsImpl.gl.createFramebuffer()!!

actual fun glCreateRenderbuffer(): Any = JsImpl.gl.createRenderbuffer()!!

actual fun glCreateProgram(): Any = JsImpl.gl.createProgram()!!

actual fun glCreateShader(type: Int): Any = JsImpl.gl.createShader(type)!!

actual fun glCreateTexture(): Any = JsImpl.gl.createTexture()!!

actual fun glCullFace(mode: Int) = JsImpl.gl.cullFace(mode)

actual fun glDeleteBuffer(buffer: BufferResource) = JsImpl.gl.deleteBuffer(buffer.glRef as WebGLBuffer)

actual fun glDeleteFramebuffer(framebuffer: FramebufferResource) =
        JsImpl.gl.deleteFramebuffer(framebuffer.glRef as WebGLFramebuffer)

actual fun glDeleteProgram(program: ProgramResource) = JsImpl.gl.deleteProgram(program.glRef as WebGLProgram)

actual fun glDeleteRenderbuffer(renderbuffer: RenderbufferResource) =
        JsImpl.gl.deleteRenderbuffer(renderbuffer.glRef as WebGLRenderbuffer)

actual fun glDeleteShader(shader: ShaderResource) = JsImpl.gl.deleteShader(shader.glRef as WebGLShader)

actual fun glDeleteTexture(texture: TextureResource) = JsImpl.gl.deleteTexture(texture.glRef as WebGLTexture)

actual fun glDepthFunc(func: Int) = JsImpl.gl.depthFunc(func)

actual fun glDepthMask(enabled: Boolean) = JsImpl.gl.depthMask(enabled)

actual fun glDisable(cap: Int) = JsImpl.gl.disable(cap)

actual fun glDisableVertexAttribArray(index: Int) = JsImpl.gl.disableVertexAttribArray(index)

actual fun glDrawBuffer(buf: Int) {
    if (JsImpl.isWebGl2Context) {
        (JsImpl.gl as WebGL2RenderingContext).drawBuffers(intArrayOf(buf))
    } // else just ignore this call
}

actual fun glDrawElements(mode: Int, count: Int, type: Int, offset: Int) =
        JsImpl.gl.drawElements(mode, count, type, offset)

actual fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) {
    if (JsImpl.isWebGl2Context) {
        (JsImpl.gl as WebGL2RenderingContext).drawElementsInstanced(mode, count, type, indicesOffset, instanceCount)
    } else {
        throw KoolException("This function requires WebGL2 support")
    }
}

actual fun glEnable(cap: Int) = JsImpl.gl.enable(cap)

actual fun glEnableVertexAttribArray(index: Int) = JsImpl.gl.enableVertexAttribArray(index)

actual fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) =
        JsImpl.gl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer.glRef as WebGLRenderbuffer)

actual fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) =
        JsImpl.gl.framebufferTexture2D(target, attachment, textarget, texture.glRef as WebGLTexture, level)

actual fun glGenerateMipmap(target: Int) = JsImpl.gl.generateMipmap(target)

actual fun glGetAttribLocation(program: ProgramResource, name: String): Int =
        JsImpl.gl.getAttribLocation(program.glRef as WebGLProgram, name)

actual fun glGetError(): Int = JsImpl.gl.getError()

actual fun glGetProgrami(program: ProgramResource, pname: Int): Int {
    val res = JsImpl.gl.getProgramParameter(program.glRef as WebGLProgram, pname)
    if (pname == GL_LINK_STATUS) {
        return if (res as Boolean) {
            GL_TRUE
        } else {
            GL_FALSE
        }
    }
    return 0
}

actual fun glGetShaderi(shader: ShaderResource, pname: Int): Int {
    val res = JsImpl.gl.getShaderParameter(shader.glRef as WebGLShader, pname)
    if (pname == GL_COMPILE_STATUS) {
        return if (res as Boolean) {
            GL_TRUE
        } else {
            GL_FALSE
        }
    }
    return 0
}

actual fun glGetProgramInfoLog(program: ProgramResource): String =
        JsImpl.gl.getProgramInfoLog(program.glRef as WebGLProgram) ?: ""

actual fun glGetShaderInfoLog(shader: ShaderResource): String =
        JsImpl.gl.getShaderInfoLog(shader.glRef as WebGLShader) ?: ""

actual fun glGetUniformLocation(program: ProgramResource, name: String): Any? =
        JsImpl.gl.getUniformLocation(program.glRef as WebGLProgram, name)

actual fun glLineWidth(width: Float) = JsImpl.gl.lineWidth(width)

actual fun glLinkProgram(program: ProgramResource) = JsImpl.gl.linkProgram(program.glRef as WebGLProgram)

actual fun glPointSize(size: Float) {
    // not supported by WebGL and silently ignored
}

actual fun glReadBuffer(src: Int) {
    if (JsImpl.isWebGl2Context) {
        (JsImpl.gl as WebGL2RenderingContext).readBuffer(src)
    } // else just ignore this call
}

actual fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) =
        JsImpl.gl.renderbufferStorage(target, internalformat, width, height)

actual fun glRenderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) {
    if (JsImpl.isWebGl2Context) {
        (JsImpl.gl as WebGL2RenderingContext).renderbufferStorageMultisample(target, samples, internalformat, width, height)
    } else {
        throw KoolException("This function requires WebGL2 support")
    }
}

actual fun glShaderSource(shader: ShaderResource, source: String) =
        JsImpl.gl.shaderSource(shader.glRef as WebGLShader, source)

actual fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) =
        JsImpl.gl.texImage2D(target, level, internalformat, width, height, border, format, type, (pixels as Uint8BufferImpl?)?.buffer)

fun glTexImage2D(target: Int, level: Int, internalformat: Int, format: Int, type: Int, pixels: Any?) {
    // pre-multiply alpha for image object
    JsImpl.gl.pixelStorei(WebGLRenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, GL_TRUE)
    JsImpl.gl.texImage2D(target, level, internalformat, format, type, pixels as HTMLImageElement?)
    JsImpl.gl.pixelStorei(WebGLRenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, GL_FALSE)
}

actual fun glTexParameteri(target: Int, pname: Int, param: Int) = JsImpl.gl.texParameteri(target, pname, param)

actual fun glUniform1f(location: Any?, x: Float) = JsImpl.gl.uniform1f((location as WebGLUniformLocation?), x)

actual fun glUniform1fv(location: Any?, x: FloatArray) {
    val tmp = Array(x.size) { i -> x[i] }
    JsImpl.gl.uniform1fv((location as WebGLUniformLocation?), tmp)
}

actual fun glUniform1i(location: Any?, x: Int) = JsImpl.gl.uniform1i((location as WebGLUniformLocation?), x)

actual fun glUniform1iv(location: Any?, x: IntArray) {
    val tmp = Array(x.size) { i -> x[i] }
    JsImpl.gl.uniform1iv((location as WebGLUniformLocation?), tmp)
}

actual fun glUniform2f(location: Any?, x: Float, y: Float) =
        JsImpl.gl.uniform2f((location as WebGLUniformLocation?), x, y)

actual fun glUniform3f(location: Any?, x: Float, y: Float, z: Float) =
        JsImpl.gl.uniform3f((location as WebGLUniformLocation?), x, y, z)

actual fun glUniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) =
        JsImpl.gl.uniform4f((location as WebGLUniformLocation?), x, y, z, w)

actual fun glUniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) =
        JsImpl.gl.uniformMatrix4fv((location as WebGLUniformLocation?), transpose, (value as Float32BufferImpl).buffer)

actual fun glUseProgram(program: ProgramResource?) = JsImpl.gl.useProgram(program?.glRef as WebGLProgram?)

actual fun glVertexAttribDivisor(index: Int, divisor: Int) {
    if (JsImpl.isWebGl2Context) {
        (JsImpl.gl as WebGL2RenderingContext).vertexAttribDivisor(index, divisor)
    } else {
        throw KoolException("This function requires WebGL2 support")
    }
}

actual fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) =
        JsImpl.gl.vertexAttribPointer(index, size, type, normalized, stride, offset)

actual fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int) {
    if (JsImpl.isWebGl2Context) {
        (JsImpl.gl as WebGL2RenderingContext).vertexAttribIPointer(index, size, type, stride, offset)
    } else {
        throw KoolException("This function requires WebGL2 support")
    }
}

actual fun glViewport(x: Int, y: Int, width: Int, height: Int) = JsImpl.gl.viewport(x, y, width, height)

actual fun isValidUniformLocation(location: Any?): Boolean = location != null && location is WebGLUniformLocation

abstract external class WebGL2RenderingContext : WebGLRenderingContext {
    fun drawBuffers(buffers: IntArray)
    fun drawElementsInstanced(mode: Int, count: Int, type: Int, offset: Int, instanceCount: Int)
    fun readBuffer(src: Int)
    fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int)
    fun vertexAttribDivisor(index: Int, divisor: Int)
    fun vertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int)
}
