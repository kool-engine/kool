package de.fabmax.kool.gl

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Uint16Buffer
import de.fabmax.kool.util.Uint32Buffer
import de.fabmax.kool.util.Uint8Buffer

/**
 * @author fabmax
 */

expect fun glActiveTexture(texture: Int)

expect fun glAttachShader(program: ProgramResource, shader: ShaderResource)

expect fun glBindBuffer(target: Int, buffer: BufferResource?)

expect fun glBindFramebuffer(target: Int, framebuffer: FramebufferResource?)

expect fun glBindRenderbuffer(target: Int, renderbuffer: RenderbufferResource?)

expect fun glBindTexture(target: Int, texture: TextureResource?)

expect fun glBlendFunc(sfactor: Int, dfactor: Int)

expect fun glBufferData(target: Int, data: Uint8Buffer, usage: Int)

expect fun glBufferData(target: Int, data: Uint16Buffer, usage: Int)

expect fun glBufferData(target: Int, data: Uint32Buffer, usage: Int)

expect fun glBufferData(target: Int, data: Float32Buffer, usage: Int)

expect fun glCheckFramebufferStatus(target: Int): Int

expect fun glClear(mask: Int)

expect fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float)

expect fun glCompileShader(shader: ShaderResource)

expect fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int)

expect fun glCreateBuffer(): Any

expect fun glCreateFramebuffer(): Any

expect fun glCreateRenderbuffer(): Any

expect fun glCreateProgram(): Any

expect fun glCreateShader(type: Int): Any

expect fun glCreateTexture(): Any

expect fun glCullFace(mode: Int)

expect fun glDeleteBuffer(buffer: BufferResource)

expect fun glDeleteFramebuffer(framebuffer: FramebufferResource)

expect fun glDeleteProgram(program: ProgramResource)

expect fun glDeleteRenderbuffer(renderbuffer: RenderbufferResource)

expect fun glDeleteShader(shader: ShaderResource)

expect fun glDeleteTexture(texture: TextureResource)

expect fun glDepthFunc(func: Int)

expect fun glDepthMask(enabled: Boolean)

expect fun glDisable(cap: Int)

expect fun glDisableVertexAttribArray(index: Int)

expect fun glDrawBuffer(buf: Int)

expect fun glDrawElements(mode: Int, count: Int, type: Int, offset: Int)

expect fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indicesOffset: Int, instanceCount: Int)

expect fun glEnable(cap: Int)

expect fun glEnableVertexAttribArray(index: Int)

expect fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: RenderbufferResource)

expect fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: TextureResource, level: Int)

expect fun glGenerateMipmap(target: Int)

expect fun glGetAttribLocation(program: ProgramResource, name: String): Int

expect fun glGetError(): Int

expect fun glGetProgrami(program: ProgramResource, pname: Int): Int

expect fun glGetShaderi(shader: ShaderResource, pname: Int): Int

expect fun glGetProgramInfoLog(program: ProgramResource): String

expect fun glGetShaderInfoLog(shader: ShaderResource): String

expect fun glGetUniformLocation(program: ProgramResource, name: String): Any?

expect fun glLineWidth(width: Float)

expect fun glLinkProgram(program: ProgramResource)

expect fun glPointSize(size: Float)

expect fun glReadBuffer(src: Int)

expect fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int)

expect fun glRenderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int)

expect fun glShaderSource(shader: ShaderResource, source: String)

expect fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Uint8Buffer?)

expect fun glTexParameteri(target: Int, pname: Int, param: Int)

expect fun glUniform1f(location: Any?, x: Float)

expect fun glUniform1fv(location: Any?, x: FloatArray)

expect fun glUniform1i(location: Any?, x: Int)

expect fun glUniform1iv(location: Any?, x: IntArray)

expect fun glUniform2f(location: Any?, x: Float, y: Float)

expect fun glUniform3f(location: Any?, x: Float, y: Float, z: Float)

expect fun glUniform4f(location: Any?, x: Float, y: Float, z: Float, w: Float)

expect fun glUniformMatrix4fv(location: Any?, transpose: Boolean, value: Float32Buffer)

expect fun glUseProgram(program: ProgramResource?)

expect fun glVertexAttribDivisor(index: Int, divisor: Int)

expect fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int)

expect fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int)

expect fun glViewport(x: Int, y: Int, width: Int, height: Int)

