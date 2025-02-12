@file:Suppress("DEPRECATION")

package de.fabmax.kool.pipeline

enum class TexFormat(val channels: Int) {
    R(1),
    RG(2),
    RGBA(4),

    R_F16(1),
    RG_F16(2),
    RGBA_F16(4),

    R_F32(1),
    RG_F32(2),
    RGBA_F32(4),

    R_I32(1),
    RG_I32(2),
    RGBA_I32(4),

    R_U32(1),
    RG_U32(2),
    RGBA_U32(4),

    RG11B10_F(3)
}

val TexFormat.isByte: Boolean get() {
    return this == TexFormat.R || this == TexFormat.RG || this == TexFormat.RGBA
}

val TexFormat.isFloat: Boolean get() {
    return this == TexFormat.R_F16 || this == TexFormat.RG_F16 || this == TexFormat.RGBA_F16 ||
            this == TexFormat.R_F32 || this == TexFormat.RG_F32 || this == TexFormat.RGBA_F32 ||
            this == TexFormat.RG11B10_F
}

val TexFormat.isF16: Boolean get() {
    return this == TexFormat.R_F16 || this == TexFormat.RG_F16 || this == TexFormat.RGBA_F16
}

val TexFormat.isF32: Boolean get() {
    return this == TexFormat.R_F32 || this == TexFormat.RG_F32 || this == TexFormat.RGBA_F32
}

val TexFormat.isI32: Boolean get() {
    return this == TexFormat.R_I32 || this == TexFormat.RG_I32 || this == TexFormat.RGBA_I32
}

val TexFormat.isU32: Boolean get() {
    return this == TexFormat.R_U32 || this == TexFormat.RG_U32 || this == TexFormat.RGBA_U32
}
