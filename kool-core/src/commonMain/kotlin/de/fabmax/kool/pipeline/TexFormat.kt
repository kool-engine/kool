package de.fabmax.kool.pipeline

enum class TexFormat(val channels: Int, val hasAlpha: Boolean) {
    R(1, false),
    RG(2, false),
    RGB(3, false),
    RGBA(4, true),

    R_F16(1, false),
    RG_F16(2, false),
    RGB_F16(3, false),
    RGBA_F16(4, true)
}