expect fun isValidUniformLocation(location: Any?): Boolean

const val GL_ACTIVE_TEXTURE = 0x84E0
const val GL_DEPTH_BUFFER_BIT = 0x00000100
const val GL_STENCIL_BUFFER_BIT = 0x00000400
const val GL_COLOR_BUFFER_BIT = 0x00004000
const val GL_FALSE = 0
const val GL_TRUE = 1
const val GL_POINTS = 0x0000
const val GL_LINES = 0x0001
const val GL_LINE_LOOP = 0x0002
const val GL_LINE_STRIP = 0x0003
const val GL_TRIANGLES = 0x0004
const val GL_TRIANGLE_STRIP = 0x0005
const val GL_TRIANGLE_FAN = 0x0006
const val GL_ZERO = 0
const val GL_ONE = 1
const val GL_SRC_COLOR = 0x0300
const val GL_ONE_MINUS_SRC_COLOR = 0x0301
const val GL_SRC_ALPHA = 0x0302
const val GL_ONE_MINUS_SRC_ALPHA = 0x0303
const val GL_DST_ALPHA = 0x0304
const val GL_ONE_MINUS_DST_ALPHA = 0x0305
const val GL_DST_COLOR = 0x0306
const val GL_ONE_MINUS_DST_COLOR = 0x0307
const val GL_SRC_ALPHA_SATURATE = 0x0308
const val GL_FUNC_ADD = 0x8006
const val GL_BLEND_EQUATION = 0x8009
const val GL_BLEND_EQUATION_RGB = 0x8009   /* same as BLEND_EQUATION */
const val GL_BLEND_EQUATION_ALPHA = 0x883D
const val GL_FUNC_SUBTRACT = 0x800A
const val GL_FUNC_REVERSE_SUBTRACT = 0x800B
const val GL_BLEND_DST_RGB = 0x80C8
const val GL_BLEND_SRC_RGB = 0x80C9
const val GL_BLEND_DST_ALPHA = 0x80CA
const val GL_BLEND_SRC_ALPHA = 0x80CB
const val GL_CONSTANT_COLOR = 0x8001
const val GL_ONE_MINUS_CONSTANT_COLOR = 0x8002
const val GL_CONSTANT_ALPHA = 0x8003
const val GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004
const val GL_BLEND_COLOR = 0x8005
const val GL_ARRAY_BUFFER = 0x8892
const val GL_ELEMENT_ARRAY_BUFFER = 0x8893
const val GL_ARRAY_BUFFER_BINDING = 0x8894
const val GL_ELEMENT_ARRAY_BUFFER_BINDING = 0x8895
const val GL_STREAM_DRAW = 0x88E0
const val GL_STATIC_DRAW = 0x88E4
const val GL_DYNAMIC_DRAW = 0x88E8
const val GL_BUFFER_SIZE = 0x8764
const val GL_BUFFER_USAGE = 0x8765
const val GL_CURRENT_VERTEX_ATTRIB = 0x8626
const val GL_FRONT = 0x0404
const val GL_BACK = 0x0405
const val GL_FRONT_AND_BACK = 0x0408
const val GL_TEXTURE_2D = 0x0DE1
const val GL_CULL_FACE = 0x0B44
const val GL_BLEND = 0x0BE2
const val GL_DITHER = 0x0BD0
const val GL_STENCIL_TEST = 0x0B90
const val GL_DEPTH_TEST = 0x0B71
const val GL_SCISSOR_TEST = 0x0C11
const val GL_POLYGON_OFFSET_FILL = 0x8037
const val GL_SAMPLE_ALPHA_TO_COVERAGE = 0x809E
const val GL_SAMPLE_COVERAGE = 0x80A0
const val GL_NO_ERROR = 0
const val GL_INVALID_ENUM = 0x0500
const val GL_INVALID_VALUE = 0x0501
const val GL_INVALID_OPERATION = 0x0502
const val GL_OUT_OF_MEMORY = 0x0505
const val GL_CW = 0x0900
const val GL_CCW = 0x0901
const val GL_LINE_WIDTH = 0x0B21
const val GL_ALIASED_POINT_SIZE_RANGE = 0x846D
const val GL_ALIASED_LINE_WIDTH_RANGE = 0x846E
const val GL_CULL_FACE_MODE = 0x0B45
const val GL_FRONT_FACE = 0x0B46
const val GL_DEPTH_RANGE = 0x0B70
const val GL_DEPTH_WRITEMASK = 0x0B72
const val GL_DEPTH_CLEAR_VALUE = 0x0B73
const val GL_DEPTH_FUNC = 0x0B74
const val GL_STENCIL_CLEAR_VALUE = 0x0B91
const val GL_STENCIL_FUNC = 0x0B92
const val GL_STENCIL_FAIL = 0x0B94
const val GL_STENCIL_PASS_DEPTH_FAIL = 0x0B95
const val GL_STENCIL_PASS_DEPTH_PASS = 0x0B96
const val GL_STENCIL_REF = 0x0B97
const val GL_STENCIL_VALUE_MASK = 0x0B93
const val GL_STENCIL_WRITEMASK = 0x0B98
const val GL_STENCIL_BACK_FUNC = 0x8800
const val GL_STENCIL_BACK_FAIL = 0x8801
const val GL_STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802
const val GL_STENCIL_BACK_PASS_DEPTH_PASS = 0x8803
const val GL_STENCIL_BACK_REF = 0x8CA3
const val GL_STENCIL_BACK_VALUE_MASK = 0x8CA4
const val GL_STENCIL_BACK_WRITEMASK = 0x8CA5
const val GL_VIEWPORT = 0x0BA2
const val GL_SCISSOR_BOX = 0x0C10
const val GL_COLOR_CLEAR_VALUE = 0x0C22
const val GL_COLOR_WRITEMASK = 0x0C23
const val GL_UNPACK_ALIGNMENT = 0x0CF5
const val GL_PACK_ALIGNMENT = 0x0D05
const val GL_MAX_TEXTURE_SIZE = 0x0D33
const val GL_MAX_VIEWPORT_DIMS = 0x0D3A
const val GL_SUBPIXEL_BITS = 0x0D50
const val GL_RED_BITS = 0x0D52
const val GL_GREEN_BITS = 0x0D53
const val GL_BLUE_BITS = 0x0D54
const val GL_ALPHA_BITS = 0x0D55
const val GL_DEPTH_BITS = 0x0D56
const val GL_STENCIL_BITS = 0x0D57
const val GL_POLYGON_OFFSET_UNITS = 0x2A00
const val GL_POLYGON_OFFSET_FACTOR = 0x8038
const val GL_TEXTURE_BINDING_2D = 0x8069
const val GL_SAMPLE_BUFFERS = 0x80A8
const val GL_SAMPLES = 0x80A9
const val GL_SAMPLE_COVERAGE_VALUE = 0x80AA
const val GL_SAMPLE_COVERAGE_INVERT = 0x80AB
const val GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2
const val GL_COMPRESSED_TEXTURE_FORMATS = 0x86A3
const val GL_DONT_CARE = 0x1100
const val GL_FASTEST = 0x1101
const val GL_NICEST = 0x1102
const val GL_GENERATE_MIPMAP_HINT = 0x8192
const val GL_BYTE = 0x1400
const val GL_UNSIGNED_BYTE = 0x1401
const val GL_SHORT = 0x1402
const val GL_UNSIGNED_SHORT = 0x1403
const val GL_INT = 0x1404
const val GL_UNSIGNED_INT = 0x1405
const val GL_FLOAT = 0x1406
const val GL_FIXED = 0x140C
const val GL_DEPTH_COMPONENT = 0x1902
const val GL_DEPTH_COMPONENT24 = 0x81A6
const val GL_DEPTH_COMPONENT32F = 0x8CAC
const val GL_ALPHA = 0x1906
const val GL_RGB = 0x1907
const val GL_RGBA = 0x1908
const val GL_LUMINANCE = 0x1909
const val GL_LUMINANCE_ALPHA = 0x190A
const val GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033
const val GL_UNSIGNED_SHORT_5_5_5_1 = 0x8034
const val GL_UNSIGNED_SHORT_5_6_5 = 0x8363
const val GL_FRAGMENT_SHADER = 0x8B30
const val GL_VERTEX_SHADER = 0x8B31
const val GL_MAX_VERTEX_ATTRIBS = 0x8869
const val GL_MAX_VERTEX_UNIFORM_VECTORS = 0x8DFB
const val GL_MAX_VARYING_VECTORS = 0x8DFC
const val GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D
const val GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C
const val GL_MAX_TEXTURE_IMAGE_UNITS = 0x8872
const val GL_MAX_FRAGMENT_UNIFORM_VECTORS = 0x8DFD
const val GL_SHADER_TYPE = 0x8B4F
const val GL_DELETE_STATUS = 0x8B80
const val GL_LINK_STATUS = 0x8B82
const val GL_VALIDATE_STATUS = 0x8B83
const val GL_ATTACHED_SHADERS = 0x8B85
const val GL_ACTIVE_UNIFORMS = 0x8B86
const val GL_ACTIVE_UNIFORM_MAX_LENGTH = 0x8B87
const val GL_ACTIVE_ATTRIBUTES = 0x8B89
const val GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 0x8B8A
const val GL_SHADING_LANGUAGE_VERSION = 0x8B8C
const val GL_CURRENT_PROGRAM = 0x8B8D
const val GL_NEVER = 0x0200
const val GL_LESS = 0x0201
const val GL_EQUAL = 0x0202
const val GL_LEQUAL = 0x0203
const val GL_GREATER = 0x0204
const val GL_NOTEQUAL = 0x0205
const val GL_GEQUAL = 0x0206
const val GL_ALWAYS = 0x0207
const val GL_KEEP = 0x1E00
const val GL_REPLACE = 0x1E01
const val GL_INCR = 0x1E02
const val GL_DECR = 0x1E03
const val GL_INVERT = 0x150A
const val GL_INCR_WRAP = 0x8507
const val GL_DECR_WRAP = 0x8508
const val GL_VENDOR = 0x1F00
const val GL_RENDERER = 0x1F01
const val GL_VERSION = 0x1F02
const val GL_EXTENSIONS = 0x1F03
const val GL_NEAREST = 0x2600
const val GL_LINEAR = 0x2601
const val GL_NEAREST_MIPMAP_NEAREST = 0x2700
const val GL_LINEAR_MIPMAP_NEAREST = 0x2701
const val GL_NEAREST_MIPMAP_LINEAR = 0x2702
const val GL_LINEAR_MIPMAP_LINEAR = 0x2703
const val GL_TEXTURE_MAG_FILTER = 0x2800
const val GL_TEXTURE_MIN_FILTER = 0x2801
const val GL_TEXTURE_WRAP_S = 0x2802
const val GL_TEXTURE_WRAP_T = 0x2803
const val GL_TEXTURE = 0x1702
const val GL_TEXTURE_CUBE_MAP = 0x8513
const val GL_TEXTURE_BINDING_CUBE_MAP = 0x8514
const val GL_TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515
const val GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516
const val GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517
const val GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518
const val GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519
const val GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A
const val GL_MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C
const val GL_TEXTURE0 = 0x84C0
const val GL_TEXTURE1 = 0x84C1
const val GL_TEXTURE2 = 0x84C2
const val GL_TEXTURE3 = 0x84C3
const val GL_TEXTURE4 = 0x84C4
const val GL_TEXTURE5 = 0x84C5
const val GL_TEXTURE6 = 0x84C6
const val GL_TEXTURE7 = 0x84C7
const val GL_TEXTURE8 = 0x84C8
const val GL_TEXTURE9 = 0x84C9
const val GL_TEXTURE10 = 0x84CA
const val GL_TEXTURE11 = 0x84CB
const val GL_TEXTURE12 = 0x84CC
const val GL_TEXTURE13 = 0x84CD
const val GL_TEXTURE14 = 0x84CE
const val GL_TEXTURE15 = 0x84CF
const val GL_TEXTURE16 = 0x84D0
const val GL_TEXTURE17 = 0x84D1
const val GL_TEXTURE18 = 0x84D2
const val GL_TEXTURE19 = 0x84D3
const val GL_TEXTURE20 = 0x84D4
const val GL_TEXTURE21 = 0x84D5
const val GL_TEXTURE22 = 0x84D6
const val GL_TEXTURE23 = 0x84D7
const val GL_TEXTURE24 = 0x84D8
const val GL_TEXTURE25 = 0x84D9
const val GL_TEXTURE26 = 0x84DA
const val GL_TEXTURE27 = 0x84DB
const val GL_TEXTURE28 = 0x84DC
const val GL_TEXTURE29 = 0x84DD
const val GL_TEXTURE30 = 0x84DE
const val GL_TEXTURE31 = 0x84DF
const val GL_REPEAT = 0x2901
const val GL_CLAMP_TO_EDGE = 0x812F
const val GL_MIRRORED_REPEAT = 0x8370
const val GL_FLOAT_VEC2 = 0x8B50
const val GL_FLOAT_VEC3 = 0x8B51
const val GL_FLOAT_VEC4 = 0x8B52
const val GL_INT_VEC2 = 0x8B53
const val GL_INT_VEC3 = 0x8B54
const val GL_INT_VEC4 = 0x8B55
const val GL_BOOL = 0x8B56
const val GL_BOOL_VEC2 = 0x8B57
const val GL_BOOL_VEC3 = 0x8B58
const val GL_BOOL_VEC4 = 0x8B59
const val GL_FLOAT_MAT2 = 0x8B5A
const val GL_FLOAT_MAT3 = 0x8B5B
const val GL_FLOAT_MAT4 = 0x8B5C
const val GL_SAMPLER_2D = 0x8B5E
const val GL_SAMPLER_CUBE = 0x8B60
const val GL_VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622
const val GL_VERTEX_ATTRIB_ARRAY_SIZE = 0x8623
const val GL_VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624
const val GL_VERTEX_ATTRIB_ARRAY_TYPE = 0x8625
const val GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A
const val GL_VERTEX_ATTRIB_ARRAY_POINTER = 0x8645
const val GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F
const val GL_IMPLEMENTATION_COLOR_READ_TYPE = 0x8B9A
const val GL_IMPLEMENTATION_COLOR_READ_FORMAT = 0x8B9B
const val GL_COMPILE_STATUS = 0x8B81
const val GL_INFO_LOG_LENGTH = 0x8B84
const val GL_SHADER_SOURCE_LENGTH = 0x8B88
const val GL_SHADER_COMPILER = 0x8DFA
const val GL_SHADER_BINARY_FORMATS = 0x8DF8
const val GL_NUM_SHADER_BINARY_FORMATS = 0x8DF9
const val GL_LOW_FLOAT = 0x8DF0
const val GL_MEDIUM_FLOAT = 0x8DF1
const val GL_HIGH_FLOAT = 0x8DF2
const val GL_LOW_INT = 0x8DF3
const val GL_MEDIUM_INT = 0x8DF4
const val GL_HIGH_INT = 0x8DF5
const val GL_FRAMEBUFFER = 0x8D40
const val GL_RENDERBUFFER = 0x8D41
const val GL_RGBA4 = 0x8056
const val GL_RGB5_A1 = 0x8057
const val GL_RGB565 = 0x8D62
const val GL_DEPTH_COMPONENT16 = 0x81A5
const val GL_STENCIL_INDEX8 = 0x8D48
const val GL_RENDERBUFFER_WIDTH = 0x8D42
const val GL_RENDERBUFFER_HEIGHT = 0x8D43
const val GL_RENDERBUFFER_INTERNAL_FORMAT = 0x8D44
const val GL_RENDERBUFFER_RED_SIZE = 0x8D50
const val GL_RENDERBUFFER_GREEN_SIZE = 0x8D51
const val GL_RENDERBUFFER_BLUE_SIZE = 0x8D52
const val GL_RENDERBUFFER_ALPHA_SIZE = 0x8D53
const val GL_RENDERBUFFER_DEPTH_SIZE = 0x8D54
const val GL_RENDERBUFFER_STENCIL_SIZE = 0x8D55
const val GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 0x8CD0
const val GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 0x8CD1
const val GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 0x8CD2
const val GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3
const val GL_COLOR_ATTACHMENT0 = 0x8CE0
const val GL_DEPTH_ATTACHMENT = 0x8D00
const val GL_STENCIL_ATTACHMENT = 0x8D20
const val GL_NONE = 0
const val GL_FRAMEBUFFER_COMPLETE = 0x8CD5
const val GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6
const val GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7
const val GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9
const val GL_FRAMEBUFFER_UNSUPPORTED = 0x8CDD
const val GL_FRAMEBUFFER_BINDING = 0x8CA6
const val GL_RENDERBUFFER_BINDING = 0x8CA7
const val GL_MAX_RENDERBUFFER_SIZE = 0x84E8
const val GL_INVALID_FRAMEBUFFER_OPERATION = 0x0506
