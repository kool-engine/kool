package de.fabmax.kool.pipeline

enum class ShaderStage(val mask: Int) {
    VERTEX_SHADER(0x01),
    TESSELEATION_CTRL(0x02),
    TESSELATION_EVAL(0x04),
    GEOMETRY_SHADER(0x08),
    FRAGMENT_SHADER(0x10),
    COMPUTE_SHADER(0x20),

    ALL(-1)
}