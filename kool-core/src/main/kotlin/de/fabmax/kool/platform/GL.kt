package de.fabmax.kool.platform

import de.fabmax.kool.*

@Suppress("unused")

/**
 * @author fabmax
 */
class GL private constructor() {
    companion object {
        private val impl = Platform.getGlImpl()

        fun isAvailable(): Boolean {
            return impl.isAvailable()
        }

        fun activeTexture(texture: Int) {
            impl.activeTexture(texture)
        }

        fun attachShader(program: ProgramResource, shader: ShaderResource) {
            impl.attachShader(program, shader)
        }

        fun bindBuffer(target: Int, buffer: BufferResource?) {
            impl.bindBuffer(target, buffer)
        }

        fun bindFramebuffer(target: Int, framebuffer: FramebufferResource?) {
            impl.bindFramebuffer(target, framebuffer)
        }

        fun bindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?) {
            impl.bindRenderbuffer(target, renderbuffer)
        }

        fun bindTexture(target: Int, texture: TextureResource?) {
            impl.bindTexture(target, texture)
        }

        fun blendFunc(sfactor: Int, dfactor: Int) {
            impl.blendFunc(sfactor, dfactor)
        }

        fun bufferData(target: Int, data: Uint8Buffer, usage: Int) {
            impl.bufferData(target, data, usage)
        }

        fun bufferData(target: Int, data: Uint16Buffer, usage: Int) {
            impl.bufferData(target, data, usage)
        }

        fun bufferData(target: Int, data: Uint32Buffer, usage: Int) {
            impl.bufferData(target, data, usage)
        }

        fun bufferData(target: Int, data: Float32Buffer, usage: Int) {
            impl.bufferData(target, data, usage)
        }

        fun clear(mask: Int) {
            impl.clear(mask)
        }

        fun clearColor(red: Float, green: Float, blue: Float, alpha: Float) {
            impl.clearColor(red, green, blue, alpha)
        }

        fun compileShader(shader: ShaderResource) {
            impl.compileShader(shader)
        }

        fun createBuffer(): Any {
            return impl.createBuffer()
        }

        fun createFramebuffer(): Any {
            return impl.createFramebuffer()
        }

        fun createRenderbuffer(): Any {
            return impl.createRenderbuffer()
        }

        fun createProgram(): Any {
            return impl.createProgram()
        }

        fun createShader(type: Int): Any {
            return impl.createShader(type)
        }

        fun createTexture(): Any {
            return impl.createTexture()
        }

        fun deleteBuffer(buffer: BufferResource) {
            impl.deleteBuffer(buffer)
        }

        fun deleteFramebuffer(framebuffer: FramebufferResource) {
            impl.deleteFramebuffer(framebuffer)
        }

        fun deleteProgram(program: ProgramResource) {
            impl.deleteProgram(program)
        }

        fun deleteRenderbuffer(renderbuffer: RenderbufferResource) {
            impl.deleteRenderbuffer(renderbuffer)
        }

        fun deleteShader(shader: ShaderResource) {
            impl.deleteShader(shader)
        }

        fun deleteTexture(texture: TextureResource) {
            impl.deleteTexture(texture)
        }

        fun depthFunc(func: Int) {
            impl.depthFunc(func)
        }

        fun depthMask(enabled: Boolean) {
            impl.depthMask(enabled)
        }

        fun disable(cap: Int) {
            impl.disable(cap)
        }

        fun disableVertexAttribArray(index: Int) {
            impl.disableVertexAttribArray(index)
        }

        fun drawElements(mode: Int, count: Int, type: Int, offset: Int) {
            impl.drawElements(mode, count, type, offset)
        }

