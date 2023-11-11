package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.TexFormat


fun TexFormat.glInternalFormat(gl: GlApi): Int = when(this) {
    TexFormat.R -> gl.R8
    TexFormat.RG -> gl.RG8
    TexFormat.RGB -> gl.RGB8
    TexFormat.RGBA -> gl.RGBA8

    TexFormat.R_F16 -> gl.R16F
    TexFormat.RG_F16 -> gl.RG16F
    TexFormat.RGB_F16 -> gl.RGB16F
    TexFormat.RGBA_F16 -> gl.RGBA16F

    TexFormat.R_F32 -> gl.R32F
    TexFormat.RG_F32 -> gl.RG32F
    TexFormat.RGB_F32 -> gl.RGB32F
    TexFormat.RGBA_F32 -> gl.RGBA32F
}

fun TexFormat.glType(gl: GlApi): Int = when(this) {
    TexFormat.R -> gl.UNSIGNED_BYTE
    TexFormat.RG -> gl.UNSIGNED_BYTE
    TexFormat.RGB -> gl.UNSIGNED_BYTE
    TexFormat.RGBA -> gl.UNSIGNED_BYTE

    TexFormat.R_F16 -> gl.FLOAT
    TexFormat.RG_F16 -> gl.FLOAT
    TexFormat.RGB_F16 -> gl.FLOAT
    TexFormat.RGBA_F16 -> gl.FLOAT

    TexFormat.R_F32 -> gl.FLOAT
    TexFormat.RG_F32 -> gl.FLOAT
    TexFormat.RGB_F32 -> gl.FLOAT
    TexFormat.RGBA_F32 -> gl.FLOAT
}

fun TexFormat.glFormat(gl: GlApi): Int = when(this) {
    TexFormat.R -> gl.RED
    TexFormat.RG -> gl.RG
    TexFormat.RGB -> gl.RGB
    TexFormat.RGBA -> gl.RGBA

    TexFormat.R_F16 -> gl.RED
    TexFormat.RG_F16 -> gl.RG
    TexFormat.RGB_F16 -> gl.RGB
    TexFormat.RGBA_F16 -> gl.RGBA

    TexFormat.R_F32 -> gl.RED
    TexFormat.RG_F32 -> gl.RG
    TexFormat.RGB_F32 -> gl.RGB
    TexFormat.RGBA_F32 -> gl.RGBA
}

val TexFormat.pxSize: Int get() = when(this) {
    TexFormat.R -> 1
    TexFormat.RG -> 2
    TexFormat.RGB -> 3
    TexFormat.RGBA -> 4

    TexFormat.R_F16 -> 2
    TexFormat.RG_F16 -> 4
    TexFormat.RGB_F16 -> 6
    TexFormat.RGBA_F16 -> 8

    TexFormat.R_F32 -> 4
    TexFormat.RG_F32 -> 8
    TexFormat.RGB_F32 -> 12
    TexFormat.RGBA_F32 -> 16
}

fun DepthCompareOp.glOp(gl: GlApi): Int = when(this) {
    DepthCompareOp.DISABLED -> 0
    DepthCompareOp.ALWAYS -> gl.ALWAYS
    DepthCompareOp.NEVER -> gl.NEVER
    DepthCompareOp.LESS -> gl.LESS
    DepthCompareOp.LESS_EQUAL -> gl.LEQUAL
    DepthCompareOp.GREATER -> gl.GREATER
    DepthCompareOp.GREATER_EQUAL -> gl.GEQUAL
    DepthCompareOp.EQUAL -> gl.EQUAL
    DepthCompareOp.NOT_EQUAL -> gl.NOTEQUAL
}