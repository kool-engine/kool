package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.TexFormat
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.ALWAYS
import org.khronos.webgl.WebGLRenderingContext.Companion.EQUAL
import org.khronos.webgl.WebGLRenderingContext.Companion.GEQUAL
import org.khronos.webgl.WebGLRenderingContext.Companion.GREATER
import org.khronos.webgl.WebGLRenderingContext.Companion.LEQUAL
import org.khronos.webgl.WebGLRenderingContext.Companion.LESS
import org.khronos.webgl.WebGLRenderingContext.Companion.NEVER
import org.khronos.webgl.WebGLRenderingContext.Companion.NOTEQUAL


val TexFormat.glInternalFormat: Int
    get() = when(this) {
        TexFormat.R -> WebGL2RenderingContext.R8
        TexFormat.RG -> WebGL2RenderingContext.RG8
        TexFormat.RGB -> WebGL2RenderingContext.RGB8
        TexFormat.RGBA -> WebGL2RenderingContext.RGBA8

        TexFormat.R_F16 -> WebGL2RenderingContext.R16F
        TexFormat.RG_F16 -> WebGL2RenderingContext.RG16F
        TexFormat.RGB_F16 -> WebGL2RenderingContext.RGB16F
        TexFormat.RGBA_F16 -> WebGL2RenderingContext.RGBA16F

        TexFormat.RI -> WebGL2RenderingContext.R8I
    }

val TexFormat.glType: Int
    get() = when(this) {
        TexFormat.R -> WebGLRenderingContext.UNSIGNED_BYTE
        TexFormat.RG -> WebGLRenderingContext.UNSIGNED_BYTE
        TexFormat.RGB -> WebGLRenderingContext.UNSIGNED_BYTE
        TexFormat.RGBA -> WebGLRenderingContext.UNSIGNED_BYTE

        TexFormat.R_F16 -> WebGLRenderingContext.FLOAT
        TexFormat.RG_F16 -> WebGLRenderingContext.FLOAT
        TexFormat.RGB_F16 -> WebGLRenderingContext.FLOAT
        TexFormat.RGBA_F16 -> WebGLRenderingContext.FLOAT

        TexFormat.RI -> WebGLRenderingContext.BYTE
    }

val TexFormat.glFormat: Int
    get() = when(this) {
        TexFormat.R -> WebGL2RenderingContext.RED
        TexFormat.RG -> WebGL2RenderingContext.RG
        TexFormat.RGB -> WebGLRenderingContext.RGB
        TexFormat.RGBA -> WebGLRenderingContext.RGBA

        TexFormat.R_F16 -> WebGL2RenderingContext.RED
        TexFormat.RG_F16 -> WebGL2RenderingContext.RG
        TexFormat.RGB_F16 -> WebGLRenderingContext.RGB
        TexFormat.RGBA_F16 -> WebGLRenderingContext.RGBA

        TexFormat.RI -> WebGL2RenderingContext.RED_INTEGER
    }

val TexFormat.pxSize: Int
    get() = when(this) {
        TexFormat.R -> 1
        TexFormat.RG -> 2
        TexFormat.RGB -> 3
        TexFormat.RGBA -> 4

        TexFormat.R_F16 -> 2
        TexFormat.RG_F16 -> 4
        TexFormat.RGB_F16 -> 6
        TexFormat.RGBA_F16 -> 8

        TexFormat.RI -> 1
    }

val DepthCompareOp.glOp: Int
    get() = when(this) {
        DepthCompareOp.DISABLED -> 0
        DepthCompareOp.ALWAYS -> ALWAYS
        DepthCompareOp.NEVER -> NEVER
        DepthCompareOp.LESS -> LESS
        DepthCompareOp.LESS_EQUAL -> LEQUAL
        DepthCompareOp.GREATER -> GREATER
        DepthCompareOp.GREATER_EQUAL -> GEQUAL
        DepthCompareOp.EQUAL -> EQUAL
        DepthCompareOp.NOT_EQUAL -> NOTEQUAL
    }