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

    // integer
    RI(1, false, false),
}