        fun drawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int) {
            impl.drawElementsInstanced(mode, count, type, indicesOffset, instanceCount)
        }

        fun enable(cap: Int) {
            impl.enable(cap)
        }

        fun enableVertexAttribArray(index: Int) {
            impl.enableVertexAttribArray(index)
        }

        fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource) {
            impl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
        }

        fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int) {
            impl.framebufferTexture2D(target, attachment, textarget, texture, level)
        }

        fun generateMipmap(target: Int) {
            impl.generateMipmap(target)
        }

        fun getAttribLocation(program: ProgramResource, name: String): Int {
            return impl.getAttribLocation(program, name)
        }

        fun getError(): Int {
            return impl.getError()
        }

        fun getProgrami(program: ProgramResource, pname: Int): Int {
            return impl.getProgrami(program, pname)
        }

        fun getShaderi(shader: ShaderResource, pname: Int): Int {
            return impl.getShaderi(shader, pname)
        }

        fun getProgramInfoLog(program: ProgramResource): String {
            return impl.getProgramInfoLog(program)
        }

        fun getShaderInfoLog(shader: ShaderResource): String {
            return impl.getShaderInfoLog(shader)
        }

        fun getUniformLocation(program: ProgramResource, name: String): Any? {
            return impl.getUniformLocation(program, name)
        }

        fun lineWidth(width: Float) {
            impl.lineWidth(width)
        }

        fun linkProgram(program: ProgramResource) {
            impl.linkProgram(program)
        }

        fun pointSize(size: Float) {
            impl.pointSize(size)
        }

        fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) {
            impl.renderbufferStorage(target, internalformat, width, height)
        }

        fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) {
            impl.renderbufferStorageMultisample(target, samples, internalformat, width, height)
        }

        fun shaderSource(shader: ShaderResource, source: String) {
            impl.shaderSource(shader, source)
        }

        fun texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?) {
            impl.texImage2D(target, level, internalformat, width, height, border, format, type, pixels)
        }

        fun texParameteri(target: Int, pname: Int, param: Int) {
            impl.texParameteri(target, pname, param)
        }

        fun uniform1f(location: Any?, x: Float) {
            impl.uniform1f(location, x)
        }

        fun uniform1i(location: Any?, x: Int) {
            impl.uniform1i(location, x)
        }

        fun uniform2f(location: Any?, x: Float, y: Float) {
            impl.uniform2f(location, x, y)
        }

        fun uniform3f(location: Any?, x: Float, y: Float, z: Float) {
            impl.uniform3f(location, x, y, z)
        }

        fun uniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float) {
            impl.uniform4f(location, x, y, z, w)
        }

        fun uniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer) {
            impl.uniformMatrix4fv(location, transpose, value)
        }

        fun useProgram(program: ProgramResource?) {
            impl.useProgram(program)
        }

        fun vertexAttribDivisor(index: Int, divisor: Int) {
            impl.vertexAttribDivisor(index, divisor)
        }

        fun vertexAttribPointer(indx: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
            impl.vertexAttribPointer(indx, size, type, normalized, stride, offset)
        }

        fun viewport(x: Int, y: Int, width: Int, height: Int) {
            impl.viewport(x, y, width, height)
        }

        val ACTIVE_TEXTURE = 0x84E0
        val DEPTH_BUFFER_BIT = 0x00000100
        val STENCIL_BUFFER_BIT = 0x00000400
        val COLOR_BUFFER_BIT = 0x00004000
        val FALSE = 0
        val TRUE = 1
        val POINTS = 0x0000
        val LINES = 0x0001
        val LINE_LOOP = 0x0002
        val LINE_STRIP = 0x0003
        val TRIANGLES = 0x0004
        val TRIANGLE_STRIP = 0x0005
        val TRIANGLE_FAN = 0x0006
        val ZERO = 0
        val ONE = 1
        val SRC_COLOR = 0x0300
        val ONE_MINUS_SRC_COLOR = 0x0301
        val SRC_ALPHA = 0x0302
        val ONE_MINUS_SRC_ALPHA = 0x0303
        val DST_ALPHA = 0x0304
        val ONE_MINUS_DST_ALPHA = 0x0305
        val DST_COLOR = 0x0306
        val ONE_MINUS_DST_COLOR = 0x0307
        val SRC_ALPHA_SATURATE = 0x0308
        val FUNC_ADD = 0x8006
        val BLEND_EQUATION = 0x8009
        val BLEND_EQUATION_RGB = 0x8009   /* same as BLEND_EQUATION */
        val BLEND_EQUATION_ALPHA = 0x883D
        val FUNC_SUBTRACT = 0x800A
        val FUNC_REVERSE_SUBTRACT = 0x800B
        val BLEND_DST_RGB = 0x80C8
        val BLEND_SRC_RGB = 0x80C9
        val BLEND_DST_ALPHA = 0x80CA
        val BLEND_SRC_ALPHA = 0x80CB
        val CONSTANT_COLOR = 0x8001
        val ONE_MINUS_CONSTANT_COLOR = 0x8002
        val CONSTANT_ALPHA = 0x8003
        val ONE_MINUS_CONSTANT_ALPHA = 0x8004
        val BLEND_COLOR = 0x8005
        val ARRAY_BUFFER = 0x8892
        val ELEMENT_ARRAY_BUFFER = 0x8893
        val ARRAY_BUFFER_BINDING = 0x8894
        val ELEMENT_ARRAY_BUFFER_BINDING = 0x8895
        val STREAM_DRAW = 0x88E0
        val STATIC_DRAW = 0x88E4
        val DYNAMIC_DRAW = 0x88E8
        val BUFFER_SIZE = 0x8764
        val BUFFER_USAGE = 0x8765
        val CURRENT_VERTEX_ATTRIB = 0x8626
        val FRONT = 0x0404
        val BACK = 0x0405
        val FRONT_AND_BACK = 0x0408
        val TEXTURE_2D = 0x0DE1
        val CULL_FACE = 0x0B44
        val BLEND = 0x0BE2
        val DITHER = 0x0BD0
        val STENCIL_TEST = 0x0B90
        val DEPTH_TEST = 0x0B71
        val SCISSOR_TEST = 0x0C11
        val POLYGON_OFFSET_FILL = 0x8037
        val SAMPLE_ALPHA_TO_COVERAGE = 0x809E
        val SAMPLE_COVERAGE = 0x80A0
        val NO_ERROR = 0
        val INVALID_ENUM = 0x0500
        val INVALID_VALUE = 0x0501
        val INVALID_OPERATION = 0x0502
        val OUT_OF_MEMORY = 0x0505
        val CW = 0x0900
        val CCW = 0x0901
        val LINE_WIDTH = 0x0B21
        val ALIASED_POINT_SIZE_RANGE = 0x846D
        val ALIASED_LINE_WIDTH_RANGE = 0x846E
        val CULL_FACE_MODE = 0x0B45
        val FRONT_FACE = 0x0B46
        val DEPTH_RANGE = 0x0B70
        val DEPTH_WRITEMASK = 0x0B72
        val DEPTH_CLEAR_VALUE = 0x0B73
        val DEPTH_FUNC = 0x0B74
        val STENCIL_CLEAR_VALUE = 0x0B91
        val STENCIL_FUNC = 0x0B92
        val STENCIL_FAIL = 0x0B94
        val STENCIL_PASS_DEPTH_FAIL = 0x0B95
        val STENCIL_PASS_DEPTH_PASS = 0x0B96
        val STENCIL_REF = 0x0B97
        val STENCIL_VALUE_MASK = 0x0B93
        val STENCIL_WRITEMASK = 0x0B98
        val STENCIL_BACK_FUNC = 0x8800
        val STENCIL_BACK_FAIL = 0x8801
        val STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802
        val STENCIL_BACK_PASS_DEPTH_PASS = 0x8803
        val STENCIL_BACK_REF = 0x8CA3
        val STENCIL_BACK_VALUE_MASK = 0x8CA4
        val STENCIL_BACK_WRITEMASK = 0x8CA5
        val VIEWPORT = 0x0BA2
        val SCISSOR_BOX = 0x0C10
        val COLOR_CLEAR_VALUE = 0x0C22
        val COLOR_WRITEMASK = 0x0C23
        val UNPACK_ALIGNMENT = 0x0CF5
        val PACK_ALIGNMENT = 0x0D05
        val MAX_TEXTURE_SIZE = 0x0D33
        val MAX_VIEWPORT_DIMS = 0x0D3A
        val SUBPIXEL_BITS = 0x0D50
        val RED_BITS = 0x0D52
        val GREEN_BITS = 0x0D53
        val BLUE_BITS = 0x0D54
        val ALPHA_BITS = 0x0D55
        val DEPTH_BITS = 0x0D56
        val STENCIL_BITS = 0x0D57
        val POLYGON_OFFSET_UNITS = 0x2A00
        val POLYGON_OFFSET_FACTOR = 0x8038
        val TEXTURE_BINDING_2D = 0x8069
        val SAMPLE_BUFFERS = 0x80A8
        val SAMPLES = 0x80A9
        val SAMPLE_COVERAGE_VALUE = 0x80AA
        val SAMPLE_COVERAGE_INVERT = 0x80AB
        val NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2
        val COMPRESSED_TEXTURE_FORMATS = 0x86A3
        val DONT_CARE = 0x1100
        val FASTEST = 0x1101
        val NICEST = 0x1102
        val GENERATE_MIPMAP_HINT = 0x8192
        val BYTE = 0x1400
        val UNSIGNED_BYTE = 0x1401
        val SHORT = 0x1402
        val UNSIGNED_SHORT = 0x1403
        val INT = 0x1404
        val UNSIGNED_INT = 0x1405
        val FLOAT = 0x1406
        val FIXED = 0x140C
        val DEPTH_COMPONENT = 0x1902
        val ALPHA = 0x1906
        val RGB = 0x1907
        val RGBA = 0x1908
        val LUMINANCE = 0x1909
        val LUMINANCE_ALPHA = 0x190A
        val UNSIGNED_SHORT_4_4_4_4 = 0x8033
        val UNSIGNED_SHORT_5_5_5_1 = 0x8034
        val UNSIGNED_SHORT_5_6_5 = 0x8363
        val FRAGMENT_SHADER = 0x8B30
        val VERTEX_SHADER = 0x8B31
        val MAX_VERTEX_ATTRIBS = 0x8869
        val MAX_VERTEX_UNIFORM_VECTORS = 0x8DFB
        val MAX_VARYING_VECTORS = 0x8DFC
        val MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D
        val MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C
        val MAX_TEXTURE_IMAGE_UNITS = 0x8872
        val MAX_FRAGMENT_UNIFORM_VECTORS = 0x8DFD
        val SHADER_TYPE = 0x8B4F
        val DELETE_STATUS = 0x8B80
        val LINK_STATUS = 0x8B82
        val VALIDATE_STATUS = 0x8B83
        val ATTACHED_SHADERS = 0x8B85
        val ACTIVE_UNIFORMS = 0x8B86
        val ACTIVE_UNIFORM_MAX_LENGTH = 0x8B87
        val ACTIVE_ATTRIBUTES = 0x8B89
        val ACTIVE_ATTRIBUTE_MAX_LENGTH = 0x8B8A
        val SHADING_LANGUAGE_VERSION = 0x8B8C
        val CURRENT_PROGRAM = 0x8B8D
        val NEVER = 0x0200
        val LESS = 0x0201
        val EQUAL = 0x0202
        val LEQUAL = 0x0203
        val GREATER = 0x0204
        val NOTEQUAL = 0x0205
        val GEQUAL = 0x0206
        val ALWAYS = 0x0207
        val KEEP = 0x1E00
        val REPLACE = 0x1E01
        val INCR = 0x1E02
        val DECR = 0x1E03
        val INVERT = 0x150A
        val INCR_WRAP = 0x8507
        val DECR_WRAP = 0x8508
        val VENDOR = 0x1F00
        val RENDERER = 0x1F01
        val VERSION = 0x1F02
        val EXTENSIONS = 0x1F03
        val NEAREST = 0x2600
        val LINEAR = 0x2601
        val NEAREST_MIPMAP_NEAREST = 0x2700
        val LINEAR_MIPMAP_NEAREST = 0x2701
        val NEAREST_MIPMAP_LINEAR = 0x2702
        val LINEAR_MIPMAP_LINEAR = 0x2703
        val TEXTURE_MAG_FILTER = 0x2800
        val TEXTURE_MIN_FILTER = 0x2801
        val TEXTURE_WRAP_S = 0x2802
        val TEXTURE_WRAP_T = 0x2803
        val TEXTURE = 0x1702
        val TEXTURE_CUBE_MAP = 0x8513
        val TEXTURE_BINDING_CUBE_MAP = 0x8514
        val TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515
        val TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516
        val TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517
        val TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518
        val TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519
        val TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A
        val MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C
        val TEXTURE0 = 0x84C0
        val TEXTURE1 = 0x84C1
        val TEXTURE2 = 0x84C2
        val TEXTURE3 = 0x84C3
        val TEXTURE4 = 0x84C4
        val TEXTURE5 = 0x84C5
        val TEXTURE6 = 0x84C6
        val TEXTURE7 = 0x84C7
        val TEXTURE8 = 0x84C8
        val TEXTURE9 = 0x84C9
        val TEXTURE10 = 0x84CA
        val TEXTURE11 = 0x84CB
        val TEXTURE12 = 0x84CC
        val TEXTURE13 = 0x84CD
        val TEXTURE14 = 0x84CE
        val TEXTURE15 = 0x84CF
        val TEXTURE16 = 0x84D0
        val TEXTURE17 = 0x84D1
        val TEXTURE18 = 0x84D2
        val TEXTURE19 = 0x84D3
        val TEXTURE20 = 0x84D4
        val TEXTURE21 = 0x84D5
        val TEXTURE22 = 0x84D6
        val TEXTURE23 = 0x84D7
        val TEXTURE24 = 0x84D8
        val TEXTURE25 = 0x84D9
        val TEXTURE26 = 0x84DA
        val TEXTURE27 = 0x84DB
        val TEXTURE28 = 0x84DC
        val TEXTURE29 = 0x84DD
        val TEXTURE30 = 0x84DE
        val TEXTURE31 = 0x84DF
        val REPEAT = 0x2901
        val CLAMP_TO_EDGE = 0x812F
        val MIRRORED_REPEAT = 0x8370
        val FLOAT_VEC2 = 0x8B50
        val FLOAT_VEC3 = 0x8B51
        val FLOAT_VEC4 = 0x8B52
        val INT_VEC2 = 0x8B53
        val INT_VEC3 = 0x8B54
        val INT_VEC4 = 0x8B55
        val BOOL = 0x8B56
        val BOOL_VEC2 = 0x8B57
        val BOOL_VEC3 = 0x8B58
        val BOOL_VEC4 = 0x8B59
        val FLOAT_MAT2 = 0x8B5A
        val FLOAT_MAT3 = 0x8B5B
        val FLOAT_MAT4 = 0x8B5C
        val SAMPLER_2D = 0x8B5E
        val SAMPLER_CUBE = 0x8B60
        val VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622
        val VERTEX_ATTRIB_ARRAY_SIZE = 0x8623
        val VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624
        val VERTEX_ATTRIB_ARRAY_TYPE = 0x8625
        val VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A
        val VERTEX_ATTRIB_ARRAY_POINTER = 0x8645
        val VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F
        val IMPLEMENTATION_COLOR_READ_TYPE = 0x8B9A
        val IMPLEMENTATION_COLOR_READ_FORMAT = 0x8B9B
        val COMPILE_STATUS = 0x8B81
        val INFO_LOG_LENGTH = 0x8B84
        val SHADER_SOURCE_LENGTH = 0x8B88
        val SHADER_COMPILER = 0x8DFA
        val SHADER_BINARY_FORMATS = 0x8DF8
        val NUM_SHADER_BINARY_FORMATS = 0x8DF9
        val LOW_FLOAT = 0x8DF0
        val MEDIUM_FLOAT = 0x8DF1
        val HIGH_FLOAT = 0x8DF2
        val LOW_INT = 0x8DF3
        val MEDIUM_INT = 0x8DF4
        val HIGH_INT = 0x8DF5
        val FRAMEBUFFER = 0x8D40
        val RENDERBUFFER = 0x8D41
        val RGBA4 = 0x8056
        val RGB5_A1 = 0x8057
        val RGB565 = 0x8D62
        val DEPTH_COMPONENT16 = 0x81A5
        val STENCIL_INDEX8 = 0x8D48
        val RENDERBUFFER_WIDTH = 0x8D42
        val RENDERBUFFER_HEIGHT = 0x8D43
        val RENDERBUFFER_INTERNAL_FORMAT = 0x8D44
        val RENDERBUFFER_RED_SIZE = 0x8D50
        val RENDERBUFFER_GREEN_SIZE = 0x8D51
        val RENDERBUFFER_BLUE_SIZE = 0x8D52
        val RENDERBUFFER_ALPHA_SIZE = 0x8D53
        val RENDERBUFFER_DEPTH_SIZE = 0x8D54
        val RENDERBUFFER_STENCIL_SIZE = 0x8D55
        val FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 0x8CD0
        val FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 0x8CD1
        val FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 0x8CD2
        val FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3
        val COLOR_ATTACHMENT0 = 0x8CE0
        val DEPTH_ATTACHMENT = 0x8D00
        val STENCIL_ATTACHMENT = 0x8D20
        val NONE = 0
        val FRAMEBUFFER_COMPLETE = 0x8CD5
        val FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6
        val FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7
        val FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9
        val FRAMEBUFFER_UNSUPPORTED = 0x8CDD
        val FRAMEBUFFER_BINDING = 0x8CA6
        val RENDERBUFFER_BINDING = 0x8CA7
        val MAX_RENDERBUFFER_SIZE = 0x84E8
        val INVALID_FRAMEBUFFER_OPERATION = 0x0506
    }

    interface Impl {
        fun isAvailable(): Boolean

        fun activeTexture(texture: Int)
        fun attachShader(program: ProgramResource, shader: ShaderResource)
        fun bindBuffer(target: Int, buffer: BufferResource?)
        fun bindFramebuffer(target: Int, framebuffer: FramebufferResource?)
        fun bindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?)
        fun bindTexture(target: Int, texture: TextureResource?)
        fun blendFunc(sfactor: Int, dfactor: Int)
        fun bufferData(target: Int, data: Uint8Buffer, usage: Int)
        fun bufferData(target: Int, data: Uint16Buffer, usage: Int)
        fun bufferData(target: Int, data: Uint32Buffer, usage: Int)
        fun bufferData(target: Int, data: Float32Buffer, usage: Int)
        fun clear(mask: Int)
        fun clearColor(red: Float, green: Float, blue: Float, alpha: Float)
        fun compileShader(shader: ShaderResource)
        fun createBuffer(): Any
        fun createFramebuffer(): Any
        fun createProgram(): Any
        fun createRenderbuffer(): Any
        fun createShader(type: Int): Any
        fun createTexture(): Any
        fun deleteBuffer(buffer: BufferResource)
        fun deleteFramebuffer(framebuffer: FramebufferResource)
        fun deleteProgram(program: ProgramResource)
        fun deleteRenderbuffer(renderbuffer: RenderbufferResource)
        fun deleteShader(shader: ShaderResource)
        fun deleteTexture(texture: TextureResource)
        fun depthFunc(func: Int)
        fun depthMask(enabled: Boolean)
        fun disable(cap: Int)
        fun disableVertexAttribArray(index: Int)
        fun drawElements(mode: Int, count: Int, type: Int, offset: Int)
        fun drawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int)
        fun enable(cap: Int)
        fun enableVertexAttribArray(index: Int)
        fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource)
        fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int)
        fun generateMipmap(target: Int)
        fun getAttribLocation(program: ProgramResource, name: String): Int
        fun getError(): Int
        fun getProgrami(program: ProgramResource, pname: Int): Int
        fun getShaderi(shader: ShaderResource, pname: Int): Int
        fun getProgramInfoLog(program: ProgramResource): String
        fun getShaderInfoLog(shader: ShaderResource): String
        fun getUniformLocation(program: ProgramResource, name: String): Any?
        fun lineWidth(width: Float)
        fun linkProgram(program: ProgramResource)
        fun pointSize(size: Float)
        fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int)
        fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int)
        fun shaderSource(shader: ShaderResource, source: String)
        fun texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?)
        fun texParameteri(target: Int, pname: Int, param: Int)
        fun uniform1f(location: Any?, x: Float)
        fun uniform1i(location: Any?, x: Int)
        fun uniform2f(location: Any?, x: Float, y: Float)
        fun uniform3f(location: Any?, x: Float, y: Float, z: Float)
        fun uniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float)
        fun uniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer)
        fun useProgram(program: ProgramResource?)
        fun vertexAttribDivisor(index: Int, divisor: Int)
        fun vertexAttribPointer(indx: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int)
        fun viewport(x: Int, y: Int, width: Int, height: Int)
    }
}
