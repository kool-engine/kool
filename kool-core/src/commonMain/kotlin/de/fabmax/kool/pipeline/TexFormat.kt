package de.fabmax.kool.pipeline

enum class TexFormat(val channels: Int, val hasAlpha: Boolean, val isFloat: Boolean) {
    R(1, false, false),
    RG(2, false, false),
    RGB(3, false, false),
    RGBA(4, true, false),

    R_F16(1, false, true),
    RG_F16(2, false, true),
    RGB_F16(3, false, true),
    RGBA_F16(4, true, true),

    R_F32(1, false, true),
    RG_F32(2, false, true),
    RGB_F32(3, false, true),
    RGBA_F32(4, true, true),

    R_I32(1, false, false),
    RG_I32(2, false, false),
    RGB_I32(3, false, false),
    RGBA_I32(4, true, false),

    R_U32(1, false, false),
    RG_U32(2, false, false),
    RGB_U32(3, false, false),
    RGBA_U32(4, true, false)
}

val TexFormat.isF16: Boolean get() {
    return this == TexFormat.R_F16 || this == TexFormat.RG_F16 || this == TexFormat.RGB_F16 || this == TexFormat.RGBA_F16
}

val TexFormat.isF32: Boolean get() {
    return this == TexFormat.R_F32 || this == TexFormat.RG_F32 || this == TexFormat.RGB_F32 || this == TexFormat.RGBA_F32
}

val TexFormat.isI32: Boolean get() {
    return this == TexFormat.R_I32 || this == TexFormat.RG_I32 || this == TexFormat.RGB_I32 || this == TexFormat.RGBA_I32
}

val TexFormat.isU32: Boolean get() {
    return this == TexFormat.R_U32 || this == TexFormat.RG_U32 || this == TexFormat.RGB_U32 || this == TexFormat.RGBA_U32
}
