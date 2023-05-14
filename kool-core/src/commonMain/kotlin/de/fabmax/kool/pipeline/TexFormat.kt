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
    RGBA_F32(4, true, true)
}

val TexFormat.isF16: Boolean get() {
    return this == TexFormat.R_F16 || this == TexFormat.RG_F16 || this == TexFormat.RGB_F16 || this == TexFormat.RGBA_F16
}

val TexFormat.isF32: Boolean get() {
    return this == TexFormat.R_F32 || this == TexFormat.RG_F32 || this == TexFormat.RGB_F32 || this == TexFormat.RGBA_F32
}
